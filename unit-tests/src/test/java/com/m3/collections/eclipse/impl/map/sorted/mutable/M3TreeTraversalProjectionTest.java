/*
 * Copyright (c) 2026 Hitesh Soliwal and Contributors to the Synexia Project.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.m3.collections.eclipse.impl.map.sorted.mutable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.function.BiConsumer;

import com.m3.collections.eclipse.api.partition.list.PartitionMutableList;
import org.junit.jupiter.api.Test;

/** The original iterator/callback protocol is the oracle; physical projections are unnecessary. */
final class M3TreeTraversalProjectionTest
{
    @Test
    void javaMapKeyValueTraversalDoesNotPopulateTheEntryCache() throws Exception
    {
        TreeSortedMap<Integer, Integer> map = tree(128);
        Field treeMap = TreeSortedMap.class.getDeclaredField("treeMap");
        treeMap.setAccessible(true);
        @SuppressWarnings("unchecked")
        SortedMap<Integer, Integer> indexed = (SortedMap<Integer, Integer>) treeMap.get(map);
        List<Integer> seen = new ArrayList<>();
        indexed.subMap(16, 112).forEach((key, value) -> seen.add(key));
        assertEquals(range(16, 112), seen);
        assertEquals(0, projections(arena(map)), "Map.forEach must pair the key and value array lanes");
    }

    @Test
    void mapForEachUsesPhysicalReachabilityWhileProcedureTraversalUsesLogicalSize() throws Exception
    {
        // The original lifecycle suite reaches this state through reentrant putAll/removal.
        TreeSortedMap<Integer, Integer> map = tree(3);
        backend(map).tree.size = 0;
        Field treeMap = TreeSortedMap.class.getDeclaredField("treeMap");
        treeMap.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Integer, Integer> indexed = (Map<Integer, Integer>) treeMap.get(map);
        List<Integer> seen = new ArrayList<>();
        indexed.forEach((key, value) -> seen.add(key));
        assertEquals(List.of(0, 1, 2), seen);
        map.forEachKeyValue((key, value) -> { throw new AssertionError("logical empty procedure traversal"); });
    }

    @Test
    void keyValueTraversalDoesNotPopulateTheEntryCache() throws Exception
    {
        TreeSortedMap<Integer, Integer> map = tree(128);
        IndexedTreeArena<?, ?> arena = arena(map);
        assertEquals(0, projections(arena));
        List<Integer> seen = new ArrayList<>();
        map.forEachKeyValue((key, value) -> seen.add(key));
        assertEquals(128, seen.size());
        assertEquals(0, projections(arena), "key/value traversal must not create retained WeakReference slots");
    }

    @Test
    void partitionTraversalDoesNotPopulateTheEntryCache() throws Exception
    {
        TreeSortedMap<Integer, Integer> map = tree(128);
        IndexedTreeArena<?, ?> arena = arena(map);
        PartitionMutableList<Integer> result = map.partitionWhile(value -> value < 64);
        assertEquals(64, result.getSelected().size());
        assertEquals(64, result.getRejected().size());
        assertEquals(0, projections(arena), "value-only partition must not request public Entry objects");
    }

    @Test
    void keyValueTraversalRetainsOriginalCallbackOrderAndMutationBoundaries()
    {
        for (int order = 0; order < 3; order++)
        {
            for (int count = 0; count <= 7; count++)
            {
                for (int operation = 0; operation <= 7; operation++)
                {
                    for (int at = 0; at <= count; at++)
                    {
                        assertEquals(observeTree(true, order, count, operation, at),
                                observeTree(false, order, count, operation, at),
                                "order=" + order + ", count=" + count + ", op=" + operation + ", at=" + at);
                    }
                }
            }
        }
    }

    @Test
    void mapForEachRetainsOriginalRangeFenceAndFailFastBoundaries()
    {
        for (int view = 0; view < 4; view++)
        {
            for (int operation = 0; operation <= 7; operation++)
            {
                for (int at = 0; at <= 7; at++)
                {
                    assertEquals(observeRange(true, view, operation, at),
                            observeRange(false, view, operation, at),
                            "view=" + view + ", op=" + operation + ", at=" + at);
                }
            }
        }
    }

    @Test
    void emptyAndNullCallbacksPreserveTheirDistinctPublicContracts()
    {
        TreeSortedMap<Integer, Integer> map = tree(0);
        map.forEachKeyValue(null);
        assertTrue(map.partitionWhile(null).getSelected().isEmpty());
        assertTrue(map.partitionWhile(null).getRejected().isEmpty());
        map.put(1, null);
        assertThrows(NullPointerException.class, () -> map.forEachKeyValue(null));
        assertThrows(NullPointerException.class, () -> map.partitionWhile(null));
        IndexedSortedMap<Integer, Integer> indexed = new IndexedSortedMap<>();
        assertThrows(NullPointerException.class, () -> indexed.forEach(null));
        assertThrows(NullPointerException.class, () -> indexed.headMap(10).forEach(null));
    }

