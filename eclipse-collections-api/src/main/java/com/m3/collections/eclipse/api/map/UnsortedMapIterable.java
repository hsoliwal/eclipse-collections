/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.api.map;

import com.m3.collections.eclipse.api.bag.Bag;
import com.m3.collections.eclipse.api.bag.primitive.BooleanBag;
import com.m3.collections.eclipse.api.bag.primitive.ByteBag;
import com.m3.collections.eclipse.api.bag.primitive.CharBag;
import com.m3.collections.eclipse.api.bag.primitive.DoubleBag;
import com.m3.collections.eclipse.api.bag.primitive.FloatBag;
import com.m3.collections.eclipse.api.bag.primitive.IntBag;
import com.m3.collections.eclipse.api.bag.primitive.LongBag;
import com.m3.collections.eclipse.api.bag.primitive.ShortBag;
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
import com.m3.collections.eclipse.api.multimap.bag.BagMultimap;
import com.m3.collections.eclipse.api.multimap.set.UnsortedSetMultimap;
import com.m3.collections.eclipse.api.ordered.OrderedIterable;
import com.m3.collections.eclipse.api.partition.bag.PartitionBag;
import com.m3.collections.eclipse.api.set.UnsortedSetIterable;
import com.m3.collections.eclipse.api.tuple.Pair;

/**
 * An iterable Map whose elements are unsorted.
 */
public interface UnsortedMapIterable<K, V>
        extends MapIterable<K, V>
{
    @Override
    UnsortedSetMultimap<V, K> flip();

    @Override
    UnsortedMapIterable<V, K> flipUniqueValues();

    @Override
    UnsortedMapIterable<K, V> tap(Procedure<? super V> procedure);

    @Override
    UnsortedMapIterable<K, V> select(Predicate2<? super K, ? super V> predicate);

    @Override
    UnsortedMapIterable<K, V> reject(Predicate2<? super K, ? super V> predicate);

    @Override
    <R> UnsortedMapIterable<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function);

    @Override
    <K2, V2> UnsortedMapIterable<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function);

    @Override
    Bag<V> select(Predicate<? super V> predicate);

    @Override
    <P> Bag<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    Bag<V> reject(Predicate<? super V> predicate);

    @Override
    <P> Bag<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    PartitionBag<V> partition(Predicate<? super V> predicate);

    @Override
    <P> PartitionBag<V> partitionWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    <S> Bag<S> selectInstancesOf(Class<S> clazz);

    @Override
    <V1> Bag<V1> collect(Function<? super V, ? extends V1> function);

    @Override
    BooleanBag collectBoolean(BooleanFunction<? super V> booleanFunction);

    @Override
    ByteBag collectByte(ByteFunction<? super V> byteFunction);

    @Override
    CharBag collectChar(CharFunction<? super V> charFunction);

    @Override
    DoubleBag collectDouble(DoubleFunction<? super V> doubleFunction);

    @Override
    FloatBag collectFloat(FloatFunction<? super V> floatFunction);

    @Override
    IntBag collectInt(IntFunction<? super V> intFunction);

    @Override
    LongBag collectLong(LongFunction<? super V> longFunction);

    @Override
    ShortBag collectShort(ShortFunction<? super V> shortFunction);

    @Override
    <P, V1> Bag<V1> collectWith(Function2<? super V, ? super P, ? extends V1> function, P parameter);

    @Override
    <V1> Bag<V1> collectIf(Predicate<? super V> predicate, Function<? super V, ? extends V1> function);

    @Override
    <V1> Bag<V1> flatCollect(Function<? super V, ? extends Iterable<V1>> function);

    /**
     * @since 9.2
     */
    @Override
    default <P, V1> Bag<V1> flatCollectWith(Function2<? super V, ? super P, ? extends Iterable<V1>> function, P parameter)
    {
        return this.flatCollect(each -> function.apply(each, parameter));
    }

    @Override
    <V1> BagMultimap<V1, V> groupBy(Function<? super V, ? extends V1> function);

    @Override
    <V1> BagMultimap<V1, V> groupByEach(Function<? super V, ? extends Iterable<V1>> function);

    @Override
    <V1> UnsortedMapIterable<V1, V> groupByUniqueKey(Function<? super V, ? extends V1> function);

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Override
    @Deprecated
    <S> Bag<Pair<V, S>> zip(Iterable<S> that);

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Override
    @Deprecated
    UnsortedSetIterable<Pair<V, Integer>> zipWithIndex();

    /**
     * Converts the UnsortedMapIterable to an immutable implementation. Returns this for immutable maps.
     *
     * @since 5.0
     */
    @Override
    ImmutableMap<K, V> toImmutable();
}
