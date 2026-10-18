// SPDX-FileCopyrightText: 2026 Hitesh Soliwal and Contributors to the Synexia Project
// SPDX-License-Identifier: Apache-2.0
package com.synexia.m3.eclipse;

import java.util.List;
import java.util.stream.Collectors;
import com.m3.collections.eclipse.impl.list.mutable.FastList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class M3EclipseStreamsTest {
    @Test void orderedDuplicateRichIterableAndLiveList() {
        var data = FastList.newListWith(1,1,2);
        var frozen = M3EclipseStreams.snapshotList(data);
        var live = M3EclipseStreams.liveList(data);
        data.add(3);
        assertEquals(List.of(1,1,2),frozen.collect(Collectors.toList()));
        assertEquals(List.of(1,1,2,3),live.collect(Collectors.toList()));
        assertEquals(List.of(2,2,4,6),live.map(x -> x*2).collect(Collectors.toList()));
        assertEquals(List.of(1,1,2,3),M3EclipseStreams.snapshot(data).collect(Collectors.toList()));
    }

    @Test void allIterableCollectionFamiliesUseTheSameOperators() {
        var list = new java.util.ArrayList<>(List.of(4,4));
        var snap = M3EclipseStreams.snapshotAny(list);
        var live = M3EclipseStreams.liveAny(list);
        list.add(5);
        assertEquals(List.of(4,4),snap.collect(Collectors.toList()));
        assertEquals(List.of(4,4,5),live.collect(Collectors.toList()));
        assertEquals(List.of(8,8,10),live.map(v -> v*2).collect(Collectors.toList()));
    }
}
