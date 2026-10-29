/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.Callable;

import com.m3.collections.eclipse.api.InternalIterable;
import com.m3.collections.eclipse.api.PrimitiveIterable;
import com.m3.collections.eclipse.api.bag.Bag;
import com.m3.collections.eclipse.api.bag.sorted.SortedBag;
import com.m3.collections.eclipse.api.block.predicate.Predicate;
import com.m3.collections.eclipse.api.collection.ImmutableCollection;
import com.m3.collections.eclipse.api.factory.Lists;
import com.m3.collections.eclipse.api.factory.Maps;
import com.m3.collections.eclipse.api.list.ImmutableList;
import com.m3.collections.eclipse.api.list.MutableList;
import com.m3.collections.eclipse.api.map.ImmutableMapIterable;
import com.m3.collections.eclipse.api.map.MapIterable;
import com.m3.collections.eclipse.api.map.MutableMap;
import com.m3.collections.eclipse.api.map.MutableMapIterable;
import com.m3.collections.eclipse.api.map.sorted.SortedMapIterable;
import com.m3.collections.eclipse.api.multimap.Multimap;
import com.m3.collections.eclipse.api.multimap.bag.BagMultimap;
import com.m3.collections.eclipse.api.multimap.list.ListMultimap;
import com.m3.collections.eclipse.api.multimap.set.SetMultimap;
import com.m3.collections.eclipse.api.multimap.sortedbag.SortedBagMultimap;
import com.m3.collections.eclipse.api.multimap.sortedset.SortedSetMultimap;
import com.m3.collections.eclipse.api.set.ImmutableSet;
import com.m3.collections.eclipse.api.set.MutableSet;
import com.m3.collections.eclipse.impl.block.factory.Predicates;
import com.m3.collections.eclipse.impl.block.procedure.CollectionAddProcedure;
import com.m3.collections.eclipse.impl.factory.Sets;
import com.m3.collections.eclipse.impl.list.mutable.FastList;
import com.m3.collections.eclipse.impl.set.mutable.UnifiedSet;
import com.m3.collections.eclipse.impl.tuple.ImmutableEntry;
import com.m3.collections.eclipse.impl.utility.ArrayIterate;
import com.m3.collections.eclipse.impl.utility.Iterate;
import org.junit.Assert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * An extension of the {@link Assert} class, which adds useful additional "assert" methods.
 * You can import this class instead of Assert, and use it thus, e.g.:
 * <pre>
 *     Verify.assertEquals("fred", name);  // from original Assert class
 *     Verify.assertContains("fred", nameList);  // from new extensions
 *     Verify.assertBefore("fred", "jim", orderedNamesList);  // from new extensions
 * </pre>
 */
@SuppressWarnings("ExtendingJUnitAssert")
public final class Verify extends Assert
{
    private static final int MAX_DIFFERENCES = 5;
    private static final byte[] LINE_SEPARATOR = {'\n'};
    private static final Encoder ENCODER = Base64.getMimeEncoder(76, LINE_SEPARATOR);
    private static final Decoder DECODER = Base64.getMimeDecoder();

    private Verify()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    public static void fail(String message, Throwable cause)
    {
        throw new AssertionError(message, cause);
    }

    /**
     * Assert that the given {@link Iterable} is empty.
     */
    public static void assertEmpty(Iterable<?> actualIterable)
    {
        Verify.assertEmpty("iterable", actualIterable);
    }

    /**
     * Assert that the given {@link Collection} is empty.
     */
    public static void assertEmpty(String iterableName, Iterable<?> actualIterable)
    {
        Verify.assertObjectNotNull(iterableName, actualIterable);

        if (Iterate.notEmpty(actualIterable))
        {
            fail(iterableName + " should be empty; actual size:<" + Iterate.sizeOf(actualIterable) + '>');
        }
        if (!Iterate.isEmpty(actualIterable))
        {
            fail(iterableName + " should be empty; actual size:<" + Iterate.sizeOf(actualIterable) + '>');
        }
        if (Iterate.sizeOf(actualIterable) != 0)
        {
            fail(iterableName + " should be empty; actual size:<" + Iterate.sizeOf(actualIterable) + '>');
        }
    }

    /**
     * Assert that the given {@link MutableMapIterable} is empty.
     */
    public static void assertEmpty(MutableMapIterable<?, ?> actualMutableMapIterable)
    {
        Verify.assertEmpty("mutableMapIterable", actualMutableMapIterable);
    }

    /**
     * Assert that the given {@link Collection} is empty.
     */
    public static void assertEmpty(String mutableMapIterableName, MutableMapIterable<?, ?> actualMutableMapIterable)
    {
        Verify.assertObjectNotNull(mutableMapIterableName, actualMutableMapIterable);

        if (Iterate.notEmpty(actualMutableMapIterable))
        {
            fail(mutableMapIterableName + " should be empty; actual size:<" + Iterate.sizeOf(actualMutableMapIterable) + '>');
        }
        if (!Iterate.isEmpty(actualMutableMapIterable))
        {
            fail(mutableMapIterableName + " should be empty; actual size:<" + Iterate.sizeOf(actualMutableMapIterable) + '>');
        }
        if (!actualMutableMapIterable.isEmpty())
        {
            fail(mutableMapIterableName + " should be empty; actual size:<" + Iterate.sizeOf(actualMutableMapIterable) + '>');
        }
        if (actualMutableMapIterable.notEmpty())
        {
            fail(mutableMapIterableName + " should be empty; actual size:<" + Iterate.sizeOf(actualMutableMapIterable) + '>');
        }
        if (!actualMutableMapIterable.isEmpty())
        {
            fail(mutableMapIterableName + " should be empty; actual size:<" + actualMutableMapIterable.size() + '>');
        }
        if (!actualMutableMapIterable.keySet().isEmpty())
        {
            fail(mutableMapIterableName + " should be empty; actual size:<" + actualMutableMapIterable.keySet().size() + '>');
        }
        if (!actualMutableMapIterable.values().isEmpty())
        {
            fail(mutableMapIterableName + " should be empty; actual size:<" + actualMutableMapIterable.values().size() + '>');
        }
        if (!actualMutableMapIterable.entrySet().isEmpty())
        {
            fail(mutableMapIterableName + " should be empty; actual size:<" + actualMutableMapIterable.entrySet().size() + '>');
        }
    }

    /**
     * Assert that the given {@link PrimitiveIterable} is empty.
     */
    public static void assertEmpty(PrimitiveIterable primitiveIterable)
    {
        Verify.assertEmpty("primitiveIterable", primitiveIterable);
    }

    /**
     * Assert that the given {@link PrimitiveIterable} is empty.
     */
    @SuppressWarnings("SizeReplaceableByIsEmpty")
    public static void assertEmpty(String iterableName, PrimitiveIterable primitiveIterable)
    {
        Verify.assertObjectNotNull(iterableName, primitiveIterable);

        if (primitiveIterable.notEmpty())
        {
            fail(iterableName + " should be empty; actual size:<" + primitiveIterable.size() + '>');
        }
        if (!primitiveIterable.isEmpty())
        {
            fail(iterableName + " should be empty; actual size:<" + primitiveIterable.size() + '>');
        }
        if (primitiveIterable.size() != 0)
        {
            fail(iterableName + " should be empty; actual size:<" + primitiveIterable.size() + '>');
        }
    }

    /**
     * Assert that the given {@link Iterable} is empty.
     */
    public static void assertIterableEmpty(Iterable<?> iterable)
    {
        Verify.assertIterableEmpty("iterable", iterable);
    }

    /**
     * Assert that the given {@link Iterable} is empty.
     */
    public static void assertIterableEmpty(String iterableName, Iterable<?> iterable)
    {
        Verify.assertObjectNotNull(iterableName, iterable);

        if (Iterate.notEmpty(iterable))
        {
            fail(iterableName + " should be empty; actual size:<" + Iterate.sizeOf(iterable) + '>');
        }
        if (!Iterate.isEmpty(iterable))
        {
            fail(iterableName + " should be empty; actual size:<" + Iterate.sizeOf(iterable) + '>');
        }
        if (Iterate.sizeOf(iterable) != 0)
        {
            fail(iterableName + " should be empty; actual size:<" + Iterate.sizeOf(iterable) + '>');
        }
    }

    /**
     * Assert that the given object is an instanceof expectedClassType.
     */
    public static void assertInstanceOf(Class<?> expectedClassType, Object actualObject)
    {
        Verify.assertInstanceOf(actualObject.getClass().getName(), expectedClassType, actualObject);
    }

    /**
     * Assert that the given object is an instanceof expectedClassType.
     */
    public static void assertInstanceOf(String objectName, Class<?> expectedClassType, Object actualObject)
    {
        if (!expectedClassType.isInstance(actualObject))
        {
            fail(objectName + " is not an instance of " + expectedClassType.getName());
        }
    }

    /**
     * Assert that the given object is not an instanceof expectedClassType.
     */
    public static void assertNotInstanceOf(Class<?> expectedClassType, Object actualObject)
    {
        Verify.assertNotInstanceOf(actualObject.getClass().getName(), expectedClassType, actualObject);
    }

    /**
     * Assert that the given object is not an instanceof expectedClassType.
     */
    public static void assertNotInstanceOf(String objectName, Class<?> expectedClassType, Object actualObject)
    {
        if (expectedClassType.isInstance(actualObject))
        {
            fail(objectName + " is an instance of " + expectedClassType.getName());
        }
    }

    /**
     * Assert that the given {@link Map} is empty.
     */
    public static void assertEmpty(Map<?, ?> actualMap)
    {
        Verify.assertEmpty("map", actualMap);
    }

    /**
     * Assert that the given {@link Multimap} is empty.
     */
    public static void assertEmpty(Multimap<?, ?> actualMultimap)
    {
        Verify.assertEmpty("multimap", actualMultimap);
    }

    /**
     * Assert that the given {@link Multimap} is empty.
     */
    @SuppressWarnings("SizeReplaceableByIsEmpty")
    public static void assertEmpty(String multimapName, Multimap<?, ?> actualMultimap)
    {
        Verify.assertObjectNotNull(multimapName, actualMultimap);

        if (actualMultimap.notEmpty())
        {
            fail(multimapName + " should be empty; actual size:<" + actualMultimap.size() + '>');
        }
        if (!actualMultimap.isEmpty())
        {
            fail(multimapName + " should be empty; actual size:<" + actualMultimap.size() + '>');
        }
        if (actualMultimap.size() != 0)
        {
            fail(multimapName + " should be empty; actual size:<" + actualMultimap.size() + '>');
        }
        if (actualMultimap.sizeDistinct() != 0)
        {
            fail(multimapName + " should be empty; actual size:<" + actualMultimap.size() + '>');
        }
        if (!actualMultimap.keyBag().isEmpty())
        {
            fail(multimapName + " should be empty; actual size:<" + actualMultimap.keyBag().size() + '>');
        }
        if (!actualMultimap.keysView().isEmpty())
        {
            fail(multimapName + " should be empty; actual size:<" + actualMultimap.keysView().size() + '>');
        }
        if (!actualMultimap.valuesView().isEmpty())
        {
            fail(multimapName + " should be empty; actual size:<" + actualMultimap.valuesView().size() + '>');
        }
        if (!actualMultimap.keyValuePairsView().isEmpty())
        {
            fail(multimapName + " should be empty; actual size:<" + actualMultimap.keyValuePairsView().size() + '>');
        }
        if (!actualMultimap.keyMultiValuePairsView().isEmpty())
        {
            fail(multimapName + " should be empty; actual size:<" + actualMultimap.keyMultiValuePairsView().size() + '>');
        }
    }

    /**
     * Assert that the given {@link Map} is empty.
     */
    @SuppressWarnings("SizeReplaceableByIsEmpty")
    public static void assertEmpty(String mapName, Map<?, ?> actualMap)
    {
        Verify.assertObjectNotNull(mapName, actualMap);

        if (!actualMap.isEmpty())
        {
            fail(mapName + " should be empty; actual size:<" + actualMap.size() + '>');
        }
        if (actualMap.size() != 0)
        {
            fail(mapName + " should be empty; actual size:<" + actualMap.size() + '>');
        }
        if (!actualMap.keySet().isEmpty())
        {
            fail(mapName + " should be empty; actual size:<" + actualMap.keySet().size() + '>');
        }
        if (!actualMap.values().isEmpty())
        {
            fail(mapName + " should be empty; actual size:<" + actualMap.values().size() + '>');
        }
        if (!actualMap.entrySet().isEmpty())
        {
            fail(mapName + " should be empty; actual size:<" + actualMap.entrySet().size() + '>');
        }
    }

    /**
     * Assert that the given {@link Iterable} is <em>not</em> empty.
     */
    public static void assertNotEmpty(Iterable<?> actualIterable)
    {
        Verify.assertNotEmpty("iterable", actualIterable);
    }

    /**
     * Assert that the given {@link Iterable} is <em>not</em> empty.
     */
    public static void assertNotEmpty(String iterableName, Iterable<?> actualIterable)
    {
        Verify.assertObjectNotNull(iterableName, actualIterable);
        assertFalse(iterableName + " should be non-empty, but was empty", Iterate.isEmpty(actualIterable));
        assertTrue(iterableName + " should be non-empty, but was empty", Iterate.notEmpty(actualIterable));
        assertNotEquals(iterableName + " should be non-empty, but was empty", 0, Iterate.sizeOf(actualIterable));
    }

    /**
     * Assert that the given {@link MutableMapIterable} is <em>not</em> empty.
     */
    public static void assertNotEmpty(MutableMapIterable<?, ?> actualMutableMapIterable)
    {
        Verify.assertNotEmpty("mutableMapIterable", actualMutableMapIterable);
    }

    /**
     * Assert that the given {@link MutableMapIterable} is <em>not</em> empty.
     */
    public static void assertNotEmpty(String mutableMapIterableName, MutableMapIterable<?, ?> actualMutableMapIterable)
    {
        Verify.assertObjectNotNull(mutableMapIterableName, actualMutableMapIterable);
        assertFalse(mutableMapIterableName + " should be non-empty, but was empty", Iterate.isEmpty(actualMutableMapIterable));
        assertTrue(mutableMapIterableName + " should be non-empty, but was empty", Iterate.notEmpty(actualMutableMapIterable));
        assertTrue(mutableMapIterableName + " should be non-empty, but was empty", actualMutableMapIterable.notEmpty());
        assertNotEquals(mutableMapIterableName + " should be non-empty, but was empty", 0, actualMutableMapIterable.size());
        assertNotEquals(mutableMapIterableName + " should be non-empty, but was empty", 0, actualMutableMapIterable.keySet().size());
        assertNotEquals(mutableMapIterableName + " should be non-empty, but was empty", 0, actualMutableMapIterable.values().size());
        assertNotEquals(mutableMapIterableName + " should be non-empty, but was empty", 0, actualMutableMapIterable.entrySet().size());
    }

