/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.block.function;

import com.m3.collections.eclipse.impl.block.factory.PrimitiveFunctions;
import com.m3.collections.eclipse.impl.set.mutable.UnifiedSet;
import com.m3.collections.eclipse.impl.set.mutable.primitive.DoubleHashSet;
import com.m3.collections.eclipse.impl.set.mutable.primitive.FloatHashSet;
import com.m3.collections.eclipse.impl.set.mutable.primitive.IntHashSet;
import com.m3.collections.eclipse.impl.set.mutable.primitive.LongHashSet;
import com.m3.collections.eclipse.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Junit test for {@link PrimitiveFunctions}.
 */
public class PrimitiveFunctionsTest
{
    @Test
    public void unboxNumberToInt()
    {
        assertEquals(
                IntHashSet.newSetWith(1, 2, 3),
                UnifiedSet.newSetWith(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3)).collectInt(PrimitiveFunctions.unboxNumberToInt()));

        assertEquals(
                IntHashSet.newSetWith(1, 2, 3),
                UnifiedSet.newSetWith(1.1, 2.2, 3.3).collectInt(PrimitiveFunctions.unboxNumberToInt()));
    }

    @Test
    public void unboxNumberToFloat()
    {
        assertEquals(
                FloatHashSet.newSetWith(1.0f, 2.0f, 3.0f),
                UnifiedSet.newSetWith(1, 2, 3).collectFloat(PrimitiveFunctions.unboxNumberToFloat()));
    }

    @Test
    public void unboxNumberToLong()
    {
        assertEquals(
                LongHashSet.newSetWith(1L, 2L, 3L),
                UnifiedSet.newSetWith(1, 2, 3).collectLong(PrimitiveFunctions.unboxNumberToLong()));
    }

    @Test
    public void unboxNumberToDouble()
    {
        assertEquals(
                DoubleHashSet.newSetWith(1.0, 2.0, 3.0),
                UnifiedSet.newSetWith(1, 2, 3).collectDouble(PrimitiveFunctions.unboxNumberToDouble()));
    }

    @Test
    public void unboxDoubleToDouble()
    {
        assertEquals(
                DoubleHashSet.newSetWith(1.0, 2.0, 3.0),
                UnifiedSet.newSetWith(Double.valueOf(1.0), Double.valueOf(2.0), Double.valueOf(3.0)).collectDouble(PrimitiveFunctions.unboxDoubleToDouble()));
    }

    @Test
    public void unboxFloatToFloat()
    {
        assertEquals(
                FloatHashSet.newSetWith(1.0f, 2.0f, 3.0f),
                UnifiedSet.newSetWith(Float.valueOf(1.0f), Float.valueOf(2.0f), Float.valueOf(3.0f)).collectFloat(PrimitiveFunctions.unboxFloatToFloat()));
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(PrimitiveFunctions.class);
    }
}
