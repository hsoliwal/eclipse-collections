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
import java.lang.reflect.Field;
import java.util.Map;

/** Target-runtime proof of every explicitly relocated factory fallback mapping. */
public final class M3EclipseFactoryNamespaceProbe {
    private M3EclipseFactoryNamespaceProbe() { }
    public static void main(String[] args) throws ReflectiveOperationException {
        if (args.length != 0) throw new IllegalArgumentException("No arguments");
        String prefix = "com.m3.collections.eclipse.";
        Class<?> owner = Class.forName(prefix + "api.factory.ServiceLoaderUtils");
        Field field = owner.getDeclaredField("FACTORY_IMPL");
        field.setAccessible(true);
        Object value = field.get(null);
        if (!(value instanceof Map<?, ?> factories) || factories.isEmpty()) {
            throw new AssertionError("Factory inventory absent");
        }
        int checks = 0;
        int absent = 0;
        int instantiated = 0;
        for (Map.Entry<?, ?> entry : factories.entrySet()) {
            if (!(entry.getKey() instanceof String api) || !(entry.getValue() instanceof String implementation)
                    || !api.startsWith(prefix) || !implementation.startsWith(prefix)) {
                throw new AssertionError("Unexpected namespace: " + entry);
            }
            Class<?> contract;
            Class<?> provider;
            try {
                contract = Class.forName(api);
                provider = Class.forName(implementation);
            } catch (ClassNotFoundException missing) {
                String simple = api.substring(api.lastIndexOf('.') + 1);
                boolean inheritedAbsent = api.startsWith(prefix + "api.factory.map.primitive.")
                        && simple.matches("(Immutable|Mutable)Boolean(Boolean|Byte|Char|Double|Float|Int|Long|Short)MapFactory")
                        && implementation.equals(prefix + "impl.map."
                                + (simple.startsWith("Immutable") ? "immutable" : "mutable")
                                + ".primitive." + simple + "Impl");
                if (!inheritedAbsent) throw missing;
                for (String name : new String[] {api, implementation}) {
                    try { Class.forName(name); throw new AssertionError("Partially present baseline pair: " + entry); }
                    catch (ClassNotFoundException expected) { /* Frozen donor inventory contains this absent pair. */ }
                }
                absent++;
                checks++;
                continue;
            }
            Object instance = provider.getDeclaredConstructor().newInstance();
            if (!contract.isInstance(instance)) throw new AssertionError("Provider mismatch: " + entry);
            instantiated++;
            checks++;
        }
        if (checks != 255) throw new AssertionError("Expected frozen 255 factory mappings; observed " + checks);
        if (absent != 16 || instantiated != 239) throw new AssertionError("Unexpected factory closure: " + absent + "/" + instantiated);
        System.out.println("PASS M3_ECLIPSE_FACTORY_NAMESPACE mappings=" + checks
                + " instantiated=" + instantiated + " inherited_absent=" + absent);
    }
}
