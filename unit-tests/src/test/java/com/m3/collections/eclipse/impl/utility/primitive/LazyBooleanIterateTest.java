/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.utility.primitive;

import com.m3.collections.eclipse.api.BooleanIterable;
import com.m3.collections.eclipse.api.LazyBooleanIterable;
import com.m3.collections.eclipse.api.block.procedure.primitive.BooleanProcedure;
import com.m3.collections.eclipse.api.list.primitive.MutableBooleanList;
import com.m3.collections.eclipse.impl.factory.primitive.BooleanLists;
import org.junit.Assert;
import org.junit.Test;

public class LazyBooleanIterateTest
{
    private final BooleanIterable iterable = BooleanLists.mutable.with(true, false);

    @Test
    public void adapt()
    {
        Assert.assertEquals(this.iterable, LazyBooleanIterate.adapt(this.iterable).toList());
    }

    @Test
    public void collectIf()
    {
        Assert.assertEquals(this.iterable.collect(each -> each), LazyBooleanIterate.collectIf(this.iterable, each -> true, each -> each).toList());
    }

    @Test
    public void empty()
    {
        Assert.assertTrue(LazyBooleanIterate.empty().isEmpty());
    }

    @Test
    public void tap()
    {
        MutableBooleanList list = BooleanLists.mutable.empty();
        LazyBooleanIterable booleanIterable = LazyBooleanIterate.tap(this.iterable, (BooleanProcedure) list::add);
        Assert.assertEquals(this.iterable, BooleanLists.mutable.ofAll(booleanIterable));
        Assert.assertEquals(this.iterable, list);
    }
}
