// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 Hitesh Soliwal and contributors.
package com.m3.collections.eclipse.impl.map.sorted.mutable;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

public final class ComparatorPutAllCallbackOrderTest {
    private static final String PREFIX = "com.m3.collections.eclipse.impl.map.sorted.mutable.";
    private static final List<String> SHAPES = List.of("SOURCE_NULL", "TARGET_NULL", "EQUAL_DISTINCT", "UNEQUAL_DISTINCT", "IDENTITY");
    private static final List<String> EVENTS = new ArrayList<>();
    private static final List<String> EQUALS = new ArrayList<>();
    private static final class Ordering implements Comparator<Integer> {
        final String name;
        final int equalityGroup;
        Ordering(String name, int equalityGroup) { this.name = name; this.equalityGroup = equalityGroup; }
        @Override public int compare(Integer a, Integer b) { EVENTS.add(name + ".compare(" + a + "," + b + ")"); return Integer.compare(a, b); }
        @Override public boolean equals(Object other) {
            String event = name + ".equals(" + (other instanceof Ordering ? ((Ordering) other).name : String.valueOf(other)) + ")";
            EVENTS.add(event); EQUALS.add(event);
            return other instanceof Ordering && equalityGroup == ((Ordering) other).equalityGroup;
        }
        @Override public int hashCode() { return equalityGroup; }
    }
    private static final class Source extends TreeMap<Integer, String> {
        private static final long serialVersionUID = 1L;
        Source(Comparator<Integer> comparator) { super(comparator); }
        @Override public int size() { EVENTS.add("source.size"); return super.size(); }
        @Override public Comparator<? super Integer> comparator() { EVENTS.add("source.comparator"); return super.comparator(); }
        @Override public Set<Map.Entry<Integer, String>> entrySet() { EVENTS.add("source.entrySet"); return super.entrySet(); }
    }
    private static Map<String, Object> row(Object... parts) {
        Map<String, Object> out = new LinkedHashMap<>();
        for (int i = 0; i < parts.length; i += 2) out.put((String) parts[i], parts[i + 1]);
        return out;
    }
    private static String json(Object value) {
        if (value == null) return "null";
        if (value instanceof Number || value instanceof Boolean) return value.toString();
        List<String> parts = new ArrayList<>();
        if (value instanceof Map<?, ?>) {
            for (Map.Entry<?, ?> e : ((Map<?, ?>) value).entrySet()) parts.add(json(e.getKey().toString()) + ":" + json(e.getValue()));
            return "{" + String.join(",", parts) + "}";
        }
        if (value instanceof Iterable<?>) {
            for (Object item : (Iterable<?>) value) parts.add(json(item));
            return "[" + String.join(",", parts) + "]";
        }
        return "\"" + value.toString().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r") + "\"";
    }
    @SuppressWarnings("unchecked")
    private static SortedMap<Integer, String> target(String provider, Comparator<Integer> comparator) throws Exception {
        Constructor<?> constructor = Class.forName(provider).getDeclaredConstructor(Comparator.class);
        if (!constructor.trySetAccessible()) throw new IllegalAccessException("provider comparator constructor inaccessible");
        return (SortedMap<Integer, String>) constructor.newInstance(comparator);
    }
    private static String observe(String provider, String shape) throws Exception {
        Ordering targetComparator = new Ordering("target", 1);
        Ordering sourceComparator = new Ordering("source", shape.equals("UNEQUAL_DISTINCT") ? 2 : 1);
        if (shape.equals("IDENTITY")) sourceComparator = targetComparator;
        SortedMap<Integer, String> target = target(provider, shape.equals("TARGET_NULL") ? null : targetComparator);
        Source source = new Source(shape.equals("SOURCE_NULL") ? null : sourceComparator);
        source.put(1, "a"); source.put(2, "b");
        EVENTS.clear(); EQUALS.clear();
        Object outcome;
        try { target.putAll(source); outcome = row("kind", "return", "value", "completed"); }
        catch (RuntimeException error) { outcome = row("kind", "throw", "class", error.getClass().getName(), "message", error.getMessage()); }
        List<String> events = List.copyOf(EVENTS);
        List<String> equals = List.copyOf(EQUALS);
        List<Object> state = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : target.entrySet()) state.add(List.of(entry.getKey(), entry.getValue()));
        return json(row("kind", "case", "shape", shape, "events", events, "equalsEvents", equals, "outcome", outcome, "state", state));
    }
    private static void verify(String provider, String original) throws Exception {
        Properties expected = new Properties();
        String file = System.getProperty("putAll.expected");
        try (InputStream input = file == null ? ComparatorPutAllCallbackOrderTest.class.getResourceAsStream("/ComparatorPutAllCallbackOrderTest.expected.properties") : Files.newInputStream(Path.of(file))) {
            if (input == null) throw new IllegalStateException("sealed original comparator callback oracle required");
            expected.load(input);
        }
        List<Executable> assertions = new ArrayList<>();
        for (String shape : SHAPES) assertions.add(() -> {
            String encoded = expected.getProperty(original + "." + shape);
            if (encoded == null) throw new IllegalStateException("missing frozen original " + shape);
            assertEquals(new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8), observe(provider, shape), provider + "/" + shape);
        });
        assertAll(provider, assertions);
    }
    @Test public void rawComparatorCallbackOrder() throws Exception { verify(PREFIX + "IndexedRedBlackMap", "JDK"); }
    @Test public void helperComparatorCallbackOrder() throws Exception { verify(PREFIX + "IndexedSortedMap", "JDK"); }
    @Test public void publicComparatorCallbackOrder() throws Exception { verify(PREFIX + "TreeSortedMap", "PUBLIC"); }
    public static void main(String[] args) throws Exception {
        if (args.length != 1) throw new IllegalArgumentException("one provider classname required");
        Class<?> type = Class.forName(args[0]);
        byte[] bytes;
        try (InputStream input = type.getResourceAsStream("/" + type.getName().replace('.', '/') + ".class")) {
            if (input == null) throw new IllegalStateException("provider resource absent");
            bytes = input.readAllBytes();
        }
        System.out.println(json(row("kind", "identity", "provider", type.getName(), "classSHA256", HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes)))));
        for (String shape : SHAPES) System.out.println(observe(args[0], shape));
        System.out.println(json(row("kind", "footer", "casesExecuted", 5)));
    }
}
