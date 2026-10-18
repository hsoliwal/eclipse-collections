/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.tuple.primitive;

import com.m3.collections.eclipse.api.tuple.primitive.BooleanBooleanPair;
import com.m3.collections.eclipse.api.tuple.primitive.BooleanBytePair;
import com.m3.collections.eclipse.api.tuple.primitive.BooleanCharPair;
import com.m3.collections.eclipse.api.tuple.primitive.BooleanDoublePair;
import com.m3.collections.eclipse.api.tuple.primitive.BooleanFloatPair;
import com.m3.collections.eclipse.api.tuple.primitive.BooleanIntPair;
import com.m3.collections.eclipse.api.tuple.primitive.BooleanLongPair;
import com.m3.collections.eclipse.api.tuple.primitive.BooleanObjectPair;
import com.m3.collections.eclipse.api.tuple.primitive.BooleanShortPair;
import com.m3.collections.eclipse.api.tuple.primitive.ByteBooleanPair;
import com.m3.collections.eclipse.api.tuple.primitive.ByteBytePair;
import com.m3.collections.eclipse.api.tuple.primitive.ByteCharPair;
import com.m3.collections.eclipse.api.tuple.primitive.ByteDoublePair;
import com.m3.collections.eclipse.api.tuple.primitive.ByteFloatPair;
import com.m3.collections.eclipse.api.tuple.primitive.ByteIntPair;
import com.m3.collections.eclipse.api.tuple.primitive.ByteLongPair;
import com.m3.collections.eclipse.api.tuple.primitive.ByteObjectPair;
import com.m3.collections.eclipse.api.tuple.primitive.ByteShortPair;
import com.m3.collections.eclipse.api.tuple.primitive.CharBooleanPair;
import com.m3.collections.eclipse.api.tuple.primitive.CharBytePair;
import com.m3.collections.eclipse.api.tuple.primitive.CharCharPair;
import com.m3.collections.eclipse.api.tuple.primitive.CharDoublePair;
import com.m3.collections.eclipse.api.tuple.primitive.CharFloatPair;
import com.m3.collections.eclipse.api.tuple.primitive.CharIntPair;
import com.m3.collections.eclipse.api.tuple.primitive.CharLongPair;
import com.m3.collections.eclipse.api.tuple.primitive.CharObjectPair;
import com.m3.collections.eclipse.api.tuple.primitive.CharShortPair;
import com.m3.collections.eclipse.api.tuple.primitive.DoubleBooleanPair;
import com.m3.collections.eclipse.api.tuple.primitive.DoubleBytePair;
import com.m3.collections.eclipse.api.tuple.primitive.DoubleCharPair;
import com.m3.collections.eclipse.api.tuple.primitive.DoubleDoublePair;
import com.m3.collections.eclipse.api.tuple.primitive.DoubleFloatPair;
import com.m3.collections.eclipse.api.tuple.primitive.DoubleIntPair;
import com.m3.collections.eclipse.api.tuple.primitive.DoubleLongPair;
import com.m3.collections.eclipse.api.tuple.primitive.DoubleObjectPair;
import com.m3.collections.eclipse.api.tuple.primitive.DoubleShortPair;
import com.m3.collections.eclipse.api.tuple.primitive.FloatBooleanPair;
import com.m3.collections.eclipse.api.tuple.primitive.FloatBytePair;
import com.m3.collections.eclipse.api.tuple.primitive.FloatCharPair;
import com.m3.collections.eclipse.api.tuple.primitive.FloatDoublePair;
import com.m3.collections.eclipse.api.tuple.primitive.FloatFloatPair;
import com.m3.collections.eclipse.api.tuple.primitive.FloatIntPair;
import com.m3.collections.eclipse.api.tuple.primitive.FloatLongPair;
import com.m3.collections.eclipse.api.tuple.primitive.FloatObjectPair;
import com.m3.collections.eclipse.api.tuple.primitive.FloatShortPair;
import com.m3.collections.eclipse.api.tuple.primitive.IntBooleanPair;
import com.m3.collections.eclipse.api.tuple.primitive.IntBytePair;
import com.m3.collections.eclipse.api.tuple.primitive.IntCharPair;
import com.m3.collections.eclipse.api.tuple.primitive.IntDoublePair;
import com.m3.collections.eclipse.api.tuple.primitive.IntFloatPair;
import com.m3.collections.eclipse.api.tuple.primitive.IntIntPair;
import com.m3.collections.eclipse.api.tuple.primitive.IntLongPair;
import com.m3.collections.eclipse.api.tuple.primitive.IntObjectPair;
import com.m3.collections.eclipse.api.tuple.primitive.IntShortPair;
import com.m3.collections.eclipse.api.tuple.primitive.LongBooleanPair;
import com.m3.collections.eclipse.api.tuple.primitive.LongBytePair;
import com.m3.collections.eclipse.api.tuple.primitive.LongCharPair;
import com.m3.collections.eclipse.api.tuple.primitive.LongDoublePair;
import com.m3.collections.eclipse.api.tuple.primitive.LongFloatPair;
import com.m3.collections.eclipse.api.tuple.primitive.LongIntPair;
import com.m3.collections.eclipse.api.tuple.primitive.LongLongPair;
import com.m3.collections.eclipse.api.tuple.primitive.LongObjectPair;
import com.m3.collections.eclipse.api.tuple.primitive.LongShortPair;
import com.m3.collections.eclipse.api.tuple.primitive.ObjectBooleanPair;
import com.m3.collections.eclipse.api.tuple.primitive.ObjectBytePair;
import com.m3.collections.eclipse.api.tuple.primitive.ObjectCharPair;
import com.m3.collections.eclipse.api.tuple.primitive.ObjectDoublePair;
import com.m3.collections.eclipse.api.tuple.primitive.ObjectFloatPair;
import com.m3.collections.eclipse.api.tuple.primitive.ObjectIntPair;
import com.m3.collections.eclipse.api.tuple.primitive.ObjectLongPair;
import com.m3.collections.eclipse.api.tuple.primitive.ObjectShortPair;
import com.m3.collections.eclipse.api.tuple.primitive.ShortBooleanPair;
import com.m3.collections.eclipse.api.tuple.primitive.ShortBytePair;
import com.m3.collections.eclipse.api.tuple.primitive.ShortCharPair;
import com.m3.collections.eclipse.api.tuple.primitive.ShortDoublePair;
import com.m3.collections.eclipse.api.tuple.primitive.ShortFloatPair;
import com.m3.collections.eclipse.api.tuple.primitive.ShortIntPair;
import com.m3.collections.eclipse.api.tuple.primitive.ShortLongPair;
import com.m3.collections.eclipse.api.tuple.primitive.ShortObjectPair;
import com.m3.collections.eclipse.api.tuple.primitive.ShortShortPair;

