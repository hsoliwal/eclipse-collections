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
        ObservableValue stored = null;
        for (int i = 0; i < 256; i++)
        {
            ObservableValue value = new ObservableValue(i);
            if (i == 75)
            {
                stored = value;
            }
            map.put(i, value);
        }
        ArrayList<Object> queries = new ArrayList<>();
        queries.add(new ObservableValue(75));
        for (int i = 0; i < 127; i++)
        {
            queries.add(new ObservableValue(i));
        }
        // The original path, Iterate.allSatisfy(collection, Predicates.in(values)), copies the
        // values into a UnifiedSet (Predicates.in, more than 6 elements), whose lookup calls the
        // stored element's equals (UnifiedSet.nonNullTableObjectEquals), not the query's.
        int before = stored.equalsCalls;
        assertTrue(map.values().containsAll(queries));
        assertTrue(stored.equalsCalls > before, "stored value equality callbacks must execute");
    }

    private static final class SubclassMap<K, V> extends UnifiedMap<K, V>
    {
        private static final long serialVersionUID = 1L;
    }

    @Test
    public void subclassLookupHooksStayOnOriginalPath()
    {
        // Subclasses take the original path. It never calls containsValue: Predicates.in copies
        // the values into a set (more than 6 elements), so only membership is observable here.
        SubclassMap<Integer, String> map = new SubclassMap<>();
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
        queries.add("missing");
        assertFalse(map.values().containsAll(queries));
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

    @Test
    public void missingEarlyQueriesRetainOriginalShortCircuitSemantics()
    {
        UnifiedMap<Integer, String> map = new UnifiedMap<>();
        for (int i = 0; i < 1024; i++)
        {
            map.put(i, value(i));
        }
        ArrayList<String> queries = new ArrayList<>();
        for (int i = 0; i < 512; i++)
        {
            queries.add(value(i));
        }
        assertTrue(map.values().containsAll(queries));
        queries.set(0, "early-unavailable");
        assertFalse(map.values().containsAll(queries));
        queries.set(0, value(0));
        queries.set(15, "late-prefix-unavailable");
        assertFalse(map.values().containsAll(queries));
        queries.set(15, value(15));
        assertTrue(map.values().containsAll(queries));
        assertEquals(1024, map.size());
    }
}
