// SPDX-License-Identifier: Apache-2.0
package com.m3.collections.eclipse.impl.map.sorted.mutable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TreeMap;

/** Source-aware structural differential proof; not a source-blind validator. */
public final class IndexedRedBlackTreeProof
{
    private static final Field ROOT;
    private static final Field LEFT;
    private static final Field RIGHT;
    private static final Field KEY;
    private static final Field COLOR;
    private static long operations;
    private static long structures;
    private static long permutations;

    static
    {
        try
        {
            ROOT = TreeMap.class.getDeclaredField("root");
            Class<?> entry = Class.forName("java.util.TreeMap$Entry");
            LEFT = entry.getDeclaredField("left"); RIGHT = entry.getDeclaredField("right");
            KEY = entry.getDeclaredField("key"); COLOR = entry.getDeclaredField("color");
            for (Field f : new Field[]{ROOT, LEFT, RIGHT, KEY, COLOR}) { f.setAccessible(true); }
        }
        catch (ReflectiveOperationException failure) { throw new ExceptionInInitializerError(failure); }
    }

    private IndexedRedBlackTreeProof() { }
    private static void check(boolean condition, String label)
    {
        if (!condition) { throw new AssertionError(label); }
    }

    private static String expectedShape(Object entry) throws IllegalAccessException
    {
        if (entry == null) { return "_"; }
        return "(" + KEY.get(entry) + ":" + COLOR.getBoolean(entry) + expectedShape(LEFT.get(entry))
                + expectedShape(RIGHT.get(entry)) + ")";
    }

    private static String actualShape(IndexedTreeArena<?, ?> arena, int slot)
    {
        if (slot < 0) { return "_"; }
        return "(" + arena.keys[slot] + ":" + arena.black(slot) + actualShape(arena, arena.left[slot])
                + actualShape(arena, arena.right[slot]) + ")";
    }

    private static <K, V> void equalTree(IndexedRedBlackTree<K, V> actual, TreeMap<K, V> expected)
            throws IllegalAccessException
    {
        actual.verifyInvariants();
        check(actual.size == expected.size(), "logical size");
        Iterator<Map.Entry<K, V>> e = expected.entrySet().iterator();
        for (int at = IndexedRedBlackTree.first(actual.arena, actual.root); at >= 0;
                at = IndexedRedBlackTree.successor(actual.arena, at))
        {
            check(e.hasNext(), "extra actual entry");
            Map.Entry<K, V> entry = e.next();
            check(Objects.equals(actual.arena.keys[at], entry.getKey()), "inorder key");
            check(Objects.equals(actual.arena.values[at], entry.getValue()), "inorder value");
        }
        check(!e.hasNext(), "missing actual entry");
        String want = expectedShape(ROOT.get(expected));
        String got = actualShape(actual.arena, actual.root);
        check(want.equals(got), "shape: expected=" + want + ";actual=" + got);
        structures++;
    }

    private static void mutation(IndexedRedBlackTree<Integer, String> actual, TreeMap<Integer, String> expected,
            int key, String value, boolean erase) throws IllegalAccessException
    {
        if (erase)
        {
            int slot = actual.find(key);
            String want = expected.remove(key);
            String got = slot < 0 ? null : (String) actual.arena.values[slot];
            if (slot >= 0) { actual.erase(actual.arena, slot); }
            check(Objects.equals(want, got), "remove return");
        }
        else { check(Objects.equals(expected.put(key, value), actual.put(key, value)), "put return"); }
        operations++;
        equalTree(actual, expected);
    }

    private static void permute(int[] values, int start) throws IllegalAccessException
    {
        if (start == values.length)
        {
            permutations++;
            for (boolean reverse : new boolean[]{false, true})
            {
                IndexedRedBlackTree<Integer, String> actual = new IndexedRedBlackTree<>(null);
                TreeMap<Integer, String> expected = new TreeMap<>();
                for (int n : values) { mutation(actual, expected, n, "v" + n, false); }
                for (int i = 0; i < values.length; i++)
                {
                    int key = values[reverse ? values.length - i - 1 : i];
                    mutation(actual, expected, key, null, true);
                }
            }
            return;
        }
        for (int i = start; i < values.length; i++)
        {
            int swap = values[start]; values[start] = values[i]; values[i] = swap;
            permute(values, start + 1);
            swap = values[start]; values[start] = values[i]; values[i] = swap;
        }
    }

    private static void randomControls() throws IllegalAccessException
    {
        for (Comparator<Integer> comparator : Arrays.<Comparator<Integer>>asList(null, Comparator.reverseOrder()))
        {
            IndexedRedBlackTree<Integer, String> actual = new IndexedRedBlackTree<>(comparator);
            TreeMap<Integer, String> expected = new TreeMap<>(comparator);
            Random random = new Random(0x4d335242);
            for (int i = 0; i < 16384; i++)
            {
                int key = random.nextInt(257) - 128;
                mutation(actual, expected, key, i % 11 == 0 ? null : "v" + i, random.nextBoolean());
            }
        }
    }

