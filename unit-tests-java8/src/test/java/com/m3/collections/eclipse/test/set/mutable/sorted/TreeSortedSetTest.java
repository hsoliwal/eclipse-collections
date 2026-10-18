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
import com.m3.collections.eclipse.impl.block.factory.Comparators;
import com.m3.collections.eclipse.impl.set.sorted.mutable.TreeSortedSet;
import com.m3.collections.eclipse.impl.test.junit.Java8Runner;
import com.m3.collections.eclipse.test.IterableTestCase;
import org.junit.runner.RunWith;

@RunWith(Java8Runner.class)
public class TreeSortedSetTest implements MutableSortedSetTestCase
{
    @SafeVarargs
    @Override
    public final <T> MutableSortedSet<T> newWith(T... elements)
    {
        MutableSortedSet<T> result = TreeSortedSet.newSet(Comparators.reverseNaturalOrder());
        IterableTestCase.addAllTo(elements, result);
        return result;
    }
}
