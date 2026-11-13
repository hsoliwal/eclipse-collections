/*
 * Copyright (c) 2026 Synexia and Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 */
package com.m3.collections.eclipse.impl.list.mutable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Validates zero-sized array reuse and the two historical FastList growth sentinels. */
public final class FastListEmptyTrimArrayTest
{
    @Test
    public void noOpTrimPreservesDefaultGrowthToTen()
    {
        FastList<Integer> list = new FastList<>();
        list.trimToSize();
        assertEquals(0, list.size());
        list.add(17);
        assertEquals(10, list.items.length);
        assertEquals(Integer.valueOf(17), list.get(0));
    }

    @Test
    public void explicitZeroCapacityStillGrowsToOne()
    {
        FastList<Integer> list = new FastList<>(0);
        list.trimToSize();
        list.add(17);
        assertEquals(1, list.items.length);
        assertEquals(Integer.valueOf(17), list.get(0));
    }

    @Test
    public void emptyTrimReusesStaticZeroArrayAcrossCycles()
    {
        FastList<Integer> list = new FastList<>(1024);
        list.add(11);
        list.clear();
        list.trimToSize();
        assertEquals(0, list.size());
        assertEquals(0, list.items.length);
        Object[] zero = list.items;

        for (int cycle = 0; cycle < 256; cycle++)
        {
            list.add(cycle);
            assertEquals(1, list.items.length);
            assertEquals(Integer.valueOf(cycle), list.get(0));
            list.clear();
            list.trimToSize();
            assertSame(zero, list.items);
            assertEquals(0, list.size());
            assertTrue(list.isEmpty());
        }
        list.add(42);
        assertEquals(Integer.valueOf(42), list.get(0));
    }

    private static final class InstrumentedList extends FastList<Integer>
    {
        private InstrumentedList()
        {
            super(128);
        }
    }

    @Test
    public void subclassesRetainOriginalAllocationPath()
    {
        InstrumentedList list = new InstrumentedList();
        list.trimToSize();
        Object[] previous = list.items;
        assertEquals(0, previous.length);
        list.ensureCapacity(128);
        list.trimToSize();
        assertEquals(0, list.items.length);
        assertNotSame(previous, list.items, "subclass retains the original fresh-array semantics");
        list.add(7);
        assertEquals(1, list.size());
        assertEquals(Integer.valueOf(7), list.get(0));
    }
}
