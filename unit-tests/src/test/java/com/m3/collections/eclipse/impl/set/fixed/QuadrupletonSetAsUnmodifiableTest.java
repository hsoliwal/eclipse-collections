/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.set.fixed;

import com.m3.collections.eclipse.api.block.function.primitive.CharFunction;
import com.m3.collections.eclipse.api.collection.MutableCollection;
import com.m3.collections.eclipse.impl.collection.mutable.UnmodifiableMutableCollectionTestCase;
import com.m3.collections.eclipse.impl.factory.Sets;
import com.m3.collections.eclipse.impl.test.Verify;
import org.junit.Test;

public class QuadrupletonSetAsUnmodifiableTest extends UnmodifiableMutableCollectionTestCase<String>
{
    @Override
    protected MutableCollection<String> getCollection()
    {
        return Sets.fixedSize.of("1", "2", "3", "4").asUnmodifiable();
    }

    @Override
    @Test
    public void collectBoolean()
    {
        Verify.assertSize(1, this.getCollection().collectBoolean(Boolean::parseBoolean));
    }

    @Override
    @Test
    public void collectByte()
    {
        Verify.assertSize(this.getCollection().size(), this.getCollection().collectByte(Byte::parseByte));
    }

    @Override
    @Test
    public void collectChar()
    {
        Verify.assertSize(this.getCollection().size(), this.getCollection().collectChar((CharFunction<String>) string -> string.charAt(0)));
    }

    @Override
    @Test
    public void collectDouble()
    {
        Verify.assertSize(this.getCollection().size(), this.getCollection().collectDouble(Double::parseDouble));
    }

    @Override
    @Test
    public void collectFloat()
    {
        Verify.assertSize(this.getCollection().size(), this.getCollection().collectFloat(Float::parseFloat));
    }

    @Override
    @Test
    public void collectInt()
    {
        Verify.assertSize(this.getCollection().size(), this.getCollection().collectInt(Integer::parseInt));
    }

    @Override
    @Test
    public void collectLong()
    {
        Verify.assertSize(this.getCollection().size(), this.getCollection().collectLong(Long::parseLong));
    }

    @Override
    @Test
    public void collectShort()
    {
        Verify.assertSize(this.getCollection().size(), this.getCollection().collectShort(Short::parseShort));
    }
}
