/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.multimap.set.strategy;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.m3.collections.eclipse.api.RichIterable;
import com.m3.collections.eclipse.api.block.HashingStrategy;
import com.m3.collections.eclipse.api.block.predicate.Predicate2;
import com.m3.collections.eclipse.api.factory.Maps;
import com.m3.collections.eclipse.api.map.MutableMap;
import com.m3.collections.eclipse.api.multimap.Multimap;
import com.m3.collections.eclipse.api.multimap.set.MutableSetMultimap;
import com.m3.collections.eclipse.api.set.MutableSet;
import com.m3.collections.eclipse.api.tuple.Pair;
import com.m3.collections.eclipse.impl.map.mutable.UnifiedMap;
import com.m3.collections.eclipse.impl.multimap.set.AbstractMutableSetMultimap;
import com.m3.collections.eclipse.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy;
import com.m3.collections.eclipse.impl.utility.Iterate;

public final class UnifiedSetWithHashingStrategyMultimap<K, V>
        extends AbstractMutableSetMultimap<K, V> implements Externalizable
{
    private static final long serialVersionUID = 1L;
    private HashingStrategy<? super V> hashingStrategy;

    /**
     * @deprecated Empty default constructor used for serialization. Instantiating an UnifiedSetWithHashingStrategyMultimap with
     * this constructor will have a null hashingStrategy and throw NullPointerException when used.
     */
    @SuppressWarnings("UnusedDeclaration")
    @Deprecated
    public UnifiedSetWithHashingStrategyMultimap()
    {
        // For Externalizable use only
    }

    public UnifiedSetWithHashingStrategyMultimap(HashingStrategy<? super V> hashingStrategy)
    {
        this.hashingStrategy = hashingStrategy;
    }

    public UnifiedSetWithHashingStrategyMultimap(UnifiedSetWithHashingStrategyMultimap<K, V> multimap)
    {
        this(multimap.hashingStrategy, multimap);
    }

    public UnifiedSetWithHashingStrategyMultimap(HashingStrategy<? super V> hashingStrategy, Multimap<? extends K, ? extends V> multimap)
    {
        super(Math.max(multimap.sizeDistinct() * 2, 16));
        this.hashingStrategy = hashingStrategy;
        this.putAll(multimap);
    }

    public UnifiedSetWithHashingStrategyMultimap(HashingStrategy<? super V> hashingStrategy, Pair<K, V>... pairs)
    {
        this.hashingStrategy = hashingStrategy;
        this.putAllPairs(pairs);
    }

    public UnifiedSetWithHashingStrategyMultimap(HashingStrategy<? super V> hashingStrategy, Iterable<Pair<K, V>> inputIterable)
    {
        this.hashingStrategy = hashingStrategy;
        for (Pair<K, V> single : inputIterable)
        {
            this.add(single);
        }
    }

    public static <K, V> UnifiedSetWithHashingStrategyMultimap<K, V> newMultimap(UnifiedSetWithHashingStrategyMultimap<K, V> multimap)
    {
        return new UnifiedSetWithHashingStrategyMultimap<>(multimap);
    }

    public static <K, V> UnifiedSetWithHashingStrategyMultimap<K, V> newMultimap(
            HashingStrategy<? super V> hashingStrategy,
            Multimap<? extends K, ? extends V> multimap)
    {
        return new UnifiedSetWithHashingStrategyMultimap<>(hashingStrategy, multimap);
    }

    public static <K, V> UnifiedSetWithHashingStrategyMultimap<K, V> newMultimap(HashingStrategy<? super V> hashingStrategy)
    {
        return new UnifiedSetWithHashingStrategyMultimap<>(hashingStrategy);
    }

    public static <K, V> UnifiedSetWithHashingStrategyMultimap<K, V> newMultimap(HashingStrategy<? super V> hashingStrategy, Pair<K, V>... pairs)
    {
        return new UnifiedSetWithHashingStrategyMultimap<>(hashingStrategy, pairs);
    }

    public static <K, V> UnifiedSetWithHashingStrategyMultimap<K, V> newMultimap(HashingStrategy<? super V> hashingStrategy, Iterable<Pair<K, V>> inputIterable)
    {
        return new UnifiedSetWithHashingStrategyMultimap<>(hashingStrategy, inputIterable);
    }

    @Override
    protected MutableMap<K, MutableSet<V>> createMap()
    {
        return Maps.mutable.empty();
    }

    @Override
    protected MutableMap<K, MutableSet<V>> createMapWithKeyCount(int keyCount)
    {
        return UnifiedMap.newMap(keyCount);
    }

    @Override
    protected UnifiedSetWithHashingStrategy<V> createCollection()
    {
        return UnifiedSetWithHashingStrategy.newSet(this.hashingStrategy);
    }

    @Override
    public UnifiedSetWithHashingStrategyMultimap<K, V> newEmpty()
    {
        return new UnifiedSetWithHashingStrategyMultimap<>(this.hashingStrategy);
    }

    public HashingStrategy<? super V> getValueHashingStrategy()
    {
        return this.hashingStrategy;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeObject(this.hashingStrategy);
        super.writeExternal(out);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        this.hashingStrategy = (HashingStrategy<? super V>) in.readObject();
        super.readExternal(in);
    }

    @Override
    public MutableSetMultimap<V, K> flip()
    {
        return Iterate.flip(this);
    }

    @Override
    public UnifiedSetWithHashingStrategyMultimap<K, V> selectKeysValues(Predicate2<? super K, ? super V> predicate)
    {
        return this.selectKeysValues(predicate, this.newEmpty());
    }

    @Override
    public UnifiedSetWithHashingStrategyMultimap<K, V> rejectKeysValues(Predicate2<? super K, ? super V> predicate)
    {
        return this.rejectKeysValues(predicate, this.newEmpty());
    }

    @Override
    public UnifiedSetWithHashingStrategyMultimap<K, V> selectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate)
    {
        return this.selectKeysMultiValues(predicate, this.newEmpty());
    }

    @Override
    public UnifiedSetWithHashingStrategyMultimap<K, V> rejectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate)
    {
        return this.rejectKeysMultiValues(predicate, this.newEmpty());
    }
}
