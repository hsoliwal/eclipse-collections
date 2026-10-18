/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.test;

import com.m3.collections.eclipse.api.factory.Lists;
import com.m3.collections.eclipse.api.list.ListIterable;
import com.m3.collections.eclipse.api.list.MutableList;
import com.m3.collections.eclipse.test.list.TransformsToListTrait;
import org.junit.Test;

public interface LazyNoIteratorTestCase extends NoIteratorTestCase, RichIterableWithDuplicatesTestCase, TransformsToListTrait
{
    @Override
    default <T> ListIterable<T> getExpectedFiltered(T... elements)
    {
        return Lists.immutable.with(elements);
    }

    @Override
    default <T> MutableList<T> newMutableForFilter(T... elements)
    {
        return Lists.mutable.with(elements);
    }

    @Override
    default <T> ListIterable<T> getExpectedTransformed(T... elements)
    {
        return Lists.immutable.with(elements);
    }

    @Override
    @Test
    default void Object_PostSerializedEqualsAndHashCode()
    {
        // Not applicable
    }

    @Override
    @Test
    default void Object_equalsAndHashCode()
    {
        // Not applicable
    }
}
