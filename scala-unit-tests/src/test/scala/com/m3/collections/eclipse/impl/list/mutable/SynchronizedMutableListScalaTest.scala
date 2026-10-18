/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.list.mutable

import com.m3.collections.eclipse.impl.Prelude._
import com.m3.collections.eclipse.impl.collection.mutable.SynchronizedMutableCollectionTestTrait

class SynchronizedMutableListScalaTest extends SynchronizedMutableCollectionTestTrait
{
    import org.junit.Test

    val classUnderTest = FastList.newListWith[String]("1", "2", "3").asSynchronized

    @Test
    override def equals_synchronized
    {
        this.assertSynchronized
        {
            this.classUnderTest.equals(null)
        }
    }

    @Test
    def sort_synchronized
    {
        this.assertSynchronized
        {
            this.classUnderTest.sort(null)
        }
    }

    @Test
    def sortThis_synchronized
    {
        this.assertSynchronized
        {
            this.classUnderTest.sortThis();
        }
    }

    @Test
    def sortThisWithComparator_synchronized
    {
        this.assertSynchronized
        {
            this.classUnderTest.sortThis(null);
        }
    }

    @Test
    override def hashCode_synchronized
    {
        this.assertSynchronized
        {
            this.classUnderTest.hashCode
        }
    }

    @Test
    def asReversed_synchronized
    {
        this.assertSynchronized
        {
            this.classUnderTest.asReversed()
        }
        val reverseIterable = this.classUnderTest.asReversed()
        this.assertSynchronized
        {
            reverseIterable.forEach{_: String => ()}
        }
    }
}
