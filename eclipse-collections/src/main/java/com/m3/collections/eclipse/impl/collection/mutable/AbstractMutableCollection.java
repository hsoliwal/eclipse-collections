/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.collection.mutable;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.RandomAccess;
import java.util.function.BinaryOperator;

import com.m3.collections.eclipse.api.RichIterable;
import com.m3.collections.eclipse.api.bag.MutableBag;
import com.m3.collections.eclipse.api.block.function.Function;
import com.m3.collections.eclipse.api.block.function.Function2;
import com.m3.collections.eclipse.api.block.function.Function3;
import com.m3.collections.eclipse.api.block.function.primitive.DoubleFunction;
import com.m3.collections.eclipse.api.block.function.primitive.FloatFunction;
import com.m3.collections.eclipse.api.block.function.primitive.IntFunction;
import com.m3.collections.eclipse.api.block.function.primitive.LongFunction;
import com.m3.collections.eclipse.api.block.predicate.Predicate;
import com.m3.collections.eclipse.api.block.predicate.Predicate2;
import com.m3.collections.eclipse.api.collection.MutableCollection;
import com.m3.collections.eclipse.api.factory.Bags;
import com.m3.collections.eclipse.api.factory.Lists;
import com.m3.collections.eclipse.api.factory.primitive.ObjectDoubleMaps;
import com.m3.collections.eclipse.api.factory.primitive.ObjectLongMaps;
import com.m3.collections.eclipse.api.list.MutableList;
import com.m3.collections.eclipse.api.map.MutableMap;
import com.m3.collections.eclipse.api.map.primitive.MutableObjectDoubleMap;
import com.m3.collections.eclipse.api.map.primitive.MutableObjectLongMap;
import com.m3.collections.eclipse.api.tuple.Twin;
import com.m3.collections.eclipse.impl.AbstractRichIterable;
import com.m3.collections.eclipse.impl.block.factory.PrimitiveFunctions;
import com.m3.collections.eclipse.impl.block.factory.Procedures2;
import com.m3.collections.eclipse.impl.utility.Iterate;
import com.m3.collections.eclipse.impl.utility.internal.IterableIterate;

public abstract class AbstractMutableCollection<T>
        extends AbstractRichIterable<T>
        implements MutableCollection<T>
{
    @Override
    public <P> Twin<MutableList<T>> selectAndRejectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        return IterableIterate.selectAndRejectWith(this, predicate, parameter);
    }

    @Override
    public boolean removeIf(Predicate<? super T> predicate)
    {
        return IterableIterate.removeIf(this, predicate);
    }

    @Override
    public <P> boolean removeIfWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return IterableIterate.removeIfWith(this, predicate, parameter);
    }

    @Override
    public <IV, P> IV injectIntoWith(
            IV injectValue,
            Function3<? super IV, ? super T, ? super P, ? extends IV> function,
            P parameter)
    {
        return IterableIterate.injectIntoWith(injectValue, this, function, parameter);
    }

    @Override
    public Optional<T> reduce(BinaryOperator<T> accumulator)
    {
        if (this.isEmpty())
        {
            return Optional.empty();
        }
        return Optional.of(this.injectInto(null, (result, each) ->
                result == null ? each : accumulator.apply(result, each)));
    }

    @Override
    public boolean addAllIterable(Iterable<? extends T> iterable)
    {
        int oldSize = this.size();

        if (iterable instanceof List && iterable instanceof RandomAccess)
        {
            List<T> list = (List<T>) iterable;
            int size = list.size();
            for (int i = 0; i < size; i++)
            {
                this.add(list.get(i));
            }
        }
        else
        {
            Iterate.forEachWith(iterable, Procedures2.addToCollection(), this);
        }
        return oldSize != this.size();
    }

    @Override
    public boolean removeAllIterable(Iterable<?> iterable)
    {
        return this.removeAll(CollectionAdapter.wrapSet(iterable));
    }

    @Override
    public boolean retainAllIterable(Iterable<?> iterable)
    {
        return this.retainAll(CollectionAdapter.wrapSet(iterable));
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
            MutableCollection<T> batch = this.newEmpty();
            for (int i = 0; i < size && iterator.hasNext(); i++)
            {
                batch.add(iterator.next());
            }
            result.add(batch);
        }
        return result;
    }

    @Override
    public <V> MutableObjectLongMap<V> sumByInt(Function<? super T, ? extends V> groupBy, IntFunction<? super T> function)
    {
        MutableObjectLongMap<V> result = ObjectLongMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByIntFunction(groupBy, function));
    }

    @Override
    public <V> MutableObjectDoubleMap<V> sumByFloat(Function<? super T, ? extends V> groupBy, FloatFunction<? super T> function)
    {
        MutableObjectDoubleMap<V> result = ObjectDoubleMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByFloatFunction(groupBy, function));
    }

    @Override
    public <V> MutableObjectLongMap<V> sumByLong(Function<? super T, ? extends V> groupBy, LongFunction<? super T> function)
    {
        MutableObjectLongMap<V> result = ObjectLongMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByLongFunction(groupBy, function));
    }

    @Override
    public <V> MutableObjectDoubleMap<V> sumByDouble(Function<? super T, ? extends V> groupBy, DoubleFunction<? super T> function)
    {
        MutableObjectDoubleMap<V> result = ObjectDoubleMaps.mutable.empty();
        return this.injectInto(result, PrimitiveFunctions.sumByDoubleFunction(groupBy, function));
    }

    @Override
    public boolean add(T element)
    {
        throw new UnsupportedOperationException("Cannot call add() on " + this.getClass().getSimpleName());
    }

    @Override
    public boolean remove(Object o)
    {
        Iterator<T> iterator = this.iterator();
        while (iterator.hasNext())
        {
            if (Objects.equals(o, iterator.next()))
            {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> source)
    {
        return this.addAllIterable(source);
    }

    @Override
    public boolean removeAll(Collection<?> source)
    {
        return this.removeAllIterable(source);
    }

    @Override
    public boolean retainAll(Collection<?> source)
    {
        int oldSize = this.size();
        Iterator<?> iterator = this.iterator();
        while (iterator.hasNext())
        {
            if (!source.contains(iterator.next()))
            {
                iterator.remove();
            }
        }
        return this.size() != oldSize;
    }

    @Override
    public <V> MutableMap<V, T> groupByUniqueKey(Function<? super T, ? extends V> function)
    {
        return Iterate.groupByUniqueKey(this, function);
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
}
