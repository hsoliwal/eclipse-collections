/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.map.mutable.primitive;

import com.m3.collections.eclipse.api.collection.primitive.MutableBooleanCollection;
import com.m3.collections.eclipse.api.list.MutableList;
import com.m3.collections.eclipse.impl.list.mutable.FastList;

/**
 * JUnit test for {@link ObjectBooleanHashMap#values()}.
 */
public class ObjectBooleanHashMapValuesTest extends ObjectBooleanHashMapValuesTestCase
{
    @Override
    protected MutableBooleanCollection classUnderTest()
    {
        return ObjectBooleanHashMap.newWithKeysValues(1, true, 2, false, 3, true).values();
    }

    @Override
    protected MutableBooleanCollection newWith(boolean... elements)
    {
        ObjectBooleanHashMap<Integer> map = new ObjectBooleanHashMap<>();
        for (int i = 0; i < elements.length; i++)
        {
            map.put(i, elements[i]);
        }
        return map.values();
    }

    @Override
    protected MutableBooleanCollection newMutableCollectionWith(boolean... elements)
    {
        return this.newWith(elements);
    }

    @Override
    protected MutableList<Object> newObjectCollectionWith(Object... elements)
    {
        return FastList.newListWith(elements);
    }
}
