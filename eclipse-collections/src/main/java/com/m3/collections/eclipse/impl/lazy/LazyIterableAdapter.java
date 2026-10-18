/*
 * Copyright (c) 2026 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.lazy;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import com.m3.collections.eclipse.api.LazyBooleanIterable;
import com.m3.collections.eclipse.api.LazyByteIterable;
import com.m3.collections.eclipse.api.LazyCharIterable;
import com.m3.collections.eclipse.api.LazyDoubleIterable;
import com.m3.collections.eclipse.api.LazyFloatIterable;
import com.m3.collections.eclipse.api.LazyIntIterable;
import com.m3.collections.eclipse.api.LazyIterable;
import com.m3.collections.eclipse.api.LazyLongIterable;
import com.m3.collections.eclipse.api.LazyShortIterable;
import com.m3.collections.eclipse.api.RichIterable;
import com.m3.collections.eclipse.api.block.function.Function;
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
import com.m3.collections.eclipse.impl.UnmodifiableIteratorAdapter;
import com.m3.collections.eclipse.impl.lazy.primitive.CollectBooleanIterable;
import com.m3.collections.eclipse.impl.lazy.primitive.CollectByteIterable;
import com.m3.collections.eclipse.impl.lazy.primitive.CollectCharIterable;
import com.m3.collections.eclipse.impl.lazy.primitive.CollectDoubleIterable;
import com.m3.collections.eclipse.impl.lazy.primitive.CollectFloatIterable;
import com.m3.collections.eclipse.impl.lazy.primitive.CollectIntIterable;
import com.m3.collections.eclipse.impl.lazy.primitive.CollectLongIterable;
import com.m3.collections.eclipse.impl.lazy.primitive.CollectShortIterable;
import com.m3.collections.eclipse.impl.utility.Iterate;
import com.m3.collections.eclipse.impl.utility.LazyIterate;

/**
 * A LazyIterableAdapter wraps any iterable with the LazyIterable interface.
 */
