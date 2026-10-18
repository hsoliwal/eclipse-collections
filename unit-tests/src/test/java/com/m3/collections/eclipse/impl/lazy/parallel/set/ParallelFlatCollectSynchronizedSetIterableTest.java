/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.lazy.parallel.set;

import com.m3.collections.eclipse.api.ParallelIterable;
import com.m3.collections.eclipse.api.bag.MutableBag;
import com.m3.collections.eclipse.api.block.function.Function;
import com.m3.collections.eclipse.impl.bag.mutable.HashBag;
import com.m3.collections.eclipse.impl.block.factory.IntegerPredicates;
import com.m3.collections.eclipse.impl.block.function.NegativeIntervalFunction;
import com.m3.collections.eclipse.impl.lazy.parallel.ParallelIterableTestCase;
import com.m3.collections.eclipse.impl.list.mutable.FastList;
import com.m3.collections.eclipse.impl.set.mutable.UnifiedSet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParallelFlatCollectSynchronizedSetIterableTest extends ParallelIterableTestCase
{
    @Override
    protected ParallelIterable<Integer> classUnderTest()
    {
        return this.newWith(4, 3, 2, 1);
    }

    @Override
    protected ParallelIterable<Integer> newWith(Integer... littleElements)
    {
        return UnifiedSet.newSetWith(littleElements)
                .asSynchronized()
                .asParallel(this.executorService, 2)
                .flatCollect(i -> FastList.newListWith(9, 8, 7, 6, 5, 4, 3, 2, 1).select(j -> j <= i).collect(j -> i * 10 + j))
                .collect(i -> i / 10);
    }

    @Override
    protected MutableBag<Integer> getExpected()
    {
        return HashBag.newBagWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
    }

    @Override
    protected MutableBag<Integer> getExpectedWith(Integer... littleElements)
    {
        return HashBag.newBagWith(littleElements)
                .asSynchronized()
                .flatCollect(i -> FastList.newListWith(9, 8, 7, 6, 5, 4, 3, 2, 1).select(j -> j <= i).collect(j -> i * 10 + j))
                .collect(i -> i / 10);
    }

    @Override
    protected boolean isOrdered()
    {
        return false;
    }

    @Override
    protected boolean isUnique()
    {
        return false;
    }

    @Test
    @Override
    public void groupBy()
    {
        Function<Integer, Boolean> isOddFunction = object -> IntegerPredicates.isOdd().accept(object);

        assertEquals(
                this.getExpected().toBag().groupBy(isOddFunction),
                this.classUnderTest().groupBy(isOddFunction));
    }

    @Test
    @Override
    public void groupByEach()
    {
        assertEquals(
                this.getExpected().toBag().groupByEach(new NegativeIntervalFunction()),
                this.classUnderTest().groupByEach(new NegativeIntervalFunction()));
    }
}
