/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.partition.stack;

import com.m3.collections.eclipse.api.partition.stack.PartitionImmutableStack;
import com.m3.collections.eclipse.api.stack.ImmutableStack;

public class PartitionImmutableStackImpl<T> implements PartitionImmutableStack<T>
{
    private final ImmutableStack<T> selected;
    private final ImmutableStack<T> rejected;

    public PartitionImmutableStackImpl(PartitionArrayStack<T> partitionArrayStack)
    {
        this.selected = partitionArrayStack.getSelected().toImmutable();
        this.rejected = partitionArrayStack.getRejected().toImmutable();
    }

    @Override
    public ImmutableStack<T> getSelected()
    {
        return this.selected;
    }

    @Override
    public ImmutableStack<T> getRejected()
    {
        return this.rejected;
    }
}
