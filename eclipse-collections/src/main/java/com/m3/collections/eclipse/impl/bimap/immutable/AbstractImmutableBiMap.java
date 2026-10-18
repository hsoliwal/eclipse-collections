/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.bimap.immutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.m3.collections.eclipse.api.RichIterable;
import com.m3.collections.eclipse.api.bag.ImmutableBag;
import com.m3.collections.eclipse.api.bag.primitive.ImmutableBooleanBag;
import com.m3.collections.eclipse.api.bag.primitive.ImmutableByteBag;
import com.m3.collections.eclipse.api.bag.primitive.ImmutableCharBag;
import com.m3.collections.eclipse.api.bag.primitive.ImmutableDoubleBag;
import com.m3.collections.eclipse.api.bag.primitive.ImmutableFloatBag;
import com.m3.collections.eclipse.api.bag.primitive.ImmutableIntBag;
import com.m3.collections.eclipse.api.bag.primitive.ImmutableLongBag;
import com.m3.collections.eclipse.api.bag.primitive.ImmutableShortBag;
import com.m3.collections.eclipse.api.bimap.ImmutableBiMap;
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
import com.m3.collections.eclipse.api.block.procedure.Procedure;
import com.m3.collections.eclipse.api.block.procedure.Procedure2;
import com.m3.collections.eclipse.api.factory.BiMaps;
import com.m3.collections.eclipse.api.factory.Sets;
import com.m3.collections.eclipse.api.map.ImmutableMap;
import com.m3.collections.eclipse.api.map.MapIterable;
import com.m3.collections.eclipse.api.map.MutableMap;
import com.m3.collections.eclipse.api.map.primitive.ImmutableObjectDoubleMap;
import com.m3.collections.eclipse.api.map.primitive.ImmutableObjectLongMap;
import com.m3.collections.eclipse.api.map.primitive.ObjectDoubleMap;
import com.m3.collections.eclipse.api.map.primitive.ObjectLongMap;
import com.m3.collections.eclipse.api.multimap.set.ImmutableSetMultimap;
import com.m3.collections.eclipse.api.ordered.OrderedIterable;
import com.m3.collections.eclipse.api.partition.set.PartitionImmutableSet;
import com.m3.collections.eclipse.api.partition.set.PartitionMutableSet;
import com.m3.collections.eclipse.api.set.ImmutableSet;
import com.m3.collections.eclipse.api.set.MutableSet;
import com.m3.collections.eclipse.api.tuple.Pair;
import com.m3.collections.eclipse.impl.bimap.AbstractBiMap;
import com.m3.collections.eclipse.impl.bimap.mutable.HashBiMap;
import com.m3.collections.eclipse.impl.block.factory.Predicates;
import com.m3.collections.eclipse.impl.block.procedure.PartitionProcedure;
import com.m3.collections.eclipse.impl.block.procedure.SelectInstancesOfProcedure;
import com.m3.collections.eclipse.impl.list.fixed.ArrayAdapter;
import com.m3.collections.eclipse.impl.multimap.set.UnifiedSetMultimap;
import com.m3.collections.eclipse.impl.partition.set.PartitionUnifiedSet;
import com.m3.collections.eclipse.impl.set.mutable.UnifiedSet;
import com.m3.collections.eclipse.impl.utility.Iterate;
import com.m3.collections.eclipse.impl.utility.MapIterate;

public abstract class AbstractImmutableBiMap<K, V> extends AbstractBiMap<K, V> implements ImmutableBiMap<K, V>, Map<K, V>
{
    private final ImmutableMap<K, V> delegate;
    private final AbstractImmutableBiMap<V, K> inverse;

    private AbstractImmutableBiMap(ImmutableMap<K, V> delegate, AbstractImmutableBiMap<V, K> valuesToKeys)
    {
        this.delegate = delegate;
        this.inverse = valuesToKeys;
    }

    AbstractImmutableBiMap(ImmutableMap<K, V> map, ImmutableMap<V, K> inverse)
    {
        this.delegate = map;
        this.inverse = new Inverse<>(inverse, this);
    }

    @Override
    protected ImmutableMap<K, V> getDelegate()
    {
        return this.delegate;
    }

    @Override
    protected ImmutableMap<V, K> getInverse()
    {
        return this.inverse.delegate;
    }

