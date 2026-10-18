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

import com.m3.collections.eclipse.api.LazyIterable;
import com.m3.collections.eclipse.api.list.MutableList;
import com.m3.collections.eclipse.impl.lazy.DropWhileIterable;
import com.m3.collections.eclipse.impl.test.junit.Java8Runner;
import com.m3.collections.eclipse.test.IterableTestCase;
import com.m3.collections.eclipse.test.LazyNoIteratorTestCase;
import com.m3.collections.eclipse.test.list.mutable.FastListNoIterator;
import org.junit.runner.RunWith;

@RunWith(Java8Runner.class)
public class DropWhileIterableNoIteratorTest implements LazyNoIteratorTestCase
{
    @Override
    public <T> LazyIterable<T> newWith(T... elements)
    {
        MutableList<T> list = new FastListNoIterator<>();
        T object = (T) new Object();
        list.add(object);
        IterableTestCase.addAllTo(elements, list);
        return new DropWhileIterable<>(list, each -> each == object);
    }
}
