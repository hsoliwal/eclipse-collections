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
 *
 * Contributors:
 *     Hitesh Soliwal (Synexia) - initial API and implementation
 *******************************************************************************/
package com.m3.collections.eclipse.impl.map.sorted.mutable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import org.junit.jupiter.api.Test;

final class IndexedSortedMapViewProtocolTest
{
    @Test void valuesRemovalPreservesEqualityReceiver()
    {
        compareViews(0);
    }

    @Test void entryOperationsPreserveLookupAndGetterOrder()
    {
        compareViews(1);
        compareViews(2);
    }

    private static void compareViews(int operation)
    {
        for (int view = 0; view < 4; view++)
        {
            for (int queryGroup : new int[]{20, 99})
            {
                List<Object> expected = observe(0, view, operation, queryGroup);
                for (int backend = 1; backend <= 2; backend++)
                {
                    List<Object> actual = observe(backend, view, operation, queryGroup);
                    if (!expected.equals(actual))
                    {
                        throw new AssertionError("backend=" + backend + ", view=" + view
                                + ", operation=" + operation + ", query=" + queryGroup
                                + ", expected=" + expected + ", actual=" + actual);
                    }
                }
            }
        }
    }

    private static List<Object> observe(int backend, int view, int operation, int queryGroup)
    {
        List<String> trace = new ArrayList<>();
        Comparator<Integer> order = (left, right) ->
        {
            trace.add("compare:" + left + ":" + right);
            return Integer.compare(left, right);
        };
        SortedMap<Integer, Value> root;
        if (backend == 0) { root = new TreeMap<>(order); }
        else if (backend == 1) { root = new IndexedSortedMap<>(order); }
        else { root = new IndexedRedBlackMap<>(order); }
        for (int key : new int[]{10, 20, 30})
        {
            root.put(key, new Value(key, "stored" + key, trace));
        }
        SortedMap<Integer, Value> selected = switch (view)
        {
            case 1 -> root.headMap(25);
            case 2 -> root.tailMap(15);
            case 3 -> root.subMap(15, 35);
            default -> root;
        };
        Value query = new Value(queryGroup, "query" + queryGroup, trace);
        Map.Entry<Integer, Value> entry = new ProbeEntry(query, trace);
        trace.clear();
        boolean removedOrPresent = switch (operation)
        {
            case 1 -> selected.entrySet().contains(entry);
            case 2 -> selected.entrySet().remove(entry);
            default -> selected.values().remove(query);
        };
        List<String> calls = List.copyOf(trace);
        List<String> state = new ArrayList<>();
        for (Map.Entry<Integer, Value> stored : root.entrySet())
        {
            state.add(stored.getKey() + "=" + stored.getValue().name);
        }
        return List.of(removedOrPresent, calls, state);
    }

    private static final class Value
    {
        private final int group;
        private final String name;
        private final List<String> trace;
        Value(int group, String name, List<String> trace)
        {
            this.group = group;
            this.name = name;
            this.trace = trace;
        }
        @Override public boolean equals(Object other)
        {
            this.trace.add("equals:" + this.name + ":"
                    + (other instanceof Value value ? value.name : String.valueOf(other)));
            return other instanceof Value value && this.group == value.group;
        }
        @Override public int hashCode() { return this.group; }
    }

    private static final class ProbeEntry implements Map.Entry<Integer, Value>
    {
        private final Value value;
        private final List<String> trace;
        ProbeEntry(Value value, List<String> trace) { this.value = value; this.trace = trace; }
        @Override public Integer getKey() { this.trace.add("entry.key"); return 20; }
        @Override public Value getValue() { this.trace.add("entry.value"); return this.value; }
        @Override public Value setValue(Value next) { throw new UnsupportedOperationException(); }
        @Override public boolean equals(Object other)
        {
            return other instanceof Map.Entry<?, ?> entry
                    && Objects.equals(this.getKey(), entry.getKey())
                    && Objects.equals(this.getValue(), entry.getValue());
        }
        @Override public int hashCode()
        {
            return Objects.hashCode(this.getKey()) ^ Objects.hashCode(this.getValue());
        }
    }
}