    /**
     * Assert that the given {@link PrimitiveIterable} is <em>not</em> empty.
     */
    public static void assertNotEmpty(PrimitiveIterable primitiveIterable)
    {
        Verify.assertNotEmpty("primitiveIterable", primitiveIterable);
    }

    /**
     * Assert that the given {@link PrimitiveIterable} is <em>not</em> empty.
     */
    public static void assertNotEmpty(String iterableName, PrimitiveIterable primitiveIterable)
    {
        Verify.assertObjectNotNull(iterableName, primitiveIterable);
        assertFalse(iterableName + " should be non-empty, but was empty", primitiveIterable.isEmpty());
        assertTrue(iterableName + " should be non-empty, but was empty", primitiveIterable.notEmpty());
        assertNotEquals(iterableName + " should be non-empty, but was empty", 0, primitiveIterable.size());
    }

    /**
     * Assert that the given {@link Iterable} is <em>not</em> empty.
     */
    public static void assertIterableNotEmpty(Iterable<?> iterable)
    {
        Verify.assertIterableNotEmpty("iterable", iterable);
    }

    /**
     * Assert that the given {@link Iterable} is <em>not</em> empty.
     */
    public static void assertIterableNotEmpty(String iterableName, Iterable<?> iterable)
    {
        Verify.assertObjectNotNull(iterableName, iterable);
        assertFalse(iterableName + " should be non-empty, but was empty", Iterate.isEmpty(iterable));
        assertTrue(iterableName + " should be non-empty, but was empty", Iterate.notEmpty(iterable));
        assertNotEquals(iterableName + " should be non-empty, but was empty", 0, Iterate.sizeOf(iterable));
    }

    /**
     * Assert that the given {@link Map} is <em>not</em> empty.
     */
    public static void assertNotEmpty(Map<?, ?> actualMap)
    {
        Verify.assertNotEmpty("map", actualMap);
    }

    /**
     * Assert that the given {@link Map} is <em>not</em> empty.
     */
    public static void assertNotEmpty(String mapName, Map<?, ?> actualMap)
    {
        Verify.assertObjectNotNull(mapName, actualMap);
        assertFalse(mapName + " should be non-empty, but was empty", actualMap.isEmpty());
        assertNotEquals(mapName + " should be non-empty, but was empty", 0, actualMap.size());
        assertNotEquals(mapName + " should be non-empty, but was empty", 0, actualMap.keySet().size());
        assertNotEquals(mapName + " should be non-empty, but was empty", 0, actualMap.values().size());
        assertNotEquals(mapName + " should be non-empty, but was empty", 0, actualMap.entrySet().size());
    }

    /**
     * Assert that the given {@link Multimap} is <em>not</em> empty.
     */
    public static void assertNotEmpty(Multimap<?, ?> actualMultimap)
    {
        Verify.assertNotEmpty("multimap", actualMultimap);
    }

    /**
     * Assert that the given {@link Multimap} is <em>not</em> empty.
     */
    public static void assertNotEmpty(String multimapName, Multimap<?, ?> actualMultimap)
    {
        Verify.assertObjectNotNull(multimapName, actualMultimap);
        assertTrue(multimapName + " should be non-empty, but was empty", actualMultimap.notEmpty());
        assertFalse(multimapName + " should be non-empty, but was empty", actualMultimap.isEmpty());
        assertNotEquals(multimapName + " should be non-empty, but was empty", 0, actualMultimap.size());
        assertNotEquals(multimapName + " should be non-empty, but was empty", 0, actualMultimap.sizeDistinct());
        assertNotEquals(multimapName + " should be non-empty, but was empty", 0, actualMultimap.keyBag().size());
        assertNotEquals(multimapName + " should be non-empty, but was empty", 0, actualMultimap.keysView().size());
        assertNotEquals(multimapName + " should be non-empty, but was empty", 0, actualMultimap.valuesView().size());
        assertNotEquals(multimapName + " should be non-empty, but was empty", 0, actualMultimap.keyValuePairsView().size());
        assertNotEquals(multimapName + " should be non-empty, but was empty", 0, actualMultimap.keyMultiValuePairsView().size());
    }

    public static <T> void assertNotEmpty(String itemsName, T[] items)
    {
        Verify.assertObjectNotNull(itemsName, items);
        assertNotEquals(itemsName, 0, items.length);
    }

    public static <T> void assertNotEmpty(T[] items)
    {
        Verify.assertNotEmpty("items", items);
    }

    /**
     * Assert the size of the given array.
     */
    public static void assertSize(int expectedSize, Object[] actualArray)
    {
        Verify.assertSize("array", expectedSize, actualArray);
    }

    /**
     * Assert the size of the given array.
     */
    public static void assertSize(String arrayName, int expectedSize, Object[] actualArray)
    {
        assertNotNull(arrayName + " should not be null", actualArray);

        int actualSize = actualArray.length;
        if (actualSize != expectedSize)
        {
            fail("Incorrect size for "
                    + arrayName
                    + "; expected:<"
                    + expectedSize
                    + "> but was:<"
                    + actualSize
                    + '>');
        }
    }

    /**
     * Assert the size of the given {@link Iterable}.
     */
    public static void assertSize(int expectedSize, Iterable<?> actualIterable)
    {
        Verify.assertSize("iterable", expectedSize, actualIterable);
    }

    /**
     * Assert the size of the given {@link Iterable}.
     */
    public static void assertSize(
            String iterableName,
            int expectedSize,
            Iterable<?> actualIterable)
    {
        Verify.assertObjectNotNull(iterableName, actualIterable);

        int actualSize = Iterate.sizeOf(actualIterable);
        if (actualSize != expectedSize)
        {
            fail("Incorrect size for "
                    + iterableName
                    + "; expected:<"
                    + expectedSize
                    + "> but was:<"
                    + actualSize
                    + '>');
        }
    }

    /**
     * Assert the size of the given {@link PrimitiveIterable}.
     */
    public static void assertSize(int expectedSize, PrimitiveIterable primitiveIterable)
    {
        Verify.assertSize("primitiveIterable", expectedSize, primitiveIterable);
    }

    /**
     * Assert the size of the given {@link PrimitiveIterable}.
     */
    public static void assertSize(
            String primitiveIterableName,
            int expectedSize,
            PrimitiveIterable actualPrimitiveIterable)
    {
        Verify.assertObjectNotNull(primitiveIterableName, actualPrimitiveIterable);

        int actualSize = actualPrimitiveIterable.size();
        if (actualSize != expectedSize)
        {
            fail("Incorrect size for "
                    + primitiveIterableName
                    + "; expected:<"
                    + expectedSize
                    + "> but was:<"
                    + actualSize
                    + '>');
        }
    }

    /**
     * Assert the size of the given {@link Iterable}.
     */
    public static void assertIterableSize(int expectedSize, Iterable<?> actualIterable)
    {
        Verify.assertIterableSize("iterable", expectedSize, actualIterable);
    }

    /**
     * Assert the size of the given {@link Iterable}.
     */
    public static void assertIterableSize(
            String iterableName,
            int expectedSize,
            Iterable<?> actualIterable)
    {
        Verify.assertObjectNotNull(iterableName, actualIterable);

        int actualSize = Iterate.sizeOf(actualIterable);
        if (actualSize != expectedSize)
        {
            fail("Incorrect size for "
                    + iterableName
                    + "; expected:<"
                    + expectedSize
                    + "> but was:<"
                    + actualSize
                    + '>');
        }
    }

    /**
     * Assert the size of the given {@link Map}.
     */
    public static void assertSize(String mapName, int expectedSize, Map<?, ?> actualMap)
    {
        Verify.assertSize(mapName, expectedSize, actualMap.keySet());
    }

    /**
     * Assert the size of the given {@link Map}.
     */
    public static void assertSize(int expectedSize, Map<?, ?> actualMap)
    {
        Verify.assertSize("map", expectedSize, actualMap);
    }

    /**
     * Assert the size of the given {@link Multimap}.
     */
    public static void assertSize(int expectedSize, Multimap<?, ?> actualMultimap)
    {
        Verify.assertSize("multimap", expectedSize, actualMultimap);
    }

    /**
     * Assert the size of the given {@link Multimap}.
     */
    public static void assertSize(String multimapName, int expectedSize, Multimap<?, ?> actualMultimap)
    {
        int actualSize = actualMultimap.size();
        if (actualSize != expectedSize)
        {
            fail("Incorrect size for "
                    + multimapName
                    + "; expected:<"
                    + expectedSize
                    + "> but was:<"
                    + actualSize
                    + '>');
        }
    }

    /**
     * Assert the size of the given {@link MutableMapIterable}.
     */
    public static void assertSize(int expectedSize, MutableMapIterable<?, ?> mutableMapIterable)
    {
        Verify.assertSize("map", expectedSize, mutableMapIterable);
    }

    /**
     * Assert the size of the given {@link MutableMapIterable}.
     */
    public static void assertSize(String mapName, int expectedSize, MutableMapIterable<?, ?> mutableMapIterable)
    {
        int actualSize = mutableMapIterable.size();
        if (actualSize != expectedSize)
        {
            fail("Incorrect size for " + mapName + "; expected:<" + expectedSize + "> but was:<" + actualSize + '>');
        }
        int keySetSize = mutableMapIterable.keySet().size();
        if (keySetSize != expectedSize)
        {
            fail("Incorrect size for " + mapName + ".keySet(); expected:<" + expectedSize + "> but was:<" + actualSize + '>');
        }
        int valuesSize = mutableMapIterable.values().size();
        if (valuesSize != expectedSize)
        {
            fail("Incorrect size for " + mapName + ".values(); expected:<" + expectedSize + "> but was:<" + actualSize + '>');
        }
        int entrySetSize = mutableMapIterable.entrySet().size();
        if (entrySetSize != expectedSize)
        {
            fail("Incorrect size for " + mapName + ".entrySet(); expected:<" + expectedSize + "> but was:<" + actualSize + '>');
        }
    }

    /**
     * Assert the size of the given {@link ImmutableSet}.
     */
    public static void assertSize(int expectedSize, ImmutableSet<?> actualImmutableSet)
    {
        Verify.assertSize("immutable set", expectedSize, actualImmutableSet);
    }

    /**
     * Assert the size of the given {@link ImmutableSet}.
     */
    public static void assertSize(String immutableSetName, int expectedSize, ImmutableSet<?> actualImmutableSet)
    {
        int actualSize = actualImmutableSet.size();
        if (actualSize != expectedSize)
        {
            fail("Incorrect size for "
                    + immutableSetName
                    + "; expected:<"
                    + expectedSize
                    + "> but was:<"
                    + actualSize
                    + '>');
        }
    }

    /**
     * Assert that the given {@code stringToFind} is contained within the {@code stringToSearch}.
     */
    public static void assertContains(String stringToFind, String stringToSearch)
    {
        Verify.assertContains("string", stringToFind, stringToSearch);
    }

    /**
     * Assert that the given {@code unexpectedString} is <em>not</em> contained within the {@code stringToSearch}.
     */
    public static void assertNotContains(String unexpectedString, String stringToSearch)
    {
        Verify.assertNotContains("string", unexpectedString, stringToSearch);
    }

    /**
     * Assert that the given {@code stringToFind} is contained within the {@code stringToSearch}.
     */
    public static void assertContains(String stringName, String stringToFind, String stringToSearch)
    {
        assertNotNull("stringToFind should not be null", stringToFind);
        assertNotNull("stringToSearch should not be null", stringToSearch);

        if (!stringToSearch.contains(stringToFind))
        {
            fail(stringName
                    + " did not contain stringToFind:<"
                    + stringToFind
                    + "> in stringToSearch:<"
                    + stringToSearch
                    + '>');
        }
    }

    /**
     * Assert that the given {@code unexpectedString} is <em>not</em> contained within the {@code stringToSearch}.
     */
    public static void assertNotContains(String stringName, String unexpectedString, String stringToSearch)
    {
        assertNotNull("unexpectedString should not be null", unexpectedString);
        assertNotNull("stringToSearch should not be null", stringToSearch);

        if (stringToSearch.contains(unexpectedString))
        {
            fail(stringName
                    + " contains unexpectedString:<"
                    + unexpectedString
                    + "> in stringToSearch:<"
                    + stringToSearch
                    + '>');
        }
    }

    public static <T> void assertCount(
            int expectedCount,
            Iterable<T> iterable,
            Predicate<? super T> predicate)
    {
        assertEquals(expectedCount, Iterate.count(iterable, predicate));
    }

    public static <T> void assertAllSatisfy(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        Verify.assertAllSatisfy("The following items failed to satisfy the condition", iterable, predicate);
    }

    public static <K, V> void assertAllSatisfy(Map<K, V> map, Predicate<? super V> predicate)
    {
        Verify.assertAllSatisfy(map.values(), predicate);
    }

    public static <T> void assertAllSatisfy(String message, Iterable<T> iterable, Predicate<? super T> predicate)
    {
        MutableList<T> unacceptable = Iterate.reject(iterable, predicate, Lists.mutable.of());
        if (unacceptable.notEmpty())
        {
            fail(message + " <" + unacceptable + '>');
        }
    }

    public static <T> void assertAnySatisfy(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        Verify.assertAnySatisfy("No items satisfied the condition", iterable, predicate);
    }

    public static <K, V> void assertAnySatisfy(Map<K, V> map, Predicate<? super V> predicate)
    {
        Verify.assertAnySatisfy(map.values(), predicate);
    }

    public static <T> void assertAnySatisfy(String message, Iterable<T> iterable, Predicate<? super T> predicate)
    {
        assertTrue(message, Predicates.<T>anySatisfy(predicate).accept(iterable));
    }

    public static <T> void assertNoneSatisfy(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        Verify.assertNoneSatisfy("The following items satisfied the condition", iterable, predicate);
    }

    public static <K, V> void assertNoneSatisfy(Map<K, V> map, Predicate<? super V> predicate)
    {
        Verify.assertNoneSatisfy(map.values(), predicate);
    }

