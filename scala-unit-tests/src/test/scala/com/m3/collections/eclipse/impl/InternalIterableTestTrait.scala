/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl

import com.m3.collections.eclipse.api.InternalIterable
import com.m3.collections.eclipse.impl.Prelude._
import com.m3.collections.eclipse.impl.block.procedure.CollectionAddProcedure
import com.m3.collections.eclipse.impl.list.mutable.FastList
import com.m3.collections.eclipse.impl.test.Verify
import org.junit.{Assert, Test}

trait InternalIterableTestTrait extends IterableTestTrait
{
    val classUnderTest: InternalIterable[String]

    @Test
    def forEach
    {
        val result = FastList.newList[String]

        this.classUnderTest.forEach(CollectionAddProcedure.on(result))

        Verify.assertSize(3, result)
        Verify.assertContainsAll(result, "1", "2", "3")
    }

    @Test
    def forEachWithIndex
    {
        val result = FastList.newList[String]
        val indices = FastList.newList[Integer]

        var count = 0
        this.classUnderTest.forEachWithIndex
        {
            (each: String, index: Int) =>
                Assert.assertEquals(index, count)
                count += 1
                result.add(each)
                indices.add(index)
                ()
        }

        Verify.assertSize(3, result)
        Verify.assertContainsAll(result, "1", "2", "3")

        Verify.assertSize(3, indices)
        Verify.assertContainsAll(indices, Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2))
    }

    @Test
    def forEachWith
    {
        val result = FastList.newList[String]

        this.classUnderTest.forEachWith(
            (each: String, parameter: String) =>
            {
                result.add(each + parameter)
                ()
            },
            "!")

        Verify.assertSize(3, result)
        Verify.assertContainsAll(result, "1!", "2!", "3!")
    }
}
