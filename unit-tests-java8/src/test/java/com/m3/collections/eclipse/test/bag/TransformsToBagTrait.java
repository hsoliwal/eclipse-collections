/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.test.bag;

import com.m3.collections.eclipse.api.bag.MutableBag;
import com.m3.collections.eclipse.api.bag.UnsortedBag;
import com.m3.collections.eclipse.api.bag.primitive.MutableBooleanBag;
import com.m3.collections.eclipse.api.bag.primitive.MutableByteBag;
import com.m3.collections.eclipse.api.bag.primitive.MutableCharBag;
import com.m3.collections.eclipse.api.bag.primitive.MutableDoubleBag;
import com.m3.collections.eclipse.api.bag.primitive.MutableFloatBag;
import com.m3.collections.eclipse.api.bag.primitive.MutableIntBag;
import com.m3.collections.eclipse.api.bag.primitive.MutableLongBag;
import com.m3.collections.eclipse.api.bag.primitive.MutableShortBag;
import com.m3.collections.eclipse.api.factory.Bags;
import com.m3.collections.eclipse.impl.bag.mutable.primitive.BooleanHashBag;
import com.m3.collections.eclipse.impl.bag.mutable.primitive.ByteHashBag;
import com.m3.collections.eclipse.impl.bag.mutable.primitive.CharHashBag;
import com.m3.collections.eclipse.impl.bag.mutable.primitive.DoubleHashBag;
import com.m3.collections.eclipse.impl.bag.mutable.primitive.FloatHashBag;
import com.m3.collections.eclipse.impl.bag.mutable.primitive.IntHashBag;
import com.m3.collections.eclipse.impl.bag.mutable.primitive.LongHashBag;
import com.m3.collections.eclipse.impl.bag.mutable.primitive.ShortHashBag;
import com.m3.collections.eclipse.test.RichIterableTestCase;

public interface TransformsToBagTrait extends RichIterableTestCase
{
    @Override
    default <T> UnsortedBag<T> getExpectedTransformed(T... elements)
    {
        return Bags.immutable.with(elements);
    }

    @Override
    default <T> MutableBag<T> newMutableForTransform(T... elements)
    {
        return Bags.mutable.with(elements);
    }

    @Override
    default MutableBooleanBag newBooleanForTransform(boolean... elements)
    {
        return new BooleanHashBag(elements);
    }

    @Override
    default MutableByteBag newByteForTransform(byte... elements)
    {
        return new ByteHashBag(elements);
    }

    @Override
    default MutableCharBag newCharForTransform(char... elements)
    {
        return new CharHashBag(elements);
    }

    @Override
    default MutableDoubleBag newDoubleForTransform(double... elements)
    {
        return new DoubleHashBag(elements);
    }

    @Override
    default MutableFloatBag newFloatForTransform(float... elements)
    {
        return new FloatHashBag(elements);
    }

    @Override
    default MutableIntBag newIntForTransform(int... elements)
    {
        return new IntHashBag(elements);
    }

    @Override
    default MutableLongBag newLongForTransform(long... elements)
    {
        return new LongHashBag(elements);
    }

    @Override
    default MutableShortBag newShortForTransform(short... elements)
    {
        return new ShortHashBag(elements);
    }
}