    public static <T> void assertNoneSatisfy(String message, Iterable<T> iterable, Predicate<? super T> predicate)
    {
        MutableList<T> unacceptable = Iterate.select(iterable, predicate, Lists.mutable.empty());
        if (unacceptable.notEmpty())
        {
            fail(message + " <" + unacceptable + '>');
        }
    }

    /**
     * Assert that the given {@link Map} contains all the given keys and values.
     */
    public static void assertContainsAllKeyValues(Map<?, ?> actualMap, Object... keyValues)
    {
        Verify.assertContainsAllKeyValues("map", actualMap, keyValues);
    }

    /**
     * Assert that the given {@link Map} contains all the given keys and values.
     */
    public static void assertContainsAllKeyValues(
            String mapName,
            Map<?, ?> actualMap,
            Object... expectedKeyValues)
    {
        Verify.assertNotEmpty("Expected keys/values in assertion", expectedKeyValues);

        if (expectedKeyValues.length % 2 != 0)
        {
            fail("Odd number of keys and values (every key must have a value)");
        }

        Verify.assertObjectNotNull(mapName, actualMap);
        Verify.assertMapContainsKeys(mapName, actualMap, expectedKeyValues);
        Verify.assertMapContainsValues(mapName, actualMap, expectedKeyValues);
    }

    /**
     * Assert that the given {@link MapIterable} contains all the given keys and values.
     */
    public static void assertContainsAllKeyValues(MapIterable<?, ?> mapIterable, Object... keyValues)
    {
        Verify.assertContainsAllKeyValues("map", mapIterable, keyValues);
    }

    /**
     * Assert that the given {@link MapIterable} contains all the given keys and values.
     */
    public static void assertContainsAllKeyValues(
            String mapIterableName,
            MapIterable<?, ?> mapIterable,
            Object... expectedKeyValues)
    {
        Verify.assertNotEmpty("Expected keys/values in assertion", expectedKeyValues);

        if (expectedKeyValues.length % 2 != 0)
        {
            fail("Odd number of keys and values (every key must have a value)");
        }

        Verify.assertObjectNotNull(mapIterableName, mapIterable);
        Verify.assertMapContainsKeys(mapIterableName, mapIterable, expectedKeyValues);
        Verify.assertMapContainsValues(mapIterableName, mapIterable, expectedKeyValues);
    }

    /**
     * Assert that the given {@link MutableMapIterable} contains all the given keys and values.
     */
    public static void assertContainsAllKeyValues(MutableMapIterable<?, ?> mutableMapIterable, Object... keyValues)
    {
        Verify.assertContainsAllKeyValues("map", mutableMapIterable, keyValues);
    }

    /**
     * Assert that the given {@link MutableMapIterable} contains all the given keys and values.
     */
    public static void assertContainsAllKeyValues(
            String mutableMapIterableName,
            MutableMapIterable<?, ?> mutableMapIterable,
            Object... expectedKeyValues)
    {
        Verify.assertNotEmpty("Expected keys/values in assertion", expectedKeyValues);

        if (expectedKeyValues.length % 2 != 0)
        {
            fail("Odd number of keys and values (every key must have a value)");
        }

        Verify.assertObjectNotNull(mutableMapIterableName, mutableMapIterable);
        Verify.assertMapContainsKeys(mutableMapIterableName, mutableMapIterable, expectedKeyValues);
        Verify.assertMapContainsValues(mutableMapIterableName, mutableMapIterable, expectedKeyValues);
    }

    /**
     * Assert that the given {@link ImmutableMapIterable} contains all the given keys and values.
     */
    public static void assertContainsAllKeyValues(ImmutableMapIterable<?, ?> immutableMapIterable, Object... keyValues)
    {
        Verify.assertContainsAllKeyValues("map", immutableMapIterable, keyValues);
    }

    /**
     * Assert that the given {@link ImmutableMapIterable} contains all the given keys and values.
     */
    public static void assertContainsAllKeyValues(
            String immutableMapIterableName,
            ImmutableMapIterable<?, ?> immutableMapIterable,
            Object... expectedKeyValues)
    {
        Verify.assertNotEmpty("Expected keys/values in assertion", expectedKeyValues);

        if (expectedKeyValues.length % 2 != 0)
        {
            fail("Odd number of keys and values (every key must have a value)");
        }

        Verify.assertObjectNotNull(immutableMapIterableName, immutableMapIterable);
        Verify.assertMapContainsKeys(immutableMapIterableName, immutableMapIterable, expectedKeyValues);
        Verify.assertMapContainsValues(immutableMapIterableName, immutableMapIterable, expectedKeyValues);
    }

    public static void denyContainsAny(Collection<?> actualCollection, Object... items)
    {
        Verify.denyContainsAny("collection", actualCollection, items);
    }

    public static void assertContainsNone(Collection<?> actualCollection, Object... items)
    {
        Verify.denyContainsAny("collection", actualCollection, items);
    }

    /**
     * Assert that the given {@link Collection} contains the given item.
     */
    public static void assertContains(Object expectedItem, Collection<?> actualCollection)
    {
        Verify.assertContains("collection", expectedItem, actualCollection);
    }

    /**
     * Assert that the given {@link Collection} contains the given item.
     */
    public static void assertContains(
            String collectionName,
            Object expectedItem,
            Collection<?> actualCollection)
    {
        Verify.assertObjectNotNull(collectionName, actualCollection);

        if (!actualCollection.contains(expectedItem))
        {
            fail(collectionName + " did not contain expectedItem:<" + expectedItem + '>');
        }
    }

    /**
     * Assert that the given {@link ImmutableCollection} contains the given item.
     */
    public static void assertContains(Object expectedItem, ImmutableCollection<?> actualImmutableCollection)
    {
        Verify.assertContains("ImmutableCollection", expectedItem, actualImmutableCollection);
    }

    /**
     * Assert that the given {@link ImmutableCollection} contains the given item.
     */
    public static void assertContains(
            String immutableCollectionName,
            Object expectedItem,
            ImmutableCollection<?> actualImmutableCollection)
    {
        Verify.assertObjectNotNull(immutableCollectionName, actualImmutableCollection);

        if (!actualImmutableCollection.contains(expectedItem))
        {
            fail(immutableCollectionName + " did not contain expectedItem:<" + expectedItem + '>');
        }
    }

    public static void assertContainsAll(
            Iterable<?> iterable,
            Object... items)
    {
        Verify.assertContainsAll("iterable", iterable, items);
    }

    public static void assertContainsAll(
            String collectionName,
            Iterable<?> iterable,
            Object... items)
    {
        Verify.assertObjectNotNull(collectionName, iterable);

        Verify.assertNotEmpty("Expected items in assertion", items);

        Predicate<Object> containsPredicate = each -> Iterate.contains(iterable, each);

        if (!ArrayIterate.allSatisfy(items, containsPredicate))
        {
            ImmutableList<Object> result = Lists.immutable.of(items).newWithoutAll(iterable);
            fail(collectionName + " did not contain these items" + ":<" + result + '>');
        }
    }

    public static void assertListsEqual(List<?> expectedList, List<?> actualList)
    {
        Verify.assertListsEqual("list", expectedList, actualList);
    }

    public static void assertListsEqual(String listName, List<?> expectedList, List<?> actualList)
    {
        if (expectedList == null && actualList == null)
        {
            return;
        }
        assertNotNull(expectedList);
        assertNotNull(actualList);
        assertEquals(listName + " size", expectedList.size(), actualList.size());
        for (int index = 0; index < actualList.size(); index++)
        {
            Object eachExpected = expectedList.get(index);
            Object eachActual = actualList.get(index);
            if (!Objects.equals(eachExpected, eachActual))
            {
                junit.framework.Assert.failNotEquals(listName + " first differed at element [" + index + "];", eachExpected, eachActual);
            }
        }
    }

    public static void assertSetsEqual(Set<?> expectedSet, Set<?> actualSet)
    {
        Verify.assertSetsEqual("set", expectedSet, actualSet);
    }

    public static void assertSetsEqual(String setName, Set<?> expectedSet, Set<?> actualSet)
    {
        if (expectedSet == null)
        {
            assertNull(setName + " should be null", actualSet);
            return;
        }

        Verify.assertObjectNotNull(setName, actualSet);
        Verify.assertSize(setName, expectedSet.size(), actualSet);

        if (!actualSet.equals(expectedSet))
        {
            MutableSet<?> inExpectedOnlySet = UnifiedSet.newSet(expectedSet);
            inExpectedOnlySet.removeAll(actualSet);

            int numberDifferences = inExpectedOnlySet.size();
            String message = setName + ": " + numberDifferences + " elements different.";

            if (numberDifferences > MAX_DIFFERENCES)
            {
                fail(message);
            }

            MutableSet<?> inActualOnlySet = UnifiedSet.newSet(actualSet);
            inActualOnlySet.removeAll(expectedSet);

            //noinspection UseOfObsoleteAssert
            junit.framework.Assert.failNotEquals(message, inExpectedOnlySet, inActualOnlySet);
        }
    }

    public static void assertSortedSetsEqual(SortedSet<?> expectedSet, SortedSet<?> actualSet)
    {
        Verify.assertSortedSetsEqual("sortedSets", expectedSet, actualSet);
    }

    public static void assertSortedSetsEqual(String setName, SortedSet<?> expectedSet, SortedSet<?> actualSet)
    {
        assertEquals(setName, expectedSet, actualSet);
        Verify.assertIterablesEqual(setName, expectedSet, actualSet);
    }

    public static void assertSortedBagsEqual(SortedBag<?> expectedBag, SortedBag<?> actualBag)
    {
        Verify.assertSortedBagsEqual("sortedBags", expectedBag, actualBag);
    }

    public static void assertSortedBagsEqual(String bagName, SortedBag<?> expectedBag, SortedBag<?> actualBag)
    {
        assertEquals(bagName, expectedBag, actualBag);
        Verify.assertIterablesEqual(bagName, expectedBag, actualBag);
    }

    public static void assertSortedMapsEqual(SortedMapIterable<?, ?> expectedMap, SortedMapIterable<?, ?> actualMap)
    {
        Verify.assertSortedMapsEqual("sortedMaps", expectedMap, actualMap);
    }

    public static void assertSortedMapsEqual(String mapName, SortedMapIterable<?, ?> expectedMap, SortedMapIterable<?, ?> actualMap)
    {
        assertEquals(mapName, expectedMap, actualMap);
        Verify.assertIterablesEqual(mapName, expectedMap, actualMap);
    }

    public static void assertIterablesEqual(Iterable<?> expectedIterable, Iterable<?> actualIterable)
    {
        Verify.assertIterablesEqual("iterables", expectedIterable, actualIterable);
    }

    public static void assertIterablesEqual(String iterableName, Iterable<?> expectedIterable, Iterable<?> actualIterable)
    {
        if (expectedIterable == null)
        {
            assertNull(iterableName + " should be null", actualIterable);
            return;
        }

        Verify.assertObjectNotNull(iterableName, actualIterable);

        if (expectedIterable instanceof InternalIterable<?> expectedInternalIterable && actualIterable instanceof InternalIterable<?> actualInternalIterable)
        {
            MutableList<Object> expectedList = FastList.newList();
            MutableList<Object> actualList = FastList.newList();
            expectedInternalIterable.forEach(CollectionAddProcedure.on(expectedList));
            actualInternalIterable.forEach(CollectionAddProcedure.on(actualList));
            Verify.assertListsEqual(iterableName, expectedList, actualList);
        }
        else
        {
            Iterator<?> expectedIterator = expectedIterable.iterator();
            Iterator<?> actualIterator = actualIterable.iterator();
            int index = 0;

            while (expectedIterator.hasNext() && actualIterator.hasNext())
            {
                Object eachExpected = expectedIterator.next();
                Object eachActual = actualIterator.next();

                if (!Objects.equals(eachExpected, eachActual))
                {
                    //noinspection UseOfObsoleteAssert
                    junit.framework.Assert.failNotEquals(iterableName + " first differed at element [" + index + "];", eachExpected, eachActual);
                }
                index++;
            }

            assertFalse("Actual " + iterableName + " had " + index + " elements but expected " + iterableName + " had more.", expectedIterator.hasNext());
            assertFalse("Expected " + iterableName + " had " + index + " elements but actual " + iterableName + " had more.", actualIterator.hasNext());
        }
    }

    public static void assertMapsEqual(Map<?, ?> expectedMap, Map<?, ?> actualMap)
    {
        Verify.assertMapsEqual("map", expectedMap, actualMap);
    }

    public static void assertMapsEqual(String mapName, Map<?, ?> expectedMap, Map<?, ?> actualMap)
    {
        if (expectedMap == null)
        {
            assertNull(mapName + " should be null", actualMap);
            return;
        }

        assertNotNull(mapName + " should not be null", actualMap);

        Set<? extends Map.Entry<?, ?>> expectedEntries = expectedMap.entrySet();
        expectedEntries.forEach(expectedEntry ->
        {
            Object expectedKey = expectedEntry.getKey();
            Object expectedValue = expectedEntry.getValue();
            Object actualValue = actualMap.get(expectedKey);
            if (!Objects.equals(actualValue, expectedValue))
            {
                fail("Values differ at key " + expectedKey + " expected " + expectedValue + " but was " + actualValue);
            }
        });
        Verify.assertSetsEqual(mapName + " keys", expectedMap.keySet(), actualMap.keySet());
        Verify.assertSetsEqual(mapName + " entries", expectedMap.entrySet(), actualMap.entrySet());
    }

    public static void assertBagsEqual(Bag<?> expectedBag, Bag<?> actualBag)
    {
        Verify.assertBagsEqual("bag", expectedBag, actualBag);
    }

    public static void assertBagsEqual(String bagName, Bag<?> expectedBag, Bag<?> actualBag)
    {
        if (expectedBag == null)
        {
            assertNull(bagName + " should be null", actualBag);
            return;
        }

        assertNotNull(bagName + " should not be null", actualBag);

        assertEquals(bagName + " size", expectedBag.size(), actualBag.size());
        assertEquals(bagName + " sizeDistinct", expectedBag.sizeDistinct(), actualBag.sizeDistinct());

        expectedBag.forEachWithOccurrences((expectedKey, expectedValue) ->
        {
            int actualValue = actualBag.occurrencesOf(expectedKey);
            assertEquals("Occurrences of " + expectedKey, expectedValue, actualValue);
        });
    }

    public static <K, V> void assertListMultimapsEqual(ListMultimap<K, V> expectedListMultimap, ListMultimap<K, V> actualListMultimap)
    {
        Verify.assertListMultimapsEqual("ListMultimap", expectedListMultimap, actualListMultimap);
    }

