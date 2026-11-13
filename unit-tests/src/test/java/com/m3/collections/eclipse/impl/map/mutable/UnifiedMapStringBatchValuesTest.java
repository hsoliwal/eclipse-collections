/*
 * Copyright (c) 2026 Synexia and Contributors.
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 and Eclipse Distribution License v1.0.
 */
package com.m3.collections.eclipse.impl.map.mutable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The optimized exact-String value batch must preserve the generic values-view contract. */
public class UnifiedMapStringBatchValuesTest
{
    private static String value(int index)
    {
        return "string-value-" + index;
    }

    @Test
    public void largeExactStringBatchesPreserveMembershipAndMapPayload()
    {
        UnifiedMap<Integer, String> map = new UnifiedMap<>();
        for (int i = 0; i < 4096; i++)
        {
            map.put(i, new String(value(i)));
        }
        ArrayList<String> queries = new ArrayList<>();
        for (int i = 0; i < 4096; i++)
        {
            queries.add(new String(value(4095 - i)));
        }
        assertTrue(map.values().containsAll(queries));
        queries.add("string-value-absent");
        assertFalse(map.values().containsAll(queries));
        assertEquals(4096, map.size());
        for (int i = 0; i < 4096; i++)
        {
            assertEquals(value(i), map.get(i));
        }
    }

    @Test
    public void duplicateStringValuesAndQueriesHaveSetMembershipSemantics()
    {
        UnifiedMap<Integer, String> map = new UnifiedMap<>();
        for (int i = 0; i < 1024; i++)
        {
            map.put(i, value(i % 211));
        }
        ArrayList<String> queries = new ArrayList<>();
        for (int i = 0; i < 1024; i++)
        {
            queries.add(value(i % 211));
        }
        assertTrue(map.values().containsAll(queries));
        queries.set(800, "missing");
        assertFalse(map.values().containsAll(queries));
    }

    @Test
    public void nonStringAndNullValuesRetainOriginalContainsBehavior()
    {
        UnifiedMap<Integer, Object> map = new UnifiedMap<>();
        for (int i = 0; i < 256; i++)
        {
            map.put(i, value(i));
        }
        map.put(256, null);
        ArrayList<Object> queries = new ArrayList<>();
        for (int i = 0; i < 128; i++)
        {
            queries.add(value(i));
        }
        queries.add(null);
        assertTrue(map.values().containsAll(queries));
        queries.add("missing");
        assertFalse(map.values().containsAll(queries));
    }

    private static final class ObservableValue
    {
        private final int value;
        private int equalsCalls;

        private ObservableValue(int value)
        {
            this.value = value;
        }

        @Override
        public boolean equals(Object other)
        {
            this.equalsCalls++;
            return other instanceof ObservableValue && this.value == ((ObservableValue) other).value;
        }

        @Override
        public int hashCode()
        {
            return this.value;
        }
    }

    @Test
    public void arbitraryValueEqualityCallbacksStayOnOriginalPath()
    {
        UnifiedMap<Integer, Object> map = new UnifiedMap<>();
        for (int i = 0; i < 256; i++)
        {
            map.put(i, new ObservableValue(i));
        }
        ArrayList<Object> queries = new ArrayList<>();
        ObservableValue marker = new ObservableValue(75);
        queries.add(marker);
        for (int i = 0; i < 127; i++)
        {
            queries.add(new ObservableValue(i));
        }
        assertTrue(map.values().containsAll(queries));
        assertTrue(marker.equalsCalls > 0, "original value equality callbacks must execute");
    }

    private static final class InstrumentedMap<K, V> extends UnifiedMap<K, V>
    {
        private int containsCalls;

        @Override
        public boolean containsValue(Object value)
        {
            this.containsCalls++;
            return super.containsValue(value);
        }
    }

    @Test
    public void subclassLookupHooksStayOnOriginalPath()
    {
        InstrumentedMap<Integer, String> map = new InstrumentedMap<>();
        for (int i = 0; i < 256; i++)
        {
            map.put(i, value(i));
        }
        ArrayList<String> queries = new ArrayList<>();
        for (int i = 0; i < 128; i++)
        {
            queries.add(value(i));
        }
        assertTrue(map.values().containsAll(queries));
        assertTrue(map.containsCalls > 0, "subclass containsValue must remain observable");
    }

    @Test
    public void nonExactQueryListAndSmallerBatchRetainMembership()
    {
        UnifiedMap<Integer, String> map = new UnifiedMap<>();
        for (int i = 0; i < 256; i++)
        {
            map.put(i, value(i));
        }
        Collection<String> listSubclass = new ArrayList<String>()
        {
            private static final long serialVersionUID = 1L;
        };
        for (int i = 0; i < 128; i++)
        {
            listSubclass.add(value(i));
        }
        assertTrue(map.values().containsAll(listSubclass));
        assertTrue(map.values().containsAll(List.of(value(0), value(100))));
        assertFalse(map.values().containsAll(List.of("absent")));
    }
}
