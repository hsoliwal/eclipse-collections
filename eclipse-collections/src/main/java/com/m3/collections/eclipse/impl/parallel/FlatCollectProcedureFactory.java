/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.parallel;

import com.m3.collections.eclipse.api.block.function.Function;
import com.m3.collections.eclipse.impl.block.procedure.FlatCollectProcedure;
import com.m3.collections.eclipse.impl.list.mutable.FastList;

public final class FlatCollectProcedureFactory<T, V> implements ProcedureFactory<FlatCollectProcedure<T, V>>
{
    private final int collectionSize;
    private final Function<? super T, ? extends Iterable<V>> function;

    public FlatCollectProcedureFactory(Function<? super T, ? extends Iterable<V>> function, int newTaskSize)
    {
        this.collectionSize = newTaskSize;
        this.function = function;
    }

    @Override
    public FlatCollectProcedure<T, V> create()
    {
        return new FlatCollectProcedure<>(this.function, new FastList<>(this.collectionSize));
    }
}
