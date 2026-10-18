/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.multimap.set;

import java.io.Externalizable;
import java.io.Serializable;

import com.m3.collections.eclipse.api.RichIterable;
import com.m3.collections.eclipse.api.block.function.Function;
import com.m3.collections.eclipse.api.block.function.Function2;
import com.m3.collections.eclipse.api.block.predicate.Predicate2;
import com.m3.collections.eclipse.api.block.procedure.Procedure2;
import com.m3.collections.eclipse.api.factory.Maps;
import com.m3.collections.eclipse.api.factory.Sets;
import com.m3.collections.eclipse.api.map.ImmutableMap;
import com.m3.collections.eclipse.api.map.MutableMap;
import com.m3.collections.eclipse.api.multimap.bag.ImmutableBagMultimap;
import com.m3.collections.eclipse.api.multimap.bag.MutableBagMultimap;
import com.m3.collections.eclipse.api.multimap.set.ImmutableSetMultimap;
import com.m3.collections.eclipse.api.multimap.set.MutableSetMultimap;
import com.m3.collections.eclipse.api.set.ImmutableSet;
import com.m3.collections.eclipse.api.set.MutableSet;
import com.m3.collections.eclipse.api.tuple.Pair;
import com.m3.collections.eclipse.impl.multimap.AbstractImmutableMultimap;
import com.m3.collections.eclipse.impl.multimap.AbstractMutableMultimap;
import com.m3.collections.eclipse.impl.multimap.ImmutableMultimapSerializationProxy;
import com.m3.collections.eclipse.impl.multimap.bag.HashBagMultimap;
import com.m3.collections.eclipse.impl.utility.Iterate;

/**
 * The default ImmutableBagMultimap implementation.
 *
 * @since 1.0
 */
public final class ImmutableSetMultimapImpl<K, V>
        extends AbstractImmutableMultimap<K, V, ImmutableSet<V>>
        implements ImmutableSetMultimap<K, V>, Serializable
{
    private static final long serialVersionUID = 1L;

    public ImmutableSetMultimapImpl(MutableMap<K, ImmutableSet<V>> map)
    {
        super(map);
    }

    public ImmutableSetMultimapImpl(ImmutableMap<K, ImmutableSet<V>> map)
    {
        super(map);
    }

    @Override
    protected ImmutableSet<V> createCollection()
    {
        return Sets.immutable.empty();
    }

    @Override
    public void forEachKeyImmutableSet(Procedure2<? super K, ? super ImmutableSet<V>> procedure)
    {
        this.getMap().forEachKeyValue(procedure);
    }

    @Override
    public ImmutableSetMultimap<K, V> newEmpty()
    {
        return new ImmutableSetMultimapImpl<>(Maps.immutable.with());
    }

    @Override
    public MutableSetMultimap<K, V> toMutable()
    {
        return new UnifiedSetMultimap<>(this);
    }

    @Override
    public ImmutableSetMultimap<K, V> toImmutable()
    {
        return this;
    }

    private Object writeReplace()
    {
        return new ImmutableSetMultimapSerializationProxy<>(this.map);
    }

    private static final class ImmutableSetMultimapSerializationProxy<K, V>
            extends ImmutableMultimapSerializationProxy<K, V, ImmutableSet<V>> implements Externalizable
    {
        private static final long serialVersionUID = 1L;

        public ImmutableSetMultimapSerializationProxy()
        {
            // For Externalizable use only
        }

        private ImmutableSetMultimapSerializationProxy(ImmutableMap<K, ImmutableSet<V>> map)
        {
            super(map);
        }

        @Override
        protected AbstractMutableMultimap<K, V, MutableSet<V>> createEmptyMutableMultimap()
        {
            return new UnifiedSetMultimap<>();
        }
    }

    @Override
    public ImmutableSetMultimap<K, V> newWith(K key, V value)
    {
        return (ImmutableSetMultimap<K, V>) super.newWith(key, value);
    }

    @Override
    public ImmutableSetMultimap<K, V> newWithout(Object key, Object value)
    {
        return (ImmutableSetMultimap<K, V>) super.newWithout(key, value);
    }

    @Override
    public ImmutableSetMultimap<K, V> newWithAll(K key, Iterable<? extends V> values)
    {
        return (ImmutableSetMultimap<K, V>) super.newWithAll(key, values);
    }

    @Override
    public ImmutableSetMultimap<K, V> newWithoutAll(Object key)
    {
        return (ImmutableSetMultimap<K, V>) super.newWithoutAll(key);
    }

    @Override
    public ImmutableSetMultimap<V, K> flip()
    {
        return Iterate.flip(this).toImmutable();
    }

    @Override
    public ImmutableSetMultimap<K, V> selectKeysValues(Predicate2<? super K, ? super V> predicate)
    {
        return this.selectKeysValues(predicate, UnifiedSetMultimap.newMultimap()).toImmutable();
    }

    @Override
    public ImmutableSetMultimap<K, V> rejectKeysValues(Predicate2<? super K, ? super V> predicate)
    {
        return this.rejectKeysValues(predicate, UnifiedSetMultimap.newMultimap()).toImmutable();
    }

    @Override
    public ImmutableSetMultimap<K, V> selectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate)
    {
        return this.selectKeysMultiValues(predicate, UnifiedSetMultimap.newMultimap()).toImmutable();
    }

    @Override
    public ImmutableSetMultimap<K, V> rejectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate)
    {
        return this.rejectKeysMultiValues(predicate, UnifiedSetMultimap.newMultimap()).toImmutable();
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
    public <V2> ImmutableBagMultimap<K, V2> collectValues(Function<? super V, ? extends V2> function)
    {
        return this.collectValues(function, HashBagMultimap.<K, V2>newMultimap()).toImmutable();
    }
}
