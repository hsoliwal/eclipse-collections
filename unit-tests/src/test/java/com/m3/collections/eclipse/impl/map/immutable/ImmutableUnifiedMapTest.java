/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.map.immutable;

import com.m3.collections.eclipse.api.map.ImmutableMap;
import com.m3.collections.eclipse.impl.math.IntegerSum;
import com.m3.collections.eclipse.impl.math.Sum;
import com.m3.collections.eclipse.impl.parallel.BatchIterable;
import com.m3.collections.eclipse.impl.tuple.Tuples;
import org.junit.Assert;
import org.junit.Test;

public class ImmutableUnifiedMapTest extends ImmutableMapTestCase
{
    @Override
    protected ImmutableMap<Integer, String> classUnderTest()
    {
        return new ImmutableUnifiedMap<>(Tuples.pair(1, "1"), Tuples.pair(2, "2"), Tuples.pair(3, "3"), Tuples.pair(4, "4"));
    }

    @Override
    protected int size()
    {
        return 4;
    }

    @Test
    @Override
    public void testToString()
    {
        Assert.assertEquals("{1=1, 2=2, 3=3, 4=4}", this.classUnderTest().toString());
    }

    @Test
    public void getBatchCount()
    {
        BatchIterable<Integer> integerBatchIterable = (BatchIterable<Integer>) this.classUnderTest();
        Assert.assertEquals(5, integerBatchIterable.getBatchCount(3));
    }

    @Test
    public void batchForEach()
    {
        Sum sum = new IntegerSum(0);
        BatchIterable<String> integerBatchIterable = (BatchIterable<String>) this.classUnderTest();
        integerBatchIterable.batchForEach(each -> sum.add(Integer.valueOf(each)), 0, 1);
        Assert.assertEquals(10, sum.getValue());
    }
}
