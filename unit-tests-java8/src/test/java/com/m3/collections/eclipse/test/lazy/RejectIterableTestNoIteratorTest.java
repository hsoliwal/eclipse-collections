/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.test.lazy;

import java.util.Iterator;

import com.m3.collections.eclipse.api.LazyIterable;
import com.m3.collections.eclipse.impl.lazy.RejectIterable;
import com.m3.collections.eclipse.test.LazyNoIteratorTestCase;
import com.m3.collections.eclipse.test.list.mutable.FastListNoIterator;

public class RejectIterableTestNoIteratorTest implements LazyNoIteratorTestCase
{
    @Override
    public <T> LazyIterable<T> newWith(T... elements)
    {
        return new RejectIterable<>(new FastListNoIterator<T>().with(elements), ignored -> false)
        {
            @Override
            public Iterator<T> iterator()
            {
                throw new AssertionError("No iteration patterns should delegate to iterator()");
            }
        };
    }
}
