/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.test.list;

import com.m3.collections.eclipse.api.factory.Lists;
import com.m3.collections.eclipse.api.list.ListIterable;
import com.m3.collections.eclipse.api.list.MutableList;
import com.m3.collections.eclipse.api.list.primitive.MutableBooleanList;
import com.m3.collections.eclipse.api.list.primitive.MutableByteList;
import com.m3.collections.eclipse.api.list.primitive.MutableCharList;
import com.m3.collections.eclipse.api.list.primitive.MutableDoubleList;
import com.m3.collections.eclipse.api.list.primitive.MutableFloatList;
import com.m3.collections.eclipse.api.list.primitive.MutableIntList;
import com.m3.collections.eclipse.api.list.primitive.MutableLongList;
import com.m3.collections.eclipse.api.list.primitive.MutableShortList;
import com.m3.collections.eclipse.impl.list.mutable.primitive.BooleanArrayList;
import com.m3.collections.eclipse.impl.list.mutable.primitive.ByteArrayList;
import com.m3.collections.eclipse.impl.list.mutable.primitive.CharArrayList;
import com.m3.collections.eclipse.impl.list.mutable.primitive.DoubleArrayList;
import com.m3.collections.eclipse.impl.list.mutable.primitive.FloatArrayList;
import com.m3.collections.eclipse.impl.list.mutable.primitive.IntArrayList;
import com.m3.collections.eclipse.impl.list.mutable.primitive.LongArrayList;
import com.m3.collections.eclipse.impl.list.mutable.primitive.ShortArrayList;
import com.m3.collections.eclipse.test.RichIterableTestCase;

public interface TransformsToListTrait extends RichIterableTestCase
{
    @Override
    default <T> ListIterable<T> getExpectedTransformed(T... elements)
    {
        return Lists.immutable.with(elements);
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
}
