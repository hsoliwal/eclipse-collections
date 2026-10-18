/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.api.set;

import java.util.concurrent.ExecutorService;

import com.m3.collections.eclipse.api.block.function.Function;
import com.m3.collections.eclipse.api.block.function.Function2;
import com.m3.collections.eclipse.api.block.function.primitive.BooleanFunction;
import com.m3.collections.eclipse.api.block.function.primitive.ByteFunction;
import com.m3.collections.eclipse.api.block.function.primitive.CharFunction;
import com.m3.collections.eclipse.api.block.function.primitive.DoubleFunction;
import com.m3.collections.eclipse.api.block.function.primitive.FloatFunction;
import com.m3.collections.eclipse.api.block.function.primitive.IntFunction;
import com.m3.collections.eclipse.api.block.function.primitive.LongFunction;
import com.m3.collections.eclipse.api.block.function.primitive.ShortFunction;
import com.m3.collections.eclipse.api.block.predicate.Predicate;
import com.m3.collections.eclipse.api.block.predicate.Predicate2;
import com.m3.collections.eclipse.api.block.procedure.Procedure;
import com.m3.collections.eclipse.api.multimap.set.UnsortedSetMultimap;
import com.m3.collections.eclipse.api.ordered.OrderedIterable;
import com.m3.collections.eclipse.api.set.primitive.BooleanSet;
import com.m3.collections.eclipse.api.set.primitive.ByteSet;
import com.m3.collections.eclipse.api.set.primitive.CharSet;
import com.m3.collections.eclipse.api.set.primitive.DoubleSet;
import com.m3.collections.eclipse.api.set.primitive.FloatSet;
import com.m3.collections.eclipse.api.set.primitive.IntSet;
import com.m3.collections.eclipse.api.set.primitive.LongSet;
import com.m3.collections.eclipse.api.set.primitive.ShortSet;
import com.m3.collections.eclipse.api.tuple.Pair;

/**
 * An iterable whose items are unique.
 */
public interface UnsortedSetIterable<T>
        extends SetIterable<T>
{
    /**
     * Returns the set whose members are all possible subsets of {@code this}. For example, the powerset of [1, 2] is
     * [[], [1], [2], [1, 2]].
     */
    UnsortedSetIterable<UnsortedSetIterable<T>> powerSet();

    @Override
    UnsortedSetIterable<T> tap(Procedure<? super T> procedure);

    @Override
    <V> UnsortedSetIterable<V> collect(Function<? super T, ? extends V> function);

    @Override
    BooleanSet collectBoolean(BooleanFunction<? super T> booleanFunction);

    @Override
    ByteSet collectByte(ByteFunction<? super T> byteFunction);

    @Override
    CharSet collectChar(CharFunction<? super T> charFunction);

    @Override
    DoubleSet collectDouble(DoubleFunction<? super T> doubleFunction);

    @Override
    FloatSet collectFloat(FloatFunction<? super T> floatFunction);

    @Override
    IntSet collectInt(IntFunction<? super T> intFunction);

    @Override
    LongSet collectLong(LongFunction<? super T> longFunction);

    @Override
    ShortSet collectShort(ShortFunction<? super T> shortFunction);

    @Override
    <P, V> UnsortedSetIterable<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter);

    @Override
    <V> UnsortedSetIterable<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function);

    @Override
    <V> UnsortedSetIterable<V> flatCollect(Function<? super T, ? extends Iterable<V>> function);

    /**
     * @since 9.2
     */
    @Override
    default <P, V> UnsortedSetIterable<V> flatCollectWith(Function2<? super T, ? super P, ? extends Iterable<V>> function, P parameter)
    {
        return this.flatCollect(each -> function.apply(each, parameter));
    }

    @Override
    <V> UnsortedSetMultimap<V, T> groupBy(Function<? super T, ? extends V> function);

    @Override
    <V> UnsortedSetMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function);

    @Override
    UnsortedSetIterable<T> union(SetIterable<? extends T> set);

    @Override
    UnsortedSetIterable<T> intersect(SetIterable<? extends T> set);

    @Override
    UnsortedSetIterable<T> difference(SetIterable<? extends T> subtrahendSet);

    @Override
    UnsortedSetIterable<T> symmetricDifference(SetIterable<? extends T> setB);

    @Override
    UnsortedSetIterable<T> select(Predicate<? super T> predicate);

    @Override
    <P> UnsortedSetIterable<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    UnsortedSetIterable<T> reject(Predicate<? super T> predicate);

    @Override
    <P> UnsortedSetIterable<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    <S> UnsortedSetIterable<S> selectInstancesOf(Class<S> clazz);

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Override
    @Deprecated
    <S> UnsortedSetIterable<Pair<T, S>> zip(Iterable<S> that);

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Override
    @Deprecated
    UnsortedSetIterable<Pair<T, Integer>> zipWithIndex();

    /**
     * Converts the UnsortedSetIterable to an immutable implementation. Returns this for immutable sets.
     *
     * @since 5.0
     */
    @Override
    ImmutableSet<T> toImmutable();

    @Override
    ParallelUnsortedSetIterable<T> asParallel(ExecutorService executorService, int batchSize);
}
