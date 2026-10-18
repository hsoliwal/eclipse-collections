/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.block.procedure;

import com.m3.collections.eclipse.impl.block.factory.Procedures;
import com.m3.collections.eclipse.impl.test.Verify;
import org.junit.Test;

public class NoopProcedureSerializationTest
{
    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                0L,
                "rO0ABX5yAENvcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLmZhY3RvcnkuUHJvY2Vk\n"
                        + "dXJlcyROb29wUHJvY2VkdXJlAAAAAAAAAAASAAB4cgAOamF2YS5sYW5nLkVudW0AAAAAAAAAABIA\n"
                        + "AHhwdAAISU5TVEFOQ0U=",
                Procedures.noop());
    }
}
