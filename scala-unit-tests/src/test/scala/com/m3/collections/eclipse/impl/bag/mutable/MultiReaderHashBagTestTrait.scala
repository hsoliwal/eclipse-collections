/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.bag.mutable

import com.m3.collections.eclipse.api.bag.MutableBag
import com.m3.collections.eclipse.impl.MultiReaderThreadSafetyTestTrait
import com.m3.collections.eclipse.impl.Prelude._

trait MultiReaderHashBagTestTrait extends MultiReaderThreadSafetyTestTrait
{
    val classUnderTest: MultiReaderHashBag[Int]

    def createReadLockHolderThread(gate: Gate): Thread =
        spawn
        {
            this.classUnderTest.withReadLockAndDelegate((_: MutableBag[_]) => sleep(gate))
        }

    def createWriteLockHolderThread(gate: Gate): Thread =
        spawn
        {
            this.classUnderTest.withWriteLockAndDelegate((_: MutableBag[_]) => sleep(gate))
        }
}
