/*
 * Copyright (c) 2021 The Bank of New York Mellon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.stream;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import com.m3.collections.eclipse.api.bag.primitive.ImmutableDoubleBag;
import com.m3.collections.eclipse.api.bag.primitive.ImmutableIntBag;
import com.m3.collections.eclipse.api.bag.primitive.ImmutableLongBag;
import com.m3.collections.eclipse.api.bag.primitive.MutableDoubleBag;
import com.m3.collections.eclipse.api.bag.primitive.MutableIntBag;
import com.m3.collections.eclipse.api.bag.primitive.MutableLongBag;
import com.m3.collections.eclipse.api.list.primitive.ImmutableDoubleList;
import com.m3.collections.eclipse.api.list.primitive.ImmutableIntList;
import com.m3.collections.eclipse.api.list.primitive.ImmutableLongList;
import com.m3.collections.eclipse.api.list.primitive.MutableDoubleList;
import com.m3.collections.eclipse.api.list.primitive.MutableIntList;
import com.m3.collections.eclipse.api.list.primitive.MutableLongList;
import com.m3.collections.eclipse.api.set.primitive.ImmutableDoubleSet;
import com.m3.collections.eclipse.api.set.primitive.ImmutableIntSet;
import com.m3.collections.eclipse.api.set.primitive.ImmutableLongSet;
import com.m3.collections.eclipse.api.set.primitive.MutableDoubleSet;
import com.m3.collections.eclipse.api.set.primitive.MutableIntSet;
import com.m3.collections.eclipse.api.set.primitive.MutableLongSet;
import com.m3.collections.eclipse.api.stack.primitive.ImmutableDoubleStack;
import com.m3.collections.eclipse.api.stack.primitive.ImmutableIntStack;
import com.m3.collections.eclipse.api.stack.primitive.ImmutableLongStack;
import com.m3.collections.eclipse.api.stack.primitive.MutableDoubleStack;
import com.m3.collections.eclipse.api.stack.primitive.MutableIntStack;
import com.m3.collections.eclipse.api.stack.primitive.MutableLongStack;
import com.m3.collections.eclipse.impl.factory.primitive.DoubleBags;
import com.m3.collections.eclipse.impl.factory.primitive.DoubleLists;
import com.m3.collections.eclipse.impl.factory.primitive.DoubleSets;
import com.m3.collections.eclipse.impl.factory.primitive.DoubleStacks;
import com.m3.collections.eclipse.impl.factory.primitive.IntBags;
import com.m3.collections.eclipse.impl.factory.primitive.IntLists;
import com.m3.collections.eclipse.impl.factory.primitive.IntSets;
import com.m3.collections.eclipse.impl.factory.primitive.IntStacks;
import com.m3.collections.eclipse.impl.factory.primitive.LongBags;
import com.m3.collections.eclipse.impl.factory.primitive.LongLists;
import com.m3.collections.eclipse.impl.factory.primitive.LongSets;
import com.m3.collections.eclipse.impl.factory.primitive.LongStacks;
import com.m3.collections.eclipse.impl.list.primitive.IntInterval;
import org.junit.Assert;
import org.junit.Test;

/**
 * @since 9.0
 */
public class PrimitiveStreamsTest
{
    @Test
    public void toIntList()
    {
        MutableIntList list = PrimitiveStreams.mIntList(IntStream.rangeClosed(1, 10));
        Assert.assertEquals(IntInterval.oneTo(10), list);
        Assert.assertEquals(IntLists.immutable.ofAll(IntStream.rangeClosed(1, 10)), list);
    }

    @Test
    public void toImmutableIntList()
    {
        ImmutableIntList list = PrimitiveStreams.iIntList(IntStream.rangeClosed(1, 10));
        Assert.assertEquals(IntInterval.oneTo(10), list);
        Assert.assertEquals(IntLists.mutable.ofAll(IntStream.rangeClosed(1, 10)), list);
    }

    @Test
    public void toIntSet()
    {
        MutableIntSet set = PrimitiveStreams.mIntSet(IntStream.rangeClosed(1, 10));
        Assert.assertEquals(IntInterval.oneTo(10).toSet(), set);
        Assert.assertEquals(IntSets.immutable.ofAll(IntStream.rangeClosed(1, 10)), set);
    }

