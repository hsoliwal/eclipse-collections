/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.api.multimap.bag;

import com.m3.collections.eclipse.api.RichIterable;
import com.m3.collections.eclipse.api.bag.ImmutableBag;
import com.m3.collections.eclipse.api.block.function.Function;
import com.m3.collections.eclipse.api.block.function.Function2;
import com.m3.collections.eclipse.api.block.predicate.Predicate2;
import com.m3.collections.eclipse.api.block.procedure.Procedure2;
import com.m3.collections.eclipse.api.tuple.Pair;

/**
 * @since 1.0
 */
public interface ImmutableBagMultimap<K, V>
        extends UnsortedBagMultimap<K, V>, ImmutableBagIterableMultimap<K, V>
{
    /**
     * @since 11.0
     */
    void forEachKeyImmutableBag(Procedure2<? super K, ? super ImmutableBag<V>> procedure);

    @Override
    ImmutableBagMultimap<K, V> newEmpty();

    @Override
    ImmutableBag<V> get(K key);

    @Override
    ImmutableBagMultimap<K, V> newWith(K key, V value);

    @Override
    ImmutableBagMultimap<K, V> newWithout(Object key, Object value);

    @Override
    ImmutableBagMultimap<K, V> newWithAll(K key, Iterable<? extends V> values);

    @Override
    ImmutableBagMultimap<K, V> newWithoutAll(Object key);

    @Override
    ImmutableBagMultimap<V, K> flip();

    @Override
    ImmutableBagMultimap<K, V> selectKeysValues(Predicate2<? super K, ? super V> predicate);

    @Override
    ImmutableBagMultimap<K, V> rejectKeysValues(Predicate2<? super K, ? super V> predicate);

    @Override
    ImmutableBagMultimap<K, V> selectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate);

    @Override
    ImmutableBagMultimap<K, V> rejectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate);

    @Override
    <K2, V2> ImmutableBagMultimap<K2, V2> collectKeysValues(Function2<? super K, ? super V, Pair<K2, V2>> function);

    @Override
    <K2, V2> ImmutableBagMultimap<K2, V2> collectKeyMultiValues(Function<? super K, ? extends K2> keyFunction, Function<? super V, ? extends V2> valueFunction);

    @Override
    <V2> ImmutableBagMultimap<K, V2> collectValues(Function<? super V, ? extends V2> function);
}
