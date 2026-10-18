/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.set.immutable.primitive;

import java.io.IOException;
import java.io.Serializable;

import com.m3.collections.eclipse.api.BooleanIterable;
import com.m3.collections.eclipse.api.LazyBooleanIterable;
import com.m3.collections.eclipse.api.LazyIterable;
import com.m3.collections.eclipse.api.RichIterable;
import com.m3.collections.eclipse.api.bag.primitive.MutableBooleanBag;
import com.m3.collections.eclipse.api.block.function.primitive.BooleanToObjectFunction;
import com.m3.collections.eclipse.api.block.function.primitive.ObjectBooleanToObjectFunction;
import com.m3.collections.eclipse.api.block.predicate.primitive.BooleanPredicate;
import com.m3.collections.eclipse.api.block.procedure.primitive.BooleanProcedure;
import com.m3.collections.eclipse.api.factory.Lists;
import com.m3.collections.eclipse.api.factory.Sets;
import com.m3.collections.eclipse.api.factory.primitive.BooleanBags;
import com.m3.collections.eclipse.api.factory.primitive.BooleanLists;
import com.m3.collections.eclipse.api.iterator.BooleanIterator;
import com.m3.collections.eclipse.api.list.primitive.MutableBooleanList;
import com.m3.collections.eclipse.api.set.ImmutableSet;
import com.m3.collections.eclipse.api.set.primitive.BooleanSet;
import com.m3.collections.eclipse.api.set.primitive.ImmutableBooleanSet;
import com.m3.collections.eclipse.api.set.primitive.MutableBooleanSet;
import com.m3.collections.eclipse.api.tuple.primitive.BooleanBooleanPair;
import com.m3.collections.eclipse.impl.factory.primitive.BooleanSets;
import com.m3.collections.eclipse.impl.iterator.ImmutableEmptyBooleanIterator;
import com.m3.collections.eclipse.impl.lazy.primitive.LazyBooleanIterableAdapter;

final class ImmutableBooleanEmptySet implements ImmutableBooleanSet, Serializable
{
    static final ImmutableBooleanSet INSTANCE = new ImmutableBooleanEmptySet();

    private static final boolean[] TO_ARRAY = new boolean[0];

    private ImmutableBooleanEmptySet()
    {
        // Singleton
    }

    @Override
    public ImmutableBooleanSet newWith(boolean element)
    {
        return element ? ImmutableTrueSet.INSTANCE : ImmutableFalseSet.INSTANCE;
    }

    @Override
    public ImmutableBooleanSet newWithout(boolean element)
    {
        return this;
    }

    @Override
    public ImmutableBooleanSet newWithAll(BooleanIterable elements)
    {
        ImmutableBooleanSet result = this;
        BooleanIterator booleanIterator = elements.booleanIterator();
        while (booleanIterator.hasNext())
        {
            result = result.newWith(booleanIterator.next());
        }
        return result;
    }

    @Override
    public ImmutableBooleanSet newWithoutAll(BooleanIterable elements)
    {
        return this;
    }

    @Override
    public BooleanIterator booleanIterator()
    {
        return ImmutableEmptyBooleanIterator.INSTANCE;
    }

    @Override
    public void forEach(BooleanProcedure procedure)
    {
    }

    /**
     * @since 7.0.
     */
    @Override
    public void each(BooleanProcedure procedure)
    {
    }

    @Override
    public <T> T injectInto(T injectedValue, ObjectBooleanToObjectFunction<? super T, ? extends T> function)
    {
        return injectedValue;
    }

    @Override
    public RichIterable<BooleanIterable> chunk(int size)
    {
        return Lists.immutable.empty();
    }

    @Override
    public int count(BooleanPredicate predicate)
    {
        return 0;
    }

    @Override
    public boolean anySatisfy(BooleanPredicate predicate)
    {
        return false;
    }

    @Override
    public boolean allSatisfy(BooleanPredicate predicate)
    {
        return true;
    }

    @Override
    public boolean noneSatisfy(BooleanPredicate predicate)
    {
        return true;
    }

    @Override
    public ImmutableBooleanSet select(BooleanPredicate predicate)
    {
        return this;
    }

    @Override
    public ImmutableBooleanSet reject(BooleanPredicate predicate)
    {
        return this;
    }

    @Override
    public boolean detectIfNone(BooleanPredicate predicate, boolean ifNone)
    {
        return ifNone;
    }

    @Override
    public <V> ImmutableSet<V> collect(BooleanToObjectFunction<? extends V> function)
    {
        return Sets.immutable.with();
    }

    @Override
    public boolean[] toArray()
    {
        return TO_ARRAY;
    }

    @Override
    public boolean[] toArray(boolean[] target)
    {
        return target;
    }

    @Override
    public boolean contains(boolean value)
    {
        return false;
    }

    @Override
    public boolean containsAll(boolean... source)
    {
        return source.length == 0;
    }

    @Override
    public boolean containsAll(BooleanIterable source)
    {
        BooleanIterator iterator = source.booleanIterator();
        return !iterator.hasNext();
    }

    @Override
    public BooleanSet freeze()
    {
        return this;
    }

    @Override
    public ImmutableBooleanSet toImmutable()
    {
        return this;
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
    public LazyIterable<BooleanBooleanPair> cartesianProduct(BooleanSet set)
    {
        return BooleanSets.cartesianProduct(this, set);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof BooleanSet))
        {
            return false;
        }

        BooleanSet other = (BooleanSet) obj;
        return other.isEmpty();
    }

    @Override
    public int hashCode()
    {
        return 0;
    }

    @Override
    public String toString()
    {
        return "[]";
    }

    @Override
    public String makeString()
    {
        return "";
    }

    @Override
    public String makeString(String separator)
    {
        return "";
    }

    @Override
    public String makeString(String start, String separator, String end)
    {
        return start + end;
    }

    @Override
    public void appendString(Appendable appendable)
    {
    }

    @Override
    public void appendString(Appendable appendable, String separator)
    {
    }

    @Override
    public void appendString(Appendable appendable, String start, String separator, String end)
    {
        try
        {
            appendable.append(start);
            appendable.append(end);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MutableBooleanList toList()
    {
        return BooleanLists.mutable.withAll(this);
    }

    @Override
    public MutableBooleanSet toSet()
    {
        return BooleanSets.mutable.withAll(this);
    }

    @Override
    public MutableBooleanBag toBag()
    {
        return BooleanBags.mutable.withAll(this);
    }

    @Override
    public LazyBooleanIterable asLazy()
    {
        return new LazyBooleanIterableAdapter(this);
    }

    private Object writeReplace()
    {
        return new ImmutableBooleanSetSerializationProxy(this);
    }
}
