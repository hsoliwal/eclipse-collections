/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.multimap.set.strategy;

import com.m3.collections.eclipse.api.multimap.MutableMultimap;
import com.m3.collections.eclipse.impl.block.factory.HashingStrategies;
import com.m3.collections.eclipse.impl.multimap.MutableMultimapSerializationTestCase;

public class UnifiedSetWithHashingStrategyMultimapSerializationTest
        extends MutableMultimapSerializationTestCase
{
    @Override
    protected String getSerializedForm()
    {
        return "rO0ABXNyAFhvcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLm11bHRpbWFwLnNldC5zdHJhdGVn\n"
                + "eS5VbmlmaWVkU2V0V2l0aEhhc2hpbmdTdHJhdGVneU11bHRpbWFwAAAAAAAAAAEMAAB4cHNyAExv\n"
                + "cmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLmZhY3RvcnkuSGFzaGluZ1N0cmF0ZWdp\n"
                + "ZXMkRGVmYXVsdFN0cmF0ZWd5AAAAAAAAAAECAAB4cHcEAAAAAnQAAUF3BAAAAAJxAH4ABHQAAUJx\n"
                + "AH4ABXcEAAAAAXEAfgAEeA==";
    }

    @Override
    protected MutableMultimap<String, String> createEmpty()
    {
        return new UnifiedSetWithHashingStrategyMultimap<>(HashingStrategies.defaultStrategy());
    }
}
