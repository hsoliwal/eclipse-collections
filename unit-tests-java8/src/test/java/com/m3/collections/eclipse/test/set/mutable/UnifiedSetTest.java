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

import com.m3.collections.eclipse.api.set.MutableSet;
import com.m3.collections.eclipse.impl.set.mutable.UnifiedSet;
import com.m3.collections.eclipse.test.IterableTestCase;

public class UnifiedSetTest implements MutableSetTestCase
{
    @SafeVarargs
    @Override
    public final <T> MutableSet<T> newWith(T... elements)
    {
        MutableSet<T> result = new UnifiedSet<>();
        IterableTestCase.addAllTo(elements, result);
        return result;
    }
}