    @Override
    public ImmutableBiMap<K, V> newWithKeyValue(K key, V value)
    {
        HashBiMap<K, V> map = new HashBiMap<>(this.delegate.castToMap());
        map.put(key, value);
        return map.toImmutable();
    }

    @Override
    public ImmutableBiMap<K, V> newWithAllKeyValues(Iterable<? extends Pair<? extends K, ? extends V>> keyValues)
    {
        HashBiMap<K, V> map = new HashBiMap<>(this.delegate.castToMap());
        for (Pair<? extends K, ? extends V> keyValuePair : keyValues)
        {
            map.put(keyValuePair.getOne(), keyValuePair.getTwo());
        }
        return map.toImmutable();
    }

    @Override
    public ImmutableBiMap<K, V> newWithMap(Map<? extends K, ? extends V> map)
    {
        HashBiMap<K, V> result = new HashBiMap<>(this.delegate.castToMap());
        result.putAll(map);
        return result.toImmutable();
    }

    @Override
    public ImmutableBiMap<K, V> newWithMapIterable(MapIterable<? extends K, ? extends V> mapIterable)
    {
        HashBiMap<K, V> map = new HashBiMap<>(this.delegate.castToMap());
        mapIterable.forEachKeyValue(map::put);
        return map.toImmutable();
    }

    @Override
    public ImmutableBiMap<K, V> newWithAllKeyValueArguments(Pair<? extends K, ? extends V>... keyValuePairs)
    {
        return this.newWithAllKeyValues(ArrayAdapter.adapt(keyValuePairs));
    }

    @Override
    public ImmutableBiMap<K, V> newWithoutKey(K key)
    {
        HashBiMap<K, V> map = new HashBiMap<>(this.delegate.castToMap());
        map.removeKey(key);
        return map.toImmutable();
    }

    @Override
    public ImmutableBiMap<K, V> newWithoutAllKeys(Iterable<? extends K> keys)
    {
        HashBiMap<K, V> map = new HashBiMap<>(this.delegate.castToMap());
        for (K key : keys)
        {
            map.removeKey(key);
        }
        return map.toImmutable();
    }

    @Override
    public ImmutableBiMap<V, K> inverse()
    {
        return this.inverse;
    }

    @Override
    public ImmutableSetMultimap<V, K> flip()
    {
        // TODO: We could optimize this since we know the values are unique
        return MapIterate.flip(this).toImmutable();
    }

    @Override
    public ImmutableBiMap<V, K> flipUniqueValues()
    {
        return this.inverse();
    }

    @Override
    public V put(K key, V value)
    {
        throw new UnsupportedOperationException("Cannot call put() on " + this.getClass().getSimpleName());
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map)
    {
        throw new UnsupportedOperationException("Cannot call putAll() on " + this.getClass().getSimpleName());
    }

    @Override
    public V remove(Object key)
    {
        throw new UnsupportedOperationException("Cannot call remove() on " + this.getClass().getSimpleName());
    }

    @Override
    public void clear()
    {
        throw new UnsupportedOperationException("Cannot call clear() on " + this.getClass().getSimpleName());
    }

    @Override
    public Set<K> keySet()
    {
        return this.delegate.castToMap().keySet();
    }

    @Override
    public Collection<V> values()
    {
        return this.delegate.castToMap().values();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet()
    {
        return this.delegate.castToMap().entrySet();
    }

    @Override
    public Iterator<V> iterator()
    {
        return this.delegate.iterator();
    }

    @Override
    public ImmutableBiMap<K, V> toImmutable()
    {
        return this;
    }

    @Override
    public Map<K, V> castToMap()
    {
        return this;
    }

    public MutableMap<K, V> toMap()
    {
        return this.getDelegate().toMap();
    }

    @Override
    public <K2, V2> ImmutableBiMap<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function)
    {
        ImmutableMap<K2, V2> result = this.delegate.collect(function);
        return BiMaps.immutable.withAll(result);
    }

    @Override
    public <VV> ImmutableBag<VV> collect(Function<? super V, ? extends VV> function)
    {
        return this.delegate.collect(function);
    }

