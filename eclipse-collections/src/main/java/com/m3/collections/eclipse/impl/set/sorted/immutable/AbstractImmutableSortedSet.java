/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.set.sorted.immutable;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ExecutorService;

import com.m3.collections.eclipse.api.LazyIterable;
import com.m3.collections.eclipse.api.RichIterable;
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
import com.m3.collections.eclipse.api.collection.MutableCollection;
import com.m3.collections.eclipse.api.factory.Lists;
import com.m3.collections.eclipse.api.factory.SortedSets;
import com.m3.collections.eclipse.api.factory.primitive.BooleanLists;
import com.m3.collections.eclipse.api.factory.primitive.ByteLists;
import com.m3.collections.eclipse.api.factory.primitive.CharLists;
import com.m3.collections.eclipse.api.factory.primitive.DoubleLists;
import com.m3.collections.eclipse.api.factory.primitive.FloatLists;
import com.m3.collections.eclipse.api.factory.primitive.IntLists;
import com.m3.collections.eclipse.api.factory.primitive.LongLists;
import com.m3.collections.eclipse.api.factory.primitive.ShortLists;
import com.m3.collections.eclipse.api.list.ImmutableList;
import com.m3.collections.eclipse.api.list.MutableList;
import com.m3.collections.eclipse.api.list.primitive.ImmutableBooleanList;
import com.m3.collections.eclipse.api.list.primitive.ImmutableByteList;
import com.m3.collections.eclipse.api.list.primitive.ImmutableCharList;
import com.m3.collections.eclipse.api.list.primitive.ImmutableDoubleList;
import com.m3.collections.eclipse.api.list.primitive.ImmutableFloatList;
import com.m3.collections.eclipse.api.list.primitive.ImmutableIntList;
import com.m3.collections.eclipse.api.list.primitive.ImmutableLongList;
import com.m3.collections.eclipse.api.list.primitive.ImmutableShortList;
import com.m3.collections.eclipse.api.list.primitive.MutableBooleanList;
import com.m3.collections.eclipse.api.list.primitive.MutableByteList;
import com.m3.collections.eclipse.api.list.primitive.MutableCharList;
import com.m3.collections.eclipse.api.list.primitive.MutableDoubleList;
import com.m3.collections.eclipse.api.list.primitive.MutableFloatList;
import com.m3.collections.eclipse.api.list.primitive.MutableIntList;
import com.m3.collections.eclipse.api.list.primitive.MutableLongList;
import com.m3.collections.eclipse.api.list.primitive.MutableShortList;
import com.m3.collections.eclipse.api.multimap.sortedset.ImmutableSortedSetMultimap;
import com.m3.collections.eclipse.api.ordered.OrderedIterable;
import com.m3.collections.eclipse.api.partition.set.sorted.PartitionImmutableSortedSet;
import com.m3.collections.eclipse.api.partition.set.sorted.PartitionMutableSortedSet;
import com.m3.collections.eclipse.api.set.SetIterable;
import com.m3.collections.eclipse.api.set.sorted.ImmutableSortedSet;
import com.m3.collections.eclipse.api.set.sorted.MutableSortedSet;
import com.m3.collections.eclipse.api.set.sorted.ParallelSortedSetIterable;
import com.m3.collections.eclipse.api.set.sorted.SortedSetIterable;
import com.m3.collections.eclipse.api.stack.MutableStack;
import com.m3.collections.eclipse.api.tuple.Pair;
import com.m3.collections.eclipse.impl.block.factory.Comparators;
import com.m3.collections.eclipse.impl.block.factory.Functions;
import com.m3.collections.eclipse.impl.block.factory.Predicates;
import com.m3.collections.eclipse.impl.block.procedure.CollectIfProcedure;
import com.m3.collections.eclipse.impl.block.procedure.CollectProcedure;
import com.m3.collections.eclipse.impl.block.procedure.FlatCollectProcedure;
import com.m3.collections.eclipse.impl.block.procedure.PartitionPredicate2Procedure;
import com.m3.collections.eclipse.impl.block.procedure.PartitionProcedure;
import com.m3.collections.eclipse.impl.block.procedure.RejectProcedure;
import com.m3.collections.eclipse.impl.block.procedure.SelectInstancesOfProcedure;
import com.m3.collections.eclipse.impl.block.procedure.SelectProcedure;
import com.m3.collections.eclipse.impl.collection.immutable.AbstractImmutableCollection;
import com.m3.collections.eclipse.impl.lazy.parallel.set.sorted.NonParallelSortedSetIterable;
import com.m3.collections.eclipse.impl.list.mutable.FastList;
import com.m3.collections.eclipse.impl.multimap.set.sorted.TreeSortedSetMultimap;
import com.m3.collections.eclipse.impl.partition.set.sorted.PartitionTreeSortedSet;
import com.m3.collections.eclipse.impl.set.sorted.mutable.TreeSortedSet;
import com.m3.collections.eclipse.impl.stack.mutable.ArrayStack;
import com.m3.collections.eclipse.impl.utility.Iterate;
import com.m3.collections.eclipse.impl.utility.OrderedIterate;
import com.m3.collections.eclipse.impl.utility.internal.IterableIterate;
import com.m3.collections.eclipse.impl.utility.internal.SetIterables;
import com.m3.collections.eclipse.impl.utility.internal.SortedSetIterables;

