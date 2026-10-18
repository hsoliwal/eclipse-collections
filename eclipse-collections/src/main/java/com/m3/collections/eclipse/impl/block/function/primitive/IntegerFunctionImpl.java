/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.block.function.primitive;

import com.m3.collections.eclipse.api.block.function.Function;
import com.m3.collections.eclipse.api.block.function.primitive.IntFunction;

/**
 * IntegerFunctionImpl is an abstract implementation of the IntFunction interface which can be subclassed
 * explicitly or as an anonymous inner class, without needing to override the valueOf method defined in
 * Function.
 */
public abstract class IntegerFunctionImpl<T>
        implements IntFunction<T>, Function<T, Integer>
{
    private static final long serialVersionUID = 1L;

    @Override
    public Integer valueOf(T anObject)
    {
        return this.intValueOf(anObject);
    }
}
