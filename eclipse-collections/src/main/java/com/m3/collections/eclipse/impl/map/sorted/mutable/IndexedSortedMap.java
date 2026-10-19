// SPDX-License-Identifier: Apache-2.0
// SPDX-FileCopyrightText: 2026 Hitesh Soliwal and contributors
package com.m3.collections.eclipse.impl.map.sorted.mutable;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

/** Private storage boundary; its visible signatures use Java 8 platform types. */
final class IndexedSortedMap<K, V> extends AbstractMap<K, V>
        implements SortedMap<K, V>, Serializable
{
    private static final long serialVersionUID = 1L;
    private static final int LOWER = 1;
    private static final int UPPER = 2;

    private final Storage<K, V> storage;
    private final int boundsMask;
    // As with sorted-map keys, bounds need be serializable only when written.
    @SuppressWarnings("serial")
    private final K lower;
    @SuppressWarnings("serial")
    private final K upper;
    private transient SortedMap<K, V> cachedView;
    private transient Object cachedBackend;
    private transient Set<Entry<K, V>> entries;
    private transient Set<K> keys;
    private transient Collection<V> values;
    private transient boolean rangeSizeCached;
    private transient int rangeSize;
    private transient int rangeSizeEpoch;

    IndexedSortedMap()
    {
        this((Comparator<? super K>) null);
    }

    IndexedSortedMap(Comparator<? super K> comparator)
    {
        this.storage = new Storage<>(comparator);
        this.boundsMask = 0;
        this.lower = null;
        this.upper = null;
    }

    IndexedSortedMap(Map<? extends K, ? extends V> input)
    {
        this();
        this.putAll(input);
    }

    IndexedSortedMap(SortedMap<K, ? extends V> input)
    {
        this(input.comparator());
        int count = input.size();
        Iterator<? extends Entry<? extends K, ? extends V>> cursor = input.entrySet().iterator();
        if (count > 0)
        {
            this.storage.backend.putAll(new SortedInput<>(this.comparator(), count, cursor));
        }
    }

    private IndexedSortedMap(Storage<K, V> storage, int boundsMask, K lower, K upper,
            SortedMap<K, V> selected)
    {
        this.storage = storage;
        this.boundsMask = boundsMask;
        this.lower = lower;
        this.upper = upper;
        this.cachedView = selected;
        this.cachedBackend = storage.backend;
    }

    private SortedMap<K, V> delegate()
    {
        if (this.cachedView == null || this.cachedBackend != this.storage.backend)
        {
            SortedMap<K, V> selected = this.storage.backend;
            if (this.boundsMask == (LOWER | UPPER))
            {
                selected = selected.subMap(this.lower, this.upper);
            }
            else if ((this.boundsMask & LOWER) != 0)
            {
                selected = selected.tailMap(this.lower);
            }
            else if ((this.boundsMask & UPPER) != 0)
            {
                selected = selected.headMap(this.upper);
            }
            this.cachedView = selected;
            this.cachedBackend = this.storage.backend;
        }
        return this.cachedView;
    }

    @Override public Comparator<? super K> comparator() { return this.storage.comparator; }
    @Override public int size()
    {
        if (this.boundsMask == 0) { return this.storage.size(); }
        if (!this.rangeSizeCached || this.rangeSizeEpoch != this.storage.backend.tree.epoch)
        {
            this.rangeSize = (this.storage.backend.tree.root < 0) ? 0 : this.delegate().size();
            this.rangeSizeEpoch = this.storage.backend.tree.epoch;
            this.rangeSizeCached = true;
        }
        return this.rangeSize;
    }
    @Override public boolean isEmpty()
    {
        return this.boundsMask == 0 ? this.storage.size() == 0
                : (this.storage.backend.tree.root < 0) || this.delegate().isEmpty();
    }
    @Override public V get(Object key)
    {
        return this.storage.readingComparator
                ? this.inReadingBounds(key) ? this.storage.backend.get(key) : null
                : this.delegate().get(key);
    }
    @Override public V getOrDefault(Object key, V fallback)
    {
        return this.storage.readingComparator
                ? this.inReadingBounds(key) ? this.storage.backend.getOrDefault(key, fallback) : fallback
                : this.delegate().getOrDefault(key, fallback);
    }
    @Override public boolean containsKey(Object key)
    {
        return this.storage.readingComparator
                ? this.inReadingBounds(key) && this.storage.backend.containsKey(key)
                : this.delegate().containsKey(key);
    }
    @Override public V put(K key, V value)
    {
        return this.delegate().put(key, value);
    }
    @Override public V remove(Object key)
    {
        return this.storage.readingComparator
                ? this.inReadingBounds(key) ? this.storage.backend.remove(key) : null
                : this.delegate().remove(key);
    }

    private boolean inReadingBounds(Object key)
    {
        return ((this.boundsMask & LOWER) == 0 || this.compareReadingBound(key, this.lower) >= 0)
                && ((this.boundsMask & UPPER) == 0 || this.compareReadingBound(key, this.upper) < 0);
    }

    @SuppressWarnings("unchecked")
    private int compareReadingBound(Object key, K bound)
    {
        Comparator<? super K> order = this.comparator();
        return order == null ? ((Comparable<? super K>) key).compareTo(bound)
                : order.compare((K) key, bound);
    }

    @Override public void clear()
    {
        if (this.boundsMask == 0)
        {
            this.storage.backend.clear();
        }
        else if (this.storage.backend.tree.root >= 0)
        {
            Iterator<Entry<K, V>> cursor = this.entrySet().iterator();
            while (cursor.hasNext()) { cursor.next(); cursor.remove(); }
        }
    }

    @Override public K firstKey()
    {
        if ((this.storage.backend.tree.root < 0)) { throw new NoSuchElementException(); }
        return this.delegate().firstKey();
    }

    @Override public K lastKey()
    {
        if ((this.storage.backend.tree.root < 0)) { throw new NoSuchElementException(); }
        return this.delegate().lastKey();
    }

    @Override public SortedMap<K, V> headMap(K high)
    {
        SortedMap<K, V> selected = this.delegate().headMap(high);
        return new IndexedSortedMap<>(this.storage, this.boundsMask | UPPER, this.lower, high, selected);
    }

    @Override public SortedMap<K, V> tailMap(K low)
    {
        SortedMap<K, V> selected = this.delegate().tailMap(low);
        return new IndexedSortedMap<>(this.storage, this.boundsMask | LOWER, low, this.upper, selected);
    }

    @Override public SortedMap<K, V> subMap(K low, K high)
    {
        SortedMap<K, V> selected = this.delegate().subMap(low, high);
        return new IndexedSortedMap<>(this.storage, LOWER | UPPER, low, high, selected);
    }

    @Override public void putAll(Map<? extends K, ? extends V> input)
    {
        int count = input.size();
        if (this.boundsMask == 0 && this.storage.size() == 0 && count != 0
                && input instanceof SortedMap<?, ?> sorted)
        {
            Comparator<?> sourceComparator = sorted.comparator();
            Comparator<? super K> targetComparator = this.comparator();
            if (sourceComparator == targetComparator
                    || targetComparator != null && targetComparator.equals(sourceComparator))
            {
                // Both iterator layers share the current owner's structural epoch.
                this.storage.backend.tree.epoch++;
                Iterator<? extends Entry<? extends K, ? extends V>> cursor = input.entrySet().iterator();
                this.storage.backend.tree.size = count;
                IndexedRedBlackTree<K, V> built = IndexedRedBlackTree.build(targetComparator, count, cursor);
                this.storage.backend.tree.arena = built.arena;
                this.storage.backend.tree.root = built.root;
                return;
            }
        }
        super.putAll(input);
    }

    private static boolean sameValue(Object stored, Object query)
    {
        return stored == null ? query == null : stored.equals(query);
    }

    private <T> Iterator<T> receivingIterator(Iterator<T> cursor)
    {
        return new Iterator<>()
        {
            // Cache reachability so hasNext never consults M3's checked iterator.
            private boolean more = cursor.hasNext();
            private int expected = storage.backend.tree.epoch;
            private boolean removable;
            @Override public boolean hasNext() { return this.more; }
            @Override public T next()
            {
                if (!this.more) { throw new NoSuchElementException(); }
                if (this.expected != storage.backend.tree.epoch) { throw new ConcurrentModificationException(); }
                T next = cursor.next();
                this.more = cursor.hasNext();
                this.removable = true;
                return next;
            }
            @Override public void remove()
            {
                if (!this.removable) { throw new IllegalStateException(); }
                if (this.expected != storage.backend.tree.epoch) { throw new ConcurrentModificationException(); }
                cursor.remove();
                this.expected = storage.backend.tree.epoch;
                this.removable = false;
            }
        };
    }

    @Override public boolean containsValue(Object query)
    {
        for (V stored : this.values())
        {
            if (sameValue(query, stored)) { return true; }
        }
        return false;
    }

    @Override public Set<Entry<K, V>> entrySet()
    {
        if (this.entries == null) { this.entries = new EntryView(); }
        return this.entries;
    }

    @Override public Set<K> keySet()
    {
        if (this.keys == null)
        {
            this.keys = new AbstractSet<>()
            {
                @Override public int size() { return IndexedSortedMap.this.size(); }
                @Override public boolean isEmpty() { return IndexedSortedMap.this.isEmpty(); }
                @Override public void clear() { IndexedSortedMap.this.clear(); }
                @Override public boolean contains(Object key) { return IndexedSortedMap.this.containsKey(key); }
                @Override public boolean remove(Object key)
                {
                    int before = this.size();
                    IndexedSortedMap.this.remove(key);
                    return this.size() != before;
                }
                @Override public Iterator<K> iterator()
                {
                    return (storage.backend.tree.root < 0) ? Collections.emptyIterator()
                            : receivingIterator(delegate().keySet().iterator());
                }
            };
        }
        return this.keys;
    }

    @Override public Collection<V> values()
    {
        if (this.values == null)
        {
            this.values = new AbstractCollection<>()
            {
                @Override public int size() { return IndexedSortedMap.this.size(); }
                @Override public boolean isEmpty() { return IndexedSortedMap.this.isEmpty(); }
                @Override public void clear() { IndexedSortedMap.this.clear(); }
                @Override public boolean contains(Object value) { return IndexedSortedMap.this.containsValue(value); }
                @Override public Iterator<V> iterator()
                {
                    return (storage.backend.tree.root < 0) ? Collections.emptyIterator()
                            : receivingIterator(delegate().values().iterator());
                }
                @Override public boolean remove(Object query)
                {
                    Iterator<V> cursor = this.iterator();
                    while (cursor.hasNext())
                    {
                        V stored = cursor.next();
                        if (boundsMask == 0 ? sameValue(stored, query) : sameValue(query, stored))
                        {
                            cursor.remove();
                            return true;
                        }
                    }
                    return false;
                }
            };
        }
        return this.values;
    }

    private final class EntryView extends AbstractSet<Entry<K, V>>
    {
        @Override public int size() { return IndexedSortedMap.this.size(); }
        @Override public boolean isEmpty() { return IndexedSortedMap.this.isEmpty(); }
        @Override public void clear() { IndexedSortedMap.this.clear(); }
        @Override public Iterator<Entry<K, V>> iterator()
        {
            if ((storage.backend.tree.root < 0)) { return Collections.emptyIterator(); }
            return receivingIterator(delegate().entrySet().iterator());
        }
        @Override public boolean contains(Object candidate)
        {
            return delegate().entrySet().contains(candidate);
        }
        @Override public boolean remove(Object candidate)
        {
            return delegate().entrySet().remove(candidate);
        }
    }

    private static final class Storage<K, V> implements Serializable
    {
        private static final long serialVersionUID = 1L;
        // Construction accepts any Comparator; serialization enforces its writeability.
        @SuppressWarnings("serial")
        private Comparator<? super K> comparator;
        private transient IndexedRedBlackMap<K, V> backend;
        private transient boolean readingComparator;
        // The indexed RB owner keeps the original logical count/root lifecycle.
        Storage(Comparator<? super K> comparator)
        {
            this.comparator = comparator;
            this.backend = new IndexedRedBlackMap<>(comparator);
        }
        int size() { return this.backend.size(); }
        private void writeObject(ObjectOutputStream output) throws IOException
        {
            output.defaultWriteObject();
            output.writeInt(this.size());
            for (Entry<K, V> entry : this.backend.entrySet())
            {
                output.writeObject(entry.getKey());
                output.writeObject(entry.getValue());
            }
        }
        @SuppressWarnings("unchecked")
        private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException
        {
            // Comparator backreferences can observe the in-progress natural empty store.
            this.readingComparator = true;
            this.backend = new IndexedRedBlackMap<>((Comparator<? super K>) null);
            input.defaultReadObject();
            this.backend = new IndexedRedBlackMap<>(this.comparator);
            this.readingComparator = false;
            int count = input.readInt();
            if (count < 0) { throw new InvalidObjectException("negative indexed storage size"); }
            if (count == 0) { return; }
            Iterator<Entry<K, V>> cursor = new Iterator<>()
            {
                private int remaining = count;
                @Override public boolean hasNext() { return this.remaining != 0; }
                @Override public Entry<K, V> next()
                {
                    if (this.remaining == 0) { throw new NoSuchElementException(); }
                    try
                    {
                        K key = (K) input.readObject();
                        V value = (V) input.readObject();
                        this.remaining--;
                        return new SimpleImmutableEntry<>(key, value);
                    }
                    catch (IOException | ClassNotFoundException failure)
                    {
                        throw new InputFailure(failure);
                    }
                }
            };
            try
            {
                this.backend.putAll(new SortedInput<>(this.comparator, count, cursor));
            }
            catch (InputFailure failure)
            {
                Throwable cause = failure.getCause();
                if (cause instanceof IOException io) { throw io; }
                throw (ClassNotFoundException) cause;
            }
        }
    }

    private static final class InputFailure extends RuntimeException
    {
        private static final long serialVersionUID = 1L;
        InputFailure(Exception cause) { super(cause); }
    }

    private static final class SortedInput<K, V> extends AbstractMap<K, V> implements SortedMap<K, V>
    {
        private final Comparator<? super K> comparator;
        private final int count;
        private final Iterator<? extends Entry<? extends K, ? extends V>> cursor;
        SortedInput(Comparator<? super K> comparator, int count,
                Iterator<? extends Entry<? extends K, ? extends V>> cursor)
        {
            this.comparator = comparator;
            this.count = count;
            this.cursor = cursor;
        }
        @Override public int size() { return this.count; }
        @Override public Comparator<? super K> comparator() { return this.comparator; }
        @Override public Set<Entry<K, V>> entrySet()
        {
            return new AbstractSet<>()
            {
                @Override public int size() { return count; }
                @Override public Iterator<Entry<K, V>> iterator()
                {
                    return new Iterator<>()
                    {
                        @Override public boolean hasNext() { return cursor.hasNext(); }
                        @Override public Entry<K, V> next()
                        {
                            Entry<? extends K, ? extends V> entry = cursor.next();
                            return new SimpleImmutableEntry<>(entry.getKey(), entry.getValue());
                        }
                    };
                }
            };
        }
        @Override public K firstKey() { throw new UnsupportedOperationException(); }
        @Override public K lastKey() { throw new UnsupportedOperationException(); }
        @Override public SortedMap<K, V> subMap(K a, K b) { throw new UnsupportedOperationException(); }
        @Override public SortedMap<K, V> headMap(K b) { throw new UnsupportedOperationException(); }
        @Override public SortedMap<K, V> tailMap(K a) { throw new UnsupportedOperationException(); }
    }
}
