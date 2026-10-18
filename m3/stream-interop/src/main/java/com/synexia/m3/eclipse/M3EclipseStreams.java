// SPDX-FileCopyrightText: 2026 Hitesh Soliwal and Contributors to the Synexia Project
// SPDX-License-Identifier: Apache-2.0
package com.synexia.m3.eclipse;

import com.m3.collections.M3ObjectStream;
import java.util.Objects;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.list.MutableList;
/** Opt-in RichIterable/MutableList gateway into M3JDK21 operations. */
public final class M3EclipseStreams {
    private M3EclipseStreams() {}
    public static <T> M3ObjectStream<T> snapshot(RichIterable<T> input) {
        return M3ObjectStream.snapshot((Iterable<T>) Objects.requireNonNull(input, "input"));
    }
    public static <T> M3ObjectStream<T> snapshotList(MutableList<T> input) {
        return M3ObjectStream.snapshot(Objects.requireNonNull(input, "input"));
    }
    public static <T> M3ObjectStream<T> liveList(MutableList<T> input) {
        return M3ObjectStream.live(Objects.requireNonNull(input, "input"));
    }
}
