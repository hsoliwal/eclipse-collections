// Modified 2026 by Hitesh Soliwal and Contributors to the Synexia Project: tree-slot lifecycle adaptation.
// SPDX-FileCopyrightText: 2026 Hitesh Soliwal and Contributors to the Synexia Project
// SPDX-License-Identifier: Apache-2.0
package com.m3.collections.eclipse.impl.map.sorted.mutable;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * Experimental tree storage adapted from M3LinkedList's reusable array slots.
 * Tree ownership and red-black balancing belong to the composing leaf.
 */
final class IndexedTreeArena<K, V> {
    Object[] keys = new Object[0];
    Object[] values = new Object[0];
    int[] left = new int[0];
    int[] right = new int[0];
    int[] parent = new int[0];
    long[] liveMask = new long[0];
    long[] blackMask = new long[0];
    Object[] entryViews = new Object[0];
    int used;
    int size;
    int free = -1;

    int allocate(K key, V value, int parentSlot) {
        if (parentSlot != -1) {
            requireLive(parentSlot);
        }
        int slot;
        int nextFree = -1;
        boolean reused = free != -1;
        if (reused) {
            slot = free;
            if (slot < 0 || slot >= used) {
                throw new IllegalStateException("arena free slot out of range");
            }
            requireCleared(slot);
            nextFree = left[slot];
            if (nextFree != -1 && (nextFree < 0 || nextFree >= used
                    || nextFree == slot || live(nextFree))) {
                throw new IllegalStateException("arena free link");
            }
        } else {
            if (used == keys.length) {
                grow();
            }
            slot = used;
            requireCleared(slot);
            if (left[slot] != -1) {
                throw new IllegalStateException("arena unused slot link");
            }
        }
        keys[slot] = key;
        values[slot] = value;
        left[slot] = -1;
        right[slot] = -1;
        parent[slot] = parentSlot;
        liveMask[slot >>> 6] |= 1L << (slot & 63);
        if (reused) {
            free = nextFree;
        } else {
            used++;
        }
        size++;
        return slot;
    }

    void release(int slot) {
        requireLive(slot);
        EntryView<K, V> view = cachedView(slot);
        if (view != null) {
            view.detach();
        }
        entryViews[slot] = null;
        keys[slot] = null;
        values[slot] = null;
        long bit = 1L << (slot & 63);
        liveMask[slot >>> 6] &= ~bit;
        blackMask[slot >>> 6] &= ~bit;
        left[slot] = free;
        right[slot] = -1;
        parent[slot] = -1;
        free = slot;
        size--;
    }

    void copyPayload(int from, int to) {
        requireLive(from);
        requireLive(to);
        keys[to] = keys[from];
        values[to] = values[from];
    }

    boolean live(int slot) {
        return slot >= 0 && slot < used && bit(liveMask, slot);
    }

    boolean black(int slot) {
        return slot < 0 || (slot < used && bit(blackMask, slot));
    }

    void setBlack(int slot, boolean black) {
        if (slot < 0) {
            return;
        }
        requireLive(slot);
        long bit = 1L << (slot & 63);
        if (black) {
            blackMask[slot >>> 6] |= bit;
        } else {
            blackMask[slot >>> 6] &= ~bit;
        }
    }

    Map.Entry<K, V> entry(int slot) {
        requireLive(slot);
        EntryView<K, V> view = cachedView(slot);
        if (view == null) {
            view = new EntryView<>(this, slot);
            entryViews[slot] = new WeakReference<>(view);
        }
        return view;
    }

    @SuppressWarnings("unchecked")
    private EntryView<K, V> cachedView(int slot) {
        Object reference = entryViews[slot];
        return reference == null ? null
                : ((WeakReference<EntryView<K, V>>) reference).get();
    }

    private static boolean bit(long[] mask, int slot) {
        return (mask[slot >>> 6] & (1L << (slot & 63))) != 0;
    }

    private void requireLive(int slot) {
        if (!live(slot)) {
            throw new IllegalStateException("arena slot is not live: " + slot);
        }
    }

    private void requireCleared(int slot) {
        if (bit(liveMask, slot) || bit(blackMask, slot)
                || keys[slot] != null || values[slot] != null
                || right[slot] != -1 || parent[slot] != -1 || entryViews[slot] != null) {
            throw new IllegalStateException("arena slot is not cleared: " + slot);
        }
    }

