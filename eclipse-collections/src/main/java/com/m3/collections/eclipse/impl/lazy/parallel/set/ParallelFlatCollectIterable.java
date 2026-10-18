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
import java.util.concurrent.atomic.AtomicReference;

import com.m3.collections.eclipse.api.LazyIterable;
import com.m3.collections.eclipse.api.annotation.Beta;
import com.m3.collections.eclipse.api.block.function.Function;
import com.m3.collections.eclipse.api.block.predicate.Predicate;
import com.m3.collections.eclipse.api.block.procedure.Procedure;
import com.m3.collections.eclipse.api.map.MapIterable;
import com.m3.collections.eclipse.api.multimap.bag.UnsortedBagMultimap;
import com.m3.collections.eclipse.impl.lazy.parallel.AbstractParallelIterable;
import com.m3.collections.eclipse.impl.lazy.parallel.AbstractParallelIterableImpl;
import com.m3.collections.eclipse.impl.lazy.parallel.Batch;
import com.m3.collections.eclipse.impl.utility.Iterate;

@Beta
public class ParallelFlatCollectIterable<T, V> extends AbstractParallelIterableImpl<V, Batch<V>>
{
    private final AbstractParallelIterable<T, ? extends Batch<T>> delegate;
    private final Function<? super T, ? extends Iterable<V>> function;

    public ParallelFlatCollectIterable(AbstractParallelIterable<T, ? extends Batch<T>> delegate, Function<? super T, ? extends Iterable<V>> function)
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
        return this.delegate.split().collect(batch -> batch.flatCollect(this.function));
    }

    @Override
    public void forEach(Procedure<? super V> procedure)
    {
        this.delegate.forEach(each -> Iterate.forEach(this.function.valueOf(each), procedure));
    }

    @Override
    public V detect(Predicate<? super V> predicate)
    {
        AtomicReference<V> result = new AtomicReference<>();
        this.delegate.anySatisfy(each -> Iterate.anySatisfy(this.function.valueOf(each), each1 -> {
            if (predicate.accept(each1))
            {
                result.compareAndSet(null, each1);
                return true;
            }

            return false;
        }));

        return result.get();
    }

    @Override
    public boolean anySatisfy(Predicate<? super V> predicate)
    {
        return this.delegate.anySatisfy(each -> Iterate.anySatisfy(this.function.valueOf(each), predicate));
    }

    @Override
    public boolean allSatisfy(Predicate<? super V> predicate)
    {
        return this.delegate.allSatisfy(each -> Iterate.allSatisfy(this.function.valueOf(each), predicate));
    }

    @Override
    public Object[] toArray()
    {
        // TODO: Implement in parallel
        return this.delegate.toList().flatCollect(this.function).toArray();
    }

    @Override
    public <E> E[] toArray(E[] array)
    {
        // TODO: Implement in parallel
        return this.delegate.toList().flatCollect(this.function).toArray(array);
    }

    @Override
    public <V1> UnsortedBagMultimap<V1, V> groupBy(Function<? super V, ? extends V1> function)
    {
        // TODO: Implement in parallel
        return this.delegate.toBag().flatCollect(this.function).groupBy(function);
    }

    @Override
    public <V1> UnsortedBagMultimap<V1, V> groupByEach(Function<? super V, ? extends Iterable<V1>> function)
    {
        // TODO: Implement in parallel
        return this.delegate.toBag().flatCollect(this.function).groupByEach(function);
    }

    @Override
    public <V1> MapIterable<V1, V> groupByUniqueKey(Function<? super V, ? extends V1> function)
    {
        // TODO: Implement in parallel
        return this.delegate.toBag().flatCollect(this.function).groupByUniqueKey(function);
    }
}
