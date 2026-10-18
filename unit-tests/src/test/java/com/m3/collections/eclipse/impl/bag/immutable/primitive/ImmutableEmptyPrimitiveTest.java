/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.bag.immutable.primitive;

import com.m3.collections.eclipse.impl.factory.primitive.BooleanBags;
import com.m3.collections.eclipse.impl.factory.primitive.ByteBags;
import com.m3.collections.eclipse.impl.factory.primitive.CharBags;
import com.m3.collections.eclipse.impl.factory.primitive.DoubleBags;
import com.m3.collections.eclipse.impl.factory.primitive.FloatBags;
import com.m3.collections.eclipse.impl.factory.primitive.IntBags;
import com.m3.collections.eclipse.impl.factory.primitive.LongBags;
import com.m3.collections.eclipse.impl.factory.primitive.ShortBags;
import com.m3.collections.eclipse.impl.test.Verify;
import org.junit.jupiter.api.Test;

/**
 * JUnit test for empty() methods of primitive classes
 */
public class ImmutableEmptyPrimitiveTest
{
    @Test
    public void isEmptyImmutable()
    {
        Verify.assertEmpty(BooleanBags.immutable.empty());
        Verify.assertEmpty(BooleanBags.immutable.of());
        Verify.assertEmpty(BooleanBags.immutable.with());

        Verify.assertEmpty(ByteBags.immutable.empty());
        Verify.assertEmpty(ByteBags.immutable.of());
        Verify.assertEmpty(ByteBags.immutable.with());

        Verify.assertEmpty(CharBags.immutable.empty());
        Verify.assertEmpty(CharBags.immutable.of());
        Verify.assertEmpty(CharBags.immutable.with());

        Verify.assertEmpty(DoubleBags.immutable.empty());
        Verify.assertEmpty(DoubleBags.immutable.of());
        Verify.assertEmpty(DoubleBags.immutable.with());

        Verify.assertEmpty(FloatBags.immutable.empty());
        Verify.assertEmpty(FloatBags.immutable.of());
        Verify.assertEmpty(FloatBags.immutable.with());

        Verify.assertEmpty(IntBags.immutable.empty());
        Verify.assertEmpty(IntBags.immutable.of());
        Verify.assertEmpty(IntBags.immutable.with());

        Verify.assertEmpty(LongBags.immutable.empty());
        Verify.assertEmpty(LongBags.immutable.of());
        Verify.assertEmpty(LongBags.immutable.with());

        Verify.assertEmpty(ShortBags.immutable.empty());
        Verify.assertEmpty(ShortBags.immutable.of());
        Verify.assertEmpty(ShortBags.immutable.with());
    }
}
