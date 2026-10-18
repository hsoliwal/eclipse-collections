/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.test.bag.mutable;

import com.m3.collections.eclipse.api.bag.MutableBag;
import com.m3.collections.eclipse.test.MutableUnorderedIterableTestCase;
import com.m3.collections.eclipse.test.bag.UnsortedBagTestCase;
import com.m3.collections.eclipse.test.bag.mutable.sorted.MutableBagIterableTestCase;

public interface MutableBagTestCase extends UnsortedBagTestCase, MutableUnorderedIterableTestCase, MutableBagIterableTestCase
{
    @Override
    <T> MutableBag<T> newWith(T... elements);
}
