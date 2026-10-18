/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.api.bag;

import com.m3.collections.eclipse.api.bag.primitive.MutableBooleanBag;
import com.m3.collections.eclipse.api.bag.primitive.MutableByteBag;
import com.m3.collections.eclipse.api.bag.primitive.MutableCharBag;
import com.m3.collections.eclipse.api.bag.primitive.MutableDoubleBag;
import com.m3.collections.eclipse.api.bag.primitive.MutableFloatBag;
import com.m3.collections.eclipse.api.bag.primitive.MutableIntBag;
import com.m3.collections.eclipse.api.bag.primitive.MutableLongBag;
import com.m3.collections.eclipse.api.bag.primitive.MutableShortBag;
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
import com.m3.collections.eclipse.api.block.predicate.primitive.IntPredicate;
import com.m3.collections.eclipse.api.block.procedure.Procedure;
import com.m3.collections.eclipse.api.factory.Sets;
import com.m3.collections.eclipse.api.factory.primitive.BooleanBags;
import com.m3.collections.eclipse.api.factory.primitive.ByteBags;
import com.m3.collections.eclipse.api.factory.primitive.CharBags;
import com.m3.collections.eclipse.api.factory.primitive.DoubleBags;
import com.m3.collections.eclipse.api.factory.primitive.FloatBags;
import com.m3.collections.eclipse.api.factory.primitive.IntBags;
import com.m3.collections.eclipse.api.factory.primitive.LongBags;
import com.m3.collections.eclipse.api.factory.primitive.ShortBags;
import com.m3.collections.eclipse.api.map.MutableMap;
import com.m3.collections.eclipse.api.multimap.bag.MutableBagMultimap;
import com.m3.collections.eclipse.api.ordered.OrderedIterable;
import com.m3.collections.eclipse.api.partition.bag.PartitionMutableBag;
import com.m3.collections.eclipse.api.set.MutableSet;
import com.m3.collections.eclipse.api.tuple.Pair;

/**
 * A MutableBag is a Collection whose elements are unordered and may contain duplicate entries. It varies from
 * MutableCollection in that it adds a protocol for determining, adding, and removing the number of occurrences for an
 * item.
 *
 * @since 1.0
 */
public interface MutableBag<T>
        extends UnsortedBag<T>, MutableBagIterable<T>
{
    @Override
    MutableMap<T, Integer> toMapOfItemToCount();

    @Override
    MutableBag<T> selectByOccurrences(IntPredicate predicate);

    /**
     * @since 9.2
     */
    @Override
    default MutableBag<T> selectDuplicates()
    {
        return this.selectByOccurrences(occurrences -> occurrences > 1);
    }

    /**
     * @since 9.2
     */
    @Override
    default MutableSet<T> selectUnique()
    {
        MutableSet<T> result = Sets.mutable.empty();
        this.forEachWithOccurrences((each, occurrences) ->
        {
            if (occurrences == 1)
            {
                result.add(each);
            }
        });
        return result;
    }

    @Override
    default MutableBag<T> with(T element)
    {
        this.add(element);
        return this;
    }

    @Override
    default MutableBag<T> without(T element)
    {
        this.remove(element);
        return this;
    }

    @Override
    default MutableBag<T> withAll(Iterable<? extends T> elements)
    {
        this.addAllIterable(elements);
        return this;
    }

    @Override
    default MutableBag<T> withoutAll(Iterable<? extends T> elements)
    {
        this.removeAllIterable(elements);
        return this;
    }

    @Override
    MutableBag<T> newEmpty();

    @Override
    MutableBag<T> asUnmodifiable();

    @Override
    MutableBag<T> asSynchronized();

    @Override
    PartitionMutableBag<T> partition(Predicate<? super T> predicate);

    @Override
    <P> PartitionMutableBag<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    <V> MutableBagMultimap<V, T> groupBy(Function<? super T, ? extends V> function);

    @Override
    <V> MutableBagMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function);

    /**
     * @since 9.0
     */
    @Override
    default <V> MutableBag<V> countBy(Function<? super T, ? extends V> function)
    {
        return this.collect(function);
    }

    /**
     * @since 9.0
     */
    @Override
    default <V, P> MutableBag<V> countByWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return this.collectWith(function, parameter);
    }

    /**
     * @since 10.0.0
     */
    @Override
    default <V> MutableBag<V> countByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.flatCollect(function);
    }

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Override
    @Deprecated
    <S> MutableBag<Pair<T, S>> zip(Iterable<S> that);

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Override
    @Deprecated
    MutableSet<Pair<T, Integer>> zipWithIndex();

    @Override
    MutableBag<T> tap(Procedure<? super T> procedure);

    @Override
    MutableBag<T> select(Predicate<? super T> predicate);

    @Override
    <P> MutableBag<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    MutableBag<T> reject(Predicate<? super T> predicate);

    @Override
    <P> MutableBag<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    <S> MutableBag<S> selectInstancesOf(Class<S> clazz);

    @Override
    <V> MutableBag<V> collect(Function<? super T, ? extends V> function);

    @Override
    default MutableByteBag collectByte(ByteFunction<? super T> byteFunction)
    {
        return this.collectByte(byteFunction, ByteBags.mutable.empty());
    }

    @Override
    default MutableCharBag collectChar(CharFunction<? super T> charFunction)
    {
        return this.collectChar(charFunction, CharBags.mutable.empty());
    }

    @Override
    default MutableIntBag collectInt(IntFunction<? super T> intFunction)
    {
        return this.collectInt(intFunction, IntBags.mutable.empty());
    }

    @Override
    default MutableBooleanBag collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        return this.collectBoolean(booleanFunction, BooleanBags.mutable.empty());
    }

    @Override
    default MutableDoubleBag collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        return this.collectDouble(doubleFunction, DoubleBags.mutable.empty());
    }

    @Override
    default MutableFloatBag collectFloat(FloatFunction<? super T> floatFunction)
    {
        return this.collectFloat(floatFunction, FloatBags.mutable.empty());
    }

    @Override
    default MutableLongBag collectLong(LongFunction<? super T> longFunction)
    {
        return this.collectLong(longFunction, LongBags.mutable.empty());
    }

    @Override
    default MutableShortBag collectShort(ShortFunction<? super T> shortFunction)
    {
        return this.collectShort(shortFunction, ShortBags.mutable.empty());
    }

    @Override
    <P, V> MutableBag<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter);

    @Override
    <V> MutableBag<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function);

    @Override
    <V> MutableBag<V> collectWithOccurrences(ObjectIntToObjectFunction<? super T, ? extends V> function);

    @Override
    <V> MutableBag<V> flatCollect(Function<? super T, ? extends Iterable<V>> function);

    /**
     * @since 9.2
     */
    @Override
    default <P, V> MutableBag<V> flatCollectWith(Function2<? super T, ? super P, ? extends Iterable<V>> function, P parameter)
    {
        return this.flatCollect(each -> function.apply(each, parameter));
    }

    /**
     * @since 8.0
     */
    @Override
    ImmutableBag<T> toImmutable();

    /**
     * Converts the MutableBag to the default ImmutableBag implementation.
     *
     * @since 11.0
     */
    @Override
    default ImmutableBag<T> toImmutableBag()
    {
        return this.toImmutable();
    }
}
