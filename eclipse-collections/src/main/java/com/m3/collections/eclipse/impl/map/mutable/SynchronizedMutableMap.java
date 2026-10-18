/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.map.mutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.m3.collections.eclipse.api.bag.MutableBag;
import com.m3.collections.eclipse.api.bag.primitive.MutableBooleanBag;
import com.m3.collections.eclipse.api.bag.primitive.MutableByteBag;
import com.m3.collections.eclipse.api.bag.primitive.MutableCharBag;
import com.m3.collections.eclipse.api.bag.primitive.MutableDoubleBag;
import com.m3.collections.eclipse.api.bag.primitive.MutableFloatBag;
import com.m3.collections.eclipse.api.bag.primitive.MutableIntBag;
import com.m3.collections.eclipse.api.bag.primitive.MutableLongBag;
import com.m3.collections.eclipse.api.bag.primitive.MutableShortBag;
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
import com.m3.collections.eclipse.api.factory.Maps;
import com.m3.collections.eclipse.api.map.ImmutableMap;
import com.m3.collections.eclipse.api.map.MapIterable;
import com.m3.collections.eclipse.api.map.MutableMap;
import com.m3.collections.eclipse.api.multimap.bag.MutableBagMultimap;
import com.m3.collections.eclipse.api.multimap.set.MutableSetMultimap;
import com.m3.collections.eclipse.api.ordered.OrderedIterable;
import com.m3.collections.eclipse.api.partition.bag.PartitionMutableBag;
import com.m3.collections.eclipse.api.set.MutableSet;
import com.m3.collections.eclipse.api.tuple.Pair;
import com.m3.collections.eclipse.impl.collection.mutable.SynchronizedMutableCollection;
import com.m3.collections.eclipse.impl.list.fixed.ArrayAdapter;
import com.m3.collections.eclipse.impl.map.AbstractSynchronizedMapIterable;
import com.m3.collections.eclipse.impl.set.mutable.SynchronizedMutableSet;

/**
 * A synchronized view of a {@link MutableMap}. It is imperative that the user manually synchronize on the collection when iterating over it using the
 * standard JDK iterator or JDK 5 for loop, as per {@link Collections#synchronizedCollection(Collection)}.
 *
 * @see MutableMap#asSynchronized()
 */
