/*
 * Copyright (c) 2022 The Bank of New York Mellon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.partition.set.strategy;

import com.m3.collections.eclipse.api.factory.Sets;
import com.m3.collections.eclipse.api.partition.set.PartitionImmutableSet;
import com.m3.collections.eclipse.api.partition.set.PartitionMutableSet;
import com.m3.collections.eclipse.api.set.MutableSet;
import com.m3.collections.eclipse.impl.block.factory.HashingStrategies;
import org.junit.Assert;
import org.junit.Test;

public class PartitionUnifiedSetWithHashingStrategyTest
{
    @Test
    public void toImmutable()
    {
        PartitionMutableSet<Integer> partitionMutableSet =
                new PartitionUnifiedSetWithHashingStrategy<>(HashingStrategies.defaultStrategy());

        MutableSet<Integer> selected = Sets.mutable.of(1, 2, 3);
        MutableSet<Integer> rejected = Sets.mutable.of(4, 5, 6);
        partitionMutableSet.getSelected().addAll(selected);
        partitionMutableSet.getRejected().addAll(rejected);

        PartitionImmutableSet<Integer> partitionImmutableSet = partitionMutableSet.toImmutable();
        Assert.assertEquals(selected, partitionImmutableSet.getSelected());
        Assert.assertEquals(rejected, partitionImmutableSet.getRejected());
    }
}
