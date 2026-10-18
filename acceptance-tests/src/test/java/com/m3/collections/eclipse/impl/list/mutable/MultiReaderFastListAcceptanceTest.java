/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.list.mutable;

import com.m3.collections.eclipse.api.list.MutableList;
import com.m3.collections.eclipse.impl.list.Interval;
import com.m3.collections.eclipse.impl.test.Verify;
import org.junit.Test;

/**
 * JUnit test for {@link MultiReaderFastList}.
 */
public class MultiReaderFastListAcceptanceTest
{
    @Test
    public void sortThisOnListWithMoreThan9Elements()
    {
        MutableList<Integer> integers = MultiReaderFastList.newList(Interval.toReverseList(1, 10000));
        Verify.assertStartsWith(integers.sortThis(), 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Verify.assertEndsWith(integers, 9997, 9998, 9999, 10000);
    }
}
