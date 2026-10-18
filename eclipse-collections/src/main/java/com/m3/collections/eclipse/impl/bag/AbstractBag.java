/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.bag;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringJoiner;

import com.m3.collections.eclipse.api.BooleanIterable;
import com.m3.collections.eclipse.api.ByteIterable;
import com.m3.collections.eclipse.api.CharIterable;
import com.m3.collections.eclipse.api.DoubleIterable;
import com.m3.collections.eclipse.api.FloatIterable;
import com.m3.collections.eclipse.api.IntIterable;
import com.m3.collections.eclipse.api.LongIterable;
import com.m3.collections.eclipse.api.ShortIterable;
import com.m3.collections.eclipse.api.bag.Bag;
import com.m3.collections.eclipse.api.bag.MutableBag;
import com.m3.collections.eclipse.api.bag.MutableBagIterable;
import com.m3.collections.eclipse.api.bag.primitive.MutableBooleanBag;
import com.m3.collections.eclipse.api.bag.primitive.MutableByteBag;
import com.m3.collections.eclipse.api.bag.primitive.MutableCharBag;
import com.m3.collections.eclipse.api.bag.primitive.MutableDoubleBag;
import com.m3.collections.eclipse.api.bag.primitive.MutableFloatBag;
import com.m3.collections.eclipse.api.bag.primitive.MutableIntBag;
import com.m3.collections.eclipse.api.bag.primitive.MutableLongBag;
import com.m3.collections.eclipse.api.bag.primitive.MutableShortBag;
import com.m3.collections.eclipse.api.bag.sorted.MutableSortedBag;
import com.m3.collections.eclipse.api.block.function.Function;
import com.m3.collections.eclipse.api.block.function.Function2;
import com.m3.collections.eclipse.api.block.function.Function3;
import com.m3.collections.eclipse.api.block.function.primitive.BooleanFunction;
import com.m3.collections.eclipse.api.block.function.primitive.ByteFunction;
import com.m3.collections.eclipse.api.block.function.primitive.CharFunction;
import com.m3.collections.eclipse.api.block.function.primitive.DoubleFunction;
import com.m3.collections.eclipse.api.block.function.primitive.DoubleObjectToDoubleFunction;
import com.m3.collections.eclipse.api.block.function.primitive.FloatFunction;
import com.m3.collections.eclipse.api.block.function.primitive.FloatObjectToFloatFunction;
import com.m3.collections.eclipse.api.block.function.primitive.IntFunction;
import com.m3.collections.eclipse.api.block.function.primitive.IntObjectToIntFunction;
import com.m3.collections.eclipse.api.block.function.primitive.LongFunction;
import com.m3.collections.eclipse.api.block.function.primitive.LongObjectToLongFunction;
import com.m3.collections.eclipse.api.block.function.primitive.ShortFunction;
import com.m3.collections.eclipse.api.block.predicate.Predicate;
import com.m3.collections.eclipse.api.block.predicate.Predicate2;
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
import com.m3.collections.eclipse.api.factory.Sets;
import com.m3.collections.eclipse.api.factory.SortedBags;
import com.m3.collections.eclipse.api.factory.SortedSets;
import com.m3.collections.eclipse.api.list.MutableList;
import com.m3.collections.eclipse.api.multimap.MutableMultimap;
import com.m3.collections.eclipse.api.set.MutableSet;
import com.m3.collections.eclipse.api.set.sorted.MutableSortedSet;
import com.m3.collections.eclipse.api.tuple.primitive.ObjectIntPair;
import com.m3.collections.eclipse.impl.AbstractRichIterable;
import com.m3.collections.eclipse.impl.Counter;
import com.m3.collections.eclipse.impl.tuple.primitive.PrimitiveTuples;
import com.m3.collections.eclipse.impl.utility.Iterate;

/**
 * @since 7.0
 */
