/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.map.sorted.mutable;

import com.m3.collections.eclipse.api.LazyIterable;
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
import com.m3.collections.eclipse.api.block.procedure.primitive.ObjectIntProcedure;
import com.m3.collections.eclipse.api.factory.Lists;
import com.m3.collections.eclipse.api.factory.SortedMaps;
import com.m3.collections.eclipse.api.factory.primitive.BooleanLists;
import com.m3.collections.eclipse.api.factory.primitive.ByteLists;
import com.m3.collections.eclipse.api.factory.primitive.CharLists;
import com.m3.collections.eclipse.api.factory.primitive.DoubleLists;
import com.m3.collections.eclipse.api.factory.primitive.FloatLists;
import com.m3.collections.eclipse.api.factory.primitive.IntLists;
import com.m3.collections.eclipse.api.factory.primitive.LongLists;
import com.m3.collections.eclipse.api.factory.primitive.ShortLists;
import com.m3.collections.eclipse.api.list.MutableList;
import com.m3.collections.eclipse.api.list.primitive.MutableBooleanList;
import com.m3.collections.eclipse.api.list.primitive.MutableByteList;
import com.m3.collections.eclipse.api.list.primitive.MutableCharList;
import com.m3.collections.eclipse.api.list.primitive.MutableDoubleList;
import com.m3.collections.eclipse.api.list.primitive.MutableFloatList;
import com.m3.collections.eclipse.api.list.primitive.MutableIntList;
import com.m3.collections.eclipse.api.list.primitive.MutableLongList;
import com.m3.collections.eclipse.api.list.primitive.MutableShortList;
import com.m3.collections.eclipse.api.map.MutableMap;
import com.m3.collections.eclipse.api.map.sorted.ImmutableSortedMap;
import com.m3.collections.eclipse.api.map.sorted.MutableSortedMap;
import com.m3.collections.eclipse.api.multimap.list.MutableListMultimap;
import com.m3.collections.eclipse.api.multimap.sortedset.MutableSortedSetMultimap;
import com.m3.collections.eclipse.api.ordered.OrderedIterable;
import com.m3.collections.eclipse.api.partition.list.PartitionMutableList;
import com.m3.collections.eclipse.api.stack.MutableStack;
import com.m3.collections.eclipse.api.tuple.Pair;
import com.m3.collections.eclipse.impl.block.factory.Functions;
import com.m3.collections.eclipse.impl.block.factory.Predicates;
import com.m3.collections.eclipse.impl.block.procedure.MapCollectProcedure;
import com.m3.collections.eclipse.impl.block.procedure.PartitionPredicate2Procedure;
import com.m3.collections.eclipse.impl.block.procedure.PartitionProcedure;
import com.m3.collections.eclipse.impl.block.procedure.SelectInstancesOfProcedure;
import com.m3.collections.eclipse.impl.list.fixed.ArrayAdapter;
import com.m3.collections.eclipse.impl.list.mutable.FastList;
import com.m3.collections.eclipse.impl.map.mutable.AbstractMutableMapIterable;
import com.m3.collections.eclipse.impl.map.mutable.UnifiedMap;
import com.m3.collections.eclipse.impl.multimap.list.FastListMultimap;
import com.m3.collections.eclipse.impl.partition.list.PartitionFastList;
import com.m3.collections.eclipse.impl.utility.Iterate;
import com.m3.collections.eclipse.impl.utility.MapIterate;

