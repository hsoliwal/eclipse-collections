/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.api.ordered;

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
import com.m3.collections.eclipse.api.block.function.primitive.ObjectIntToObjectFunction;
import com.m3.collections.eclipse.api.block.function.primitive.ShortFunction;
import com.m3.collections.eclipse.api.block.predicate.Predicate;
import com.m3.collections.eclipse.api.block.predicate.Predicate2;
import com.m3.collections.eclipse.api.block.procedure.Procedure;
import com.m3.collections.eclipse.api.block.procedure.primitive.ObjectIntProcedure;
import com.m3.collections.eclipse.api.multimap.ordered.ReversibleIterableMultimap;
import com.m3.collections.eclipse.api.ordered.primitive.ReversibleBooleanIterable;
import com.m3.collections.eclipse.api.ordered.primitive.ReversibleByteIterable;
import com.m3.collections.eclipse.api.ordered.primitive.ReversibleCharIterable;
import com.m3.collections.eclipse.api.ordered.primitive.ReversibleDoubleIterable;
import com.m3.collections.eclipse.api.ordered.primitive.ReversibleFloatIterable;
import com.m3.collections.eclipse.api.ordered.primitive.ReversibleIntIterable;
import com.m3.collections.eclipse.api.ordered.primitive.ReversibleLongIterable;
import com.m3.collections.eclipse.api.ordered.primitive.ReversibleShortIterable;
import com.m3.collections.eclipse.api.partition.ordered.PartitionReversibleIterable;
import com.m3.collections.eclipse.api.tuple.Pair;

/**
 * A ReversibleIterable is an ordered iterable that you can iterate over forwards or backwards.
 * It has methods that support efficient iteration from the end, including {@link #asReversed()} and
 * {@link #reverseForEach(Procedure)}.
 *
 * @since 5.0
 */
public interface ReversibleIterable<T> extends OrderedIterable<T>
{
    /**
     * Evaluates the procedure for each element of the list iterating in reverse order.
     *
     * <pre>e.g.
     * people.reverseForEach(person -&gt; LOGGER.info(person.getName()));
     * </pre>
     */
    default void reverseForEach(Procedure<? super T> procedure)
    {
        if (this.notEmpty())
        {
            this.forEach(this.size() - 1, 0, procedure);
        }
    }

    /**
     * Evaluates the procedure for each element and it's index in reverse order.
     * <pre>e.g.
     * people.reverseForEachWithIndex((person, index) -&gt;
     *         LOGGER.info("Index: " + index + " person: " + person.getName()));
     * </pre>
     *
     * @since 9.0.0
     */
    default void reverseForEachWithIndex(ObjectIntProcedure<? super T> procedure)
    {
        if (this.notEmpty())
        {
            this.forEachWithIndex(this.size() - 1, 0, procedure);
        }
    }

    /**
     * Returns a reversed view of this ReversibleIterable.
     */
    default LazyIterable<T> asReversed()
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".asReversed() not implemented yet");
    }

    /**
     * Returns a new ReversibleIterable in reverse order.
     *
     * @since 6.0.0
     */
    ReversibleIterable<T> toReversed();

    /**
     * Returns the index of the last element of the {@code ReversibleIterable} for which the {@code predicate} evaluates to true.
     * Returns -1 if no element evaluates true for the {@code predicate}.
     *
     * @since 6.0
     */
    int detectLastIndex(Predicate<? super T> predicate);

    /**
     * Returns the first {@code count} elements of the iterable
     * or all the elements in the iterable if {@code count} is greater than the length of
     * the iterable.
     *
     * @param count the number of items to take.
     * @throws IllegalArgumentException if {@code count} is less than zero
     * @since 6.0
     */
    ReversibleIterable<T> take(int count);

    /**
     * Returns the initial elements that satisfy the Predicate. Short circuits at the first element which does not
     * satisfy the Predicate.
     */
    @Override
    ReversibleIterable<T> takeWhile(Predicate<? super T> predicate);

    /**
     * Returns an iterable after skipping the first {@code count} elements
     * or an empty iterable if the {@code count} is greater than the length of the iterable.
     *
     * @param count the number of items to drop.
     * @throws IllegalArgumentException if {@code count} is less than zero
     * @since 6.0
     */
    ReversibleIterable<T> drop(int count);

    /**
     * Returns the final elements that do not satisfy the Predicate. Short circuits at the first element which does
     * satisfy the Predicate.
     */
    @Override
    ReversibleIterable<T> dropWhile(Predicate<? super T> predicate);

    @Override
    PartitionReversibleIterable<T> partitionWhile(Predicate<? super T> predicate);

    @Override
    ReversibleIterable<T> distinct();

    @Override
    ReversibleIterable<T> tap(Procedure<? super T> procedure);

    @Override
    ReversibleIterable<T> select(Predicate<? super T> predicate);

    @Override
    <P> ReversibleIterable<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    ReversibleIterable<T> reject(Predicate<? super T> predicate);

    @Override
    <P> ReversibleIterable<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    PartitionReversibleIterable<T> partition(Predicate<? super T> predicate);

    @Override
    <P> PartitionReversibleIterable<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    <S> ReversibleIterable<S> selectInstancesOf(Class<S> clazz);

    @Override
    <V> ReversibleIterable<V> collect(Function<? super T, ? extends V> function);

    /**
     * @since 9.1.
     */
    @Override
    default <V> ReversibleIterable<V> collectWithIndex(ObjectIntToObjectFunction<? super T, ? extends V> function)
    {
        int[] index = {0};
        return this.collect(each -> function.valueOf(each, index[0]++));
    }

    @Override
    <P, V> ReversibleIterable<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter);

    @Override
    <V> ReversibleIterable<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function);

    @Override
    <V> ReversibleIterable<V> flatCollect(Function<? super T, ? extends Iterable<V>> function);

    /**
     * @since 9.2
     */
    @Override
    default <P, V> ReversibleIterable<V> flatCollectWith(Function2<? super T, ? super P, ? extends Iterable<V>> function, P parameter)
    {
        return this.flatCollect(each -> function.apply(each, parameter));
    }

    @Override
    ReversibleBooleanIterable collectBoolean(BooleanFunction<? super T> booleanFunction);

    @Override
    ReversibleByteIterable collectByte(ByteFunction<? super T> byteFunction);

    @Override
    ReversibleCharIterable collectChar(CharFunction<? super T> charFunction);

    @Override
    ReversibleDoubleIterable collectDouble(DoubleFunction<? super T> doubleFunction);

    @Override
    ReversibleFloatIterable collectFloat(FloatFunction<? super T> floatFunction);

    @Override
    ReversibleIntIterable collectInt(IntFunction<? super T> intFunction);

    @Override
    ReversibleLongIterable collectLong(LongFunction<? super T> longFunction);

    @Override
    ReversibleShortIterable collectShort(ShortFunction<? super T> shortFunction);

    @Override
    <V> ReversibleIterableMultimap<V, T> groupBy(Function<? super T, ? extends V> function);

    @Override
    <V> ReversibleIterableMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function);

    @Override
    <S> ReversibleIterable<Pair<T, S>> zip(Iterable<S> that);

    @Override
    ReversibleIterable<Pair<T, Integer>> zipWithIndex();
}
