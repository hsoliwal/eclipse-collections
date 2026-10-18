/*
 * Copyright (c) 2021 The Bank of New York Mellon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.partition.list;

import com.m3.collections.eclipse.api.factory.Lists;
import com.m3.collections.eclipse.api.list.MutableList;
import com.m3.collections.eclipse.api.partition.list.PartitionImmutableList;
import com.m3.collections.eclipse.api.partition.list.PartitionMutableList;
import org.junit.Assert;
import org.junit.Test;

public class PartitionFastListTest
{
    @Test
    public void toImmutable()
    {
        MutableList<Integer> selected = Lists.mutable.of(1, 2, 3);
        MutableList<Integer> rejected = Lists.mutable.of(4, 5, 6);
        PartitionMutableList<Integer> partitionedList = new PartitionFastList<>();
        partitionedList.getSelected().addAll(selected);
        partitionedList.getRejected().addAll(rejected);
        PartitionImmutableList<Integer> immutable = partitionedList.toImmutable();
        Assert.assertEquals(selected, immutable.getSelected());
        Assert.assertEquals(rejected, immutable.getRejected());
    }
}
