/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.stack.immutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.m3.collections.eclipse.api.BooleanIterable;
import com.m3.collections.eclipse.api.ByteIterable;
import com.m3.collections.eclipse.api.CharIterable;
import com.m3.collections.eclipse.api.DoubleIterable;
import com.m3.collections.eclipse.api.FloatIterable;
import com.m3.collections.eclipse.api.IntIterable;
import com.m3.collections.eclipse.api.LongIterable;
import com.m3.collections.eclipse.api.RichIterable;
import com.m3.collections.eclipse.api.ShortIterable;
import com.m3.collections.eclipse.api.bag.ImmutableBag;
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
import com.m3.collections.eclipse.api.block.procedure.primitive.ObjectIntProcedure;
import com.m3.collections.eclipse.api.collection.primitive.MutableBooleanCollection;
import com.m3.collections.eclipse.api.collection.primitive.MutableByteCollection;
import com.m3.collections.eclipse.api.collection.primitive.MutableCharCollection;
import com.m3.collections.eclipse.api.collection.primitive.MutableDoubleCollection;
import com.m3.collections.eclipse.api.collection.primitive.MutableFloatCollection;
import com.m3.collections.eclipse.api.collection.primitive.MutableIntCollection;
import com.m3.collections.eclipse.api.collection.primitive.MutableLongCollection;
import com.m3.collections.eclipse.api.collection.primitive.MutableShortCollection;
import com.m3.collections.eclipse.api.factory.Bags;
import com.m3.collections.eclipse.api.factory.Lists;
import com.m3.collections.eclipse.api.factory.Maps;
import com.m3.collections.eclipse.api.factory.Stacks;
import com.m3.collections.eclipse.api.factory.primitive.BooleanStacks;
import com.m3.collections.eclipse.api.factory.primitive.ByteStacks;
import com.m3.collections.eclipse.api.factory.primitive.CharStacks;
import com.m3.collections.eclipse.api.factory.primitive.DoubleStacks;
import com.m3.collections.eclipse.api.factory.primitive.FloatStacks;
import com.m3.collections.eclipse.api.factory.primitive.IntStacks;
import com.m3.collections.eclipse.api.factory.primitive.LongStacks;
import com.m3.collections.eclipse.api.factory.primitive.ObjectDoubleMaps;
import com.m3.collections.eclipse.api.factory.primitive.ObjectLongMaps;
import com.m3.collections.eclipse.api.factory.primitive.ShortStacks;
import com.m3.collections.eclipse.api.list.ListIterable;
import com.m3.collections.eclipse.api.map.ImmutableMap;
import com.m3.collections.eclipse.api.map.MutableMapIterable;
import com.m3.collections.eclipse.api.map.primitive.ImmutableObjectDoubleMap;
import com.m3.collections.eclipse.api.map.primitive.ImmutableObjectLongMap;
import com.m3.collections.eclipse.api.multimap.MutableMultimap;
import com.m3.collections.eclipse.api.multimap.list.ImmutableListMultimap;
import com.m3.collections.eclipse.api.ordered.OrderedIterable;
import com.m3.collections.eclipse.api.partition.list.PartitionImmutableList;
import com.m3.collections.eclipse.api.partition.stack.PartitionImmutableStack;
import com.m3.collections.eclipse.api.stack.ImmutableStack;
import com.m3.collections.eclipse.api.stack.MutableStack;
import com.m3.collections.eclipse.api.stack.StackIterable;
import com.m3.collections.eclipse.api.stack.primitive.ImmutableBooleanStack;
import com.m3.collections.eclipse.api.stack.primitive.ImmutableByteStack;
import com.m3.collections.eclipse.api.stack.primitive.ImmutableCharStack;
import com.m3.collections.eclipse.api.stack.primitive.ImmutableDoubleStack;
import com.m3.collections.eclipse.api.stack.primitive.ImmutableFloatStack;
import com.m3.collections.eclipse.api.stack.primitive.ImmutableIntStack;
import com.m3.collections.eclipse.api.stack.primitive.ImmutableLongStack;
import com.m3.collections.eclipse.api.stack.primitive.ImmutableShortStack;
import com.m3.collections.eclipse.api.tuple.Pair;
import com.m3.collections.eclipse.impl.AbstractRichIterable;
import com.m3.collections.eclipse.impl.EmptyIterator;
import com.m3.collections.eclipse.impl.factory.Multimaps;
import com.m3.collections.eclipse.impl.partition.list.PartitionFastList;
import com.m3.collections.eclipse.impl.tuple.Tuples;

