// SPDX-FileCopyrightText: 2026 Hitesh Soliwal and Contributors to the Synexia Project
// SPDX-License-Identifier: Apache-2.0
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
