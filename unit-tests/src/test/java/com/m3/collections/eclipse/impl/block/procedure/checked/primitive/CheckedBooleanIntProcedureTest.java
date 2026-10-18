/*
 * Copyright (c) 2022 The Bank of New York Mellon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.block.procedure.checked.primitive;

import java.io.IOException;

import com.m3.collections.eclipse.api.block.procedure.primitive.BooleanIntProcedure;
import com.m3.collections.eclipse.api.factory.Lists;
import com.m3.collections.eclipse.api.list.MutableList;
import com.m3.collections.eclipse.api.tuple.primitive.BooleanIntPair;
import com.m3.collections.eclipse.impl.tuple.primitive.PrimitiveTuples;
import org.junit.Assert;
import org.junit.Test;

public class CheckedBooleanIntProcedureTest
{
    @Test
    public void value()
    {
        MutableList<BooleanIntPair> list = Lists.mutable.empty();
        BooleanIntProcedure procedure = new CheckedBooleanIntProcedure()
        {
            @Override
            public void safeValue(boolean item1, int item2)
            {
                list.add(PrimitiveTuples.pair(item1, item2));
            }
        };
        procedure.value(true, 1);
        procedure.value(false, 10);
        Assert.assertEquals(Lists.mutable.of(PrimitiveTuples.pair(true, 1),
                PrimitiveTuples.pair(false, 10)), list);
    }

    @Test
    public void valueWithCheckedException()
    {
        IOException ioException = new IOException("checked exception");
        try
        {
            new CheckedBooleanIntProcedure()
            {
                @Override
                public void safeValue(boolean item1, int item2) throws Exception
                {
                    throw ioException;
                }
            }.value(true, 10);
            Assert.fail("Exception should have been thrown");
        }
        catch (Exception e)
        {
            Assert.assertEquals("Checked exception caught in BooleanIntProcedure", e.getMessage());
            Assert.assertEquals(ioException, e.getCause());
        }
    }

    @Test
    public void valueWithUncheckedException()
    {
        RuntimeException exception = new RuntimeException("Runtime exception");
        try
        {
            new CheckedBooleanIntProcedure()
            {
                @Override
                public void safeValue(boolean item1, int item2)
                {
                    throw exception;
                }
            }.value(true, 10);
            Assert.fail("Exception should have been thrown");
        }
        catch (Exception e)
        {
            Assert.assertEquals(exception, e);
        }
    }
}
