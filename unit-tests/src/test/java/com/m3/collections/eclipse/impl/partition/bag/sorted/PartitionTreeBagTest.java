/*
 * Copyright (c) 2022 The Bank of New York Mellon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.partition.bag.sorted;

import com.m3.collections.eclipse.api.bag.sorted.MutableSortedBag;
import com.m3.collections.eclipse.api.factory.SortedBags;
import com.m3.collections.eclipse.api.partition.bag.sorted.PartitionImmutableSortedBag;
import com.m3.collections.eclipse.api.partition.bag.sorted.PartitionMutableSortedBag;
import com.m3.collections.eclipse.impl.block.factory.Comparators;
import org.junit.Assert;
import org.junit.Test;

public class PartitionTreeBagTest
{
    @Test
    public void toImmutable()
    {
        PartitionMutableSortedBag<Integer> partitionTreeBag = new PartitionTreeBag<>(Comparators.naturalOrder());
        MutableSortedBag<Integer> selected = SortedBags.mutable.of(1, 2, 3);
        MutableSortedBag<Integer> rejected = SortedBags.mutable.of(4, 5, 6);
        partitionTreeBag.getSelected().addAll(selected);
        partitionTreeBag.getRejected().addAll(rejected);
        PartitionImmutableSortedBag<Integer> immutableSortedBag = partitionTreeBag.toImmutable();
        Assert.assertEquals(selected, immutableSortedBag.getSelected());
        Assert.assertEquals(rejected, immutableSortedBag.getRejected());
    }
}
