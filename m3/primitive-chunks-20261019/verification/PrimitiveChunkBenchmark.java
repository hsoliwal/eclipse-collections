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
package com.synexia.collectionwitness;

import com.m3.collections.eclipse.api.list.primitive.ImmutableBooleanList;
import com.m3.collections.eclipse.api.list.primitive.ImmutableIntList;
import com.m3.collections.eclipse.api.list.primitive.ImmutableLongList;
import com.m3.collections.eclipse.api.list.primitive.ImmutableByteList;
import com.m3.collections.eclipse.api.list.primitive.ImmutableShortList;
import com.m3.collections.eclipse.api.list.primitive.ImmutableCharList;
import com.m3.collections.eclipse.api.list.primitive.ImmutableFloatList;
import com.m3.collections.eclipse.api.list.primitive.ImmutableDoubleList;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

/** Measures the actual fork classes; no replacement collection is embedded. */
@State(Scope.Thread)
public class PrimitiveChunkBenchmark {
  @Param({"2:1", "2:2", "8:2", "64:8", "4097:64", "8:2147483647"})
  public String shape;
  private int width;
  private ImmutableIntList ints;
  private ImmutableLongList longs;
  private ImmutableBooleanList booleans;
  private ImmutableByteList bytes;
  private ImmutableShortList shorts;
  private ImmutableCharList chars;
  private ImmutableFloatList floats;
  private ImmutableDoubleList doubles;

  @Setup
  public void setup() {
    String[] values = shape.split(":");
    int size = Integer.parseInt(values[0]);
    width = Integer.parseInt(values[1]);
    int[] a = new int[size];
    long[] b = new long[size];
    boolean[] c = new boolean[size];
    for (int i = 0; i < size; i++) {
      a[i] = i * 31;
      b[i] = (long) i * Integer.MAX_VALUE;
      c[i] = (i & 3) == 0;
    }
    ints = com.m3.collections.eclipse.impl.factory.primitive.IntLists.immutable.with(a);
    longs = com.m3.collections.eclipse.impl.factory.primitive.LongLists.immutable.with(b);
    booleans = com.m3.collections.eclipse.api.factory.primitive.BooleanLists.immutable.with(c);
    byte[] bytesInput = new byte[size];
    for (int i = 0; i < size; i++) bytesInput[i] = (byte) i;
    this.bytes = com.m3.collections.eclipse.impl.factory.primitive.ByteLists.immutable.with(bytesInput);
    short[] shortsInput = new short[size];
    for (int i = 0; i < size; i++) shortsInput[i] = (short) i;
    this.shorts = com.m3.collections.eclipse.impl.factory.primitive.ShortLists.immutable.with(shortsInput);
    char[] charsInput = new char[size];
    for (int i = 0; i < size; i++) charsInput[i] = (char) i;
    this.chars = com.m3.collections.eclipse.impl.factory.primitive.CharLists.immutable.with(charsInput);
    float[] floatsInput = new float[size];
    for (int i = 0; i < size; i++) floatsInput[i] = i * 1.25F;
    this.floats = com.m3.collections.eclipse.impl.factory.primitive.FloatLists.immutable.with(floatsInput);
    double[] doublesInput = new double[size];
    for (int i = 0; i < size; i++) doublesInput[i] = i * 1.25D;
    this.doubles = com.m3.collections.eclipse.impl.factory.primitive.DoubleLists.immutable.with(doublesInput);

  }

  @Benchmark public Object bytes() { return this.bytes.chunk(this.width); }
  @Benchmark public Object shorts() { return this.shorts.chunk(this.width); }
  @Benchmark public Object chars() { return this.chars.chunk(this.width); }
  @Benchmark public Object floats() { return this.floats.chunk(this.width); }
  @Benchmark public Object doubles() { return this.doubles.chunk(this.width); }
  @Benchmark public Object ints() { return this.ints.chunk(this.width); }
  @Benchmark public Object longs() { return this.longs.chunk(this.width); }
  @Benchmark public Object booleans() { return this.booleans.chunk(this.width); }
}
