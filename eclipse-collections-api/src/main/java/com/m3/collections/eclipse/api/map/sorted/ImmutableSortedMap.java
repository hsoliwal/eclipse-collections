/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.api.map.sorted;

import java.util.Map;
import java.util.SortedMap;

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
import com.m3.collections.eclipse.api.block.function.primitive.ObjectIntToObjectFunction;
import com.m3.collections.eclipse.api.block.function.primitive.ShortFunction;
import com.m3.collections.eclipse.api.block.predicate.Predicate;
import com.m3.collections.eclipse.api.block.predicate.Predicate2;
import com.m3.collections.eclipse.api.block.procedure.Procedure;
import com.m3.collections.eclipse.api.block.procedure.Procedure2;
import com.m3.collections.eclipse.api.factory.Maps;
import com.m3.collections.eclipse.api.list.ImmutableList;
import com.m3.collections.eclipse.api.list.primitive.ImmutableBooleanList;
import com.m3.collections.eclipse.api.list.primitive.ImmutableByteList;
import com.m3.collections.eclipse.api.list.primitive.ImmutableCharList;
import com.m3.collections.eclipse.api.list.primitive.ImmutableDoubleList;
import com.m3.collections.eclipse.api.list.primitive.ImmutableFloatList;
import com.m3.collections.eclipse.api.list.primitive.ImmutableIntList;
import com.m3.collections.eclipse.api.list.primitive.ImmutableLongList;
import com.m3.collections.eclipse.api.list.primitive.ImmutableShortList;
import com.m3.collections.eclipse.api.map.ImmutableMap;
import com.m3.collections.eclipse.api.map.ImmutableMapIterable;
import com.m3.collections.eclipse.api.map.ImmutableOrderedMap;
import com.m3.collections.eclipse.api.map.MapIterable;
import com.m3.collections.eclipse.api.map.MutableMap;
import com.m3.collections.eclipse.api.multimap.list.ImmutableListMultimap;
import com.m3.collections.eclipse.api.multimap.sortedset.ImmutableSortedSetMultimap;
import com.m3.collections.eclipse.api.partition.list.PartitionImmutableList;
import com.m3.collections.eclipse.api.tuple.Pair;

/**
 * An ImmutableSortedMap is different from a JCF SortedMap because it has no mutating methods. It provides
 * the read-only protocol of a SortedMap.
 */
