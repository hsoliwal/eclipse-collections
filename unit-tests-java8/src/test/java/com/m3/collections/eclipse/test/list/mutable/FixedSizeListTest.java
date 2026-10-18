/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.test.list.mutable;

import com.m3.collections.eclipse.api.factory.Lists;
import com.m3.collections.eclipse.api.list.MutableList;
import com.m3.collections.eclipse.impl.test.junit.Java8Runner;
import com.m3.collections.eclipse.test.list.FixedSizeListTestCase;
import org.junit.runner.RunWith;

@RunWith(Java8Runner.class)
public class FixedSizeListTest implements FixedSizeListTestCase
{
    @SafeVarargs
    @Override
    public final <T> MutableList<T> newWith(T... elements)
    {
        return Lists.fixedSize.with(elements);
    }
}