    public static <K, V> void assertListMultimapsEqual(String multimapName, ListMultimap<K, V> expectedListMultimap, ListMultimap<K, V> actualListMultimap)
    {
        if (expectedListMultimap == null)
        {
            assertNull(multimapName + " should be null", actualListMultimap);
            return;
        }

        assertNotNull(multimapName + " should not be null", actualListMultimap);

        assertEquals(multimapName + " size", expectedListMultimap.size(), actualListMultimap.size());
        Verify.assertBagsEqual(multimapName + " keyBag", expectedListMultimap.keyBag(), actualListMultimap.keyBag());

        for (K key : expectedListMultimap.keysView())
        {
            Verify.assertListsEqual(multimapName + " value list for key:" + key, (List<V>) expectedListMultimap.get(key), (List<V>) actualListMultimap.get(key));
        }
        assertEquals(multimapName, expectedListMultimap, actualListMultimap);
    }

    public static <K, V> void assertSetMultimapsEqual(SetMultimap<K, V> expectedSetMultimap, SetMultimap<K, V> actualSetMultimap)
    {
        Verify.assertSetMultimapsEqual("SetMultimap", expectedSetMultimap, actualSetMultimap);
    }

    public static <K, V> void assertSetMultimapsEqual(String multimapName, SetMultimap<K, V> expectedSetMultimap, SetMultimap<K, V> actualSetMultimap)
    {
        if (expectedSetMultimap == null)
        {
            assertNull(multimapName + " should be null", actualSetMultimap);
            return;
        }

        assertNotNull(multimapName + " should not be null", actualSetMultimap);

        assertEquals(multimapName + " size", expectedSetMultimap.size(), actualSetMultimap.size());
        Verify.assertBagsEqual(multimapName + " keyBag", expectedSetMultimap.keyBag(), actualSetMultimap.keyBag());

        for (K key : expectedSetMultimap.keysView())
        {
            Verify.assertSetsEqual(multimapName + " value set for key:" + key, (Set<V>) expectedSetMultimap.get(key), (Set<V>) actualSetMultimap.get(key));
        }
        assertEquals(multimapName, expectedSetMultimap, actualSetMultimap);
    }

    public static <K, V> void assertBagMultimapsEqual(BagMultimap<K, V> expectedBagMultimap, BagMultimap<K, V> actualBagMultimap)
    {
        Verify.assertBagMultimapsEqual("BagMultimap", expectedBagMultimap, actualBagMultimap);
    }

    public static <K, V> void assertBagMultimapsEqual(String multimapName, BagMultimap<K, V> expectedBagMultimap, BagMultimap<K, V> actualBagMultimap)
    {
        if (expectedBagMultimap == null)
        {
            assertNull(multimapName + " should be null", actualBagMultimap);
            return;
        }

        assertNotNull(multimapName + " should not be null", actualBagMultimap);

        assertEquals(multimapName + " size", expectedBagMultimap.size(), actualBagMultimap.size());
        Verify.assertBagsEqual(multimapName + " keyBag", expectedBagMultimap.keyBag(), actualBagMultimap.keyBag());

        for (K key : expectedBagMultimap.keysView())
        {
            Verify.assertBagsEqual(multimapName + " value bag for key:" + key, expectedBagMultimap.get(key), actualBagMultimap.get(key));
        }
        assertEquals(multimapName, expectedBagMultimap, actualBagMultimap);
    }

    public static <K, V> void assertSortedSetMultimapsEqual(SortedSetMultimap<K, V> expectedSortedSetMultimap, SortedSetMultimap<K, V> actualSortedSetMultimap)
    {
        Verify.assertSortedSetMultimapsEqual("SortedSetMultimap", expectedSortedSetMultimap, actualSortedSetMultimap);
    }

    public static <K, V> void assertSortedSetMultimapsEqual(String multimapName, SortedSetMultimap<K, V> expectedSortedSetMultimap, SortedSetMultimap<K, V> actualSortedSetMultimap)
    {
        if (expectedSortedSetMultimap == null)
        {
            assertNull(multimapName + " should be null", actualSortedSetMultimap);
            return;
        }

        assertNotNull(multimapName + " should not be null", actualSortedSetMultimap);

        assertEquals(multimapName + " size", expectedSortedSetMultimap.size(), actualSortedSetMultimap.size());
        Verify.assertBagsEqual(multimapName + " keyBag", expectedSortedSetMultimap.keyBag(), actualSortedSetMultimap.keyBag());

        for (K key : expectedSortedSetMultimap.keysView())
        {
            Verify.assertSortedSetsEqual(multimapName + " value set for key:" + key, (SortedSet<V>) expectedSortedSetMultimap.get(key), (SortedSet<V>) actualSortedSetMultimap.get(key));
        }
        assertEquals(multimapName, expectedSortedSetMultimap, actualSortedSetMultimap);
    }

    public static <K, V> void assertSortedBagMultimapsEqual(SortedBagMultimap<K, V> expectedSortedBagMultimap, SortedBagMultimap<K, V> actualSortedBagMultimap)
    {
        Verify.assertSortedBagMultimapsEqual("SortedBagMultimap", expectedSortedBagMultimap, actualSortedBagMultimap);
    }

    public static <K, V> void assertSortedBagMultimapsEqual(String multimapName, SortedBagMultimap<K, V> expectedSortedBagMultimap, SortedBagMultimap<K, V> actualSortedBagMultimap)
    {
        if (expectedSortedBagMultimap == null)
        {
            assertNull(multimapName + " should be null", actualSortedBagMultimap);
            return;
        }

        assertNotNull(multimapName + " should not be null", actualSortedBagMultimap);

        assertEquals(multimapName + " size", expectedSortedBagMultimap.size(), actualSortedBagMultimap.size());
        Verify.assertBagsEqual(multimapName + " keyBag", expectedSortedBagMultimap.keyBag(), actualSortedBagMultimap.keyBag());

        for (K key : expectedSortedBagMultimap.keysView())
        {
            Verify.assertSortedBagsEqual(multimapName + " value set for key:" + key, expectedSortedBagMultimap.get(key), actualSortedBagMultimap.get(key));
        }
        assertEquals(multimapName, expectedSortedBagMultimap, actualSortedBagMultimap);
    }

    private static void assertMapContainsKeys(
            String mapName,
            Map<?, ?> actualMap,
            Object... expectedKeyValues)
    {
        Verify.assertNotEmpty("Expected keys/values in assertion", expectedKeyValues);

        MutableList<Object> expectedKeys = Lists.mutable.of();
        for (int i = 0; i < expectedKeyValues.length; i += 2)
        {
            expectedKeys.add(expectedKeyValues[i]);
        }

        Verify.assertContainsAll(mapName + ".keySet()", actualMap.keySet(), expectedKeys.toArray());
    }

    private static void assertMapContainsValues(
            String mapName,
            Map<?, ?> actualMap,
            Object... expectedKeyValues)
    {
        Verify.assertNotEmpty("Expected keys/values in assertion", expectedKeyValues);

        MutableMap<Object, String> missingEntries = Maps.mutable.empty();
        int i = 0;
        while (i < expectedKeyValues.length)
        {
            Object expectedKey = expectedKeyValues[i++];
            Object expectedValue = expectedKeyValues[i++];
            Object actualValue = actualMap.get(expectedKey);
            if (!Objects.equals(expectedValue, actualValue))
            {
                missingEntries.put(
                        expectedKey,
                        "expectedValue:<" + expectedValue + ">, actualValue:<" + actualValue + '>');
            }
        }
        if (!missingEntries.isEmpty())
        {
            StringBuilder buf = new StringBuilder(mapName + " has incorrect values for keys:[");
            for (Map.Entry<Object, String> expectedEntry : missingEntries.entrySet())
            {
                buf.append("key:<")
                        .append(expectedEntry.getKey())
                        .append(',')
                        .append(expectedEntry.getValue())
                        .append("> ");
            }
            buf.append(']');
            fail(buf.toString());
        }
    }

    private static void assertMapContainsKeys(
            String mapIterableName,
            MapIterable<?, ?> mapIterable,
            Object... expectedKeyValues)
    {
        Verify.assertNotEmpty("Expected keys/values in assertion", expectedKeyValues);

        MutableList<Object> expectedKeys = Lists.mutable.of();
        for (int i = 0; i < expectedKeyValues.length; i += 2)
        {
            expectedKeys.add(expectedKeyValues[i]);
        }

        Verify.assertContainsAll(mapIterableName + ".keysView()", mapIterable.keysView(), expectedKeys.toArray());
    }

    private static void assertMapContainsValues(
            String mapIterableName,
            MapIterable<?, ?> mapIterable,
            Object... expectedKeyValues)
    {
        Verify.assertNotEmpty("Expected keys/values in assertion", expectedKeyValues);

        MutableList<Object> expectedValues = Lists.mutable.of();
        for (int i = 1; i < expectedKeyValues.length; i += 2)
        {
            expectedValues.add(expectedKeyValues[i]);
        }

        Verify.assertContainsAll(mapIterableName + ".valuesView()", mapIterable.valuesView(), expectedValues.toArray());
    }

    private static void assertMapContainsKeys(
            String mutableMapIterableName,
            MutableMapIterable<?, ?> mutableMapIterable,
            Object... expectedKeyValues)
    {
        Verify.assertNotEmpty("Expected keys/values in assertion", expectedKeyValues);

        MutableList<Object> expectedKeys = Lists.mutable.of();
        for (int i = 0; i < expectedKeyValues.length; i += 2)
        {
            expectedKeys.add(expectedKeyValues[i]);
        }

        Verify.assertContainsAll(mutableMapIterableName + ".keysView()", mutableMapIterable.keysView(), expectedKeys.toArray());
    }

    private static void assertMapContainsValues(
            String mutableMapIterableName,
            MutableMapIterable<?, ?> mutableMapIterable,
            Object... expectedKeyValues)
    {
        Verify.assertNotEmpty("Expected keys/values in assertion", expectedKeyValues);

        MutableList<Object> expectedValues = Lists.mutable.of();
        for (int i = 1; i < expectedKeyValues.length; i += 2)
        {
            expectedValues.add(expectedKeyValues[i]);
        }

        Verify.assertContainsAll(mutableMapIterableName + ".valuesView()", mutableMapIterable.valuesView(), expectedValues.toArray());
    }

    private static void assertMapContainsKeys(
            String immutableMapIterableName,
            ImmutableMapIterable<?, ?> immutableMapIterable,
            Object... expectedKeyValues)
    {
        Verify.assertNotEmpty("Expected keys/values in assertion", expectedKeyValues);

        MutableList<Object> expectedKeys = Lists.mutable.of();
        for (int i = 0; i < expectedKeyValues.length; i += 2)
        {
            expectedKeys.add(expectedKeyValues[i]);
        }

        Verify.assertContainsAll(immutableMapIterableName + ".keysView()", immutableMapIterable.keysView(), expectedKeys.toArray());
    }

    private static void assertMapContainsValues(
            String immutableMapIterableName,
            ImmutableMapIterable<?, ?> immutableMapIterable,
            Object... expectedKeyValues)
    {
        Verify.assertNotEmpty("Expected keys/values in assertion", expectedKeyValues);

        MutableList<Object> expectedValues = Lists.mutable.of();
        for (int i = 1; i < expectedKeyValues.length; i += 2)
        {
            expectedValues.add(expectedKeyValues[i]);
        }

        Verify.assertContainsAll(immutableMapIterableName + ".valuesView()", immutableMapIterable.valuesView(), expectedValues.toArray());
    }

    /**
     * Assert that the given {@link Multimap} contains an entry with the given key and value.
     */
    public static <K, V> void assertContainsEntry(
            K expectedKey,
            V expectedValue,
            Multimap<K, V> actualMultimap)
    {
        Verify.assertContainsEntry("multimap", expectedKey, expectedValue, actualMultimap);
    }

    /**
     * Assert that the given {@link Multimap} contains an entry with the given key and value.
     */
    public static <K, V> void assertContainsEntry(
            String multimapName,
            K expectedKey,
            V expectedValue,
            Multimap<K, V> actualMultimap)
    {
        assertNotNull(multimapName, actualMultimap);

        if (!actualMultimap.containsKeyAndValue(expectedKey, expectedValue))
        {
            fail(multimapName + " did not contain entry: <" + expectedKey + ", " + expectedValue + '>');
        }
    }

    /**
     * Assert the given {@link Multimap} contains all the given keys and values.
     */
    public static void assertContainsAllEntries(Multimap<?, ?> actualMultimap, Object... keyValues)
    {
        Verify.assertContainsAllEntries("multimap", actualMultimap, keyValues);
    }

    /**
     * Assert the given {@link Multimap} contains all the given keys and values.
     */
    public static void assertContainsAllEntries(
            String multimapName,
            Multimap<?, ?> actualMultimap,
            Object... expectedKeyValues)
    {
        Verify.assertNotEmpty("Expected keys/values in assertion", expectedKeyValues);

        if (expectedKeyValues.length % 2 != 0)
        {
            fail("Odd number of keys and values (every key must have a value)");
        }

        Verify.assertObjectNotNull(multimapName, actualMultimap);

        MutableList<Map.Entry<?, ?>> missingEntries = Lists.mutable.of();
        for (int i = 0; i < expectedKeyValues.length; i += 2)
        {
            Object expectedKey = expectedKeyValues[i];
            Object expectedValue = expectedKeyValues[i + 1];

            if (!actualMultimap.containsKeyAndValue(expectedKey, expectedValue))
            {
                missingEntries.add(new ImmutableEntry<>(expectedKey, expectedValue));
            }
        }

        if (!missingEntries.isEmpty())
        {
            fail(multimapName + " is missing entries: " + missingEntries);
        }
    }

    public static void denyContainsAny(
            String collectionName,
            Collection<?> actualCollection,
            Object... items)
    {
        Verify.assertNotEmpty("Expected items in assertion", items);

        Verify.assertObjectNotNull(collectionName, actualCollection);

        MutableSet<Object> intersection = Sets.intersect(UnifiedSet.newSet(actualCollection), UnifiedSet.newSetWith(items));
        if (intersection.notEmpty())
        {
            fail(collectionName
                    + " has an intersection with these items and should not :<" + intersection + '>');
        }
    }

    /**
     * Assert that the given {@link Map} contains an entry with the given key.
     */
    public static void assertContainsKey(Object expectedKey, Map<?, ?> actualMap)
    {
        Verify.assertContainsKey("map", expectedKey, actualMap);
    }

