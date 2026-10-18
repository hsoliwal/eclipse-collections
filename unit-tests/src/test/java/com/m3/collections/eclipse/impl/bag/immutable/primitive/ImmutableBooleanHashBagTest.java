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

import com.m3.collections.eclipse.api.bag.primitive.ImmutableBooleanBag;
import com.m3.collections.eclipse.api.set.primitive.ImmutableBooleanSet;
import com.m3.collections.eclipse.impl.factory.primitive.BooleanSets;
import org.junit.Assert;
import org.junit.Test;

public class ImmutableBooleanHashBagTest extends AbstractImmutableBooleanBagTestCase
{
    @Override
    protected ImmutableBooleanBag classUnderTest()
    {
        return ImmutableBooleanHashBag.newBagWith(true, false, true);
    }

    @Override
    @Test
    public void selectUnique()
    {
        super.selectUnique();

        ImmutableBooleanBag bag = this.classUnderTest();
        ImmutableBooleanSet expected = BooleanSets.immutable.with(false);
        ImmutableBooleanSet actual = bag.selectUnique();
        Assert.assertEquals(expected, actual);
    }
}