    @Override
    public <R> ImmutableBiMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function)
    {
        ImmutableMap<K, R> result = this.delegate.collectValues(function);
        return BiMaps.immutable.withAll(result);
    }

    @Override
    public ImmutableBooleanBag collectBoolean(BooleanFunction<? super V> booleanFunction)
    {
        return this.delegate.collectBoolean(booleanFunction);
    }

    @Override
    public ImmutableByteBag collectByte(ByteFunction<? super V> byteFunction)
    {
        return this.delegate.collectByte(byteFunction);
    }

    @Override
    public ImmutableCharBag collectChar(CharFunction<? super V> charFunction)
    {
        return this.delegate.collectChar(charFunction);
    }

    @Override
    public ImmutableDoubleBag collectDouble(DoubleFunction<? super V> doubleFunction)
    {
        return this.delegate.collectDouble(doubleFunction);
    }

    @Override
    public ImmutableFloatBag collectFloat(FloatFunction<? super V> floatFunction)
    {
        return this.delegate.collectFloat(floatFunction);
    }

    @Override
    public ImmutableIntBag collectInt(IntFunction<? super V> intFunction)
    {
        return this.delegate.collectInt(intFunction);
    }

    @Override
    public ImmutableLongBag collectLong(LongFunction<? super V> longFunction)
    {
        return this.delegate.collectLong(longFunction);
    }

    @Override
    public ImmutableShortBag collectShort(ShortFunction<? super V> shortFunction)
    {
        return this.delegate.collectShort(shortFunction);
    }

    @Override
    public <P, VV> ImmutableBag<VV> collectWith(Function2<? super V, ? super P, ? extends VV> function, P parameter)
    {
        return this.delegate.collectWith(function, parameter);
    }

    @Override
    public <VV> ImmutableBag<VV> collectIf(Predicate<? super V> predicate, Function<? super V, ? extends VV> function)
    {
        return this.delegate.collectIf(predicate, function);
    }

    @Override
    public <VV> ImmutableBag<VV> flatCollect(Function<? super V, ? extends Iterable<VV>> function)
    {
        return this.delegate.flatCollect(function);
    }

    @Override
    public ImmutableBiMap<K, V> select(Predicate2<? super K, ? super V> predicate)
    {
        return MapIterate.selectMapOnEntry(this, predicate, HashBiMap.newMap()).toImmutable();
    }

    @Override
    public ImmutableBiMap<K, V> tap(Procedure<? super V> procedure)
    {
        this.forEach(procedure);
        return this;
    }

    @Override
    public ImmutableSet<V> select(Predicate<? super V> predicate)
    {
        return this.delegate.select(predicate, Sets.mutable.empty()).toImmutable();
    }

    @Override
    public <P> ImmutableSet<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.delegate.selectWith(predicate, parameter, Sets.mutable.empty()).toImmutable();
    }

    @Override
    public ImmutableBiMap<K, V> reject(Predicate2<? super K, ? super V> predicate)
    {
        return MapIterate.rejectMapOnEntry(this, predicate, HashBiMap.newMap()).toImmutable();
    }

    @Override
    public ImmutableSet<V> reject(Predicate<? super V> predicate)
    {
        return this.delegate.reject(predicate, Sets.mutable.empty()).toImmutable();
    }

    @Override
    public <P> ImmutableSet<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.delegate.rejectWith(predicate, parameter, Sets.mutable.empty()).toImmutable();
    }

    @Override
    public PartitionImmutableSet<V> partition(Predicate<? super V> predicate)
    {
        PartitionMutableSet<V> result = new PartitionUnifiedSet<>();
        this.inverse.forEachKey(new PartitionProcedure<>(predicate, result));
        return result.toImmutable();
    }

    @Override
    public <P> PartitionImmutableSet<V> partitionWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.partition(Predicates.bind(predicate, parameter));
    }

    /**
     * @deprecated in 8.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Override
    @Deprecated
    public <S> ImmutableSet<Pair<V, S>> zip(Iterable<S> that)
    {
        if (that instanceof Collection || that instanceof RichIterable)
        {
            int thatSize = Iterate.sizeOf(that);
            UnifiedSet<Pair<V, S>> target = UnifiedSet.newSet(Math.min(this.size(), thatSize));
            return this.delegate.zip(that, target).toImmutable();
        }
        return this.delegate.zip(that, Sets.mutable.empty()).toImmutable();
    }

    /**
     * @deprecated in 8.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Override
    @Deprecated
    public ImmutableSet<Pair<V, Integer>> zipWithIndex()
    {
        return this.delegate.zipWithIndex(Sets.mutable.withInitialCapacity(this.size())).toImmutable();
    }

    @Override
    public <V1> ImmutableObjectLongMap<V1> sumByInt(Function<? super V, ? extends V1> groupBy, IntFunction<? super V> function)
    {
        ObjectLongMap<V1> map = this.delegate.sumByInt(groupBy, function);
        return map.toImmutable();
    }

    @Override
    public <V1> ImmutableObjectDoubleMap<V1> sumByFloat(Function<? super V, ? extends V1> groupBy, FloatFunction<? super V> function)
    {
        ObjectDoubleMap<V1> map = this.delegate.sumByFloat(groupBy, function);
        return map.toImmutable();
    }

    @Override
    public <V1> ImmutableObjectLongMap<V1> sumByLong(Function<? super V, ? extends V1> groupBy, LongFunction<? super V> function)
    {
        ObjectLongMap<V1> map = this.delegate.sumByLong(groupBy, function);
        return map.toImmutable();
    }

    @Override
    public <V1> ImmutableObjectDoubleMap<V1> sumByDouble(Function<? super V, ? extends V1> groupBy, DoubleFunction<? super V> function)
    {
        ObjectDoubleMap<V1> map = this.delegate.sumByDouble(groupBy, function);
        return map.toImmutable();
    }

    @Override
    public <VV> ImmutableSetMultimap<VV, V> groupBy(Function<? super V, ? extends VV> function)
    {
        return this.delegate.groupBy(function, new UnifiedSetMultimap<VV, V>()).toImmutable();
    }

    @Override
    public <VV> ImmutableSetMultimap<VV, V> groupByEach(Function<? super V, ? extends Iterable<VV>> function)
    {
        return this.delegate.groupByEach(function, new UnifiedSetMultimap<>()).toImmutable();
    }

    @Override
    public <VV> ImmutableBiMap<VV, V> groupByUniqueKey(Function<? super V, ? extends VV> function)
    {
        return BiMaps.immutable.withAll(this.delegate.groupByUniqueKey(function));
    }

    @Override
    public <K2, V2> ImmutableMap<K2, V2> aggregateBy(Function<? super V, ? extends K2> groupBy, Function0<? extends V2> zeroValueFactory, Function2<? super V2, ? super V, ? extends V2> nonMutatingAggregator)
    {
        return this.delegate.aggregateBy(groupBy, zeroValueFactory, nonMutatingAggregator);
    }

    @Override
    public <K1, V1, V2> ImmutableMap<K1, V2> aggregateBy(
            Function<? super K, ? extends K1> keyFunction,
            Function<? super V, ? extends V1> valueFunction,
            Function0<? extends V2> zeroValueFactory,
            Function2<? super V2, ? super V1, ? extends V2> nonMutatingAggregator)
    {
        return this.delegate.aggregateBy(keyFunction, valueFunction, zeroValueFactory, nonMutatingAggregator);
    }

    @Override
    public <K2, V2> ImmutableMap<K2, V2> aggregateInPlaceBy(Function<? super V, ? extends K2> groupBy, Function0<? extends V2> zeroValueFactory, Procedure2<? super V2, ? super V> mutatingAggregator)
    {
        return this.delegate.aggregateInPlaceBy(groupBy, zeroValueFactory, mutatingAggregator);
    }

    @Override
    public <S> ImmutableSet<S> selectInstancesOf(Class<S> clazz)
    {
        MutableSet<S> result = new UnifiedSet<>();
        this.inverse.forEachKey(new SelectInstancesOfProcedure<>(clazz, result));
        return result.toImmutable();
    }

    private static class Inverse<K, V> extends AbstractImmutableBiMap<K, V> implements Serializable
    {
        Inverse(ImmutableMap<K, V> delegate, AbstractImmutableBiMap<V, K> inverse)
        {
            super(delegate, inverse);
        }

        protected Object writeReplace()
        {
            return new ImmutableBiMapSerializationProxy<>(this);
        }
    }
}
