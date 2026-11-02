/*
 * Copyright (c) 2026 Hitesh Soliwal and Contributors to the Synexia Project.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.m3.collections.eclipse.impl.list.fixed;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

/** Array-only append candidate versus the unchanged public iterable factory. */
public class M3ArrayAdapterAppendContractTest
{
    @Test
    public void appendMatchesGeneralFactoryAndDoesNotAlias()
    {
        for (int size = 0; size < 128; size++)
        {
            Object[] storage = new Object[size];
            for (int i = 0; i < size; i++)
            {
                storage[i] = i % 7 == 0 ? null : new Object();
            }
            ArrayAdapter<Object> original = ArrayAdapter.adapt(storage);
            Object added = new Object();
            ArrayAdapter<Object> expected = ArrayAdapter.newArrayWithItem(original, added);
            ArrayAdapter<Object> actual = original.with(added);

            assertNotSame(original, actual);
            assertArrayEquals(expected.toArray(), actual.toArray());
            assertEquals(size, original.size());
            assertEquals(size + 1, actual.size());
            assertSame(added, actual.get(size));
            if (size > 0)
            {
                Object before = actual.get(0);
                storage[0] = new Object();
                assertSame(before, actual.get(0));
            }
        }
    }

    @Test
    public void appendedNullAndChainedWithAreIndependent()
    {
        ArrayAdapter<Object> original = ArrayAdapter.newArray();
        ArrayAdapter<Object> first = original.with(null);
        Object value = new Object();
        ArrayAdapter<Object> second = first.with(value);
        assertEquals(0, original.size());
        assertArrayEquals(new Object[]{null}, first.toArray());
        assertArrayEquals(new Object[]{null, value}, second.toArray());
    }
}