public class LazyIterableAdapter<T>
        extends AbstractLazyIterable<T>
{
    private final Iterable<T> adapted;

    public LazyIterableAdapter(Iterable<T> newAdapted)
    {
        this.adapted = newAdapted;
    }

    @Override
    public void each(Procedure<? super T> procedure)
    {
        Iterate.forEach(this.adapted, procedure);
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        Iterate.forEachWithIndex(this.adapted, objectIntProcedure);
    }

    @Override
    public <P> void forEachWith(Procedure2<? super T, ? super P> procedure, P parameter)
    {
        Iterate.forEachWith(this.adapted, procedure, parameter);
    }

    @Override
    public Iterator<T> iterator()
    {
        return new UnmodifiableIteratorAdapter<>(this.adapted.iterator());
    }

    @Override
    public <R extends Collection<T>> R into(R target)
    {
        Iterate.addAllIterable(this.adapted, target);
        return target;
    }

    @Override
    public LazyIterable<T> tap(Procedure<? super T> procedure)
    {
        return LazyIterate.tap(this.adapted, procedure);
    }

    @Override
    public LazyIterable<RichIterable<T>> chunk(int size)
    {
        return LazyIterate.chunk(this.adapted, size);
    }

    @Override
    public LazyBooleanIterable collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        RichIterable<T> richIterable = this.adapted instanceof RichIterable<T> ri ? ri : this;
        return new CollectBooleanIterable<>(richIterable, booleanFunction);
    }

    @Override
    public LazyByteIterable collectByte(ByteFunction<? super T> byteFunction)
    {
        RichIterable<T> richIterable = this.adapted instanceof RichIterable<T> ri ? ri : this;
        return new CollectByteIterable<>(richIterable, byteFunction);
    }

    @Override
    public LazyCharIterable collectChar(CharFunction<? super T> charFunction)
    {
        RichIterable<T> richIterable = this.adapted instanceof RichIterable<T> ri ? ri : this;
        return new CollectCharIterable<>(richIterable, charFunction);
    }

    @Override
    public LazyDoubleIterable collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        RichIterable<T> richIterable = this.adapted instanceof RichIterable<T> ri ? ri : this;
        return new CollectDoubleIterable<>(richIterable, doubleFunction);
    }

    @Override
    public LazyFloatIterable collectFloat(FloatFunction<? super T> floatFunction)
    {
        RichIterable<T> richIterable = this.adapted instanceof RichIterable<T> ri ? ri : this;
        return new CollectFloatIterable<>(richIterable, floatFunction);
    }

    @Override
    public LazyIntIterable collectInt(IntFunction<? super T> intFunction)
    {
        RichIterable<T> richIterable = this.adapted instanceof RichIterable<T> ri ? ri : this;
        return new CollectIntIterable<>(richIterable, intFunction);
    }

    @Override
    public LazyLongIterable collectLong(LongFunction<? super T> longFunction)
    {
        RichIterable<T> richIterable = this.adapted instanceof RichIterable<T> ri ? ri : this;
        return new CollectLongIterable<>(richIterable, longFunction);
    }

    @Override
    public LazyShortIterable collectShort(ShortFunction<? super T> shortFunction)
    {
        RichIterable<T> richIterable = this.adapted instanceof RichIterable<T> ri ? ri : this;
        return new CollectShortIterable<>(richIterable, shortFunction);
    }

    @Override
    public LazyIterable<T> select(Predicate<? super T> predicate)
    {
        return LazyIterate.select(this.adapted, predicate);
    }

    @Override
    public LazyIterable<T> reject(Predicate<? super T> predicate)
    {
        return LazyIterate.reject(this.adapted, predicate);
    }

    @Override
    public <V> LazyIterable<V> collect(Function<? super T, ? extends V> function)
    {
        return LazyIterate.collect(this.adapted, function);
    }

    @Override
    public <V> LazyIterable<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return LazyIterate.flatCollect(this.adapted, function);
    }

    @Override
    public <V> LazyIterable<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function)
    {
        return LazyIterate.collectIf(this.adapted, predicate, function);
    }

    @Override
    public LazyIterable<T> take(int count)
    {
        return LazyIterate.take(this.adapted, count);
    }

    @Override
    public LazyIterable<T> drop(int count)
    {
        return LazyIterate.drop(this.adapted, count);
    }

    @Override
    public LazyIterable<T> takeWhile(Predicate<? super T> predicate)
    {
        return LazyIterate.takeWhile(this.adapted, predicate);
    }

    @Override
    public LazyIterable<T> dropWhile(Predicate<? super T> predicate)
    {
        return LazyIterate.dropWhile(this.adapted, predicate);
    }

    @Override
    public LazyIterable<T> distinct()
    {
        return LazyIterate.distinct(this.adapted);
    }

    @Override
    public int size()
    {
        return Iterate.sizeOf(this.adapted);
    }

    @Override
    public boolean isEmpty()
    {
        return Iterate.isEmpty(this.adapted);
    }

    @Override
    public boolean anySatisfy(Predicate<? super T> predicate)
    {
        return Iterate.anySatisfy(this.adapted, predicate);
    }

    @Override
    public boolean allSatisfy(Predicate<? super T> predicate)
    {
        return Iterate.allSatisfy(this.adapted, predicate);
    }

    @Override
    public boolean noneSatisfy(Predicate<? super T> predicate)
    {
        return Iterate.noneSatisfy(this.adapted, predicate);
    }

    @Override
    public <P> boolean anySatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return Iterate.anySatisfyWith(this.adapted, predicate, parameter);
    }

    @Override
    public <P> boolean allSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return Iterate.allSatisfyWith(this.adapted, predicate, parameter);
    }

    @Override
    public <P> boolean noneSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return Iterate.noneSatisfyWith(this.adapted, predicate, parameter);
    }

    @Override
    public T getFirst()
    {
        return Iterate.getFirst(this.adapted);
    }

    @Override
    public T getLast()
    {
        return Iterate.getLast(this.adapted);
    }

    @Override
    public T detect(Predicate<? super T> predicate)
    {
        return Iterate.detect(this.adapted, predicate);
    }

    @Override
    public <P> T detectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return Iterate.detectWith(this.adapted, predicate, parameter);
    }

    @Override
    public Optional<T> detectOptional(Predicate<? super T> predicate)
    {
        return Iterate.detectOptional(this.adapted, predicate);
    }

    @Override
    public <P> Optional<T> detectWithOptional(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return Iterate.detectWithOptional(this.adapted, predicate, parameter);
    }
}
