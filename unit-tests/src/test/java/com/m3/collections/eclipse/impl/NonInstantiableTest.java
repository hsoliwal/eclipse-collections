/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl;

import com.m3.collections.eclipse.impl.block.factory.PrimitiveFunctions;
import com.m3.collections.eclipse.impl.block.factory.primitive.CharToCharFunctions;
import com.m3.collections.eclipse.impl.block.factory.primitive.IntToIntFunctions;
import com.m3.collections.eclipse.impl.block.function.MaxFunction;
import com.m3.collections.eclipse.impl.block.function.MaxSizeFunction;
import com.m3.collections.eclipse.impl.block.function.MinFunction;
import com.m3.collections.eclipse.impl.block.function.MinSizeFunction;
import com.m3.collections.eclipse.impl.block.function.SubtractFunction;
import com.m3.collections.eclipse.impl.factory.SortedMaps;
import com.m3.collections.eclipse.impl.factory.primitive.BooleanSets;
import com.m3.collections.eclipse.impl.factory.primitive.BooleanStacks;
import com.m3.collections.eclipse.impl.factory.primitive.ByteBooleanMaps;
import com.m3.collections.eclipse.impl.factory.primitive.CharBooleanMaps;
import com.m3.collections.eclipse.impl.factory.primitive.DoubleBooleanMaps;
import com.m3.collections.eclipse.impl.factory.primitive.FloatBooleanMaps;
import com.m3.collections.eclipse.impl.factory.primitive.IntBooleanMaps;
import com.m3.collections.eclipse.impl.factory.primitive.LongBooleanMaps;
import com.m3.collections.eclipse.impl.factory.primitive.ObjectBooleanMaps;
import com.m3.collections.eclipse.impl.factory.primitive.ShortBooleanMaps;
import com.m3.collections.eclipse.impl.parallel.Combiners;
import com.m3.collections.eclipse.impl.test.Verify;
import com.m3.collections.eclipse.impl.tuple.primitive.PrimitiveTuples;
import com.m3.collections.eclipse.impl.utility.internal.InternalArrayIterate;
import com.m3.collections.eclipse.impl.utility.internal.IteratorIterate;
import com.m3.collections.eclipse.impl.utility.internal.MutableCollectionIterate;
import com.m3.collections.eclipse.impl.utility.internal.ReflectionHelper;
import com.m3.collections.eclipse.impl.utility.internal.SetIterables;
import com.m3.collections.eclipse.impl.utility.internal.SetIterate;
import com.m3.collections.eclipse.impl.utility.internal.SortedSetIterables;
import com.m3.collections.eclipse.impl.utility.internal.primitive.BooleanIterableIterate;
import com.m3.collections.eclipse.impl.utility.internal.primitive.BooleanIteratorIterate;
import com.m3.collections.eclipse.impl.utility.primitive.LazyBooleanIterate;
import org.junit.Test;

public class NonInstantiableTest
{
    // TODO: Move the test for each of these classes into appropriate generated test classes
    private static final Class<?>[] GENERATED_NON_INSTANTIABLE_CLASSES = {
            BooleanStacks.class,
            ByteBooleanMaps.class,
            CharBooleanMaps.class,
            DoubleBooleanMaps.class,
            FloatBooleanMaps.class,
            IntBooleanMaps.class,
            LongBooleanMaps.class,
            ShortBooleanMaps.class,
            ObjectBooleanMaps.class,
            BooleanIterableIterate.class,
            BooleanIteratorIterate.class,
            LazyBooleanIterate.class,
    };

    // With no dedicated test class
    private static final Class<?>[] HAND_CODED_NON_INSTANTIABLE_CLASSES = {
            PrimitiveFunctions.class,

            CharToCharFunctions.class,
            IntToIntFunctions.class,

            MaxFunction.class,
            MaxSizeFunction.class,
            MinFunction.class,
            MinSizeFunction.class,
            SubtractFunction.class,

            BooleanSets.class,

            Combiners.class,

            PrimitiveTuples.class,

            InternalArrayIterate.class,
            IteratorIterate.class,
            MutableCollectionIterate.class,
            ReflectionHelper.class,
            SetIterables.class,
            SetIterate.class,
            SortedMaps.class,
            SortedSetIterables.class,
    };

    @Test
    public void generatedNonInstantiableClassesThrow()
    {
        for (Class<?> aClass : GENERATED_NON_INSTANTIABLE_CLASSES)
        {
            Verify.assertClassNonInstantiable(aClass);
        }
    }

    @Test
    public void handCodedNonInstantiableClassesThrow()
    {
        for (Class<?> aClass : HAND_CODED_NON_INSTANTIABLE_CLASSES)
        {
            Verify.assertClassNonInstantiable(aClass);
        }
    }
}
