/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.block.procedure;

import com.m3.collections.eclipse.api.block.function.Function;
import com.m3.collections.eclipse.api.block.function.Function0;
import com.m3.collections.eclipse.api.block.function.Function2;
import com.m3.collections.eclipse.api.block.procedure.Procedure;
import com.m3.collections.eclipse.api.map.MutableMapIterable;

/**
 * This procedure is used to apply an aggregate function like sum on a grouped set of data. The values in the
 * grouping must be immutable using this procedure. An example would be using an Integer which is immutable
 * vs. an AtomicInteger which is not.
 */
public final class NonMutatingAggregationProcedure<T, K, V> implements Procedure<T>
{
    private static final long serialVersionUID = 1L;
    private final MutableMapIterable<K, V> map;
    private final Function<? super T, ? extends K> groupBy;
    private final Function0<? extends V> zeroValueFactory;
    private final Function2<? super V, ? super T, ? extends V> nonMutatingAggregator;

    public NonMutatingAggregationProcedure(
            MutableMapIterable<K, V> map,
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Function2<? super V, ? super T, ? extends V> nonMutatingAggregator)
    {
        this.map = map;
        this.groupBy = groupBy;
        this.zeroValueFactory = zeroValueFactory;
        this.nonMutatingAggregator = nonMutatingAggregator;
    }

    @Override
    public void value(T each)
    {
        K key = this.groupBy.valueOf(each);
        this.map.updateValueWith(key, this.zeroValueFactory, this.nonMutatingAggregator, each);
    }
}
