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
import com.m3.collections.eclipse.api.block.predicate.Predicate;
import com.m3.collections.eclipse.impl.block.procedure.FastListCollectIfProcedure;
import com.m3.collections.eclipse.impl.list.mutable.FastList;

public final class FastListCollectIfProcedureFactory<T, V> implements ProcedureFactory<FastListCollectIfProcedure<T, V>>
{
    private final int collectionSize;
    private final Function<? super T, V> function;
    private final Predicate<? super T> predicate;

    public FastListCollectIfProcedureFactory(
            Function<? super T, V> function,
            Predicate<? super T> predicate,
            int newTaskSize)
    {
        this.collectionSize = newTaskSize;
        this.function = function;
        this.predicate = predicate;
    }

    @Override
    public FastListCollectIfProcedure<T, V> create()
    {
        return new FastListCollectIfProcedure<>(FastList.newList(this.collectionSize), this.function, this.predicate);
    }
}
