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

import java.util.Map;

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
import com.m3.collections.eclipse.api.list.MutableList;
import com.m3.collections.eclipse.api.list.primitive.MutableBooleanList;
import com.m3.collections.eclipse.api.list.primitive.MutableByteList;
import com.m3.collections.eclipse.api.list.primitive.MutableCharList;
import com.m3.collections.eclipse.api.list.primitive.MutableDoubleList;
import com.m3.collections.eclipse.api.list.primitive.MutableFloatList;
import com.m3.collections.eclipse.api.list.primitive.MutableIntList;
import com.m3.collections.eclipse.api.list.primitive.MutableLongList;
import com.m3.collections.eclipse.api.list.primitive.MutableShortList;
import com.m3.collections.eclipse.api.multimap.list.MutableListMultimap;
import com.m3.collections.eclipse.api.partition.list.PartitionMutableList;
import com.m3.collections.eclipse.api.tuple.Pair;

public interface MutableOrderedMap<K, V> extends OrderedMap<K, V>, MutableMapIterable<K, V>
{
    @Override
    MutableOrderedMap<K, V> tap(Procedure<? super V> procedure);

    @Override
    MutableOrderedMap<V, K> flipUniqueValues();

    @Override
    MutableListMultimap<V, K> flip();

    @Override
    MutableOrderedMap<K, V> select(Predicate2<? super K, ? super V> predicate);

    @Override
    MutableOrderedMap<K, V> reject(Predicate2<? super K, ? super V> predicate);

    @Override
    <K2, V2> MutableOrderedMap<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function);

    @Override
    <R> MutableOrderedMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function);

    @Override
    MutableOrderedMap<K, V> toReversed();

    @Override
    MutableOrderedMap<K, V> take(int count);

    @Override
    MutableOrderedMap<K, V> takeWhile(Predicate<? super V> predicate);

    @Override
    MutableOrderedMap<K, V> drop(int count);

    @Override
    MutableOrderedMap<K, V> dropWhile(Predicate<? super V> predicate);

    @Override
    PartitionMutableList<V> partitionWhile(Predicate<? super V> predicate);

    @Override
    MutableList<V> distinct();

    @Override
    MutableList<V> select(Predicate<? super V> predicate);

    @Override
    <P> MutableList<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    MutableList<V> reject(Predicate<? super V> predicate);

    @Override
    <P> MutableList<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    PartitionMutableList<V> partition(Predicate<? super V> predicate);

    @Override
    <P> PartitionMutableList<V> partitionWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    MutableBooleanList collectBoolean(BooleanFunction<? super V> booleanFunction);

    @Override
    MutableByteList collectByte(ByteFunction<? super V> byteFunction);

    @Override
    MutableCharList collectChar(CharFunction<? super V> charFunction);

    @Override
    MutableDoubleList collectDouble(DoubleFunction<? super V> doubleFunction);

    @Override
    MutableFloatList collectFloat(FloatFunction<? super V> floatFunction);

    @Override
    MutableIntList collectInt(IntFunction<? super V> intFunction);

    @Override
    MutableLongList collectLong(LongFunction<? super V> longFunction);

    @Override
    MutableShortList collectShort(ShortFunction<? super V> shortFunction);

    @Override
    <S> MutableList<Pair<V, S>> zip(Iterable<S> that);

    @Override
    MutableList<Pair<V, Integer>> zipWithIndex();

    @Override
    <VV> MutableList<VV> collect(Function<? super V, ? extends VV> function);

    @Override
    <P, V1> MutableList<V1> collectWith(Function2<? super V, ? super P, ? extends V1> function, P parameter);

    @Override
    <V1> MutableList<V1> collectIf(Predicate<? super V> predicate, Function<? super V, ? extends V1> function);

    @Override
    <S> MutableList<S> selectInstancesOf(Class<S> clazz);

    @Override
    <V1> MutableList<V1> flatCollect(Function<? super V, ? extends Iterable<V1>> function);

    /**
     * @since 9.2
     */
    @Override
    default <P, V1> MutableList<V1> flatCollectWith(Function2<? super V, ? super P, ? extends Iterable<V1>> function, P parameter)
    {
        return this.flatCollect(each -> function.apply(each, parameter));
    }

    @Override
    <V1> MutableListMultimap<V1, V> groupBy(Function<? super V, ? extends V1> function);

    @Override
    <V1> MutableListMultimap<V1, V> groupByEach(Function<? super V, ? extends Iterable<V1>> function);

    @Override
    <V1> MutableOrderedMap<V1, V> groupByUniqueKey(Function<? super V, ? extends V1> function);

    @Override
    MutableOrderedMap<K, V> withKeyValue(K key, V value);

    @Override
    default MutableOrderedMap<K, V> withMap(Map<? extends K, ? extends V> map)
    {
        this.putAll(map);
        return this;
    }

    @Override
    default MutableOrderedMap<K, V> withMapIterable(MapIterable<? extends K, ? extends V> mapIterable)
    {
        this.putAllMapIterable(mapIterable);
        return this;
    }

    @Override
    MutableOrderedMap<K, V> withAllKeyValues(Iterable<? extends Pair<? extends K, ? extends V>> keyValues);

    @Override
    MutableOrderedMap<K, V> withAllKeyValueArguments(Pair<? extends K, ? extends V>... keyValuePairs);

    @Override
    MutableOrderedMap<K, V> withoutKey(K key);

    @Override
    MutableOrderedMap<K, V> withoutAllKeys(Iterable<? extends K> keys);

    @Override
    MutableOrderedMap<K, V> asUnmodifiable();
}
