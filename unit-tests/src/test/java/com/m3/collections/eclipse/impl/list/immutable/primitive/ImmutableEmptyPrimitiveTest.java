/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.list.immutable.primitive;

import com.m3.collections.eclipse.impl.factory.primitive.BooleanLists;
import com.m3.collections.eclipse.impl.factory.primitive.ByteLists;
import com.m3.collections.eclipse.impl.factory.primitive.CharLists;
import com.m3.collections.eclipse.impl.factory.primitive.DoubleLists;
import com.m3.collections.eclipse.impl.factory.primitive.FloatLists;
import com.m3.collections.eclipse.impl.factory.primitive.IntLists;
import com.m3.collections.eclipse.impl.factory.primitive.LongLists;
import com.m3.collections.eclipse.impl.factory.primitive.ShortLists;
import com.m3.collections.eclipse.impl.test.Verify;
import org.junit.Test;

/**
 * JUnit test for empty() methods of primitive classes
 */
public class ImmutableEmptyPrimitiveTest
{
    @Test
    public void isEmptyImmutable()
    {
        Verify.assertEmpty(BooleanLists.immutable.empty());
        Verify.assertEmpty(BooleanLists.immutable.of());
        Verify.assertEmpty(BooleanLists.immutable.with());

        Verify.assertEmpty(ByteLists.immutable.empty());
        Verify.assertEmpty(ByteLists.immutable.of());
        Verify.assertEmpty(ByteLists.immutable.with());

        Verify.assertEmpty(CharLists.immutable.empty());
        Verify.assertEmpty(CharLists.immutable.of());
        Verify.assertEmpty(CharLists.immutable.with());

        Verify.assertEmpty(DoubleLists.immutable.empty());
        Verify.assertEmpty(DoubleLists.immutable.of());
        Verify.assertEmpty(DoubleLists.immutable.with());

        Verify.assertEmpty(FloatLists.immutable.empty());
        Verify.assertEmpty(FloatLists.immutable.of());
        Verify.assertEmpty(FloatLists.immutable.with());

        Verify.assertEmpty(IntLists.immutable.empty());
        Verify.assertEmpty(IntLists.immutable.of());
        Verify.assertEmpty(IntLists.immutable.with());

        Verify.assertEmpty(LongLists.immutable.empty());
        Verify.assertEmpty(LongLists.immutable.of());
        Verify.assertEmpty(LongLists.immutable.with());

        Verify.assertEmpty(ShortLists.immutable.empty());
        Verify.assertEmpty(ShortLists.immutable.of());
        Verify.assertEmpty(ShortLists.immutable.with());
    }
}
