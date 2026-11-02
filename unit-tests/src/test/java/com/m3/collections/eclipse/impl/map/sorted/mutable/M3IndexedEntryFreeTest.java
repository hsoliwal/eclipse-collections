/* SPDX-License-Identifier: Apache-2.0 OR EPL-2.0 */

package com.m3.collections.eclipse.impl.map.sorted.mutable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.function.BiConsumer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** The oracle is the previous entry-iterator protocol, independent of slot traversal. */
class M3IndexedEntryFreeTest
{
    @Test
    void traversalPreservesOrderingNullsAndValues() throws Exception
    {
        for (Comparator<Integer> order : orders())
        {
            for (int count = 0; count <= 128; count++)
            {
                IndexedSortedMap<Integer, String> map = map(order, count);
                List<Object> actual = new ArrayList<>();
                map.forEachKeyValue((key, value) -> actual.add(Arrays.asList(key, value)));
                assertNoEntries(map);
                List<Object> expected = new ArrayList<>();
                originalTraversal(map, (key, value) -> expected.add(Arrays.asList(key, value)));
                assertEquals(expected, actual);
            }
        }
    }

    @Test
    void callbackMutationsMatchEntryIteratorProtocol()
    {
        for (Comparator<Integer> order : orders())
        {
            for (int count : new int[]{1, 2, 3, 16, 65})
            {
                for (int mode = 0; mode < 6; mode++)
                {
                    for (int callback : new int[]{1, count})
                    {
                        IndexedSortedMap<Integer, String> candidate = map(order, count);
                        IndexedSortedMap<Integer, String> baseline = map(order, count);
                        assertEquals(run(baseline, false, mode, callback), run(candidate, true, mode, callback));
                        assertEquals(new ArrayList<>(baseline.entrySet()), new ArrayList<>(candidate.entrySet()));
                    }
                }
            }
        }
    }

    @Test
    void emptyNullCallbackAndCallbackFailureKeepOriginalBehavior()
    {
        IndexedSortedMap<Integer, String> empty = new IndexedSortedMap<>();
        empty.forEachKeyValue(null);
        IndexedSortedMap<Integer, String> map = map(null, 4);
        assertThrows(NullPointerException.class, () -> map.forEachKeyValue(null));
        RuntimeException expected = new RuntimeException("sentinel");
        assertSame(expected, assertThrows(RuntimeException.class,
                () -> map.forEachKeyValue((key, value) -> { throw expected; })));
    }

    @Test
    void rangeTraversalPreservesCachedFenceAndMutationOrder()
    {
        for (int mode = 0; mode < 6; mode++)
        {
            IndexedSortedMap<Integer, String> candidate = map(null, 20);
            IndexedSortedMap<Integer, String> baseline = map(null, 20);
            IndexedSortedMap<Integer, String> candidateRange = (IndexedSortedMap<Integer, String>) candidate.subMap(3, 17);
            IndexedSortedMap<Integer, String> baselineRange = (IndexedSortedMap<Integer, String>) baseline.subMap(3, 17);
            assertEquals(run(baselineRange, false, mode, 1), run(candidateRange, true, mode, 1));
            assertEquals(new ArrayList<>(baseline.entrySet()), new ArrayList<>(candidate.entrySet()));
        }
    }

    @Test
    void boundedClearPreservesLiveEntryDetachAndSlotReuse()
    {
        for (int low = 0; low < 20; low++)
        {
            for (int high = low; high <= 20; high++)
            {
                IndexedSortedMap<Integer, String> candidate = map(null, 20);
                IndexedSortedMap<Integer, String> baseline = map(null, 20);
                List<Map.Entry<Integer, String>> savedCandidate = new ArrayList<>(candidate.entrySet());
                List<Map.Entry<Integer, String>> savedBaseline = new ArrayList<>(baseline.entrySet());
                candidate.subMap(low, high).clear();
                var oldCursor = baseline.subMap(low, high).entrySet().iterator();
                while (oldCursor.hasNext())
                {
                    oldCursor.next();
                    oldCursor.remove();
                }
                assertEquals(savedBaseline, savedCandidate);
                assertEquals(baseline, candidate);
                candidate.put(100, "reused");
                baseline.put(100, "reused");
                for (int index = 0; index < savedCandidate.size(); index++)
                {
                    assertEquals(savedBaseline.get(index).setValue("saved"), savedCandidate.get(index).setValue("saved"));
                }
                assertEquals(baseline, candidate);
            }
        }
    }

    @Test
    void boundedClearPreservesComparatorMutationAndFailure()
    {
        for (int mutation = 0; mutation < 4; mutation++)
        {
            for (int trigger = 1; trigger < 25; trigger++)
            {
                assertEquals(reentrantClear(false, mutation, trigger), reentrantClear(true, mutation, trigger));
            }
        }
    }