public abstract class AbstractMutableSortedMap<K, V> extends AbstractMutableMapIterable<K, V>
        implements MutableSortedMap<K, V>
{
    @Override
    @SuppressWarnings("AbstractMethodOverridesAbstractMethod")
    public abstract MutableSortedMap<K, V> clone();

    @Override
    public MutableSortedMap<K, V> withKeyValue(K key, V value)
    {
        this.put(key, value);
        return this;
    }

    @Override
    public MutableSortedMap<K, V> withAllKeyValues(Iterable<? extends Pair<? extends K, ? extends V>> keyValues)
    {
        for (Pair<? extends K, ? extends V> keyVal : keyValues)
        {
            this.put(keyVal.getOne(), keyVal.getTwo());
        }
        return this;
    }

    @Override
    public MutableSortedMap<K, V> withAllKeyValueArguments(Pair<? extends K, ? extends V>... keyValues)
    {
        return this.withAllKeyValues(ArrayAdapter.adapt(keyValues));
    }

    @Override
    public MutableSortedMap<K, V> withoutKey(K key)
    {
        this.removeKey(key);
        return this;
    }

    @Override
    public MutableSortedMap<K, V> withoutAllKeys(Iterable<? extends K> keys)
    {
        for (K key : keys)
        {
            this.removeKey(key);
        }
        return this;
    }

    @Override
    public MutableSortedMap<K, V> asUnmodifiable()
    {
        return UnmodifiableTreeMap.of(this);
    }

    @Override
    public ImmutableSortedMap<K, V> toImmutable()
    {
        return SortedMaps.immutable.withSortedMap(this);
    }

    @Override
    public MutableSortedMap<K, V> asSynchronized()
    {
        return SynchronizedSortedMap.of(this);
    }

    @Override
    public MutableSortedSetMultimap<V, K> flip()
    {
        return MapIterate.flip(this);
    }

    @Override
    public MutableBooleanList collectBoolean(BooleanFunction<? super V> booleanFunction)
    {
        return this.collectBoolean(booleanFunction, BooleanLists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public MutableByteList collectByte(ByteFunction<? super V> byteFunction)
    {
        return this.collectByte(byteFunction, ByteLists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public MutableCharList collectChar(CharFunction<? super V> charFunction)
    {
        return this.collectChar(charFunction, CharLists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public MutableDoubleList collectDouble(DoubleFunction<? super V> doubleFunction)
    {
        return this.collectDouble(doubleFunction, DoubleLists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public MutableFloatList collectFloat(FloatFunction<? super V> floatFunction)
    {
        return this.collectFloat(floatFunction, FloatLists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public MutableIntList collectInt(IntFunction<? super V> intFunction)
    {
        return this.collectInt(intFunction, IntLists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public MutableLongList collectLong(LongFunction<? super V> longFunction)
    {
        return this.collectLong(longFunction, LongLists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public MutableShortList collectShort(ShortFunction<? super V> shortFunction)
    {
        return this.collectShort(shortFunction, ShortLists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public <E> MutableSortedMap<K, V> collectKeysAndValues(
            Iterable<E> iterable,
            Function<? super E, ? extends K> keyFunction,
            Function<? super E, ? extends V> valueFunction)
    {
        Iterate.forEach(iterable, new MapCollectProcedure<>(this, keyFunction, valueFunction));
        return this;
    }

    @Override
    public <R> MutableSortedMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function)
    {
        return MapIterate.collectValues(this, function, TreeSortedMap.newMap(this.comparator()));
    }

    @Override
    public MutableSortedMap<K, V> tap(Procedure<? super V> procedure)
    {
        this.forEach(procedure);
        return this;
    }

    @Override
    public MutableSortedMap<K, V> select(Predicate2<? super K, ? super V> predicate)
    {
        return MapIterate.selectMapOnEntry(this, predicate, this.newEmpty());
    }

    @Override
    public MutableSortedMap<K, V> reject(Predicate2<? super K, ? super V> predicate)
    {
        return MapIterate.rejectMapOnEntry(this, predicate, this.newEmpty());
    }

    @Override
    public <R> MutableList<R> collect(Function<? super V, ? extends R> function)
    {
        return this.collect(function, Lists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public <P, VV> MutableList<VV> collectWith(Function2<? super V, ? super P, ? extends VV> function, P parameter)
    {
        return this.collect(Functions.bind(function, parameter));
    }

    @Override
    public <R> MutableList<R> collectIf(Predicate<? super V> predicate, Function<? super V, ? extends R> function)
    {
        return this.collectIf(predicate, function, Lists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public <R> MutableList<R> flatCollect(Function<? super V, ? extends Iterable<R>> function)
    {
        return this.flatCollect(function, Lists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public MutableList<V> reject(Predicate<? super V> predicate)
    {
        return this.reject(predicate, Lists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public <P> MutableList<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.select(Predicates.bind(predicate, parameter));
    }

    @Override
    public <P> MutableList<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.reject(Predicates.bind(predicate, parameter));
    }

    @Override
    public MutableList<V> select(Predicate<? super V> predicate)
    {
        return this.select(predicate, Lists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public PartitionMutableList<V> partition(Predicate<? super V> predicate)
    {
        PartitionMutableList<V> partitionMutableList = new PartitionFastList<>();
        this.forEach(new PartitionProcedure<>(predicate, partitionMutableList));
        return partitionMutableList;
    }

    @Override
    public <P> PartitionMutableList<V> partitionWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        PartitionMutableList<V> partitionMutableList = new PartitionFastList<>();
        this.forEach(new PartitionPredicate2Procedure<>(predicate, parameter, partitionMutableList));
        return partitionMutableList;
    }

    @Override
    public <S> MutableList<S> selectInstancesOf(Class<S> clazz)
    {
        FastList<S> result = FastList.newList(this.size());
        this.forEach(new SelectInstancesOfProcedure<>(clazz, result));
        result.trimToSize();
        return result;
    }

    @Override
    public <S> MutableList<Pair<V, S>> zip(Iterable<S> that)
    {
        return this.zip(that, Lists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public MutableList<Pair<V, Integer>> zipWithIndex()
    {
        return this.zipWithIndex(Lists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public <VV> MutableListMultimap<VV, V> groupBy(Function<? super V, ? extends VV> function)
    {
        return this.groupBy(function, FastListMultimap.newMultimap());
    }

    @Override
    public <VV> MutableListMultimap<VV, V> groupByEach(Function<? super V, ? extends Iterable<VV>> function)
    {
        return this.groupByEach(function, FastListMultimap.newMultimap());
    }

    @Override
    public <VV> MutableMap<VV, V> groupByUniqueKey(Function<? super V, ? extends VV> function)
    {
        return this.groupByUniqueKey(function, UnifiedMap.newMap(this.size()));
    }

    @Override
    public LazyIterable<V> asReversed()
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".asReversed() not implemented yet");
    }

    @Override
    public int detectLastIndex(Predicate<? super V> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".detectLastIndex() not implemented yet");
    }

    @Override
    public int indexOf(Object object)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".indexOf() not implemented yet");
    }

    @Override
    public <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super V, ? super S> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".corresponds() not implemented yet");
    }

    @Override
    public void forEach(int startIndex, int endIndex, Procedure<? super V> procedure)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".forEach() not implemented yet");
    }

    @Override
    public void forEachWithIndex(int fromIndex, int toIndex, ObjectIntProcedure<? super V> objectIntProcedure)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".forEachWithIndex() not implemented yet");
    }

    @Override
    public MutableStack<V> toStack()
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".toStack() not implemented yet");
    }

    @Override
    public int detectIndex(Predicate<? super V> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".detectIndex() not implemented yet");
    }
}