public interface ImmutableSortedMap<K, V>
        extends SortedMapIterable<K, V>, ImmutableMapIterable<K, V>
{
    @Override
    SortedMap<K, V> castToMap();

    SortedMap<K, V> castToSortedMap();

    // TODO: Keys could be ordered
    @Override
    ImmutableSortedSetMultimap<V, K> flip();

    @Override
    ImmutableSortedMap<K, V> newWithKeyValue(K key, V value);

    @Override
    ImmutableSortedMap<K, V> newWithMap(Map<? extends K, ? extends V> map);

    @Override
    ImmutableSortedMap<K, V> newWithMapIterable(MapIterable<? extends K, ? extends V> mapIterable);

    @Override
    ImmutableSortedMap<K, V> newWithAllKeyValues(Iterable<? extends Pair<? extends K, ? extends V>> keyValues);

    @Override
    ImmutableSortedMap<K, V> newWithAllKeyValueArguments(Pair<? extends K, ? extends V>... keyValuePairs);

    @Override
    ImmutableSortedMap<K, V> newWithoutKey(K key);

    @Override
    ImmutableSortedMap<K, V> newWithoutAllKeys(Iterable<? extends K> keys);

    MutableSortedMap<K, V> toSortedMap();

    // TODO: When we have implementations of linked hash maps
    // ImmutableOrderedMap<V, K> flipUniqueValues();

    @Override
    ImmutableSortedMap<K, V> select(Predicate2<? super K, ? super V> predicate);

    @Override
    ImmutableSortedMap<K, V> reject(Predicate2<? super K, ? super V> predicate);

    @Override
    ImmutableSortedMap<K, V> tap(Procedure<? super V> procedure);

    @Override
    ImmutableList<V> select(Predicate<? super V> predicate);

    @Override
    <P> ImmutableList<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    ImmutableList<V> reject(Predicate<? super V> predicate);

    @Override
    <P> ImmutableList<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    <S> ImmutableList<S> selectInstancesOf(Class<S> clazz);

    @Override
    PartitionImmutableList<V> partition(Predicate<? super V> predicate);

    @Override
    <P> PartitionImmutableList<V> partitionWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    <R> ImmutableList<R> collect(Function<? super V, ? extends R> function);

    /**
     * @since 9.1.
     */
    @Override
    default <R> ImmutableList<R> collectWithIndex(ObjectIntToObjectFunction<? super V, ? extends R> function)
    {
        int[] index = {0};
        return this.collect(each -> function.valueOf(each, index[0]++));
    }

    @Override
    <P, VV> ImmutableList<VV> collectWith(Function2<? super V, ? super P, ? extends VV> function, P parameter);

    @Override
    <K2, V2> ImmutableMap<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function);

    @Override
    <R> ImmutableSortedMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function);

    @Override
    <R> ImmutableOrderedMap<R, V> collectKeysUnique(Function2<? super K, ? super V, ? extends R> function);

    @Override
    <R> ImmutableList<R> collectIf(
            Predicate<? super V> predicate,
            Function<? super V, ? extends R> function);

    @Override
    <R> ImmutableList<R> flatCollect(Function<? super V, ? extends Iterable<R>> function);

    /**
     * @since 9.2
     */
    @Override
    default <P, R> ImmutableList<R> flatCollectWith(Function2<? super V, ? super P, ? extends Iterable<R>> function, P parameter)
    {
        return this.flatCollect(each -> function.apply(each, parameter));
    }

    @Override
    ImmutableBooleanList collectBoolean(BooleanFunction<? super V> booleanFunction);

    @Override
    ImmutableByteList collectByte(ByteFunction<? super V> byteFunction);

    @Override
    ImmutableCharList collectChar(CharFunction<? super V> charFunction);

    @Override
    ImmutableDoubleList collectDouble(DoubleFunction<? super V> doubleFunction);

    @Override
    ImmutableFloatList collectFloat(FloatFunction<? super V> floatFunction);

    @Override
    ImmutableIntList collectInt(IntFunction<? super V> intFunction);

    @Override
    ImmutableLongList collectLong(LongFunction<? super V> longFunction);

    @Override
    ImmutableShortList collectShort(ShortFunction<? super V> shortFunction);

    @Override
    <S> ImmutableList<Pair<V, S>> zip(Iterable<S> that);

    @Override
    ImmutableList<Pair<V, Integer>> zipWithIndex();

    @Override
    <VV> ImmutableListMultimap<VV, V> groupBy(Function<? super V, ? extends VV> function);

    @Override
    <VV> ImmutableListMultimap<VV, V> groupByEach(Function<? super V, ? extends Iterable<VV>> function);

    @Override
    default <VV> ImmutableMap<VV, V> groupByUniqueKey(Function<? super V, ? extends VV> function)
    {
        MutableMap<VV, V> target = Maps.mutable.withInitialCapacity(this.size());
        return this.groupByUniqueKey(function, target).toImmutable();
    }

    @Override
    default <KK, VV> ImmutableMap<KK, VV> aggregateInPlaceBy(
            Function<? super V, ? extends KK> groupBy,
            Function0<? extends VV> zeroValueFactory,
            Procedure2<? super VV, ? super V> mutatingAggregator)
    {
        MutableMap<KK, VV> map = Maps.mutable.empty();
        this.forEach(each ->
        {
            KK key = groupBy.valueOf(each);
            VV value = map.getIfAbsentPut(key, zeroValueFactory);
            mutatingAggregator.value(value, each);
        });
        return map.toImmutable();
    }

    @Override
    default <KK, VV> ImmutableMap<KK, VV> aggregateBy(
            Function<? super V, ? extends KK> groupBy,
            Function0<? extends VV> zeroValueFactory,
            Function2<? super VV, ? super V, ? extends VV> nonMutatingAggregator)
    {
        MutableMap<KK, VV> map = this.aggregateBy(
                groupBy,
                zeroValueFactory,
                nonMutatingAggregator,
                Maps.mutable.empty());
        return map.toImmutable();
    }

    @Override
    default <K1, V1, V2> ImmutableMap<K1, V2> aggregateBy(
            Function<? super K, ? extends K1> keyFunction,
            Function<? super V, ? extends V1> valueFunction,
            Function0<? extends V2> zeroValueFactory,
            Function2<? super V2, ? super V1, ? extends V2> nonMutatingAggregator)
    {
        MutableMap<K1, V2> map = Maps.mutable.empty();
        this.forEachKeyValue((key, value) -> map.updateValueWith(
                keyFunction.valueOf(key),
                zeroValueFactory,
                nonMutatingAggregator,
                valueFunction.valueOf(value)));
        return map.toImmutable();
    }

    @Override
    default <KK> ImmutableMap<KK, V> reduceBy(
            Function<? super V, ? extends KK> groupBy,
            Function2<? super V, ? super V, ? extends V> reduceFunction)
    {
        MutableMap<KK, V> map = this.reduceBy(groupBy, reduceFunction, Maps.mutable.empty());
        return map.toImmutable();
    }
}
