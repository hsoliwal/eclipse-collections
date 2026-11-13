/*
 * Copyright (c) 2026 Synexia and Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 */
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.HexFormat;

/**
 * Source-sealed Java 21 recipe to repair erased FastList backing-array inspection.
 * The production FastList algorithm and all of its signatures remain untouched.
 */
public final class FastListTestErasedBackingRecipe
{
    private static final String ORIGINAL_SHA256 =
            "018be7328c64fd49709766fa488abd0a96afb9ebef4d66f74ede697e61c57d96";
    private static final String RESULT_SHA256 =
            "334550b0206be35ac81828a21465937fa069db9f253e3314c26fb1bb43bd39f9";
    private static final String OLD_ACCESS = "list.items";
    private static final String NEW_ACCESS = "backing(list)";
    private static final String HELPER =
            "    /** Expose the erased backing lane without a runtime Integer[] checkcast. */\n"
            + "    private static Object[] backing(FastList<?> list)\n"
            + "    {\n"
            + "        return list.items;\n"
            + "    }\n";

    private FastListTestErasedBackingRecipe()
    {
    }

    private static String hash(byte[] data) throws Exception
    {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(data));
    }

    private static int occurrences(String input, String needle)
    {
        int count = 0;
        int at = 0;
        while ((at = input.indexOf(needle, at)) != -1)
        {
            count++;
            at += needle.length();
        }
        return count;
    }

    public static void main(String[] args) throws Exception
    {
        if (args.length != 2 || !("--apply".equals(args[0]) || "--check".equals(args[0])))
        {
            throw new IllegalArgumentException("usage: --apply|--check <FastListEmptyTrimArrayTest.java>");
        }
        Path target = Path.of(args[1]).toAbsolutePath().normalize();
        if (!"FastListEmptyTrimArrayTest.java".equals(target.getFileName().toString()))
        {
            throw new IllegalArgumentException("EXPECTED_FASTLIST_TEST");
        }
        byte[] beforeBytes = Files.readAllBytes(target);
        String before = hash(beforeBytes);
        String source = new String(beforeBytes, StandardCharsets.UTF_8);
        String result;
        if (ORIGINAL_SHA256.equals(before))
        {
            if (occurrences(source, OLD_ACCESS) != 9 || occurrences(source, NEW_ACCESS) != 0
                    || !source.endsWith("}\n"))
            {
                throw new IllegalStateException("SOURCE_SEAL_ANCHORS_CHANGED");
            }
            result = source.replace(OLD_ACCESS, NEW_ACCESS);
            result = result.substring(0, result.length() - 2) + HELPER + "}\n";
        }
        else if (RESULT_SHA256.equals(before))
        {
            if (occurrences(source, NEW_ACCESS) != 9 || occurrences(source, OLD_ACCESS) != 1
                    || occurrences(source, HELPER) != 1)
            {
                throw new IllegalStateException("POSTIMAGE_ANCHORS_CHANGED");
            }
            result = source;
        }
        else
        {
            throw new IllegalStateException("SOURCE_SEAL_SHA256_MISMATCH:" + before);
        }
        byte[] afterBytes = result.getBytes(StandardCharsets.UTF_8);
        String after = hash(afterBytes);
        if (!RESULT_SHA256.equals(after))
        {
            throw new IllegalStateException("UNPINNED_RECIPE_RESULT:" + after);
        }
        boolean changed = !before.equals(after);
        System.out.println("recipe=fastlist-test-erased-backing/1 changed=" + changed + " sha256=" + after);
        if ("--check".equals(args[0]) && changed)
        {
            throw new IllegalStateException("RECIPE_NOT_APPLIED");
        }
        if ("--apply".equals(args[0]) && changed)
        {
            Path temp = Files.createTempFile(target.getParent(), ".fastlist-test-", ".tmp");
            try
            {
                Files.write(temp, afterBytes);
                Files.move(temp, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            }
            finally
            {
                Files.deleteIfExists(temp);
            }
        }
    }
}