public class SynchronizedMutableMap<K, V>
        extends AbstractSynchronizedMapIterable<K, V> implements MutableMap<K, V>, Serializable
{
    private static final long serialVersionUID = 2L;

    public SynchronizedMutableMap(MutableMap<K, V> newMap)
    {
        super(newMap);
    }

    public SynchronizedMutableMap(MutableMap<K, V> newMap, Object newLock)
    {
        super(newMap, newLock);
    }

    /**
     * This method will take a MutableMap and wrap it directly in a SynchronizedMutableMap. It will
     * take any other non-Eclipse-Collections map and first adapt it will a MapAdapter, and then return a
     * SynchronizedMutableMap that wraps the adapter.
     */
    public static <K, V, M extends Map<K, V>> SynchronizedMutableMap<K, V> of(M map)
    {
        if (map == null)
        {
            throw new IllegalArgumentException("cannot create a SynchronizedMutableMap for null");
        }
        return new SynchronizedMutableMap<>(MapAdapter.adapt(map));
    }

    /**
     * This method will take a MutableMap and wrap it directly in a SynchronizedMutableMap. It will
     * take any other non-Eclipse-Collections map and first adapt it will a MapAdapter, and then return a
     * SynchronizedMutableMap that wraps the adapter. Additionally, a developer specifies which lock to use
     * with the collection.
     */
    public static <K, V, M extends Map<K, V>> SynchronizedMutableMap<K, V> of(M map, Object lock)
    {
        if (map == null)
        {
            throw new IllegalArgumentException("cannot create a SynchronizedMutableMap for null");
        }
        return new SynchronizedMutableMap<>(MapAdapter.adapt(map), lock);
    }

    @Override
    public MutableMap<K, V> withKeyValue(K key, V value)
    {
        synchronized (this.lock)
        {
            this.put(key, value);
            return this;
        }
    }

    @Override
    public MutableMap<K, V> withMap(Map<? extends K, ? extends V> map)
    {
        synchronized (this.lock)
        {
            this.putAll(map);
            return this;
        }
    }

    @Override
    public MutableMap<K, V> withMapIterable(MapIterable<? extends K, ? extends V> mapIterable)
    {
        this.putAllMapIterable(mapIterable);
        return this;
    }

    @Override
    public void putAllMapIterable(MapIterable<? extends K, ? extends V> mapIterable)
    {
        synchronized (this.lock)
        {
            mapIterable.forEachKeyValue(this.getDelegate()::put);
        }
    }

    @Override
    public MutableMap<K, V> withAllKeyValueArguments(Pair<? extends K, ? extends V>... keyValuePairs)
    {
        return this.withAllKeyValues(ArrayAdapter.adapt(keyValuePairs));
    }

    @Override
    public MutableMap<K, V> withAllKeyValues(Iterable<? extends Pair<? extends K, ? extends V>> keyValues)
    {
        synchronized (this.lock)
        {
            for (Pair<? extends K, ? extends V> keyValue : keyValues)
            {
                this.getDelegate().put(keyValue.getOne(), keyValue.getTwo());
            }
            return this;
        }
    }

    @Override
    public MutableMap<K, V> withoutKey(K key)
    {
        synchronized (this.lock)
        {
            this.remove(key);
            return this;
        }
    }

    @Override
    public MutableMap<K, V> withoutAllKeys(Iterable<? extends K> keys)
    {
        synchronized (this.lock)
        {
            for (K key : keys)
            {
                this.getDelegate().removeKey(key);
            }
            return this;
        }
    }

    @Override
    public MutableMap<K, V> newEmpty()
    {
        synchronized (this.lock)
        {
            return this.getDelegate().newEmpty();
        }
    }

    @Override
    public MutableMap<K, V> clone()
    {
        synchronized (this.lock)
        {
            return SynchronizedMutableMap.of(this.getDelegate().clone());
        }
    }

    protected Object writeReplace()
    {
        return new SynchronizedMapSerializationProxy<>(this.getDelegate());
    }

    @Override
    protected MutableMap<K, V> getDelegate()
    {
        return (MutableMap<K, V>) super.getDelegate();
    }

    @Override
    public <E> MutableMap<K, V> collectKeysAndValues(
            Iterable<E> iterable,
            Function<? super E, ? extends K> keyFunction,
            Function<? super E, ? extends V> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collectKeysAndValues(iterable, keyFunction, function);
        }
    }

    @Override
    public MutableMap<K, V> select(Predicate2<? super K, ? super V> predicate)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().select(predicate);
        }
    }

    @Override
    public MutableMap<K, V> reject(Predicate2<? super K, ? super V> predicate)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().reject(predicate);
        }
    }

    @Override
    public <K2, V2> MutableMap<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> pairFunction)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collect(pairFunction);
        }
    }

    @Override
    public <R> MutableMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collectValues(function);
        }
    }

    @Override
    public MutableMap<K, V> tap(Procedure<? super V> procedure)
    {
        return (MutableMap<K, V>) super.tap(procedure);
    }

    @Override
    public MutableBag<V> select(Predicate<? super V> predicate)
    {
        return (MutableBag<V>) super.select(predicate);
    }

    @Override
    public <P> MutableBag<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return (MutableBag<V>) super.selectWith(predicate, parameter);
    }

    @Override
    public MutableBag<V> reject(Predicate<? super V> predicate)
    {
        return (MutableBag<V>) super.reject(predicate);
    }

    @Override
    public <P> MutableBag<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return (MutableBag<V>) super.rejectWith(predicate, parameter);
    }

    @Override
    public PartitionMutableBag<V> partition(Predicate<? super V> predicate)
    {
        return (PartitionMutableBag<V>) super.partition(predicate);
    }

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Override
    @Deprecated
    public MutableSet<Pair<V, Integer>> zipWithIndex()
    {
        return (MutableSet<Pair<V, Integer>>) super.zipWithIndex();
    }

    @Override
    public <P> PartitionMutableBag<V> partitionWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return (PartitionMutableBag<V>) super.partitionWith(predicate, parameter);
    }

    @Override
    public <S> MutableBag<S> selectInstancesOf(Class<S> clazz)
    {
        return (MutableBag<S>) super.selectInstancesOf(clazz);
    }

    @Override
    public <A> MutableBag<A> collect(Function<? super V, ? extends A> function)
    {
        return (MutableBag<A>) super.collect(function);
    }

    @Override
    public MutableBooleanBag collectBoolean(BooleanFunction<? super V> booleanFunction)
    {
        return (MutableBooleanBag) super.collectBoolean(booleanFunction);
    }

    @Override
    public MutableByteBag collectByte(ByteFunction<? super V> byteFunction)
    {
        return (MutableByteBag) super.collectByte(byteFunction);
    }

    @Override
    public MutableCharBag collectChar(CharFunction<? super V> charFunction)
    {
        return (MutableCharBag) super.collectChar(charFunction);
    }

    @Override
    public MutableDoubleBag collectDouble(DoubleFunction<? super V> doubleFunction)
    {
        return (MutableDoubleBag) super.collectDouble(doubleFunction);
    }

    @Override
    public MutableFloatBag collectFloat(FloatFunction<? super V> floatFunction)
    {
        return (MutableFloatBag) super.collectFloat(floatFunction);
    }

    @Override
    public MutableIntBag collectInt(IntFunction<? super V> intFunction)
    {
        return (MutableIntBag) super.collectInt(intFunction);
    }

    @Override
    public MutableLongBag collectLong(LongFunction<? super V> longFunction)
    {
        return (MutableLongBag) super.collectLong(longFunction);
    }

    @Override
    public MutableShortBag collectShort(ShortFunction<? super V> shortFunction)
    {
        return (MutableShortBag) super.collectShort(shortFunction);
    }

    @Override
    public <P, A> MutableBag<A> collectWith(Function2<? super V, ? super P, ? extends A> function, P parameter)
    {
        return (MutableBag<A>) super.<P, A>collectWith(function, parameter);
    }

    @Override
    public <A> MutableBag<A> collectIf(Predicate<? super V> predicate, Function<? super V, ? extends A> function)
    {
        return (MutableBag<A>) super.<A>collectIf(predicate, function);
    }

    @Override
    public <A> MutableBag<A> flatCollect(Function<? super V, ? extends Iterable<A>> function)
    {
        return (MutableBag<A>) super.flatCollect(function);
    }

    @Override
    public <KK> MutableBagMultimap<KK, V> groupBy(Function<? super V, ? extends KK> function)
    {
        return (MutableBagMultimap<KK, V>) super.<KK>groupBy(function);
    }

    @Override
    public <KK> MutableBagMultimap<KK, V> groupByEach(Function<? super V, ? extends Iterable<KK>> function)
    {
        return (MutableBagMultimap<KK, V>) super.groupByEach(function);
    }

    /**
     * @since 11.0
     */
    @Override
    public <KK, VV> MutableMap<KK, VV> aggregateBy(
            Function<? super V, ? extends KK> groupBy,
            Function0<? extends VV> zeroValueFactory,
            Function2<? super VV, ? super V, ? extends VV> nonMutatingAggregator)
    {
        return (MutableMap<KK, VV>) super.aggregateBy(groupBy, zeroValueFactory, nonMutatingAggregator);
    }

    /**
     * @since 11.0
     */
    @Override
    public <K1, V1, V2> MutableMap<K1, V2> aggregateBy(
            Function<? super K, ? extends K1> keyFunction,
            Function<? super V, ? extends V1> valueFunction,
            Function0<? extends V2> zeroValueFactory,
            Function2<? super V2, ? super V1, ? extends V2> nonMutatingAggregator)
    {
        return (MutableMap<K1, V2>) super.aggregateBy(keyFunction, valueFunction, zeroValueFactory, nonMutatingAggregator);
    }

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Override
    @Deprecated
    public <S> MutableBag<Pair<V, S>> zip(Iterable<S> that)
    {
        return (MutableBag<Pair<V, S>>) super.zip(that);
    }

    @Override
    public <VV> MutableMap<VV, V> groupByUniqueKey(Function<? super V, ? extends VV> function)
    {
        return (MutableMap<VV, V>) super.groupByUniqueKey(function);
    }

    @Override
    public MutableMap<V, K> flipUniqueValues()
    {
        synchronized (this.lock)
        {
            return this.getDelegate().flipUniqueValues();
        }
    }

    @Override
    public MutableSetMultimap<V, K> flip()
    {
        synchronized (this.lock)
        {
            return this.getDelegate().flip();
        }
    }

    @Override
    public Set<K> keySet()
    {
        synchronized (this.lock)
        {
            return SynchronizedMutableSet.of(this.getDelegate().keySet(), this.lock);
        }
    }

    @Override
    public Collection<V> values()
    {
        synchronized (this.lock)
        {
            return SynchronizedMutableCollection.of(this.getDelegate().values(), this.lock);
        }
    }

    @Override
    public Set<Entry<K, V>> entrySet()
    {
        synchronized (this.lock)
        {
            return SynchronizedMutableSet.of(this.getDelegate().entrySet(), this.lock);
        }
    }

    @Override
    public MutableMap<K, V> asUnmodifiable()
    {
        synchronized (this.lock)
        {
            return UnmodifiableMutableMap.of(this);
        }
    }

    @Override
    public MutableMap<K, V> asSynchronized()
    {
        return this;
    }

    @Override
    public ImmutableMap<K, V> toImmutable()
    {
        synchronized (this.lock)
        {
            return Maps.immutable.withAll(this);
        }
    }
}
