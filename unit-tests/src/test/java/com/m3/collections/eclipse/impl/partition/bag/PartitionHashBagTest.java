/*
 * Copyright (c) 2022 The Bank of New York Mellon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.partition.bag;

import com.m3.collections.eclipse.api.bag.MutableBag;
import com.m3.collections.eclipse.api.factory.Bags;
import com.m3.collections.eclipse.api.partition.bag.PartitionImmutableBag;
import com.m3.collections.eclipse.api.partition.bag.PartitionMutableBag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PartitionHashBagTest
{
    @Test
    public void toImmutable()
    {
        PartitionMutableBag<Integer> partitionMutableBag = new PartitionHashBag<>();
        MutableBag<Integer> selected = Bags.mutable.of(1, 2, 3);
        MutableBag<Integer> rejected = Bags.mutable.of(4, 5, 6);
        partitionMutableBag.getSelected().withAll(selected);
        partitionMutableBag.getRejected().withAll(rejected);

        PartitionImmutableBag<Integer> partitionImmutableBag = partitionMutableBag.toImmutable();
        assertEquals(selected, partitionImmutableBag.getSelected());
        assertEquals(rejected, partitionImmutableBag.getRejected());
    }
}
