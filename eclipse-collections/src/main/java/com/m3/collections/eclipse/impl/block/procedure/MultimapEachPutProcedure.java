/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.block.procedure;

import com.m3.collections.eclipse.api.block.function.Function;
import com.m3.collections.eclipse.api.block.procedure.Procedure;
import com.m3.collections.eclipse.api.multimap.MutableMultimap;
import com.m3.collections.eclipse.impl.utility.Iterate;

/**
 * MultimapEachPutProcedure uses a Function to calculate the keys for an object and puts the object with each of
 * the keys into the specified {@link MutableMultimap}.
 */
public final class MultimapEachPutProcedure<K, V> implements Procedure<V>
{
    private static final long serialVersionUID = 2L;
    private final MutableMultimap<K, V> multimap;
    private final Function<? super V, ? extends Iterable<K>> keyFunction;

    public MultimapEachPutProcedure(
            MutableMultimap<K, V> multimap,
            Function<? super V, ? extends Iterable<K>> keyFunction)
    {
        this.multimap = multimap;
        this.keyFunction = keyFunction;
    }

    public static <K, V> MultimapEachPutProcedure<K, V> on(
            MutableMultimap<K, V> multimap,
            Function<? super V, ? extends Iterable<K>> keyFunction)
    {
        return new MultimapEachPutProcedure<>(multimap, keyFunction);
    }

    @Override
    public void value(V each)
    {
        Iterate.forEachWith(this.keyFunction.valueOf(each), this.multimap::put, each);
    }
}
