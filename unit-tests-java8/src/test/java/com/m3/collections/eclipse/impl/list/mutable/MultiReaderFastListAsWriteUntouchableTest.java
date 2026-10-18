/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.list.mutable;

import com.m3.collections.eclipse.api.list.MutableList;
import com.m3.collections.eclipse.test.IterableTestCase;
import com.m3.collections.eclipse.test.list.mutable.MutableListTestCase;

public class MultiReaderFastListAsWriteUntouchableTest implements MutableListTestCase
{
    @SafeVarargs
    @Override
    public final <T> MutableList<T> newWith(T... elements)
    {
        MultiReaderFastList<T> result = MultiReaderFastList.newList();
        IterableTestCase.addAllTo(elements, result);
        return result.asWriteUntouchable();
    }

    @Override
    public boolean allowsSerialization()
    {
        return false;
    }
}
