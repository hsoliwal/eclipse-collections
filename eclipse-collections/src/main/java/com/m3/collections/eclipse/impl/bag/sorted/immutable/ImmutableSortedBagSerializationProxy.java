/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.bag.sorted.immutable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Comparator;

import com.m3.collections.eclipse.api.bag.sorted.ImmutableSortedBag;
import com.m3.collections.eclipse.api.bag.sorted.MutableSortedBag;
import com.m3.collections.eclipse.impl.bag.sorted.mutable.TreeBag;
import com.m3.collections.eclipse.impl.block.procedure.checked.CheckedObjectIntProcedure;

class ImmutableSortedBagSerializationProxy<T> implements Externalizable
{
    private static final long serialVersionUID = 1L;

    private ImmutableSortedBag<T> bag;

    @SuppressWarnings("UnusedDeclaration")
    public ImmutableSortedBagSerializationProxy()
    {
        // Empty constructor for Externalizable class
    }

    ImmutableSortedBagSerializationProxy(ImmutableSortedBag<T> bag)
    {
        this.bag = bag;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeInt(this.bag.sizeDistinct());
        out.writeObject(this.bag.comparator());
        try
        {
            this.bag.forEachWithOccurrences(new CheckedObjectIntProcedure<T>()
            {
                public void safeValue(T object, int index) throws IOException
                {
                    out.writeObject(object);
                    out.writeInt(index);
                }
            });
        }
        catch (RuntimeException e)
        {
            if (e.getCause() instanceof IOException)
            {
                throw (IOException) e.getCause();
            }
            throw e;
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        int size = in.readInt();
        Comparator<T> comparator = (Comparator<T>) in.readObject();
        MutableSortedBag<T> deserializedBag = new TreeBag<>(comparator);

        for (int i = 0; i < size; i++)
        {
            deserializedBag.addOccurrences((T) in.readObject(), in.readInt());
        }

        this.bag = deserializedBag.toImmutable();
    }

    protected Object readResolve()
    {
        return this.bag.toImmutable();
    }
}
