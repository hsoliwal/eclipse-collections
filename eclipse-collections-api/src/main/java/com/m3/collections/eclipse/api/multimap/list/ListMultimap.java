/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.api.multimap.list;

import com.m3.collections.eclipse.api.RichIterable;
import com.m3.collections.eclipse.api.block.function.Function;
import com.m3.collections.eclipse.api.block.function.Function2;
import com.m3.collections.eclipse.api.block.predicate.Predicate2;
import com.m3.collections.eclipse.api.list.ListIterable;
import com.m3.collections.eclipse.api.multimap.bag.BagMultimap;
import com.m3.collections.eclipse.api.multimap.bag.UnsortedBagMultimap;
import com.m3.collections.eclipse.api.multimap.ordered.ReversibleIterableMultimap;
import com.m3.collections.eclipse.api.tuple.Pair;

public interface ListMultimap<K, V>
        extends ReversibleIterableMultimap<K, V>
{
    @Override
    ListMultimap<K, V> newEmpty();

    @Override
    ListIterable<V> get(K key);

    @Override
    MutableListMultimap<K, V> toMutable();

    @Override
    ImmutableListMultimap<K, V> toImmutable();

    @Override
    UnsortedBagMultimap<V, K> flip();

    @Override
    ListMultimap<K, V> selectKeysValues(Predicate2<? super K, ? super V> predicate);

    @Override
    ListMultimap<K, V> rejectKeysValues(Predicate2<? super K, ? super V> predicate);

    @Override
    ListMultimap<K, V> selectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate);

    @Override
    ListMultimap<K, V> rejectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate);

    @Override
    <K2, V2> BagMultimap<K2, V2> collectKeysValues(Function2<? super K, ? super V, Pair<K2, V2>> function);

    @Override
    <K2, V2> BagMultimap<K2, V2> collectKeyMultiValues(Function<? super K, ? extends K2> keyFunction, Function<? super V, ? extends V2> valueFunction);

    @Override
    <V2> ListMultimap<K, V2> collectValues(Function<? super V, ? extends V2> function);
}
