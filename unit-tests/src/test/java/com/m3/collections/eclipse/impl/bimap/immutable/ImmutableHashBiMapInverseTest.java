/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.bimap.immutable;

import com.m3.collections.eclipse.api.bimap.ImmutableBiMap;
import com.m3.collections.eclipse.impl.bimap.mutable.HashBiMap;
import com.m3.collections.eclipse.impl.factory.BiMaps;
import com.m3.collections.eclipse.impl.factory.Maps;
import com.m3.collections.eclipse.impl.map.mutable.UnifiedMap;

public class ImmutableHashBiMapInverseTest extends AbstractImmutableBiMapTestCase
{
    @Override
    protected ImmutableBiMap<Integer, String> classUnderTest()
    {
        return BiMaps.immutable.with("1", 1, "2", 2, "3", 3, "4", 4).inverse();
    }

    @Override
    protected ImmutableBiMap<Integer, String> newEmpty()
    {
        return BiMaps.immutable.<String, Integer>empty().inverse();
    }

    @Override
    protected ImmutableBiMap<Integer, String> newWithMap()
    {
        return BiMaps.immutable.withAll(UnifiedMap.newWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4)).inverse();
    }

    @Override
    protected ImmutableBiMap<Integer, String> newWithHashBiMap()
    {
        return BiMaps.immutable.withAll(HashBiMap.newWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4)).inverse();
    }

    @Override
    protected ImmutableBiMap<Integer, String> newWithImmutableMap()
    {
        return BiMaps.immutable.withAll(Maps.immutable.of("1", 1, "2", 2, "3", 3, "4", 4)).inverse();
    }
}
