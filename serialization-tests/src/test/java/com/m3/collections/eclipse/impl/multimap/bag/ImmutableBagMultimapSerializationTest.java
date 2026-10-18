/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.multimap.bag;

import com.m3.collections.eclipse.api.multimap.MutableMultimap;
import com.m3.collections.eclipse.impl.multimap.ImmutableMultimapSerializationTestCase;

public class ImmutableBagMultimapSerializationTest extends ImmutableMultimapSerializationTestCase
{
    @Override
    protected MutableMultimap<String, String> createEmpty()
    {
        return HashBagMultimap.newMultimap();
    }

    @Override
    protected String getSerializedForm()
    {
        return "rO0ABXNyAGlvcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLm11bHRpbWFwLmJhZy5JbW11dGFi\n"
                + "bGVCYWdNdWx0aW1hcEltcGwkSW1tdXRhYmxlQmFnTXVsdGltYXBTZXJpYWxpemF0aW9uUHJveHkA\n"
                + "AAAAAAAAAQwAAHhwdwQAAAACdAABQXcEAAAAAnEAfgACdwQAAAABdAABQncEAAAAAnEAfgADdwQA\n"
                + "AAABcQB+AAJ3BAAAAAF4";
    }
}
