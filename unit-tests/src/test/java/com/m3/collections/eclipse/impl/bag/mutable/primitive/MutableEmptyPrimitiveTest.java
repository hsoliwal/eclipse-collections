/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.bag.mutable.primitive;

import com.m3.collections.eclipse.impl.factory.primitive.BooleanBags;
import com.m3.collections.eclipse.impl.factory.primitive.ByteBags;
import com.m3.collections.eclipse.impl.factory.primitive.CharBags;
import com.m3.collections.eclipse.impl.factory.primitive.DoubleBags;
import com.m3.collections.eclipse.impl.factory.primitive.FloatBags;
import com.m3.collections.eclipse.impl.factory.primitive.IntBags;
import com.m3.collections.eclipse.impl.factory.primitive.LongBags;
import com.m3.collections.eclipse.impl.factory.primitive.ShortBags;
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
        Verify.assertEmpty(BooleanBags.mutable.empty());
        Verify.assertEmpty(BooleanBags.mutable.of());
        Verify.assertEmpty(BooleanBags.mutable.with());

        Verify.assertEmpty(ByteBags.mutable.empty());
        Verify.assertEmpty(ByteBags.mutable.of());
        Verify.assertEmpty(ByteBags.mutable.with());

        Verify.assertEmpty(CharBags.mutable.empty());
        Verify.assertEmpty(CharBags.mutable.of());
        Verify.assertEmpty(CharBags.mutable.with());

        Verify.assertEmpty(DoubleBags.mutable.empty());
        Verify.assertEmpty(DoubleBags.mutable.of());
        Verify.assertEmpty(DoubleBags.mutable.with());

        Verify.assertEmpty(FloatBags.mutable.empty());
        Verify.assertEmpty(FloatBags.mutable.of());
        Verify.assertEmpty(FloatBags.mutable.with());

        Verify.assertEmpty(IntBags.mutable.empty());
        Verify.assertEmpty(IntBags.mutable.of());
        Verify.assertEmpty(IntBags.mutable.with());

        Verify.assertEmpty(LongBags.mutable.empty());
        Verify.assertEmpty(LongBags.mutable.of());
        Verify.assertEmpty(LongBags.mutable.with());

        Verify.assertEmpty(ShortBags.mutable.empty());
        Verify.assertEmpty(ShortBags.mutable.of());
        Verify.assertEmpty(ShortBags.mutable.with());
    }
}
