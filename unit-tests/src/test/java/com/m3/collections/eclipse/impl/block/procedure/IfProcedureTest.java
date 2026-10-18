/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.block.procedure;

import com.m3.collections.eclipse.api.block.procedure.Procedure;
import com.m3.collections.eclipse.api.factory.Lists;
import com.m3.collections.eclipse.api.list.MutableList;
import com.m3.collections.eclipse.impl.list.mutable.FastList;
import com.m3.collections.eclipse.impl.test.Verify;
import com.m3.collections.eclipse.impl.utility.Iterate;
import com.m3.collections.eclipse.impl.utility.StringIterate;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IfProcedureTest
{
    private static final Logger LOGGER = LoggerFactory.getLogger(IfProcedureTest.class);

    @Test
    public void procedure()
    {
        MutableList<String> list1 = Lists.mutable.of();
        MutableList<String> list2 = Lists.mutable.of();
        Procedure<String> ifProcedure = new IfProcedure<>("1"::equals, list1::add, list2::add);
        LOGGER.info("{}", ifProcedure);
        MutableList<String> list = FastList.newListWith("1", "2");
        Iterate.forEach(list, ifProcedure);
        assertEquals(1, list1.size());
        Verify.assertContains("1", list1);
        assertEquals(1, list2.size());
        Verify.assertContains("2", list2);
    }

    @Test
    public void toStringTest()
    {
        MutableList<String> list1 = Lists.mutable.of();
        MutableList<String> list2 = Lists.mutable.of();
        Procedure<String> ifProcedure = new IfProcedure<>("1"::equals, list1::add, list2::add);
        String s = ifProcedure.toString();
        assertNotNull(s);
        assertTrue(StringIterate.notEmptyOrWhitespace(s));
    }
}
