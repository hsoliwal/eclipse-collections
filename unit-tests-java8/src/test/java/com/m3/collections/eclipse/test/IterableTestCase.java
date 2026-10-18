/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.test;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.SortedSet;

import com.m3.collections.eclipse.api.LazyBooleanIterable;
import com.m3.collections.eclipse.api.LazyByteIterable;
import com.m3.collections.eclipse.api.LazyCharIterable;
import com.m3.collections.eclipse.api.LazyDoubleIterable;
import com.m3.collections.eclipse.api.LazyFloatIterable;
import com.m3.collections.eclipse.api.LazyIntIterable;
import com.m3.collections.eclipse.api.LazyIterable;
import com.m3.collections.eclipse.api.LazyLongIterable;
import com.m3.collections.eclipse.api.LazyShortIterable;
import com.m3.collections.eclipse.api.collection.ImmutableCollection;
import com.m3.collections.eclipse.api.collection.MutableCollection;
import com.m3.collections.eclipse.api.factory.Sets;
import com.m3.collections.eclipse.api.list.ListIterable;
import com.m3.collections.eclipse.api.list.primitive.BooleanList;
import com.m3.collections.eclipse.api.list.primitive.ByteList;
import com.m3.collections.eclipse.api.list.primitive.CharList;
import com.m3.collections.eclipse.api.list.primitive.DoubleList;
import com.m3.collections.eclipse.api.list.primitive.FloatList;
import com.m3.collections.eclipse.api.list.primitive.IntList;
import com.m3.collections.eclipse.api.list.primitive.LongList;
import com.m3.collections.eclipse.api.list.primitive.ShortList;
import com.m3.collections.eclipse.api.ordered.ReversibleIterable;
import com.m3.collections.eclipse.api.ordered.SortedIterable;
import com.m3.collections.eclipse.api.set.MutableSet;
import com.m3.collections.eclipse.impl.list.mutable.MultiReaderFastList;
import com.m3.collections.eclipse.impl.test.SerializeTestHelper;
import com.m3.collections.eclipse.impl.test.Verify;
import org.junit.Assert;
import org.junit.Test;

