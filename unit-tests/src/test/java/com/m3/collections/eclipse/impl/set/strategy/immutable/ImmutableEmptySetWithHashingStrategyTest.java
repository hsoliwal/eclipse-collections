/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.set.strategy.immutable;

import com.m3.collections.eclipse.api.block.HashingStrategy;
import com.m3.collections.eclipse.api.set.ImmutableSet;
import com.m3.collections.eclipse.api.set.MutableSet;
import com.m3.collections.eclipse.impl.block.factory.HashingStrategies;
import com.m3.collections.eclipse.impl.factory.HashingStrategySets;
import com.m3.collections.eclipse.impl.list.Interval;
import com.m3.collections.eclipse.impl.list.mutable.FastList;
import com.m3.collections.eclipse.impl.set.immutable.AbstractImmutableEmptySetTestCase;
import com.m3.collections.eclipse.impl.set.mutable.UnifiedSet;
import com.m3.collections.eclipse.impl.test.Verify;
import org.junit.Assert;
import org.junit.Test;

public class ImmutableEmptySetWithHashingStrategyTest extends AbstractImmutableEmptySetTestCase
{
    //Not using the static factor method in order to have concrete types for test cases
    private static final HashingStrategy<Integer> HASHING_STRATEGY = HashingStrategies.nullSafeHashingStrategy(new HashingStrategy<Integer>()
    {
        public int computeHashCode(Integer object)
        {
            return object.hashCode();
        }

        public boolean equals(Integer object1, Integer object2)
        {
            return object1.equals(object2);
        }
    });

    @Override
    protected ImmutableSet<Integer> classUnderTest()
    {
        return new ImmutableEmptySetWithHashingStrategy<>(HASHING_STRATEGY);
    }

    @Override
    @Test
    public void newWithout()
    {
        Assert.assertEquals(
                HashingStrategySets.immutable.of(HASHING_STRATEGY),
                HashingStrategySets.immutable.of(HASHING_STRATEGY).newWithout(1));
        Assert.assertEquals(
                HashingStrategySets.immutable.of(HASHING_STRATEGY),
                HashingStrategySets.immutable.of(HASHING_STRATEGY).newWithoutAll(Interval.oneTo(3)));
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        ImmutableSet<Integer> immutable = this.classUnderTest();
        MutableSet<Integer> mutable = UnifiedSet.newSet(immutable);
        Verify.assertEqualsAndHashCode(mutable, immutable);
        Verify.assertPostSerializedEqualsAndHashCode(immutable);
        Assert.assertNotEquals(FastList.newList(mutable), immutable);
    }
}