    /**
     * Assert that the given {@link Map} contains an entry with the given key.
     */
    public static void assertContainsKey(String mapName, Object expectedKey, Map<?, ?> actualMap)
    {
        assertNotNull(mapName, actualMap);

        if (!actualMap.containsKey(expectedKey))
        {
            fail(mapName + " did not contain expectedKey:<" + expectedKey + '>');
        }
    }

    /**
     * Assert that the given {@link MapIterable} contains an entry with the given key.
     */
    public static void assertContainsKey(Object expectedKey, MapIterable<?, ?> mapIterable)
    {
        Verify.assertContainsKey("map", expectedKey, mapIterable);
    }

    /**
     * Assert that the given {@link MapIterable} contains an entry with the given key.
     */
    public static void assertContainsKey(
            String mapIterableName,
            Object expectedKey,
            MapIterable<?, ?> mapIterable)
    {
        assertNotNull(mapIterableName, mapIterable);

        if (!mapIterable.containsKey(expectedKey))
        {
            fail(mapIterableName + " did not contain expectedKey:<" + expectedKey + '>');
        }
    }

    /**
     * Assert that the given {@link MutableMapIterable} contains an entry with the given key.
     */
    public static void assertContainsKey(Object expectedKey, MutableMapIterable<?, ?> mutableMapIterable)
    {
        Verify.assertContainsKey("map", expectedKey, mutableMapIterable);
    }

    /**
     * Assert that the given {@link MutableMapIterable} contains an entry with the given key.
     */
    public static void assertContainsKey(
            String mutableMapIterableName,
            Object expectedKey,
            MutableMapIterable<?, ?> mutableMapIterable)
    {
        assertNotNull(mutableMapIterableName, mutableMapIterable);

        if (!mutableMapIterable.containsKey(expectedKey))
        {
            fail(mutableMapIterableName + " did not contain expectedKey:<" + expectedKey + '>');
        }
    }

    /**
     * Assert that the given {@link ImmutableMapIterable} contains an entry with the given key.
     */
    public static void assertContainsKey(Object expectedKey, ImmutableMapIterable<?, ?> immutableMapIterable)
    {
        Verify.assertContainsKey("map", expectedKey, immutableMapIterable);
    }

    /**
     * Assert that the given {@link ImmutableMapIterable} contains an entry with the given key.
     */
    public static void assertContainsKey(
            String immutableMapIterableName,
            Object expectedKey,
            ImmutableMapIterable<?, ?> immutableMapIterable)
    {
        assertNotNull(immutableMapIterableName, immutableMapIterable);

        if (!immutableMapIterable.containsKey(expectedKey))
        {
            fail(immutableMapIterableName + " did not contain expectedKey:<" + expectedKey + '>');
        }
    }

    /**
     * Deny that the given {@link Map} contains an entry with the given key.
     */
    public static void denyContainsKey(Object unexpectedKey, Map<?, ?> actualMap)
    {
        Verify.denyContainsKey("map", unexpectedKey, actualMap);
    }

    /**
     * Deny that the given {@link Map} contains an entry with the given key.
     */
    public static void denyContainsKey(String mapName, Object unexpectedKey, Map<?, ?> actualMap)
    {
        assertNotNull(mapName, actualMap);

        if (actualMap.containsKey(unexpectedKey))
        {
            fail(mapName + " contained unexpectedKey:<" + unexpectedKey + '>');
        }
    }

    /**
     * Assert that the given {@link Map} contains an entry with the given key and value.
     */
    public static void assertContainsKeyValue(
            Object expectedKey,
            Object expectedValue,
            Map<?, ?> actualMap)
    {
        Verify.assertContainsKeyValue("map", expectedKey, expectedValue, actualMap);
    }

    /**
     * Assert that the given {@link Map} contains an entry with the given key and value.
     */
    public static void assertContainsKeyValue(
            String mapName,
            Object expectedKey,
            Object expectedValue,
            Map<?, ?> actualMap)
    {
        Verify.assertContainsKey(mapName, expectedKey, actualMap);

        Object actualValue = actualMap.get(expectedKey);
        if (!Objects.equals(actualValue, expectedValue))
        {
            fail(
                    mapName
                            + " entry with expectedKey:<"
                            + expectedKey
                            + "> "
                            + "did not contain expectedValue:<"
                            + expectedValue
                            + ">, "
                            + "but had actualValue:<"
                            + actualValue
                            + '>');
        }
    }

    /**
     * Assert that the given {@link MapIterable} contains an entry with the given key and value.
     */
    public static void assertContainsKeyValue(
            Object expectedKey,
            Object expectedValue,
            MapIterable<?, ?> mapIterable)
    {
        Verify.assertContainsKeyValue("map", expectedKey, expectedValue, mapIterable);
    }

    /**
     * Assert that the given {@link MapIterable} contains an entry with the given key and value.
     */
    public static void assertContainsKeyValue(
            String mapIterableName,
            Object expectedKey,
            Object expectedValue,
            MapIterable<?, ?> mapIterable)
    {
        Verify.assertContainsKey(mapIterableName, expectedKey, mapIterable);

        Object actualValue = mapIterable.get(expectedKey);
        if (!Objects.equals(actualValue, expectedValue))
        {
            fail(
                    mapIterableName
                            + " entry with expectedKey:<"
                            + expectedKey
                            + "> "
                            + "did not contain expectedValue:<"
                            + expectedValue
                            + ">, "
                            + "but had actualValue:<"
                            + actualValue
                            + '>');
        }
    }

    /**
     * Assert that the given {@link MutableMapIterable} contains an entry with the given key and value.
     */
    public static void assertContainsKeyValue(
            Object expectedKey,
            Object expectedValue,
            MutableMapIterable<?, ?> mapIterable)
    {
        Verify.assertContainsKeyValue("map", expectedKey, expectedValue, mapIterable);
    }

    /**
     * Assert that the given {@link MutableMapIterable} contains an entry with the given key and value.
     */
    public static void assertContainsKeyValue(
            String mapIterableName,
            Object expectedKey,
            Object expectedValue,
            MutableMapIterable<?, ?> mutableMapIterable)
    {
        Verify.assertContainsKey(mapIterableName, expectedKey, mutableMapIterable);

        Object actualValue = mutableMapIterable.get(expectedKey);
        if (!Objects.equals(actualValue, expectedValue))
        {
            fail(
                    mapIterableName
                            + " entry with expectedKey:<"
                            + expectedKey
                            + "> "
                            + "did not contain expectedValue:<"
                            + expectedValue
                            + ">, "
                            + "but had actualValue:<"
                            + actualValue
                            + '>');
        }
    }

    /**
     * Assert that the given {@link ImmutableMapIterable} contains an entry with the given key and value.
     */
    public static void assertContainsKeyValue(
            Object expectedKey,
            Object expectedValue,
            ImmutableMapIterable<?, ?> mapIterable)
    {
        Verify.assertContainsKeyValue("map", expectedKey, expectedValue, mapIterable);
    }

    /**
     * Assert that the given {@link ImmutableMapIterable} contains an entry with the given key and value.
     */
    public static void assertContainsKeyValue(
            String mapIterableName,
            Object expectedKey,
            Object expectedValue,
            ImmutableMapIterable<?, ?> immutableMapIterable)
    {
        Verify.assertContainsKey(mapIterableName, expectedKey, immutableMapIterable);

        Object actualValue = immutableMapIterable.get(expectedKey);
        if (!Objects.equals(actualValue, expectedValue))
        {
            fail(
                    mapIterableName
                            + " entry with expectedKey:<"
                            + expectedKey
                            + "> "
                            + "did not contain expectedValue:<"
                            + expectedValue
                            + ">, "
                            + "but had actualValue:<"
                            + actualValue
                            + '>');
        }
    }

    /**
     * Assert that the given {@link Collection} does <em>not</em> contain the given item.
     */
    public static void assertNotContains(Object unexpectedItem, Collection<?> actualCollection)
    {
        Verify.assertNotContains("collection", unexpectedItem, actualCollection);
    }

    /**
     * Assert that the given {@link Collection} does <em>not</em> contain the given item.
     */
    public static void assertNotContains(
            String collectionName,
            Object unexpectedItem,
            Collection<?> actualCollection)
    {
        Verify.assertObjectNotNull(collectionName, actualCollection);

        if (actualCollection.contains(unexpectedItem))
        {
            fail(collectionName + " should not contain unexpectedItem:<" + unexpectedItem + '>');
        }
    }

    /**
     * Assert that the given {@link Iterable} does <em>not</em> contain the given item.
     */
    public static void assertNotContains(Object unexpectedItem, Iterable<?> iterable)
    {
        Verify.assertNotContains("iterable", unexpectedItem, iterable);
    }

    /**
     * Assert that the given {@link Iterable} does <em>not</em> contain the given item.
     */
    public static void assertNotContains(
            String collectionName,
            Object unexpectedItem,
            Iterable<?> iterable)
    {
        Verify.assertObjectNotNull(collectionName, iterable);

        if (Iterate.contains(iterable, unexpectedItem))
        {
            fail(collectionName + " should not contain unexpectedItem:<" + unexpectedItem + '>');
        }
    }

    /**
     * Assert that the given {@link Collection} does <em>not</em> contain the given item.
     */
    public static void assertNotContainsKey(Object unexpectedKey, Map<?, ?> actualMap)
    {
        Verify.assertNotContainsKey("map", unexpectedKey, actualMap);
    }

    /**
     * Assert that the given {@link Collection} does <em>not</em> contain the given item.
     */
    public static void assertNotContainsKey(String mapName, Object unexpectedKey, Map<?, ?> actualMap)
    {
        Verify.assertObjectNotNull(mapName, actualMap);

        if (actualMap.containsKey(unexpectedKey))
        {
            fail(mapName + " should not contain unexpectedItem:<" + unexpectedKey + '>');
        }
    }

    /**
     * Assert that the formerItem appears before the latterItem in the given {@link Collection}.
     * Both the formerItem and the latterItem must appear in the collection, or this assert will fail.
     */
    public static void assertBefore(Object formerItem, Object latterItem, List<?> actualList)
    {
        Verify.assertBefore("list", formerItem, latterItem, actualList);
    }

    /**
     * Assert that the formerItem appears before the latterItem in the given {@link Collection}.
     * {@link #assertContains(String, Object, Collection)} will be called for both the formerItem and the
     * latterItem, prior to the "before" assertion.
     */
    public static void assertBefore(
            String listName,
            Object formerItem,
            Object latterItem,
            List<?> actualList)
    {
        Verify.assertObjectNotNull(listName, actualList);
        assertNotEquals(
                "Bad test, formerItem and latterItem are equal, listName:<" + listName + '>',
                formerItem,
                latterItem);
        Verify.assertContainsAll(listName, actualList, formerItem, latterItem);
        int formerPosition = actualList.indexOf(formerItem);
        int latterPosition = actualList.indexOf(latterItem);
        if (latterPosition < formerPosition)
        {
            fail("Items in "
                    + listName
                    + " are in incorrect order; "
                    + "expected formerItem:<"
                    + formerItem
                    + "> "
                    + "to appear before latterItem:<"
                    + latterItem
                    + ">, but didn't");
        }
    }

    public static void assertObjectNotNull(String objectName, Object actualObject)
    {
        assertNotNull(objectName + " should not be null", actualObject);
    }

    /**
     * Assert that the given {@code item} is at the {@code index} in the given {@link List}.
     */
    public static void assertItemAtIndex(Object expectedItem, int index, List<?> list)
    {
        Verify.assertItemAtIndex("list", expectedItem, index, list);
    }

    /**
     * Assert that the given {@code item} is at the {@code index} in the given {@code array}.
     */
    public static void assertItemAtIndex(Object expectedItem, int index, Object[] array)
    {
        Verify.assertItemAtIndex("array", expectedItem, index, array);
    }

    public static <T> void assertStartsWith(T[] array, T... items)
    {
        Verify.assertNotEmpty("Expected items in assertion", items);

        for (int i = 0; i < items.length; i++)
        {
            T item = items[i];
            Verify.assertItemAtIndex("array", item, i, array);
        }
    }

    public static <T> void assertStartsWith(List<T> list, T... items)
    {
        Verify.assertStartsWith("list", list, items);
    }

    public static <T> void assertStartsWith(String listName, List<T> list, T... items)
    {
        Verify.assertNotEmpty("Expected items in assertion", items);

        for (int i = 0; i < items.length; i++)
        {
            T item = items[i];
            Verify.assertItemAtIndex(listName, item, i, list);
        }
    }

    public static <T> void assertEndsWith(List<T> list, T... items)
    {
        Verify.assertNotEmpty("Expected items in assertion", items);

        for (int i = 0; i < items.length; i++)
        {
            T item = items[i];
            Verify.assertItemAtIndex("list", item, list.size() - items.length + i, list);
        }
    }

    public static <T> void assertEndsWith(T[] array, T... items)
    {
        Verify.assertNotEmpty("Expected items in assertion", items);

        for (int i = 0; i < items.length; i++)
        {
            T item = items[i];
            Verify.assertItemAtIndex("array", item, array.length - items.length + i, array);
        }
    }

    /**
     * Assert that the given {@code item} is at the {@code index} in the given {@link List}.
     */
    public static void assertItemAtIndex(
            String listName,
            Object expectedItem,
            int index,
            List<?> list)
    {
        Verify.assertObjectNotNull(listName, list);

        Object actualItem = list.get(index);
        if (!Objects.equals(expectedItem, actualItem))
        {
            assertEquals(
                    listName + " has incorrect element at index:<" + index + '>',
                    expectedItem,
                    actualItem);
        }
    }

    /**
     * Assert that the given {@code item} is at the {@code index} in the given {@link List}.
     */
    public static void assertItemAtIndex(
            String arrayName,
            Object expectedItem,
            int index,
            Object[] array)
    {
        assertNotNull(array);
        Object actualItem = array[index];
        if (!Objects.equals(expectedItem, actualItem))
        {
            assertEquals(
                    arrayName + " has incorrect element at index:<" + index + '>',
                    expectedItem,
                    actualItem);
        }
    }

    public static void assertPostSerializedEqualsAndHashCode(Object object)
    {
        Object deserialized = SerializeTestHelper.serializeDeserialize(object);
        Verify.assertEqualsAndHashCode("objects", object, deserialized);
        assertNotSame("not same object", object, deserialized);
    }

    public static void assertPostSerializedEqualsHashCodeAndToString(Object object)
    {
        Object deserialized = SerializeTestHelper.serializeDeserialize(object);
        Verify.assertEqualsAndHashCode("objects", object, deserialized);
        assertNotSame("not same object", object, deserialized);
        assertEquals("not same toString", object.toString(), deserialized.toString());
    }

