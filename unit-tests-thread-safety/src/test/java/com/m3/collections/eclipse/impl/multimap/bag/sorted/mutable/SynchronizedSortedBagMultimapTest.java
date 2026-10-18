/*
 * Copyright (c) 2026 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.multimap.bag.sorted.mutable;

import com.m3.collections.eclipse.api.multimap.sortedbag.MutableSortedBagMultimap;
import com.m3.collections.eclipse.impl.multimap.SynchronizedMultimapTestTrait;
import com.m3.collections.eclipse.impl.tuple.Tuples;

public class SynchronizedSortedBagMultimapTest
        implements SynchronizedMultimapTestTrait
{
    private final MutableSortedBagMultimap<String, String> classUnderTest = SynchronizedSortedBagMultimap.of(TreeBagMultimap.newMultimap(
            Tuples.pair("One", "1"),
            Tuples.pair("Two", "2"),
            Tuples.pair("Three", "3")));

    @Override
    public MutableSortedBagMultimap<String, String> getClassUnderTest()
    {
        return this.classUnderTest;
    }
}
