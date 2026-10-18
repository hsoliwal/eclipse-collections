/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.multimap;

import com.m3.collections.eclipse.api.multimap.ImmutableMultimap;
import com.m3.collections.eclipse.api.multimap.Multimap;
import com.m3.collections.eclipse.api.multimap.MutableMultimap;
import org.junit.jupiter.api.BeforeEach;

public abstract class ImmutableMultimapSerializationTestCase extends MultimapSerializationTestCase
{
    private ImmutableMultimap<String, String> undertest;

    @BeforeEach
    public void buildUnderTest()
    {
        MutableMultimap<String, String> map = this.createEmpty();
        map.put("A", "A");
        map.put("A", "B");
        map.put("A", "B");
        map.put("B", "A");
        this.undertest = map.toImmutable();
    }

    @Override
    protected Multimap<String, String> getMultimapUnderTest()
    {
        return this.undertest;
    }
}