/**
 * This class is the parent class for all ImmutableSortedSets. All implementations of ImmutableSortedSet must implement the SortedSet
 * interface so an TreeSet.equals(anImmutableSortedSet) can return true when the contents are the same.
 */
abstract class AbstractImmutableSortedSet<T> extends AbstractImmutableCollection<T>
        implements ImmutableSortedSet<T>, SortedSet<T>
{
    @Override
    public SortedSet<T> castToSortedSet()
    {
        return this;
    }

    @Override
    public ImmutableSortedSet<T> newWith(T element)
    {
        if (!this.contains(element))
        {
            return SortedSets.mutable.withAll(this).with(element).toImmutable();
        }
        return this;
    }

    @Override
    public ImmutableSortedSet<T> newWithout(T element)
    {
        if (this.contains(element))
        {
            TreeSortedSet<T> result = TreeSortedSet.newSet(this);
            result.remove(element);
            return result.toImmutable();
        }
        return this;
    }

    @Override
    public ImmutableSortedSet<T> newWithAll(Iterable<? extends T> elements)
    {
        TreeSortedSet<T> result = TreeSortedSet.newSet(this);
        result.addAllIterable(elements);
        return result.toImmutable();
    }

    @Override
    public ImmutableSortedSet<T> newWithoutAll(Iterable<? extends T> elements)
    {
        TreeSortedSet<T> result = TreeSortedSet.newSet(this);
        this.removeAllFrom(elements, result);
        return result.toImmutable();
    }

    @Override
    public T getFirst()
    {
        return this.first();
    }

    @Override
    public T getLast()
    {
        return this.last();
    }

    @Override
    protected MutableCollection<T> newMutable(int size)
    {
        return TreeSortedSet.newSet(this.comparator());
    }

    @Override
    public ImmutableBooleanList collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        MutableBooleanList result = BooleanLists.mutable.withInitialCapacity(this.size());
        return this.collectBoolean(booleanFunction, result).toImmutable();
    }

    @Override
    public ImmutableByteList collectByte(ByteFunction<? super T> byteFunction)
    {
        MutableByteList result = ByteLists.mutable.withInitialCapacity(this.size());
        return this.collectByte(byteFunction, result).toImmutable();
    }

    @Override
    public ImmutableCharList collectChar(CharFunction<? super T> charFunction)
    {
        MutableCharList result = CharLists.mutable.withInitialCapacity(this.size());
        return this.collectChar(charFunction, result).toImmutable();
    }

    @Override
    public ImmutableDoubleList collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        MutableDoubleList result = DoubleLists.mutable.withInitialCapacity(this.size());
        return this.collectDouble(doubleFunction, result).toImmutable();
    }

    @Override
    public ImmutableFloatList collectFloat(FloatFunction<? super T> floatFunction)
    {
        MutableFloatList result = FloatLists.mutable.withInitialCapacity(this.size());
        return this.collectFloat(floatFunction, result).toImmutable();
    }

    @Override
    public ImmutableIntList collectInt(IntFunction<? super T> intFunction)
    {
        MutableIntList result = IntLists.mutable.withInitialCapacity(this.size());
        return this.collectInt(intFunction, result).toImmutable();
    }

    @Override
    public ImmutableLongList collectLong(LongFunction<? super T> longFunction)
    {
        MutableLongList result = LongLists.mutable.withInitialCapacity(this.size());
        return this.collectLong(longFunction, result).toImmutable();
    }

    @Override
    public ImmutableShortList collectShort(ShortFunction<? super T> shortFunction)
    {
        MutableShortList result = ShortLists.mutable.withInitialCapacity(this.size());
        return this.collectShort(shortFunction, result).toImmutable();
    }

    @Override
    public ImmutableSortedSet<T> tap(Procedure<? super T> procedure)
    {
        this.forEach(procedure);
        return this;
    }

    @Override
    public <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super T, ? super S> predicate)
    {
        return OrderedIterate.corresponds(this, other, predicate);
    }

    @Override
    public ImmutableSortedSet<T> select(Predicate<? super T> predicate)
    {
        TreeSortedSet<T> result = TreeSortedSet.newSet(this.comparator());
        this.forEach(new SelectProcedure<>(predicate, result));
        return result.toImmutable();
    }

    @Override
    public <P> ImmutableSortedSet<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.select(Predicates.bind(predicate, parameter));
    }

    @Override
    public ImmutableSortedSet<T> reject(Predicate<? super T> predicate)
    {
        TreeSortedSet<T> result = TreeSortedSet.newSet(this.comparator());
        this.forEach(new RejectProcedure<>(predicate, result));
        return result.toImmutable();
    }

    @Override
    public <P> ImmutableSortedSet<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.reject(Predicates.bind(predicate, parameter));
    }

    @Override
    public PartitionImmutableSortedSet<T> partition(Predicate<? super T> predicate)
    {
        PartitionMutableSortedSet<T> partitionTreeSortedSet = new PartitionTreeSortedSet<>(this.comparator());
        this.forEach(new PartitionProcedure<>(predicate, partitionTreeSortedSet));
        return partitionTreeSortedSet.toImmutable();
    }

    @Override
    public <P> PartitionImmutableSortedSet<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        PartitionMutableSortedSet<T> partitionTreeSortedSet = new PartitionTreeSortedSet<>(this.comparator());
        this.forEach(new PartitionPredicate2Procedure<>(predicate, parameter, partitionTreeSortedSet));
        return partitionTreeSortedSet.toImmutable();
    }

    @Override
    public PartitionImmutableSortedSet<T> partitionWhile(Predicate<? super T> predicate)
    {
        PartitionTreeSortedSet<T> result = new PartitionTreeSortedSet<>(this.comparator());
        return IterableIterate.partitionWhile(this, predicate, result).toImmutable();
    }

    @Override
    public <S> ImmutableSortedSet<S> selectInstancesOf(Class<S> clazz)
    {
        TreeSortedSet<S> result = TreeSortedSet.newSet((Comparator<? super S>) this.comparator());
        this.forEach(new SelectInstancesOfProcedure<>(clazz, result));
        return result.toImmutable();
    }

    @Override
    public <V> ImmutableList<V> collect(Function<? super T, ? extends V> function)
    {
        MutableList<V> result = Lists.mutable.empty();
        this.forEach(new CollectProcedure<>(function, result));
        return result.toImmutable();
    }

    @Override
    public <P, V> ImmutableList<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return this.collect(Functions.bind(function, parameter));
    }

    @Override
    public <V> ImmutableList<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function)
    {
        MutableList<V> result = Lists.mutable.empty();
        this.forEach(new CollectIfProcedure<>(result, function, predicate));
        return result.toImmutable();
    }

    @Override
    public <V> ImmutableList<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        MutableList<V> result = Lists.mutable.empty();
        this.forEach(new FlatCollectProcedure<>(function, result));
        return result.toImmutable();
    }

    @Override
    public int detectIndex(Predicate<? super T> predicate)
    {
        return Iterate.detectIndex(this, predicate);
    }

    @Override
    public <V> ImmutableSortedSetMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return this.groupBy(function, TreeSortedSetMultimap.<V, T>newMultimap(this.comparator())).toImmutable();
    }

    @Override
    public <V> ImmutableSortedSetMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.groupByEach(function, TreeSortedSetMultimap.newMultimap(this.comparator())).toImmutable();
    }

    @Override
    public <S> ImmutableList<Pair<T, S>> zip(Iterable<S> that)
    {
        if (that instanceof Collection || that instanceof RichIterable)
        {
            int thatSize = Iterate.sizeOf(that);
            FastList<Pair<T, S>> target = FastList.newList(Math.min(this.size(), thatSize));
            return Iterate.zip(this, that, target).toImmutable();
        }
        return Iterate.zip(this, that, FastList.newList()).toImmutable();
    }

    @Override
    public ImmutableSortedSet<Pair<T, Integer>> zipWithIndex()
    {
        Comparator<? super T> comparator = this.comparator();
        if (comparator == null)
        {
            TreeSortedSet<Pair<T, Integer>> pairs = TreeSortedSet.newSet(Comparators.byFunction(Functions.firstOfPair(), Comparators.naturalOrder()));
            return Iterate.zipWithIndex(this, pairs).toImmutable();
        }
        return Iterate.zipWithIndex(this, TreeSortedSet.newSet(Comparators.byFirstOfPair(comparator))).toImmutable();
    }

    @Override
    public ImmutableSortedSet<T> distinct()
    {
        return this;
    }

    @Override
    public ImmutableSortedSet<T> takeWhile(Predicate<? super T> predicate)
    {
        MutableSortedSet<T> result = TreeSortedSet.newSet(this.comparator());
        return IterableIterate.takeWhile(this, predicate, result).toImmutable();
    }

    @Override
    public ImmutableSortedSet<T> dropWhile(Predicate<? super T> predicate)
    {
        MutableSortedSet<T> result = TreeSortedSet.newSet(this.comparator());
        return IterableIterate.dropWhile(this, predicate, result).toImmutable();
    }

    @Override
    public MutableStack<T> toStack()
    {
        return ArrayStack.newStack(this);
    }

    @Override
    public ImmutableSortedSet<T> union(SetIterable<? extends T> set)
    {
        return SetIterables.unionInto(this, set, TreeSortedSet.newSet(this.comparator())).toImmutable();
    }

    @Override
    public <R extends Set<T>> R unionInto(SetIterable<? extends T> set, R targetSet)
    {
        return SetIterables.unionInto(this, set, targetSet);
    }

    @Override
    public ImmutableSortedSet<T> intersect(SetIterable<? extends T> set)
    {
        return SetIterables.intersectInto(this, set, TreeSortedSet.newSet(this.comparator())).toImmutable();
    }

    @Override
    public <R extends Set<T>> R intersectInto(SetIterable<? extends T> set, R targetSet)
    {
        return SetIterables.intersectInto(this, set, targetSet);
    }

    @Override
    public ImmutableSortedSet<T> difference(SetIterable<? extends T> subtrahendSet)
    {
        return SetIterables.differenceInto(this, subtrahendSet, TreeSortedSet.newSet(this.comparator())).toImmutable();
    }

    @Override
    public <R extends Set<T>> R differenceInto(SetIterable<? extends T> subtrahendSet, R targetSet)
    {
        return SetIterables.differenceInto(this, subtrahendSet, targetSet);
    }

    @Override
    public ImmutableSortedSet<T> symmetricDifference(SetIterable<? extends T> setB)
    {
        return SetIterables.symmetricDifferenceInto(this, setB, TreeSortedSet.newSet(this.comparator())).toImmutable();
    }

    @Override
    public <R extends Set<T>> R symmetricDifferenceInto(SetIterable<? extends T> set, R targetSet)
    {
        return SetIterables.symmetricDifferenceInto(this, set, targetSet);
    }

    @Override
    public boolean isSubsetOf(SetIterable<? extends T> candidateSuperset)
    {
        return SetIterables.isSubsetOf(this, candidateSuperset);
    }

    @Override
    public boolean isProperSubsetOf(SetIterable<? extends T> candidateSuperset)
    {
        return SetIterables.isProperSubsetOf(this, candidateSuperset);
    }

    @Override
    public ImmutableSortedSet<SortedSetIterable<T>> powerSet()
    {
        return (ImmutableSortedSet<SortedSetIterable<T>>) (ImmutableSortedSet<?>) SortedSetIterables.immutablePowerSet(this);
    }

    @Override
    public <B> LazyIterable<Pair<T, B>> cartesianProduct(SetIterable<B> set)
    {
        return SetIterables.cartesianProduct(this, set);
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".subSet() not implemented yet");
    }

    @Override
    public SortedSet<T> headSet(T toElement)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".headSet() not implemented yet");
    }

    @Override
    public SortedSet<T> tailSet(T fromElement)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".tailSet() not implemented yet");
    }

    @Override
    public ImmutableSortedSet<T> toImmutable()
    {
        return this;
    }

    @Override
    public ParallelSortedSetIterable<T> asParallel(ExecutorService executorService, int batchSize)
    {
        Objects.requireNonNull(executorService);
        if (batchSize < 1)
        {
            throw new IllegalArgumentException("batchSize must be greater than zero, but was: " + batchSize);
        }
        return new NonParallelSortedSetIterable<>(this);
    }

    @Override
    public LazyIterable<T> asReversed()
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".asReversed() not implemented yet");
    }

    @Override
    public ImmutableSortedSet<T> toReversed()
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".toReversed() not implemented yet");
    }

    @Override
    public int detectLastIndex(Predicate<? super T> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".detectLastIndex() not implemented yet");
    }
}
