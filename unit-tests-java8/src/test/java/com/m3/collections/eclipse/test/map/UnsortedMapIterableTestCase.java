/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.test.map;

import com.m3.collections.eclipse.api.RichIterable;
import com.m3.collections.eclipse.api.bag.MutableBag;
import com.m3.collections.eclipse.api.bag.UnsortedBag;
import com.m3.collections.eclipse.api.factory.Bags;
import com.m3.collections.eclipse.api.factory.Lists;
import com.m3.collections.eclipse.api.list.MutableList;
import com.m3.collections.eclipse.api.map.MapIterable;
import com.m3.collections.eclipse.api.map.UnsortedMapIterable;
import com.m3.collections.eclipse.test.UnorderedIterableTestCase;
import com.m3.collections.eclipse.test.bag.TransformsToBagTrait;
import org.junit.Test;

import static com.m3.collections.eclipse.test.IterableTestCase.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isOneOf;

public interface UnsortedMapIterableTestCase
        extends MapIterableTestCase, UnorderedIterableTestCase, TransformsToBagTrait
{
    @Override
    <T> UnsortedMapIterable<Object, T> newWith(T... elements);

    @Override
    default <T> UnsortedBag<T> getExpectedFiltered(T... elements)
    {
        return Bags.immutable.with(elements);
    }

    @Override
    default <T> MutableBag<T> newMutableForFilter(T... elements)
    {
        return Bags.mutable.with(elements);
    }

    @Override
    default void Iterable_toString()
    {
        MapIterable<String, Integer> map = this.newWithKeysValues("Two", 2, "One", 1);

        assertThat(map.toString(), isOneOf("{One=1, Two=2}", "{Two=2, One=1}"));
        assertThat(map.keysView().toString(), isOneOf("[One, Two]", "[Two, One]"));
        assertThat(map.valuesView().toString(), isOneOf("[1, 2]", "[2, 1]"));
        assertThat(map.keyValuesView().toString(), isOneOf("[One:1, Two:2]", "[Two:2, One:1]"));
        assertThat(map.asLazy().toString(), isOneOf("[1, 2]", "[2, 1]"));
    }

    @Override
    @Test
    default void RichIterable_makeString_appendString()
    {
        RichIterable<Integer> iterable = this.newWith(2, 2, 1);
        assertThat(iterable.makeString(), isOneOf("2, 2, 1", "1, 2, 2", "2, 1, 2"));
        assertThat(iterable.makeString("/"), isOneOf("2/2/1", "1/2/2", "2/1/2"));
        assertThat(iterable.makeString("[", "/", "]"), isOneOf("[2/2/1]", "[1/2/2]", "[2/1/2]"));

        StringBuilder builder1 = new StringBuilder();
        iterable.appendString(builder1);
        assertThat(builder1.toString(), isOneOf("2, 2, 1", "1, 2, 2", "2, 1, 2"));

        StringBuilder builder2 = new StringBuilder();
        iterable.appendString(builder2, "/");
        assertThat(builder2.toString(), isOneOf("2/2/1", "1/2/2", "2/1/2"));

        StringBuilder builder3 = new StringBuilder();
        iterable.appendString(builder3, "[", "/", "]");
        assertThat(builder3.toString(), isOneOf("[2/2/1]", "[1/2/2]", "[2/1/2]"));
    }

    @Override
    @Test
    default void RichIterable_toList()
    {
        UnsortedMapIterable<Object, Integer> iterable = this.newWith(2, 2, 1);
        assertThat(
                iterable.toList(),
                isOneOf(
                        Lists.immutable.with(2, 2, 1),
                        Lists.immutable.with(1, 2, 2),
                        Lists.immutable.with(2, 1, 2)));

        MutableList<Integer> target = Lists.mutable.empty();
        iterable.each(target::add);
        assertEquals(
                target,
                iterable.toList());
    }

    @Override
    @Test
    default void RichIterable_into()
    {
        assertThat(
                this.newWith(2, 2, 1).into(Lists.mutable.empty()),
                isOneOf(
                        Lists.immutable.with(2, 2, 1),
                        Lists.immutable.with(1, 2, 2),
                        Lists.immutable.with(2, 1, 2)));
    }
}
