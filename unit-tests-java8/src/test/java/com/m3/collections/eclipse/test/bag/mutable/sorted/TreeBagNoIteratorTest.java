/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.test.bag.mutable.sorted;

import java.util.Comparator;
import java.util.Iterator;

import com.m3.collections.eclipse.api.bag.sorted.MutableSortedBag;
import com.m3.collections.eclipse.impl.bag.sorted.mutable.TreeBag;
import com.m3.collections.eclipse.impl.block.factory.Comparators;
import com.m3.collections.eclipse.impl.test.junit.Java8Runner;
import com.m3.collections.eclipse.test.IterableTestCase;
import org.junit.runner.RunWith;

@RunWith(Java8Runner.class)
public class TreeBagNoIteratorTest implements MutableSortedBagTestCase, OrderedIterableNoIteratorTest
{
    @SafeVarargs
    @Override
    public final <T> MutableSortedBag<T> newWith(T... elements)
    {
        MutableSortedBag<T> result = new TreeBagNoIterator<>(Comparators.reverseNaturalOrder());
        IterableTestCase.addAllTo(elements, result);
        return result;
    }

    @Override
    public void Iterable_remove()
    {
        OrderedIterableNoIteratorTest.super.Iterable_remove();
    }

    @Override
    public void RichIterable_iterator_iterationOrder()
    {
        OrderedIterableNoIteratorTest.super.RichIterable_iterator_iterationOrder();
    }

    public static class TreeBagNoIterator<T> extends TreeBag<T>
    {
        public TreeBagNoIterator()
        {
            // For serialization
        }

        public TreeBagNoIterator(Comparator<? super T> comparator)
        {
            super(comparator);
        }

        @Override
        public Iterator<T> iterator()
        {
            throw new AssertionError("No iteration patterns should delegate to iterator()");
        }
    }
}