    @Test
    void callbackExceptionsArePropagatedByIdentity()
    {
        TreeSortedMap<Integer, Integer> map = tree(3);
        IllegalStateException failure = new IllegalStateException("caller");
        assertSame(failure, assertThrows(IllegalStateException.class,
                () -> map.forEachKeyValue((key, value) -> { throw failure; })));
        assertSame(failure, assertThrows(IllegalStateException.class,
                () -> map.partitionWhile(value -> { throw failure; })));
    }

    @Test
    void partitionRetainsPrefixSemanticsAndCallbackDemand()
    {
        for (int count = 0; count <= 16; count++)
        {
            for (int boundary = 0; boundary <= count; boundary++)
            {
                TreeSortedMap<Integer, Integer> map = tree(count);
                List<Integer> called = new ArrayList<>();
                final int limit = boundary;
                PartitionMutableList<Integer> result = map.partitionWhile(value ->
                {
                    called.add(value);
                    return value < limit;
                });
                assertEquals(range(0, boundary), new ArrayList<>(result.getSelected()));
                assertEquals(range(boundary, count), new ArrayList<>(result.getRejected()));
                assertEquals(range(0, Math.min(count, boundary + 1)), called);
                map.clear();
                assertEquals(count, result.getSelected().size() + result.getRejected().size());
            }
        }
    }

    @Test
    void partitionRetainsReentrantUpdatesAndStructuralFailure()
    {
        TreeSortedMap<Integer, Integer> map = tree(3);
        PartitionMutableList<Integer> result = map.partitionWhile(value ->
        {
            if (value == 0) { map.put(1, 100); }
            return value < 100;
        });
        assertEquals(List.of(0), new ArrayList<>(result.getSelected()));
        assertEquals(List.of(100, 2), new ArrayList<>(result.getRejected()));
        TreeSortedMap<Integer, Integer> structurallyChanged = tree(3);
        assertThrows(ConcurrentModificationException.class, () -> structurallyChanged.partitionWhile(value ->
        {
            structurallyChanged.remove(2);
            return true;
        }));
        TreeSortedMap<Integer, Integer> last = tree(1);
        assertEquals(List.of(0), new ArrayList<>(last.partitionWhile(value ->
        {
            last.clear();
            return true;
        }).getSelected()));
    }

    @Test
    void explicitEntriesRetainLiveIdentityAndDetachedSlotIndependence() throws Exception
    {
        TreeSortedMap<Integer, Integer> map = tree(3);
        Map.Entry<Integer, Integer> saved = map.entrySet().iterator().next();
        assertSame(saved, map.entrySet().iterator().next());
        IndexedTreeArena<?, ?> arena = arena(map);
        int before = projections(arena);
        map.forEachKeyValue((key, value) -> { });
        map.partitionWhile(value -> true);
        assertEquals(before, projections(arena), "internal traversal cannot populate unrelated entry slots");
        map.put(0, 99);
        assertEquals(99, saved.getValue());
        map.remove(0);
        map.put(10, 100);
        assertEquals(99, saved.setValue(123));
        assertEquals(0, saved.getKey());
        assertEquals(100, map.get(10));
    }

