/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.block.procedure;

import com.m3.collections.eclipse.api.block.function.Function;
import com.m3.collections.eclipse.api.block.function.primitive.FloatFunction;
import com.m3.collections.eclipse.api.block.procedure.Procedure;

/**
 * Implementation of {@link Procedure} that holds on to the summation of elements seen so far,
 * determined by the {@link Function}.
 */
public class SumOfFloatProcedure<T> implements Procedure<T>, DoubleSumResultHolder
{
    private static final long serialVersionUID = 2L;

    private final FloatFunction<? super T> function;
    private double result;
    private double compensation;

    public SumOfFloatProcedure(FloatFunction<? super T> function)
    {
        this.function = function;
    }

    @Override
    public double getResult()
    {
        return this.result;
    }

    @Override
    public double getCompensation()
    {
        return this.compensation;
    }

    @Override
    public void value(T each)
    {
        double adjustedValue = (double) this.function.floatValueOf(each) - this.compensation;
        double nextSum = this.result + adjustedValue;
        this.compensation = nextSum - this.result - adjustedValue;
        this.result = nextSum;
    }
}

