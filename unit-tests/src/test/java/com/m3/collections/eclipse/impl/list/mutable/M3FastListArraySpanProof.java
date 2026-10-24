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

package com.m3.collections.eclipse.impl.list.mutable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/** Independent original-source oracle for intrinsic FastList span clearing. */
public final class M3FastListArraySpanProof
{
    private static long observations;

    private M3FastListArraySpanProof()
    {
    }

    private static final class Owner
    {
        private final ClassLoader loader;
        private final Class<?> type;
        private final Constructor<?> constructor;
        private final Method removeRange;
        private final Method removeIfWith;
        private final Class<?> predicate2;
        private final Field items;

        private Owner(ClassLoader loader) throws ReflectiveOperationException
        {
            this.loader = loader;
            this.type = Class.forName("com.m3.collections.eclipse.impl.list.mutable.FastList", true, loader);
            this.constructor = this.type.getConstructor();
            this.removeRange = this.type.getMethod("removeRange", int.class, int.class);
            this.predicate2 = Class.forName("com.m3.collections.eclipse.api.block.predicate.Predicate2", true, loader);
            this.removeIfWith = this.type.getMethod("removeIfWith", this.predicate2, Object.class);
            this.items = this.type.getDeclaredField("items");
            this.items.setAccessible(true);
        }

        @SuppressWarnings("unchecked")
        private List<Object> fresh(int count) throws ReflectiveOperationException
        {
            List<Object> result = (List<Object>) this.constructor.newInstance();
            for (int index = 0; index < count; index++)
            {
                result.add(index % 7 == 0 ? null : index);
            }
            return result;
        }

        private Object[] storage(List<Object> list) throws IllegalAccessException
        {
            return (Object[]) this.items.get(list);
        }
    }

    @FunctionalInterface
    private interface Action
    {
        Object run() throws Exception;
    }

    private static Object outcome(Action action)
    {
        try
        {
            return action.run();
        }
        catch (Exception failure)
        {
            Throwable cause = failure instanceof InvocationTargetException ? failure.getCause() : failure;
            return cause.getClass().getName() + ":" + cause.getMessage();
        }
    }

    private static void require(boolean condition, String message)
    {
        observations++;
        if (!condition)
        {
            throw new AssertionError(message);
        }
    }

    private static void same(Owner original, List<Object> before, Owner generated,
            List<Object> after, Object expected, Object actual, String label) throws Exception
    {
        require(Objects.equals(expected, actual), label + " result: " + expected + " / " + actual);
        require(before.size() == after.size(), label + " size");
        require(Arrays.deepEquals(original.storage(before), generated.storage(after)), label + " all backing slots");
        require(Objects.equals(outcome(() -> Arrays.asList(before.toArray())),
                outcome(() -> Arrays.asList(after.toArray()))), label + " visible array");
    }

    private static void ranges(Owner original, Owner generated) throws Exception
    {
        for (int count = 0; count <= 32; count++)
        {
            for (int from = -2; from <= count + 2; from++)
            {
                for (int to = -2; to <= count + 2; to++)
                {
                    List<Object> before = original.fresh(count);
                    List<Object> after = generated.fresh(count);
                    int first = from;
                    int last = to;
                    same(original, before, generated, after,
                            outcome(() -> original.removeRange.invoke(before, first, last)),
                            outcome(() -> generated.removeRange.invoke(after, first, last)),
                            "range " + count + ':' + from + ':' + to);
                }
            }
        }
    }

    private static Predicate<Object> callback(Owner owner, List<Object> list,
            int mode, int failAt, List<String> trace)
    {
        return value ->
        {
            int call = trace.size();
            trace.add(call + ":" + value + ":" + list.size());
            if (call == failAt)
            {
                throw new IllegalArgumentException("predicate boundary " + call);
            }
            if (call == 0 && mode == 1)
            {
                list.add(10000);
            }
            if (call == 0 && mode == 2)
            {
                list.clear();
                return false;
            }
            if (mode == 3 && call == list.size() - 1)
            {
                try
                {
                    owner.items.set(list, new Object[] {"protected state sentinel"});
                }
                catch (IllegalAccessException failure)
                {
                    throw new AssertionError(failure);
                }
            }
            return mode == 3 || value == null || ((Integer) value & 1) == 0;
        };
    }

    private static void callbacks(Owner original, Owner generated) throws Exception
    {
        for (int count : new int[] {0, 1, 2, 3, 7, 8, 16, 31, 32, 65, 257})
        {
            for (int mode = 0; mode < 4; mode++)
            {
                for (int failAt : new int[] {-1, 0, 1, count / 2, count - 1})
                {
                    List<Object> before = original.fresh(count);
                    List<Object> after = generated.fresh(count);
                    List<String> oldTrace = new ArrayList<>();
                    List<String> newTrace = new ArrayList<>();
                    Predicate<Object> oldPredicate = callback(original, before, mode, failAt, oldTrace);
                    Predicate<Object> newPredicate = callback(generated, after, mode, failAt, newTrace);
                    same(original, before, generated, after,
                            outcome(() -> before.removeIf(oldPredicate)),
                            outcome(() -> after.removeIf(newPredicate)),
                            "predicate " + count + ':' + mode + ':' + failAt);
                    require(oldTrace.equals(newTrace), "callback order/count");
                }
            }
        }
        List<Object> before = original.fresh(0);
        List<Object> after = generated.fresh(0);
        original.items.set(before, null);
        generated.items.set(after, null);
        same(original, before, generated, after,
                outcome(() -> before.removeIf(value -> true)),
                outcome(() -> after.removeIf(value -> true)), "null protected empty storage");
    }

