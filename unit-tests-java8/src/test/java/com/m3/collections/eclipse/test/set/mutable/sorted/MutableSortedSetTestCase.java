/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.test.set.mutable.sorted;

import com.m3.collections.eclipse.api.set.sorted.MutableSortedSet;
import com.m3.collections.eclipse.test.IterableTestCase;
import com.m3.collections.eclipse.test.MutableSortedIterableTestCase;
import com.m3.collections.eclipse.test.collection.mutable.MutableCollectionUniqueTestCase;
import com.m3.collections.eclipse.test.set.sorted.NavigableSetTestCase;
import com.m3.collections.eclipse.test.set.sorted.SortedSetIterableTestCase;
import org.junit.jupiter.api.Test;

import static com.m3.collections.eclipse.test.IterableTestCase.assertIterablesEqual;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface MutableSortedSetTestCase extends SortedSetIterableTestCase, MutableCollectionUniqueTestCase, NavigableSetTestCase, MutableSortedIterableTestCase
{
    @Override
    <T> MutableSortedSet<T> newWith(T... elements);

    @Override
    default IterableTestCase.OrderingType getOrderingType()
    {
        return IterableTestCase.OrderingType.SORTED_REVERSE_NATURAL;
    }

    @Override
    @Test
    default void Iterable_toString()
    {
        NavigableSetTestCase.super.Iterable_toString();
    }

    @Override
    @Test
    default void Iterable_remove()
    {
        // Both implementations are the same
        NavigableSetTestCase.super.Iterable_remove();
        MutableSortedIterableTestCase.super.Iterable_remove();
    }

    @Test
    default void MutableSortedSet_toReversed()
    {
        MutableSortedSet<Integer> sortedSet = this.newWith(4, 3, 2, 1);
        MutableSortedSet<Integer> reversed = sortedSet.toReversed();
        assertIterablesEqual(sortedSet.toList().reverseThis(), reversed.toList());
        assertTrue(reversed.add(99));
        assertIterablesEqual(this.newWith(4, 3, 2, 1), sortedSet);
    }
}
