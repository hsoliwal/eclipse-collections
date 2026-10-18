/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.set.immutable;

import com.m3.collections.eclipse.api.bag.MutableBag;
import com.m3.collections.eclipse.api.set.ImmutableSet;
import com.m3.collections.eclipse.api.set.MutableSet;
import com.m3.collections.eclipse.impl.factory.Bags;
import com.m3.collections.eclipse.impl.factory.Sets;
import com.m3.collections.eclipse.impl.list.Interval;
import com.m3.collections.eclipse.impl.list.mutable.FastList;
import com.m3.collections.eclipse.impl.list.primitive.IntInterval;
import com.m3.collections.eclipse.impl.set.mutable.UnifiedSet;
import com.m3.collections.eclipse.impl.test.Verify;
import org.junit.Assert;
import org.junit.Test;

public class ImmutableEmptySetTest extends AbstractImmutableEmptySetTestCase
{
    @Override
    protected ImmutableSet<Integer> classUnderTest()
    {
        return Sets.immutable.of();
    }

    @Override
    @Test
    public void newWithout()
    {
        Assert.assertSame(Sets.immutable.of(), Sets.immutable.of().newWithout(1));
        Assert.assertSame(Sets.immutable.of(), Sets.immutable.of().newWithoutAll(Interval.oneTo(3)));
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        ImmutableSet<Integer> immutable = this.classUnderTest();
        MutableSet<Integer> mutable = UnifiedSet.newSet(immutable);
        Verify.assertEqualsAndHashCode(mutable, immutable);
        Verify.assertPostSerializedIdentity(immutable);
        Assert.assertNotEquals(FastList.newList(mutable), immutable);
    }

    @Test
    public void countByEach()
    {
        Assert.assertEquals(Bags.immutable.empty(), this.classUnderTest().countByEach(each -> IntInterval.oneTo(5).collect(i -> each + i)));
    }

    @Test
    public void countByEach_target()
    {
        MutableBag<Integer> target = Bags.mutable.empty();
        Assert.assertEquals(target, this.classUnderTest().countByEach(each -> IntInterval.oneTo(5).collect(i -> each + i), target));
    }
}
