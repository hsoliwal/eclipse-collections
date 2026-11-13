/*
 * Copyright (c) 2026 Hitesh Soliwal and Contributors to the Synexia Project.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.m3.collections.eclipse.impl.map.mutable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UnifiedMapCollisionGrowthTest
{
    private static long observation = 1;

    private record Key(int id) implements Serializable
    {
        @Override
        public int hashCode()
        {
            return 0;
        }

        @Override
        public boolean equals(Object other)
        {
            observation = observation * 31 + this.id;
            observation = observation * 31 + (other instanceof Key key ? key.id : -1);
            return other instanceof Key key && this.id == key.id;
        }
    }

    // The donor's inherited generic newEmpty(int) bridge is already unchecked.
    @SuppressWarnings("unchecked")
    public static final class StorageMap extends UnifiedMap<Key, Integer>
    {
        private static final long serialVersionUID = 1L;

        public StorageMap()
        {
            this(0);
        }

        StorageMap(int capacity)
        {
            super(capacity);
        }

        Object[] chain()
        {
            return this.table[0] == CHAINED_KEY ? (Object[]) this.table[1] : null;
        }

        int actualWords()
        {
            int words = this.table.length + 2;
            for (int index = 0; index < this.table.length; index += 2)
            {
                if (this.table[index] == CHAINED_KEY)
                {
                    words += ((Object[]) this.table[index + 1]).length + 2;
                }
            }
            return words;
        }
    }

    @Test
    void everyInsertionFamilyPreservesOccupiedPrefixAndCallbacks()
    {
        for (int family = 0; family < 9; family++)
        {
            StorageMap actual = new StorageMap(1);
            Map<Key, Integer> expected = oracle();
            for (int id = 0; id < 257; id++)
            {
                Key key = id == 0 ? null : new Key(id);
                int value = id + 1;
                int priorSize = actual.size();
                int[] callbacks = {0};
                insert(actual, family, key, value, () -> {
                    callbacks[0]++;
                    assertEquals(priorSize, actual.size());
                    assertFalse(actual.containsKey(key));
                });
                expected.put(key, value);
                assertEquals(family == 0 || family == 3 || family == 7 ? 0 : 1, callbacks[0]);
                assertEquals(expected, actual);
                assertEquals(actual.actualWords(), actual.getMapMemoryUsedInWords());
                if (actual.chain() != null)
                {
                    assertEquals(0, actual.chain().length & 1);
                }
                observe(actual);
            }
            assertEquals(new ArrayList<>(expected.values()).size(), actual.size());
            assertEquals(1, actual.getCollidingBuckets());
        }
    }

    private static void insert(StorageMap map, int family, Key key, int value, Runnable callback)
    {
        switch (family)
        {
            case 0 -> assertEquals(null, map.put(key, value));
            case 1 -> assertEquals(value, map.updateValue(key, () -> {
                callback.run();
                return 0;
            }, ignored -> value));
            case 2 -> assertEquals(value, map.updateValueWith(key, () -> {
                callback.run();
                return 0;
            }, (ignored, parameter) -> parameter, value));
            case 3 -> assertEquals(value, map.merge(key, value, Integer::sum));
            case 4 -> assertEquals(value, map.computeIfAbsent(key, ignored -> {
                callback.run();
                return value;
            }));
            case 5 -> assertEquals(value, map.compute(key, (ignored, previous) -> {
                callback.run();
                assertEquals(null, previous);
                return value;
            }));
            case 6 -> assertEquals(value, map.getIfAbsentPut(key, () -> {
                callback.run();
                return value;
            }));
            case 7 -> assertEquals(value, map.getIfAbsentPut(key, Integer.valueOf(value)));
            case 8 -> assertEquals(value, map.getIfAbsentPutWith(key, parameter -> {
                callback.run();
                return parameter;
            }, value));
            default -> throw new AssertionError(family);
        }
    }

    @Test
    void randomizedCollisionMutationsMatchJdkMap()
    {
        StorageMap actual = new StorageMap(1);
        Map<Key, Integer> expected = oracle();
        Random random = new Random(0x51A7E5L);
        for (int step = 0; step < 20_000; step++)
        {
            int id = random.nextInt(257);
            Key key = id == 0 ? null : new Key(id);
            Integer value = random.nextInt(7) == 0 ? null : random.nextInt(1000);
            switch (random.nextInt(7))
            {
                case 0 -> assertEquals(expected.put(key, value), actual.put(key, value));
                case 1 -> assertEquals(expected.remove(key), actual.remove(key));
                case 2 -> assertEquals(expected.computeIfAbsent(key, ignored -> value),
                        actual.computeIfAbsent(key, ignored -> value));
                case 3 -> assertEquals(expected.compute(key, (ignored, old) -> value),
                        actual.compute(key, (ignored, old) -> value));
                case 4 -> assertEquals(expected.replace(key, value), actual.replace(key, value));
                case 5 -> assertEquals(expected.remove(key, value), actual.remove(key, value));
                case 6 -> {
                    int increment = value == null ? 1 : value;
                    assertEquals(expected.merge(key, increment, Integer::sum),
                            actual.merge(key, increment, Integer::sum));
                }
                default -> throw new AssertionError();
            }
            assertEquals(expected.size(), actual.size());
            if ((step & 127) == 0)
            {
                assertEquals(expected, actual);
                assertEquals(expected.keySet(), actual.keySet());
                assertEquals(expected.entrySet(), actual.entrySet());
                observe(actual);
            }
        }
        assertEquals(expected, actual);
    }

    @Test
    void nullsEntryMutationIteratorRemovalCloneAndTrimRemainBacked()
    {
        StorageMap actual = new StorageMap(8192);
        for (int id = 0; id < 257; id++)
        {
            actual.put(id == 0 ? null : new Key(id), id % 7 == 0 ? null : id);
        }
        var keys = actual.keySet();
        var entries = actual.entrySet();
        var values = actual.values();
        Map<Key, Integer> expected = oracle();
        expected.putAll(actual);
        for (Map.Entry<Key, Integer> entry : entries)
        {
            if (entry.getKey() != null && entry.getKey().id() % 3 == 0)
            {
                assertEquals(expected.put(entry.getKey(), -1), entry.setValue(-1));
            }
        }
        Iterator<Key> iterator = keys.iterator();
        while (iterator.hasNext())
        {
            Key key = iterator.next();
            if (key == null || key.id() % 4 == 0)
            {
                iterator.remove();
                expected.remove(key);
            }
        }
        assertEquals(expected, actual);
        observe(actual);
        assertTrue(actual.trimToSize());
        assertEquals(expected, actual);
        assertEquals(expected.keySet(), keys);
        assertEquals(expected.entrySet(), entries);
        assertEquals(expected.size(), values.size());
        assertEquals(actual.actualWords(), actual.getMapMemoryUsedInWords());
        UnifiedMap<Key, Integer> clone = actual.clone();
        assertEquals(actual, clone);
        assertNotSame(actual, clone);
        actual.clear();
        assertTrue(keys.isEmpty());
        assertTrue(entries.isEmpty());
        assertTrue(values.isEmpty());
        assertEquals(expected, clone);
        observe(clone);
    }

    @Test
    void callbackFailuresPreserveMappingsAndExceptionIdentity()
    {
        for (int family : new int[]{1, 2, 4, 5, 6, 8})
        {
            for (int size : new int[]{2, 4, 6, 7, 8, 9, 12, 16, 31, 64, 128})
            {
                StorageMap map = new StorageMap(512);
                for (int id = 0; id < size; id++)
                {
                    map.put(new Key(id), id);
                }
                Map<Key, Integer> snapshot = oracle();
                snapshot.putAll(map);
                IllegalStateException failure = new IllegalStateException("frozen callback");
                assertSame(failure, assertThrows(IllegalStateException.class,
                        () -> insert(map, family, new Key(size), 11, () -> { throw failure; })));
                assertEquals(snapshot, map);
                observe(map);
            }
        }
    }

    @Test
    void evenLengthChainsSupportBulkAndBatchOperations()
    {
        for (int size : new int[]{7, 10, 22, 49, 145})
        {
            StorageMap map = new StorageMap(512);
            Map<Key, Integer> expected = oracle();
            for (int id = 0; id < size; id++)
            {
                Key key = new Key(id);
                map.put(key, id);
                expected.put(key, id);
            }
            assertEquals(new ArrayList<>(expected.keySet()), List.of(map.keySet().toArray()));
            assertEquals(new ArrayList<>(expected.values()), List.of(map.values().toArray()));
            assertEquals(new ArrayList<>(expected.entrySet()), List.of(map.entrySet().toArray()));
            List<Integer> visited = new ArrayList<>();
            int batches = map.getBatchCount(7);
            for (int batch = 0; batch < batches; batch++)
            {
                map.batchForEach(visited::add, batch, batches);
            }
            assertEquals(new ArrayList<>(expected.values()), visited);
            assertEquals(expected.values().removeAll(List.of(1, 5, 9, 17)),
                    map.values().removeAll(List.of(1, 5, 9, 17)));
            assertEquals(expected, map);
            assertEquals(expected.values().retainAll(List.of(0, 2, 4, 8, 18, 32, 64, 128)),
                    map.values().retainAll(List.of(0, 2, 4, 8, 18, 32, 64, 128)));
            assertEquals(expected, map);
            assertEquals(expected.entrySet().removeIf(entry -> entry.getValue() % 4 == 0),
                    map.removeIf((key, value) -> value % 4 == 0));
            assertEquals(expected, map);
            observe(map);
        }
    }

    @Test
    void serializationRetainsLogicalOrderAndContents() throws Exception
    {
        UnifiedMap<Key, Integer> map = new UnifiedMap<>(1);
        for (int id = 0; id < 257; id++)
        {
            map.put(id == 0 ? null : new Key(id), id % 5 == 0 ? null : id);
        }
        byte[] bytes;
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                ObjectOutputStream output = new ObjectOutputStream(buffer))
        {
            output.writeObject(map);
            output.flush();
            bytes = buffer.toByteArray();
        }
        for (byte value : bytes)
        {
            observation = observation * 31 + value;
        }
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes)))
        {
            Object restored = input.readObject();
            assertEquals(map, restored);
        }
    }

    @Test
    void collisionGrowthCopiesLinearReferences()
    {
        StorageMap map = new StorageMap(4096);
        Object[] previous = null;
        long copiedReferences = 0;
        int growths = 0;
        for (int id = 0; id < 4096; id++)
        {
            map.put(new Key(id), id);
            Object[] current = map.chain();
            if (current != previous)
            {
                copiedReferences += previous == null ? 0 : previous.length;
                growths++;
                previous = current;
            }
        }
        assertTrue(growths <= 24, "geometric collision growth count: " + growths);
        assertTrue(copiedReferences < 6L * map.size(),
                "collision storage must not copy quadratically: " + copiedReferences);
        assertTrue(previous.length >= 2 * map.size());
        assertTrue(previous.length < 3 * map.size());
        assertEquals(map.actualWords(), map.getMapMemoryUsedInWords());
    }

    @Test
    void checkedGrowthKeepsCompletePairsAtIntegerLimit() throws Exception
    {
        Method growth = UnifiedMap.class.getDeclaredMethod("expandedChainCapacity", int.class);
        growth.setAccessible(true);
        for (int old : new int[]{4, 8, 12, 1024, 1 << 30, Integer.MAX_VALUE - 5})
        {
            int next = (Integer) growth.invoke(null, old);
            assertTrue(next > old);
            assertEquals(0, next & 1);
        }
        for (int old : new int[]{Integer.MAX_VALUE - 3, Integer.MAX_VALUE - 1})
        {
            NegativeArraySizeException incumbent = assertThrows(NegativeArraySizeException.class,
                    () -> Objects.requireNonNull(new Object[old + 4]));
            InvocationTargetException problem = assertThrows(InvocationTargetException.class,
                    () -> growth.invoke(null, old));
            assertTrue(problem.getCause() instanceof NegativeArraySizeException);
            assertEquals(incumbent.getMessage(), problem.getCause().getMessage());
        }
    }

    private static Map<Key, Integer> oracle()
    {
        return new TreeMap<>(Comparator.nullsFirst(Comparator.comparingInt(Key::id)));
    }

    private static void observe(UnifiedMap<Key, Integer> map)
    {
        map.forEachKeyValue((key, value) -> {
            observation = observation * 31 + (key == null ? -1 : key.id());
            observation = observation * 31 + Objects.hashCode(value);
        });
    }

    public static void main(String[] args) throws Exception
    {
        UnifiedMapCollisionGrowthTest suite = new UnifiedMapCollisionGrowthTest();
        suite.everyInsertionFamilyPreservesOccupiedPrefixAndCallbacks();
        suite.randomizedCollisionMutationsMatchJdkMap();
        suite.nullsEntryMutationIteratorRemovalCloneAndTrimRemainBacked();
        suite.callbackFailuresPreserveMappingsAndExceptionIdentity();
        suite.evenLengthChainsSupportBulkAndBatchOperations();
        suite.serializationRetainsLogicalOrderAndContents();
        System.getLogger(UnifiedMapCollisionGrowthTest.class.getName()).log(System.Logger.Level.INFO,
                "behavior trace=" + Long.toUnsignedString(observation, 16));
        List<Key> keys = new ArrayList<>();
        for (int index = 0; index < 4096; index++)
        {
            keys.add(new Key(index));
        }
        var bean = (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();
        bean.setThreadAllocatedMemoryEnabled(true);
        for (int trial = 0; trial < 4; trial++)
        {
            long before = bean.getCurrentThreadAllocatedBytes();
            StorageMap map = new StorageMap(keys.size());
            Object[] previous = null;
            long copied = 0;
            int growths = 0;
            for (Key key : keys)
            {
                map.put(key, 1);
                Object[] current = map.chain();
                if (current != previous)
                {
                    copied += previous == null ? 0 : previous.length;
                    growths++;
                    previous = current;
                }
            }
            long allocated = bean.getCurrentThreadAllocatedBytes() - before;
            System.getLogger(UnifiedMapCollisionGrowthTest.class.getName()).log(System.Logger.Level.INFO,
                    "trial=" + trial + " allocatedBytes=" + allocated
                            + " chainGrowths=" + growths + " copiedReferences=" + copied
                            + " retainedChainSlots=" + previous.length);
        }
    }
}
