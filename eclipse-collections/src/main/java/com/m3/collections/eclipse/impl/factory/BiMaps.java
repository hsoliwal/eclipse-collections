/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.factory;

import com.m3.collections.eclipse.api.factory.bimap.ImmutableBiMapFactory;
import com.m3.collections.eclipse.api.factory.bimap.MutableBiMapFactory;
import com.m3.collections.eclipse.impl.bimap.immutable.ImmutableBiMapFactoryImpl;
import com.m3.collections.eclipse.impl.bimap.mutable.MutableBiMapFactoryImpl;

/**
 * @since 6.0
 */
@SuppressWarnings("ConstantNamingConvention")
public final class BiMaps
{
    public static final ImmutableBiMapFactory immutable = ImmutableBiMapFactoryImpl.INSTANCE;
    public static final MutableBiMapFactory mutable = MutableBiMapFactoryImpl.INSTANCE;

    private BiMaps()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }
}
