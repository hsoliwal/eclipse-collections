/*******************************************************************************
 * Copyright (c) 2026 Synexia, Hitesh Soliwal (hsoliwal@gmail.com) and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0, or (at your option) the Eclipse
 * Public License 2.0, which are available at
 * https://www.apache.org/licenses/LICENSE-2.0 and
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: Apache-2.0 OR EPL-2.0
 *
 * Contributors:
 *     Hitesh Soliwal (Synexia) - initial API and implementation
 *******************************************************************************/
package com.m3.collections.eclipse.impl.map.sorted.mutable;

import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** Standalone lifecycle tests; requires no owner, external library or test runner. */
public final class IndexedTreeArenaTest {
    private static int checks;

    public static void main(String[] arguments) {
        for (int count : new int[] {63, 64, 65, 127, 128, 129}) {
            growthAndReuse(count);
        }
        payloadAliases();
        entryAcrossGrowth();
        nullsAndGuards();
        invariantDiscriminators();
        apiEntryDifferential();
        System.out.println("IndexedTreeArenaTest PASS checks=" + checks);
    }

    private static void growthAndReuse(int count) {
        IndexedTreeArena<Integer, String> arena = new IndexedTreeArena<>();
        arena.verifyInvariants();
        check(arena.used == 0 && arena.size == 0 && arena.free == -1, "empty counts");
        check(arena.keys.length == 0, "empty arena is unallocated");
        check(arena.black(-1) && arena.black(-7) && arena.black(Integer.MIN_VALUE),
                "negative sentinels are black");
        arena.setBlack(-1, false);
        arena.setBlack(-7, true);
        check(!arena.live(-1) && !arena.live(Integer.MAX_VALUE), "outside arena is not live");
        for (int i = 0; i < count; i++) {
            check(arena.allocate(i, "v" + i, -1) == i, "fresh sequential slot");
            check(arena.left[i] == -1 && arena.right[i] == -1 && arena.parent[i] == -1,
                    "fresh null links");
            check(arena.entryViews[i] == null, "allocate does not create a projection");
            check(!arena.black(i), "new allocation is red");
            arena.setBlack(i, (i & 1) != 0);
            if (i == 62 || i == 63 || i == 64 || i == 126 || i == 127 || i == 128) {
                arena.verifyInvariants();
            }
        }
        arena.verifyInvariants();
        check(arena.used == count && arena.size == count && arena.free == -1, "grown counts");
        check(arena.keys.length >= count && arena.keys.length < count * 2, "geometric capacity");
        check(arena.liveMask.length == (arena.keys.length + 63) / 64, "mask capacity");
        for (int i = 0; i < count; i++) {
            check(Objects.equals(arena.keys[i], i) && Objects.equals(arena.values[i], "v" + i),
                    "growth preserves payloads");
            check(arena.live(i) && arena.black(i) == ((i & 1) != 0), "growth preserves both masks");
        }

        List<Integer> released = new ArrayList<>();
        List<Map.Entry<Integer, String>> detached = new ArrayList<>();
        for (int slot = 0; slot < count; slot += 3) {
            detached.add(arena.entry(slot));
            arena.release(slot);
            released.add(slot);
            check(arena.keys[slot] == null && arena.values[slot] == null, "released strong payloads");
            check(arena.entryViews[slot] == null && arena.right[slot] == -1
                    && arena.parent[slot] == -1, "released cache and links");
            check(!arena.live(slot) && !arena.black(slot), "released mask bits");
            arena.verifyInvariants();
        }
        Set<Integer> freeSlots = new HashSet<>();
        for (int slot = arena.free; slot != -1; slot = arena.left[slot]) {
            check(freeSlots.add(slot), "free chain has no repetition");
        }
        check(freeSlots.equals(new HashSet<>(released)), "free chain conserves dead slots");
        check(arena.size + freeSlots.size() == arena.used, "used equals live plus free");
        int capacity = arena.keys.length;
        for (int i = released.size() - 1; i >= 0; i--) {
            int slot = released.get(i);
            check(arena.allocate(-slot - 1, "replacement" + slot, -1) == slot, "reuse is free-chain order");
            check(arena.entryViews[slot] == null && !arena.black(slot), "reuse starts without cache or black bit");
            check(arena.left[slot] == -1 && arena.right[slot] == -1 && arena.parent[slot] == -1,
                    "reuse restores all null links");
            Map.Entry<Integer, String> old = detached.get(i);
            check(Objects.equals(old.getKey(), slot) && Objects.equals(old.getValue(), "v" + slot),
                    "retained old entry never observes reuse");
            old.setValue("detached");
            check(Objects.equals(arena.values[slot], "replacement" + slot), "detached write cannot reach replacement");
            arena.verifyInvariants();
        }
        check(arena.used == count && arena.size == count && arena.free == -1,
                "reuse preserves high-water mark and conservation");
        check(arena.keys.length == capacity, "free slots need no growth");
    }

