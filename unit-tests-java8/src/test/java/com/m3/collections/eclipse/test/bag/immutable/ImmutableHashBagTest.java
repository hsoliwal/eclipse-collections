/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.test.bag.immutable;

import com.m3.collections.eclipse.api.bag.ImmutableBag;
import com.m3.collections.eclipse.impl.bag.immutable.ImmutableHashBag;
import com.m3.collections.eclipse.impl.bag.mutable.HashBag;
import com.m3.collections.eclipse.impl.test.junit.Java8Runner;
import org.junit.runner.RunWith;

@RunWith(Java8Runner.class)
public class ImmutableHashBagTest implements ImmutableBagTestCase
{
    @SafeVarargs
    @Override
    public final <T> ImmutableBag<T> newWith(T... elements)
    {
        return new ImmutableHashBag<>(HashBag.newBagWith(elements));
    }
}
