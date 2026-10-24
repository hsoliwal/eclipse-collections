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
import com.m3.collections.eclipse.impl.list.fixed.ArrayAdapter;
import java.io.*;
import java.util.*;

/** Standalone consumer contract, run unchanged against both actual library builds. */
public final class ArrayAdapterConcatContract {
    private static long checks;
    private static void require(boolean condition) {
        checks++;
        if (!condition) throw new AssertionError("check " + checks);
    }
    public static void main(String[] args) throws Exception {
        Random random = new Random(932781);
        for (int a = 0; a <= 96; a++) for (int b = 0; b <= 96; b++) {
            Object[] left = new Object[a], right = new Object[b];
            for (int i = 0; i < a; i++) left[i] = i % 7 == 0 ? null : Integer.valueOf(random.nextInt());
            for (int i = 0; i < b; i++) right[i] = i % 5 == 0 ? null : Integer.valueOf(random.nextInt());
            var l = ArrayAdapter.adapt(left);
            var r = ArrayAdapter.adapt(right);
            var result = l.withAll(r);
            require((result == l) == (b == 0));
            require(result.size() == a + b);
            for (int i = 0; i < a + b; i++) require(result.get(i) == (i < a ? left[i] : right[i - a]));
            require(Arrays.equals(l.toArray(), left));
            require(Arrays.equals(r.toArray(), right));
            if (b != 0) {
                Object marker = new Object();
                result.set(result.size() - 1, marker);
                require(r.get(b - 1) != marker);
                right[0] = marker;
                if (b > 1) require(result.get(a) != marker);
            }
            var self = l.withAll(l);
            require(self.size() == a * 2);
            for (int i = 0; i < a; i++) require(self.get(i) == self.get(a + i));
        }
        String[] reified = {"a", "b"};
        ArrayAdapter<Object> wider = ArrayAdapter.<Object>adapt((Object[]) reified).withAll(ArrayAdapter.adapt(new String[]{"c"}));
        wider.set(1, Integer.valueOf(42));
        require(wider.get(1).equals(42));
        require(reified[1].equals("b"));
        var sameArray = ArrayAdapter.adapt(reified).withAll(ArrayAdapter.adapt(reified));
        require(sameArray.equals(Arrays.asList("a", "b", "a", "b")));
        StringBuilder trace = new StringBuilder();
        Iterable<Integer> custom = () -> {
            trace.append('I');
            return new Iterator<Integer>() {
                private int position;
                public boolean hasNext() { trace.append('H'); return position < 3; }
                public Integer next() { trace.append('N'); return position++; }
            };
        };
        require(ArrayAdapter.newArrayWith(9).withAll(custom).equals(Arrays.asList(9, 0, 1, 2)));
        Iterable<Integer> exceptional = () -> new Iterator<Integer>() {
            int n;
            public boolean hasNext() { return true; }
            public Integer next() { if (n++ == 2) throw new IllegalStateException("sentinel"); return n; }
        };
        var original = ArrayAdapter.newArrayWith(3, 4);
        try { original.withAll(exceptional); throw new AssertionError("exception lost"); }
        catch (IllegalStateException expected) { require(expected.getMessage().equals("sentinel")); }
        require(original.equals(Arrays.asList(3, 4)));
        var joined = original.withAll(ArrayAdapter.newArrayWith(5, null));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) { out.writeObject(joined); }
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            require(joined.equals(in.readObject()));
        }
        require(joined.stream().filter(Objects::nonNull).mapToInt(Integer::intValue).sum() == 12);
        System.out.println("checks=" + checks + " fallbackTrace=" + trace);
    }
}
