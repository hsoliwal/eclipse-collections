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

import com.m3.collections.eclipse.test.UnmodifiableMutableCollectionTestCase;
import com.m3.collections.eclipse.test.set.UnmodifiableSetTestCase;
import org.junit.jupiter.api.Test;

public interface UnmodifiableMutableSetTestCase extends UnmodifiableMutableCollectionTestCase, UnmodifiableSetTestCase, MutableSetTestCase
{
    @Override
    default OrderingType getOrderingType()
    {
        return MutableSetTestCase.super.getOrderingType();
    }

    @Override
    default boolean allowsRemove()
    {
        return false;
    }

    @Override
    @Test
    default void Iterable_remove()
    {
        UnmodifiableSetTestCase.super.Iterable_remove();
    }
}