/**
 * This class should be used to create instances of object/primitive, primitive/primitive and primitive/object pairs.
 */
public final class PrimitiveTuples
{
    private PrimitiveTuples()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    public static <T> ObjectBooleanPair<T> pair(T one, boolean two)
    {
        return new ObjectBooleanPairImpl<>(one, two);
    }

    public static <T> ObjectBytePair<T> pair(T one, byte two)
    {
        return new ObjectBytePairImpl<>(one, two);
    }

    public static <T> ObjectCharPair<T> pair(T one, char two)
    {
        return new ObjectCharPairImpl<>(one, two);
    }

    public static <T> ObjectShortPair<T> pair(T one, short two)
    {
        return new ObjectShortPairImpl<>(one, two);
    }

    public static <T> ObjectIntPair<T> pair(T one, int two)
    {
        return new ObjectIntPairImpl<>(one, two);
    }

    public static <T> ObjectFloatPair<T> pair(T one, float two)
    {
        return new ObjectFloatPairImpl<>(one, two);
    }

    public static <T> ObjectLongPair<T> pair(T one, long two)
    {
        return new ObjectLongPairImpl<>(one, two);
    }

    public static <T> ObjectDoublePair<T> pair(T one, double two)
    {
        return new ObjectDoublePairImpl<>(one, two);
    }

    public static <T> BooleanObjectPair<T> pair(boolean one, T two)
    {
        return new BooleanObjectPairImpl<>(one, two);
    }

    public static <T> ByteObjectPair<T> pair(byte one, T two)
    {
        return new ByteObjectPairImpl<>(one, two);
    }