    private static void payloadAliases() {
        IndexedTreeArena<String, String> arena = new IndexedTreeArena<>();
        int target = arena.allocate("target", "old", -1);
        int source = arena.allocate("successor", "source", -1);
        int untouched = arena.allocate("other", "other-value", target);
        arena.left[target] = untouched;
        Map.Entry<String, String> targetEntry = arena.entry(target);
        Map.Entry<String, String> sourceEntry = arena.entry(source);
        Object cache = arena.entryViews[target];
        check(cache instanceof WeakReference<?> && ((WeakReference<?>) cache).get() == targetEntry,
                "cache holds only a weak projection reference");
        check(arena.entry(target) == targetEntry && arena.entryViews[target] == cache,
                "same live projection and reference are reused");
        arena.setBlack(target, true);
        arena.copyPayload(source, target);
        check(Objects.equals(targetEntry.getKey(), "successor")
                && Objects.equals(targetEntry.getValue(), "source"), "target alias observes successor copy");
        check(Objects.equals(sourceEntry.getKey(), "successor")
                && Objects.equals(sourceEntry.getValue(), "source"), "source projection is unchanged by copy");
        check(arena.black(target) && arena.left[target] == untouched
                && arena.parent[untouched] == target, "payload copy changes neither state nor topology");
        check(Objects.equals(targetEntry.setValue("target-updated"), "source"), "live setValue returns previous");
        check(Objects.equals(sourceEntry.getValue(), "source"), "copy does not join value cells");
        arena.release(source);
        check(Objects.equals(sourceEntry.setValue("source-detached"), "source"), "source detached with release payload");
        check(Objects.equals(targetEntry.getValue(), "target-updated"), "detached source cannot change target");
        arena.left[target] = -1;
        arena.parent[untouched] = -1;
        arena.release(target);
        check(Objects.equals(targetEntry.getKey(), "successor")
                && Objects.equals(targetEntry.getValue(), "target-updated"), "target detaches current copied payload");
        check(arena.allocate("replacement", "replacement-value", -1) == target, "target physical slot reused");
        check(arena.entry(target) != targetEntry, "replacement has a distinct API projection");
        targetEntry.setValue("target-detached");
        check(Objects.equals(arena.values[target], "replacement-value"), "old alias cannot mutate new occupant");
        arena.verifyInvariants();

        IndexedTreeArena<String, String> replacementArena = new IndexedTreeArena<>();
        replacementArena.allocate("new-owner", "new-value", -1);
        Map.Entry<String, String> retainedOldArenaEntry = arena.entry(untouched);
        retainedOldArenaEntry.setValue("retained-old-arena");
        check(Objects.equals(arena.values[untouched], "retained-old-arena")
                && Objects.equals(replacementArena.values[0], "new-value"), "replacing an owner arena preserves old aliases");
        arena.verifyInvariants();
        replacementArena.verifyInvariants();
    }

    private static void entryAcrossGrowth() {
        IndexedTreeArena<Integer, String> arena = new IndexedTreeArena<>();
        int slot = arena.allocate(0, "first", -1);
        Map.Entry<Integer, String> entry = arena.entry(slot);
        Object reference = arena.entryViews[slot];
        for (int i = 1; i < 129; i++) {
            arena.allocate(i, "v" + i, -1);
        }
        check(arena.entry(slot) == entry && arena.entryViews[slot] == reference,
                "projection cache survives lane replacement");
        entry.setValue("after-growth");
        check(Objects.equals(arena.values[slot], "after-growth"), "retained projection uses current grown lanes");
        arena.verifyInvariants();
    }

    private static void nullsAndGuards() {
        IndexedTreeArena<Object, Object> arena = new IndexedTreeArena<>();
        int slot = arena.allocate(null, null, -1);
        Map.Entry<Object, Object> entry = arena.entry(slot);
        check(entry.getKey() == null && entry.getValue() == null && entry.setValue(null) == null,
                "null entry payloads");
        arena.setBlack(slot, true);
        arena.release(slot);
        check(entry.getKey() == null && entry.getValue() == null, "detached null payloads");
        expect(IllegalStateException.class, () -> arena.release(slot));
        expect(IllegalStateException.class, () -> arena.entry(slot));
        expect(IllegalStateException.class, () -> arena.setBlack(slot, true));
        expect(IllegalStateException.class, () -> arena.allocate("bad", "parent", slot));
        int used = arena.used;
        arena.values[slot] = "uncleared";
        expect(IllegalStateException.class, () -> arena.allocate("bad", "reuse", -1));
        check(arena.free == slot && arena.used == used && arena.size == 0,
                "uncleared-slot rejection leaves counts unchanged");
        arena.values[slot] = null;
        arena.blackMask[slot >>> 6] |= 1L << (slot & 63);
        expect(IllegalStateException.class, () -> arena.allocate("bad", "black", -1));
        arena.blackMask[slot >>> 6] = 0;
        check(arena.allocate(null, null, -1) == slot, "cleared null slot reusable");
        arena.copyPayload(slot, slot);
        arena.verifyInvariants();
    }

