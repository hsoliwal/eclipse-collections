/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.bag.sorted.mutable;

import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;

import com.m3.collections.eclipse.api.RichIterable;
import com.m3.collections.eclipse.api.annotation.Beta;
import com.m3.collections.eclipse.api.bag.MutableBag;
import com.m3.collections.eclipse.api.bag.sorted.ImmutableSortedBag;
import com.m3.collections.eclipse.api.bag.sorted.MutableSortedBag;
import com.m3.collections.eclipse.api.bag.sorted.ParallelSortedBag;
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
import com.m3.collections.eclipse.api.factory.Bags;
import com.m3.collections.eclipse.api.factory.Lists;
import com.m3.collections.eclipse.api.factory.SortedBags;
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
import com.m3.collections.eclipse.api.map.sorted.MutableSortedMap;
import com.m3.collections.eclipse.api.partition.bag.sorted.PartitionMutableSortedBag;
import com.m3.collections.eclipse.api.tuple.Pair;
import com.m3.collections.eclipse.impl.bag.mutable.AbstractMutableBagIterable;
import com.m3.collections.eclipse.impl.list.mutable.FastList;
import com.m3.collections.eclipse.impl.map.sorted.mutable.TreeSortedMap;
import com.m3.collections.eclipse.impl.partition.bag.sorted.PartitionTreeBag;
import com.m3.collections.eclipse.impl.utility.Iterate;
import com.m3.collections.eclipse.impl.utility.internal.IterableIterate;

