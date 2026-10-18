/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.api.set;

import java.util.Set;

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
import com.m3.collections.eclipse.api.multimap.set.ImmutableSetMultimap;
import com.m3.collections.eclipse.api.ordered.OrderedIterable;
import com.m3.collections.eclipse.api.partition.set.PartitionImmutableSet;
import com.m3.collections.eclipse.api.set.primitive.ImmutableBooleanSet;
import com.m3.collections.eclipse.api.set.primitive.ImmutableByteSet;
import com.m3.collections.eclipse.api.set.primitive.ImmutableCharSet;
import com.m3.collections.eclipse.api.set.primitive.ImmutableDoubleSet;
import com.m3.collections.eclipse.api.set.primitive.ImmutableFloatSet;
import com.m3.collections.eclipse.api.set.primitive.ImmutableIntSet;
import com.m3.collections.eclipse.api.set.primitive.ImmutableLongSet;
import com.m3.collections.eclipse.api.set.primitive.ImmutableShortSet;
import com.m3.collections.eclipse.api.tuple.Pair;

/**
 * ImmutableSet is the non-modifiable equivalent interface to {@link MutableSet}. {@link MutableSet#toImmutable()} will
 * give you an appropriately trimmed implementation of ImmutableSet. All ImmutableSet implementations must implement
 * the java.util.Set interface in order to satisfy the equals() contract and be compared against other set structures
 * like UnifiedSet or HashSet.
 */
public interface ImmutableSet<T>
        extends UnsortedSetIterable<T>, ImmutableSetIterable<T>
{
    @Override
    ImmutableSet<T> newWith(T element);

    @Override
    ImmutableSet<T> newWithout(T element);

    @Override
    ImmutableSet<T> newWithAll(Iterable<? extends T> elements);

    @Override
    ImmutableSet<T> newWithoutAll(Iterable<? extends T> elements);

    @Override
    ImmutableSet<T> tap(Procedure<? super T> procedure);

    @Override
    ImmutableSet<T> select(Predicate<? super T> predicate);

    @Override
    <P> ImmutableSet<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    ImmutableSet<T> reject(Predicate<? super T> predicate);

    @Override
    <P> ImmutableSet<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    PartitionImmutableSet<T> partition(Predicate<? super T> predicate);

    @Override
    <P> PartitionImmutableSet<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    <S> ImmutableSet<S> selectInstancesOf(Class<S> clazz);

    @Override
    <V> ImmutableSet<V> collect(Function<? super T, ? extends V> function);

    @Override
    ImmutableBooleanSet collectBoolean(BooleanFunction<? super T> booleanFunction);

    @Override
    ImmutableByteSet collectByte(ByteFunction<? super T> byteFunction);

    @Override
    ImmutableCharSet collectChar(CharFunction<? super T> charFunction);

    @Override
    ImmutableDoubleSet collectDouble(DoubleFunction<? super T> doubleFunction);

    @Override
    ImmutableFloatSet collectFloat(FloatFunction<? super T> floatFunction);

    @Override
    ImmutableIntSet collectInt(IntFunction<? super T> intFunction);

    @Override
    ImmutableLongSet collectLong(LongFunction<? super T> longFunction);

    @Override
    ImmutableShortSet collectShort(ShortFunction<? super T> shortFunction);

    @Override
    <P, V> ImmutableSet<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter);

    @Override
    <V> ImmutableSet<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function);

    @Override
    <V> ImmutableSet<V> flatCollect(Function<? super T, ? extends Iterable<V>> function);

    /**
     * @since 9.2
     */
    @Override
    default <P, V> ImmutableSet<V> flatCollectWith(Function2<? super T, ? super P, ? extends Iterable<V>> function, P parameter)
    {
        return this.flatCollect(each -> function.apply(each, parameter));
    }

    @Override
    <V> ImmutableSetMultimap<V, T> groupBy(Function<? super T, ? extends V> function);

    @Override
    <V> ImmutableSetMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function);

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Override
    @Deprecated
    <S> ImmutableSet<Pair<T, S>> zip(Iterable<S> that);

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Override
    @Deprecated
    ImmutableSet<Pair<T, Integer>> zipWithIndex();

    Set<T> castToSet();

    @Override
    ImmutableSet<T> union(SetIterable<? extends T> set);

    @Override
    ImmutableSet<T> intersect(SetIterable<? extends T> set);

    @Override
    ImmutableSet<T> difference(SetIterable<? extends T> subtrahendSet);

    @Override
    ImmutableSet<T> symmetricDifference(SetIterable<? extends T> setB);

    @Override
    ImmutableSet<UnsortedSetIterable<T>> powerSet();

    /**
     * Overrides toImmutableSet in RichIterable to return this.
     *
     * @since 11.0
     */
    @Override
    default ImmutableSet<T> toImmutableSet()
    {
        return this;
    }
}
