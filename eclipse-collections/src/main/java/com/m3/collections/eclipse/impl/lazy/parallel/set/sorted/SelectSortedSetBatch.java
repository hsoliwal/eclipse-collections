/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.lazy.parallel.set.sorted;

import com.m3.collections.eclipse.api.annotation.Beta;
import com.m3.collections.eclipse.api.block.function.Function;
import com.m3.collections.eclipse.api.block.predicate.Predicate;
import com.m3.collections.eclipse.api.block.procedure.Procedure;
import com.m3.collections.eclipse.impl.block.procedure.IfProcedure;
import com.m3.collections.eclipse.impl.lazy.parallel.AbstractBatch;
import com.m3.collections.eclipse.impl.lazy.parallel.list.ListBatch;
import com.m3.collections.eclipse.impl.map.mutable.ConcurrentHashMap;

@Beta
public class SelectSortedSetBatch<T> extends AbstractBatch<T> implements SortedSetBatch<T>
{
    private final SortedSetBatch<T> sortedSetBatch;
    private final Predicate<? super T> predicate;

    public SelectSortedSetBatch(SortedSetBatch<T> sortedSetBatch, Predicate<? super T> predicate)
    {
        this.sortedSetBatch = sortedSetBatch;
        this.predicate = predicate;
    }

    @Override
    public void forEach(Procedure<? super T> procedure)
    {
        this.sortedSetBatch.forEach(new IfProcedure<>(this.predicate, procedure));
    }

    @Override
    public SortedSetBatch<T> select(Predicate<? super T> predicate)
    {
        return new SelectSortedSetBatch<>(this, predicate);
    }

    @Override
    public <V> ListBatch<V> collect(Function<? super T, ? extends V> function)
    {
        return new CollectSortedSetBatch<>(this, function);
    }

    @Override
    public <V> ListBatch<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return new FlatCollectSortedSetBatch<>(this, function);
    }

    @Override
    public SortedSetBatch<T> distinct(ConcurrentHashMap<T, Boolean> distinct)
    {
        return this;
    }
}