    public static <T> CharObjectPair<T> pair(char one, T two)
    {
        return new CharObjectPairImpl<>(one, two);
    }

    public static <T> ShortObjectPair<T> pair(short one, T two)
    {
        return new ShortObjectPairImpl<>(one, two);
    }

    public static <T> IntObjectPair<T> pair(int one, T two)
    {
        return new IntObjectPairImpl<>(one, two);
    }

    public static <T> FloatObjectPair<T> pair(float one, T two)
    {
        return new FloatObjectPairImpl<>(one, two);
    }

    public static <T> LongObjectPair<T> pair(long one, T two)
    {
        return new LongObjectPairImpl<>(one, two);
    }

    public static <T> DoubleObjectPair<T> pair(double one, T two)
    {
        return new DoubleObjectPairImpl<>(one, two);
    }

    public static IntIntPair pair(int one, int two)
    {
        return new IntIntPairImpl(one, two);
    }

    public static IntFloatPair pair(int one, float two)
    {
        return new IntFloatPairImpl(one, two);
    }

    public static IntDoublePair pair(int one, double two)
    {
        return new IntDoublePairImpl(one, two);
    }

    public static IntLongPair pair(int one, long two)
    {
        return new IntLongPairImpl(one, two);
    }

    public static IntShortPair pair(int one, short two)
    {
        return new IntShortPairImpl(one, two);
    }

    public static IntBytePair pair(int one, byte two)
    {
        return new IntBytePairImpl(one, two);
    }

    public static IntCharPair pair(int one, char two)
    {
        return new IntCharPairImpl(one, two);
    }

    public static IntBooleanPair pair(int one, boolean two)
    {
        return new IntBooleanPairImpl(one, two);
    }

    public static FloatIntPair pair(float one, int two)
    {
        return new FloatIntPairImpl(one, two);
    }

    public static FloatFloatPair pair(float one, float two)
    {
        return new FloatFloatPairImpl(one, two);
    }

    public static FloatDoublePair pair(float one, double two)
    {
        return new FloatDoublePairImpl(one, two);
    }

    public static FloatLongPair pair(float one, long two)
    {
        return new FloatLongPairImpl(one, two);
    }

    public static FloatShortPair pair(float one, short two)
    {
        return new FloatShortPairImpl(one, two);
    }

    public static FloatBytePair pair(float one, byte two)
    {
        return new FloatBytePairImpl(one, two);
    }

    public static FloatCharPair pair(float one, char two)
    {
        return new FloatCharPairImpl(one, two);
    }

    public static FloatBooleanPair pair(float one, boolean two)
    {
        return new FloatBooleanPairImpl(one, two);
    }

    public static DoubleIntPair pair(double one, int two)
    {
        return new DoubleIntPairImpl(one, two);
    }

    public static DoubleFloatPair pair(double one, float two)
    {
        return new DoubleFloatPairImpl(one, two);
    }

    public static DoubleDoublePair pair(double one, double two)
    {
        return new DoubleDoublePairImpl(one, two);
    }

    public static DoubleLongPair pair(double one, long two)
    {
        return new DoubleLongPairImpl(one, two);
    }

    public static DoubleShortPair pair(double one, short two)
    {
        return new DoubleShortPairImpl(one, two);
    }

    public static DoubleBytePair pair(double one, byte two)
    {
        return new DoubleBytePairImpl(one, two);
    }

    public static DoubleCharPair pair(double one, char two)
    {
        return new DoubleCharPairImpl(one, two);
    }

    public static DoubleBooleanPair pair(double one, boolean two)
    {
        return new DoubleBooleanPairImpl(one, two);
    }

    public static LongIntPair pair(long one, int two)
    {
        return new LongIntPairImpl(one, two);
    }

    public static LongFloatPair pair(long one, float two)
    {
        return new LongFloatPairImpl(one, two);
    }

    public static LongDoublePair pair(long one, double two)
    {
        return new LongDoublePairImpl(one, two);
    }

    public static LongLongPair pair(long one, long two)
    {
        return new LongLongPairImpl(one, two);
    }

    public static LongShortPair pair(long one, short two)
    {
        return new LongShortPairImpl(one, two);
    }

