/* SPDX-License-Identifier: Apache-2.0 OR EPL-2.0 */

package com.m3.collections.eclipse.impl.map.sorted.mutable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.m3.collections.eclipse.api.partition.list.PartitionMutableList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class M3TreeSortedMapEntryFreeTest
{
    @Test
    void publicForEachUsesKeyValueLanesWithoutCreatingEntries() throws Exception
    {
        TreeSortedMap<Integer, String> map = new TreeSortedMap<>();
        List<Object> expected = new ArrayList<>();
        for (int key = 0; key < 257; key++)
        {
            String value = key % 7 == 0 ? null : "v" + key;
            map.put(key, value);
            expected.add(Arrays.asList(key, value));
        }
        List<Object> actual = new ArrayList<>();
        map.forEachKeyValue((key, value) -> actual.add(Arrays.asList(key, value)));
        assertEquals(expected, actual);
        M3IndexedEntryFreeTest.assertNoEntries(storage(map));
        new TreeSortedMap<>().forEachKeyValue(null);
        assertThrows(NullPointerException.class, () -> map.forEachKeyValue(null));
    }

    @Test
    void partitionWhileUsesValueLaneAndCallsPredicateOnlyThroughFirstRejection() throws Exception
    {
        for (int count = 0; count < 65; count++)
        {
            for (int stop = 0; stop <= count; stop++)
            {
                TreeSortedMap<Integer, Integer> map = new TreeSortedMap<>();
                for (int key = count - 1; key >= 0; key--)
                {
                    map.put(key, key);
                }
                int[] calls = {0};
                int cutoff = stop;
                PartitionMutableList<Integer> result = map.partitionWhile(value ->
                {
                    calls[0]++;
                    return value < cutoff;
                });
                List<Integer> selected = new ArrayList<>();
                List<Integer> rejected = new ArrayList<>();
                for (int key = 0; key < count; key++)
                {
                    (key < stop ? selected : rejected).add(key);
                }
                assertEquals(selected, result.getSelected());
                assertEquals(rejected, result.getRejected());
                assertEquals(Math.min(count, stop + 1), calls[0]);
                M3IndexedEntryFreeTest.assertNoEntries(storage(map));
            }
        }
    }

    @Test
    void partitionPreservesNullAndThrowingCallbacks()
    {
        TreeSortedMap<Integer, String> empty = new TreeSortedMap<>();
        assertEquals(List.of(), empty.partitionWhile(null).getSelected());
        TreeSortedMap<Integer, String> map = new TreeSortedMap<>();
        map.put(1, null);
        map.put(2, "two");
        assertEquals(Arrays.asList((String) null), map.partitionWhile(value -> value == null).getSelected());
        assertThrows(NullPointerException.class, () -> map.partitionWhile(null));
        RuntimeException expected = new RuntimeException("predicate sentinel");
        assertSame(expected, assertThrows(RuntimeException.class,
                () -> map.partitionWhile(value -> { throw expected; })));
    }

    private static IndexedSortedMap<?, ?> storage(TreeSortedMap<?, ?> map) throws Exception
    {
        Field field = TreeSortedMap.class.getDeclaredField("treeMap");
        field.setAccessible(true);
        return (IndexedSortedMap<?, ?>) field.get(map);
    }
}
