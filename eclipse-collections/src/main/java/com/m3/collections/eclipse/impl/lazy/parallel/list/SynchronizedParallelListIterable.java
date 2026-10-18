/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.lazy.parallel.list;

import com.m3.collections.eclipse.api.annotation.Beta;
import com.m3.collections.eclipse.api.block.function.Function;
import com.m3.collections.eclipse.api.block.function.Function2;
import com.m3.collections.eclipse.api.block.predicate.Predicate;
import com.m3.collections.eclipse.api.block.predicate.Predicate2;
import com.m3.collections.eclipse.api.list.ParallelListIterable;
import com.m3.collections.eclipse.api.multimap.list.ListMultimap;
import com.m3.collections.eclipse.api.set.ParallelUnsortedSetIterable;
import com.m3.collections.eclipse.impl.lazy.parallel.AbstractSynchronizedParallelIterable;

@Beta
public final class SynchronizedParallelListIterable<T> extends AbstractSynchronizedParallelIterable<T, ParallelListIterable<T>> implements ParallelListIterable<T>
{
    public SynchronizedParallelListIterable(ParallelListIterable<T> delegate, Object lock)
    {
        super(delegate, lock);
    }

    @Override
    public ParallelUnsortedSetIterable<T> asUnique()
    {
        return this.wrap(this.delegate.asUnique());
    }

    @Override
    public ParallelListIterable<T> select(Predicate<? super T> predicate)
    {
        return this.wrap(this.delegate.select(predicate));
    }

    @Override
    public <P> ParallelListIterable<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.wrap(this.delegate.selectWith(predicate, parameter));
    }

    @Override
    public ParallelListIterable<T> reject(Predicate<? super T> predicate)
    {
        return this.wrap(this.delegate.reject(predicate));
    }

    @Override
    public <P> ParallelListIterable<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.wrap(this.delegate.rejectWith(predicate, parameter));
    }

    @Override
    public <S> ParallelListIterable<S> selectInstancesOf(Class<S> clazz)
    {
        return this.wrap(this.delegate.selectInstancesOf(clazz));
    }

    @Override
    public <V> ParallelListIterable<V> collect(Function<? super T, ? extends V> function)
    {
        return this.wrap(this.delegate.collect(function));
    }

    @Override
    public <P, V> ParallelListIterable<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return this.wrap(this.delegate.collectWith(function, parameter));
    }

    @Override
    public <V> ParallelListIterable<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function)
    {
        return this.wrap(this.delegate.collectIf(predicate, function));
    }

    @Override
    public <V> ParallelListIterable<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.wrap(this.delegate.flatCollect(function));
    }

    @Override
    public <V> ListMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.groupBy(function);
        }
    }

    @Override
    public <V> ListMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        synchronized (this.lock)
        {
            return this.delegate.groupByEach(function);
        }
    }
}
