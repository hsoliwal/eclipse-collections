/*
 * SPDX-FileCopyrightText: 2026 Hitesh Soliwal and Contributors to the Synexia Project
 * SPDX-License-Identifier: Apache-2.0
 */
package com.m3.collections.eclipse.impl.list.mutable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.m3.collections.eclipse.impl.utility.ArrayListIterate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FastListArrayListIndexedGrowthTest
{
    private static void sameElements(List<?> expected, FastList<?> actual)
    {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++)
        {
            assertSame(expected.get(i), actual.get(i), "identity at " + i);
        }
    }

    @Test
    void everyOffsetPreservesValuesReferencesCapacityAndInputOwnership()
    {
        int[] sizes = {0, 1, 2, 3, 8, 17, 32, 65};
        int[] additions = {0, 1, 2, 5, 18};
        int[] spareCapacities = {0, 1, 8, 64};
        for (int size : sizes)
        {
            for (int index = 0; index <= size; index++)
            {
                for (int added : additions)
                {
                    for (int spare : spareCapacities)
                    {
                        int capacity = size + spare;
                        FastList<Object> list = new FastList<>(capacity);
                        ArrayList<Object> expected = new ArrayList<>();
                        for (int i = 0; i < size; i++)
                        {
                            Object value = i % 4 == 0 ? null : new Object();
                            list.add(value);
                            expected.add(value);
                        }
                        ArrayList<Object> source = new ArrayList<>();
                        for (int i = 0; i < added; i++)
                        {
                            source.add(i % 3 == 0 ? null : new Object());
                        }
                        Object[] sourceBefore = source.toArray();
                        Object[] oldStorage = list.items;
                        Object[] oldValues = oldStorage.clone();
                        expected.addAll(index, source);
                        assertEquals(added != 0, list.addAll(index, source));
                        sameElements(expected, list);
                        assertArrayEquals(sourceBefore, source.toArray());
                        int requested = size + added;
                        if (requested > capacity)
                        {
                            assertNotSame(oldStorage, list.items);
                            assertEquals(Math.max(capacity + (capacity >> 1) + 1, requested), list.items.length);
                            assertArrayEquals(oldValues, oldStorage);
                        }
                        else
                        {
                            assertSame(oldStorage, list.items);
                        }
                        for (int i = list.size(); i < list.items.length; i++)
                        {
                            assertSame(null, list.items[i]);
                        }
                    }
                }
            }
        }
    }

    @Test
    void repeatedRandomMutationsAgreeWithIndependentArrayListOracle()
    {
        for (int seed = 0; seed < 12; seed++)
        {
            Random random = new Random(0x5a17c0deL + seed);
            FastList<Object> list = new FastList<>(seed);
            ArrayList<Object> expected = new ArrayList<>();
            Object[] values = {null, new Object(), new Object(), "same", Integer.valueOf(17)};
            for (int step = 0; step < 300; step++)
            {
                if (step % 41 == 0)
                {
                    list.clear();
                    expected.clear();
                }
                else if (!expected.isEmpty() && random.nextInt(4) == 0)
                {
                    int index = random.nextInt(expected.size());
                    assertSame(expected.remove(index), list.remove(index));
                }
                else
                {
                    int index = random.nextInt(expected.size() + 1);
                    ArrayList<Object> source = new ArrayList<>();
                    for (int i = random.nextInt(19); i > 0; i--)
                    {
                        source.add(values[random.nextInt(values.length)]);
                    }
                    assertEquals(expected.addAll(index, source), list.addAll(index, source));
                }
                sameElements(expected, list);
            }
        }
    }

    @Test
    void growthWidensTypedBackingAndDoesNotPublishDirtyTailOrMutateAliases()
    {
        String[] storage = {"a", "b", "outside logical size"};
        FastList<Object> list = new FastList<>(2, storage);
        Object token = new Object();
        ArrayList<Object> source = new ArrayList<>(Arrays.asList(token, null, "z"));
        assertTrue(list.addAll(1, source));
        sameElements(Arrays.asList("a", token, null, "z", "b"), list);
        assertEquals(Object[].class, list.items.getClass());
        assertArrayEquals(new String[]{"a", "b", "outside logical size"}, storage);
        source.clear();
        assertSame(token, list.get(1));
    }

    @Test
    void subclassesKeepCapacityCallbackOrderAndTheExactThrownInstance()
    {
        class ObservedList extends FastList<String>
        {
            private static final long serialVersionUID = 1L;
            int calls;
            RuntimeException failure;

            public ObservedList()
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
        ArrayList<String> source = new ArrayList<>(Arrays.asList("x", "y"));
        RuntimeException failure = new IllegalStateException("capacity callback");
        list.failure = failure;
        Object[] before = list.items;
        assertSame(failure, assertThrows(IllegalStateException.class, () -> list.addAll(1, source)));
        assertSame(before, list.items);
        assertEquals(Arrays.asList("a", "b"), list);
        list.failure = null;
        assertTrue(list.addAll(1, source));
        assertEquals(Arrays.asList("a", "x", "y", "b"), list);
        assertEquals(2, list.calls);
    }

    @Test
    void subclassCapacityCallbackCanRepairNullProtectedStorage()
    {
        class RepairingList extends FastList<String>
        {
            private static final long serialVersionUID = 1L;
            int calls;

            public RepairingList()
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
        assertTrue(list.addAll(1, new ArrayList<>(Arrays.asList("x", "y"))));
        assertEquals(Arrays.asList("a", "x", "y", "b"), list);
        assertEquals(1, list.calls);
    }

    @Test
    void sourceSubclassGetFailureKeepsCallbacksAndPartialMutation()
    {
        List<String> calls = new ArrayList<>();
        IllegalStateException failure = new IllegalStateException("get callback");
        class Source extends ArrayList<String>
        {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isEmpty()
            {
                calls.add("empty");
                return super.isEmpty();
            }

            @Override
            public int size()
            {
                calls.add("size");
                return super.size();
            }

            @Override
            public String get(int index)
            {
                calls.add("get:" + index);
                if (index == 1)
                {
                    throw failure;
                }
                return super.get(index);
            }
        }
        Source source = new Source();
        source.add("x");
        source.add("y");
        FastList<String> list = FastList.newListWith("a", "b");
        assertSame(failure, assertThrows(IllegalStateException.class, () -> list.addAll(1, source)));
        assertEquals(Arrays.asList("empty", "size", "get:0", "get:1"), calls);
        assertEquals(2, list.size());
        assertArrayEquals(new Object[]{"a", "x", null, "b"}, list.items);
    }

    @Test
    void noGrowthTypedArrayFailureKeepsSuccessfulPrefixAndExistingSize()
    {
        String[] storage = {"a", "b", null, null, null, null};
        FastList<Object> list = new FastList<>(2, storage);
        ArrayList<Object> source = new ArrayList<>(Arrays.asList("x", Integer.valueOf(7)));
        assertThrows(ArrayStoreException.class, () -> list.addAll(1, source));
        assertSame(storage, list.items);
        assertEquals(2, list.size());
        assertArrayEquals(new Object[]{"a", "x", null, "b", null, null}, storage);
    }

    @Test
    void rangeValidationPrecedesSourceAccessAndEmptyInputDoesNotGrow()
    {
        FastList<String> list = FastList.newListWith("a", "b");
        ArrayList<String> empty = new ArrayList<>();
        Object[] storage = list.items;
        assertFalse(list.addAll(1, empty));
        assertSame(storage, list.items);
        assertThrows(IndexOutOfBoundsException.class, () -> list.addAll(-1, null));
        assertThrows(IndexOutOfBoundsException.class, () -> list.addAll(3, null));
        assertThrows(NullPointerException.class, () -> list.addAll(1, null));
        class RefusingSource extends ArrayList<String>
        {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isEmpty()
            {
                throw new AssertionError("invalid index accessed source");
            }
        }
        RefusingSource refusing = new RefusingSource();
        assertThrows(IndexOutOfBoundsException.class, () -> list.addAll(-1, refusing));
        assertThrows(IndexOutOfBoundsException.class, () -> list.addAll(3, refusing));
    }

    @Test
    void grownArrayListInsertPreservesIteratorCloneAndSerialization() throws Exception
    {
        FastList<String> list = FastList.newListWith("a", "b");
        Iterator<String> before = list.iterator();
        list.addAll(1, new ArrayList<>(Arrays.asList(null, "x")));
        List<String> seen = new ArrayList<>();
        before.forEachRemaining(seen::add);
        assertEquals(Arrays.asList("a", null, "x", "b"), seen);
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

    @Test
    void declaredArrayListConversionModeIsActuallyExercised() throws Exception
    {
        String expected = System.getProperty("m3.arraylist.direct");
        if (expected != null)
        {
            Field field = ArrayListIterate.class.getDeclaredField("ELEMENT_DATA_FIELD");
            field.setAccessible(true);
            assertEquals(Boolean.parseBoolean(expected), field.get(null) != null);
        }
        FastList<String> list = FastList.newListWith("a", "b");
        list.addAll(0, new ArrayList<>(Arrays.asList("x", "y")));
        assertEquals(Arrays.asList("x", "y", "a", "b"), list);
    }

    @Test
    void reflectiveReadFailureRetainsThePublishedGrowthAndShift() throws Exception
    {
        Field holder = ArrayListIterate.class.getDeclaredField("ELEMENT_DATA_FIELD");
        holder.setAccessible(true);
        Field elementData = (Field) holder.get(null);
        FastList<String> list = FastList.newListWith("a", "b", "c", "d");
        Object[] original = list.items;
        ArrayList<String> source = new ArrayList<>(Arrays.asList("x", "y"));
        if (elementData == null)
        {
            list.addAll(0, source);
            assertEquals(Arrays.asList("x", "y", "a", "b", "c", "d"), list);
            return;
        }
        // Inject the helper's documented reflective-read failure, without changing
        // either collection, and restore its access state for subsequent tests.
        elementData.setAccessible(false);
        try
        {
            RuntimeException thrown = assertThrows(RuntimeException.class, () -> list.addAll(0, source));
            assertEquals(IllegalAccessException.class, thrown.getCause().getClass());
            assertEquals(4, list.size());
            assertNotSame(original, list.items);
            assertArrayEquals(new Object[]{"a", "b", "a", "b", "c", "d", null}, list.items);
            assertArrayEquals(new Object[]{"a", "b", "c", "d"}, original);
        }
        finally
        {
            elementData.setAccessible(true);
        }
    }
}
