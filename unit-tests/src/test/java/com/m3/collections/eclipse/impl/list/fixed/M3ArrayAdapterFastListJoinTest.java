/*
 * Copyright (c) 2026 Hitesh Soliwal and Contributors to the Synexia Project.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.m3.collections.eclipse.impl.list.fixed;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.m3.collections.eclipse.impl.list.mutable.FastList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

/** Native Eclipse Collections oracle for the exact FastList one-array join path. */
public class M3ArrayAdapterFastListJoinTest
{
    @Test
    public void exactFastListMatchesOriginalMutableListComposition()
    {
        Random random = new Random(0x5a1c01L);
        int[] sizes = {0, 1, 2, 3, 7, 8, 16, 31, 32, 63, 64, 127, 128, 255};
        for (int originalSize : sizes)
        {
            for (int appendedSize : sizes)
            {
                Object[] initial = new Object[originalSize];
                Object[] extra = new Object[appendedSize];
                for (int i = 0; i < originalSize; i++)
                {
                    initial[i] = random.nextInt(7) == 0 ? null : new Object();
                }
                for (int i = 0; i < appendedSize; i++)
                {
                    extra[i] = random.nextInt(5) == 0 ? null : new Object();
                }
                ArrayAdapter<Object> original = ArrayAdapter.adapt(initial);
                FastList<Object> appended = FastList.newListWith(extra);
                ArrayAdapter<Object> optimized = original.withAll(appended);

                if (appendedSize == 0)
                {
                    assertSame(original, optimized, "empty input returns the original adapter");
                    continue;
                }

                // Replicates the original fallback expression before specialization.
                ArrayAdapter<Object> expected =
                        ArrayAdapter.newArray(original.toList().withAll(appended));
                assertNotSame(original, optimized);
                assertArrayEquals(expected.toArray(), optimized.toArray());
                assertEquals(originalSize + appendedSize, optimized.size());

                // Both the retained source and the FastList may be mutated later.
                Object[] snapshot = optimized.toArray();
                if (initial.length > 0)
                {
                    initial[0] = new Object();
                }
                appended.clear();
                appended.add(new Object());
                assertArrayEquals(snapshot, optimized.toArray(), "joined array is detached");
                assertEquals(originalSize, original.size());
            }
        }
    }

    @Test
    public void ordinaryIterableAndNonFastListFallbackStillMatch()
    {
        List<Object> values = new ArrayList<>(Arrays.asList(null, "A", "B", null));
        ArrayAdapter<Object> original = ArrayAdapter.newArrayWith("start");
        Object[] expected = {"start", null, "A", "B", null};
        assertArrayEquals(expected, original.withAll(values).toArray());

        values.clear();
        assertSame(original, original.withAll(values));
    }
}
