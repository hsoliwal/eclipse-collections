/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.api.bag;

import com.m3.collections.eclipse.api.ParallelIterable;
import com.m3.collections.eclipse.api.annotation.Beta;
import com.m3.collections.eclipse.api.block.function.Function;
import com.m3.collections.eclipse.api.block.predicate.Predicate;
import com.m3.collections.eclipse.api.block.predicate.Predicate2;
import com.m3.collections.eclipse.api.block.procedure.primitive.ObjectIntProcedure;
import com.m3.collections.eclipse.api.multimap.bag.BagMultimap;

/**
 * @since 5.0
 */
@Beta
public interface ParallelBag<T> extends ParallelIterable<T>
{
    void forEachWithOccurrences(ObjectIntProcedure<? super T> procedure);

    /**
     * Creates a parallel iterable for selecting elements from the current iterable.
     */
    @Override
    ParallelBag<T> select(Predicate<? super T> predicate);

    @Override
    <P> ParallelBag<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Creates a parallel iterable for rejecting elements from the current iterable.
     */
    @Override
    ParallelBag<T> reject(Predicate<? super T> predicate);

    @Override
    <P> ParallelBag<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    <S> ParallelBag<S> selectInstancesOf(Class<S> clazz);

    @Override
    <V> BagMultimap<V, T> groupBy(Function<? super T, ? extends V> function);

    @Override
    <V> BagMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function);
}
