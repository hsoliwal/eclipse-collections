/*
 * Copyright (c) 2026 Synexia and Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 */
package com.m3.collections.eclipse.impl.map.mutable;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Regression for the operation-local String[] membership index in values().removeAll/retainAll. */
public final class UnifiedMapBulkStringMutationTest
{
    private static String value(int index)
    {
        return "value-" + index;
    }

    @Test
    public void removeAllUsesExistingStringArrayStorage()
    {
        UnifiedMap<Integer, String> map = new UnifiedMap<>();
        ArrayList<String> queries = new ArrayList<>();
        for (int i = 0; i < 1024; i++)
        {
            map.put(i, value(i));
            if ((i & 1) == 0)
            {
                queries.add(value(i));
            }
        }
        assertTrue(map.values().removeAll(queries));
        assertEquals(512, map.size());
        for (int i = 0; i < 1024; i++)
        {
            if ((i & 1) == 0)
            {
                assertFalse(map.containsKey(i));
                assertNull(map.get(i));
            }
            else
            {
                assertEquals(value(i), map.get(i));
            }
        }
        assertFalse(map.values().removeAll(queries));
    }

    @Test
    public void retainAllUsesExistingStringArrayStorage()
    {
        UnifiedMap<Integer, String> map = new UnifiedMap<>();
        ArrayList<String> queries = new ArrayList<>();
        for (int i = 0; i < 1024; i++)
        {
            map.put(i, value(i));
            if ((i & 1) == 0)
            {
                queries.add(value(i));
            }
        }
        assertTrue(map.values().retainAll(queries));
        assertEquals(512, map.size());
        for (int i = 0; i < 1024; i++)
        {
            if ((i & 1) == 0)
            {
                assertEquals(value(i), map.get(i));
            }
            else
            {
                assertFalse(map.containsKey(i));
                assertNull(map.get(i));
            }
        }
        assertFalse(map.values().retainAll(queries));
    }

    private static final class CollisionKey
    {
        private final int value;
        CollisionKey(int value)
        {
            this.value = value;
        }

        @Override
        public int hashCode()
        {
            return 1;
        }

        @Override
        public boolean equals(Object other)
        {
            return other instanceof CollisionKey key && this.value == key.value;
        }
    }

    @Test
    public void chainedSlotsReleaseOnlyMatchingValues()
    {
        UnifiedMap<CollisionKey, String> map = new UnifiedMap<>();
        ArrayList<String> queries = new ArrayList<>();
        for (int i = 0; i < 256; i++)
        {
            map.put(new CollisionKey(i), value(i));
            if ((i & 1) == 0)
            {
                queries.add(value(i));
            }
        }
        assertTrue(map.values().removeAll(queries));
        assertEquals(128, map.size());
        for (int i = 0; i < 256; i++)
        {
            if ((i & 1) == 0)
            {
                assertFalse(map.containsKey(new CollisionKey(i)));
            }
            else
            {
                assertEquals(value(i), map.get(new CollisionKey(i)));
            }
        }
    }

    @Test
    public void nullAndMixedValuesKeepTheOriginalGenericPath()
    {
        UnifiedMap<Integer, Object> map = new UnifiedMap<>();
        ArrayList<Object> queries = new ArrayList<>();
        for (int i = 0; i < 256; i++)
        {
            Object value = i == 130 ? null : (i == 131 ? Integer.valueOf(i) : value(i));
            map.put(i, value);
            if ((i & 1) == 0)
            {
                queries.add(value);
            }
        }
        assertTrue(map.values().removeAll(queries));
        assertEquals(128, map.size());
        assertFalse(map.containsKey(130));
        assertEquals(Integer.valueOf(131), map.get(131));
        assertEquals(value(133), map.get(133));
    }

    private static final class CountedContains extends ArrayList<Object>
    {
        private static final long serialVersionUID = 1L;
        private int calls;

        @Override
        public boolean contains(Object value)
        {
            this.calls++;
            return super.contains(value);
        }
    }

    @Test
    public void collectionSubclassRetainsContainsCallbacks()
    {
        UnifiedMap<Integer, String> map = new UnifiedMap<>();
        for (int i = 0; i < 256; i++)
        {
            map.put(i, value(i));
        }
        CountedContains probes = new CountedContains();
        for (int i = 0; i < 128; i++)
        {
            probes.add(value(i));
        }
        assertTrue(map.values().retainAll(probes));
        assertTrue(probes.calls > 0, "generic collection.contains callback must remain visible");
        assertEquals(128, map.size());
    }
}
