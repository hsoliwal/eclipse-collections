/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.api.multimap.sortedbag;

import com.m3.collections.eclipse.api.RichIterable;
import com.m3.collections.eclipse.api.bag.sorted.SortedBag;
import com.m3.collections.eclipse.api.block.function.Function;
import com.m3.collections.eclipse.api.block.function.Function2;
import com.m3.collections.eclipse.api.block.predicate.Predicate2;
import com.m3.collections.eclipse.api.multimap.bag.BagMultimap;
import com.m3.collections.eclipse.api.multimap.list.ListMultimap;
import com.m3.collections.eclipse.api.multimap.ordered.ReversibleIterableMultimap;
import com.m3.collections.eclipse.api.multimap.ordered.SortedIterableMultimap;
import com.m3.collections.eclipse.api.tuple.Pair;

/**
 * @since 4.2
 */
public interface SortedBagMultimap<K, V>
        extends BagMultimap<K, V>, SortedIterableMultimap<K, V>, ReversibleIterableMultimap<K, V>
{
    @Override
    SortedBagMultimap<K, V> newEmpty();

    @Override
    SortedBag<V> get(K key);

    @Override
    MutableSortedBagMultimap<K, V> toMutable();

    @Override
    ImmutableSortedBagMultimap<K, V> toImmutable();

    @Override
    SortedBagMultimap<K, V> selectKeysValues(Predicate2<? super K, ? super V> predicate);

    @Override
    SortedBagMultimap<K, V> rejectKeysValues(Predicate2<? super K, ? super V> predicate);

    @Override
    SortedBagMultimap<K, V> selectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate);

    @Override
    SortedBagMultimap<K, V> rejectKeysMultiValues(Predicate2<? super K, ? super RichIterable<V>> predicate);

    @Override
    <K2, V2> BagMultimap<K2, V2> collectKeysValues(Function2<? super K, ? super V, Pair<K2, V2>> function);

    @Override
    <K2, V2> BagMultimap<K2, V2> collectKeyMultiValues(Function<? super K, ? extends K2> keyFunction, Function<? super V, ? extends V2> valueFunction);

    @Override
    <V2> ListMultimap<K, V2> collectValues(Function<? super V, ? extends V2> function);
}
