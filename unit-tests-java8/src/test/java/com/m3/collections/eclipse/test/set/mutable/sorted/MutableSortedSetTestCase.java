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
import com.m3.collections.eclipse.test.MutableSortedIterableTestCase;
import com.m3.collections.eclipse.test.collection.mutable.MutableCollectionUniqueTestCase;
import com.m3.collections.eclipse.test.set.sorted.SortedSetIterableTestCase;
import com.m3.collections.eclipse.test.set.sorted.SortedSetTestCase;

public interface MutableSortedSetTestCase extends SortedSetIterableTestCase, MutableCollectionUniqueTestCase, SortedSetTestCase, MutableSortedIterableTestCase
{
    @Override
    <T> MutableSortedSet<T> newWith(T... elements);

    @Override
    default void Iterable_remove()
    {
        // Both implementations are the same
        SortedSetTestCase.super.Iterable_remove();
        MutableSortedIterableTestCase.super.Iterable_remove();
    }
}
