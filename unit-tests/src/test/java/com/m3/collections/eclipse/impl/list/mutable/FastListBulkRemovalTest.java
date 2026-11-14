/*
 * Copyright (c) 2026 Contributors to the Synexia Project.
 * Licensed under the Eclipse Public License v1.0 and Eclipse Distribution License v1.0.
 */
package com.m3.collections.eclipse.impl.list.mutable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.m3.collections.eclipse.api.block.predicate.Predicate2;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

/** Verifies FastList's immutable-value membership array fast path and legacy delegation. */
class FastListBulkRemovalTest {

    @Test
    void largeArrayListBulkCompactsInOriginalOrder() {
        final ArrayList<Object> values = new ArrayList<>();
        for (int i = 0; i < 16_384; i++) {
            values.add(i == 0 ? null : "key-" + i);
        }
        final FastList<Object> actual = FastList.newList(values);
        final ArrayList<Object> removals = new ArrayList<>();
        for (int i = 0; i < 4_096; i++) {
            removals.add(i == 0 ? null : "key-" + (i * 3 + 1));
        }
        removals.add("key-1");

        // Baseline AbstractMutableList uses ArrayList.contains for each item.
        final ArrayList<Object> expected = new ArrayList<>(values);
        assertEquals(expected.removeAll(removals), actual.removeAll(removals));
        assertEquals(expected, actual);
        assertFalse(actual.removeAll(removals));
    }

    @Test
    void seededSmallAndLargeCollectionsHaveEquivalentMembership() {
        final Random random = new Random(0x5eede21L);
        final int[] sizes = {0, 1, 31, 255, 4095, 4096, 8192, 16_384};
        final int[] removals = {0, 1, 32, 511, 512, 1024};
        for (int size : sizes) {
            ArrayList<Integer> data = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                data.add(i);
            }
            for (int removeCount : removals) {
                ArrayList<Integer> targets = new ArrayList<>(removeCount);
                for (int i = 0; i < removeCount; i++) {
                    targets.add(random.nextInt(size + 64));
                }
                FastList<Integer> actual = FastList.newList(data);
                ArrayList<Integer> expected = new ArrayList<>(data);
                assertEquals(expected.removeAll(targets), actual.removeAll(targets),
                        "size=" + size + " count=" + removeCount);
                assertEquals(expected, actual);
            }
        }
    }

    @Test
    void linkedCollectionRetainsSuperclassSemantics() {
        ArrayList<String> data = new ArrayList<>();
        for (int i = 0; i < 512; i++) {
            data.add("item-" + i);
        }
        Collection<String> source = new LinkedList<>();
        for (int i = 100; i < 200; i++) {
            source.add("item-" + i);
        }
        FastList<String> actual = FastList.newList(data);
        assertTrue(actual.removeAll(source));
        assertEquals(412, actual.size());
    }

    @Test
    void subclassRemoveIfWithDispatchIsPreserved() {
        final class Tracking extends FastList<String> {
            private int visits;

            @Override
            public <P> boolean removeIfWith(Predicate2<? super String, ? super P> predicate,
                    P parameter) {
                this.visits++;
                return super.removeIfWith(predicate, parameter);
            }
        }
        Tracking actual = new Tracking();
        for (int i = 0; i < 8_192; i++) {
            actual.add("item-" + i);
        }
        List<String> targets = new ArrayList<>();
        for (int i = 0; i < 512; i++) {
            targets.add("item-" + i);
        }
        assertTrue(actual.removeAll(targets));
        assertEquals(1, actual.visits);
        assertEquals(7_680, actual.size());
    }

    @Test
    void opaqueEqualityAndNullArgumentRetainFallback() {
        final class Probe {
            final int value;
            int calls;

            Probe(int value) {
                this.value = value;
            }

            @Override
            public boolean equals(Object value) {
                this.calls++;
                return value instanceof Probe && ((Probe) value).value == this.value;
            }

            @Override
            public int hashCode() {
                return value;
            }
        }
        ArrayList<Probe> data = new ArrayList<>();
        for (int i = 0; i < 8_192; i++) {
            data.add(new Probe(i));
        }
        FastList<Probe> actual = FastList.newList(data);
        ArrayList<Probe> targets = new ArrayList<>();
        for (int i = 0; i < 512; i++) {
            targets.add(new Probe(i));
        }
        assertTrue(actual.removeAll(targets));
        assertEquals(7_680, actual.size());
        assertTrue(data.stream().anyMatch(item -> item.calls > 0));
        assertThrows(NullPointerException.class, () -> actual.removeAll(null));
    }
}