final class ImmutableEmptyStack<T>
        extends AbstractRichIterable<T>
        implements ImmutableStack<T>, Serializable
{
    static final ImmutableStack<?> INSTANCE = new ImmutableEmptyStack<>();
    private static final PartitionImmutableList<?> EMPTY = new PartitionFastList<>().toImmutable();

    private static final long serialVersionUID = 1L;

    private Object readResolve()
    {
        return INSTANCE;
    }

    @Override
    public ImmutableStack<T> push(T item)
    {
        return new ImmutableNotEmptyStack<>(item, this);
    }

    @Override
    public ImmutableStack<T> pop()
    {
        throw new EmptyStackException();
    }

    @Override
    public ImmutableStack<T> pop(int count)
    {
        this.checkNegativeCount(count);
        if (this.checkZeroCount(count))
        {
            return this;
        }
        throw new EmptyStackException();
    }

    @Override
    public Pair<T, ImmutableStack<T>> peekAndPop()
    {
        throw new EmptyStackException();
    }

    @Override
    public Pair<ListIterable<T>, ImmutableStack<T>> peekAndPop(int count)
    {
        return Tuples.pair(this.peek(count), this.pop(count));
    }

    @Override
    public T peek()
    {
        throw new EmptyStackException();
    }

    @Override
    public ListIterable<T> peek(int count)
    {
        this.checkNegativeCount(count);
        if (this.checkZeroCount(count))
        {
            return Lists.mutable.empty();
        }
        throw new EmptyStackException();
    }

    private boolean checkZeroCount(int count)
    {
        return count == 0;
    }

    private void checkNegativeCount(int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be positive but was " + count);
        }
    }

    @Override
    public T peekAt(int index)
    {
        this.checkNegativeCount(index);
        throw new EmptyStackException();
    }

    @Override
    public T getFirst()
    {
        throw new EmptyStackException();
    }

    @Override
    public T getLast()
    {
        throw new UnsupportedOperationException("Cannot call getLast() on " + this.getClass().getSimpleName());
    }

    @Override
    public MutableStack<T> toStack()
    {
        return Stacks.mutable.empty();
    }

    @Override
    public ImmutableStack<T> select(Predicate<? super T> predicate)
    {
        return this;
    }

    @Override
    public <P> ImmutableStack<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this;
    }

    @Override
    public <R extends Collection<T>> R select(Predicate<? super T> predicate, R target)
    {
        return target;
    }

    @Override
    public <P, R extends Collection<T>> R selectWith(Predicate2<? super T, ? super P> predicate, P parameter, R target)
    {
        return target;
    }

    @Override
    public ImmutableStack<T> reject(Predicate<? super T> predicate)
    {
        return this;
    }

    @Override
    public <R extends Collection<T>> R reject(Predicate<? super T> predicate, R target)
    {
        return target;
    }

    @Override
    public <P> ImmutableStack<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this;
    }

    @Override
    public <P, R extends Collection<T>> R rejectWith(Predicate2<? super T, ? super P> predicate, P parameter, R target)
    {
        return target;
    }

    @Override
    public PartitionImmutableStack<T> partition(Predicate<? super T> predicate)
    {
        return (PartitionImmutableStack<T>) EMPTY;
    }

    @Override
    public <P> PartitionImmutableStack<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return (PartitionImmutableStack<T>) EMPTY;
    }

    @Override
    public <S> ImmutableStack<S> selectInstancesOf(Class<S> clazz)
    {
        return Stacks.immutable.empty();
    }

    @Override
    public <V> ImmutableStack<V> collect(Function<? super T, ? extends V> function)
    {
        return Stacks.immutable.empty();
    }

    @Override
    public ImmutableBooleanStack collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        return BooleanStacks.immutable.empty();
    }

    @Override
    public <R extends MutableBooleanCollection> R collectBoolean(BooleanFunction<? super T> booleanFunction, R target)
    {
        return target;
    }

    @Override
    public <R extends MutableBooleanCollection> R flatCollectBoolean(
            Function<? super T, ? extends BooleanIterable> function, R target)
    {
        return target;
    }

    @Override
    public ImmutableByteStack collectByte(ByteFunction<? super T> byteFunction)
    {
        return ByteStacks.immutable.empty();
    }

    @Override
    public <R extends MutableByteCollection> R collectByte(ByteFunction<? super T> byteFunction, R target)
    {
        return target;
    }

    @Override
    public <R extends MutableByteCollection> R flatCollectByte(
            Function<? super T, ? extends ByteIterable> function, R target)
    {
        return target;
    }

    @Override
    public ImmutableCharStack collectChar(CharFunction<? super T> charFunction)
    {
        return CharStacks.immutable.empty();
    }

    @Override
    public <R extends MutableCharCollection> R collectChar(CharFunction<? super T> charFunction, R target)
    {
        return target;
    }

    @Override
    public <R extends MutableCharCollection> R flatCollectChar(
            Function<? super T, ? extends CharIterable> function, R target)
    {
        return target;
    }

    @Override
    public ImmutableDoubleStack collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        return DoubleStacks.immutable.empty();
    }

    @Override
    public <R extends MutableDoubleCollection> R collectDouble(DoubleFunction<? super T> doubleFunction, R target)
    {
        return target;
    }

    @Override
    public <R extends MutableDoubleCollection> R flatCollectDouble(
            Function<? super T, ? extends DoubleIterable> function, R target)
    {
        return target;
    }

    @Override
    public ImmutableFloatStack collectFloat(FloatFunction<? super T> floatFunction)
    {
        return FloatStacks.immutable.empty();
    }

    @Override
    public <R extends MutableFloatCollection> R collectFloat(FloatFunction<? super T> floatFunction, R target)
    {
        return target;
    }

    @Override
    public <R extends MutableFloatCollection> R flatCollectFloat(
            Function<? super T, ? extends FloatIterable> function, R target)
    {
        return target;
    }

    @Override
    public ImmutableIntStack collectInt(IntFunction<? super T> intFunction)
    {
        return IntStacks.immutable.empty();
    }

    @Override
    public <R extends MutableIntCollection> R collectInt(IntFunction<? super T> intFunction, R target)
    {
        return target;
    }

    @Override
    public <R extends MutableIntCollection> R flatCollectInt(
            Function<? super T, ? extends IntIterable> function, R target)
    {
        return target;
    }

    @Override
    public ImmutableLongStack collectLong(LongFunction<? super T> longFunction)
    {
        return LongStacks.immutable.empty();
    }

    @Override
    public <R extends MutableLongCollection> R collectLong(LongFunction<? super T> longFunction, R target)
    {
        return target;
    }

    @Override
    public <R extends MutableLongCollection> R flatCollectLong(
            Function<? super T, ? extends LongIterable> function, R target)
    {
        return target;
    }

    @Override
    public ImmutableShortStack collectShort(ShortFunction<? super T> shortFunction)
    {
        return ShortStacks.immutable.empty();
    }

    @Override
    public <R extends MutableShortCollection> R collectShort(ShortFunction<? super T> shortFunction, R target)
    {
        return target;
    }

    @Override
    public <R extends MutableShortCollection> R flatCollectShort(
            Function<? super T, ? extends ShortIterable> function, R target)
    {
        return target;
    }

    @Override
    public <V, R extends Collection<V>> R collect(Function<? super T, ? extends V> function, R target)
    {
        return target;
    }

    @Override
    public <P, V> ImmutableStack<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return Stacks.immutable.empty();
    }

    @Override
    public <P, V, R extends Collection<V>> R collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter, R target)
    {
        return target;
    }

    @Override
    public <V> ImmutableStack<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function)
    {
        return Stacks.immutable.empty();
    }

    @Override
    public <V, R extends Collection<V>> R collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function, R target)
    {
        return target;
    }

    @Override
    public <V> ImmutableStack<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return Stacks.immutable.empty();
    }

    @Override
    public <V, R extends Collection<V>> R flatCollect(Function<? super T, ? extends Iterable<V>> function, R target)
    {
        return target;
    }

    @Override
    public T detect(Predicate<? super T> predicate)
    {
        return null;
    }

    @Override
    public <P> T detectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return null;
    }

    @Override
    public Optional<T> detectOptional(Predicate<? super T> predicate)
    {
        return Optional.empty();
    }

    @Override
    public <P> Optional<T> detectWithOptional(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return Optional.empty();
    }

    @Override
    public T detectIfNone(Predicate<? super T> predicate, Function0<? extends T> function)
    {
        return function.value();
    }

    @Override
    public <P> T detectWithIfNone(Predicate2<? super T, ? super P> predicate, P parameter, Function0<? extends T> function)
    {
        return function.value();
    }

    @Override
    public int count(Predicate<? super T> predicate)
    {
        return 0;
    }

    @Override
    public <P> int countWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return 0;
    }

    @Override
    public <R extends Collection<T>> R into(R target)
    {
        return target;
    }

    @Override
    public <NK, NV, R extends Map<NK, NV>> R toMap(Function<? super T, ? extends NK> keyFunction, Function<? super T, ? extends NV> valueFunction, R target)
    {
        return target;
    }

    @Override
    public T min(Comparator<? super T> comparator)
    {
        throw new NoSuchElementException();
    }

    @Override
    public T max(Comparator<? super T> comparator)
    {
        throw new NoSuchElementException();
    }

    @Override
    public T min()
    {
        throw new NoSuchElementException();
    }

    @Override
    public T max()
    {
        throw new NoSuchElementException();
    }

    @Override
    public <V extends Comparable<? super V>> T minBy(Function<? super T, ? extends V> function)
    {
        throw new NoSuchElementException();
    }

    @Override
    public <V extends Comparable<? super V>> T maxBy(Function<? super T, ? extends V> function)
    {
        throw new NoSuchElementException();
    }

    @Override
    public <V> ImmutableObjectLongMap<V> sumByInt(Function<? super T, ? extends V> groupBy, IntFunction<? super T> function)
    {
        return ObjectLongMaps.immutable.empty();
    }

    @Override
    public <V> ImmutableObjectDoubleMap<V> sumByFloat(Function<? super T, ? extends V> groupBy, FloatFunction<? super T> function)
    {
        return ObjectDoubleMaps.immutable.empty();
    }

    @Override
    public <V> ImmutableObjectLongMap<V> sumByLong(Function<? super T, ? extends V> groupBy, LongFunction<? super T> function)
    {
        return ObjectLongMaps.immutable.empty();
    }

    @Override
    public <V> ImmutableObjectDoubleMap<V> sumByDouble(Function<? super T, ? extends V> groupBy, DoubleFunction<? super T> function)
    {
        return ObjectDoubleMaps.immutable.empty();
    }

    @Override
    public <V> ImmutableListMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return Multimaps.immutable.list.empty();
    }

    @Override
    public <V, R extends MutableMultimap<V, T>> R groupBy(Function<? super T, ? extends V> function, R target)
    {
        return target;
    }

    @Override
    public <V> ImmutableListMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return Multimaps.immutable.list.empty();
    }

    @Override
    public <V, R extends MutableMultimap<V, T>> R groupByEach(Function<? super T, ? extends Iterable<V>> function, R target)
    {
        return target;
    }

    @Override
    public <V> ImmutableMap<V, T> groupByUniqueKey(Function<? super T, ? extends V> function)
    {
        return Maps.immutable.empty();
    }

    @Override
    public <V, R extends MutableMapIterable<V, T>> R groupByUniqueKey(Function<? super T, ? extends V> function, R target)
    {
        return target;
    }

    @Override
    public <S> ImmutableStack<Pair<T, S>> zip(Iterable<S> that)
    {
        return Stacks.immutable.empty();
    }

    @Override
    public <S, R extends Collection<Pair<T, S>>> R zip(Iterable<S> that, R target)
    {
        return target;
    }

    @Override
    public ImmutableStack<Pair<T, Integer>> zipWithIndex()
    {
        return Stacks.immutable.empty();
    }

    @Override
    public <R extends Collection<Pair<T, Integer>>> R zipWithIndex(R target)
    {
        return target;
    }

    @Override
    public ImmutableStack<T> toImmutable()
    {
        return this;
    }

    @Override
    public RichIterable<RichIterable<T>> chunk(int size)
    {
        if (size <= 0)
        {
            throw new IllegalArgumentException("Size for groups must be positive but was: " + size);
        }

        return Lists.immutable.empty();
    }

    @Override
    public <K, V> ImmutableMap<K, V> aggregateInPlaceBy(Function<? super T, ? extends K> groupBy, Function0<? extends V> zeroValueFactory, Procedure2<? super V, ? super T> mutatingAggregator)
    {
        return Maps.immutable.empty();
    }

    @Override
    public <K, V> ImmutableMap<K, V> aggregateBy(Function<? super T, ? extends K> groupBy, Function0<? extends V> zeroValueFactory, Function2<? super V, ? super T, ? extends V> nonMutatingAggregator)
    {
        return Maps.immutable.empty();
    }

    @Override
    public <V> ImmutableBag<V> countByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return Bags.immutable.empty();
    }

    @Override
    public int size()
    {
        return 0;
    }

    @Override
    public boolean isEmpty()
    {
        return true;
    }

    @Override
    public boolean notEmpty()
    {
        return false;
    }

    @Override
    public ImmutableStack<T> tap(Procedure<? super T> procedure)
    {
        return this;
    }

    @Override
    public void each(Procedure<? super T> procedure)
    {
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
    }

    @Override
    public <P> void forEachWith(Procedure2<? super T, ? super P> procedure, P parameter)
    {
    }

    @Override
    public ImmutableStack<T> takeWhile(Predicate<? super T> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".takeWhile() not implemented yet");
    }

    @Override
    public ImmutableStack<T> dropWhile(Predicate<? super T> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".dropWhile() not implemented yet");
    }

    @Override
    public PartitionImmutableStack<T> partitionWhile(Predicate<? super T> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".partitionWhile() not implemented yet");
    }

    @Override
    public ImmutableStack<T> distinct()
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".distinct() not implemented yet");
    }

    @Override
    public int indexOf(Object object)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".indexOf() not implemented yet");
    }

    @Override
    public <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super T, ? super S> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".corresponds() not implemented yet");
    }

    public boolean hasSameElements(OrderedIterable<T> other)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".hasSameElements() not implemented yet");
    }

    @Override
    public void forEach(int startIndex, int endIndex, Procedure<? super T> procedure)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".forEach() not implemented yet");
    }

    @Override
    public void forEachWithIndex(int fromIndex, int toIndex, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".forEachWithIndex() not implemented yet");
    }

    @Override
    public int detectIndex(Predicate<? super T> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".detectIndex() not implemented yet");
    }

    @Override
    public Iterator<T> iterator()
    {
        return EmptyIterator.getInstance();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof StackIterable<?>))
        {
            return false;
        }

        StackIterable<?> that = (StackIterable<?>) o;
        return that.isEmpty();
    }

    @Override
    public int hashCode()
    {
        return 1;
    }

    private Object writeReplace()
    {
        return new ImmutableStackSerializationProxy<>(this);
    }
}
