/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.api.map.primitive;

import com.m3.collections.eclipse.api.bag.ImmutableBag;
import com.m3.collections.eclipse.api.bag.primitive.ImmutableBooleanBag;
import com.m3.collections.eclipse.api.bag.primitive.ImmutableByteBag;
import com.m3.collections.eclipse.api.bag.primitive.ImmutableCharBag;
import com.m3.collections.eclipse.api.bag.primitive.ImmutableDoubleBag;
import com.m3.collections.eclipse.api.bag.primitive.ImmutableFloatBag;
import com.m3.collections.eclipse.api.bag.primitive.ImmutableIntBag;
import com.m3.collections.eclipse.api.bag.primitive.ImmutableLongBag;
import com.m3.collections.eclipse.api.bag.primitive.ImmutableShortBag;
import com.m3.collections.eclipse.api.block.function.Function;
import com.m3.collections.eclipse.api.block.function.Function0;
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
import com.m3.collections.eclipse.api.block.procedure.Procedure2;
import com.m3.collections.eclipse.api.factory.Maps;
import com.m3.collections.eclipse.api.map.ImmutableMap;
import com.m3.collections.eclipse.api.map.MutableMap;
import com.m3.collections.eclipse.api.multimap.bag.ImmutableBagMultimap;
import com.m3.collections.eclipse.api.ordered.OrderedIterable;
import com.m3.collections.eclipse.api.partition.bag.PartitionImmutableBag;
import com.m3.collections.eclipse.api.set.ImmutableSet;
import com.m3.collections.eclipse.api.tuple.Pair;

/**
 * @since 8.0.
 */
public interface ImmutablePrimitiveObjectMap<V> extends PrimitiveObjectMap<V>
{
    @Override
    default <K, VV> ImmutableMap<K, VV> aggregateInPlaceBy(
            Function<? super V, ? extends K> groupBy,
            Function0<? extends VV> zeroValueFactory,
            Procedure2<? super VV, ? super V> mutatingAggregator)
    {
        MutableMap<K, VV> map = Maps.mutable.empty();
        this.forEach(each ->
        {
            K key = groupBy.valueOf(each);
            VV value = map.getIfAbsentPut(key, zeroValueFactory);
            mutatingAggregator.value(value, each);
        });
        return map.toImmutable();
    }

    @Override
    default <K, VV> ImmutableMap<K, VV> aggregateBy(
            Function<? super V, ? extends K> groupBy,
            Function0<? extends VV> zeroValueFactory,
            Function2<? super VV, ? super V, ? extends VV> nonMutatingAggregator)
    {
        MutableMap<K, VV> map = this.aggregateBy(
                groupBy,
                zeroValueFactory,
                nonMutatingAggregator,
                Maps.mutable.empty());
        return map.toImmutable();
    }

    @Override
    <VV> ImmutableBagMultimap<VV, V> groupByEach(Function<? super V, ? extends Iterable<VV>> function);

    @Override
    <VV> ImmutableBagMultimap<VV, V> groupBy(Function<? super V, ? extends VV> function);

    @Override
    default <VV> ImmutableMap<VV, V> groupByUniqueKey(Function<? super V, ? extends VV> function)
    {
        MutableMap<VV, V> target = Maps.mutable.withInitialCapacity(this.size());
        return this.groupByUniqueKey(function, target).toImmutable();
    }

    @Override
    <VV> ImmutableBag<VV> collectIf(Predicate<? super V> predicate, Function<? super V, ? extends VV> function);

    @Override
    <VV> ImmutableBag<VV> collect(Function<? super V, ? extends VV> function);

    @Override
    <VV> ImmutableBag<VV> flatCollect(Function<? super V, ? extends Iterable<VV>> function);

    /**
     * @since 9.2
     */
    @Override
    default <P, VV> ImmutableBag<VV> flatCollectWith(Function2<? super V, ? super P, ? extends Iterable<VV>> function, P parameter)
    {
        return this.flatCollect(each -> function.apply(each, parameter));
    }

    @Override
    ImmutableBooleanBag collectBoolean(BooleanFunction<? super V> booleanFunction);

    @Override
    ImmutableByteBag collectByte(ByteFunction<? super V> byteFunction);

    @Override
    ImmutableCharBag collectChar(CharFunction<? super V> charFunction);

    @Override
    ImmutableDoubleBag collectDouble(DoubleFunction<? super V> doubleFunction);

    @Override
    ImmutableFloatBag collectFloat(FloatFunction<? super V> floatFunction);

    @Override
    ImmutableIntBag collectInt(IntFunction<? super V> intFunction);

    @Override
    ImmutableLongBag collectLong(LongFunction<? super V> longFunction);

    @Override
    ImmutableShortBag collectShort(ShortFunction<? super V> shortFunction);

    @Override
    <P, VV> ImmutableBag<VV> collectWith(Function2<? super V, ? super P, ? extends VV> function, P parameter);

    @Override
    <S> ImmutableBag<S> selectInstancesOf(Class<S> clazz);

    @Override
    ImmutableBag<V> select(Predicate<? super V> predicate);

    @Override
    <P> ImmutableBag<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    ImmutableBag<V> reject(Predicate<? super V> predicate);

    @Override
    <P> ImmutableBag<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    PartitionImmutableBag<V> partition(Predicate<? super V> predicate);

    @Override
    <P> PartitionImmutableBag<V> partitionWith(Predicate2<? super V, ? super P> predicate, P parameter);

    /**
     * @deprecated in 7.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Override
    @Deprecated
    <S> ImmutableBag<Pair<V, S>> zip(Iterable<S> that);

    /**
     * @deprecated in 7.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Override
    @Deprecated
    ImmutableSet<Pair<V, Integer>> zipWithIndex();

    @Override
    <VV> ImmutableObjectLongMap<VV> sumByInt(Function<? super V, ? extends VV> groupBy, IntFunction<? super V> function);

    @Override
    <VV> ImmutableObjectDoubleMap<VV> sumByFloat(Function<? super V, ? extends VV> groupBy, FloatFunction<? super V> function);

    @Override
    <VV> ImmutableObjectLongMap<VV> sumByLong(Function<? super V, ? extends VV> groupBy, LongFunction<? super V> function);

    @Override
    <VV> ImmutableObjectDoubleMap<VV> sumByDouble(Function<? super V, ? extends VV> groupBy, DoubleFunction<? super V> function);
}
