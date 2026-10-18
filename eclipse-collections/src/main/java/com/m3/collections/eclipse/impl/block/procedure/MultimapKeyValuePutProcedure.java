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

/**
 * MultimapKeyValuePutProcedure uses two Functions to calculate the key and value for an object and puts the key and value
 * into the specified {@link MutableMultimap}.
 */
public class MultimapKeyValuePutProcedure<T, K, V> implements Procedure<T>
{
    private static final long serialVersionUID = 1L;

    private final MutableMultimap<K, V> mutableMultimap;
    private final Function<? super T, ? extends K> keyFunction;
    private final Function<? super T, ? extends V> valueFunction;

    public MultimapKeyValuePutProcedure(MutableMultimap<K, V> mutableMultimap, Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends V> valueFunction)
    {
        this.mutableMultimap = mutableMultimap;
        this.keyFunction = keyFunction;
        this.valueFunction = valueFunction;
    }

    @Override
    public void value(T each)
    {
        K key = this.keyFunction.valueOf(each);
        V value = this.valueFunction.valueOf(each);
        this.mutableMultimap.put(key, value);
    }
}
