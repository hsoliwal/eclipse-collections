/*
 * Copyright (c) 2026 Hitesh Soliwal and Contributors to the Synexia Project.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.list.mutable;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import com.m3.collections.eclipse.api.RichIterable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class M3FastListArraySpanTest
{
    @TempDir
    Path temporary;

    @Test
    void callbackRetentionRangeAndSerializedContractsMatchFrozenOriginal() throws Exception
    {
        byte[] original;
        try (InputStream resource = this.getClass().getResourceAsStream(
                "/m3/array-spans-20261019/original/FastList.java.txt"))
        {
            assertNotNull(resource);
            original = resource.readAllBytes();
        }
        assertEquals("897e945ab4a10f0a106c5e29ddf2f49265a422d12a90f73733efdedc084b0c8e",
                HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(original)));
        Path source = this.temporary.resolve("src/com/m3/collections/eclipse/impl/list/mutable/FastList.java");
        Files.createDirectories(source.getParent());
        Files.write(source, original);
        Path output = this.temporary.resolve("original-classes");
        Files.createDirectories(output);
        URL implementation = FastList.class.getProtectionDomain().getCodeSource().getLocation();
        URL api = RichIterable.class.getProtectionDomain().getCodeSource().getLocation();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        assertNotNull(compiler, "the independent source oracle requires a JDK");
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        try (StandardJavaFileManager files = compiler.getStandardFileManager(diagnostics, null, null))
        {
            List<String> options = List.of("--release", "21", "-proc:none", "-d", output.toString(),
                    "-classpath", Path.of(implementation.toURI()) + File.pathSeparator + Path.of(api.toURI()));
            assertTrue(compiler.getTask(null, files, diagnostics, options, null,
                    files.getJavaFileObjects(source.toFile())).call(), diagnostics.getDiagnostics().toString());
        }
        long observations = M3FastListArraySpanProof.run(
                new URL[] {output.toUri().toURL(), implementation, api},
                new URL[] {implementation, api});
        assertEquals(71676L, observations, "all original-source contract observations executed");
    }
}
