/*
 * Copyright 2026 Hitesh Soliwal and Contributors to the Synexia Project
 * SPDX-License-Identifier: Apache-2.0
 */
package com.m3.collections.eclipse.impl.map.sorted.mutable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

/** Actual-owner projection lifetime and optional-storage regressions. */
public final class IndexedTreeArenaProjectionTest {
    private static int checks;

    private IndexedTreeArenaProjectionTest() { }

    public static void main(String[] arguments) throws Exception {
        String mode = arguments.length == 0 ? "all" : arguments[0];
        if (mode.equals("all") || mode.equals("contract")) contracts();
        if (mode.equals("all") || mode.equals("storage")) optionalStorage();
        if (mode.equals("all") || mode.equals("gc")) gcLifetimes();
        if (mode.equals("oom")) projectionAllocationFailure();
        check(mode.equals("all") || mode.equals("contract") || mode.equals("storage")
                || mode.equals("gc") || mode.equals("oom"), "known mode");
        System.out.println("IndexedTreeArenaProjectionTest PASS mode=" + mode + " checks=" + checks);
    }

    private static void contracts() throws Exception {
        IndexedRedBlackMap<Integer, String> map = new IndexedRedBlackMap<>(null);
        for (int key : new int[] {2, 1, 3}) map.put(key, "v" + key);
        Map.Entry<Integer, String> middle = entry(map, 2);
        Map.Entry<Integer, String> successor = entry(map, 3);
        check(entry(map, 2) == middle, "live projection identity");
        check(entry(map.subMap(1, 4), 2) == middle, "range shares live projection identity");
        check(Objects.equals(middle.setValue("changed"), "v2"), "live prior value");
        check(Objects.equals(map.get(2), "changed"), "live write through");
        check(Objects.equals(map.remove(2), "changed"), "two-child removal result");
        check(middle.getKey() == 3 && Objects.equals(middle.getValue(), "v3"),
                "retained target observes successor payload copy");
        successor.setValue("detached-successor");
        check(Objects.equals(map.get(3), "v3"), "released successor cannot mutate survivor");
        map.put(4, "v4");
        check(Objects.equals(successor.getValue(), "detached-successor"), "slot reuse stays detached");
        middle.setValue("survivor");
        check(Objects.equals(map.get(3), "survivor"), "surviving target remains backed");
        map.tree.verifyInvariants();

        Iterator<Integer> cursor = map.keySet().iterator();
        int first = cursor.next();
        cursor.remove();
        check(!map.containsKey(first), "key iterator removal");
        Iterator<String> stale = map.values().iterator();
        map.put(20, "new");
        expect(ConcurrentModificationException.class, stale::next);
        Map.Entry<Integer, String> held = entry(map, 3);
        map.clear();
        held.setValue("old-arena");
        map.put(3, "new-arena");
        check(Objects.equals(map.get(3), "new-arena") && Objects.equals(held.getValue(), "old-arena"),
                "clear replaces the arena without rebinding retained entries");

        IndexedSortedMap<Integer, String> serial = new IndexedSortedMap<>();
        TreeMap<Integer, String> expected = new TreeMap<>();
        for (int i = 0; i < 257; i++) {
            String value = i % 13 == 0 ? null : "v" + i;
            serial.put(i, value);
            expected.put(i, value);
        }
        Map.Entry<Integer, String> before = entry(serial, 128);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(serial);
        }
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            Object restored = input.readObject();
            check(restored instanceof SortedMap<?, ?> && expected.equals(restored), "serialized map contents");
        }
        check(entry(serial, 128) == before, "serialization preserves retained source projection identity");
        check(expected.equals(serial) && serial.equals(expected), "map equality after serialization");
    }

    private static Map.Entry<Integer, String> entry(SortedMap<Integer, String> map, int key) {
        for (Map.Entry<Integer, String> candidate : map.entrySet()) {
            if (candidate.getKey() == key) return candidate;
        }
        throw new AssertionError("missing entry " + key);
    }

    private static void optionalStorage() {
        IndexedTreeArena<Integer, String> arena = new IndexedTreeArena<>();
        Object[] unused = arena.entryViews;
        for (int i = 0; i < 16_385; i++) arena.allocate(i, "value", -1);
        check(arena.keys.length == 32_768, "payload growth precondition");
        check(arena.entryViews == unused && arena.entryViews.length == 0,
                "unexposed slots reserve no projection references");
        Map.Entry<Integer, String> low = arena.entry(0);
        check(arena.entryViews.length == 8, "first low projection reserves a bounded prefix");
        Object[] prefix = arena.entryViews;
        Object weak = prefix[0];
        for (int i = arena.used; i < 65_537; i++) arena.allocate(i, "value", -1);
        check(arena.entryViews == prefix && arena.entryViews[0] == weak,
                "payload growth neither copies nor expands projection storage");
        for (int slot : new int[] {7, 8, 63, 64, 127, 128, 65_535, 65_536}) {
            Map.Entry<Integer, String> projected = arena.entry(slot);
            check(arena.entryViews.length > slot && arena.entryViews.length <= arena.keys.length,
                    "projection prefix covers the request within payload capacity");
            check(arena.entry(slot) == projected && arena.entry(0) == low,
                    "projection growth preserves all retained identities");
        }
        low.setValue("updated");
        check(Objects.equals(arena.values[0], "updated"), "retained projection follows grown payload lanes");
        arena.verifyInvariants();

        IndexedTreeArena<Integer, String> sparse = new IndexedTreeArena<>();
        for (int i = 0; i < 129; i++) sparse.allocate(i, "value", -1);
        sparse.entry(0);
        sparse.release(128);
        check(sparse.allocate(999, "replacement", -1) == 128,
                "release and reuse outside projection prefix");
        check(sparse.entryViews.length == 8, "unprojected reuse leaves prefix unchanged");
        sparse.verifyInvariants();
    }

    private static void gcLifetimes() {
        IndexedTreeArena<Integer, String> arena = new IndexedTreeArena<>();
        arena.allocate(1, "value", -1);
        WeakReference<Map.Entry<Integer, String>> projected = weakProjection(arena);
        awaitCollection(projected);
        check(((WeakReference<?>) arena.entryViews[0]).get() == null, "arena cache is weak");
        check(Objects.equals(arena.entry(0).getValue(), "value"), "collected projection can be recreated");
        arena.verifyInvariants();

        Retained live = retained(false);
        System.gc();
        check(live.arena.get() != null && Objects.equals(live.entry.getValue(), "value"),
                "retained live projection owns its arena");
        Reference.reachabilityFence(live.entry);

        Retained detached = retained(true);
        awaitCollection(detached.arena);
        check(detached.entry.getKey() == 1 && Objects.equals(detached.entry.getValue(), "value"),
                "detached projection retains payload without retaining the arena");
        check(Objects.equals(detached.entry.setValue("local"), "value"), "detached write remains local");
        Reference.reachabilityFence(detached.entry);
        Reference.reachabilityFence(arena);
    }

    private static WeakReference<Map.Entry<Integer, String>> weakProjection(IndexedTreeArena<Integer, String> arena) {
        return new WeakReference<>(arena.entry(0));
    }

    private record Retained(Map.Entry<Integer, String> entry, WeakReference<IndexedTreeArena<Integer, String>> arena) { }

    private static Retained retained(boolean release) {
        IndexedTreeArena<Integer, String> arena = new IndexedTreeArena<>();
        arena.allocate(1, "value", -1);
        Map.Entry<Integer, String> entry = arena.entry(0);
        if (release) arena.release(0);
        return new Retained(entry, new WeakReference<>(arena));
    }

    private static void awaitCollection(WeakReference<?> reference) {
        for (int attempt = 0; attempt < 40 && reference.get() != null; attempt++) {
            System.gc();
            byte[] pressure = new byte[256 * 1024];
            Reference.reachabilityFence(pressure);
        }
        check(reference.get() == null, "weakly reachable object collected within bounded attempts");
    }

    /** Run separately with -Xms16m -Xmx16m -XX:+UseSerialGC. */
    private static void projectionAllocationFailure() {
        check(Runtime.getRuntime().maxMemory() <= 20L * 1024 * 1024, "requires the bounded 16-MiB child heap");
        IndexedTreeArena<Integer, String> arena = new IndexedTreeArena<>();
        for (int i = 0; i < 16_384; i++) arena.allocate(i, "value", -1);
        Map.Entry<Integer, String> held = arena.entry(0);
        Object[] views = arena.entryViews;
        check(views.length == 8, "projection extension precondition");
        Object[] keys = arena.keys;
        Object[] values = arena.values;
        Object[] oldKeys = keys.clone();
        Object[] oldValues = values.clone();
        int[] left = arena.left.clone();
        int[] right = arena.right.clone();
        int[] parent = arena.parent.clone();
        long[] live = arena.liveMask.clone();
        long[] black = arena.blackMask.clone();
        byte[][] ballast = new byte[8192][];
        int count = 0;
        boolean exhausted = false;
        try {
            while (count < ballast.length) {
                ballast[count] = new byte[4096];
                count++;
            }
        } catch (OutOfMemoryError expected) { exhausted = true; }
        boolean failed = false;
        try {
            arena.entry(16_383);
        } catch (OutOfMemoryError expected) { failed = true; }
        finally {
            Arrays.fill(ballast, null);
            ballast = null;
        }
        System.gc();
        check(exhausted && failed, "actual JVM OOME during optional projection-lane growth");
        check(arena.keys == keys && arena.values == values && arena.entryViews == views,
                "failure preserves payload and projection lane identities");
        check(Arrays.equals(keys, oldKeys) && Arrays.equals(values, oldValues)
                && Arrays.equals(arena.left, left) && Arrays.equals(arena.right, right)
                && Arrays.equals(arena.parent, parent) && Arrays.equals(arena.liveMask, live)
                && Arrays.equals(arena.blackMask, black), "failure preserves payload, links and masks");
        check(arena.used == 16_384 && arena.size == 16_384 && arena.free == -1, "failure preserves counts");
        check(arena.entry(0) == held && arena.entry(16_383).getKey() == 16_383, "projection retry and retained identity");
        arena.verifyInvariants();
    }

    private static void expect(Class<? extends Throwable> expected, Runnable action) {
        try { action.run(); }
        catch (Throwable failure) {
            check(expected.isInstance(failure), "expected exception type");
            return;
        }
        throw new AssertionError("missing expected exception");
    }

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
}
