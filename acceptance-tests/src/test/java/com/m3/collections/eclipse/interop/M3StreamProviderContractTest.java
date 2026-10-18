/*
 * Copyright 2026 Hitesh Soliwal and contributors.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.m3.collections.eclipse.interop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import com.m3.collections.eclipse.api.list.MutableList;
import com.m3.collections.eclipse.impl.list.mutable.FastList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

/** Provider contract oracle for iterable snapshot semantics. */
public final class M3StreamProviderContractTest {
    @Test
    public void richIterablePreservesOccurrenceOrderWhileDetachedSnapshotsStayDetached() {
        MutableList<String> source = FastList.newList();
        source.add("duplicate");
        source.add("duplicate");
        source.add("tail");
        List<String> detached = new ArrayList<>();
        for (String value : source) detached.add(value);
        source.add("later");
        assertEquals(Arrays.asList("duplicate", "duplicate", "tail"), detached);
        assertEquals(Arrays.asList("duplicate", "duplicate", "tail", "later"), source);
        assertNotSame(source, detached);
    }
}