    private static List<Object> reentrantClear(boolean candidate, int mutation, int trigger)
    {
        List<Object> result = new ArrayList<>();
        class ReentrantOrder implements Comparator<Integer>
        {
            private IndexedSortedMap<Integer, String> owner;
            private int remaining;

            @Override public int compare(Integer left, Integer right)
            {
                if (this.remaining != 0)
                {
                    result.add(Arrays.asList(left, right));
                    if (--this.remaining == 0)
                    {
                        switch (mutation)
                        {
                            case 0 -> this.owner.clear();
                            case 1 -> this.owner.remove(3);
                            case 2 -> this.owner.remove(9);
                            case 3 -> this.owner.put(21, "new");
                            default -> throw new AssertionError(mutation);
                        }
                        // A comparator which mutates and then returns can leave
                        // even the original owner traversing a detached tree.
                        // This fault injects a failure before traversal resumes.
                        throw new IllegalArgumentException("comparator sentinel");
                    }
                }
                return Integer.compare(left, right);
            }
        }

        ReentrantOrder order = new ReentrantOrder();
        IndexedSortedMap<Integer, String> map = new IndexedSortedMap<>(order);
        order.owner = map;
        for (int key = 0; key < 20; key++)
        {
            map.put(key, "v" + key);
        }
        SortedMap<Integer, String> range = map.subMap(3, 17);
        order.remaining = trigger;
        try
        {
            if (candidate)
            {
                range.clear();
            }
            else
            {
                var cursor = range.entrySet().iterator();
                while (cursor.hasNext())
                {
                    cursor.next();
                    cursor.remove();
                }
            }
            result.add("return");
        }
        catch (RuntimeException failure)
        {
            result.add(Arrays.asList(failure.getClass().getName(), failure.getMessage()));
        }
        order.remaining = 0;
        result.add(new ArrayList<>(map.entrySet()));
        return result;
    }

    private static List<Comparator<Integer>> orders()
    {
        return Arrays.asList(null, Comparator.reverseOrder(), Comparator.nullsFirst(Comparator.naturalOrder()));
    }

    private static IndexedSortedMap<Integer, String> map(Comparator<Integer> order, int count)
    {
        IndexedSortedMap<Integer, String> map = new IndexedSortedMap<>(order);
        for (int index = count - 1; index >= 0; index--)
        {
            map.put(index, index % 3 == 0 ? null : "v" + index);
        }
        if (order != null && order.compare(1, 2) < 0 && count != 0)
        {
            map.put(null, null);
        }
        return map;
    }

    private static List<Object> run(IndexedSortedMap<Integer, String> map, boolean candidate,
            int mode, int callback)
    {
        List<Object> result = new ArrayList<>();
        int[] calls = {0};
        BiConsumer<Integer, String> action = (key, value) ->
        {
            result.add(Arrays.asList(key, value));
            if (++calls[0] == callback)
            {
                switch (mode)
                {
                    case 0 -> map.put(key, "updated");
                    case 1 -> map.put(100, "inserted");
                    case 2 -> map.remove(key);
                    case 3 -> map.clear();
                    case 4 -> map.remove(Objects.equals(key, 0) ? 1 : 0);
                    case 5 ->
                    {
                        map.put(key, "updated");
                        throw new IllegalArgumentException("sentinel");
                    }
                    default -> throw new AssertionError(mode);
                }
            }
        };
        try
        {
            if (candidate)
            {
                map.forEachKeyValue(action);
            }
            else
            {
                originalTraversal(map, action);
            }
            result.add("return");
        }
        catch (RuntimeException failure)
        {
            result.add(failure.getClass().getName());
        }
        return result;
    }

    private static <K, V> void originalTraversal(SortedMap<K, V> map, BiConsumer<? super K, ? super V> action)
    {
        if (!map.isEmpty())
        {
            for (Map.Entry<K, V> entry : map.entrySet())
            {
                action.accept(entry.getKey(), entry.getValue());
            }
        }
    }

    static void assertNoEntries(IndexedSortedMap<?, ?> map) throws Exception
    {
        Field storageField = IndexedSortedMap.class.getDeclaredField("storage");
        storageField.setAccessible(true);
        Object storage = storageField.get(map);
        Field backendField = storage.getClass().getDeclaredField("backend");
        backendField.setAccessible(true);
        IndexedRedBlackMap<?, ?> backend = (IndexedRedBlackMap<?, ?>) backendField.get(storage);
        for (Object view : backend.tree.arena.entryViews)
        {
            assertNull(view);
        }
    }
}
