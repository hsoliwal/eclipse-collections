/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.test.set.mutable;

import java.util.Set;

import com.m3.collections.eclipse.api.bimap.MutableBiMap;
import com.m3.collections.eclipse.impl.bimap.mutable.HashBiMap;
import com.m3.collections.eclipse.impl.test.junit.Java8Runner;
import com.m3.collections.eclipse.test.set.SetTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Java8Runner.class)
public class HashBiMapKeySetTest implements SetTestCase
{
    @SafeVarargs
    @Override
    public final <T> Set<T> newWith(T... elements)
    {
        MutableBiMap<T, T> result = new HashBiMap<>();
        for (T element : elements)
        {
            if (result.containsKey(element))
            {
                throw new IllegalStateException();
            }
            result.put(element, element);
        }
        return result.keySet();
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void Collection_add()
    {
        // TODO Move up to a keySet view abstraction
        SetTestCase.super.Collection_add();
    }

    @Override
    public boolean allowsDuplicates()
    {
        return false;
    }
}
