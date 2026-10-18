/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.map.mutable;

import com.m3.collections.eclipse.api.list.MutableList;
import com.m3.collections.eclipse.api.map.MutableMap;
import com.m3.collections.eclipse.impl.block.factory.Functions;
import com.m3.collections.eclipse.impl.list.mutable.FastList;
import com.m3.collections.eclipse.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * Abstract JUnit TestCase for {@link MutableMap}s.
 */
public abstract class MutableMapTestCase extends MutableMapIterableTestCase
{
    @Override
    protected abstract <K, V> MutableMap<K, V> newMap();

    @Override
    protected abstract <K, V> MutableMap<K, V> newMapWithKeyValue(K key, V value);

    @Override
    protected abstract <K, V> MutableMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2);

    @Override
    protected abstract <K, V> MutableMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3);

    @Override
    protected abstract <K, V> MutableMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4);

    @Test
    public void collectKeysAndValues()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        MutableList<Integer> toAdd = FastList.newListWith(2, 3);
        map.collectKeysAndValues(toAdd, Functions.getIntegerPassThru(), String::valueOf);
        Verify.assertSize(3, map);
        Verify.assertContainsAllKeyValues(map, 1, "1", 2, "2", 3, "3");
    }

    @Test
    public void testClone()
    {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "Two");
        MutableMap<Integer, String> clone = map.clone();
        assertNotSame(map, clone);
        Verify.assertEqualsAndHashCode(map, clone);
    }
}