    public static LongBytePair pair(long one, byte two)
    {
        return new LongBytePairImpl(one, two);
    }

    public static LongCharPair pair(long one, char two)
    {
        return new LongCharPairImpl(one, two);
    }

    public static LongBooleanPair pair(long one, boolean two)
    {
        return new LongBooleanPairImpl(one, two);
    }

    public static ShortIntPair pair(short one, int two)
    {
        return new ShortIntPairImpl(one, two);
    }

    public static ShortFloatPair pair(short one, float two)
    {
        return new ShortFloatPairImpl(one, two);
    }

    public static ShortDoublePair pair(short one, double two)
    {
        return new ShortDoublePairImpl(one, two);
    }

    public static ShortLongPair pair(short one, long two)
    {
        return new ShortLongPairImpl(one, two);
    }

    public static ShortShortPair pair(short one, short two)
    {
        return new ShortShortPairImpl(one, two);
    }

    public static ShortBytePair pair(short one, byte two)
    {
        return new ShortBytePairImpl(one, two);
    }

    public static ShortCharPair pair(short one, char two)
    {
        return new ShortCharPairImpl(one, two);
    }

    public static ShortBooleanPair pair(short one, boolean two)
    {
        return new ShortBooleanPairImpl(one, two);
    }

    public static ByteIntPair pair(byte one, int two)
    {
        return new ByteIntPairImpl(one, two);
    }

    public static ByteFloatPair pair(byte one, float two)
    {
        return new ByteFloatPairImpl(one, two);
    }

    public static ByteDoublePair pair(byte one, double two)
    {
        return new ByteDoublePairImpl(one, two);
    }

    public static ByteLongPair pair(byte one, long two)
    {
        return new ByteLongPairImpl(one, two);
    }

    public static ByteShortPair pair(byte one, short two)
    {
        return new ByteShortPairImpl(one, two);
    }

    public static ByteBytePair pair(byte one, byte two)
    {
        return new ByteBytePairImpl(one, two);
    }

    public static ByteCharPair pair(byte one, char two)
    {
        return new ByteCharPairImpl(one, two);
    }

    public static ByteBooleanPair pair(byte one, boolean two)
    {
        return new ByteBooleanPairImpl(one, two);
    }

    public static CharIntPair pair(char one, int two)
    {
        return new CharIntPairImpl(one, two);
    }

    public static CharFloatPair pair(char one, float two)
    {
        return new CharFloatPairImpl(one, two);
    }

    public static CharDoublePair pair(char one, double two)
    {
        return new CharDoublePairImpl(one, two);
    }

    public static CharLongPair pair(char one, long two)
    {
        return new CharLongPairImpl(one, two);
    }

    public static CharShortPair pair(char one, short two)
    {
        return new CharShortPairImpl(one, two);
    }

    public static CharBytePair pair(char one, byte two)
    {
        return new CharBytePairImpl(one, two);
    }

    public static CharCharPair pair(char one, char two)
    {
        return new CharCharPairImpl(one, two);
    }

    public static CharBooleanPair pair(char one, boolean two)
    {
        return new CharBooleanPairImpl(one, two);
    }

    public static BooleanIntPair pair(boolean one, int two)
    {
        return new BooleanIntPairImpl(one, two);
    }

    public static BooleanFloatPair pair(boolean one, float two)
    {
        return new BooleanFloatPairImpl(one, two);
    }

    public static BooleanDoublePair pair(boolean one, double two)
    {
        return new BooleanDoublePairImpl(one, two);
    }

    public static BooleanLongPair pair(boolean one, long two)
    {
        return new BooleanLongPairImpl(one, two);
    }

    public static BooleanShortPair pair(boolean one, short two)
    {
        return new BooleanShortPairImpl(one, two);
    }

    public static BooleanBytePair pair(boolean one, byte two)
    {
        return new BooleanBytePairImpl(one, two);
    }

    public static BooleanCharPair pair(boolean one, char two)
    {
        return new BooleanCharPairImpl(one, two);
    }

    public static BooleanBooleanPair pair(boolean one, boolean two)
    {
        return new BooleanBooleanPairImpl(one, two);
    }
}
