/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.list.mutable.primitive;

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
public class MutableEmptyPrimitiveTest
{
    @Test
    public void isEmptyMutable()
    {
        Verify.assertEmpty(BooleanLists.mutable.empty());
        Verify.assertEmpty(BooleanLists.mutable.of());
        Verify.assertEmpty(BooleanLists.mutable.with());

        Verify.assertEmpty(ByteLists.mutable.empty());
        Verify.assertEmpty(ByteLists.mutable.of());
        Verify.assertEmpty(ByteLists.mutable.with());

        Verify.assertEmpty(CharLists.mutable.empty());
        Verify.assertEmpty(CharLists.mutable.of());
        Verify.assertEmpty(CharLists.mutable.with());

        Verify.assertEmpty(DoubleLists.mutable.empty());
        Verify.assertEmpty(DoubleLists.mutable.of());
        Verify.assertEmpty(DoubleLists.mutable.with());

        Verify.assertEmpty(FloatLists.mutable.empty());
        Verify.assertEmpty(FloatLists.mutable.of());
        Verify.assertEmpty(FloatLists.mutable.with());

        Verify.assertEmpty(IntLists.mutable.empty());
        Verify.assertEmpty(IntLists.mutable.of());
        Verify.assertEmpty(IntLists.mutable.with());

        Verify.assertEmpty(LongLists.mutable.empty());
        Verify.assertEmpty(LongLists.mutable.of());
        Verify.assertEmpty(LongLists.mutable.with());

        Verify.assertEmpty(ShortLists.mutable.empty());
        Verify.assertEmpty(ShortLists.mutable.of());
        Verify.assertEmpty(ShortLists.mutable.with());
    }
}
