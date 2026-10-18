/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.api.partition.bag.sorted;

import com.m3.collections.eclipse.api.bag.sorted.MutableSortedBag;
import com.m3.collections.eclipse.api.partition.bag.PartitionMutableBagIterable;

/**
 * @since 4.2
 */
public interface PartitionMutableSortedBag<T> extends PartitionSortedBag<T>, PartitionMutableBagIterable<T>
{
    @Override
    MutableSortedBag<T> getSelected();

    @Override
    MutableSortedBag<T> getRejected();

    @Override
    PartitionImmutableSortedBag<T> toImmutable();
}
