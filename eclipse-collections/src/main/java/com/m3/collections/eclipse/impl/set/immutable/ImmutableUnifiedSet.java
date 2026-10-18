/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.set.immutable;

import java.io.Serializable;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;

import com.m3.collections.eclipse.api.block.procedure.Procedure;
import com.m3.collections.eclipse.api.set.ImmutableSet;
import com.m3.collections.eclipse.api.set.ParallelUnsortedSetIterable;
import com.m3.collections.eclipse.impl.UnmodifiableIteratorAdapter;
import com.m3.collections.eclipse.impl.parallel.BatchIterable;
import com.m3.collections.eclipse.impl.set.mutable.UnifiedSet;

final class ImmutableUnifiedSet<T>
        extends AbstractImmutableSet<T>
        implements Serializable, BatchIterable<T>
{
    private static final long serialVersionUID = 1L;

    private final UnifiedSet<T> delegate;

    private ImmutableUnifiedSet(UnifiedSet<T> delegate)
    {
        this.delegate = delegate;
    }

    @Override
    public int size()
    {
        return this.delegate.size();
    }

    @Override
    public boolean equals(Object other)
    {
        if (other == this)
        {
            return true;
        }

        return this.delegate.equals(other);
    }

    @Override
    public int hashCode()
    {
        return this.delegate.hashCode();
    }

    @Override
    public boolean contains(Object object)
    {
        return this.delegate.contains(object);
    }

    @Override
    public Iterator<T> iterator()
    {
        return new UnmodifiableIteratorAdapter<>(this.delegate.iterator());
    }

    @Override
    public T getFirst()
    {
        return this.delegate.getFirst();
    }

    @Override
    public T getLast()
    {
        return this.delegate.getLast();
    }

    @Override
    public T getOnly()
    {
        return this.delegate.getOnly();
    }

    public static <T> ImmutableSet<T> newSetWith(T... elements)
    {
        return new ImmutableUnifiedSet<>(UnifiedSet.newSetWith(elements));
    }

    public static <T> ImmutableSet<T> newSet(Iterable<? extends T> iterable)
    {
        return new ImmutableUnifiedSet<>(UnifiedSet.newSet(iterable));
    }

    @Override
    public void each(Procedure<? super T> procedure)
    {
        this.delegate.forEach(procedure);
    }

    @Override
    public int getBatchCount(int batchSize)
    {
        return this.delegate.getBatchCount(batchSize);
    }

    @Override
    public void batchForEach(Procedure<? super T> procedure, int sectionIndex, int sectionCount)
    {
        this.delegate.batchForEach(procedure, sectionIndex, sectionCount);
    }

    private Object writeReplace()
    {
        return new ImmutableSetSerializationProxy<>(this);
    }

    @Override
    public ParallelUnsortedSetIterable<T> asParallel(ExecutorService executorService, int batchSize)
    {
        return this.delegate.asParallel(executorService, batchSize);
    }
}
