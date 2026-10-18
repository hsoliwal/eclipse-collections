/*
 * Copyright (c) 2021 The Bank of New York Mellon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.block.procedure;

import com.m3.collections.eclipse.api.factory.Lists;
import com.m3.collections.eclipse.impl.block.factory.Functions;
import com.m3.collections.eclipse.impl.list.Interval;
import org.junit.Assert;
import org.junit.Test;

public class FlatCollectProcedureTest
{
    @Test
    public void value()
    {
        FlatCollectProcedure<Integer, Integer> procedure =
                new FlatCollectProcedure<>(each -> Interval.from(0).to(each).collect(Functions.getPassThru()),
                        Lists.mutable.empty());
        procedure.value(1);
        procedure.value(2);
        procedure.value(3);
        Assert.assertEquals(Lists.mutable.of(0, 1, 0, 1, 2, 0, 1, 2, 3), procedure.getCollection());
    }
}