    @Test
    public void toImmutableIntSet()
    {
        ImmutableIntSet set = PrimitiveStreams.iIntSet(IntStream.rangeClosed(1, 10));
        Assert.assertEquals(IntInterval.oneTo(10).toSet(), set);
        Assert.assertEquals(IntSets.mutable.ofAll(IntStream.rangeClosed(1, 10)), set);
    }

    @Test
    public void toIntBag()
    {
        MutableIntBag bag = PrimitiveStreams.mIntBag(IntStream.rangeClosed(1, 10));
        Assert.assertEquals(IntInterval.oneTo(10).toBag(), bag);
        Assert.assertEquals(IntBags.immutable.ofAll(IntStream.rangeClosed(1, 10)), bag);
    }

    @Test
    public void toImmutableIntBag()
    {
        ImmutableIntBag bag = PrimitiveStreams.iIntBag(IntStream.rangeClosed(1, 10));
        Assert.assertEquals(IntInterval.oneTo(10).toBag(), bag);
        Assert.assertEquals(IntBags.mutable.ofAll(IntStream.rangeClosed(1, 10)), bag);
    }

    @Test
    public void toIntStack()
    {
        MutableIntStack stack = PrimitiveStreams.mIntStack(IntStream.rangeClosed(1, 10));
        Assert.assertEquals(IntStacks.mutable.withAll(IntInterval.oneTo(10)), stack);
        Assert.assertEquals(IntStacks.immutable.ofAll(IntStream.rangeClosed(1, 10)), stack);
    }

    @Test
    public void toImmutableIntStack()
    {
        ImmutableIntStack stack = PrimitiveStreams.iIntStack(IntStream.rangeClosed(1, 10));
        Assert.assertEquals(IntStacks.immutable.withAll(IntInterval.oneTo(10)), stack);
        Assert.assertEquals(IntStacks.mutable.ofAll(IntStream.rangeClosed(1, 10)), stack);
    }

    @Test
    public void toLongList()
    {
        MutableLongList list = PrimitiveStreams.mLongList(LongStream.rangeClosed(1, 10));
        Assert.assertEquals(IntInterval.oneTo(10).collectLong(i -> (long) i, LongLists.mutable.empty()), list);
        Assert.assertEquals(LongLists.immutable.ofAll(LongStream.rangeClosed(1, 10)), list);
    }

    @Test
    public void toImmutableLongList()
    {
        ImmutableLongList list = PrimitiveStreams.iLongList(LongStream.rangeClosed(1, 10));
        Assert.assertEquals(IntInterval.oneTo(10).collectLong(i -> (long) i, LongLists.mutable.empty()), list);
        Assert.assertEquals(LongLists.mutable.ofAll(LongStream.rangeClosed(1, 10)), list);
    }

    @Test
    public void toLongSet()
    {
        MutableLongSet set = PrimitiveStreams.mLongSet(LongStream.rangeClosed(1, 10));
        Assert.assertEquals(IntInterval.oneTo(10).collectLong(i -> (long) i, LongSets.mutable.empty()), set);
        Assert.assertEquals(LongSets.immutable.ofAll(LongStream.rangeClosed(1, 10)), set);
    }

    @Test
    public void toImmutableLongSet()
    {
        ImmutableLongSet set = PrimitiveStreams.iLongSet(LongStream.rangeClosed(1, 10));
        Assert.assertEquals(IntInterval.oneTo(10).collectLong(i -> (long) i, LongSets.mutable.empty()), set);
        Assert.assertEquals(LongSets.mutable.ofAll(LongStream.rangeClosed(1, 10)), set);
    }

    @Test
    public void toLongBag()
    {
        MutableLongBag bag = PrimitiveStreams.mLongBag(LongStream.rangeClosed(1, 10));
        Assert.assertEquals(IntInterval.oneTo(10).collectLong(i -> (long) i, LongBags.mutable.empty()), bag);
        Assert.assertEquals(LongBags.immutable.ofAll(LongStream.rangeClosed(1, 10)), bag);
    }

    @Test
    public void toImmutableLongBag()
    {
        ImmutableLongBag bag = PrimitiveStreams.iLongBag(LongStream.rangeClosed(1, 10));
        Assert.assertEquals(IntInterval.oneTo(10).collectLong(i -> (long) i, LongBags.mutable.empty()), bag);
        Assert.assertEquals(LongBags.mutable.ofAll(LongStream.rangeClosed(1, 10)), bag);
    }

