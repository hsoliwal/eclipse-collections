/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.multimap.set.sorted;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Comparator;

import com.m3.collections.eclipse.api.RichIterable;
import com.m3.collections.eclipse.api.block.function.Function;
import com.m3.collections.eclipse.api.block.function.Function2;
import com.m3.collections.eclipse.api.block.predicate.Predicate2;
import com.m3.collections.eclipse.api.factory.Maps;
import com.m3.collections.eclipse.api.factory.SortedSets;
import com.m3.collections.eclipse.api.map.ImmutableMap;
import com.m3.collections.eclipse.api.map.MutableMap;
import com.m3.collections.eclipse.api.multimap.bag.ImmutableBagMultimap;
import com.m3.collections.eclipse.api.multimap.bag.MutableBagMultimap;
import com.m3.collections.eclipse.api.multimap.list.ImmutableListMultimap;
import com.m3.collections.eclipse.api.multimap.set.ImmutableSetMultimap;
import com.m3.collections.eclipse.api.multimap.sortedset.ImmutableSortedSetMultimap;
import com.m3.collections.eclipse.api.multimap.sortedset.MutableSortedSetMultimap;
import com.m3.collections.eclipse.api.set.sorted.ImmutableSortedSet;
import com.m3.collections.eclipse.api.set.sorted.MutableSortedSet;
import com.m3.collections.eclipse.api.tuple.Pair;
import com.m3.collections.eclipse.impl.multimap.AbstractImmutableMultimap;
import com.m3.collections.eclipse.impl.multimap.AbstractMutableMultimap;
import com.m3.collections.eclipse.impl.multimap.ImmutableMultimapSerializationProxy;
import com.m3.collections.eclipse.impl.multimap.bag.HashBagMultimap;
import com.m3.collections.eclipse.impl.multimap.list.FastListMultimap;
import com.m3.collections.eclipse.impl.utility.Iterate;

/**
 * The default ImmutableSortedSetMultimap implementation.
 *
 * @since 1.0
 */
public final class ImmutableSortedSetMultimapImpl<K, V>
        extends AbstractImmutableMultimap<K, V, ImmutableSortedSet<V>>
        implements ImmutableSortedSetMultimap<K, V>, Serializable
{
    private static final long serialVersionUID = 1L;
    private final Comparator<? super V> comparator;

    ImmutableSortedSetMultimapImpl(MutableMap<K, ImmutableSortedSet<V>> map)
    {
        super(map);
        this.comparator = null;
    }

    public ImmutableSortedSetMultimapImpl(MutableMap<K, ImmutableSortedSet<V>> map, Comparator<? super V> comparator)
    {
        super(map);
        this.comparator = comparator;
    }

    ImmutableSortedSetMultimapImpl(ImmutableMap<K, ImmutableSortedSet<V>> map)
    {
        super(map);
        this.comparator = null;
    }

    public ImmutableSortedSetMultimapImpl(ImmutableMap<K, ImmutableSortedSet<V>> map, Comparator<? super V> comparator)
    {
        super(map);
        this.comparator = comparator;
    }

    @Override
    protected ImmutableSortedSet<V> createCollection()
    {
        return SortedSets.immutable.with(this.comparator());
    }

    @Override
    public ImmutableSortedSetMultimap<K, V> newEmpty()
    {
        return new ImmutableSortedSetMultimapImpl<>(Maps.immutable.with(), this.comparator());
    }

    @Override
    public Comparator<? super V> comparator()
    {
        return this.comparator;
    }

    @Override
    public MutableSortedSetMultimap<K, V> toMutable()
    {
        return new TreeSortedSetMultimap<>(this);
    }

    @Override
    public ImmutableSortedSetMultimap<K, V> toImmutable()
    {
        return this;
    }

    private Object writeReplace()
    {
        return new ImmutableSortedSetMultimapSerializationProxy<>(this.map, this.comparator());
    }

    private static final class ImmutableSortedSetMultimapSerializationProxy<K, V>
            extends ImmutableMultimapSerializationProxy<K, V, ImmutableSortedSet<V>> implements Externalizable
    {
        private static final long serialVersionUID = 1L;
        private Comparator<? super V> comparator;

        public ImmutableSortedSetMultimapSerializationProxy()
        {
            // For Externalizable use only
        }

        private ImmutableSortedSetMultimapSerializationProxy(ImmutableMap<K, ImmutableSortedSet<V>> map, Comparator<? super V> comparator)
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
        protected AbstractMutableMultimap<K, V, MutableSortedSet<V>> createEmptyMutableMultimap()
        {
            return new TreeSortedSetMultimap<>(this.comparator);
        }
    }

    @Override
    public ImmutableSortedSetMultimap<K, V> newWith(K key, V value)
    {
        return (ImmutableSortedSetMultimap<K, V>) super.newWith(key, value);
    }

    @Override
    public ImmutableSortedSetMultimap<K, V> newWithout(Object key, Object value)
    {
        return (ImmutableSortedSetMultimap<K, V>) super.newWithout(key, value);
    }

    @Override
    public ImmutableSortedSetMultimap<K, V> newWithAll(K key, Iterable<? extends V> values)
    {
        return (ImmutableSortedSetMultimap<K, V>) super.newWithAll(key, values);
    }

    @Override
    public ImmutableSortedSetMultimap<K, V> newWithoutAll(Object key)
    {
        return (ImmutableSortedSetMultimap<K, V>) super.newWithoutAll(key);
    }

    @Override
    public ImmutableSetMultimap<V, K> flip()
    {
        return Iterate.flip(this).toImmutable();
    }

    @Override
    public ImmutableSortedSetMultimap<K, V> selectKeysValues(Predicate2<? super K, ? super V> predicate)
    {
        return this.selectKeysValues(predicate, TreeSortedSetMultimap.newMultimap(this.comparator())).toImmutable();
    }

    @Override
    public ImmutableSortedSetMultimap<K, V> rejectKeysValues(Predicate2<? super K, ? super V> predicate)
    {
        return this.rejectKeysValues(predicate, TreeSortedSetMultimap.newMultimap(this.comparator())).toImmutable();
    }

    @Override
    public ImmutableSortedSetMultimap<K, V> selectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate)
    {
        return this.selectKeysMultiValues(predicate, TreeSortedSetMultimap.newMultimap(this.comparator())).toImmutable();
    }

    @Override
    public ImmutableSortedSetMultimap<K, V> rejectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate)
    {
        return this.rejectKeysMultiValues(predicate, TreeSortedSetMultimap.newMultimap(this.comparator())).toImmutable();
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
}
