/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.partition.set.strategy;

import com.m3.collections.eclipse.api.block.HashingStrategy;
import com.m3.collections.eclipse.api.partition.set.PartitionImmutableSet;
import com.m3.collections.eclipse.api.partition.set.PartitionMutableSet;
import com.m3.collections.eclipse.api.set.MutableSet;
import com.m3.collections.eclipse.impl.partition.set.PartitionImmutableSetImpl;
import com.m3.collections.eclipse.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy;

public class PartitionUnifiedSetWithHashingStrategy<T>
        implements PartitionMutableSet<T>
{
    private final MutableSet<T> selected;
    private final MutableSet<T> rejected;

    public PartitionUnifiedSetWithHashingStrategy(HashingStrategy<? super T> hashingStrategy)
    {
        this.selected = UnifiedSetWithHashingStrategy.newSet(hashingStrategy);
        this.rejected = UnifiedSetWithHashingStrategy.newSet(hashingStrategy);
    }

    @Override
    public MutableSet<T> getSelected()
    {
        return this.selected;
    }

    @Override
    public MutableSet<T> getRejected()
    {
        return this.rejected;
    }

    @Override
    public PartitionImmutableSet<T> toImmutable()
    {
        return new PartitionImmutableSetImpl<>(this);
    }
}