    private void grow() {
        int old = keys.length;
        int maximum = Integer.MAX_VALUE - 8;
        if (old >= maximum) {
            throw new OutOfMemoryError("arena capacity exhausted");
        }
        int capacity = old == 0 ? 8 : old <= maximum / 2 ? old * 2 : maximum;
        int words = ((capacity - 1) >>> 6) + 1;
        // Publish no lane until every copy and initialization has succeeded.
        Object[] newKeys = Arrays.copyOf(keys, capacity);
        Object[] newValues = Arrays.copyOf(values, capacity);
        int[] newLeft = Arrays.copyOf(left, capacity);
        int[] newRight = Arrays.copyOf(right, capacity);
        int[] newParent = Arrays.copyOf(parent, capacity);
        long[] newLive = Arrays.copyOf(liveMask, words);
        long[] newBlack = Arrays.copyOf(blackMask, words);
        Object[] newViews = Arrays.copyOf(entryViews, capacity);
        Arrays.fill(newLeft, old, capacity, -1);
        Arrays.fill(newRight, old, capacity, -1);
        Arrays.fill(newParent, old, capacity, -1);
        keys = newKeys;
        values = newValues;
        left = newLeft;
        right = newRight;
        parent = newParent;
        liveMask = newLive;
        blackMask = newBlack;
        entryViews = newViews;
    }

    void verifyInvariants() {
        int capacity = keys.length;
        int words = capacity == 0 ? 0 : ((capacity - 1) >>> 6) + 1;
        invariant(values.length == capacity && left.length == capacity
                && right.length == capacity && parent.length == capacity
                && entryViews.length == capacity && liveMask.length == words
                && blackMask.length == words, "parallel lanes");
        invariant(used >= 0 && used <= capacity && size >= 0 && size <= used,
                "arena counts");
        invariant(free == -1 || (free >= 0 && free < used), "free head");
        int active = 0;
        for (int word = 0; word < words; word++) {
            invariant((blackMask[word] & ~liveMask[word]) == 0, "dead black bit");
            active += Long.bitCount(liveMask[word]);
        }
        invariant(active == size, "live mask count");
        if (capacity != 0 && (capacity & 63) != 0) {
            invariant(((liveMask[words - 1] | blackMask[words - 1])
                    >>> (capacity & 63)) == 0, "mask beyond capacity");
        }
        for (int slot = 0; slot < capacity; slot++) {
            if (slot < used && live(slot)) {
                verifyLink(slot, left[slot]);
                verifyLink(slot, right[slot]);
                verifyLink(slot, parent[slot]);
                invariant(entryViews[slot] == null
                        || entryViews[slot] instanceof WeakReference<?>, "entry cache");
            } else {
                invariant(keys[slot] == null && values[slot] == null
                        && entryViews[slot] == null && right[slot] == -1
                        && parent[slot] == -1 && !bit(liveMask, slot)
                        && !bit(blackMask, slot), "cleared slot");
                invariant(slot < used || left[slot] == -1, "unused slot link");
            }
        }
        int dead = 0;
        for (int slot = free; slot != -1; slot = left[slot]) {
            invariant(slot >= 0 && slot < used && !live(slot) && dead < used - size,
                    "free chain");
            dead++;
        }
        invariant(dead == used - size, "free conservation");
    }

    private void verifyLink(int owner, int link) {
        invariant(link == -1 || (link != owner && live(link)), "live tree link");
    }

    private static void invariant(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static final class EntryView<K, V> implements Map.Entry<K, V> {
        private IndexedTreeArena<K, V> arena;
        private final int slot;
        private K detachedKey;
        private V detachedValue;

        EntryView(IndexedTreeArena<K, V> arena, int slot) {
            this.arena = arena;
            this.slot = slot;
        }

        private void detach() {
            detachedKey = getKey();
            detachedValue = getValue();
            arena = null;
        }

        @Override
        @SuppressWarnings("unchecked")
        public K getKey() {
            return arena == null ? detachedKey : (K) arena.keys[slot];
        }

        @Override
        @SuppressWarnings("unchecked")
        public V getValue() {
            return arena == null ? detachedValue : (V) arena.values[slot];
        }

        @Override
        public V setValue(V value) {
            V previous = getValue();
            if (arena == null) {
                detachedValue = value;
            } else {
                arena.values[slot] = value;
            }
            return previous;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof Map.Entry<?, ?> entry
                    && equalPayload(getKey(), entry.getKey())
                    && equalPayload(getValue(), entry.getValue());
        }

        private static boolean equalPayload(Object stored, Object query) {
            return stored == null ? query == null : stored.equals(query);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(getKey()) ^ Objects.hashCode(getValue());
        }

        @Override
        public String toString() {
            return getKey() + "=" + getValue();
        }
    }
}
