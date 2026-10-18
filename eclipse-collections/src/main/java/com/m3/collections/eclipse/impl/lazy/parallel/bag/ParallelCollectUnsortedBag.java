/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.lazy.parallel.bag;

import java.util.concurrent.ExecutorService;

import com.m3.collections.eclipse.api.LazyIterable;
import com.m3.collections.eclipse.api.annotation.Beta;
import com.m3.collections.eclipse.api.block.function.Function;
import com.m3.collections.eclipse.api.block.predicate.Predicate;
import com.m3.collections.eclipse.api.block.procedure.Procedure;
import com.m3.collections.eclipse.api.block.procedure.primitive.ObjectIntProcedure;
import com.m3.collections.eclipse.impl.block.factory.Functions;
import com.m3.collections.eclipse.impl.block.factory.Predicates;

@Beta
public class ParallelCollectUnsortedBag<T, V> extends AbstractParallelUnsortedBag<V, UnsortedBagBatch<V>>
{
    private final AbstractParallelUnsortedBag<T, ? extends UnsortedBagBatch<T>> parallelIterable;
    private final Function<? super T, ? extends V> function;

    public ParallelCollectUnsortedBag(AbstractParallelUnsortedBag<T, ? extends UnsortedBagBatch<T>> parallelIterable, Function<? super T, ? extends V> function)
    {
        this.parallelIterable = parallelIterable;
        this.function = function;
    }

    @Override
    public ExecutorService getExecutorService()
    {
        return this.parallelIterable.getExecutorService();
    }

    @Override
    public int getBatchSize()
    {
        return this.parallelIterable.getBatchSize();
    }

    @Override
    public LazyIterable<UnsortedBagBatch<V>> split()
    {
        return this.parallelIterable.split().collect(eachBatch -> eachBatch.collect(this.function));
    }

    @Override
    public void forEach(Procedure<? super V> procedure)
    {
        this.parallelIterable.forEach(Functions.bind(procedure, this.function));
    }

    @Override
    public void forEachWithOccurrences(ObjectIntProcedure<? super V> procedure)
    {
        this.parallelIterable.forEachWithOccurrences((each, parameter) -> procedure.value(this.function.valueOf(each), parameter));
    }

    @Override
    public boolean anySatisfy(Predicate<? super V> predicate)
    {
        return this.parallelIterable.anySatisfy(Predicates.attributePredicate(this.function, predicate));
    }

    @Override
    public boolean allSatisfy(Predicate<? super V> predicate)
    {
        return this.parallelIterable.allSatisfy(Predicates.attributePredicate(this.function, predicate));
    }

    @Override
    public V detect(Predicate<? super V> predicate)
    {
        T resultItem = this.parallelIterable.detect(Predicates.attributePredicate(this.function, predicate));
        return resultItem == null ? null : this.function.valueOf(resultItem);
    }
}
