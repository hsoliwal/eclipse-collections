/*
 * Copyright (c) 2026 Hitesh Soliwal and contributors to the Synexia Project.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */
package com.m3.collections.eclipse.impl.list.mutable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FastListIndexedGrowthTest
{
    @Test
    void growthInsertsAtEveryOffsetWithoutLosingPrefixSuffixOrNulls()
    {
        for (int size = 0; size <= 40; size++)
        {
            for (int index = 0; index <= size; index++)
            {
                for (int inserted = 1; inserted <= 12; inserted++)
                {
                    FastList<Integer> list = new FastList<>(size);
                    List<Integer> expected = new ArrayList<>();
                    for (int i = 0; i < size; i++)
                    {
                        Integer value = i % 5 == 0 ? null : i;
                        list.add(value);
                        expected.add(value);
                    }
                    FastList<Integer> source = new FastList<>(inserted);
                    for (int i = 0; i < inserted; i++)
                    {
                        source.add(i % 3 == 0 ? null : -i - 1);
                    }
                    Object[] old = list.items;
                    expected.addAll(index, source);
                    assertTrue(list.addAll(index, source));
                    assertEquals(expected, list);
                    Object[] grown = list.items;
                    assertNotSame(old, grown);
                    assertEquals(Math.max(size + (size >> 1) + 1, size + inserted), grown.length);
                    for (int tail = list.size(); tail < grown.length; tail++)
                    {
                        assertSame(null, grown[tail]);
                    }
                    assertEquals(inserted, source.size());
                }
            }
        }
    }

    @Test
    void selfAndDistinctOwnersSharingAnArrayRetainSnapshotOrder()
    {
        for (int size = 1; size <= 32; size++)
        {
            for (int index = 0; index <= size; index++)
            {
                FastList<Integer> self = new FastList<>(size);
                for (int i = 0; i < size; i++)
                {
                    self.add(i);
                }
                List<Integer> expected = new ArrayList<>(self);
                expected.addAll(index, new ArrayList<>(expected));
                self.addAll(index, self);
                assertEquals(expected, self);

                Integer[] array = new Integer[size];
                for (int i = 0; i < size; i++)
                {
                    array[i] = i;
                }
                Integer[] unchanged = array.clone();
                FastList<Integer> target = new FastList<>(size, array);
                FastList<Integer> source = new FastList<>(Math.max(1, size / 2), array);
                List<Integer> sharedExpected = new ArrayList<>(target);
                sharedExpected.addAll(index, new ArrayList<>(source));
                target.addAll(index, source);
                assertEquals(sharedExpected, target);
                assertSame(array, source.items);
                assertArrayEquals(unchanged, source.items);
                assertNotSame(array, target.items);
            }
        }
    }

    @Test
    void subclassesStillObserveEnsureCapacityAndItsAbruptCompletion()
    {
        class ObservedList extends FastList<String>
        {
            int calls;
            RuntimeException failure;

            ObservedList()
            {
                super(2);
            }

            @Override
            public void ensureCapacity(int capacity)
            {
                this.calls++;
                assertEquals(4, capacity);
                assertEquals(Arrays.asList("a", "b"), this);
                if (this.failure != null)
                {
                    throw this.failure;
                }
                super.ensureCapacity(capacity);
            }
        }
        ObservedList list = new ObservedList();
        list.add("a");
        list.add("b");
        FastList<String> source = FastList.newListWith("x", "y");
        RuntimeException failure = new IllegalStateException("capacity callback");
        list.failure = failure;
        assertSame(failure, assertThrows(IllegalStateException.class, () -> list.addAll(1, source)));
        assertEquals(Arrays.asList("a", "b"), list);
        list.failure = null;
        assertTrue(list.addAll(1, source));
        assertEquals(Arrays.asList("a", "x", "y", "b"), list);
        assertEquals(2, list.calls);
    }

    @Test
    void subclassCapacityCallbackCanRestoreProtectedBackingStorage()
    {
        class RepairingList extends FastList<String>
        {
            int calls;

            RepairingList()
            {
                this.size = 2;
                this.items = null;
            }

            @Override
            public void ensureCapacity(int capacity)
            {
                this.calls++;
                assertEquals(4, capacity);
                this.items = new String[]{"a", "b", null, null};
            }
        }
        RepairingList list = new RepairingList();
        assertTrue(list.addAll(1, FastList.newListWith("x", "y")));
        assertEquals(Arrays.asList("a", "x", "y", "b"), list);
        assertEquals(1, list.calls);
    }

    @Test
    void noGrowthRetainsBackingArrayAndValidationPrecedesEmptyCheck()
    {
        FastList<String> list = new FastList<>(8);
        list.add("a");
        list.add("b");
        Object[] storage = list.items;
        assertTrue(list.addAll(1, FastList.newListWith("x", null)));
        assertSame(storage, list.items);
        assertEquals(Arrays.asList("a", "x", null, "b"), list);
        assertFalse(list.addAll(2, new FastList<>()));
        assertThrows(IndexOutOfBoundsException.class, () -> list.addAll(-1, new FastList<>()));
        assertThrows(IndexOutOfBoundsException.class, () -> list.addAll(5, new FastList<>()));
        assertThrows(IndexOutOfBoundsException.class, () -> list.addAll(-1, null));
        assertThrows(NullPointerException.class, () -> list.addAll(1, null));
    }

    @Test
    void grownListRetainsIteratorCloneAndSerializationBehavior() throws Exception
    {
        FastList<String> list = FastList.newListWith("a", "b");
        Iterator<String> iterator = list.iterator();
        list.addAll(1, FastList.newListWith(null, "x"));
        assertEquals("a", iterator.next());
        assertSame(null, iterator.next());
        assertEquals("x", iterator.next());
        assertEquals("b", iterator.next());
        assertFalse(iterator.hasNext());
        FastList<String> clone = list.clone();
        assertEquals(list, clone);
        assertNotSame(list.items, clone.items);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes))
        {
            out.writeObject(list);
        }
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray())))
        {
            assertEquals(list, in.readObject());
        }
    }
}