public abstract class AbstractMutableSortedBag<T>
        extends AbstractMutableBagIterable<T>
        implements MutableSortedBag<T>
{
    @Override
    @SuppressWarnings("AbstractMethodOverridesAbstractMethod")
    public abstract MutableSortedBag<T> clone();

    @Override
    public ImmutableSortedBag<T> toImmutable()
    {
        return SortedBags.immutable.ofSortedBag(this);
    }

    @Override
    public UnmodifiableSortedBag<T> asUnmodifiable()
    {
        return UnmodifiableSortedBag.of(this);
    }

    @Override
    public MutableSortedBag<T> asSynchronized()
    {
        return SynchronizedSortedBag.of(this);
    }

    @Override
    public MutableSortedBag<T> tap(Procedure<? super T> procedure)
    {
        this.forEach(procedure);
        return this;
    }

    @Override
    public MutableSortedMap<T, Integer> toMapOfItemToCount()
    {
        MutableSortedMap<T, Integer> map = TreeSortedMap.newMap(this.comparator());
        this.forEachWithOccurrences(map::put);
        return map;
    }

    @Override
    public <S> MutableSortedBag<S> selectInstancesOf(Class<S> clazz)
    {
        Comparator<? super S> comparator = (Comparator<? super S>) this.comparator();
        MutableSortedBag<S> result = SortedBags.mutable.empty(comparator);
        this.forEachWithOccurrences((each, occurrences) -> {
            if (clazz.isInstance(each))
            {
                result.addOccurrences(clazz.cast(each), occurrences);
            }
        });
        return result;
    }

    @Override
    public MutableSortedBag<T> takeWhile(Predicate<? super T> predicate)
    {
        MutableSortedBag<T> result = SortedBags.mutable.empty(this.comparator());
        return IterableIterate.takeWhile(this, predicate, result);
    }

    @Override
    public MutableSortedBag<T> dropWhile(Predicate<? super T> predicate)
    {
        MutableSortedBag<T> result = SortedBags.mutable.empty(this.comparator());
        return IterableIterate.dropWhile(this, predicate, result);
    }

    /**
     * @since 9.0
     */
    @Override
    public <V> MutableBag<V> countBy(Function<? super T, ? extends V> function)
    {
        return this.countBy(function, Bags.mutable.empty());
    }

    /**
     * @since 9.0
     */
    @Override
    public <V, P> MutableBag<V> countByWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return this.countByWith(function, parameter, Bags.mutable.empty());
    }

    /**
     * @since 10.0.0
     */
    @Override
    public <V> MutableBag<V> countByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.countByEach(function, Bags.mutable.empty());
    }

    @Override
    public MutableSortedBag<T> select(Predicate<? super T> predicate)
    {
        return this.select(predicate, SortedBags.mutable.empty(this.comparator()));
    }

    @Override
    public <P> MutableSortedBag<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.selectWith(predicate, parameter, SortedBags.mutable.empty(this.comparator()));
    }

    @Override
    public MutableSortedBag<T> reject(Predicate<? super T> predicate)
    {
        return this.reject(predicate, SortedBags.mutable.empty(this.comparator()));
    }

    @Override
    public <P> MutableSortedBag<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.rejectWith(predicate, parameter, SortedBags.mutable.empty(this.comparator()));
    }

    @Override
    public PartitionMutableSortedBag<T> partition(Predicate<? super T> predicate)
    {
        PartitionMutableSortedBag<T> result = new PartitionTreeBag<>(this.comparator());
        this.forEachWithOccurrences((each, index) -> {
            MutableSortedBag<T> bucket = predicate.accept(each) ? result.getSelected() : result.getRejected();
            bucket.addOccurrences(each, index);
        });
        return result;
    }

    @Override
    public <P> PartitionMutableSortedBag<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        PartitionMutableSortedBag<T> result = new PartitionTreeBag<>(this.comparator());
        this.forEachWithOccurrences((each, index) -> {
            MutableSortedBag<T> bucket = predicate.accept(each, parameter)
                    ? result.getSelected()
                    : result.getRejected();
            bucket.addOccurrences(each, index);
        });
        return result;
    }

    @Override
    public PartitionMutableSortedBag<T> partitionWhile(Predicate<? super T> predicate)
    {
        PartitionTreeBag<T> result = new PartitionTreeBag<>(this.comparator());
        return IterableIterate.partitionWhile(this, predicate, result);
    }

    @Override
    public <V> MutableList<V> collect(Function<? super T, ? extends V> function)
    {
        return this.collect(function, Lists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public <V> MutableList<V> collectWithOccurrences(ObjectIntToObjectFunction<? super T, ? extends V> function)
    {
        return this.collectWithOccurrences(function, Lists.mutable.empty());
    }

    /**
     * @since 9.1.
     */
    @Override
    public <V> MutableList<V> collectWithIndex(ObjectIntToObjectFunction<? super T, ? extends V> function)
    {
        return this.collectWithIndex(function, Lists.mutable.withInitialCapacity(this.size()));
    }

    /**
     * @since 9.1.
     */
    @Override
    public <V, R extends Collection<V>> R collectWithIndex(ObjectIntToObjectFunction<? super T, ? extends V> function, R target)
    {
        int[] count = new int[1];
        this.forEachWithOccurrences((each, occurrences) ->
        {
            for (int i = 0; i < occurrences; i++)
            {
                V value = function.valueOf(each, count[0]++);
                target.add(value);
            }
        });
        return target;
    }

    @Override
    public <P, V> MutableList<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return this.collectWith(function, parameter, FastList.newList());
    }

    @Override
    public <V> MutableList<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function)
    {
        return this.collectIf(predicate, function, FastList.newList());
    }

    @Override
    public <V> MutableList<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.flatCollect(function, FastList.newList());
    }

    @Override
    public MutableBooleanList collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        return this.collectBoolean(booleanFunction, BooleanLists.mutable.empty());
    }

    @Override
    public MutableByteList collectByte(ByteFunction<? super T> byteFunction)
    {
        return this.collectByte(byteFunction, ByteLists.mutable.empty());
    }

    @Override
    public MutableCharList collectChar(CharFunction<? super T> charFunction)
    {
        return this.collectChar(charFunction, CharLists.mutable.empty());
    }

    @Override
    public MutableDoubleList collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        return this.collectDouble(doubleFunction, DoubleLists.mutable.empty());
    }

    @Override
    public MutableFloatList collectFloat(FloatFunction<? super T> floatFunction)
    {
        return this.collectFloat(floatFunction, FloatLists.mutable.empty());
    }

    @Override
    public MutableIntList collectInt(IntFunction<? super T> intFunction)
    {
        return this.collectInt(intFunction, IntLists.mutable.empty());
    }

    @Override
    public MutableLongList collectLong(LongFunction<? super T> longFunction)
    {
        return this.collectLong(longFunction, LongLists.mutable.empty());
    }

    @Override
    public MutableShortList collectShort(ShortFunction<? super T> shortFunction)
    {
        return this.collectShort(shortFunction, ShortLists.mutable.empty());
    }

    @Override
    public <S> MutableList<Pair<T, S>> zip(Iterable<S> that)
    {
        if (that instanceof Collection || that instanceof RichIterable)
        {
            int thatSize = Iterate.sizeOf(that);
            FastList<Pair<T, S>> target = FastList.newList(Math.min(this.size(), thatSize));
            return this.zip(that, target);
        }
        return this.zip(that, FastList.newList());
    }

    @Override
    public MutableSortedBag<T> toReversed()
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".toReversed() not implemented yet");
    }

    @Override
    public int detectLastIndex(Predicate<? super T> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".detectLastIndex() not implemented yet");
    }

    @Beta
    public ParallelSortedBag<T> asParallel(ExecutorService executorService, int batchSize)
    {
        if (executorService == null)
        {
            throw new NullPointerException();
        }
        if (batchSize < 1)
        {
            throw new IllegalArgumentException();
        }
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".asParallel() not implemented yet");
    }
}
