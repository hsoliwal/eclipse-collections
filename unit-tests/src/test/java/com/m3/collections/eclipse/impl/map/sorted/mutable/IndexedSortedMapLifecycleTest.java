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
// Regression expectations come from the sealed JDK/current-EC public originals.
package com.m3.collections.eclipse.impl.map.sorted.mutable;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import org.junit.jupiter.api.Test;

final class IndexedSortedMapLifecycleTest
{
    @Test void independentEntryIteratorsRetainOneLiveIdentity()
    {
        IndexedSortedMap<Integer, String> map = new IndexedSortedMap<>();
        map.put(10, "old");
        Map.Entry<Integer, String> first = map.entrySet().iterator().next();
        Map.Entry<Integer, String> second = map.entrySet().iterator().next();
        check(first == second, "independent iterators must return the same live entry");
        equal("old", first.setValue("updated"));
        equal("updated", second.getValue());
        equal("updated", map.get(10));
    }

    @Test void twoChildIteratorRemovalCopiesPayloadWithoutChangingEntryIdentity()
    {
        Fixture fixture = new Fixture(new int[]{20, 10, 30}, 2, 3);
        fixture.map.putAll(fixture.input);
        Map.Entry<Integer, String> target = fixture.saved;
        fixture.held.remove();
        equal(30, target.getKey());
        equal("old30", target.getValue());
        check(fixture.held.next() == target, "successor copy must retain the target entry object");
        equal(2, fixture.map.size());
        equal(List.of(100, 200, 300), new ArrayList<>(fixture.map.keySet()));
    }

    @Test void physicalViewsRemainReachableWhenOriginalLogicalCountBecomesZero()
    {
        Fixture fixture = new Fixture(new int[]{10, 20}, 1, 1);
        fixture.map.putAll(fixture.input);
        fixture.held.remove();
        equal(0, fixture.map.size());
        check(fixture.map.isEmpty(), "root reports the original logical count");
        equal(0, fixture.map.entrySet().size());
        check(fixture.map.entrySet().isEmpty(), "entry view size follows the root count");
        List<Integer> physicalKeys = new ArrayList<>();
        for (Integer key : fixture.map.keySet()) { physicalKeys.add(key); }
        equal(List.of(20), physicalKeys);
        equal(20, fixture.map.firstKey());
        equal(20, fixture.map.lastKey());
        SortedMap<Integer, String> range = fixture.map.tailMap(15);
        equal(1, range.size());
        check(!range.isEmpty(), "bounded views count the reachable physical entry");
        equal(List.of("old20"), new ArrayList<>(range.values()));
    }

    @Test void detachedEntryCannotReachAReusedSlot()
    {
        IndexedSortedMap<Integer, String> map = new IndexedSortedMap<>();
        map.put(10, null);
        Map.Entry<Integer, String> saved = map.entrySet().iterator().next();
        map.remove(10);
        map.put(20, "replacement");
        equal(null, saved.setValue("detached"));
        equal(10, saved.getKey());
        equal("replacement", map.get(20));
    }

    private static void check(boolean condition, String message)
    { if (!condition) { throw new AssertionError(message); } }
    private static void equal(Object expected, Object actual)
    { check(Objects.equals(expected, actual), "expected=" + expected + ", actual=" + actual); }

    private static final class Fixture
    {
        private final Comparator<Integer> order = Integer::compare;
        private final IndexedSortedMap<Integer, String> map = new IndexedSortedMap<>(this.order);
        private Iterator<Map.Entry<Integer, String>> held;
        private Map.Entry<Integer, String> saved;
        private final SortedMap<Integer, String> input;

        Fixture(int[] oldKeys, int readCount, int incomingCount)
        {
            List<Map.Entry<Integer, String>> entries = new ArrayList<>();
            for (int n = 1; n <= incomingCount; n++)
            { entries.add(new AbstractMap.SimpleImmutableEntry<>(n * 100, "new" + n * 100)); }
            this.input = new SortedInput<>(this.order, entries, () ->
            {
                for (int key : oldKeys) { this.map.put(key, "old" + key); }
            }, () ->
            {
                this.held = this.map.entrySet().iterator();
                for (int n = 0; n < readCount; n++) { this.saved = this.held.next(); }
            });
        }
    }

    private static final class SortedInput<K, V> extends AbstractMap<K, V> implements SortedMap<K, V>
    {
        private final Comparator<? super K> order;
        private final List<Map.Entry<K, V>> entries;
        private final Runnable onEntries;
        private final Runnable onIterator;
        SortedInput(Comparator<? super K> order, List<Map.Entry<K, V>> entries,
                Runnable onEntries, Runnable onIterator)
        { this.order = order; this.entries = entries; this.onEntries = onEntries; this.onIterator = onIterator; }
        @Override public int size() { return this.entries.size(); }
        @Override public Comparator<? super K> comparator() { return this.order; }
        @Override public Set<Map.Entry<K, V>> entrySet()
        {
            this.onEntries.run();
            return new AbstractSet<>()
            {
                @Override public int size() { return entries.size(); }
                @Override public Iterator<Map.Entry<K, V>> iterator()
                { onIterator.run(); return entries.iterator(); }
            };
        }
        @Override public K firstKey() { throw new UnsupportedOperationException(); }
        @Override public K lastKey() { throw new UnsupportedOperationException(); }
        @Override public SortedMap<K, V> headMap(K key) { throw new UnsupportedOperationException(); }
        @Override public SortedMap<K, V> tailMap(K key) { throw new UnsupportedOperationException(); }
        @Override public SortedMap<K, V> subMap(K low, K high) { throw new UnsupportedOperationException(); }
    }
}
