/*
 * Copyright (c) 2026 Hitesh Soliwal and Contributors to the Synexia Project.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.m3.collections.eclipse.impl.list.fixed;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

/** Compares the exact-adapter factory fast path against its untouched Iterable path. */
public class M3ArrayAdapterFactoryContractTest
{
    @Test
    public void exactAdapterMatchesGeneralIterableAndPreservesOriginal()
    {
        Random random = new Random(0x51A1L);
        for (int size = 0; size < 256; size++)
        {
            Object[] payload = new Object[size];
            for (int i = 0; i < size; i++)
            {
                payload[i] = random.nextInt(5) == 0 ? null : new Object();
            }
            List<Object> general = new ArrayList<>(Arrays.asList(payload));
            Object appended = size % 3 == 0 ? null : new Object();
            ArrayAdapter<Object> expected = ArrayAdapter.newArrayWithItem(general, appended);
            ArrayAdapter<Object> source = ArrayAdapter.adapt(payload);
            ArrayAdapter<Object> actual = ArrayAdapter.newArrayWithItem(source, appended);

            assertNotSame(source, actual);
            assertArrayEquals(expected.toArray(), actual.toArray());
            assertEquals(size, source.size());
            assertEquals(size + 1, actual.size());
            assertSame(appended, actual.get(size));
            if (size > 0)
            {
                Object original = actual.get(0);
                payload[0] = new Object();
                assertSame(original, actual.get(0));
            }
        }
    }

    @Test
    public void retainedResultIsIndependentAcrossSubsequentFactoryCalls()
    {
        Object marker = new Object();
        ArrayAdapter<Object> original = ArrayAdapter.newArray();
        ArrayAdapter<Object> first = ArrayAdapter.newArrayWithItem(original, null);
        ArrayAdapter<Object> second = ArrayAdapter.newArrayWithItem(first, marker);
        assertArrayEquals(new Object[0], original.toArray());
        assertArrayEquals(new Object[]{null}, first.toArray());
        assertArrayEquals(new Object[]{null, marker}, second.toArray());
    }
}
