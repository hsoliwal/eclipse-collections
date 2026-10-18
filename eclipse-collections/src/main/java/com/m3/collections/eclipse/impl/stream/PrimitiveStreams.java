/*
 * Copyright (c) 2022 The Bank of New York Mellon.
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
import com.m3.collections.eclipse.api.factory.primitive.DoubleBags;
import com.m3.collections.eclipse.api.factory.primitive.DoubleLists;
import com.m3.collections.eclipse.api.factory.primitive.DoubleSets;
import com.m3.collections.eclipse.api.factory.primitive.DoubleStacks;
import com.m3.collections.eclipse.api.factory.primitive.IntBags;
import com.m3.collections.eclipse.api.factory.primitive.IntLists;
import com.m3.collections.eclipse.api.factory.primitive.IntSets;
import com.m3.collections.eclipse.api.factory.primitive.IntStacks;
import com.m3.collections.eclipse.api.factory.primitive.LongBags;
import com.m3.collections.eclipse.api.factory.primitive.LongLists;
import com.m3.collections.eclipse.api.factory.primitive.LongSets;
import com.m3.collections.eclipse.api.factory.primitive.LongStacks;
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

/**
 * This class provides interop between primitive streams and primitive collections.
 *
 * @since 9.0
 */
public final class PrimitiveStreams
{
    private PrimitiveStreams()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    public static MutableIntList mIntList(IntStream stream)
    {
        return IntLists.mutable.withAll(stream);
    }

    public static ImmutableIntList iIntList(IntStream stream)
    {
        return IntLists.immutable.withAll(stream);
    }

    public static MutableIntSet mIntSet(IntStream stream)
    {
        return IntSets.mutable.withAll(stream);
    }

    public static ImmutableIntSet iIntSet(IntStream stream)
    {
        return IntSets.immutable.withAll(stream);
    }

    public static MutableIntBag mIntBag(IntStream stream)
    {
        return IntBags.mutable.withAll(stream);
    }

    public static ImmutableIntBag iIntBag(IntStream stream)
    {
        return IntBags.immutable.withAll(stream);
    }

    public static MutableIntStack mIntStack(IntStream stream)
    {
        return IntStacks.mutable.withAll(stream);
    }

    public static ImmutableIntStack iIntStack(IntStream stream)
    {
        return IntStacks.immutable.withAll(stream);
    }

    public static MutableLongList mLongList(LongStream stream)
    {
        return LongLists.mutable.withAll(stream);
    }

    public static ImmutableLongList iLongList(LongStream stream)
    {
        return LongLists.immutable.withAll(stream);
    }

    public static MutableLongSet mLongSet(LongStream stream)
    {
        return LongSets.mutable.withAll(stream);
    }

    public static ImmutableLongSet iLongSet(LongStream stream)
    {
        return LongSets.immutable.withAll(stream);
    }

    public static MutableLongBag mLongBag(LongStream stream)
    {
        return LongBags.mutable.withAll(stream);
    }

    public static ImmutableLongBag iLongBag(LongStream stream)
    {
        return LongBags.immutable.withAll(stream);
    }

    public static MutableLongStack mLongStack(LongStream stream)
    {
        return LongStacks.mutable.withAll(stream);
    }

    public static ImmutableLongStack iLongStack(LongStream stream)
    {
        return LongStacks.immutable.withAll(stream);
    }

    public static MutableDoubleList mDoubleList(DoubleStream stream)
    {
        return DoubleLists.mutable.withAll(stream);
    }

    public static ImmutableDoubleList iDoubleList(DoubleStream stream)
    {
        return DoubleLists.immutable.withAll(stream);
    }

    public static MutableDoubleSet mDoubleSet(DoubleStream stream)
    {
        return DoubleSets.mutable.withAll(stream);
    }

    public static ImmutableDoubleSet iDoubleSet(DoubleStream stream)
    {
        return DoubleSets.immutable.withAll(stream);
    }

    public static MutableDoubleBag mDoubleBag(DoubleStream stream)
    {
        return DoubleBags.mutable.withAll(stream);
    }

    public static ImmutableDoubleBag iDoubleBag(DoubleStream stream)
    {
        return DoubleBags.immutable.withAll(stream);
    }

    public static MutableDoubleStack mDoubleStack(DoubleStream stream)
    {
        return DoubleStacks.mutable.withAll(stream);
    }

    public static ImmutableDoubleStack iDoubleStack(DoubleStream stream)
    {
        return DoubleStacks.immutable.withAll(stream);
    }
}
