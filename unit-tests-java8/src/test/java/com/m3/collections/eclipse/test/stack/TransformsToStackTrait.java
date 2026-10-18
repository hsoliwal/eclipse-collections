/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.test.stack;

import com.m3.collections.eclipse.api.factory.Lists;
import com.m3.collections.eclipse.api.factory.Stacks;
import com.m3.collections.eclipse.api.list.MutableList;
import com.m3.collections.eclipse.api.list.primitive.MutableBooleanList;
import com.m3.collections.eclipse.api.list.primitive.MutableByteList;
import com.m3.collections.eclipse.api.list.primitive.MutableCharList;
import com.m3.collections.eclipse.api.list.primitive.MutableDoubleList;
import com.m3.collections.eclipse.api.list.primitive.MutableFloatList;
import com.m3.collections.eclipse.api.list.primitive.MutableIntList;
import com.m3.collections.eclipse.api.list.primitive.MutableLongList;
import com.m3.collections.eclipse.api.list.primitive.MutableShortList;
import com.m3.collections.eclipse.api.stack.StackIterable;
import com.m3.collections.eclipse.api.stack.primitive.BooleanStack;
import com.m3.collections.eclipse.api.stack.primitive.ByteStack;
import com.m3.collections.eclipse.api.stack.primitive.CharStack;
import com.m3.collections.eclipse.api.stack.primitive.DoubleStack;
import com.m3.collections.eclipse.api.stack.primitive.FloatStack;
import com.m3.collections.eclipse.api.stack.primitive.IntStack;
import com.m3.collections.eclipse.api.stack.primitive.LongStack;
import com.m3.collections.eclipse.api.stack.primitive.ShortStack;
import com.m3.collections.eclipse.impl.factory.primitive.BooleanLists;
import com.m3.collections.eclipse.impl.factory.primitive.BooleanStacks;
import com.m3.collections.eclipse.impl.factory.primitive.ByteLists;
import com.m3.collections.eclipse.impl.factory.primitive.ByteStacks;
import com.m3.collections.eclipse.impl.factory.primitive.CharLists;
import com.m3.collections.eclipse.impl.factory.primitive.CharStacks;
import com.m3.collections.eclipse.impl.factory.primitive.DoubleLists;
import com.m3.collections.eclipse.impl.factory.primitive.DoubleStacks;
import com.m3.collections.eclipse.impl.factory.primitive.FloatLists;
import com.m3.collections.eclipse.impl.factory.primitive.FloatStacks;
import com.m3.collections.eclipse.impl.factory.primitive.IntLists;
import com.m3.collections.eclipse.impl.factory.primitive.IntStacks;
import com.m3.collections.eclipse.impl.factory.primitive.LongLists;
import com.m3.collections.eclipse.impl.factory.primitive.LongStacks;
import com.m3.collections.eclipse.impl.factory.primitive.ShortLists;
import com.m3.collections.eclipse.impl.factory.primitive.ShortStacks;
import com.m3.collections.eclipse.impl.list.mutable.primitive.BooleanArrayList;
import com.m3.collections.eclipse.impl.list.mutable.primitive.ByteArrayList;
import com.m3.collections.eclipse.impl.list.mutable.primitive.CharArrayList;
import com.m3.collections.eclipse.impl.list.mutable.primitive.DoubleArrayList;
import com.m3.collections.eclipse.impl.list.mutable.primitive.FloatArrayList;
import com.m3.collections.eclipse.impl.list.mutable.primitive.IntArrayList;
import com.m3.collections.eclipse.impl.list.mutable.primitive.LongArrayList;
import com.m3.collections.eclipse.impl.list.mutable.primitive.ShortArrayList;
import com.m3.collections.eclipse.test.RichIterableTestCase;

public interface TransformsToStackTrait extends RichIterableTestCase
{
    @Override
    default <T> StackIterable<T> getExpectedTransformed(T... elements)
    {
        return Stacks.immutable.withReversed(elements);
    }

    @Override
    default <T> MutableList<T> newMutableForTransform(T... elements)
    {
        return Lists.mutable.with(elements);
    }

    @Override
    default MutableBooleanList newBooleanForTransform(boolean... elements)
    {
        return new BooleanArrayList(elements);
    }

    @Override
    default MutableByteList newByteForTransform(byte... elements)
    {
        return new ByteArrayList(elements);
    }

    @Override
    default MutableCharList newCharForTransform(char... elements)
    {
        return new CharArrayList(elements);
    }

    @Override
    default MutableDoubleList newDoubleForTransform(double... elements)
    {
        return new DoubleArrayList(elements);
    }

    @Override
    default MutableFloatList newFloatForTransform(float... elements)
    {
        return new FloatArrayList(elements);
    }

    @Override
    default MutableIntList newIntForTransform(int... elements)
    {
        return new IntArrayList(elements);
    }

    @Override
    default MutableLongList newLongForTransform(long... elements)
    {
        return new LongArrayList(elements);
    }

    @Override
    default MutableShortList newShortForTransform(short... elements)
    {
        return new ShortArrayList(elements);
    }

    @Override
    default BooleanStack getExpectedBoolean(boolean... elements)
    {
        return BooleanStacks.immutable.withAllReversed(BooleanLists.immutable.with(elements));
    }

    @Override
    default ByteStack getExpectedByte(byte... elements)
    {
        return ByteStacks.immutable.withAllReversed(ByteLists.immutable.with(elements));
    }

    @Override
    default CharStack getExpectedChar(char... elements)
    {
        return CharStacks.immutable.withAllReversed(CharLists.immutable.with(elements));
    }

    @Override
    default DoubleStack getExpectedDouble(double... elements)
    {
        return DoubleStacks.immutable.withAllReversed(DoubleLists.immutable.with(elements));
    }

    @Override
    default FloatStack getExpectedFloat(float... elements)
    {
        return FloatStacks.immutable.withAllReversed(FloatLists.immutable.with(elements));
    }

    @Override
    default IntStack getExpectedInt(int... elements)
    {
        return IntStacks.immutable.withAllReversed(IntLists.immutable.with(elements));
    }

    @Override
    default LongStack getExpectedLong(long... elements)
    {
        return LongStacks.immutable.withAllReversed(LongLists.immutable.with(elements));
    }

    @Override
    default ShortStack getExpectedShort(short... elements)
    {
        return ShortStacks.immutable.withAllReversed(ShortLists.immutable.with(elements));
    }
}
