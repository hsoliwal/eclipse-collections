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
import com.m3.collections.eclipse.impl.lazy.parallel.AbstractBatch;
import com.m3.collections.eclipse.impl.lazy.parallel.list.CollectListBatch;
import com.m3.collections.eclipse.impl.lazy.parallel.list.DistinctBatch;
import com.m3.collections.eclipse.impl.lazy.parallel.list.FlatCollectListBatch;
import com.m3.collections.eclipse.impl.lazy.parallel.list.ListBatch;
import com.m3.collections.eclipse.impl.lazy.parallel.list.SelectListBatch;
import com.m3.collections.eclipse.impl.lazy.parallel.set.UnsortedSetBatch;
import com.m3.collections.eclipse.impl.map.mutable.ConcurrentHashMap;
import com.m3.collections.eclipse.impl.utility.Iterate;

@Beta
public class FlatCollectSortedSetBatch<T, V> extends AbstractBatch<V> implements ListBatch<V>
{
    private final SortedSetBatch<T> sortedSetBatch;
    private final Function<? super T, ? extends Iterable<V>> function;

    public FlatCollectSortedSetBatch(SortedSetBatch<T> sortedSetBatch, Function<? super T, ? extends Iterable<V>> function)
    {
        this.sortedSetBatch = sortedSetBatch;
        this.function = function;
    }

    @Override
    public void forEach(Procedure<? super V> procedure)
    {
        this.sortedSetBatch.forEach(each -> Iterate.forEach(this.function.valueOf(each), procedure));
    }

    @Override
    public ListBatch<V> select(Predicate<? super V> predicate)
    {
        return new SelectListBatch<>(this, predicate);
    }

    @Override
    public <VV> ListBatch<VV> collect(Function<? super V, ? extends VV> function)
    {
        return new CollectListBatch<>(this, function);
    }

    @Override
    public <VV> ListBatch<VV> flatCollect(Function<? super V, ? extends Iterable<VV>> function)
    {
        return new FlatCollectListBatch<>(this, function);
    }

    @Override
    public UnsortedSetBatch<V> distinct(ConcurrentHashMap<V, Boolean> distinct)
    {
        return new DistinctBatch<>(this, distinct);
    }
}
