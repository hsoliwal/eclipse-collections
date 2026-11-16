/*
 * Copyright (c) 2026 Synexia, Hitesh Soliwal and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.list.mutable.primitive;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eclipse.collections.api.BooleanIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.factory.primitive.BooleanLists;
import org.eclipse.collections.api.iterator.BooleanIterator;
import org.eclipse.collections.api.list.primitive.MutableBooleanList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Differential test of {@link BooleanArrayList#chunk(int)} against the element-by-element chunking it replaced.
 */
public class BooleanArrayListChunkTest
{
    private static final long SEED = 0x424f4f4c43484e4bL;
    private static final int TRIALS = 4000;
    private static final int MAX_SIZE = 400;

    /**
     * The former implementation, through the public API only: one iterator step and one add per element.
     */
    private static List<MutableBooleanList> elementByElement(BooleanArrayList list, int size)
    {
        List<MutableBooleanList> result = new ArrayList<>();
        if (list.notEmpty())
        {
            if (list.size() <= size)
            {
                result.add(BooleanLists.mutable.withAll(list));
            }
            else
            {
                BooleanIterator iterator = list.booleanIterator();
                while (iterator.hasNext())
                {
                    MutableBooleanList batch = BooleanLists.mutable.empty();
                    for (int i = 0; i < size && iterator.hasNext(); i++)
                    {
                        batch.add(iterator.next());
                    }
                    result.add(batch);
                }
            }
        }
        return result;
    }

    private static BooleanArrayList random(Random random, int size)
    {
        BooleanArrayList list = new BooleanArrayList();
        int flavour = random.nextInt(4);
        for (int i = 0; i < size; i++)
        {
            list.add(flavour == 0 ? random.nextBoolean() : flavour == 1 ? false : flavour == 2 || random.nextInt(8) == 0);
        }
        return list;
    }

    private static void assertSameChunks(BooleanArrayList list, int size)
    {
        boolean[] before = list.toArray();
        List<MutableBooleanList> expected = elementByElement(list, size);
        RichIterable<BooleanIterable> actual = list.chunk(size);
        assertEquals(expected.size(), actual.size(), "chunk count for size " + size);
        int index = 0;
        int total = 0;
        for (BooleanIterable chunk : actual)
        {
            MutableBooleanList reference = expected.get(index++);
            assertSame(reference.getClass(), chunk.getClass());
            assertEquals(reference.size(), chunk.size());
            assertArrayEquals(reference.toArray(), chunk.toArray());
            assertEquals(reference, chunk);
            assertEquals(reference.hashCode(), chunk.hashCode());
            total += chunk.size();
        }
        assertEquals(list.size(), total);
        assertArrayEquals(before, list.toArray(), "chunk must not change its source");
    }

    @Test
    public void randomListsMatchElementByElementChunking()
    {
        Random random = new Random(SEED);
        for (int trial = 0; trial < TRIALS; trial++)
        {
            BooleanArrayList list = random(random, random.nextInt(MAX_SIZE + 1));
            assertSameChunks(list, 1 + random.nextInt(MAX_SIZE));
        }
    }

    @Test
    public void wordBoundaries()
    {
        Random random = new Random(SEED + 1);
        for (int listSize : new int[]{1, 2, 63, 64, 65, 127, 128, 129, 191, 192, 193, 1000})
        {
            BooleanArrayList list = random(random, listSize);
            for (int size : new int[]{1, 2, 31, 32, 33, 63, 64, 65, 127, 128, 129, listSize, listSize + 1, Integer.MAX_VALUE})
            {
                assertSameChunks(list, size);
            }
        }
    }

    @Test
    public void chunksAreIndependentCopies()
    {
        BooleanArrayList list = BooleanArrayList.newListWith(true, false, true, true, false, false, true);
        RichIterable<BooleanIterable> chunks = list.chunk(3);
        MutableBooleanList first = (MutableBooleanList) chunks.getFirst();
        first.set(0, false);
        first.add(true);
        assertArrayEquals(new boolean[]{true, false, true, true, false, false, true}, list.toArray());
        list.set(1, true);
        assertArrayEquals(new boolean[]{false, false, true, true}, first.toArray());
        MutableBooleanList last = (MutableBooleanList) chunks.getLast();
        assertArrayEquals(new boolean[]{true}, last.toArray());
        last.addAll(false, true);
        assertArrayEquals(new boolean[]{true, false, true}, last.toArray());
    }

    @Test
    public void emptyAndInvalid()
    {
        assertTrue(new BooleanArrayList().chunk(3).isEmpty());
        assertThrows(IllegalArgumentException.class, () -> new BooleanArrayList().chunk(0));
        assertThrows(IllegalArgumentException.class, () -> BooleanArrayList.newListWith(true).chunk(-1));
    }
}