    public static void assertPostSerializedIdentity(Object object)
    {
        Object deserialized = SerializeTestHelper.serializeDeserialize(object);
        Verify.assertEqualsAndHashCode("objects", object, deserialized);
        assertSame("same object", object, deserialized);
    }

    public static void assertSerializedForm(String expectedBase64Form, Object actualObject)
    {
        Verify.assertInstanceOf(Serializable.class, actualObject);
        assertEquals(
                "Serialization was broken.",
                Verify.relocatedExpectedForm(expectedBase64Form),
                Verify.encodeObject(actualObject));
    }

    public static void assertSerializedForm(
            long expectedSerialVersionUID,
            String expectedBase64Form,
            Object actualObject)
    {
        Verify.assertInstanceOf(Serializable.class, actualObject);

        assertEquals(
                "Serialization was broken.",
                Verify.relocatedExpectedForm(expectedBase64Form),
                Verify.encodeObject(actualObject));

        Object decodeToObject = Verify.decodeObject(expectedBase64Form);

        assertEquals(
                "serialVersionUID's differ",
                expectedSerialVersionUID,
                ObjectStreamClass.lookup(decodeToObject.getClass()).getSerialVersionUID());
    }

    public static void assertDeserializedForm(String expectedBase64Form, Object actualObject)
    {
        Verify.assertInstanceOf(Serializable.class, actualObject);

        Object decodeToObject = Verify.decodeObject(expectedBase64Form);
        assertEquals("Serialization was broken.", decodeToObject, actualObject);
    }

    private static Object decodeObject(String expectedBase64Form)
    {
        try
        {
            byte[] bytes = DECODER.decode(Verify.relocatedExpectedForm(expectedBase64Form));
            return new ObjectInputStream(new ByteArrayInputStream(bytes)).readObject();
        }
        catch (IOException | ClassNotFoundException e)
        {
            throw new AssertionError(e);
        }
    }