import static com.m3.collections.eclipse.impl.test.Verify.assertIterablesEqual;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public interface IterableTestCase
{
    <T> Iterable<T> newWith(T... elements);

    boolean allowsDuplicates();

    static void assertEquals(Object o1, Object o2)
    {
        if (o1 instanceof ListIterable<?> && o2 instanceof LazyIterable<?>)
        {
            IterableTestCase.assertEquals(o1, ((LazyIterable<?>) o2).toList());
            return;
        }
        if (o1 instanceof BooleanList && o2 instanceof LazyBooleanIterable)
        {
            IterableTestCase.assertEquals(o1, ((LazyBooleanIterable) o2).toList());
            return;
        }
        if (o1 instanceof ByteList && o2 instanceof LazyByteIterable)
        {
            IterableTestCase.assertEquals(o1, ((LazyByteIterable) o2).toList());
            return;
        }
        if (o1 instanceof CharList && o2 instanceof LazyCharIterable)
        {
            IterableTestCase.assertEquals(o1, ((LazyCharIterable) o2).toList());
            return;
        }
        if (o1 instanceof DoubleList && o2 instanceof LazyDoubleIterable)
        {
            IterableTestCase.assertEquals(o1, ((LazyDoubleIterable) o2).toList());
            return;
        }
        if (o1 instanceof FloatList && o2 instanceof LazyFloatIterable)
        {
            IterableTestCase.assertEquals(o1, ((LazyFloatIterable) o2).toList());
            return;
        }
        if (o1 instanceof IntList && o2 instanceof LazyIntIterable)
        {
            IterableTestCase.assertEquals(o1, ((LazyIntIterable) o2).toList());
            return;
        }
        if (o1 instanceof LongList && o2 instanceof LazyLongIterable)
        {
            IterableTestCase.assertEquals(o1, ((LazyLongIterable) o2).toList());
            return;
        }
        if (o1 instanceof ShortList && o2 instanceof LazyShortIterable)
        {
            IterableTestCase.assertEquals(o1, ((LazyShortIterable) o2).toList());
            return;
        }

        Assert.assertEquals(o1, o2);

        assertFalse("Neither item should equal null", o1.equals(null));
        assertFalse("Neither item should equal null", o2.equals(null));
        assertNotEquals("Neither item should equal new Object()", o1.equals(new Object()));
        assertNotEquals("Neither item should equal new Object()", o2.equals(new Object()));
        Assert.assertEquals(o1, o1);
        Assert.assertEquals(o2, o2);
        Assert.assertEquals(o1, o2);
        Assert.assertEquals(o2, o1);
        Assert.assertEquals(o1.hashCode(), o2.hashCode());

        checkNotSame(o1, o2);

        if (o1 instanceof MultiReaderFastList<?> || o2 instanceof MultiReaderFastList<?>)
        {
            return;
        }
        if (o1 instanceof SortedIterable<?> || o2 instanceof SortedIterable<?>
                || o1 instanceof ReversibleIterable<?> || o2 instanceof ReversibleIterable<?>
                || o1 instanceof List<?> || o2 instanceof List<?>
                || o1 instanceof SortedSet<?> || o2 instanceof SortedSet<?>)
        {
            assertIterablesEqual((Iterable<?>) o1, (Iterable<?>) o2);
            if (o1 instanceof SortedIterable<?> || o2 instanceof SortedIterable<?>)
            {
                Comparator<?> comparator1 = ((SortedIterable<?>) o1).comparator();
                Comparator<?> comparator2 = ((SortedIterable<?>) o2).comparator();
                if (comparator1 != null && comparator2 != null)
                {
                    assertSame(comparator1.getClass(), comparator2.getClass());
                }
            }
        }
        else if (o1 instanceof SortedMap<?, ?> || o2 instanceof SortedMap<?, ?>)
        {
            IterableTestCase.assertEquals(((SortedMap<?, ?>) o1).keySet(), ((SortedMap<?, ?>) o2).keySet());
        }
    }

    static void checkNotSame(Object o1, Object o2)
    {
        if (o1 instanceof String && o2 instanceof String)
        {
            return;
        }
        if ((o1 instanceof Number && o2 instanceof Number)
                || (o1 instanceof Boolean && o2 instanceof Boolean)
                || o1 instanceof ImmutableCollection<?> && o2 instanceof ImmutableCollection<?>
                && ((ImmutableCollection<?>) o1).isEmpty() && ((ImmutableCollection<?>) o2).isEmpty()
                && !(o1 instanceof SortedIterable<?>) && !(o2 instanceof SortedIterable<?>))
        {
            assertSame(o1, o2);
            return;
        }
        assertNotSame(o1, o2);
    }

    static void assertNotEquals(Object o1, Object o2)
    {
        Assert.assertNotEquals(o1, o2);
        Assert.assertNotEquals(o2, o1);

        assertFalse("Neither item should equal null", o1.equals(null));
        assertFalse("Neither item should equal null", o2.equals(null));
        Assert.assertNotEquals("Neither item should equal new Object()", o1.equals(new Object()));
        Assert.assertNotEquals("Neither item should equal new Object()", o2.equals(new Object()));
        Assert.assertEquals(o1, o1);
        Assert.assertEquals(o2, o2);
    }

    static <T> void addAllTo(T[] elements, MutableCollection<T> result)
    {
        for (T element : elements)
        {
            if (!result.add(element))
            {
                throw new IllegalStateException();
            }
        }
    }

    @Test
    default void Object_PostSerializedEqualsAndHashCode()
    {
        Iterable<Integer> iterable = this.newWith(3, 3, 3, 2, 2, 1);
        Object deserialized = SerializeTestHelper.serializeDeserialize(iterable);
        assertNotSame(iterable, deserialized);
    }

    @Test
    default void Object_equalsAndHashCode()
    {
        Verify.assertEqualsAndHashCode(this.newWith(3, 3, 3, 2, 2, 1), this.newWith(3, 3, 3, 2, 2, 1));

        assertNotEquals(this.newWith(4, 3, 2, 1), this.newWith(3, 2, 1));
        assertNotEquals(this.newWith(3, 2, 1), this.newWith(4, 3, 2, 1));

        assertNotEquals(this.newWith(2, 1), this.newWith(3, 2, 1));
        assertNotEquals(this.newWith(3, 2, 1), this.newWith(2, 1));

        assertNotEquals(this.newWith(3, 3, 2, 1), this.newWith(3, 2, 1));
        assertNotEquals(this.newWith(3, 2, 1), this.newWith(3, 3, 2, 1));

        assertNotEquals(this.newWith(3, 3, 2, 1), this.newWith(3, 2, 2, 1));
        assertNotEquals(this.newWith(3, 2, 2, 1), this.newWith(3, 3, 2, 1));
    }

    @Test
    default void Iterable_hasNext()
    {
        assertTrue(this.newWith(3, 2, 1).iterator().hasNext());
        assertFalse(this.newWith().iterator().hasNext());
    }

    @Test
    default void Iterable_next()
    {
        Iterator<Integer> iterator = this.newWith(3, 2, 1).iterator();
        MutableSet<Integer> set = Sets.mutable.with();
        assertTrue(set.add(iterator.next()));
        assertTrue(set.add(iterator.next()));
        assertTrue(set.add(iterator.next()));
        IterableTestCase.assertEquals(Sets.immutable.with(3, 2, 1), set);
    }

    @Test(expected = NoSuchElementException.class)
    default void Iterable_next_throws_on_empty()
    {
        this.newWith().iterator().next();
    }

    @Test
    default void Iterable_next_throws_at_end()
    {
        Iterable<Integer> iterable = this.newWith(3, 2, 1);
        Iterator<Integer> iterator = iterable.iterator();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);

        Iterator<Integer> iterator2 = iterable.iterator();
        iterator2.next();
        iterator2.next();
        iterator2.next();
        assertThrows(NoSuchElementException.class, iterator2::next);
        assertThrows(NoSuchElementException.class, iterator2::next);
    }

    void Iterable_remove();

    void Iterable_toString();
}
