/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.bag.immutable;

import java.util.Iterator;

import com.m3.collections.eclipse.api.RichIterable;
import com.m3.collections.eclipse.api.bag.ImmutableBag;
import com.m3.collections.eclipse.api.bag.MutableBag;
import com.m3.collections.eclipse.api.bag.primitive.ImmutableBooleanBag;
import com.m3.collections.eclipse.api.bag.primitive.ImmutableByteBag;
import com.m3.collections.eclipse.api.bag.primitive.ImmutableCharBag;
import com.m3.collections.eclipse.api.bag.primitive.ImmutableDoubleBag;
import com.m3.collections.eclipse.api.bag.primitive.ImmutableFloatBag;
import com.m3.collections.eclipse.api.bag.primitive.ImmutableIntBag;
import com.m3.collections.eclipse.api.bag.primitive.ImmutableLongBag;
import com.m3.collections.eclipse.api.bag.primitive.ImmutableShortBag;
import com.m3.collections.eclipse.api.block.function.Function;
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
import com.m3.collections.eclipse.api.collection.MutableCollection;
import com.m3.collections.eclipse.api.factory.Bags;
import com.m3.collections.eclipse.api.factory.Lists;
import com.m3.collections.eclipse.api.factory.primitive.BooleanBags;
import com.m3.collections.eclipse.api.factory.primitive.ByteBags;
import com.m3.collections.eclipse.api.factory.primitive.CharBags;
import com.m3.collections.eclipse.api.factory.primitive.DoubleBags;
import com.m3.collections.eclipse.api.factory.primitive.FloatBags;
import com.m3.collections.eclipse.api.factory.primitive.IntBags;
import com.m3.collections.eclipse.api.factory.primitive.LongBags;
import com.m3.collections.eclipse.api.factory.primitive.ShortBags;
import com.m3.collections.eclipse.api.list.ImmutableList;
import com.m3.collections.eclipse.api.list.MutableList;
import com.m3.collections.eclipse.api.map.ImmutableMap;
import com.m3.collections.eclipse.api.partition.bag.PartitionImmutableBag;
import com.m3.collections.eclipse.api.partition.bag.PartitionMutableBag;
import com.m3.collections.eclipse.api.tuple.primitive.ObjectIntPair;
import com.m3.collections.eclipse.impl.block.factory.Functions;
import com.m3.collections.eclipse.impl.block.factory.Predicates;
import com.m3.collections.eclipse.impl.map.mutable.UnifiedMap;
import com.m3.collections.eclipse.impl.partition.bag.PartitionHashBag;

/**
 * @since 1.0
 */
