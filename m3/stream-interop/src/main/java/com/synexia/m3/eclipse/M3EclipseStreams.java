/*******************************************************************************
 * Copyright (c) 2026 Synexia, Hitesh Soliwal (hsoliwal@gmail.com) and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0, or (at your option) the Eclipse
 * Public License 2.0, which are available at
 * https://www.apache.org/licenses/LICENSE-2.0 and
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: Apache-2.0 OR EPL-2.0
 *
 * Contributors:
 *     Hitesh Soliwal (Synexia) - initial API and implementation
 *******************************************************************************/
package com.synexia.m3.eclipse;

import com.m3.collections.M3ObjectStream;
import java.util.Objects;
import com.m3.collections.eclipse.api.RichIterable;
import com.m3.collections.eclipse.api.list.MutableList;
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

    /** Universal Iterable adapter for List, Set, Queue, Bag and other provider occurrences. */
    public static <T> M3ObjectStream<T> snapshotAny(Iterable<? extends T> source) {
        return M3ObjectStream.snapshot(Objects.requireNonNull(source, "source"));
    }

    /** Universal live adapter; encounter and fail-fast semantics remain provider-owned. */
    public static <T> M3ObjectStream<T> liveAny(java.util.Collection<T> source) {
        return M3ObjectStream.live(Objects.requireNonNull(source, "source"));
    }
}