    private static Object parameterPredicate(Owner owner, List<String> trace)
    {
        return Proxy.newProxyInstance(owner.loader, new Class<?>[] {owner.predicate2}, (proxy, method, args) ->
        {
            if (!"accept".equals(method.getName()))
            {
                throw new AssertionError("unexpected predicate method: " + method);
            }
            trace.add(args[0] + ":" + args[1]);
            return args[0] == null || (Integer) args[0] % (Integer) args[1] == 0;
        });
    }

    private static void parameterCallbacks(Owner original, Owner generated) throws Exception
    {
        for (int count = 0; count <= 65; count++)
        {
            List<Object> before = original.fresh(count);
            List<Object> after = generated.fresh(count);
            List<String> oldTrace = new ArrayList<>();
            List<String> newTrace = new ArrayList<>();
            Object oldPredicate = parameterPredicate(original, oldTrace);
            Object newPredicate = parameterPredicate(generated, newTrace);
            same(original, before, generated, after,
                    outcome(() -> original.removeIfWith.invoke(before, oldPredicate, 3)),
                    outcome(() -> generated.removeIfWith.invoke(after, newPredicate, 3)), "parameter predicate " + count);
            require(oldTrace.equals(newTrace), "parameter callback order/count");
        }
    }

    private static byte[] serialize(Object value) throws Exception
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream stream = new ObjectOutputStream(bytes))
        {
            stream.writeObject(value);
        }
        return bytes.toByteArray();
    }

    private static Object deserialize(byte[] data, ClassLoader loader) throws Exception
    {
        try (ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(data))
        {
            @Override
            protected Class<?> resolveClass(ObjectStreamClass descriptor)
                    throws java.io.IOException, ClassNotFoundException
            {
                return Class.forName(descriptor.getName(), false, loader);
            }
        })
        {
            return stream.readObject();
        }
    }

    private static void serializedForms(Owner original, Owner generated) throws Exception
    {
        for (int count : new int[] {0, 1, 2, 8, 33, 64, 257, 1024})
        {
            List<Object> before = original.fresh(count);
            List<Object> after = generated.fresh(count);
            before.removeIf(value -> value == null || (Integer) value % 3 == 0);
            after.removeIf(value -> value == null || (Integer) value % 3 == 0);
            byte[] oldBytes = serialize(before);
            byte[] newBytes = serialize(after);
            require(Arrays.equals(oldBytes, newBytes), "serialized form " + count);
            require(before.equals(deserialize(newBytes, original.loader)), "new stream read by original");
            require(after.equals(deserialize(oldBytes, generated.loader)), "original stream read by successor");
        }
    }

    private static void protectedAliases(Owner original, Owner generated) throws Exception
    {
        for (int count : new int[] {0, 1, 2, 7, 33, 65})
        {
            List<Object> before = original.fresh(count);
            List<Object> after = generated.fresh(count);
            Object[] oldAlias = before.toArray();
            Object[] newAlias = after.toArray();
            original.items.set(before, oldAlias);
            generated.items.set(after, newAlias);
            same(original, before, generated, after,
                    outcome(() -> original.removeRange.invoke(before, count / 3, count)),
                    outcome(() -> generated.removeRange.invoke(after, count / 3, count)),
                    "protected array alias " + count);
            require(original.storage(before) == oldAlias && generated.storage(after) == newAlias,
                    "backing-array identity survives clearing");
            require(Arrays.deepEquals(oldAlias, newAlias), "caller-owned aliases see equal erasure");
        }
    }

    public static long run(URL[] beforePaths, URL[] afterPaths) throws Exception
    {
        observations = 0;
        try (URLClassLoader beforeLoader = new URLClassLoader(beforePaths, ClassLoader.getPlatformClassLoader());
                URLClassLoader afterLoader = new URLClassLoader(afterPaths, ClassLoader.getPlatformClassLoader()))
        {
            Owner original = new Owner(beforeLoader);
            Owner generated = new Owner(afterLoader);
            require(original.type != generated.type, "original and successor must use distinct definitions");
            require(!original.type.getProtectionDomain().getCodeSource().getLocation().equals(
                    generated.type.getProtectionDomain().getCodeSource().getLocation()),
                    "same-source oracle is refused");
            ranges(original, generated);
            callbacks(original, generated);
            parameterCallbacks(original, generated);
            serializedForms(original, generated);
            protectedAliases(original, generated);
        }
        return observations;
    }

    /** Arguments are original overlay, generated overlay, implementation closure and API closure. */
    public static void main(String[] args) throws Exception
    {
        if (args.length != 4)
        {
            throw new IllegalArgumentException("four original/generated/implementation/API class locations required");
        }
        URL original = Path.of(args[0]).toUri().toURL();
        URL generated = Path.of(args[1]).toUri().toURL();
        URL implementation = Path.of(args[2]).toUri().toURL();
        URL api = Path.of(args[3]).toUri().toURL();
        long count = run(new URL[] {original, implementation, api}, new URL[] {generated, implementation, api});
        System.getLogger(M3FastListArraySpanProof.class.getName()).log(System.Logger.Level.INFO,
                "M3_ECLIPSE_FASTLIST_ORIGINAL_DIFFERENTIAL_PASS observations=" + count);
    }
}