public abstract class AbstractImmutableBag<T>
        extends AbstractImmutableBagIterable<T>
        implements ImmutableBag<T>
{
    @Override
    public ImmutableBag<T> newWithoutAll(Iterable<? extends T> elements)
    {
        return this.reject(Predicates.in(elements));
    }

    @Override
    public ImmutableBag<T> toImmutable()
    {
        return this;
    }

    @Override
    public ImmutableBag<T> tap(Procedure<? super T> procedure)
    {
        this.forEach(procedure);
        return this;
    }

    @Override
    public <P> ImmutableBag<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.select(Predicates.bind(predicate, parameter));
    }

    @Override
    public <P> ImmutableBag<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.reject(Predicates.bind(predicate, parameter));
    }

    @Override
    public PartitionImmutableBag<T> partition(Predicate<? super T> predicate)
    {
        PartitionMutableBag<T> partitionMutableBag = new PartitionHashBag<>();
        this.forEachWithOccurrences((each, occurrences) -> {
            MutableBag<T> bucket = predicate.accept(each)
                    ? partitionMutableBag.getSelected()
                    : partitionMutableBag.getRejected();
            bucket.addOccurrences(each, occurrences);
        });
        return partitionMutableBag.toImmutable();
    }

    @Override
    public <P> PartitionImmutableBag<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        PartitionMutableBag<T> partitionMutableBag = new PartitionHashBag<>();
        this.forEachWithOccurrences((each, occurrences) -> {
            MutableBag<T> bucket = predicate.accept(each, parameter)
                    ? partitionMutableBag.getSelected()
                    : partitionMutableBag.getRejected();
            bucket.addOccurrences(each, occurrences);
        });
        return partitionMutableBag.toImmutable();
    }

    /**
     * @since 9.0
     */
    @Override
    public <V> ImmutableBag<V> countBy(Function<? super T, ? extends V> function)
    {
        return this.collect(function);
    }

    /**
     * @since 9.0
     */
    @Override
    public <V, P> ImmutableBag<V> countByWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return this.collectWith(function, parameter);
    }

    /**
     * @since 10.0.0
     */
    @Override
    public <V> ImmutableBag<V> countByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.flatCollect(function);
    }

    @Override
    public <V> ImmutableBag<V> collectWithOccurrences(ObjectIntToObjectFunction<? super T, ? extends V> function)
    {
        return this.collectWithOccurrences(function, Bags.mutable.<V>empty()).toImmutable();
    }

    @Override
    public <P, V> ImmutableBag<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return this.collect(Functions.bind(function, parameter));
    }

    @Override
    public ImmutableBooleanBag collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        return this.collectBoolean(booleanFunction, BooleanBags.mutable.empty()).toImmutable();
    }

    @Override
    public ImmutableByteBag collectByte(ByteFunction<? super T> byteFunction)
    {
        return this.collectByte(byteFunction, ByteBags.mutable.empty()).toImmutable();
    }

    @Override
    public ImmutableCharBag collectChar(CharFunction<? super T> charFunction)
    {
        return this.collectChar(charFunction, CharBags.mutable.empty()).toImmutable();
    }

    @Override
    public ImmutableDoubleBag collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        return this.collectDouble(doubleFunction, DoubleBags.mutable.empty()).toImmutable();
    }

    @Override
    public ImmutableFloatBag collectFloat(FloatFunction<? super T> floatFunction)
    {
        return this.collectFloat(floatFunction, FloatBags.mutable.empty()).toImmutable();
    }

    @Override
    public ImmutableIntBag collectInt(IntFunction<? super T> intFunction)
    {
        return this.collectInt(intFunction, IntBags.mutable.empty()).toImmutable();
    }

    @Override
    public ImmutableLongBag collectLong(LongFunction<? super T> longFunction)
    {
        return this.collectLong(longFunction, LongBags.mutable.empty()).toImmutable();
    }

    @Override
    public ImmutableShortBag collectShort(ShortFunction<? super T> shortFunction)
    {
        return this.collectShort(shortFunction, ShortBags.mutable.empty()).toImmutable();
    }

    @Override
    public ImmutableList<ObjectIntPair<T>> topOccurrences(int n)
    {
        MutableList<ObjectIntPair<T>> result = this.occurrencesSortingBy(
                n,
                item -> -item.getTwo(),
                Lists.fixedSize.empty());
        return result.toImmutable();
    }

    @Override
    public ImmutableList<ObjectIntPair<T>> bottomOccurrences(int n)
    {
        MutableList<ObjectIntPair<T>> result = this.occurrencesSortingBy(
                n,
                ObjectIntPair::getTwo,
                Lists.fixedSize.empty());
        return result.toImmutable();
    }

    @Override
    public <V> ImmutableMap<V, T> groupByUniqueKey(Function<? super T, ? extends V> function)
    {
        return this.groupByUniqueKey(function, UnifiedMap.<V, T>newMap(this.size())).toImmutable();
    }

    @Override
    public RichIterable<RichIterable<T>> chunk(int size)
    {
        if (size <= 0)
        {
            throw new IllegalArgumentException("Size for groups must be positive but was: " + size);
        }

        Iterator<T> iterator = this.iterator();
        MutableList<RichIterable<T>> result = Lists.mutable.empty();
        while (iterator.hasNext())
        {
            MutableCollection<T> batch = Bags.mutable.empty();
            for (int i = 0; i < size && iterator.hasNext(); i++)
            {
                batch.add(iterator.next());
            }
            result.add(batch.toImmutable());
        }
        return result.toImmutable();
    }
}
