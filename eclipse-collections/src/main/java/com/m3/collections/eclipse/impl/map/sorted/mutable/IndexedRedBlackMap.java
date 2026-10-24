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

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

/** Experimental composition boundary; it is not a production admission. */
final class IndexedRedBlackMap<K, V> extends AbstractMap<K, V> implements SortedMap<K, V> {
    private static final int LOWER = 1;
    private static final int UPPER = 2;
    private static final int ENTRY = 0;
    private static final int KEY = 1;
    private static final int VALUE = 2;
    private static final Object UNBOUNDED = new Object();

    final IndexedRedBlackTree<K, V> tree;
    private final int boundsMask;
    private final K lower;
    private final K upper;
    private Set<Entry<K, V>> entries;
    private Set<K> keys;
    private Collection<V> values;

    IndexedRedBlackMap(Comparator<? super K> comparator) {
        tree = new IndexedRedBlackTree<>(comparator);
        boundsMask = 0;
        lower = null;
        upper = null;
    }

    private IndexedRedBlackMap(IndexedRedBlackTree<K, V> tree, int mask, K lower, K upper) {
        this.tree = tree;
        if (mask == (LOWER | UPPER)) {
            if (compare(lower, upper) > 0) {
                throw new IllegalArgumentException("fromKey > toKey");
            }
        } else if ((mask & LOWER) != 0) {
            compare(lower, lower);
        } else if ((mask & UPPER) != 0) {
            compare(upper, upper);
        }
        boundsMask = mask;
        this.lower = lower;
        this.upper = upper;
    }

    @Override
    public Comparator<? super K> comparator() {
        return tree.comparator;
    }

    @SuppressWarnings("unchecked")
    private int compare(Object first, Object second) {
        Comparator<? super K> order = tree.comparator;
        return order == null ? ((Comparable<? super K>) first).compareTo((K) second)
                : order.compare((K) first, (K) second);
    }

    private boolean inBounds(Object key) {
        return ((boundsMask & LOWER) == 0 || compare(key, lower) >= 0)
                && ((boundsMask & UPPER) == 0 || compare(key, upper) < 0);
    }

    private boolean endpointInBounds(Object key, boolean upperEndpoint) {
        if ((boundsMask & LOWER) != 0 && compare(key, lower) < 0) {
            return false;
        }
        if ((boundsMask & UPPER) != 0) {
            int order = compare(key, upper);
            if (order > 0 || order == 0 && !upperEndpoint) {
                return false;
            }
        }
        return true;
    }

    private int findInBounds(Object key) {
        return inBounds(key) ? tree.find(key) : -1;
    }

    @Override
    public int size() {
        if (boundsMask == 0) {
            return tree.size;
        }
        int count = 0;
        Iterator<K> cursor = new SlotIterator<>(KEY);
        while (cursor.hasNext()) {
            cursor.next();
            count++;
        }
        return count;
    }

    @Override
    public boolean isEmpty() {
        if (boundsMask == 0) {
            return tree.size == 0;
        }
        int slot = lowest(tree.arena, tree.root);
        return slot < 0 || (boundsMask & UPPER) != 0
                && compare(tree.arena.keys[slot], upper) >= 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        int slot = findInBounds(key);
        return slot < 0 ? null : (V) tree.arena.values[slot];
    }

    @Override
    public boolean containsKey(Object key) {
        return findInBounds(key) >= 0;
    }