    @Test
    public void toLongStack()
    {
        MutableLongStack stack = PrimitiveStreams.mLongStack(LongStream.rangeClosed(1, 10));
        Assert.assertEquals(LongStacks.mutable.ofAll(IntInterval.oneTo(10).asLazy().collectLong(i -> (long) i)), stack);
        Assert.assertEquals(LongStacks.immutable.ofAll(LongStream.rangeClosed(1, 10)), stack);
    }

    @Test
    public void toImmutableLongStack()
    {
        ImmutableLongStack stack = PrimitiveStreams.iLongStack(LongStream.rangeClosed(1, 10));
        Assert.assertEquals(LongStacks.mutable.ofAll(IntInterval.oneTo(10).asLazy().collectLong(i -> (long) i)), stack);
        Assert.assertEquals(LongStacks.mutable.ofAll(LongStream.rangeClosed(1, 10)), stack);
    }

    @Test
    public void toDoubleList()
    {
        MutableDoubleList list = PrimitiveStreams.mDoubleList(DoubleStream.of(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0));
        Assert.assertEquals(DoubleLists.mutable.ofAll(IntInterval.oneTo(10).asLazy().collectDouble(i -> (double) i)), list);
        Assert.assertEquals(DoubleLists.immutable.ofAll(DoubleStream.of(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)), list);
    }

    @Test
    public void toImmutableDoubleList()
    {
        ImmutableDoubleList list = PrimitiveStreams.iDoubleList(DoubleStream.of(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0));
        Assert.assertEquals(DoubleLists.mutable.ofAll(IntInterval.oneTo(10).asLazy().collectDouble(i -> (double) i)), list);
        Assert.assertEquals(DoubleLists.mutable.ofAll(DoubleStream.of(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)), list);
    }

    @Test
    public void toDoubleSet()
    {
        MutableDoubleSet set = PrimitiveStreams.mDoubleSet(DoubleStream.of(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0));
        Assert.assertEquals(DoubleSets.mutable.ofAll(IntInterval.oneTo(10).asLazy().collectDouble(i -> (double) i)), set);
        Assert.assertEquals(DoubleSets.immutable.ofAll(DoubleStream.of(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)), set);
    }

    @Test
    public void toImmutableDoubleSet()
    {
        ImmutableDoubleSet set = PrimitiveStreams.iDoubleSet(DoubleStream.of(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0));
        Assert.assertEquals(DoubleSets.mutable.ofAll(IntInterval.oneTo(10).asLazy().collectDouble(i -> (double) i)), set);
        Assert.assertEquals(DoubleSets.mutable.ofAll(DoubleStream.of(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)), set);
    }

    @Test
    public void toDoubleBag()
    {
        MutableDoubleBag bag = PrimitiveStreams.mDoubleBag(DoubleStream.of(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0));
        Assert.assertEquals(DoubleBags.mutable.ofAll(IntInterval.oneTo(10).asLazy().collectDouble(i -> (double) i)), bag);
        Assert.assertEquals(DoubleBags.immutable.ofAll(DoubleStream.of(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)), bag);
    }

    @Test
    public void toImmutableDoubleBag()
    {
        ImmutableDoubleBag bag = PrimitiveStreams.iDoubleBag(DoubleStream.of(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0));
        Assert.assertEquals(DoubleBags.mutable.ofAll(IntInterval.oneTo(10).asLazy().collectDouble(i -> (double) i)), bag);
        Assert.assertEquals(DoubleBags.mutable.ofAll(DoubleStream.of(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)), bag);
    }

    @Test
    public void toDoubleStack()
    {
        MutableDoubleStack stack = PrimitiveStreams.mDoubleStack(DoubleStream.of(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0));
        Assert.assertEquals(DoubleStacks.mutable.ofAll(IntInterval.oneTo(10).asLazy().collectDouble(i -> (double) i)), stack);
        Assert.assertEquals(DoubleStacks.immutable.ofAll(DoubleStream.of(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)), stack);
    }

    @Test
    public void toImmutableDoubleStack()
    {
        ImmutableDoubleStack stack = PrimitiveStreams.iDoubleStack(DoubleStream.of(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0));
        Assert.assertEquals(DoubleStacks.mutable.ofAll(IntInterval.oneTo(10).asLazy().collectDouble(i -> (double) i)), stack);
        Assert.assertEquals(DoubleStacks.mutable.ofAll(DoubleStream.of(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)), stack);
    }
}