    private static String encodeObject(Object actualObject)
    {
        try
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutput objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(actualObject);
            objectOutputStream.flush();
            objectOutputStream.close();

            String string = ENCODER.encodeToString(byteArrayOutputStream.toByteArray());
            return Verify.addFinalNewline(string);
        }
        catch (IOException e)
        {
            throw new AssertionError(e);
        }
    }

    private static String addFinalNewline(String string)
    {
        if (string.length() % 77 == 76)
        {
            return string + '\n';
        }
        return string;
    }

    public static void assertNotSerializable(Object actualObject)
    {
        Verify.assertThrows(NotSerializableException.class, () ->
        {
            new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(actualObject);
            return null;
        });
    }

    /**
     * Assert that {@code objectA} and {@code objectB} are equal via the {@link Object#equals(Object)} method,
     * and that they both return the same {@link Object#hashCode()}.
     */
    public static void assertEqualsAndHashCode(Object objectA, Object objectB)
    {
        Verify.assertEqualsAndHashCode("objects", objectA, objectB);
    }

    /**
     * Asserts that a value is negative.
     */
    public static void assertNegative(int value)
    {
        assertTrue(value + " is not negative", value < 0);
    }

    /**
     * Asserts that a value is positive.
     */
    public static void assertPositive(int value)
    {
        assertTrue(value + " is not positive", value > 0);
    }

    /**
     * Asserts that a value is positive.
     */
    public static void assertZero(int value)
    {
        assertEquals(0, value);
    }

    /**
     * Assert that {@code objectA} and {@code objectB} are equal (via the {@link Object#equals(Object)} method,
     * and that they both return the same {@link Object#hashCode()}.
     */
    public static void assertEqualsAndHashCode(String itemNames, Object objectA, Object objectB)
    {
        if (objectA == null || objectB == null)
        {
            fail("Neither item should be null: <" + objectA + "> <" + objectB + '>');
        }

        assertFalse("Neither item should equal null", objectA.equals(null));
        assertFalse("Neither item should equal null", objectB.equals(null));
        assertFalse("Neither item should equal new Object()", objectA.equals(new Object()));
        assertFalse("Neither item should equal new Object()", objectB.equals(new Object()));
        assertEquals("Expected " + itemNames + " to be equal.", objectA, objectA);
        assertEquals("Expected " + itemNames + " to be equal.", objectB, objectB);
        assertEquals("Expected " + itemNames + " to be equal.", objectA, objectB);
        assertEquals("Expected " + itemNames + " to be equal.", objectB, objectA);
        assertEquals(
                "Expected " + itemNames + " to have the same hashCode().",
                objectA.hashCode(),
                objectB.hashCode());
    }

    /**
     * @deprecated since 8.2.0 as will not work with Java 9
     */
    @Deprecated
    public static void assertShallowClone(Cloneable object)
    {
        Verify.assertShallowClone("object", object);
    }

    /**
     * @deprecated since 8.2.0 as will not work with Java 9
     */
    @Deprecated
    public static void assertShallowClone(String itemName, Cloneable object)
    {
        try
        {
            Method method = Object.class.getDeclaredMethod("clone", (Class<?>[]) null);
            method.setAccessible(true);
            Object clone = method.invoke(object);
            String prefix = itemName + " and its clone";
            assertNotSame(prefix, object, clone);
            Verify.assertEqualsAndHashCode(prefix, object, clone);
        }
        catch (IllegalArgumentException | IllegalAccessException | NoSuchMethodException | SecurityException | InvocationTargetException e)
        {
            throw new AssertionError(e.getLocalizedMessage());
        }
    }

    public static <T> void assertClassNonInstantiable(Class<T> aClass)
    {
        try
        {
            Constructor<T> declaredConstructor = aClass.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            declaredConstructor.newInstance();
            fail("Expected class '" + aClass + "' to be non-instantiable");
        }
        catch (InstantiationException | NoSuchMethodException e)
        {
            // pass
        }
        catch (IllegalAccessException e)
        {
            if (Verify.canInstantiateThroughReflection(aClass))
            {
                fail("Expected constructor of non-instantiable class '" + aClass + "' to throw an exception, but didn't");
            }
        }
        catch (InvocationTargetException e)
        {
            // pass - constructor threw an exception, which is expected for non-instantiable classes
        }
    }

    private static <T> boolean canInstantiateThroughReflection(Class<T> aClass)
    {
        try
        {
            Constructor<T> declaredConstructor = aClass.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            declaredConstructor.newInstance();
            return true;
        }
        catch (NoSuchMethodException | AssertionError | IllegalAccessException | InstantiationException | InvocationTargetException e)
        {
            return false;
        }
    }

    public static void assertError(Class<? extends Error> expectedErrorClass, Runnable code)
    {
        try
        {
            code.run();
        }
        catch (Error ex)
        {
            assertSame(
                    "Caught error of type <"
                            + ex.getClass().getName()
                            + ">, expected one of type <"
                            + expectedErrorClass.getName()
                            + '>',
                    expectedErrorClass,
                    ex.getClass());
            return;
        }

        fail("Block did not throw an error of type " + expectedErrorClass.getName());
    }

    /**
     * Runs the {@link Callable} {@code code} and asserts that it throws an {@code Exception} of the type
     * {@code expectedExceptionClass}.
     * <p>
     * {@code Callable} is most appropriate when a checked exception will be thrown.
     * If a subclass of {@link RuntimeException} will be thrown, the form
     * {@link Assert#assertThrows(Class, org.junit.function.ThrowingRunnable)} may be more convenient.
     * <p>
     * e.g.
     * <pre>
     * Verify.<b>assertThrows</b>(StringIndexOutOfBoundsException.class, new Callable&lt;String&gt;()
     * {
     *    public String call() throws Exception
     *    {
     *        return "Craig".substring(42, 3);
     *    }
     * });
     * </pre>
     *
     * @see Assert#assertThrows(Class, org.junit.function.ThrowingRunnable)
     */
    public static void assertThrows(
            Class<? extends Exception> expectedExceptionClass,
            Callable<?> code)
    {
        try
        {
            code.call();
        }
        catch (Exception ex)
        {
            assertSame(
                    "Caught exception of type <"
                            + ex.getClass().getName()
                            + ">, expected one of type <"
                            + expectedExceptionClass.getName()
                            + '>'
                            + '\n'
                            + "Exception Message: " + ex.getMessage()
                            + '\n',
                    expectedExceptionClass,
                    ex.getClass());
            return;
        }

        fail("Block did not throw an exception of type " + expectedExceptionClass.getName());
    }

    /**
     * Runs the {@link Callable} {@code code} and asserts that it throws an {@code Exception} of the type
     * {@code expectedExceptionClass}, which contains a cause of type expectedCauseClass.
     * <p>
     * {@code Callable} is most appropriate when a checked exception will be thrown.
     * If a subclass of {@link RuntimeException} will be thrown, the form
     * {@link #assertThrowsWithCause(Class, Class, Runnable)} may be more convenient.
     * <p>
     * e.g.
     * <pre>
     * Verify.assertThrowsWithCause(RuntimeException.class, IOException.class, new Callable&lt;Void&gt;()
     * {
     *    public Void call() throws Exception
     *    {
     *        try
     *        {
     *            new File("").createNewFile();
     *        }
     *        catch (final IOException e)
     *        {
     *            throw new RuntimeException("Uh oh!", e);
     *        }
     *        return null;
     *    }
     * });
     * </pre>
     *
     * @see #assertThrowsWithCause(Class, Class, Runnable)
     */
    public static void assertThrowsWithCause(
            Class<? extends Exception> expectedExceptionClass,
            Class<? extends Throwable> expectedCauseClass,
            Callable<?> code)
    {
        try
        {
            code.call();
        }
        catch (Exception ex)
        {
            assertSame(
                    "Caught exception of type <"
                            + ex.getClass().getName()
                            + ">, expected one of type <"
                            + expectedExceptionClass.getName()
                            + '>',
                    expectedExceptionClass,
                    ex.getClass());
            Throwable actualCauseClass = ex.getCause();
            assertNotNull(
                    "Caught exception with null cause, expected cause of type <"
                            + expectedCauseClass.getName()
                            + '>',
                    actualCauseClass);
            assertSame(
                    "Caught exception with cause of type<"
                            + actualCauseClass.getClass().getName()
                            + ">, expected cause of type <"
                            + expectedCauseClass.getName()
                            + '>',
                    expectedCauseClass,
                    actualCauseClass.getClass());
            return;
        }

        fail("Block did not throw an exception of type " + expectedExceptionClass.getName());
    }

    /**
     * Runs the {@link Runnable} {@code code} and asserts that it throws an {@code Exception} of the type
     * {@code expectedExceptionClass}, which contains a cause of type expectedCauseClass.
     * <p>
     * {@code Runnable} is most appropriate when a subclass of {@link RuntimeException} will be thrown.
     * If a checked exception will be thrown, the form {@link #assertThrowsWithCause(Class, Class, Callable)}
     * may be more convenient.
     * <p>
     * e.g.
     * <pre>
     * Verify.assertThrowsWithCause(RuntimeException.class, StringIndexOutOfBoundsException.class, new Runnable()
     * {
     *    public void run()
     *    {
     *        try
     *        {
     *            LOGGER.info("Craig".substring(42, 3));
     *        }
     *        catch (final StringIndexOutOfBoundsException e)
     *        {
     *            throw new RuntimeException("Uh oh!", e);
     *        }
     *    }
     * });
     * </pre>
     *
     * @see #assertThrowsWithCause(Class, Class, Callable)
     */
    public static void assertThrowsWithCause(
            Class<? extends Exception> expectedExceptionClass,
            Class<? extends Throwable> expectedCauseClass,
            Runnable code)
    {
        try
        {
            code.run();
        }
        catch (RuntimeException ex)
        {
            assertSame(
                    "Caught exception of type <"
                            + ex.getClass().getName()
                            + ">, expected one of type <"
                            + expectedExceptionClass.getName()
                            + '>',
                    expectedExceptionClass,
                    ex.getClass());
            Throwable actualCauseClass = ex.getCause();
            assertNotNull(
                    "Caught exception with null cause, expected cause of type <"
                            + expectedCauseClass.getName()
                            + '>',
                    actualCauseClass);
            assertSame(
                    "Caught exception with cause of type<"
                            + actualCauseClass.getClass().getName()
                            + ">, expected cause of type <"
                            + expectedCauseClass.getName()
                            + '>',
                    expectedCauseClass,
                    actualCauseClass.getClass());
            return;
        }

        fail("Block did not throw an exception of type " + expectedExceptionClass.getName());
    }

    private static String relocatedExpectedForm(String expectedBase64Form)
    {
        try
        {
            byte[] original = DECODER.decode(expectedBase64Form);
            HistoricalSerializationMetadata metadata = new HistoricalSerializationMetadata(original);
            if (!metadata.isHistoricalStream())
            {
                return expectedBase64Form;
            }
            if (!expectedBase64Form.equals(Verify.addFinalNewline(ENCODER.encodeToString(original))))
            {
                throw new IllegalArgumentException("Historical Base64 literal is not canonical");
            }
            byte[] relocated = metadata.rewrite();
            return java.util.Arrays.equals(original, relocated)
                    ? expectedBase64Form
                    : Verify.addFinalNewline(ENCODER.encodeToString(relocated));
        }
        catch (IllegalArgumentException e)
        {
            throw new AssertionError("Historical serialization metadata is outside the reviewed relocation", e);
        }
    }

    /**
     * Adapts expected metadata for this fork's existing package relocation.
     * Golden literals, current-object serialization and every payload byte stay unchanged.
     * This private test utility is deliberately not a general serialization reader.
     * The only UID exceptions are three array identities in seven exact fixture streams.
     * Three exact lambda streams also bind compiler-proven names to their metadata fields;
     * captured application values and shared payload String handles remain immutable.
     */
    private static final class HistoricalSerializationMetadata
    {
        private static final String OLD_PACKAGE = "org.eclipse.collections.";
        private static final String NEW_PACKAGE = "com.m3.collections.eclipse.";
        private static final int CLASS_DESCRIPTOR = 1;
        private static final int STRING = 2;
        private static final int VALUE = 3;
        private static final int DESCRIPTOR_ROLE = 1;
        private static final int FIELD_TYPE_ROLE = 2;
        private static final int PAYLOAD_ROLE = 3;
        private static final int LAMBDA_ROLE = 4;
        // These three original streams are JDK wrappers around relocated collections (six golden sites).
        private static final Set<String> HISTORICAL_WRAPPER_STREAMS = Set.of(
                "83a49fb4bb5df3f823df77a13ae3bfd1f616b8d48d3a542f5883fb0534d658ba",
                "4a23d5a0fafd811abd5bc97e9556ca384eac90c3dd56a1b32faefa0ea398357a",
                "87fc614dd54eb7fc8d01d38602de19c035e0744fc9c82408d2708de83351dcb6");
        private static final Map<String, long[]> ARRAY_UIDS = Map.of(
                "[Lorg.eclipse.collections.api.block.function.Function;", new long[]{-1327736830033849207L, -8802506257625654965L},
                "[Lorg.eclipse.collections.api.block.predicate.Predicate;", new long[]{228085428218344230L, -1748138008523009722L},
                "[Lorg.eclipse.collections.api.block.HashingStrategy;", new long[]{-2414103105749696569L, -945861659354630551L});
        private static final Map<String, String> ARRAY_STREAMS = Map.of(
                "7e54f0280d9ed8250a49f1afebdcbbdeac68c253d860ac445f6d102457ce0ee4", "[Lorg.eclipse.collections.api.block.function.Function;",
                "7a97b5ed69f17a9203085a423e9e71309c05d77daca1f8ea13fddd15d7c83642", "[Lorg.eclipse.collections.api.block.function.Function;",
                "e0ff5e642bb72a217c47d7288a90e686e3385f764f0a98127d9477b28cbe953f", "[Lorg.eclipse.collections.api.block.function.Function;",
                "23811be57930f233f68023763cc8230115643326a9832af3134ffa6969376a59", "[Lorg.eclipse.collections.api.block.predicate.Predicate;",
                "5ec8b049f343bd82c568c6dbb22672eff3e3c1d151265375fb78f9101bdb80f7", "[Lorg.eclipse.collections.api.block.predicate.Predicate;",
                "b6aed59d75f342ffb35a730597e65086fb69417acdd7f394191b903877f18826", "[Lorg.eclipse.collections.api.block.predicate.Predicate;",
                "a8521219a8f407fe71fbb7ebbbb95c954cae6ea92daf7bc9f386940c8e8f16fc", "[Lorg.eclipse.collections.api.block.HashingStrategy;");
        private static final Map<String, String> LAMBDA_STREAMS = Map.of(
                "07cb2d222eeab0ddc10504671e25f3d090dafc0c2a5f5d3ad0714e8049161a93", "function",
                "beb7c279152c0d4f2649377b7f44f83fdafa92950367309cfc5bd8e813411d49", "procedure",
                "20119c0b82473f1fe26b264b02110f3ca7680dd4a658eee96270b9b2850263b9", "procedure");
        private static final Map<String, String> LAMBDA_METHODS = Map.of(
                "lambda$fizzBuzz$e838902d$1", "lambda$fizzBuzz$6c445d54$1",
                "lambda$fizzBuzz$e838902d$2", "lambda$fizzBuzz$6c445d54$2",
                "lambda$fizzBuzz$e838902d$3", "lambda$fizzBuzz$6c445d54$3",
                "lambda$fizzBuzz$e838902d$4", "lambda$fizzBuzz$6c445d54$4",
                "lambda$fizzBuzz$ce5628af$1", "lambda$fizzBuzz$85f09f16$1",
                "lambda$fizzBuzz$ce5628af$2", "lambda$fizzBuzz$85f09f16$2",
                "lambda$fizzBuzz$ce5628af$3", "lambda$fizzBuzz$85f09f16$3",
                "lambda$oneCaseWithDefaultIntProcecure$1884dfc0$1", "lambda$oneCaseWithDefaultIntProcecure$54393639$1");
        private static final List<String> LAMBDA_SCHEMA = List.of(
                "I:implMethodKind:", "[:capturedArgs:[Ljava/lang/Object;", "L:capturingClass:Ljava/lang/Class;",
                "L:functionalInterfaceClass:Ljava/lang/String;", "L:functionalInterfaceMethodName:Ljava/lang/String;",
                "L:functionalInterfaceMethodSignature:Ljava/lang/String;", "L:implClass:Ljava/lang/String;",
                "L:implMethodName:Ljava/lang/String;", "L:implMethodSignature:Ljava/lang/String;",
                "L:instantiatedMethodType:Ljava/lang/String;");

        private final byte[] data;
        private final String originalHash;
        private final List<SerializationHandle> handles = new java.util.ArrayList<>();
        private final List<MetadataEdit> edits = new java.util.ArrayList<>();
        private int position;
        private int depth;
        private int arrayUidEdits;
        private int lambdaCount;

        private HistoricalSerializationMetadata(byte[] data)
        {
            this.data = data;
            try
            {
                this.originalHash = java.util.HexFormat.of().formatHex(
                        java.security.MessageDigest.getInstance("SHA-256").digest(data));
            }
            catch (java.security.NoSuchAlgorithmException e)
            {
                throw new AssertionError(e);
            }
        }

        private boolean isHistoricalStream()
        {
            if (HISTORICAL_WRAPPER_STREAMS.contains(this.originalHash))
            {
                return true;
            }
            // Inspect only the root descriptor. Ordinary captured Strings never opt a stream in.
            if (this.data.length < 8 || (this.data[0] & 255) != 172 || (this.data[1] & 255) != 237
                    || this.data[2] != 0 || this.data[3] != 5)
            {
                return false;
            }
            int offset = 4;
            while (offset < this.data.length && this.data[offset] == 121)
            {
                offset++;
            }
            if (this.data.length - offset < 4 || !Set.of(115, 117, 118, 126).contains(this.data[offset] & 255)
                    || this.data[offset + 1] != 114)
            {
                return false;
            }
            int length = ((this.data[offset + 2] & 255) << 8) | (this.data[offset + 3] & 255);
            offset += 4;
            if (length > this.data.length - offset)
            {
                return false;
            }
            int end = offset + length;
            boolean array = false;
            while (offset < end && this.data[offset] == '[')
            {
                array = true;
                offset++;
            }
            if (array && offset < end && this.data[offset] == 'L')
            {
                offset++;
            }
            byte[] prefix = OLD_PACKAGE.getBytes(java.nio.charset.StandardCharsets.US_ASCII);
            if (end - offset < prefix.length)
            {
                return false;
            }
            for (int i = 0; i < prefix.length; i++)
            {
                if (this.data[offset + i] != prefix[i])
                {
                    return false;
                }
            }
            return true;
        }

        private void require(boolean condition, String reason)
        {
            if (!condition)
            {
                throw new IllegalArgumentException("Offset " + this.position + ": " + reason);
            }
        }

        private byte[] take(int length)
        {
            this.require(length >= 0 && length <= this.data.length - this.position, "Invalid length or truncated stream");
            byte[] result = java.util.Arrays.copyOfRange(this.data, this.position, this.position + length);
            this.position += length;
            return result;
        }

        private long number(int width)
        {
            long value = 0L;
            for (byte next : this.take(width))
            {
                value = (value << 8) | (next & 255L);
            }
            return value;
        }

        private SerializationUtf utf(int width)
        {
            int start = this.position;
            long length = this.number(width);
            this.require(length >= 0 && length <= this.data.length - this.position, "Invalid UTF length");
            byte[] bytes = this.take((int) length);
            for (int i = 0; i < bytes.length; )
            {
                int first = bytes[i] & 255;
                if (first > 0 && first < 128)
                {
                    i++;
                }
                else if (first >= 192 && first <= 223)
                {
                    this.require(i + 1 < bytes.length && (bytes[i + 1] & 192) == 128, "Malformed modified UTF-8");
                    int value = ((first & 31) << 6) | (bytes[i + 1] & 63);
                    this.require(value >= 128 || (first == 192 && bytes[i + 1] == (byte) 128), "Overlong modified UTF-8");
                    i += 2;
                }
                else if (first >= 224 && first <= 239)
                {
                    this.require(i + 2 < bytes.length && (bytes[i + 1] & 192) == 128 && (bytes[i + 2] & 192) == 128,
                            "Malformed modified UTF-8");
                    int value = ((first & 15) << 12) | ((bytes[i + 1] & 63) << 6) | (bytes[i + 2] & 63);
                    this.require(value >= 2048, "Overlong modified UTF-8");
                    i += 3;
                }
                else
                {
                    throw new IllegalArgumentException("Invalid modified UTF-8 lead byte");
                }
            }
            return new SerializationUtf(start, this.position, width, bytes);
        }

        private String metadataName(SerializationUtf utf)
        {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < utf.bytes.length; )
            {
                int first = utf.bytes[i++] & 255;
                if (first < 128)
                {
                    result.append((char) first);
                }
                else if (first < 224)
                {
                    result.append((char) (((first & 31) << 6) | (utf.bytes[i++] & 63)));
                }
                else
                {
                    int second = utf.bytes[i++] & 63;
                    result.append((char) (((first & 15) << 12) | (second << 6) | (utf.bytes[i++] & 63)));
                }
            }
            return result.toString();
        }

        private MetadataEdit replaceUtf(SerializationUtf utf, String value)
        {
            this.require(value.chars().allMatch(character -> character > 0 && character < 128), "Editable metadata must be ASCII");
            byte[] bytes = value.getBytes(java.nio.charset.StandardCharsets.US_ASCII);
            this.require(utf.width == 8 || bytes.length <= 65535, "Relocated UTF length overflows");
            byte[] encoded = new byte[utf.width + bytes.length];
            for (int i = 0; i < utf.width; i++)
            {
                encoded[utf.width - 1 - i] = (byte) ((long) bytes.length >>> (i * 8));
            }
            System.arraycopy(bytes, 0, encoded, utf.width, bytes.length);
            MetadataEdit edit = new MetadataEdit(utf.start, utf.end, encoded);
            this.edits.add(edit);
            return edit;
        }

        private MetadataEdit relocateName(SerializationUtf utf, boolean fieldType, boolean proxy)
        {
            String name = this.metadataName(utf);
            String prefix = fieldType ? OLD_PACKAGE.replace('.', '/') : OLD_PACKAGE;
            String replacement = fieldType ? NEW_PACKAGE.replace('.', '/') : NEW_PACKAGE;
            int dimensions = 0;
            while (dimensions < name.length() && name.charAt(dimensions) == '[')
            {
                dimensions++;
            }
            if (fieldType)
            {
                this.require(name.matches("\\[*L[^;]+;|\\[+[BCDFIJSZ]"), "Invalid reference field type");
            }
            if (proxy)
            {
                this.require(!name.isEmpty() && dimensions == 0
                        && !Set.of("boolean", "byte", "char", "short", "int", "float", "long", "double", "void").contains(name),
                        "Invalid proxy interface name");
            }
            boolean wrapped = fieldType || dimensions > 0;
            if (wrapped && (dimensions >= name.length() || name.charAt(dimensions) != 'L' || !name.endsWith(";")))
            {
                return null;
            }
            int start = wrapped ? dimensions + 1 : 0;
            int end = wrapped ? name.length() - 1 : name.length();
            String binary = name.substring(start, end);
            if (!binary.startsWith(prefix))
            {
                return null;
            }
            String relocated = replacement + binary.substring(prefix.length());
            try
            {
                Class<?> bound = Class.forName(relocated.replace('/', '.'), false, Verify.class.getClassLoader());
                this.require(!proxy || bound.isInterface(), "Relocated proxy identity is not an interface");
            }
            catch (ClassNotFoundException e)
            {
                throw new IllegalArgumentException("Relocated descriptor has no compiled class binding: " + relocated, e);
            }
            return this.replaceUtf(utf, name.substring(0, start) + relocated + name.substring(end));
        }

        private static Map<String, String> lambdaFieldValues(String group, String fieldName)
        {
            boolean function = "function".equals(group);
            switch (fieldName)
            {
                case "functionalInterfaceClass":
                    return function ? Map.of(
                            "org/eclipse/collections/api/block/predicate/primitive/IntPredicate", "com/m3/collections/eclipse/api/block/predicate/primitive/IntPredicate",
                            "org/eclipse/collections/api/block/function/primitive/IntToObjectFunction", "com/m3/collections/eclipse/api/block/function/primitive/IntToObjectFunction") : Map.of(
                            "org/eclipse/collections/api/block/predicate/primitive/IntPredicate", "com/m3/collections/eclipse/api/block/predicate/primitive/IntPredicate",
                            "org/eclipse/collections/api/block/procedure/primitive/IntProcedure", "com/m3/collections/eclipse/api/block/procedure/primitive/IntProcedure");
                case "implClass":
                    return function ? Map.of(
                            "org/eclipse/collections/impl/block/function/primitive/PrimitiveCaseFunctionSerializationTest", "com/m3/collections/eclipse/impl/block/function/primitive/PrimitiveCaseFunctionSerializationTest") : Map.of(
                            "org/eclipse/collections/api/collection/primitive/MutableIntCollection", "com/m3/collections/eclipse/api/collection/primitive/MutableIntCollection",
                            "org/eclipse/collections/impl/block/procedure/primitive/PrimitiveCaseProcedureSerializationTest", "com/m3/collections/eclipse/impl/block/procedure/primitive/PrimitiveCaseProcedureSerializationTest");
                case "implMethodName":
                    if (function)
                    {
                        Map<String, String> values = new java.util.HashMap<>(LAMBDA_METHODS);
                        values.remove("lambda$oneCaseWithDefaultIntProcecure$1884dfc0$1");
                        return values;
                    }
                    return Map.of("add", "add", "lambda$oneCaseWithDefaultIntProcecure$1884dfc0$1", "lambda$oneCaseWithDefaultIntProcecure$54393639$1");
                case "functionalInterfaceMethodName":
                    return function ? Map.of("accept", "accept", "valueOf", "valueOf") : Map.of("accept", "accept", "value", "value");
                case "functionalInterfaceMethodSignature":
                    return function ? Map.of("(I)Z", "(I)Z", "(I)Ljava/lang/Object;", "(I)Ljava/lang/Object;") : Map.of("(I)Z", "(I)Z", "(I)V", "(I)V");
                case "implMethodSignature":
                    return function ? Map.of("(I)Z", "(I)Z", "(I)Ljava/lang/String;", "(I)Ljava/lang/String;") : Map.of("(I)Z", "(I)Z");
                case "instantiatedMethodType":
                    return function ? Map.of("(I)Z", "(I)Z", "(I)Ljava/lang/String;", "(I)Ljava/lang/String;") : Map.of("(I)Z", "(I)Z", "(I)V", "(I)V");
                default:
                    throw new IllegalArgumentException("Unreviewed SerializedLambda field role");
            }
        }

        private void relocateArrayUid(SerializationDescriptor descriptor, int offset, MetadataEdit nameEdit)
        {
            if (nameEdit == null || !descriptor.name.startsWith("["))
            {
                return;
            }
            long[] pair = ARRAY_UIDS.get(descriptor.name);
            this.require(pair != null && descriptor.name.equals(ARRAY_STREAMS.get(this.originalHash)),
                    "Array UID is outside the three types and seven original stream allowlist");
            this.require(descriptor.uid == pair[0], "Array UID preimage differs from reviewed value");
            byte[] bytes = new byte[8];
            for (int i = 0; i < bytes.length; i++)
            {
                bytes[7 - i] = (byte) (pair[1] >>> (i * 8));
            }
            this.edits.add(new MetadataEdit(offset, offset + 8, bytes));
            this.arrayUidEdits++;
        }

        private SerializationHandle handle(int kind, SerializationDescriptor descriptor, SerializationUtf utf)
        {
            SerializationHandle result = new SerializationHandle(kind, descriptor, utf);
            this.handles.add(result);
            return result;
        }

        private void role(SerializationHandle handle, int role, String lambdaField)
        {
            if (role == DESCRIPTOR_ROLE)
            {
                this.require(handle.kind == CLASS_DESCRIPTOR, "Class descriptor reference has the wrong handle kind");
            }
            else if (role == FIELD_TYPE_ROLE)
            {
                this.require(handle.kind == STRING, "Field type reference has the wrong handle kind");
                handle.roles |= 1;
                if (handle.edit == null)
                {
                    handle.edit = this.relocateName(handle.utf, true, false);
                }
            }
            else if (role == LAMBDA_ROLE)
            {
                this.require(handle.kind == STRING, "SerializedLambda metadata is not a String");
                String group = LAMBDA_STREAMS.get(this.originalHash);
                this.require(group != null, "SerializedLambda is not bound to an original fixture stream");
                Map<String, String> values = HistoricalSerializationMetadata.lambdaFieldValues(group, lambdaField);
                String name = this.metadataName(handle.utf);
                this.require(values.containsKey(name), "SerializedLambda field differs from independently compiled metadata");
                handle.roles |= 1;
                if (!name.equals(values.get(name)) && handle.edit == null)
                {
                    handle.edit = this.replaceUtf(handle.utf, values.get(name));
                }
            }
            else if (handle.kind == STRING)
            {
                handle.roles |= 2;
            }
            this.require(handle.kind != STRING || handle.edit == null || handle.roles != 3,
                    "Editable descriptor String aliases ordinary payload");
        }

        private void annotation()
        {
            while (true)
            {
                this.require(this.position < this.data.length, "Missing TC_ENDBLOCKDATA");
                if (this.data[this.position] == 120)
                {
                    this.position++;
                    return;
                }
                this.content(PAYLOAD_ROLE, true);
            }
        }

        private SerializationHandle descriptor(boolean proxy)
        {
            SerializationDescriptor descriptor = new SerializationDescriptor();
            SerializationHandle handle;
            if (proxy)
            {
                handle = this.handle(CLASS_DESCRIPTOR, descriptor, null);
                int count = (int) this.number(4);
                this.require(count >= 0 && count <= (this.data.length - this.position) / 2, "Invalid proxy interface count");
                descriptor.name = "<proxy>";
                descriptor.flags = 2;
                for (int i = 0; i < count; i++)
                {
                    this.relocateName(this.utf(2), false, true);
                }
            }
            else
            {
                SerializationUtf name = this.utf(2);
                descriptor.name = this.metadataName(name);
                MetadataEdit nameEdit = this.relocateName(name, false, false);
                int uidOffset = this.position;
                descriptor.uid = this.number(8);
                this.relocateArrayUid(descriptor, uidOffset, nameEdit);
                handle = this.handle(CLASS_DESCRIPTOR, descriptor, null);
                descriptor.flags = (int) this.number(1);
                this.require(Set.of(0, 2, 3, 12, 18).contains(descriptor.flags), "Unknown flags or opaque protocol1 Externalizable");
                this.require(descriptor.flags != 18 || descriptor.uid == 0L, "Enum UID must be zero");
                int count = (int) this.number(2);
                this.require(!Set.of(0, 12, 18).contains(descriptor.flags) || count == 0, "Fields forbidden by descriptor flags");
                this.require(!descriptor.name.startsWith("[") || count == 0, "Array descriptor has fields");
                Set<String> names = new java.util.HashSet<>();
                byte[] previousName = null;
                boolean previousPrimitive = true;
                for (int i = 0; i < count; i++)
                {
                    char code = (char) this.number(1);
                    boolean primitive = HistoricalSerializationMetadata.primitiveWidth(code) > 0;
                    this.require(primitive || code == 'L' || code == '[', "Unknown field type code");
                    SerializationUtf fieldName = this.utf(2);
                    String fieldNameText = this.metadataName(fieldName);
                    this.require(!fieldNameText.isEmpty() && fieldNameText.indexOf(0) < 0 && names.add(fieldNameText), "Empty, NUL or duplicate field name");
                    this.require(previousName == null || (previousPrimitive && !primitive)
                            || (previousPrimitive == primitive && java.util.Arrays.compareUnsigned(previousName, fieldName.bytes) < 0),
                            "Unordered fields");
                    previousName = fieldName.bytes;
                    previousPrimitive = primitive;
                    String signature = "";
                    if (!primitive)
                    {
                        SerializationHandle type = this.content(FIELD_TYPE_ROLE, false);
                        signature = this.metadataName(type.utf);
                        this.require(signature.charAt(0) == code, "Field code and type descriptor disagree");
                    }
                    descriptor.fields.add(new SerializationField(code, fieldNameText, signature));
                }
            }
            this.annotation();
            descriptor.parent = this.content(DESCRIPTOR_ROLE, false);
            return handle;
        }

        private static int primitiveWidth(char code)
        {
            switch (code)
            {
                case 'B':
                case 'Z':
                    return 1;
                case 'C':
                case 'S':
                    return 2;
                case 'I':
                case 'F':
                    return 4;
                case 'J':
                case 'D':
                    return 8;
                default:
                    return 0;
            }
        }

        private void objectData(SerializationHandle handle)
        {
            List<SerializationDescriptor> hierarchy = new java.util.ArrayList<>();
            Set<SerializationHandle> visited = new java.util.HashSet<>();
            for (SerializationHandle current = handle; current != null; current = current.descriptor.parent)
            {
                this.require(current.kind == CLASS_DESCRIPTOR && visited.add(current), "Invalid or cyclic superclass descriptor");
                hierarchy.add(current.descriptor);
            }
            if (hierarchy.get(0).flags == 12)
            {
                this.annotation();
                return;
            }
            for (int i = hierarchy.size() - 1; i >= 0; i--)
            {
                SerializationDescriptor descriptor = hierarchy.get(i);
                this.require(descriptor.flags == 2 || descriptor.flags == 3, "Unsupported object flags");
                boolean lambda = "java.lang.invoke.SerializedLambda".equals(descriptor.name);
                if (lambda)
                {
                    List<String> schema = new java.util.ArrayList<>();
                    for (SerializationField field : descriptor.fields)
                    {
                        schema.add(field.code + ":" + field.name + ':' + field.signature);
                    }
                    this.require(descriptor.uid == 8025925345765570181L && descriptor.flags == 2
                            && descriptor.parent == null && schema.equals(LAMBDA_SCHEMA), "SerializedLambda owner UID or exact schema changed");
                    this.require(LAMBDA_STREAMS.containsKey(this.originalHash), "SerializedLambda is not bound to an original fixture stream");
                }
                for (SerializationField field : descriptor.fields)
                {
                    int width = HistoricalSerializationMetadata.primitiveWidth(field.code);
                    if (width > 0)
                    {
                        long value = this.number(width);
                        this.require(!lambda || (field.code == 'I' && (value == 6L || value == 9L)), "Unreviewed lambda method kind");
                    }
                    else
                    {
                        int role = lambda && !"capturedArgs".equals(field.name) && !"capturingClass".equals(field.name)
                                ? LAMBDA_ROLE : PAYLOAD_ROLE;
                        this.content(role, false, field.name);
                    }
                }
                if (descriptor.flags == 3)
                {
                    this.annotation();
                }
                if (lambda)
                {
                    this.lambdaCount++;
                }
            }
        }

        private SerializationHandle content(int role, boolean allowBlock)
        {
            return this.content(role, allowBlock, null);
        }

        private SerializationHandle content(int role, boolean allowBlock, String lambdaField)
        {
            this.require(this.depth < 128, "Graph nesting exceeds reviewed bound");
            this.depth++;
            try
            {
                int tag = (int) this.number(1);
                if (tag == 112)
                {
                    this.require(role != FIELD_TYPE_ROLE && role != LAMBDA_ROLE, "Null metadata String");
                    return null;
                }
                if (tag == 113)
                {
                    long index = this.number(4) - 0x7e0000L;
                    this.require(index >= 0 && index < this.handles.size(), "Invalid or forward handle reference");
                    SerializationHandle handle = this.handles.get((int) index);
                    this.role(handle, role, lambdaField);
                    return handle;
                }
                if (tag == 114 || tag == 125)
                {
                    this.require(role != FIELD_TYPE_ROLE && role != LAMBDA_ROLE, "Descriptor used as metadata String");
                    return this.descriptor(tag == 125);
                }
                if (tag == 116 || tag == 124)
                {
                    this.require(role != DESCRIPTOR_ROLE, "String used as class descriptor");
                    SerializationHandle handle = this.handle(STRING, null, this.utf(tag == 116 ? 2 : 8));
                    this.role(handle, role, lambdaField);
                    return handle;
                }
                this.require(role == PAYLOAD_ROLE, "Metadata token has wrong kind");
                if (tag == 115 || tag == 117 || tag == 118 || tag == 126)
                {
                    SerializationHandle descriptor = this.content(DESCRIPTOR_ROLE, false);
                    this.require(descriptor != null, "Null descriptor for object, array, class or enum");
                    SerializationHandle handle = this.handle(VALUE, descriptor.descriptor, null);
                    if (tag == 115)
                    {
                        this.objectData(descriptor);
                    }
                    else if (tag == 117)
                    {
                        String name = descriptor.descriptor.name;
                        this.require(name.startsWith("["), "Array tag without array descriptor");
                        int count = (int) this.number(4);
                        this.require(count >= 0 && count <= this.data.length - this.position, "Invalid array length");
                        int width = name.length() == 2 ? HistoricalSerializationMetadata.primitiveWidth(name.charAt(1)) : 0;
                        if (width > 0)
                        {
                            this.require(count <= (this.data.length - this.position) / width, "Truncated primitive array");
                            this.take(count * width);
                        }
                        else
                        {
                            this.require(name.startsWith("[[") || (name.startsWith("[L") && name.endsWith(";")), "Invalid reference array descriptor");
                            for (int i = 0; i < count; i++)
                            {
                                this.content(PAYLOAD_ROLE, false);
                            }
                        }
                    }
                    else if (tag == 126)
                    {
                        this.require(descriptor.descriptor.flags == 18, "Enum tag without enum flags");
                        SerializationHandle name = this.content(PAYLOAD_ROLE, false);
                        this.require(name != null && name.kind == STRING, "Enum constant is not String");
                    }
                    return handle;
                }
                if (tag == 119 || tag == 122)
                {
                    this.require(allowBlock, "Block data outside annotation");
                    this.take((int) this.number(tag == 119 ? 1 : 4));
                    return null;
                }
                if (tag == 121)
                {
                    this.require(this.depth == 1 && !allowBlock, "TC_RESET inside an active object");
                    this.handles.clear();
                    return null;
                }
                throw new IllegalArgumentException("Unknown, misplaced or unsuccessful-stream token: " + tag);
            }
            finally
            {
                this.depth--;
            }
        }

        private byte[] rewrite()
        {
            this.require(this.number(4) == 0xaced0005L, "Wrong serialization header");
            int values = 0;
            while (this.position < this.data.length)
            {
                boolean reset = this.data[this.position] == 121;
                this.content(PAYLOAD_ROLE, false);
                if (!reset)
                {
                    values++;
                }
            }
            this.require(values == 1, "Expected exactly one serialized top-level value");
            this.require(this.arrayUidEdits == (ARRAY_STREAMS.containsKey(this.originalHash) ? 1 : 0),
                    "Expected exactly the reviewed array UID occurrences");
            String lambdaGroup = LAMBDA_STREAMS.get(this.originalHash);
            this.require(this.lambdaCount == (lambdaGroup == null ? 0 : ("function".equals(lambdaGroup) ? 7 : 3)),
                    "SerializedLambda occurrence count changed");
            this.edits.sort(java.util.Comparator.comparingInt(edit -> edit.start));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            int previous = 0;
            for (MetadataEdit edit : this.edits)
            {
                this.require(edit.start >= previous, "Overlapping metadata edits");
                output.write(this.data, previous, edit.start - previous);
                output.writeBytes(edit.bytes);
                previous = edit.end;
            }
            output.write(this.data, previous, this.data.length - previous);
            return output.toByteArray();
        }

        private static final class SerializationHandle
        {
            private final int kind;
            private final SerializationDescriptor descriptor;
            private final SerializationUtf utf;
            private int roles;
            private MetadataEdit edit;

            private SerializationHandle(int kind, SerializationDescriptor descriptor, SerializationUtf utf)
            {
                this.kind = kind;
                this.descriptor = descriptor;
                this.utf = utf;
            }
        }

        private static final class SerializationDescriptor
        {
            private String name;
            private long uid;
            private int flags;
            private final List<SerializationField> fields = new java.util.ArrayList<>();
            private SerializationHandle parent;
        }

        private static final class SerializationField
        {
            private final char code;
            private final String name;
            private final String signature;

            private SerializationField(char code, String name, String signature)
            {
                this.code = code;
                this.name = name;
                this.signature = signature;
            }
        }

        private static final class SerializationUtf
        {
            private final int start;
            private final int end;
            private final int width;
            private final byte[] bytes;

            private SerializationUtf(int start, int end, int width, byte[] bytes)
            {
                this.start = start;
                this.end = end;
                this.width = width;
                this.bytes = bytes;
            }
        }

        private static final class MetadataEdit
        {
            private final int start;
            private final int end;
            private final byte[] bytes;

            private MetadataEdit(int start, int end, byte[] bytes)
            {
                this.start = start;
                this.end = end;
                this.bytes = bytes;
            }
        }
    }
}
