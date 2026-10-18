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
import com.m3.collections.eclipse.api.factory.primitive.BooleanSets;
import com.m3.collections.eclipse.api.factory.primitive.ByteSets;
import com.m3.collections.eclipse.api.factory.primitive.CharSets;
import com.m3.collections.eclipse.api.factory.primitive.DoubleSets;
import com.m3.collections.eclipse.api.factory.primitive.FloatSets;
import com.m3.collections.eclipse.api.factory.primitive.IntSets;
import com.m3.collections.eclipse.api.factory.primitive.LongSets;
import com.m3.collections.eclipse.api.factory.primitive.ShortSets;
import com.m3.collections.eclipse.api.multimap.set.MutableSetMultimap;
import com.m3.collections.eclipse.api.ordered.OrderedIterable;
import com.m3.collections.eclipse.api.partition.set.PartitionMutableSet;
import com.m3.collections.eclipse.api.set.primitive.MutableBooleanSet;
import com.m3.collections.eclipse.api.set.primitive.MutableByteSet;
import com.m3.collections.eclipse.api.set.primitive.MutableCharSet;
import com.m3.collections.eclipse.api.set.primitive.MutableDoubleSet;
import com.m3.collections.eclipse.api.set.primitive.MutableFloatSet;
import com.m3.collections.eclipse.api.set.primitive.MutableIntSet;
import com.m3.collections.eclipse.api.set.primitive.MutableLongSet;
import com.m3.collections.eclipse.api.set.primitive.MutableShortSet;
import com.m3.collections.eclipse.api.tuple.Pair;

/**
 * A MutableSet is an extension java.util.Set which provides methods matching the Smalltalk Collection protocol.
 */
public interface MutableSet<T>
        extends UnsortedSetIterable<T>, MutableSetIterable<T>, Cloneable
{
    @Override
    default MutableSet<T> with(T element)
    {
        this.add(element);
        return this;
    }

    @Override
    default MutableSet<T> without(T element)
    {
        this.remove(element);
        return this;
    }

    @Override
    default MutableSet<T> withAll(Iterable<? extends T> elements)
    {
        this.addAllIterable(elements);
        return this;
    }

    @Override
    default MutableSet<T> withoutAll(Iterable<? extends T> elements)
    {
        this.removeAllIterable(elements);
        return this;
    }

    @Override
    MutableSet<T> newEmpty();

    MutableSet<T> clone();

    @Override
    MutableSet<T> tap(Procedure<? super T> procedure);

    @Override
    MutableSet<T> select(Predicate<? super T> predicate);

    @Override
    <P> MutableSet<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    MutableSet<T> reject(Predicate<? super T> predicate);

    @Override
    <P> MutableSet<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    PartitionMutableSet<T> partition(Predicate<? super T> predicate);

    @Override
    <P> PartitionMutableSet<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    <S> MutableSet<S> selectInstancesOf(Class<S> clazz);

    @Override
    <V> MutableSet<V> collect(Function<? super T, ? extends V> function);

    @Override
    default MutableBooleanSet collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        return this.collectBoolean(booleanFunction, BooleanSets.mutable.empty());
    }

    @Override
    default MutableByteSet collectByte(ByteFunction<? super T> byteFunction)
    {
        return this.collectByte(byteFunction, ByteSets.mutable.empty());
    }

    @Override
    default MutableCharSet collectChar(CharFunction<? super T> charFunction)
    {
        return this.collectChar(charFunction, CharSets.mutable.empty());
    }

    @Override
    default MutableDoubleSet collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        return this.collectDouble(doubleFunction, DoubleSets.mutable.empty());
    }

    @Override
    default MutableFloatSet collectFloat(FloatFunction<? super T> floatFunction)
    {
        return this.collectFloat(floatFunction, FloatSets.mutable.empty());
    }

    @Override
    default MutableIntSet collectInt(IntFunction<? super T> intFunction)
    {
        return this.collectInt(intFunction, IntSets.mutable.empty());
    }

    @Override
    default MutableLongSet collectLong(LongFunction<? super T> longFunction)
    {
        return this.collectLong(longFunction, LongSets.mutable.empty());
    }

    @Override
    default MutableShortSet collectShort(ShortFunction<? super T> shortFunction)
    {
        return this.collectShort(shortFunction, ShortSets.mutable.empty());
    }

    @Override
    <P, V> MutableSet<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter);

    @Override
    <V> MutableSet<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function);

    @Override
    <V> MutableSet<V> flatCollect(Function<? super T, ? extends Iterable<V>> function);

    /**
     * @since 9.2
     */
    @Override
    default <P, V> MutableSet<V> flatCollectWith(Function2<? super T, ? super P, ? extends Iterable<V>> function, P parameter)
    {
        return this.flatCollect(each -> function.apply(each, parameter));
    }

    /**
     * Returns an unmodifiable view of the set.
     *
     * @return an unmodifiable view of this set
     */
    @Override
    MutableSet<T> asUnmodifiable();

    @Override
    MutableSet<T> asSynchronized();

    /**
     * Returns an immutable copy of this set. If the set is immutable, it returns itself.
     */
    @Override
    ImmutableSet<T> toImmutable();

    @Override
    <V> MutableSetMultimap<V, T> groupBy(Function<? super T, ? extends V> function);

    @Override
    <V> MutableSetMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function);

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Override
    @Deprecated
    <S> MutableSet<Pair<T, S>> zip(Iterable<S> that);

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Override
    @Deprecated
    MutableSet<Pair<T, Integer>> zipWithIndex();

    @Override
    MutableSet<T> union(SetIterable<? extends T> set);

    @Override
    MutableSet<T> intersect(SetIterable<? extends T> set);

    @Override
    MutableSet<T> difference(SetIterable<? extends T> subtrahendSet);

    @Override
    MutableSet<T> symmetricDifference(SetIterable<? extends T> setB);

    @Override
    MutableSet<UnsortedSetIterable<T>> powerSet();

    /**
     * Converts the MutableSet to the default ImmutableSet implementation.
     *
     * @since 11.0
     */
    @Override
    default ImmutableSet<T> toImmutableSet()
    {
        return this.toImmutable();
    }
}
