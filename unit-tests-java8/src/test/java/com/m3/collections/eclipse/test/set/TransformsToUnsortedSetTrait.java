/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.test.set;

import com.m3.collections.eclipse.api.factory.Sets;
import com.m3.collections.eclipse.api.set.MutableSet;
import com.m3.collections.eclipse.api.set.UnsortedSetIterable;
import com.m3.collections.eclipse.api.set.primitive.MutableBooleanSet;
import com.m3.collections.eclipse.api.set.primitive.MutableByteSet;
import com.m3.collections.eclipse.api.set.primitive.MutableCharSet;
import com.m3.collections.eclipse.api.set.primitive.MutableDoubleSet;
import com.m3.collections.eclipse.api.set.primitive.MutableFloatSet;
import com.m3.collections.eclipse.api.set.primitive.MutableIntSet;
import com.m3.collections.eclipse.api.set.primitive.MutableLongSet;
import com.m3.collections.eclipse.api.set.primitive.MutableShortSet;
import com.m3.collections.eclipse.impl.set.mutable.primitive.BooleanHashSet;
import com.m3.collections.eclipse.impl.set.mutable.primitive.ByteHashSet;
import com.m3.collections.eclipse.impl.set.mutable.primitive.CharHashSet;
import com.m3.collections.eclipse.impl.set.mutable.primitive.DoubleHashSet;
import com.m3.collections.eclipse.impl.set.mutable.primitive.FloatHashSet;
import com.m3.collections.eclipse.impl.set.mutable.primitive.IntHashSet;
import com.m3.collections.eclipse.impl.set.mutable.primitive.LongHashSet;
import com.m3.collections.eclipse.impl.set.mutable.primitive.ShortHashSet;
import com.m3.collections.eclipse.test.RichIterableTestCase;

// TODO Collected sets should return bags
public interface TransformsToUnsortedSetTrait extends RichIterableTestCase
{
    @Override
    default <T> UnsortedSetIterable<T> getExpectedTransformed(T... elements)
    {
        return Sets.immutable.with(elements);
    }

    @Override
    default <T> MutableSet<T> newMutableForTransform(T... elements)
    {
        return Sets.mutable.with(elements);
    }

    @Override
    default MutableBooleanSet newBooleanForTransform(boolean... elements)
    {
        return new BooleanHashSet(elements);
    }

    @Override
    default MutableByteSet newByteForTransform(byte... elements)
    {
        return new ByteHashSet(elements);
    }

    @Override
    default MutableCharSet newCharForTransform(char... elements)
    {
        return new CharHashSet(elements);
    }

    @Override
    default MutableDoubleSet newDoubleForTransform(double... elements)
    {
        return new DoubleHashSet(elements);
    }

    @Override
    default MutableFloatSet newFloatForTransform(float... elements)
    {
        return new FloatHashSet(elements);
    }

    @Override
    default MutableIntSet newIntForTransform(int... elements)
    {
        return new IntHashSet(elements);
    }

    @Override
    default MutableLongSet newLongForTransform(long... elements)
    {
        return new LongHashSet(elements);
    }

    @Override
    default MutableShortSet newShortForTransform(short... elements)
    {
        return new ShortHashSet(elements);
    }
}
