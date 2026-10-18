/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.multimap.bag.sorted.immutable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Comparator;

import com.m3.collections.eclipse.api.RichIterable;
import com.m3.collections.eclipse.api.bag.sorted.ImmutableSortedBag;
import com.m3.collections.eclipse.api.bag.sorted.MutableSortedBag;
import com.m3.collections.eclipse.api.block.function.Function;
import com.m3.collections.eclipse.api.block.function.Function2;
import com.m3.collections.eclipse.api.block.predicate.Predicate2;
import com.m3.collections.eclipse.api.factory.Maps;
import com.m3.collections.eclipse.api.factory.SortedBags;
import com.m3.collections.eclipse.api.map.ImmutableMap;
import com.m3.collections.eclipse.api.map.MutableMap;
import com.m3.collections.eclipse.api.multimap.bag.ImmutableBagMultimap;
import com.m3.collections.eclipse.api.multimap.bag.MutableBagMultimap;
import com.m3.collections.eclipse.api.multimap.list.ImmutableListMultimap;
import com.m3.collections.eclipse.api.multimap.sortedbag.ImmutableSortedBagMultimap;
import com.m3.collections.eclipse.api.multimap.sortedbag.MutableSortedBagMultimap;
import com.m3.collections.eclipse.api.tuple.Pair;
import com.m3.collections.eclipse.impl.multimap.AbstractImmutableMultimap;
import com.m3.collections.eclipse.impl.multimap.AbstractMutableMultimap;
import com.m3.collections.eclipse.impl.multimap.ImmutableMultimapSerializationProxy;
import com.m3.collections.eclipse.impl.multimap.bag.HashBagMultimap;
import com.m3.collections.eclipse.impl.multimap.bag.sorted.mutable.TreeBagMultimap;
import com.m3.collections.eclipse.impl.multimap.list.FastListMultimap;
import com.m3.collections.eclipse.impl.utility.Iterate;

public class ImmutableSortedBagMultimapImpl<K, V>
        extends AbstractImmutableMultimap<K, V, ImmutableSortedBag<V>>
        implements ImmutableSortedBagMultimap<K, V>, Serializable
{
    private static final long serialVersionUID = 1L;
    private final Comparator<? super V> comparator;

    ImmutableSortedBagMultimapImpl(MutableMap<K, ImmutableSortedBag<V>> map)
    {
        super(map);
        this.comparator = null;
    }

    public ImmutableSortedBagMultimapImpl(MutableMap<K, ImmutableSortedBag<V>> map, Comparator<? super V> comparator)
    {
        super(map);
        this.comparator = comparator;
    }

    ImmutableSortedBagMultimapImpl(ImmutableMap<K, ImmutableSortedBag<V>> map)
    {
        super(map);
        this.comparator = null;
    }

    public ImmutableSortedBagMultimapImpl(ImmutableMap<K, ImmutableSortedBag<V>> map, Comparator<? super V> comparator)
    {
        super(map);
        this.comparator = comparator;
    }

    @Override
    public ImmutableSortedBagMultimap<K, V> newWith(K key, V value)
    {
        return (ImmutableSortedBagMultimap<K, V>) super.newWith(key, value);
    }

    @Override
    public ImmutableSortedBagMultimap<K, V> newWithout(Object key, Object value)
    {
        return (ImmutableSortedBagMultimap<K, V>) super.newWithout(key, value);
    }

    @Override
    public ImmutableSortedBagMultimap<K, V> newWithAll(K key, Iterable<? extends V> values)
    {
        return (ImmutableSortedBagMultimap<K, V>) super.newWithAll(key, values);
    }

    @Override
    public ImmutableSortedBagMultimap<K, V> newWithoutAll(Object key)
    {
        return (ImmutableSortedBagMultimap<K, V>) super.newWithoutAll(key);
    }

    @Override
    protected ImmutableSortedBag<V> createCollection()
    {
        return SortedBags.immutable.with(this.comparator);
    }

    @Override
    public ImmutableSortedBagMultimapImpl<K, V> toImmutable()
    {
        return this;
    }

    @Override
    public ImmutableSortedBagMultimap<K, V> newEmpty()
    {
        return new ImmutableSortedBagMultimapImpl<>(Maps.immutable.with(), this.comparator);
    }

    @Override
    public Comparator<? super V> comparator()
    {
        return this.comparator;
    }

    @Override
    public MutableSortedBagMultimap<K, V> toMutable()
    {
        return new TreeBagMultimap<>(this);
    }

    @Override
    public ImmutableBagMultimap<V, K> flip()
    {
        return Iterate.flip(this).toImmutable();
    }

    @Override
    public ImmutableSortedBagMultimap<K, V> selectKeysValues(Predicate2<? super K, ? super V> predicate)
    {
        return this.selectKeysValues(predicate, TreeBagMultimap.newMultimap(this.comparator)).toImmutable();
    }

    @Override
    public ImmutableSortedBagMultimap<K, V> rejectKeysValues(Predicate2<? super K, ? super V> predicate)
    {
        return this.rejectKeysValues(predicate, TreeBagMultimap.newMultimap(this.comparator)).toImmutable();
    }

    @Override
    public ImmutableSortedBagMultimap<K, V> selectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate)
    {
        return this.selectKeysMultiValues(predicate, TreeBagMultimap.newMultimap(this.comparator)).toImmutable();
    }

    @Override
    public ImmutableSortedBagMultimap<K, V> rejectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate)
    {
        return this.rejectKeysMultiValues(predicate, TreeBagMultimap.newMultimap(this.comparator)).toImmutable();
    }

    @Override
    public <K2, V2> ImmutableBagMultimap<K2, V2> collectKeysValues(Function2<? super K, ? super V, Pair<K2, V2>> function)
    {
        return this.collectKeysValues(function, HashBagMultimap.newMultimap()).toImmutable();
    }

    @Override
    public <K2, V2> ImmutableBagMultimap<K2, V2> collectKeyMultiValues(Function<? super K, ? extends K2> keyFunction, Function<? super V, ? extends V2> valueFunction)
    {
        MutableBagMultimap<K2, V2> mutableMultimap = this.collectKeyMultiValues(keyFunction, valueFunction, HashBagMultimap.newMultimap());
        return mutableMultimap.toImmutable();
    }

    @Override
    public <V2> ImmutableListMultimap<K, V2> collectValues(Function<? super V, ? extends V2> function)
    {
        return this.collectValues(function, FastListMultimap.<K, V2>newMultimap()).toImmutable();
    }

    private Object writeReplace()
    {
        return new ImmutableSortedBagMultimapSerializationProxy<>(this.map, this.comparator());
    }

    private static final class ImmutableSortedBagMultimapSerializationProxy<K, V>
            extends ImmutableMultimapSerializationProxy<K, V, ImmutableSortedBag<V>> implements Externalizable
    {
        private static final long serialVersionUID = 1L;
        private Comparator<? super V> comparator;

        public ImmutableSortedBagMultimapSerializationProxy()
        {
            // For Externalizable use only
        }

        private ImmutableSortedBagMultimapSerializationProxy(ImmutableMap<K, ImmutableSortedBag<V>> map, Comparator<? super V> comparator)
        {
            super(map);
            this.comparator = comparator;
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException
        {
            out.writeObject(this.comparator);
            super.writeExternal(out);
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
        {
            this.comparator = (Comparator<? super V>) in.readObject();
            super.readExternal(in);
        }

        @Override
        protected AbstractMutableMultimap<K, V, MutableSortedBag<V>> createEmptyMutableMultimap()
        {
            return new TreeBagMultimap<>(this.comparator);
        }
    }
}
