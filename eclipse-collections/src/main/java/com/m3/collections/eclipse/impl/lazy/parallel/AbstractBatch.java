/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.lazy.parallel;

import java.util.Comparator;

import com.m3.collections.eclipse.api.block.function.Function;
import com.m3.collections.eclipse.api.block.function.primitive.DoubleFunction;
import com.m3.collections.eclipse.api.block.function.primitive.FloatFunction;
import com.m3.collections.eclipse.api.block.function.primitive.IntFunction;
import com.m3.collections.eclipse.api.block.function.primitive.LongFunction;
import com.m3.collections.eclipse.api.block.predicate.Predicate;
import com.m3.collections.eclipse.impl.block.procedure.CountProcedure;
import com.m3.collections.eclipse.impl.block.procedure.DoubleSumResultHolder;
import com.m3.collections.eclipse.impl.block.procedure.MaxByProcedure;
import com.m3.collections.eclipse.impl.block.procedure.MaxComparatorProcedure;
import com.m3.collections.eclipse.impl.block.procedure.MinByProcedure;
import com.m3.collections.eclipse.impl.block.procedure.MinComparatorProcedure;
import com.m3.collections.eclipse.impl.block.procedure.SumOfDoubleProcedure;
import com.m3.collections.eclipse.impl.block.procedure.SumOfFloatProcedure;
import com.m3.collections.eclipse.impl.block.procedure.SumOfIntProcedure;
import com.m3.collections.eclipse.impl.block.procedure.SumOfLongProcedure;

public abstract class AbstractBatch<T> implements Batch<T>
{
    @Override
    public int count(Predicate<? super T> predicate)
    {
        CountProcedure<T> procedure = new CountProcedure<>(predicate);
        this.forEach(procedure);
        return procedure.getCount();
    }

    @Override
    public String makeString(String separator)
    {
        StringBuilder stringBuilder = new StringBuilder();
        this.forEach(each -> {
            if (stringBuilder.length() != 0)
            {
                stringBuilder.append(separator);
            }
            stringBuilder.append(each);
        });
        return stringBuilder.toString();
    }

    @Override
    public T min(Comparator<? super T> comparator)
    {
        MinComparatorProcedure<T> procedure = new MinComparatorProcedure<>(comparator);
        this.forEach(procedure);
        return procedure.isVisitedAtLeastOnce() ? procedure.getResult() : null;
    }

    @Override
    public T max(Comparator<? super T> comparator)
    {
        MaxComparatorProcedure<T> procedure = new MaxComparatorProcedure<>(comparator);
        this.forEach(procedure);
        return procedure.isVisitedAtLeastOnce() ? procedure.getResult() : null;
    }

    @Override
    public <V extends Comparable<? super V>> T minBy(Function<? super T, ? extends V> function)
    {
        MinByProcedure<T, V> procedure = new MinByProcedure<>(function);
        this.forEach(procedure);
        return procedure.isVisitedAtLeastOnce() ? procedure.getResult() : null;
    }

    @Override
    public <V extends Comparable<? super V>> T maxBy(Function<? super T, ? extends V> function)
    {
        MaxByProcedure<T, V> procedure = new MaxByProcedure<>(function);
        this.forEach(procedure);
        return procedure.isVisitedAtLeastOnce() ? procedure.getResult() : null;
    }

    @Override
    public long sumOfInt(IntFunction<? super T> function)
    {
        SumOfIntProcedure<T> procedure = new SumOfIntProcedure<>(function);
        this.forEach(procedure);
        return procedure.getResult();
    }

    @Override
    public DoubleSumResultHolder sumOfFloat(FloatFunction<? super T> function)
    {
        SumOfFloatProcedure<T> procedure = new SumOfFloatProcedure<>(function);
        this.forEach(procedure);
        return procedure;
    }

    @Override
    public long sumOfLong(LongFunction<? super T> function)
    {
        SumOfLongProcedure<T> procedure = new SumOfLongProcedure<>(function);
        this.forEach(procedure);
        return procedure.getResult();
    }

    @Override
    public DoubleSumResultHolder sumOfDouble(DoubleFunction<? super T> function)
    {
        SumOfDoubleProcedure<T> procedure = new SumOfDoubleProcedure<>(function);
        this.forEach(procedure);
        return procedure;
    }
}
