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
import java.io.*;
import java.util.*;
public final class PrimitiveChunkContract {
  private static long checks, transcript = 1;
  private static void check(boolean condition) { checks++; if (!condition) throw new AssertionError(checks); }
  private static void record(long value) { transcript = 31 * transcript + value; }
  private static Object copy(Object value) throws Exception {
    var bytes = new ByteArrayOutputStream();
    try (var out = new ObjectOutputStream(bytes)) { out.writeObject(value); }
    try (var in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) { return in.readObject(); }
  }
  public static void main(String[] args) throws Exception {
    testByte();
    testShort();
    testChar();
    testInt();
    testLong();
    testFloat();
    testDouble();
    testBoolean();
    serializedBooleanBoundary();
    System.out.println("checks=" + checks + " transcript=" + transcript);
  }
  private static void serializedBooleanBoundary() throws Exception {
    String name = "com.m3.collections.eclipse.impl.list.immutable.primitive.ImmutableBooleanArrayList$ImmutableBooleanListSerializationProxy";
    Class<?> type = Class.forName(name);
    var constructor = type.getDeclaredConstructor(); constructor.setAccessible(true);
    var resolve = type.getDeclaredMethod("readResolve"); resolve.setAccessible(true);
    for (int size : new int[]{0,1,2,63,64,65}) {
      var bytes = new ByteArrayOutputStream();
      try (var out = new ObjectOutputStream(bytes)) {
        out.writeInt(size);
        for (int i = 0; i < size; i++) out.writeBoolean((i & 1) == 0);
      }
      var proxy = (Externalizable) constructor.newInstance();
      try (var input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) { proxy.readExternal(input); }
      var list = (com.m3.collections.eclipse.api.list.primitive.ImmutableBooleanList) resolve.invoke(proxy);
      for (int width : new int[]{1,2,64,Integer.MAX_VALUE}) {
        var chunks = list.chunk(width);
        // The serialized array owner reports notEmpty() even at size zero.
        // Its existing contract wraps that owner in one result chunk.
        check(chunks.size() == (size <= width ? 1 : 1 + (size - 1) / width));
        int offset = 0;
        for (var chunk : chunks) for (boolean value : chunk.toArray()) { check(value == ((offset++ & 1) == 0)); }
        check(offset == size); record(chunks.hashCode());
      }
      try { list.chunk(0); throw new AssertionError("Serialized array owner validates width"); }
      catch (IllegalArgumentException expected) { check(expected.getMessage().equals("Size for groups must be positive but was: 0")); }
    }
  }
  private static void testByte() throws Exception {
    var random = new Random(707146L);
    for (int run = 0; run < 65; run++) {
      int n = run < 12 ? new int[]{0,1,2,3,7,8,63,64,65,127,128,4097}[run] : random.nextInt(513);
      byte[] values = new byte[n];
      for (int i = 0; i < n; i++) values[i] = (byte) random.nextInt();
      var list = com.m3.collections.eclipse.impl.factory.primitive.ByteLists.immutable.with(values);
      for (int width : new int[]{-1,0,1,2,3,7,8,63,64,65,127,128,129,257,4097,Integer.MAX_VALUE}) {
        if (width <= 0 && n == 0) { check(list.chunk(width).isEmpty()); continue; }
        if (width <= 0) {
          try { list.chunk(width); throw new AssertionError("invalid width"); }
          catch (IllegalArgumentException expected) { check(expected.getMessage().equals("Size for groups must be positive but was: " + width)); }
          continue;
        }
        var chunks = list.chunk(width);
        check(chunks.size() == (n == 0 ? 0 : 1 + (n - 1) / width));
        if (n > 0 && width >= n) check(chunks.iterator().next() == list);
        int offset = 0;
        for (var chunk : chunks) {
          byte[] actual = chunk.toArray(); int length = Math.min(width, n - offset);
          check(actual.length == length);
          var expected = com.m3.collections.eclipse.impl.factory.primitive.ByteLists.immutable.with(Arrays.copyOfRange(values, offset, offset + length));
          check(chunk.equals(expected)); check(chunk.getClass() == expected.getClass());
          record(chunk.hashCode()); record(chunk.getClass().getName().hashCode());
          for (int j = 0; j < length; j++) { byte a = actual[j], b = values[offset + j]; check(a == b); }
          if (length > 0) { actual[0] = (byte) random.nextInt(); check(chunk.equals(expected)); }
          offset += length;
        }
        check(offset == n);
        if (run < 12 && (width == 1 || width == 64 || width == Integer.MAX_VALUE)) check(chunks.equals(copy(chunks)));
      }
    }
  }
  private static void testShort() throws Exception {
    var random = new Random(707146L);
    for (int run = 0; run < 65; run++) {
      int n = run < 12 ? new int[]{0,1,2,3,7,8,63,64,65,127,128,4097}[run] : random.nextInt(513);
      short[] values = new short[n];
      for (int i = 0; i < n; i++) values[i] = (short) random.nextInt();
      var list = com.m3.collections.eclipse.impl.factory.primitive.ShortLists.immutable.with(values);
      for (int width : new int[]{-1,0,1,2,3,7,8,63,64,65,127,128,129,257,4097,Integer.MAX_VALUE}) {
        if (width <= 0 && n == 0) { check(list.chunk(width).isEmpty()); continue; }
        if (width <= 0) {
          try { list.chunk(width); throw new AssertionError("invalid width"); }
          catch (IllegalArgumentException expected) { check(expected.getMessage().equals("Size for groups must be positive but was: " + width)); }
          continue;
        }
        var chunks = list.chunk(width);
        check(chunks.size() == (n == 0 ? 0 : 1 + (n - 1) / width));
        if (n > 0 && width >= n) check(chunks.iterator().next() == list);
        int offset = 0;
        for (var chunk : chunks) {
          short[] actual = chunk.toArray(); int length = Math.min(width, n - offset);
          check(actual.length == length);
          var expected = com.m3.collections.eclipse.impl.factory.primitive.ShortLists.immutable.with(Arrays.copyOfRange(values, offset, offset + length));
          check(chunk.equals(expected)); check(chunk.getClass() == expected.getClass());
          record(chunk.hashCode()); record(chunk.getClass().getName().hashCode());
          for (int j = 0; j < length; j++) { short a = actual[j], b = values[offset + j]; check(a == b); }
          if (length > 0) { actual[0] = (short) random.nextInt(); check(chunk.equals(expected)); }
          offset += length;
        }
        check(offset == n);
        if (run < 12 && (width == 1 || width == 64 || width == Integer.MAX_VALUE)) check(chunks.equals(copy(chunks)));
      }
    }
  }
  private static void testChar() throws Exception {
    var random = new Random(707146L);
    for (int run = 0; run < 65; run++) {
      int n = run < 12 ? new int[]{0,1,2,3,7,8,63,64,65,127,128,4097}[run] : random.nextInt(513);
      char[] values = new char[n];
      for (int i = 0; i < n; i++) values[i] = (char) random.nextInt();
      var list = com.m3.collections.eclipse.impl.factory.primitive.CharLists.immutable.with(values);
      for (int width : new int[]{-1,0,1,2,3,7,8,63,64,65,127,128,129,257,4097,Integer.MAX_VALUE}) {
        if (width <= 0 && n == 0) { check(list.chunk(width).isEmpty()); continue; }
        if (width <= 0) {
          try { list.chunk(width); throw new AssertionError("invalid width"); }
          catch (IllegalArgumentException expected) { check(expected.getMessage().equals("Size for groups must be positive but was: " + width)); }
          continue;
        }
        var chunks = list.chunk(width);
        check(chunks.size() == (n == 0 ? 0 : 1 + (n - 1) / width));
        if (n > 0 && width >= n) check(chunks.iterator().next() == list);
        int offset = 0;
        for (var chunk : chunks) {
          char[] actual = chunk.toArray(); int length = Math.min(width, n - offset);
          check(actual.length == length);
          var expected = com.m3.collections.eclipse.impl.factory.primitive.CharLists.immutable.with(Arrays.copyOfRange(values, offset, offset + length));
          check(chunk.equals(expected)); check(chunk.getClass() == expected.getClass());
          record(chunk.hashCode()); record(chunk.getClass().getName().hashCode());
          for (int j = 0; j < length; j++) { char a = actual[j], b = values[offset + j]; check(a == b); }
          if (length > 0) { actual[0] = (char) random.nextInt(); check(chunk.equals(expected)); }
          offset += length;
        }
        check(offset == n);
        if (run < 12 && (width == 1 || width == 64 || width == Integer.MAX_VALUE)) check(chunks.equals(copy(chunks)));
      }
    }
  }
  private static void testInt() throws Exception {
    var random = new Random(707146L);
    for (int run = 0; run < 65; run++) {
      int n = run < 12 ? new int[]{0,1,2,3,7,8,63,64,65,127,128,4097}[run] : random.nextInt(513);
      int[] values = new int[n];
      for (int i = 0; i < n; i++) values[i] = random.nextInt();
      var list = com.m3.collections.eclipse.impl.factory.primitive.IntLists.immutable.with(values);
      for (int width : new int[]{-1,0,1,2,3,7,8,63,64,65,127,128,129,257,4097,Integer.MAX_VALUE}) {
        if (width <= 0 && n == 0) { check(list.chunk(width).isEmpty()); continue; }
        if (width <= 0) {
          try { list.chunk(width); throw new AssertionError("invalid width"); }
          catch (IllegalArgumentException expected) { check(expected.getMessage().equals("Size for groups must be positive but was: " + width)); }
          continue;
        }
        var chunks = list.chunk(width);
        check(chunks.size() == (n == 0 ? 0 : 1 + (n - 1) / width));
        if (n > 0 && width >= n) check(chunks.iterator().next() == list);
        int offset = 0;
        for (var chunk : chunks) {
          int[] actual = chunk.toArray(); int length = Math.min(width, n - offset);
          check(actual.length == length);
          var expected = com.m3.collections.eclipse.impl.factory.primitive.IntLists.immutable.with(Arrays.copyOfRange(values, offset, offset + length));
          check(chunk.equals(expected)); check(chunk.getClass() == expected.getClass());
          record(chunk.hashCode()); record(chunk.getClass().getName().hashCode());
          for (int j = 0; j < length; j++) { int a = actual[j], b = values[offset + j]; check(a == b); }
          if (length > 0) { actual[0] = random.nextInt(); check(chunk.equals(expected)); }
          offset += length;
        }
        check(offset == n);
        if (run < 12 && (width == 1 || width == 64 || width == Integer.MAX_VALUE)) check(chunks.equals(copy(chunks)));
      }
    }
  }
  private static void testLong() throws Exception {
    var random = new Random(707146L);
    for (int run = 0; run < 65; run++) {
      int n = run < 12 ? new int[]{0,1,2,3,7,8,63,64,65,127,128,4097}[run] : random.nextInt(513);
      long[] values = new long[n];
      for (int i = 0; i < n; i++) values[i] = random.nextLong();
      var list = com.m3.collections.eclipse.impl.factory.primitive.LongLists.immutable.with(values);
      for (int width : new int[]{-1,0,1,2,3,7,8,63,64,65,127,128,129,257,4097,Integer.MAX_VALUE}) {
        if (width <= 0 && n == 0) { check(list.chunk(width).isEmpty()); continue; }
        if (width <= 0) {
          try { list.chunk(width); throw new AssertionError("invalid width"); }
          catch (IllegalArgumentException expected) { check(expected.getMessage().equals("Size for groups must be positive but was: " + width)); }
          continue;
        }
        var chunks = list.chunk(width);
        check(chunks.size() == (n == 0 ? 0 : 1 + (n - 1) / width));
        if (n > 0 && width >= n) check(chunks.iterator().next() == list);
        int offset = 0;
        for (var chunk : chunks) {
          long[] actual = chunk.toArray(); int length = Math.min(width, n - offset);
          check(actual.length == length);
          var expected = com.m3.collections.eclipse.impl.factory.primitive.LongLists.immutable.with(Arrays.copyOfRange(values, offset, offset + length));
          check(chunk.equals(expected)); check(chunk.getClass() == expected.getClass());
          record(chunk.hashCode()); record(chunk.getClass().getName().hashCode());
          for (int j = 0; j < length; j++) { long a = actual[j], b = values[offset + j]; check(a == b); }
          if (length > 0) { actual[0] = random.nextLong(); check(chunk.equals(expected)); }
          offset += length;
        }
        check(offset == n);
        if (run < 12 && (width == 1 || width == 64 || width == Integer.MAX_VALUE)) check(chunks.equals(copy(chunks)));
      }
    }
  }
  private static void testFloat() throws Exception {
    var random = new Random(707146L);
    for (int run = 0; run < 65; run++) {
      int n = run < 12 ? new int[]{0,1,2,3,7,8,63,64,65,127,128,4097}[run] : random.nextInt(513);
      float[] values = new float[n];
      for (int i = 0; i < n; i++) values[i] = Float.intBitsToFloat(random.nextInt());
      var list = com.m3.collections.eclipse.impl.factory.primitive.FloatLists.immutable.with(values);
      for (int width : new int[]{-1,0,1,2,3,7,8,63,64,65,127,128,129,257,4097,Integer.MAX_VALUE}) {
        if (width <= 0 && n == 0) { check(list.chunk(width).isEmpty()); continue; }
        if (width <= 0) {
          try { list.chunk(width); throw new AssertionError("invalid width"); }
          catch (IllegalArgumentException expected) { check(expected.getMessage().equals("Size for groups must be positive but was: " + width)); }
          continue;
        }
        var chunks = list.chunk(width);
        check(chunks.size() == (n == 0 ? 0 : 1 + (n - 1) / width));
        if (n > 0 && width >= n) check(chunks.iterator().next() == list);
        int offset = 0;
        for (var chunk : chunks) {
          float[] actual = chunk.toArray(); int length = Math.min(width, n - offset);
          check(actual.length == length);
          var expected = com.m3.collections.eclipse.impl.factory.primitive.FloatLists.immutable.with(Arrays.copyOfRange(values, offset, offset + length));
          check(chunk.equals(expected)); check(chunk.getClass() == expected.getClass());
          record(chunk.hashCode()); record(chunk.getClass().getName().hashCode());
          for (int j = 0; j < length; j++) { float a = actual[j], b = values[offset + j]; check(Float.floatToRawIntBits(a) == Float.floatToRawIntBits(b)); }
          if (length > 0) { actual[0] = Float.intBitsToFloat(random.nextInt()); check(chunk.equals(expected)); }
          offset += length;
        }
        check(offset == n);
        if (run < 12 && (width == 1 || width == 64 || width == Integer.MAX_VALUE)) check(chunks.equals(copy(chunks)));
      }
    }
  }
  private static void testDouble() throws Exception {
    var random = new Random(707146L);
    for (int run = 0; run < 65; run++) {
      int n = run < 12 ? new int[]{0,1,2,3,7,8,63,64,65,127,128,4097}[run] : random.nextInt(513);
      double[] values = new double[n];
      for (int i = 0; i < n; i++) values[i] = Double.longBitsToDouble(random.nextLong());
      var list = com.m3.collections.eclipse.impl.factory.primitive.DoubleLists.immutable.with(values);
      for (int width : new int[]{-1,0,1,2,3,7,8,63,64,65,127,128,129,257,4097,Integer.MAX_VALUE}) {
        if (width <= 0 && n == 0) { check(list.chunk(width).isEmpty()); continue; }
        if (width <= 0) {
          try { list.chunk(width); throw new AssertionError("invalid width"); }
          catch (IllegalArgumentException expected) { check(expected.getMessage().equals("Size for groups must be positive but was: " + width)); }
          continue;
        }
        var chunks = list.chunk(width);
        check(chunks.size() == (n == 0 ? 0 : 1 + (n - 1) / width));
        if (n > 0 && width >= n) check(chunks.iterator().next() == list);
        int offset = 0;
        for (var chunk : chunks) {
          double[] actual = chunk.toArray(); int length = Math.min(width, n - offset);
          check(actual.length == length);
          var expected = com.m3.collections.eclipse.impl.factory.primitive.DoubleLists.immutable.with(Arrays.copyOfRange(values, offset, offset + length));
          check(chunk.equals(expected)); check(chunk.getClass() == expected.getClass());
          record(chunk.hashCode()); record(chunk.getClass().getName().hashCode());
          for (int j = 0; j < length; j++) { double a = actual[j], b = values[offset + j]; check(Double.doubleToRawLongBits(a) == Double.doubleToRawLongBits(b)); }
          if (length > 0) { actual[0] = Double.longBitsToDouble(random.nextLong()); check(chunk.equals(expected)); }
          offset += length;
        }
        check(offset == n);
        if (run < 12 && (width == 1 || width == 64 || width == Integer.MAX_VALUE)) check(chunks.equals(copy(chunks)));
      }
    }
  }
  private static void testBoolean() throws Exception {
    var random = new Random(707146L);
    for (int run = 0; run < 65; run++) {
      int n = run < 12 ? new int[]{0,1,2,3,7,8,63,64,65,127,128,4097}[run] : random.nextInt(513);
      boolean[] values = new boolean[n];
      for (int i = 0; i < n; i++) values[i] = random.nextBoolean();
      var list = com.m3.collections.eclipse.api.factory.primitive.BooleanLists.immutable.with(values);
      for (int width : new int[]{-1,0,1,2,3,7,8,63,64,65,127,128,129,257,4097,Integer.MAX_VALUE}) {
        if (width <= 0 && n == 0) { check(list.chunk(width).isEmpty()); continue; }
        if (width <= 0) {
          try { list.chunk(width); throw new AssertionError("invalid width"); }
          catch (IllegalArgumentException expected) { check(expected.getMessage().equals("Size for groups must be positive but was: " + width)); }
          continue;
        }
        var chunks = list.chunk(width);
        check(chunks.size() == (n == 0 ? 0 : 1 + (n - 1) / width));
        if (n > 0 && width >= n) check(chunks.iterator().next() == list);
        int offset = 0;
        for (var chunk : chunks) {
          boolean[] actual = chunk.toArray(); int length = Math.min(width, n - offset);
          check(actual.length == length);
          var expected = com.m3.collections.eclipse.api.factory.primitive.BooleanLists.immutable.with(Arrays.copyOfRange(values, offset, offset + length));
          check(chunk.equals(expected)); check(chunk.getClass() == expected.getClass());
          record(chunk.hashCode()); record(chunk.getClass().getName().hashCode());
          for (int j = 0; j < length; j++) { boolean a = actual[j], b = values[offset + j]; check(a == b); }
          if (length > 0) { actual[0] = random.nextBoolean(); check(chunk.equals(expected)); }
          offset += length;
        }
        check(offset == n);
        if (run < 12 && (width == 1 || width == 64 || width == Integer.MAX_VALUE)) check(chunks.equals(copy(chunks)));
      }
    }
  }
}
