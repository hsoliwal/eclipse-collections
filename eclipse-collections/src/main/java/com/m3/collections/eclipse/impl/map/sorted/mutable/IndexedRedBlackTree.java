// SPDX-License-Identifier: Apache-2.0
// SPDX-FileCopyrightText: 2026 Hitesh Soliwal and contributors
// Rotations and deletion repair adapted from Apache Commons Collections
// TreeBidiMap, Apache-2.0, ASF copyright; see THIRD_PARTY_NOTICES.md.
package com.m3.collections.eclipse.impl.map.sorted.mutable;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/** Experimental private mutation kernel; array slots are the only stored nodes. */
final class IndexedRedBlackTree<K, V>
{
    final Comparator<? super K> comparator;
    IndexedTreeArena<K, V> arena = new IndexedTreeArena<>();
    int root = -1;
    int size;
    int epoch;

    IndexedRedBlackTree(Comparator<? super K> comparator) { this.comparator = comparator; }

    @SuppressWarnings("unchecked")
    private int compare(Object a, Object b)
    {
        return this.comparator == null ? ((Comparable<Object>) a).compareTo(b)
                : this.comparator.compare((K) a, (K) b);
    }

    int find(Object key)
    {
        if (this.comparator == null) { Objects.requireNonNull((Comparable<?>) key); }
        int slot = this.root;
        while (slot >= 0)
        {
            int order = this.compare(key, this.arena.keys[slot]);
            if (order == 0) { return slot; }
            slot = order < 0 ? this.arena.left[slot] : this.arena.right[slot];
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    V put(K key, V value)
    {
        if (this.root < 0)
        {
            this.compare(key, key);
            int first = this.arena.allocate(key, value, -1);
            this.arena.setBlack(first, true);
            this.root = first;
            this.size = 1;
            this.epoch++;
            return null;
        }
        int at = this.root;
        int parent;
        int order;
        do
        {
            parent = at;
            order = this.compare(key, this.arena.keys[at]);
            if (order == 0)
            {
                V old = (V) this.arena.values[at];
                this.arena.values[at] = value;
                return old;
            }
            at = order < 0 ? this.arena.left[at] : this.arena.right[at];
        }
        while (at >= 0);
        int added = this.arena.allocate(key, value, parent);
        if (order < 0) { this.arena.left[parent] = added; }
        else { this.arena.right[parent] = added; }
        this.repairInsert(added);
        this.size++;
        this.epoch++;
        return null;
    }

    private boolean isRoot(IndexedTreeArena<K, V> selected, int slot)
    {
        return selected == this.arena && slot == this.root;
    }

    private void publishRoot(IndexedTreeArena<K, V> selected, int slot)
    {
        this.arena = selected;
        this.root = slot;
    }

    static int left(IndexedTreeArena<?, ?> selected, int slot) { return slot < 0 ? -1 : selected.left[slot]; }
    static int right(IndexedTreeArena<?, ?> selected, int slot) { return slot < 0 ? -1 : selected.right[slot]; }
    static int parent(IndexedTreeArena<?, ?> selected, int slot) { return slot < 0 ? -1 : selected.parent[slot]; }

    private void rotateLeft(IndexedTreeArena<K, V> selected, int slot)
    {
        int pivot = selected.right[slot];
        int middle = selected.left[pivot];
        selected.right[slot] = middle;
        if (middle >= 0) { selected.parent[middle] = slot; }
        int oldParent = selected.parent[slot];
        selected.parent[pivot] = oldParent;
        if (oldParent < 0) { this.publishRoot(selected, pivot); }
        else if (selected.left[oldParent] == slot) { selected.left[oldParent] = pivot; }
        else { selected.right[oldParent] = pivot; }
        selected.left[pivot] = slot;
        selected.parent[slot] = pivot;
    }

    private void rotateRight(IndexedTreeArena<K, V> selected, int slot)
    {
        int pivot = selected.left[slot];
        int middle = selected.right[pivot];
        selected.left[slot] = middle;
        if (middle >= 0) { selected.parent[middle] = slot; }
        int oldParent = selected.parent[slot];
        selected.parent[pivot] = oldParent;
        if (oldParent < 0) { this.publishRoot(selected, pivot); }
        else if (selected.right[oldParent] == slot) { selected.right[oldParent] = pivot; }
        else { selected.left[oldParent] = pivot; }
        selected.right[pivot] = slot;
        selected.parent[slot] = pivot;
    }

    private void repairInsert(int slot)
    {
        IndexedTreeArena<K, V> selected = this.arena;
        selected.setBlack(slot, false);
        while (slot != this.root && !selected.black(parent(selected, slot)))
        {
            int p = parent(selected, slot);
            int grand = parent(selected, p);
            // The donor tests the inserted node's side here. Test the parent.
            if (p == left(selected, grand))
            {
                int uncle = right(selected, grand);
                if (!selected.black(uncle))
                {
                    selected.setBlack(p, true);
                    selected.setBlack(uncle, true);
                    selected.setBlack(grand, false);
                    slot = grand;
                }
                else
                {
                    if (slot == right(selected, p))
                    {
                        slot = p;
                        this.rotateLeft(selected, slot);
                    }
                    p = parent(selected, slot);
                    grand = parent(selected, p);
                    selected.setBlack(p, true);
                    selected.setBlack(grand, false);
                    this.rotateRight(selected, grand);
                }
            }
            else
            {
                int uncle = left(selected, grand);
                if (!selected.black(uncle))
                {
                    selected.setBlack(p, true);
                    selected.setBlack(uncle, true);
                    selected.setBlack(grand, false);
                    slot = grand;
                }
                else
                {
                    if (slot == left(selected, p))
                    {
                        slot = p;
                        this.rotateRight(selected, slot);
                    }
                    p = parent(selected, slot);
                    grand = parent(selected, p);
                    selected.setBlack(p, true);
                    selected.setBlack(grand, false);
                    this.rotateLeft(selected, grand);
                }
            }
        }
        selected.setBlack(this.root, true);
    }

    static int first(IndexedTreeArena<?, ?> selected, int slot)
    {
        while (slot >= 0 && selected.left[slot] >= 0) { slot = selected.left[slot]; }
        return slot;
    }

    static int successor(IndexedTreeArena<?, ?> selected, int slot)
    {
        if (slot < 0) { return -1; }
        if (selected.right[slot] >= 0) { return first(selected, selected.right[slot]); }
        int p = selected.parent[slot];
        while (p >= 0 && slot == selected.right[p]) { slot = p; p = selected.parent[p]; }
        return p;
    }

    /** Remove the exact old slot; no key comparison or search through the new root. */
    void erase(IndexedTreeArena<K, V> selected, int slot)
    {
        if (!selected.live(slot)) { throw new IllegalStateException("erased tree slot"); }
        this.size--;
        this.epoch++;
        if (selected.left[slot] >= 0 && selected.right[slot] >= 0)
        {
            int successor = successor(selected, slot);
            selected.copyPayload(successor, slot);
            slot = successor;
        }
        int child = selected.left[slot] >= 0 ? selected.left[slot] : selected.right[slot];
        if (child >= 0)
        {
            int p = selected.parent[slot];
            selected.parent[child] = p;
            if (p < 0) { this.publishRoot(selected, child); }
            else if (slot == selected.left[p]) { selected.left[p] = child; }
            else { selected.right[p] = child; }
            selected.left[slot] = -1;
            selected.right[slot] = -1;
            selected.parent[slot] = -1;
            if (selected.black(slot)) { this.repairDelete(selected, child); }
        }
        else if (selected.parent[slot] < 0) { this.publishRoot(selected, -1); }
        else
        {
            if (selected.black(slot)) { this.repairDelete(selected, slot); }
            int p = selected.parent[slot];
            if (p >= 0)
            {
                if (slot == selected.left[p]) { selected.left[p] = -1; }
                else if (slot == selected.right[p]) { selected.right[p] = -1; }
                selected.parent[slot] = -1;
            }
        }
        selected.release(slot);
    }

    private void repairDelete(IndexedTreeArena<K, V> selected, int slot)
    {
        while (!this.isRoot(selected, slot) && selected.black(slot))
        {
            // Keep the donor's null-safe state transitions. With a foreign root,
            // the original's detached-black path may not complete; its separately
            // bounded original/candidate observations must never count as a pass.
            int p = parent(selected, slot);
            if (slot == left(selected, p))
            {
                int sibling = right(selected, p);
                if (!selected.black(sibling))
                {
                    selected.setBlack(sibling, true);
                    selected.setBlack(p, false);
                    this.rotateLeft(selected, p);
                    sibling = right(selected, parent(selected, slot));
                }
                if (selected.black(left(selected, sibling)) && selected.black(right(selected, sibling)))
                {
                    selected.setBlack(sibling, false);
                    slot = parent(selected, slot);
                }
                else
                {
                    if (selected.black(right(selected, sibling)))
                    {
                        selected.setBlack(left(selected, sibling), true);
                        selected.setBlack(sibling, false);
                        this.rotateRight(selected, sibling);
                        sibling = right(selected, parent(selected, slot));
                    }
                    p = parent(selected, slot);
                    selected.setBlack(sibling, selected.black(p));
                    selected.setBlack(p, true);
                    selected.setBlack(right(selected, sibling), true);
                    this.rotateLeft(selected, p);
                    this.arena.setBlack(this.root, true);
                    return;
                }
            }
            else
            {
                int sibling = left(selected, p);
                if (!selected.black(sibling))
                {
                    selected.setBlack(sibling, true);
                    selected.setBlack(p, false);
                    this.rotateRight(selected, p);
                    sibling = left(selected, parent(selected, slot));
                }
                if (selected.black(right(selected, sibling)) && selected.black(left(selected, sibling)))
                {
                    selected.setBlack(sibling, false);
                    slot = parent(selected, slot);
                }
                else
                {
                    if (selected.black(left(selected, sibling)))
                    {
                        selected.setBlack(right(selected, sibling), true);
                        selected.setBlack(sibling, false);
                        this.rotateLeft(selected, sibling);
                        sibling = left(selected, parent(selected, slot));
                    }
                    p = parent(selected, slot);
                    selected.setBlack(sibling, selected.black(p));
                    selected.setBlack(p, true);
                    selected.setBlack(left(selected, sibling), true);
                    this.rotateRight(selected, p);
                    this.arena.setBlack(this.root, true);
                    return;
                }
            }
        }
        selected.setBlack(slot, true);
    }

    /** Build privately; the caller chooses the publication/count/epoch boundary. */
    static <K, V> IndexedRedBlackTree<K, V> build(Comparator<? super K> comparator, int count,
            Iterator<? extends Map.Entry<? extends K, ? extends V>> cursor)
    {
        if (count < 0) { throw new IllegalArgumentException("negative sorted count"); }
        IndexedRedBlackTree<K, V> built = new IndexedRedBlackTree<>(comparator);
        int redLevel = 0;
        for (int remaining = count - 1; remaining >= 0; remaining = remaining / 2 - 1) { redLevel++; }
        built.root = built.readSorted(0, 0, count - 1, redLevel, cursor);
        built.size = count;
        return built;
    }

    @SuppressWarnings("unchecked")
    private int readSorted(int level, int low, int high, int redLevel,
            Iterator<? extends Map.Entry<? extends K, ? extends V>> cursor)
    {
        if (high < low) { return -1; }
        int middle = (low + high) >>> 1;
        int leftChild = this.readSorted(level + 1, low, middle - 1, redLevel, cursor);
        Map.Entry<? extends K, ? extends V> entry = cursor.next();
        K key = entry.getKey();
        V value = entry.getValue();
        int slot = this.arena.allocate(key, value, -1);
        this.arena.setBlack(slot, level != redLevel);
        this.arena.left[slot] = leftChild;
        if (leftChild >= 0) { this.arena.parent[leftChild] = slot; }
        int rightChild = this.readSorted(level + 1, middle + 1, high, redLevel, cursor);
        this.arena.right[slot] = rightChild;
        if (rightChild >= 0) { this.arena.parent[rightChild] = slot; }
        return slot;
    }

    /** Normal coherent-tree proof only; ghost logical counts are an explicit separate state. */
    void verifyInvariants()
    {
        this.arena.verifyInvariants();
        if (this.root < 0)
        {
            if (this.size != 0 || this.arena.size != 0) { throw new AssertionError("empty tree count"); }
            return;
        }
        if (this.arena.parent[this.root] >= 0 || !this.arena.black(this.root)) { throw new AssertionError("root"); }
        int[] count = {0};
        this.verifyBranch(this.root, -1, false, null, false, null, count);
        if (count[0] != this.size || count[0] != this.arena.size) { throw new AssertionError("reachable count"); }
    }

    private int verifyBranch(int slot, int expectedParent, boolean hasLow, Object low,
            boolean hasHigh, Object high, int[] count)
    {
        if (slot < 0) { return 1; }
        if (++count[0] > this.arena.size || !this.arena.live(slot)
                || this.arena.parent[slot] != expectedParent) { throw new AssertionError("tree links"); }
        Object key = this.arena.keys[slot];
        if (hasLow && this.compare(key, low) <= 0 || hasHigh && this.compare(key, high) >= 0)
        { throw new AssertionError("tree order"); }
        int leftChild = this.arena.left[slot];
        int rightChild = this.arena.right[slot];
        if (!this.arena.black(slot) && (!this.arena.black(leftChild) || !this.arena.black(rightChild)))
        { throw new AssertionError("red parent"); }
        int a = this.verifyBranch(leftChild, slot, hasLow, low, true, key, count);
        int b = this.verifyBranch(rightChild, slot, true, key, hasHigh, high, count);
        if (a != b) { throw new AssertionError("black height"); }
        return a + (this.arena.black(slot) ? 1 : 0);
    }
}
