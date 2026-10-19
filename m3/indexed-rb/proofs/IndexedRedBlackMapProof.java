// SPDX-License-Identifier: Apache-2.0
package com.m3.collections.eclipse.impl.map.sorted.mutable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Supplier;

/** Public-operation differential controls; source-aware author, opaque JDK provider. */
public final class IndexedRedBlackMapProof
{
    private static int operations;
    private IndexedRedBlackMapProof() { }
    private static Object observed(Supplier<?> action)
    {
        try { return Arrays.asList("return", action.get()); }
        catch (RuntimeException failure) { return Arrays.asList("throw", failure.getClass().getName(), failure.getMessage()); }
    }
    private static void equal(Object actual, Object expected, String label)
    {
        if (!Objects.equals(actual, expected)) { throw new AssertionError(label + ": actual=" + actual + "; expected=" + expected); }
    }
    private static List<Object> state(SortedMap<Integer, String> map)
    {
        List<Object> result = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : map.entrySet()) { result.add(Arrays.asList(entry.getKey(), entry.getValue())); }
        return Arrays.asList(map.size(), map.isEmpty(), result);
    }
    private static Object operation(SortedMap<Integer, String> map, int op, int key, String value)
    {
        return observed(() -> switch (op)
        {
            case 0 -> map.put(key, value);
            case 1 -> map.remove(key);
            case 2 -> map.get(key);
            case 3 -> map.containsKey(key);
            case 4 -> map.getOrDefault(key, "missing");
            case 5 -> map.firstKey();
            case 6 -> map.lastKey();
            case 7 -> map.keySet().remove(key);
            case 8 -> map.values().remove(value);
            case 9 -> map.entrySet().remove(new java.util.AbstractMap.SimpleEntry<>(key, value));
            default -> throw new AssertionError("unknown operation");
        });
    }
    private static List<SortedMap<Integer, String>> views(SortedMap<Integer, String> root)
    {
        return List.of(root, root.headMap(0), root.tailMap(0), root.subMap(-8, 8));
    }
    private static void randomControls() throws Exception
    {
        IndexedRedBlackMap<Integer, String> actual = new IndexedRedBlackMap<>(null);
        TreeMap<Integer, String> expected = new TreeMap<>();
        List<SortedMap<Integer, String>> av = views(actual), ev = views(expected);
        Random random = new Random(0x4d334d4150L);
        for (int i = 0; i < 8192; i++)
        {
            int view = random.nextInt(4), op = random.nextInt(10), key = random.nextInt(33) - 16;
            String value = i % 7 == 0 ? null : "v" + random.nextInt(5);
            equal(operation(av.get(view), op, key, value), operation(ev.get(view), op, key, value), "operation " + i);
            for (int n = 0; n < 4; n++) { equal(state(av.get(n)), state(ev.get(n)), "state " + i + "/" + n); }
            actual.tree.verifyInvariants();
            operations++;
        }
        for (int n = 0; n < 4; n++)
        {
            actual.clear(); expected.clear();
            for (int key = -16; key <= 16; key++) { actual.put(key, "v" + key); expected.put(key, "v" + key); }
            Iterator<Integer> a = av.get(n).keySet().iterator(), e = ev.get(n).keySet().iterator();
            while (e.hasNext())
            {
                equal(a.hasNext(), true, "key cursor availability");
                equal(a.next(), e.next(), "key cursor value");
                a.remove(); e.remove();
                equal(state(actual), state(expected), "iterator removal state");
                actual.tree.verifyInvariants();
                operations++;
            }
            equal(a.hasNext(), false, "key cursor exhaustion");
            equal(observed(a::next), observed(e::next), "exhausted next");
            a = av.get(n).keySet().iterator(); e = ev.get(n).keySet().iterator();
            Iterator<Integer> aa = a, ee = e;
            equal(observed(() -> { aa.remove(); return null; }), observed(() -> { ee.remove(); return null; }), "ISE ordering");
        }
    }
    private static Object endpoint(SortedMap<Integer, String> root, int selector)
    {
        return observed(() ->
        {
            SortedMap<Integer, String> view = switch (selector)
            {
                case 0 -> root.subMap(-3, 3).subMap(-3, 3);
                case 1 -> root.headMap(0).headMap(0);
                case 2 -> root.headMap(0).tailMap(0);
                case 3 -> root.tailMap(0).headMap(0);
                case 4 -> root.subMap(-3, 3).headMap(4);
                case 5 -> root.subMap(-3, 3).tailMap(-4);
                case 6 -> root.subMap(3, -3);
                case 7 -> root.subMap(-3, 3).subMap(-2, 4);
                case 8 -> root.subMap(-3, 3).subMap(-4, 2);
                default -> throw new AssertionError("unknown endpoint");
            };
            return state(view);
        });
    }
    private static void endpointControls()
    {
        IndexedRedBlackMap<Integer, String> actual = new IndexedRedBlackMap<>(null);
        TreeMap<Integer, String> expected = new TreeMap<>();
        for (int n = -5; n <= 5; n++) { actual.put(n, "v" + n); expected.put(n, "v" + n); }
        for (int selector = 0; selector < 9; selector++)
        { equal(endpoint(actual, selector), endpoint(expected, selector), "endpoint " + selector); operations++; }
        for (Comparator<Integer> order : Arrays.<Comparator<Integer>>asList(null, Comparator.reverseOrder(), Comparator.nullsFirst(Comparator.naturalOrder())))
        {
            IndexedRedBlackMap<Integer, String> a = new IndexedRedBlackMap<>(order);
            TreeMap<Integer, String> e = new TreeMap<>(order);
            for (int key : new int[]{3, 1, 4, 2}) { a.put(key, "v" + key); e.put(key, "v" + key); }
            equal(state(a), state(e), "comparator root");
            if (order != null && order.compare(1, 2) < 0)
            {
                a.put(null, "null"); e.put(null, "null");
                equal(state(a.headMap(2)), state(e.headMap(2)), "null-first bounded");
            }
        }
    }
    public static void main(String[] args) throws Exception
    {
        randomControls(); endpointControls();
        System.out.println("{\"status\":\"PASS_SOURCE_AWARE_SORTED_MAP_CONTROLS\",\"operations\":" + operations + "}");
    }
}