    @Override
    public V put(K key, V value) {
        if (!inBounds(key)) {
            throw new IllegalArgumentException("key out of range");
        }
        return tree.put(key, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public V remove(Object key) {
        int slot = findInBounds(key);
        if (slot < 0) {
            return null;
        }
        IndexedTreeArena<K, V> selected = tree.arena;
        V previous = (V) selected.values[slot];
        tree.erase(selected, slot);
        return previous;
    }

    @Override
    public void clear() {
        if (boundsMask == 0) {
            tree.arena = new IndexedTreeArena<>();
            tree.root = -1;
            tree.size = 0;
            tree.epoch++;
        } else {
            Iterator<K> cursor = new SlotIterator<>(KEY);
            while (cursor.hasNext()) {
                cursor.next();
                cursor.remove();
            }
        }
    }

    /** Ceiling uses the query-to-node direction and no Entry projection. */
    private int ceiling(IndexedTreeArena<K, V> selected, int root, Object key) {
        int slot = root;
        int candidate = -1;
        while (slot >= 0) {
            int order = compare(key, selected.keys[slot]);
            if (order == 0) {
                return slot;
            }
            if (order < 0) {
                candidate = slot;
                slot = selected.left[slot];
            } else {
                slot = selected.right[slot];
            }
        }
        return candidate;
    }

    private int strictlyLower(IndexedTreeArena<K, V> selected, int root, Object key) {
        int slot = root;
        int candidate = -1;
        while (slot >= 0) {
            if (compare(key, selected.keys[slot]) > 0) {
                candidate = slot;
                slot = selected.right[slot];
            } else {
                slot = selected.left[slot];
            }
        }
        return candidate;
    }

    private int lowest(IndexedTreeArena<K, V> selected, int root) {
        int slot = (boundsMask & LOWER) == 0
                ? IndexedRedBlackTree.first(selected, root) : ceiling(selected, root, lower);
        return slot >= 0 && (boundsMask & UPPER) != 0
                && compare(selected.keys[slot], upper) >= 0 ? -1 : slot;
    }

    private int highest(IndexedTreeArena<K, V> selected, int root) {
        int slot;
        if ((boundsMask & UPPER) != 0) {
            slot = strictlyLower(selected, root, upper);
        } else {
            slot = root;
            while (slot >= 0 && selected.right[slot] >= 0) {
                slot = selected.right[slot];
            }
        }
        return slot >= 0 && (boundsMask & LOWER) != 0
                && compare(selected.keys[slot], lower) < 0 ? -1 : slot;
    }

    @Override
    @SuppressWarnings("unchecked")
    public K firstKey() {
        IndexedTreeArena<K, V> selected = tree.arena;
        int slot = lowest(selected, tree.root);
        if (slot < 0) {
            throw new NoSuchElementException();
        }
        return (K) selected.keys[slot];
    }

    @Override
    @SuppressWarnings("unchecked")
    public K lastKey() {
        IndexedTreeArena<K, V> selected = tree.arena;
        int slot = highest(selected, tree.root);
        if (slot < 0) {
            throw new NoSuchElementException();
        }
        return (K) selected.keys[slot];
    }

    @Override
    public SortedMap<K, V> headMap(K toKey) {
        if (!endpointInBounds(toKey, true)) {
            throw new IllegalArgumentException("toKey out of range");
        }
        return new IndexedRedBlackMap<>(tree, boundsMask | UPPER, lower, toKey);
    }

    @Override
    public SortedMap<K, V> tailMap(K fromKey) {
        if (!endpointInBounds(fromKey, false)) {
            throw new IllegalArgumentException("fromKey out of range");
        }
        return new IndexedRedBlackMap<>(tree, boundsMask | LOWER, fromKey, upper);
    }

    @Override
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        if (!endpointInBounds(fromKey, false)) {
            throw new IllegalArgumentException("fromKey out of range");
        }
        if (!endpointInBounds(toKey, true)) {
            throw new IllegalArgumentException("toKey out of range");
        }
        return new IndexedRedBlackMap<>(tree, LOWER | UPPER, fromKey, toKey);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> source) {
        int count = source.size();
        if (boundsMask == 0 && tree.size == 0 && count != 0
                && source instanceof SortedMap<?, ?> sorted) {
            Comparator<?> sourceOrder = sorted.comparator();
            Comparator<? super K> targetOrder = comparator();
            if (sourceOrder == targetOrder || targetOrder != null && targetOrder.equals(sourceOrder)) {
                tree.epoch++;
                Iterator<? extends Entry<? extends K, ? extends V>> cursor = source.entrySet().iterator();
                tree.size = count;
                IndexedRedBlackTree<K, V> built = IndexedRedBlackTree.build(targetOrder, count, cursor);
                // Source callbacks may already have changed logical size or epoch.
                tree.arena = built.arena;
                tree.root = built.root;
                return;
            }
        }
        super.putAll(source);
    }

    private static boolean equalValue(Object first, Object second) {
        return first == null ? second == null : first.equals(second);
    }

    @Override
    public boolean containsValue(Object query) {
        Iterator<V> cursor = new SlotIterator<>(VALUE);
        while (cursor.hasNext()) {
            if (equalValue(query, cursor.next())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<K> keySet() {
        if (keys == null) {
            keys = new AbstractSet<>() {
                @Override public int size() { return IndexedRedBlackMap.this.size(); }
                @Override public boolean isEmpty() { return IndexedRedBlackMap.this.isEmpty(); }
                @Override public void clear() { IndexedRedBlackMap.this.clear(); }
                @Override public boolean contains(Object key) { return containsKey(key); }
                @Override public Iterator<K> iterator() { return new SlotIterator<>(KEY); }
                @Override public boolean remove(Object key) {
                    int slot = findInBounds(key);
                    if (slot < 0) { return false; }
                    tree.erase(tree.arena, slot);
                    return true;
                }
            };
        }
        return keys;
    }

    @Override
    public Collection<V> values() {
        if (values == null) {
            values = new AbstractCollection<>() {
                @Override public int size() { return IndexedRedBlackMap.this.size(); }
                @Override public boolean isEmpty() { return IndexedRedBlackMap.this.isEmpty(); }
                @Override public void clear() { IndexedRedBlackMap.this.clear(); }
                @Override public boolean contains(Object query) { return containsValue(query); }
                @Override public Iterator<V> iterator() { return new SlotIterator<>(VALUE); }
                @Override public boolean remove(Object query) {
                    Iterator<V> cursor = iterator();
                    while (cursor.hasNext()) {
                        V stored = cursor.next();
                        // TreeMap's root Values uses stored-to-query equality;
                        // bounded AbstractCollection views use query-to-stored.
                        if (boundsMask == 0 ? equalValue(stored, query) : equalValue(query, stored)) {
                            cursor.remove();
                            return true;
                        }
                    }
                    return false;
                }
            };
        }
        return values;
    }

    private boolean matchEntry(Object candidate, boolean remove) {
        if (!(candidate instanceof Entry<?, ?> query)) { return false; }
        // Root entry sets read value first; bounded sets search before reading it.
        Object queryValue = boundsMask == 0 ? query.getValue() : null;
        Object key = query.getKey();
        int slot = findInBounds(key);
        if (slot < 0) { return false; }
        IndexedTreeArena<K, V> selected = tree.arena;
        Object stored = selected.values[slot];
        if (boundsMask != 0) { queryValue = query.getValue(); }
        if (!equalValue(stored, queryValue)) { return false; }
        if (remove) { tree.erase(selected, slot); }
        return true;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        if (entries == null) {
            entries = new AbstractSet<>() {
                @Override public int size() { return IndexedRedBlackMap.this.size(); }
                @Override public boolean isEmpty() { return IndexedRedBlackMap.this.isEmpty(); }
                @Override public void clear() { IndexedRedBlackMap.this.clear(); }
                @Override public Iterator<Entry<K, V>> iterator() { return new SlotIterator<>(ENTRY); }
                @Override public boolean contains(Object candidate) {
                    return matchEntry(candidate, false);
                }
                @Override public boolean remove(Object candidate) {
                    return matchEntry(candidate, true);
                }
            };
        }
        return entries;
    }

    private final class SlotIterator<T> implements Iterator<T> {
        private final IndexedTreeArena<K, V> selected;
        private final int kind;
        private final Object fenceKey;
        private int next;
        private int returned = -1;
        private int expected;

        SlotIterator(int kind) {
            this.kind = kind;
            selected = tree.arena;
            next = lowest(selected, tree.root);
            if ((boundsMask & UPPER) == 0) {
                fenceKey = UNBOUNDED;
            } else {
                // Like JDK submap construction, resolve the fence after the start.
                IndexedTreeArena<K, V> fenceArena = tree.arena;
                int fence = ceiling(fenceArena, tree.root, upper);
                fenceKey = fence < 0 ? UNBOUNDED : fenceArena.keys[fence];
            }
            expected = tree.epoch;
        }

        @Override
        public boolean hasNext() {
            return next >= 0 && selected.keys[next] != fenceKey;
        }

        @Override
        @SuppressWarnings("unchecked")
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            if (expected != tree.epoch) {
                throw new ConcurrentModificationException();
            }
            int slot = next;
            next = IndexedRedBlackTree.successor(selected, slot);
            returned = slot;
            return (T) (kind == ENTRY ? selected.entry(slot)
                    : kind == KEY ? selected.keys[slot] : selected.values[slot]);
        }

        @Override
        public void remove() {
            if (returned < 0) {
                throw new IllegalStateException();
            }
            if (expected != tree.epoch) {
                throw new ConcurrentModificationException();
            }
            if (selected.left[returned] >= 0 && selected.right[returned] >= 0) {
                next = returned;
            }
            tree.erase(selected, returned);
            expected = tree.epoch;
            returned = -1;
        }
    }
}