    private static void invariantDiscriminators() {
        IndexedTreeArena<Integer, Integer> arena = new IndexedTreeArena<>();
        int slot = arena.allocate(1, 2, -1);
        arena.release(slot);
        arena.left[slot] = slot;
        expect(AssertionError.class, arena::verifyInvariants);
        arena.left[slot] = -1;
        arena.keys[slot] = "leaked";
        expect(AssertionError.class, arena::verifyInvariants);
        arena.keys[slot] = null;
        arena.blackMask[0] = 1;
        expect(AssertionError.class, arena::verifyInvariants);
        arena.blackMask[0] = 0;
        arena.free = -1;
        expect(AssertionError.class, arena::verifyInvariants);
        arena.free = slot;
        arena.liveMask[0] = 1L << 7;
        expect(AssertionError.class, arena::verifyInvariants);
        arena.liveMask[0] = 0;
        arena.verifyInvariants();
    }

    private static void apiEntryDifferential() {
        for (String key : new String[] {null, "key"}) {
            for (String value : new String[] {null, "value"}) {
                IndexedTreeArena<String, String> arena = new IndexedTreeArena<>();
                int slot = arena.allocate(key, value, -1);
                Map.Entry<String, String> ours = arena.entry(slot);
                Map.Entry<String, String> jdk = new AbstractMap.SimpleEntry<>(key, value);
                check(ours.equals(jdk) && jdk.equals(ours), "standard symmetric entry equality");
                check(ours.hashCode() == jdk.hashCode(), "standard entry hash");
                check(ours.toString().equals(jdk.toString()), "standard entry string");
                check(!ours.equals(null) && !ours.equals("not-an-entry"), "standard foreign equality");
                check(Objects.equals(ours.setValue("next"), jdk.setValue("next")), "standard setValue previous");
                arena.release(slot);
                check(Objects.equals(ours.setValue(null), jdk.setValue(null)) && ours.equals(jdk),
                        "detached entry standard API");
            }
        }
        List<String> oursEvents = new ArrayList<>();
        List<String> jdkEvents = new ArrayList<>();
        IndexedTreeArena<Probe, Probe> arena = new IndexedTreeArena<>();
        int slot = arena.allocate(new Probe("key", oursEvents), new Probe("value", oursEvents), -1);
        check(oursEvents.isEmpty(), "allocation invokes no payload callbacks");
        Map.Entry<Probe, Probe> ours = arena.entry(slot);
        check(oursEvents.isEmpty(), "projection creation invokes no payload callbacks");
        Map.Entry<Probe, Probe> jdk = new AbstractMap.SimpleEntry<>(
                new Probe("key", jdkEvents), new Probe("value", jdkEvents));
        check(observe(ours, oursEvents).equals(observe(jdk, jdkEvents)), "JDK entry method results");
        check(oursEvents.equals(jdkEvents), "JDK entry callback sequence: arena="
                + oursEvents + "; jdk=" + jdkEvents);
        oursEvents.clear();
        arena.release(slot);
        check(oursEvents.isEmpty(), "release detachment invokes no payload callbacks");
    }

    private static List<Object> observe(Map.Entry<Probe, Probe> entry, List<String> events) {
        List<Object> results = new ArrayList<>();
        results.add(entry.equals(new AbstractMap.SimpleEntry<>(new Probe("key", events), new Probe("value", events))));
        results.add(entry.equals(entry));
        results.add(entry.equals(null));
        results.add(entry.equals("not-entry"));
        results.add(entry.hashCode());
        results.add(entry.toString());
        results.add(entry.setValue(new Probe("next", events)).name);
        results.add(entry.getKey().name);
        results.add(entry.getValue().name);
        results.add(entry.equals(new AbstractMap.SimpleEntry<>(new Probe("key", events), new Probe("other", events))));
        return results;
    }

    private static final class Probe {
        private final String name;
        private final List<String> events;
        Probe(String name, List<String> events) { this.name = name; this.events = events; }
        @Override public boolean equals(Object other) {
            events.add("equals:" + name + ":" + (other instanceof Probe p ? p.name : "foreign"));
            return other instanceof Probe p && name.equals(p.name);
        }
        @Override public int hashCode() { events.add("hash:" + name); return name.hashCode(); }
        @Override public String toString() { events.add("string:" + name); return name; }
    }

    private static void expect(Class<? extends Throwable> type, Runnable action) {
        try {
            action.run();
        } catch (Throwable failure) {
            check(type.isInstance(failure), "expected " + type.getName() + ", got " + failure);
            return;
        }
        throw new AssertionError("expected " + type.getName());
    }

    private static void check(boolean condition, String description) {
        checks++;
        if (!condition) {
            throw new AssertionError(description);
        }
    }
}
