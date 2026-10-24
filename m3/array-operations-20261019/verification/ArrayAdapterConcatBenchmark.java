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
package com.synexia.verification;

import com.m3.collections.eclipse.impl.list.fixed.ArrayAdapter;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ArrayAdapterConcatBenchmark {
    @Param({"1", "8", "64", "4096"}) public int length;
    private ArrayAdapter<Object> left, right;
    @Setup public void setup() {
        Object[] a = new Object[length], b = new Object[length];
        for (int i = 0; i < length; i++) { a[i] = new Object(); b[i] = new Object(); }
        left = ArrayAdapter.adapt(a); right = ArrayAdapter.adapt(b);
    }
    @Benchmark public Object concatenate() { return left.withAll(right); }
}
