/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.test.map.mutable;

import java.util.Iterator;

import com.m3.collections.eclipse.api.map.MutableMap;
import com.m3.collections.eclipse.impl.utility.Iterate;
import com.m3.collections.eclipse.test.MutableUnorderedIterableTestCase;
import com.m3.collections.eclipse.test.map.UnsortedMapIterableTestCase;

import static com.m3.collections.eclipse.test.IterableTestCase.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isOneOf;

public interface MutableMapTestCase extends UnsortedMapIterableTestCase, MutableUnorderedIterableTestCase, MutableMapIterableTestCase
{
    @Override
    <T> MutableMap<Object, T> newWith(T... elements);

    @Override
    <K, V> MutableMap<K, V> newWithKeysValues(Object... elements);

    @Override
    default void Iterable_toString()
    {
        UnsortedMapIterableTestCase.super.Iterable_toString();

        MutableMap<String, Integer> map = this.newWithKeysValues("Two", 2, "One", 1);
        assertThat(map.keySet().toString(), isOneOf("[One, Two]", "[Two, One]"));
        assertThat(map.values().toString(), isOneOf("[1, 2]", "[2, 1]"));
        assertThat(map.entrySet().toString(), isOneOf("[One=1, Two=2]", "[Two=2, One=1]"));
    }

    @Override
    default void Iterable_remove()
    {
        MutableMap<Object, Integer> iterable = this.newWith(3, 3, 3, 2, 2, 1);
        Iterator<Integer> iterator = iterable.iterator();
        iterator.next();
        iterator.remove();
        assertEquals(this.allowsDuplicates() ? 5 : 2, Iterate.sizeOf(iterable));
        assertThat(iterable.toBag(), isOneOf(
                this.getExpectedFiltered(3, 3, 3, 2, 2),
                this.getExpectedFiltered(3, 3, 3, 2, 1),
                this.getExpectedFiltered(3, 3, 2, 2, 1)));
    }
}