    private static void sortedControls() throws IllegalAccessException
    {
        for (int count = 0; count <= 257; count++)
        {
            TreeMap<Integer, String> input = new TreeMap<>();
            for (int i = 0; i < count; i++) { input.put(i, "v" + i); }
            TreeMap<Integer, String> expected = new TreeMap<>(input);
            IndexedRedBlackTree<Integer, String> actual = IndexedRedBlackTree.build(null, count,
                    input.entrySet().iterator());
            equalTree(actual, expected);
            if (count > 0)
            {
                mutation(actual, expected, count / 2, null, true);
                mutation(actual, expected, -1, "v-1", false);
            }
        }
    }

    private static void aliases() throws IllegalAccessException
    {
        IndexedRedBlackTree<Integer, String> actual = new IndexedRedBlackTree<>(null);
        TreeMap<Integer, String> expected = new TreeMap<>();
        for (int key : new int[]{20, 10, 30, 5, 15, 25, 35})
        { mutation(actual, expected, key, "v" + key, false); }
        List<Map.Entry<Integer, String>> wanted = new ArrayList<>(expected.entrySet());
        List<Map.Entry<Integer, String>> got = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : wanted)
        {
            int slot = actual.find(entry.getKey());
            Map.Entry<Integer, String> view = actual.arena.entry(slot);
            check(view == actual.arena.entry(slot), "same live API projection");
            got.add(view);
        }
        mutation(actual, expected, 20, null, true);
        for (int i = 0; i < wanted.size(); i++)
        {
            check(wanted.get(i).equals(got.get(i)), "saved successor-copy alias" + i);
            check(wanted.get(i).hashCode() == got.get(i).hashCode(), "saved alias hash" + i);
            check(wanted.get(i).toString().equals(got.get(i).toString()), "saved alias string" + i);
        }
        Map.Entry<Integer, String> detachedExpected = wanted.get(4); // successor25 removed physically
        Map.Entry<Integer, String> detachedActual = got.get(4);
        check(Objects.equals(detachedExpected.setValue("detached"), detachedActual.setValue("detached")), "detached set");
        mutation(actual, expected, 99, "reuse", false);
        check(detachedExpected.equals(detachedActual), "slot reuse isolation");
        equalTree(actual, expected);
    }

    private static void detachedRoots()
    {
        for (int[] seed : new int[][]{{10}, {10, 20}, {20, 10}, {20, 10, 30}, {20, 10, 30, 5, 15}, {1, 2, 3, 4}})
        {
            IndexedRedBlackTree<Integer, String> actual = new IndexedRedBlackTree<>(null);
            for (int key : seed) { actual.put(key, "old" + key); }
            int deleted = seed.length == 5 ? 10 : seed.length == 4 ? 1 : seed[0];
            IndexedTreeArena<Integer, String> old = actual.arena;
            int slot = actual.find(deleted);
            Map.Entry<Integer, String> alias = old.entry(slot);
            TreeMap<Integer, String> incoming = new TreeMap<>();
            for (int key : new int[]{100, 200, 300}) { incoming.put(key, "new" + key); }
            IndexedRedBlackTree<Integer, String> built = IndexedRedBlackTree.build(null, 3, incoming.entrySet().iterator());
            actual.arena = built.arena; actual.root = built.root; actual.size = 3;
            int beforeEpoch = actual.epoch;
            actual.erase(old, slot);
            check(actual.size == 2 && actual.epoch == beforeEpoch + 1, "shared count/epoch");
            List<Object> keys = new ArrayList<>();
            for (int at = IndexedRedBlackTree.first(actual.arena, actual.root); at >= 0;
                    at = IndexedRedBlackTree.successor(actual.arena, at)) { keys.add(actual.arena.keys[at]); }
            List<Integer> want = seed.length == 1 ? List.of() : seed.length == 2
                    ? List.of(seed[1]) : seed.length == 4 ? List.of(2, 3, 4) : List.of(100, 200, 300);
            check(keys.equals(want), "retained deletion root transition " + Arrays.toString(seed) + keys);
            check(alias.getKey().equals(seed.length == 3 ? 30 : seed.length == 5 ? 15 : deleted), "retained entry payload");
        }
    }

    public static void main(String[] args) throws Exception
    {
        permute(new int[]{0, 1, 2, 3, 4, 5, 6}, 0);
        randomControls(); sortedControls(); aliases(); detachedRoots();
        System.out.println("{\"status\":\"PASS_SOURCE_AWARE_KERNEL\",\"permutations\":" + permutations
                + ",\"mutations\":" + operations + ",\"exactTopologyAndInvariantChecks\":" + structures
                + ",\"integrationAdmission\":false}");
    }
}
