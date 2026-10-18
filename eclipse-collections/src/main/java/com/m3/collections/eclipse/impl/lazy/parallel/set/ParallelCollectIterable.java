/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.lazy.parallel.set;

import java.util.concurrent.ExecutorService;

import com.m3.collections.eclipse.api.LazyIterable;
import com.m3.collections.eclipse.api.annotation.Beta;
import com.m3.collections.eclipse.api.bag.MutableBag;
import com.m3.collections.eclipse.api.block.function.Function;
import com.m3.collections.eclipse.api.block.predicate.Predicate;
import com.m3.collections.eclipse.api.block.procedure.Procedure;
import com.m3.collections.eclipse.api.map.MapIterable;
import com.m3.collections.eclipse.api.multimap.bag.UnsortedBagMultimap;
import com.m3.collections.eclipse.impl.block.factory.Functions;
import com.m3.collections.eclipse.impl.block.factory.Predicates;
import com.m3.collections.eclipse.impl.lazy.parallel.AbstractParallelIterable;
import com.m3.collections.eclipse.impl.lazy.parallel.AbstractParallelIterableImpl;
import com.m3.collections.eclipse.impl.lazy.parallel.Batch;

@Beta
public class ParallelCollectIterable<T, V> extends AbstractParallelIterableImpl<V, Batch<V>>
{
    private final AbstractParallelIterable<T, ? extends Batch<T>> delegate;
    private final Function<? super T, ? extends V> function;

    public ParallelCollectIterable(AbstractParallelIterable<T, ? extends Batch<T>> delegate, Function<? super T, ? extends V> function)
    {
        this.delegate = delegate;
        this.function = function;
    }

    @Override
    public ExecutorService getExecutorService()
    {
        return this.delegate.getExecutorService();
    }

    @Override
    public int getBatchSize()
    {
        return this.delegate.getBatchSize();
    }

    @Override
    public LazyIterable<Batch<V>> split()
    {
        return this.delegate.split().collect(eachBatch -> eachBatch.collect(this.function));
    }

    @Override
    public void forEach(Procedure<? super V> procedure)
    {
        this.delegate.forEach(Functions.bind(procedure, this.function));
    }

    @Override
    public boolean anySatisfy(Predicate<? super V> predicate)
    {
        return this.delegate.anySatisfy(Predicates.attributePredicate(this.function, predicate));
    }

    @Override
    public boolean allSatisfy(Predicate<? super V> predicate)
    {
        return this.delegate.allSatisfy(Predicates.attributePredicate(this.function, predicate));
    }

    @Override
    public V detect(Predicate<? super V> predicate)
    {
        T resultItem = this.delegate.detect(Predicates.attributePredicate(this.function, predicate));
        return resultItem == null ? null : this.function.valueOf(resultItem);
    }

    @Override
    public Object[] toArray()
    {
        // TODO: Implement in parallel
        return this.delegate.toList().collect(this.function).toArray();
    }

    @Override
    public <E> E[] toArray(E[] array)
    {
        // TODO: Implement in parallel
        return this.delegate.toList().collect(this.function).toArray(array);
    }

    @Override
    public <V1> UnsortedBagMultimap<V1, V> groupBy(Function<? super V, ? extends V1> function)
    {
        // TODO: Implement in parallel
        MutableBag<V> mutableBag = this.delegate.toBag().collect(this.function);
        return mutableBag.groupBy(function);
    }

    @Override
    public <V1> UnsortedBagMultimap<V1, V> groupByEach(Function<? super V, ? extends Iterable<V1>> function)
    {
        // TODO: Implement in parallel
        MutableBag<V> mutableBag = this.delegate.toBag().collect(this.function);
        return mutableBag.groupByEach(function);
    }

    @Override
    public <V1> MapIterable<V1, V> groupByUniqueKey(Function<? super V, ? extends V1> function)
    {
        // TODO: Implement in parallel
        MutableBag<V> mutableBag = this.delegate.toBag().collect(this.function);
        return mutableBag.groupByUniqueKey(function);
    }
}
