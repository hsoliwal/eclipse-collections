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
 ******************************************************************************/
package com.m3.collections.eclipse.impl.map.sorted.mutable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

class M3IndexedSortedMapClearProjectionTest
{
    @Test
    void boundedClearDoesNotAllocateAnEntryForEveryRemovedKey()
    {
        assumeTrue(ManagementFactory.getThreadMXBean() instanceof com.sun.management.ThreadMXBean);
        com.sun.management.ThreadMXBean bean =
                (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();
        assumeTrue(bean.isThreadAllocatedMemorySupported());
        bean.setThreadAllocatedMemoryEnabled(true);
        int count = 4096;
        // Resolve traversal classes on separate maps so this test is independent
        // of class loading and of which other test methods ran before it.
        for (int warmup = 0; warmup < 3; warmup++)
        {
            IndexedSortedMap<Integer, Integer> warmupMap = new IndexedSortedMap<>();
            for (int key = 0; key <= count; key++)
            {
                warmupMap.put(key, key);
            }
            warmupMap.subMap(0, count).clear();
        }
        IndexedSortedMap<Integer, Integer> map = new IndexedSortedMap<>();
        for (int key = 0; key <= count; key++)
        {
            map.put(key, key);
        }
        SortedMap<Integer, Integer> view = map.subMap(0, count);
        // Resolve lazy view construction before measuring the traversal itself.
        view.keySet();
        long thread = Thread.currentThread().getId();
        bean.getThreadAllocatedBytes(thread);
        long before = bean.getThreadAllocatedBytes(thread);
        view.clear();
        long allocated = bean.getThreadAllocatedBytes(thread) - before;
        System.out.println("bounded clear: removed=" + count + ", allocatedBytes=" + allocated);
        assertEquals(List.of(count), new ArrayList<>(map.keySet()));
        assertTrue(allocated < count * 8L, "bounded clear allocated bytes=" + allocated);
    }

    @Test
    void boundedClearPreservesOrderingCallbacksAndNestedViews()
    {
        for (int selected = 0; selected < 3; selected++)
        {
            List<String> expectedTrace = new ArrayList<>();
            List<String> actualTrace = new ArrayList<>();
            Comparator<Integer> expectedComparator = (left, right) ->
            {
                expectedTrace.add(left + ":" + right);
                return Integer.compare(left, right);
            };
            Comparator<Integer> actualComparator = (left, right) ->
            {
                actualTrace.add(left + ":" + right);
                return Integer.compare(left, right);
            };
            SortedMap<Integer, String> expected = new TreeMap<>(expectedComparator);
            SortedMap<Integer, String> actual = new IndexedSortedMap<>(actualComparator);
            for (int key : new int[]{4, 2, 6, 1, 3, 5, 7})
            {
                expected.put(key, key == 3 ? null : "v" + key);
                actual.put(key, key == 3 ? null : "v" + key);
            }
            SortedMap<Integer, String> expectedView = view(expected, selected);
            SortedMap<Integer, String> actualView = view(actual, selected);
            expectedTrace.clear();
            actualTrace.clear();
            expectedView.clear();
            actualView.clear();
            assertEquals(expectedTrace, actualTrace);
            assertEquals(new ArrayList<>(expected.keySet()), new ArrayList<>(actual.keySet()));
            assertEquals(new ArrayList<>(expected.values()), new ArrayList<>(actual.values()));
            assertTrue(actualView.isEmpty());
        }
    }

    private static <V> SortedMap<Integer, V> view(SortedMap<Integer, V> map, int selected)
    {
        if (selected == 0)
        {
            return map.headMap(5);
        }
        if (selected == 1)
        {
            return map.tailMap(3);
        }
        return map.subMap(2, 7).tailMap(3).headMap(6);
    }

    @Test
    void exposedEntriesDetachAndExistingIteratorsStayFailFast() throws Exception
    {
        IndexedSortedMap<Integer, String> map = new IndexedSortedMap<>();
        map.put(1, "first");
        map.put(2, "second");
        map.put(3, "third");
        Map.Entry<Integer, String> saved = map.entrySet().iterator().next();
        Iterator<Integer> stale = map.keySet().iterator();
        map.headMap(3).clear();
        assertThrows(ConcurrentModificationException.class, stale::next);
        map.put(4, "replacement");
        assertEquals("first", saved.setValue("detached"));
        assertEquals(1, saved.getKey());
        assertEquals("replacement", map.get(4));

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes))
        {
            output.writeObject(map);
        }
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray())))
        {
            assertEquals(map, input.readObject());
        }
    }
}
