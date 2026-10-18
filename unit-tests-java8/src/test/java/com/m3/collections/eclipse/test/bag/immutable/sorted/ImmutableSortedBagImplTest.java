/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.test.bag.immutable.sorted;

import com.m3.collections.eclipse.api.bag.sorted.ImmutableSortedBag;
import com.m3.collections.eclipse.api.bag.sorted.MutableSortedBag;
import com.m3.collections.eclipse.impl.bag.sorted.mutable.TreeBag;
import com.m3.collections.eclipse.impl.block.factory.Comparators;
import com.m3.collections.eclipse.test.IterableTestCase;

public class ImmutableSortedBagImplTest implements ImmutableSortedBagTestCase
{
    @SafeVarargs
    @Override
    public final <T> ImmutableSortedBag<T> newWith(T... elements)
    {
        MutableSortedBag<T> result = new TreeBag<>(Comparators.reverseNaturalOrder());
        IterableTestCase.addAllTo(elements, result);
        return result.toImmutable();
    }
}
