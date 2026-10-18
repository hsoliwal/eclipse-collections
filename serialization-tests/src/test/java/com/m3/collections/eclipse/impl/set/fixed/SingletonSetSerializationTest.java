/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.set.fixed;

import com.m3.collections.eclipse.api.factory.Sets;
import com.m3.collections.eclipse.impl.test.Verify;
import org.junit.jupiter.api.Test;

public class SingletonSetSerializationTest
{
    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyADNvcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLnNldC5maXhlZC5TaW5nbGV0b25T\n"
                        + "ZXQAAAAAAAAAAQwAAHhwcHg=",
                Sets.fixedSize.of(null));
    }
}