public abstract class AbstractBag<T>
        extends AbstractRichIterable<T>
        implements Collection<T>, Bag<T>
{
    @Override
    public Object[] toArray()
    {
        return super.toArray();
    }

    @Override
    public <E> E[] toArray(E[] array)
    {
        return super.toArray(array);
    }

    @Override
    public <R extends Collection<T>> R select(Predicate<? super T> predicate, R target)
    {
        if (target instanceof MutableBagIterable<?>)
        {
            MutableBagIterable<T> targetBag = (MutableBagIterable<T>) target;

            this.forEachWithOccurrences((each, occurrences) -> {
                if (predicate.accept(each))
                {
                    targetBag.addOccurrences(each, occurrences);
                }
            });
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) -> {
                if (predicate.accept(each))
                {
                    for (int i = 0; i < occurrences; i++)
                    {
                        target.add(each);
                    }
                }
            });
        }
        return target;
    }

    @Override
    public <P, R extends Collection<T>> R selectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R target)
    {
        if (target instanceof MutableBagIterable<?>)
        {
            MutableBagIterable<T> targetBag = (MutableBagIterable<T>) target;

            this.forEachWithOccurrences((each, occurrences) -> {
                if (predicate.accept(each, parameter))
                {
                    targetBag.addOccurrences(each, occurrences);
                }
            });
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) -> {
                if (predicate.accept(each, parameter))
                {
                    for (int i = 0; i < occurrences; i++)
                    {
                        target.add(each);
                    }
                }
            });
        }
        return target;
    }

    @Override
    public <R extends Collection<T>> R reject(Predicate<? super T> predicate, R target)
    {
        if (target instanceof MutableBagIterable<?>)
        {
            MutableBagIterable<T> targetBag = (MutableBagIterable<T>) target;

            this.forEachWithOccurrences((each, occurrences) -> {
                if (!predicate.accept(each))
                {
                    targetBag.addOccurrences(each, occurrences);
                }
            });
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) -> {
                if (!predicate.accept(each))
                {
                    for (int i = 0; i < occurrences; i++)
                    {
                        target.add(each);
                    }
                }
            });
        }
        return target;
    }

    @Override
    public <P, R extends Collection<T>> R rejectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R target)
    {
        if (target instanceof MutableBagIterable<?>)
        {
            MutableBagIterable<T> targetBag = (MutableBagIterable<T>) target;

            this.forEachWithOccurrences((each, occurrences) -> {
                if (!predicate.accept(each, parameter))
                {
                    targetBag.addOccurrences(each, occurrences);
                }
            });
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) -> {
                if (!predicate.accept(each, parameter))
                {
                    for (int i = 0; i < occurrences; i++)
                    {
                        target.add(each);
                    }
                }
            });
        }
        return target;
    }

    @Override
    public int count(Predicate<? super T> predicate)
    {
        Counter result = new Counter();
        this.forEachWithOccurrences((each, occurrences) -> {
            if (predicate.accept(each))
            {
                result.add(occurrences);
            }
        });
        return result.getCount();
    }

    @Override
    public <V, R extends Collection<V>> R collect(Function<? super T, ? extends V> function, R target)
    {
        if (target instanceof MutableBagIterable<?>)
        {
            MutableBagIterable<V> targetBag = (MutableBagIterable<V>) target;

            this.forEachWithOccurrences((each, occurrences) -> targetBag.addOccurrences(function.valueOf(each), occurrences));
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) -> {
                V value = function.valueOf(each);
                for (int i = 0; i < occurrences; i++)
                {
                    target.add(value);
                }
            });
        }
        return target;
    }

    @Override
    public <P, V, R extends Collection<V>> R collectWith(
            Function2<? super T, ? super P, ? extends V> function,
            P parameter,
            R target)
    {
        if (target instanceof MutableBagIterable<?>)
        {
            MutableBagIterable<V> targetBag = (MutableBagIterable<V>) target;

            this.forEachWithOccurrences((each, occurrences) -> targetBag.addOccurrences(function.value(each, parameter), occurrences));
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) -> {
                V value = function.value(each, parameter);
                for (int i = 0; i < occurrences; i++)
                {
                    target.add(value);
                }
            });
        }
        return target;
    }

    @Override
    public <V, R extends Collection<V>> R collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function,
            R target)
    {
        if (target instanceof MutableBagIterable<?>)
        {
            MutableBagIterable<V> targetBag = (MutableBagIterable<V>) target;

            this.forEachWithOccurrences((each, occurrences) -> {
                if (predicate.accept(each))
                {
                    targetBag.addOccurrences(function.valueOf(each), occurrences);
                }
            });
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) -> {
                if (predicate.accept(each))
                {
                    V value = function.valueOf(each);
                    for (int i = 0; i < occurrences; i++)
                    {
                        target.add(value);
                    }
                }
            });
        }
        return target;
    }

    @Override
    public <V, R extends Collection<V>> R flatCollect(Function<? super T, ? extends Iterable<V>> function, R target)
    {
        if (target instanceof MutableBagIterable<?>)
        {
            MutableBagIterable<V> targetBag = (MutableBagIterable<V>) target;

            this.forEachWithOccurrences((each, occurrences) -> {
                Iterable<V> values = function.valueOf(each);
                Iterate.forEach(values, eachValue -> targetBag.addOccurrences(eachValue, occurrences));
            });
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) -> {
                Iterable<V> values = function.valueOf(each);
                for (int i = 0; i < occurrences; i++)
                {
                    Iterate.forEach(values, target::add);
                }
            });
        }
        return target;
    }

    @Override
    public <R extends MutableBooleanCollection> R collectBoolean(BooleanFunction<? super T> booleanFunction, R target)
    {
        if (target instanceof MutableBooleanBag targetBag)
        {
            this.forEachWithOccurrences((each, occurrences) -> targetBag.addOccurrences(booleanFunction.booleanValueOf(each), occurrences));
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) -> {
                boolean value = booleanFunction.booleanValueOf(each);
                for (int i = 0; i < occurrences; i++)
                {
                    target.add(value);
                }
            });
        }
        return target;
    }

    @Override
    public <R extends MutableBooleanCollection> R flatCollectBoolean(
            Function<? super T, ? extends BooleanIterable> function, R target)
    {
        if (target instanceof MutableBooleanBag targetBag)
        {
            this.forEachWithOccurrences((each, occurrences) -> {
                BooleanIterable values = function.valueOf(each);
                values.forEach(value -> targetBag.addOccurrences(value, occurrences));
            });
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) -> {
                BooleanIterable value = function.valueOf(each);
                for (int i = 0; i < occurrences; i++)
                {
                    value.forEach(target::add);
                }
            });
        }
        return target;
    }

    @Override
    public <R extends MutableByteCollection> R collectByte(ByteFunction<? super T> byteFunction, R target)
    {
        if (target instanceof MutableByteBag targetBag)
        {
            this.forEachWithOccurrences((each, occurrences) -> targetBag.addOccurrences(byteFunction.byteValueOf(each), occurrences));
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) -> {
                byte value = byteFunction.byteValueOf(each);
                for (int i = 0; i < occurrences; i++)
                {
                    target.add(value);
                }
            });
        }
        return target;
    }

    @Override
    public <R extends MutableByteCollection> R flatCollectByte(
            Function<? super T, ? extends ByteIterable> function, R target)
    {
        if (target instanceof MutableByteBag targetBag)
        {
            this.forEachWithOccurrences((each, occurrences) -> {
                ByteIterable values = function.valueOf(each);
                values.forEach(value -> targetBag.addOccurrences(value, occurrences));
            });
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) -> {
                ByteIterable value = function.valueOf(each);
                for (int i = 0; i < occurrences; i++)
                {
                    value.forEach(target::add);
                }
            });
        }
        return target;
    }

    @Override
    public <R extends MutableCharCollection> R collectChar(CharFunction<? super T> charFunction, R target)
    {
        if (target instanceof MutableCharBag targetBag)
        {
            this.forEachWithOccurrences((each, occurrences) -> targetBag.addOccurrences(charFunction.charValueOf(each), occurrences));
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) -> {
                char value = charFunction.charValueOf(each);
                for (int i = 0; i < occurrences; i++)
                {
                    target.add(value);
                }
            });
        }
        return target;
    }

    @Override
    public <R extends MutableCharCollection> R flatCollectChar(
            Function<? super T, ? extends CharIterable> function, R target)
    {
        if (target instanceof MutableCharBag targetBag)
        {
            this.forEachWithOccurrences((each, occurrences) -> {
                CharIterable values = function.valueOf(each);
                values.forEach(value -> targetBag.addOccurrences(value, occurrences));
            });
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) -> {
                CharIterable value = function.valueOf(each);
                for (int i = 0; i < occurrences; i++)
                {
                    value.forEach(target::add);
                }
            });
        }
        return target;
    }

    @Override
    public <R extends MutableDoubleCollection> R collectDouble(DoubleFunction<? super T> doubleFunction, R target)
    {
        if (target instanceof MutableDoubleBag targetBag)
        {
            this.forEachWithOccurrences((each, occurrences) -> targetBag.addOccurrences(doubleFunction.doubleValueOf(each), occurrences));
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) -> {
                double value = doubleFunction.doubleValueOf(each);
                for (int i = 0; i < occurrences; i++)
                {
                    target.add(value);
                }
            });
        }
        return target;
    }

    @Override
    public <R extends MutableDoubleCollection> R flatCollectDouble(
            Function<? super T, ? extends DoubleIterable> function,
            R target)
    {
        if (target instanceof MutableDoubleBag targetBag)
        {
            this.forEachWithOccurrences((each, occurrences) -> {
                DoubleIterable values = function.valueOf(each);
                values.forEach(value -> targetBag.addOccurrences(value, occurrences));
            });
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) -> {
                DoubleIterable value = function.valueOf(each);
                for (int i = 0; i < occurrences; i++)
                {
                    value.forEach(target::add);
                }
            });
        }
        return target;
    }

    @Override
    public <R extends MutableFloatCollection> R collectFloat(FloatFunction<? super T> floatFunction, R target)
    {
        if (target instanceof MutableFloatBag targetBag)
        {
            this.forEachWithOccurrences((each, occurrences) -> targetBag.addOccurrences(floatFunction.floatValueOf(each), occurrences));
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) -> {
                float value = floatFunction.floatValueOf(each);
                for (int i = 0; i < occurrences; i++)
                {
                    target.add(value);
                }
            });
        }
        return target;
    }

    @Override
    public <R extends MutableFloatCollection> R flatCollectFloat(
            Function<? super T, ? extends FloatIterable> function, R target)
    {
        if (target instanceof MutableFloatBag targetBag)
        {
            this.forEachWithOccurrences((each, occurrences) -> {
                FloatIterable values = function.valueOf(each);
                values.forEach(value -> targetBag.addOccurrences(value, occurrences));
            });
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) -> {
                FloatIterable value = function.valueOf(each);
                for (int i = 0; i < occurrences; i++)
                {
                    value.forEach(target::add);
                }
            });
        }
        return target;
    }

    @Override
    public <R extends MutableIntCollection> R collectInt(IntFunction<? super T> intFunction, R target)
    {
        if (target instanceof MutableIntBag targetBag)
        {
            this.forEachWithOccurrences((each, occurrences) -> targetBag.addOccurrences(intFunction.intValueOf(each), occurrences));
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) -> {
                int value = intFunction.intValueOf(each);
                for (int i = 0; i < occurrences; i++)
                {
                    target.add(value);
                }
            });
        }
        return target;
    }

    @Override
    public <R extends MutableIntCollection> R flatCollectInt(
            Function<? super T, ? extends IntIterable> function, R target)
    {
        if (target instanceof MutableIntBag targetBag)
        {
            this.forEachWithOccurrences((each, occurrences) -> {
                IntIterable values = function.valueOf(each);
                values.forEach(value -> targetBag.addOccurrences(value, occurrences));
            });
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) -> {
                IntIterable value = function.valueOf(each);
                for (int i = 0; i < occurrences; i++)
                {
                    value.forEach(target::add);
                }
            });
        }
        return target;
    }

    @Override
    public <R extends MutableLongCollection> R collectLong(LongFunction<? super T> longFunction, R target)
    {
        if (target instanceof MutableLongBag targetBag)
        {
            this.forEachWithOccurrences((each, occurrences) -> targetBag.addOccurrences(longFunction.longValueOf(each), occurrences));
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) -> {
                long value = longFunction.longValueOf(each);
                for (int i = 0; i < occurrences; i++)
                {
                    target.add(value);
                }
            });
        }
        return target;
    }

    @Override
    public <R extends MutableLongCollection> R flatCollectLong(
            Function<? super T, ? extends LongIterable> function, R target)
    {
        if (target instanceof MutableLongBag targetBag)
        {
            this.forEachWithOccurrences((each, occurrences) -> {
                LongIterable values = function.valueOf(each);
                values.forEach(value -> targetBag.addOccurrences(value, occurrences));
            });
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) -> {
                LongIterable value = function.valueOf(each);
                for (int i = 0; i < occurrences; i++)
                {
                    value.forEach(target::add);
                }
            });
        }
        return target;
    }

    @Override
    public <R extends MutableShortCollection> R collectShort(ShortFunction<? super T> shortFunction, R target)
    {
        if (target instanceof MutableShortBag targetBag)
        {
            this.forEachWithOccurrences((each, occurrences) -> targetBag.addOccurrences(shortFunction.shortValueOf(each), occurrences));
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) -> {
                short value = shortFunction.shortValueOf(each);
                for (int i = 0; i < occurrences; i++)
                {
                    target.add(value);
                }
            });
        }
        return target;
    }

    @Override
    public <R extends MutableShortCollection> R flatCollectShort(
            Function<? super T, ? extends ShortIterable> function, R target)
    {
        if (target instanceof MutableShortBag targetBag)
        {
            this.forEachWithOccurrences((each, occurrences) -> {
                ShortIterable values = function.valueOf(each);
                values.forEach(value -> targetBag.addOccurrences(value, occurrences));
            });
        }
        else
        {
            this.forEachWithOccurrences((each, occurrences) -> {
                ShortIterable value = function.valueOf(each);
                for (int i = 0; i < occurrences; i++)
                {
                    value.forEach(target::add);
                }
            });
        }
        return target;
    }

    @Override
    public <V, R extends MutableMultimap<V, T>> R groupBy(
            Function<? super T, ? extends V> function,
            R target)
    {
        this.forEachWithOccurrences((each, occurrences) -> {
            V value = function.valueOf(each);
            target.putAll(value, Collections.nCopies(occurrences, each));
        });
        return target;
    }

    @Override
    public <V, R extends MutableMultimap<V, T>> R groupByEach(
            Function<? super T, ? extends Iterable<V>> function,
            R target)
    {
        this.forEachWithOccurrences((each, occurrences) -> {
            Iterable<V> values = function.valueOf(each);
            Iterate.forEach(values, value -> target.putAll(value, Collections.nCopies(occurrences, each)));
        });
        return target;
    }

    @Override
    public long sumOfInt(IntFunction<? super T> function)
    {
        long[] sum = {0L};
        this.forEachWithOccurrences((each, occurrences) -> {
            int intValue = function.intValueOf(each);
            sum[0] += (long) intValue * (long) occurrences;
        });
        return sum[0];
    }

    @Override
    public double sumOfFloat(FloatFunction<? super T> function)
    {
        double[] sum = {0.0d};
        double[] compensation = {0.0d};
        this.forEachWithOccurrences((each, occurrences) -> {
            float f = function.floatValueOf(each);
            for (int i = 0; i < occurrences; i++)
            {
                double adjustedValue = f - compensation[0];
                double nextSum = sum[0] + adjustedValue;
                compensation[0] = nextSum - sum[0] - adjustedValue;
                sum[0] = nextSum;
            }
        });
        return sum[0];
    }

    @Override
    public long sumOfLong(LongFunction<? super T> function)
    {
        long[] sum = {0L};
        this.forEachWithOccurrences((each, occurrences) -> {
            long longValue = function.longValueOf(each);
            sum[0] += longValue * (long) occurrences;
        });
        return sum[0];
    }

    @Override
    public double sumOfDouble(DoubleFunction<? super T> function)
    {
        double[] sum = {0.0d};
        double[] compensation = {0.0d};
        this.forEachWithOccurrences((each, occurrences) -> {
            double d = function.doubleValueOf(each);
            for (int i = 0; i < occurrences; i++)
            {
                double y = d - compensation[0];
                double t = sum[0] + y;
                compensation[0] = t - sum[0] - y;
                sum[0] = t;
            }
        });
        return sum[0];
    }

    @Override
    public <IV> IV injectInto(IV injectedValue, Function2<? super IV, ? super T, ? extends IV> function)
    {
        IV[] result = (IV[]) new Object[]{injectedValue};
        this.forEachWithOccurrences((each, occurrences) -> {
            for (int i = 0; i < occurrences; i++)
            {
                result[0] = function.value(result[0], each);
            }
        });
        return result[0];
    }

    @Override
    public int injectInto(int injectedValue, IntObjectToIntFunction<? super T> function)
    {
        int[] result = {injectedValue};
        this.forEachWithOccurrences((each, occurrences) -> {
            for (int i = 0; i < occurrences; i++)
            {
                result[0] = function.intValueOf(result[0], each);
            }
        });
        return result[0];
    }

    @Override
    public long injectInto(long injectedValue, LongObjectToLongFunction<? super T> function)
    {
        long[] result = {injectedValue};
        this.forEachWithOccurrences((each, occurrences) -> {
            for (int i = 0; i < occurrences; i++)
            {
                result[0] = function.longValueOf(result[0], each);
            }
        });
        return result[0];
    }

    @Override
    public double injectInto(double injectedValue, DoubleObjectToDoubleFunction<? super T> function)
    {
        double[] result = {injectedValue};
        this.forEachWithOccurrences((each, occurrences) -> {
            for (int i = 0; i < occurrences; i++)
            {
                result[0] = function.doubleValueOf(result[0], each);
            }
        });
        return result[0];
    }

    @Override
    public float injectInto(float injectedValue, FloatObjectToFloatFunction<? super T> function)
    {
        float[] result = {injectedValue};
        this.forEachWithOccurrences((each, occurrences) -> {
            for (int i = 0; i < occurrences; i++)
            {
                result[0] = function.floatValueOf(result[0], each);
            }
        });
        return result[0];
    }

    public <IV, P> IV injectIntoWith(IV injectedValue, Function3<? super IV, ? super T, ? super P, ? extends IV> function, P parameter)
    {
        IV[] result = (IV[]) new Object[]{injectedValue};
        this.forEachWithOccurrences((each, occurrences) -> {
            for (int i = 0; i < occurrences; i++)
            {
                result[0] = function.value(result[0], each, parameter);
            }
        });
        return result[0];
    }

    @Override
    public String toStringOfItemToCount()
    {
        StringJoiner joiner = new StringJoiner(", ", "{", "}");
        this.forEachWithOccurrences((each, occurrences) -> joiner.add(each + "=" + occurrences));
        return joiner.toString();
    }

    protected MutableList<ObjectIntPair<T>> toListWithOccurrences()
    {
        MutableList<ObjectIntPair<T>> result = Lists.mutable.withInitialCapacity(this.sizeDistinct());
        this.forEachWithOccurrences((each, count) -> result.add(PrimitiveTuples.pair(each, count)));
        return result;
    }

    @Override
    public MutableList<T> toList()
    {
        MutableList<T> result = Lists.mutable.withInitialCapacity(this.size());
        this.forEachWithOccurrences((each, occurrences) -> {
            for (int i = 0; i < occurrences; i++)
            {
                result.add(each);
            }
        });
        return result;
    }

    @Override
    public MutableList<T> toSortedList(Comparator<? super T> comparator)
    {
        MutableList<ObjectIntPair<T>> sorted = this.toListWithOccurrences().sortThis((o1, o2) -> comparator.compare(o1.getOne(), o2.getOne()));

        MutableList<T> result = Lists.mutable.withInitialCapacity(this.size());
        sorted.each(each -> {
            T object = each.getOne();
            int occurrences = each.getTwo();
            for (int i = 0; i < occurrences; i++)
            {
                result.add(object);
            }
        });
        return result;
    }

    @Override
    public MutableSet<T> toSet()
    {
        MutableSet<T> result = Sets.mutable.withInitialCapacity(this.sizeDistinct());
        this.forEachWithOccurrences((each, occurrences) -> result.add(each));
        return result;
    }

    @Override
    public MutableSortedSet<T> toSortedSet()
    {
        MutableSortedSet<T> result = SortedSets.mutable.empty();
        this.forEachWithOccurrences((each, occurrences) -> result.add(each));
        return result;
    }

    @Override
    public MutableSortedSet<T> toSortedSet(Comparator<? super T> comparator)
    {
        MutableSortedSet<T> result = SortedSets.mutable.with(comparator);
        this.forEachWithOccurrences((each, occurrences) -> result.add(each));
        return result;
    }

    @Override
    public MutableBag<T> toBag()
    {
        MutableBag<T> result = Bags.mutable.withInitialCapacity(this.sizeDistinct());
        this.forEachWithOccurrences(result::addOccurrences);
        return result;
    }

    @Override
    public MutableSortedBag<T> toSortedBag()
    {
        MutableSortedBag<T> result = SortedBags.mutable.empty();
        this.forEachWithOccurrences(result::addOccurrences);
        return result;
    }

    @Override
    public MutableSortedBag<T> toSortedBag(Comparator<? super T> comparator)
    {
        MutableSortedBag<T> result = SortedBags.mutable.empty(comparator);
        this.forEachWithOccurrences(result::addOccurrences);
        return result;
    }

    protected MutableList<ObjectIntPair<T>> occurrencesSortingBy(int n, IntFunction<ObjectIntPair<T>> function, MutableList<ObjectIntPair<T>> returnWhenEmpty)
    {
        if (n < 0)
        {
            throw new IllegalArgumentException("Cannot use a value of n < 0");
        }
        if (n == 0)
        {
            return returnWhenEmpty;
        }
        int keySize = Math.min(n, this.sizeDistinct());
        MutableList<ObjectIntPair<T>> sorted = this.toListWithOccurrences().sortThisByInt(function);
        MutableList<ObjectIntPair<T>> results = sorted.subList(0, keySize).toList();
        while (keySize < sorted.size() && results.getLast().getTwo() == sorted.get(keySize).getTwo())
        {
            results.add(sorted.get(keySize));
            keySize++;
        }
        return results;
    }
}
