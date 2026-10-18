/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.block.factory;

import com.m3.collections.eclipse.api.RichIterable;
import com.m3.collections.eclipse.api.block.function.Function;
import com.m3.collections.eclipse.api.multimap.list.MutableListMultimap;
import com.m3.collections.eclipse.impl.list.mutable.FastList;
import com.m3.collections.eclipse.impl.multimap.list.FastListMultimap;
import com.m3.collections.eclipse.impl.test.Verify;
import org.junit.Assert;
import org.junit.Test;

public class MultimapFunctionsTest
{
    @Test
    public void get()
    {
        MutableListMultimap<String, String> multimap = FastListMultimap.newMultimap();
        multimap.putAll("One", FastList.newListWith("O", "N", "E"));
        multimap.putAll("Two", FastList.newListWith("T", "W", "O"));
        multimap.putAll("Three", FastList.newListWith("T", "H", "R", "E", "E"));

        Function<String, RichIterable<String>> getFunction = MultimapFunctions.get(multimap);

        Assert.assertEquals(
                FastList.newListWith(
                        FastList.newListWith("O", "N", "E"),
                        FastList.newListWith("T", "W", "O"),
                        FastList.newListWith("T", "H", "R", "E", "E")),
                FastList.newListWith("One", "Two", "Three").collect(getFunction));
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(MultimapFunctions.class);
    }
}