    @Test
    void fullKeyValueTraversalAllocationIsIndependentOfElementCount()
    {
        assumeTrue(ManagementFactory.getThreadMXBean() instanceof com.sun.management.ThreadMXBean);
        com.sun.management.ThreadMXBean bean =
                (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();
        assumeTrue(bean.isThreadAllocatedMemorySupported());
        bean.setThreadAllocatedMemoryEnabled(true);
        long[] sum = {0};
        for (int i = 0; i < 3; i++)
        {
            tree(4096).forEachKeyValue((key, value) -> sum[0] += value);
        }
        TreeSortedMap<Integer, Integer> map = tree(4096);
        com.m3.collections.eclipse.api.block.procedure.Procedure2<Integer, Integer> consume =
                (key, value) -> sum[0] += value;
        long thread = Thread.currentThread().getId();
        long before = bean.getThreadAllocatedBytes(thread);
        map.forEachKeyValue(consume);
        long bytes = bean.getThreadAllocatedBytes(thread) - before;
        System.out.println("EC_KEY_VALUE_ALLOCATION rows=4096 bytes=" + bytes);
        assertTrue(bytes < 16_384, "traversal allocated " + bytes + " bytes for 4096 rows");
        assertEquals(4L * 4095 * 4096 / 2, sum[0]);
    }

    private static List<Object> observeTree(boolean reference, int order, int count, int operation, int at)
    {
        Comparator<Integer> comparator = order == 0 ? Comparator.naturalOrder()
                : order == 1 ? Comparator.reverseOrder() : Comparator.nullsFirst(Comparator.naturalOrder());
        TreeSortedMap<Integer, Integer> root = new TreeSortedMap<>(comparator);
        for (int i = 0; i < count; i++) { root.put(order == 2 && i == 0 ? null : i, i % 3 == 0 ? null : i); }
        List<Object> tape = new ArrayList<>();
        int[] visits = {0};
        BiConsumer<Integer, Integer> callback = (key, value) ->
        {
            tape.add(String.valueOf(key) + "=" + value);
            if (visits[0]++ == at) { mutate(root, key, operation); }
        };
        String failure = "NONE";
        try
        {
            if (reference)
            {
                if (!root.isEmpty())
                {
                    for (Map.Entry<Integer, Integer> entry : root.entrySet())
                    { callback.accept(entry.getKey(), entry.getValue()); }
                }
            }
            else { root.forEachKeyValue(callback::accept); }
        }
        catch (RuntimeException exception) { failure = exception.getClass().getName(); }
        return List.of(tape, failure, snapshot(root));
    }

    private static List<Object> observeRange(boolean reference, int view, int operation, int at)
    {
        List<String> comparisons = new ArrayList<>();
        IndexedSortedMap<Integer, Integer> root = new IndexedSortedMap<>((left, right) ->
        {
            comparisons.add(left + ":" + right);
            return Integer.compare(left, right);
        });
        for (int i = 0; i < 7; i++) { root.put(i, i); }
        SortedMap<Integer, Integer> selected = switch (view)
        {
            case 1 -> root.headMap(5);
            case 2 -> root.tailMap(2);
            case 3 -> root.subMap(2, 5);
            default -> root;
        };
        comparisons.clear();
        List<String> tape = new ArrayList<>();
        int[] visits = {0};
        BiConsumer<Integer, Integer> callback = (key, value) ->
        {
            tape.add(key + "=" + value);
            if (visits[0]++ == at) { mutate(root, key, operation); }
        };
        String failure = "NONE";
        try
        {
            if (reference)
            {
                for (Map.Entry<Integer, Integer> entry : selected.entrySet())
                { callback.accept(entry.getKey(), entry.getValue()); }
            }
            else { selected.forEach(callback); }
        }
        catch (RuntimeException exception) { failure = exception.getClass().getName(); }
        return List.of(tape, failure, List.copyOf(comparisons), snapshot(root));
    }

    private static void mutate(Map<Integer, Integer> map, Integer key, int operation)
    {
        switch (operation)
        {
            case 1 -> map.put(key, 90);
            case 2 -> map.remove(key);
            case 3 -> map.clear();
            case 4 -> map.put(20, 20);
            case 5 -> map.remove(5);
            case 6 -> map.put(6, 60);
            case 7 -> throw new IllegalStateException("callback");
            default -> { }
        }
    }

    private static List<String> snapshot(Map<Integer, Integer> map)
    {
        List<String> result = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : map.entrySet())
        { result.add(String.valueOf(entry.getKey()) + "=" + entry.getValue()); }
        return result;
    }

    private static List<Integer> range(int from, int to)
    {
        List<Integer> result = new ArrayList<>();
        for (int i = from; i < to; i++) { result.add(i); }
        return result;
    }

    private static TreeSortedMap<Integer, Integer> tree(int count)
    {
        TreeSortedMap<Integer, Integer> result = new TreeSortedMap<>();
        for (int i = 0; i < count; i++) { result.put(i, i); }
        return result;
    }

    private static IndexedTreeArena<?, ?> arena(TreeSortedMap<?, ?> map) throws Exception
    {
        return backend(map).tree.arena;
    }

    private static IndexedRedBlackMap<?, ?> backend(TreeSortedMap<?, ?> map) throws Exception
    {
        Field treeMap = TreeSortedMap.class.getDeclaredField("treeMap");
        treeMap.setAccessible(true);
        Field storage = IndexedSortedMap.class.getDeclaredField("storage");
        storage.setAccessible(true);
        Object owner = storage.get(treeMap.get(map));
        Field backend = owner.getClass().getDeclaredField("backend");
        backend.setAccessible(true);
        return (IndexedRedBlackMap<?, ?>) backend.get(owner);
    }

    private static int projections(IndexedTreeArena<?, ?> arena)
    {
        int count = 0;
        for (Object view : arena.entryViews) { if (view != null) { count++; } }
        return count;
    }
}
