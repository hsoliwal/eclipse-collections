/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.api.factory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public final class ServiceLoaderUtils
{
    private static final Map<String, String> FACTORY_IMPL = new HashMap<>();

    static
    {
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.bag.ImmutableBagFactory", "com.m3.collections.eclipse.impl.bag.immutable.ImmutableBagFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.bag.MultiReaderBagFactory", "com.m3.collections.eclipse.impl.bag.mutable.MultiReaderMutableBagFactory");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.bag.MutableBagFactory", "com.m3.collections.eclipse.impl.bag.mutable.MutableBagFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.bag.primitive.ImmutableBooleanBagFactory", "com.m3.collections.eclipse.impl.bag.immutable.primitive.ImmutableBooleanBagFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.bag.primitive.ImmutableByteBagFactory", "com.m3.collections.eclipse.impl.bag.immutable.primitive.ImmutableByteBagFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.bag.primitive.ImmutableCharBagFactory", "com.m3.collections.eclipse.impl.bag.immutable.primitive.ImmutableCharBagFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.bag.primitive.ImmutableDoubleBagFactory", "com.m3.collections.eclipse.impl.bag.immutable.primitive.ImmutableDoubleBagFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.bag.primitive.ImmutableFloatBagFactory", "com.m3.collections.eclipse.impl.bag.immutable.primitive.ImmutableFloatBagFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.bag.primitive.ImmutableIntBagFactory", "com.m3.collections.eclipse.impl.bag.immutable.primitive.ImmutableIntBagFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.bag.primitive.ImmutableLongBagFactory", "com.m3.collections.eclipse.impl.bag.immutable.primitive.ImmutableLongBagFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.bag.primitive.ImmutableShortBagFactory", "com.m3.collections.eclipse.impl.bag.immutable.primitive.ImmutableShortBagFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.bag.primitive.MutableBooleanBagFactory", "com.m3.collections.eclipse.impl.bag.mutable.primitive.MutableBooleanBagFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.bag.primitive.MutableByteBagFactory", "com.m3.collections.eclipse.impl.bag.mutable.primitive.MutableByteBagFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.bag.primitive.MutableCharBagFactory", "com.m3.collections.eclipse.impl.bag.mutable.primitive.MutableCharBagFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.bag.primitive.MutableDoubleBagFactory", "com.m3.collections.eclipse.impl.bag.mutable.primitive.MutableDoubleBagFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.bag.primitive.MutableFloatBagFactory", "com.m3.collections.eclipse.impl.bag.mutable.primitive.MutableFloatBagFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.bag.primitive.MutableIntBagFactory", "com.m3.collections.eclipse.impl.bag.mutable.primitive.MutableIntBagFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.bag.primitive.MutableLongBagFactory", "com.m3.collections.eclipse.impl.bag.mutable.primitive.MutableLongBagFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.bag.primitive.MutableShortBagFactory", "com.m3.collections.eclipse.impl.bag.mutable.primitive.MutableShortBagFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.bag.sorted.ImmutableSortedBagFactory", "com.m3.collections.eclipse.impl.bag.sorted.immutable.ImmutableSortedBagFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.bag.sorted.MutableSortedBagFactory", "com.m3.collections.eclipse.impl.bag.sorted.mutable.MutableSortedBagFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.bimap.ImmutableBiMapFactory", "com.m3.collections.eclipse.impl.bimap.immutable.ImmutableBiMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.bimap.MutableBiMapFactory", "com.m3.collections.eclipse.impl.bimap.mutable.MutableBiMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.list.FixedSizeListFactory", "com.m3.collections.eclipse.impl.list.fixed.FixedSizeListFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.list.ImmutableListFactory", "com.m3.collections.eclipse.impl.list.immutable.ImmutableListFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.list.MultiReaderListFactory", "com.m3.collections.eclipse.impl.list.mutable.MultiReaderMutableListFactory");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.list.MutableListFactory", "com.m3.collections.eclipse.impl.list.mutable.MutableListFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.list.primitive.ImmutableBooleanListFactory", "com.m3.collections.eclipse.impl.list.immutable.primitive.ImmutableBooleanListFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.list.primitive.ImmutableByteListFactory", "com.m3.collections.eclipse.impl.list.immutable.primitive.ImmutableByteListFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.list.primitive.ImmutableCharListFactory", "com.m3.collections.eclipse.impl.list.immutable.primitive.ImmutableCharListFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.list.primitive.ImmutableDoubleListFactory", "com.m3.collections.eclipse.impl.list.immutable.primitive.ImmutableDoubleListFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.list.primitive.ImmutableFloatListFactory", "com.m3.collections.eclipse.impl.list.immutable.primitive.ImmutableFloatListFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.list.primitive.ImmutableIntListFactory", "com.m3.collections.eclipse.impl.list.immutable.primitive.ImmutableIntListFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.list.primitive.ImmutableLongListFactory", "com.m3.collections.eclipse.impl.list.immutable.primitive.ImmutableLongListFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.list.primitive.ImmutableShortListFactory", "com.m3.collections.eclipse.impl.list.immutable.primitive.ImmutableShortListFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.list.primitive.MutableBooleanListFactory", "com.m3.collections.eclipse.impl.list.mutable.primitive.MutableBooleanListFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.list.primitive.MutableByteListFactory", "com.m3.collections.eclipse.impl.list.mutable.primitive.MutableByteListFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.list.primitive.MutableCharListFactory", "com.m3.collections.eclipse.impl.list.mutable.primitive.MutableCharListFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.list.primitive.MutableDoubleListFactory", "com.m3.collections.eclipse.impl.list.mutable.primitive.MutableDoubleListFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.list.primitive.MutableFloatListFactory", "com.m3.collections.eclipse.impl.list.mutable.primitive.MutableFloatListFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.list.primitive.MutableIntListFactory", "com.m3.collections.eclipse.impl.list.mutable.primitive.MutableIntListFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.list.primitive.MutableLongListFactory", "com.m3.collections.eclipse.impl.list.mutable.primitive.MutableLongListFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.list.primitive.MutableShortListFactory", "com.m3.collections.eclipse.impl.list.mutable.primitive.MutableShortListFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.FixedSizeMapFactory", "com.m3.collections.eclipse.impl.map.fixed.FixedSizeMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.ImmutableMapFactory", "com.m3.collections.eclipse.impl.map.immutable.ImmutableMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.MutableMapFactory", "com.m3.collections.eclipse.impl.map.mutable.MutableMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableBooleanBooleanMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableBooleanBooleanMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableBooleanByteMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableBooleanByteMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableBooleanCharMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableBooleanCharMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableBooleanDoubleMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableBooleanDoubleMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableBooleanFloatMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableBooleanFloatMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableBooleanIntMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableBooleanIntMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableBooleanLongMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableBooleanLongMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableBooleanShortMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableBooleanShortMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableByteBooleanMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableByteBooleanMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableByteByteMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableByteByteMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableByteCharMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableByteCharMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableByteDoubleMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableByteDoubleMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableByteFloatMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableByteFloatMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableByteIntMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableByteIntMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableByteLongMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableByteLongMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableByteObjectMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableByteObjectMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableByteShortMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableByteShortMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableCharBooleanMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableCharBooleanMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableCharByteMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableCharByteMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableCharCharMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableCharCharMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableCharDoubleMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableCharDoubleMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableCharFloatMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableCharFloatMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableCharIntMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableCharIntMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableCharLongMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableCharLongMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableCharObjectMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableCharObjectMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableCharShortMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableCharShortMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableDoubleBooleanMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableDoubleBooleanMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableDoubleByteMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableDoubleByteMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableDoubleCharMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableDoubleCharMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableDoubleDoubleMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableDoubleDoubleMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableDoubleFloatMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableDoubleFloatMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableDoubleIntMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableDoubleIntMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableDoubleLongMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableDoubleLongMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableDoubleObjectMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableDoubleObjectMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableDoubleShortMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableDoubleShortMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableFloatBooleanMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableFloatBooleanMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableFloatByteMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableFloatByteMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableFloatCharMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableFloatCharMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableFloatDoubleMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableFloatDoubleMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableFloatFloatMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableFloatFloatMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableFloatIntMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableFloatIntMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableFloatLongMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableFloatLongMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableFloatObjectMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableFloatObjectMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableFloatShortMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableFloatShortMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableIntBooleanMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableIntBooleanMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableIntByteMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableIntByteMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableIntCharMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableIntCharMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableIntDoubleMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableIntDoubleMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableIntFloatMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableIntFloatMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableIntIntMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableIntIntMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableIntLongMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableIntLongMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableIntObjectMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableIntObjectMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableIntShortMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableIntShortMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableLongBooleanMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableLongBooleanMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableLongByteMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableLongByteMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableLongCharMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableLongCharMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableLongDoubleMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableLongDoubleMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableLongFloatMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableLongFloatMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableLongIntMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableLongIntMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableLongLongMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableLongLongMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableLongObjectMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableLongObjectMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableLongShortMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableLongShortMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableObjectBooleanMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableObjectBooleanMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableObjectByteMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableObjectByteMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableObjectCharMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableObjectCharMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableObjectDoubleMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableObjectDoubleMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableObjectFloatMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableObjectFloatMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableObjectIntMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableObjectIntMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableObjectLongMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableObjectLongMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableObjectShortMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableObjectShortMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableShortBooleanMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableShortBooleanMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableShortByteMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableShortByteMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableShortCharMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableShortCharMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableShortDoubleMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableShortDoubleMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableShortFloatMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableShortFloatMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableShortIntMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableShortIntMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableShortLongMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableShortLongMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableShortObjectMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableShortObjectMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.ImmutableShortShortMapFactory", "com.m3.collections.eclipse.impl.map.immutable.primitive.ImmutableShortShortMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableBooleanBooleanMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableBooleanBooleanMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableBooleanByteMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableBooleanByteMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableBooleanCharMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableBooleanCharMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableBooleanDoubleMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableBooleanDoubleMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableBooleanFloatMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableBooleanFloatMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableBooleanIntMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableBooleanIntMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableBooleanLongMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableBooleanLongMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableBooleanShortMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableBooleanShortMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableByteBooleanMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableByteBooleanMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableByteByteMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableByteByteMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableByteCharMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableByteCharMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableByteDoubleMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableByteDoubleMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableByteFloatMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableByteFloatMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableByteIntMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableByteIntMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableByteLongMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableByteLongMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableByteObjectMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableByteObjectMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableByteShortMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableByteShortMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableCharBooleanMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableCharBooleanMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableCharByteMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableCharByteMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableCharCharMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableCharCharMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableCharDoubleMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableCharDoubleMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableCharFloatMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableCharFloatMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableCharIntMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableCharIntMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableCharLongMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableCharLongMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableCharObjectMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableCharObjectMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableCharShortMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableCharShortMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableDoubleBooleanMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableDoubleBooleanMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableDoubleByteMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableDoubleByteMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableDoubleCharMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableDoubleCharMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableDoubleDoubleMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableDoubleDoubleMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableDoubleFloatMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableDoubleFloatMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableDoubleIntMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableDoubleIntMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableDoubleLongMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableDoubleLongMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableDoubleObjectMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableDoubleObjectMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableDoubleShortMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableDoubleShortMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableFloatBooleanMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableFloatBooleanMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableFloatByteMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableFloatByteMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableFloatCharMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableFloatCharMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableFloatDoubleMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableFloatDoubleMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableFloatFloatMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableFloatFloatMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableFloatIntMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableFloatIntMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableFloatLongMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableFloatLongMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableFloatObjectMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableFloatObjectMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableFloatShortMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableFloatShortMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableIntBooleanMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableIntBooleanMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableIntByteMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableIntByteMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableIntCharMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableIntCharMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableIntDoubleMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableIntDoubleMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableIntFloatMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableIntFloatMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableIntIntMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableIntIntMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableIntLongMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableIntLongMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableIntObjectMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableIntObjectMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableIntShortMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableIntShortMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableLongBooleanMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableLongBooleanMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableLongByteMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableLongByteMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableLongCharMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableLongCharMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableLongDoubleMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableLongDoubleMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableLongFloatMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableLongFloatMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableLongIntMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableLongIntMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableLongLongMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableLongLongMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableLongObjectMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableLongObjectMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableLongShortMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableLongShortMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableObjectBooleanHashingStrategyMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableObjectBooleanHashingStrategyMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableObjectBooleanMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableObjectBooleanMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableObjectByteHashingStrategyMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableObjectByteHashingStrategyMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableObjectByteMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableObjectByteMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableObjectCharHashingStrategyMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableObjectCharHashingStrategyMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableObjectCharMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableObjectCharMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableObjectDoubleHashingStrategyMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableObjectDoubleHashingStrategyMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableObjectDoubleMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableObjectDoubleMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableObjectFloatHashingStrategyMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableObjectFloatHashingStrategyMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableObjectFloatMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableObjectFloatMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableObjectIntHashingStrategyMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableObjectIntHashingStrategyMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableObjectIntMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableObjectIntMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableObjectLongHashingStrategyMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableObjectLongHashingStrategyMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableObjectLongMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableObjectLongMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableObjectShortHashingStrategyMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableObjectShortHashingStrategyMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableObjectShortMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableObjectShortMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableShortBooleanMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableShortBooleanMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableShortByteMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableShortByteMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableShortCharMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableShortCharMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableShortDoubleMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableShortDoubleMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableShortFloatMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableShortFloatMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableShortIntMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableShortIntMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableShortLongMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableShortLongMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableShortObjectMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableShortObjectMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.primitive.MutableShortShortMapFactory", "com.m3.collections.eclipse.impl.map.mutable.primitive.MutableShortShortMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.sorted.ImmutableSortedMapFactory", "com.m3.collections.eclipse.impl.map.sorted.immutable.ImmutableSortedMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.map.sorted.MutableSortedMapFactory", "com.m3.collections.eclipse.impl.map.sorted.mutable.MutableSortedMapFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.set.FixedSizeSetFactory", "com.m3.collections.eclipse.impl.set.fixed.FixedSizeSetFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.set.ImmutableSetFactory", "com.m3.collections.eclipse.impl.set.immutable.ImmutableSetFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.set.MultiReaderSetFactory", "com.m3.collections.eclipse.impl.set.mutable.MultiReaderMutableSetFactory");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.set.MutableSetFactory", "com.m3.collections.eclipse.impl.set.mutable.MutableSetFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.set.primitive.ImmutableBooleanSetFactory", "com.m3.collections.eclipse.impl.set.immutable.primitive.ImmutableBooleanSetFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.set.primitive.ImmutableByteSetFactory", "com.m3.collections.eclipse.impl.set.immutable.primitive.ImmutableByteSetFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.set.primitive.ImmutableCharSetFactory", "com.m3.collections.eclipse.impl.set.immutable.primitive.ImmutableCharSetFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.set.primitive.ImmutableDoubleSetFactory", "com.m3.collections.eclipse.impl.set.immutable.primitive.ImmutableDoubleSetFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.set.primitive.ImmutableFloatSetFactory", "com.m3.collections.eclipse.impl.set.immutable.primitive.ImmutableFloatSetFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.set.primitive.ImmutableIntSetFactory", "com.m3.collections.eclipse.impl.set.immutable.primitive.ImmutableIntSetFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.set.primitive.ImmutableLongSetFactory", "com.m3.collections.eclipse.impl.set.immutable.primitive.ImmutableLongSetFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.set.primitive.ImmutableShortSetFactory", "com.m3.collections.eclipse.impl.set.immutable.primitive.ImmutableShortSetFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.set.primitive.MutableBooleanSetFactory", "com.m3.collections.eclipse.impl.set.mutable.primitive.MutableBooleanSetFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.set.primitive.MutableByteSetFactory", "com.m3.collections.eclipse.impl.set.mutable.primitive.MutableByteSetFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.set.primitive.MutableCharSetFactory", "com.m3.collections.eclipse.impl.set.mutable.primitive.MutableCharSetFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.set.primitive.MutableDoubleSetFactory", "com.m3.collections.eclipse.impl.set.mutable.primitive.MutableDoubleSetFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.set.primitive.MutableFloatSetFactory", "com.m3.collections.eclipse.impl.set.mutable.primitive.MutableFloatSetFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.set.primitive.MutableIntSetFactory", "com.m3.collections.eclipse.impl.set.mutable.primitive.MutableIntSetFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.set.primitive.MutableLongSetFactory", "com.m3.collections.eclipse.impl.set.mutable.primitive.MutableLongSetFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.set.primitive.MutableShortSetFactory", "com.m3.collections.eclipse.impl.set.mutable.primitive.MutableShortSetFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.set.sorted.ImmutableSortedSetFactory", "com.m3.collections.eclipse.impl.set.sorted.immutable.ImmutableSortedSetFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.set.sorted.MutableSortedSetFactory", "com.m3.collections.eclipse.impl.set.sorted.mutable.MutableSortedSetFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.stack.ImmutableStackFactory", "com.m3.collections.eclipse.impl.stack.immutable.ImmutableStackFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.stack.MutableStackFactory", "com.m3.collections.eclipse.impl.stack.mutable.MutableStackFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.stack.primitive.ImmutableBooleanStackFactory", "com.m3.collections.eclipse.impl.stack.immutable.primitive.ImmutableBooleanStackFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.stack.primitive.ImmutableByteStackFactory", "com.m3.collections.eclipse.impl.stack.immutable.primitive.ImmutableByteStackFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.stack.primitive.ImmutableCharStackFactory", "com.m3.collections.eclipse.impl.stack.immutable.primitive.ImmutableCharStackFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.stack.primitive.ImmutableDoubleStackFactory", "com.m3.collections.eclipse.impl.stack.immutable.primitive.ImmutableDoubleStackFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.stack.primitive.ImmutableFloatStackFactory", "com.m3.collections.eclipse.impl.stack.immutable.primitive.ImmutableFloatStackFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.stack.primitive.ImmutableIntStackFactory", "com.m3.collections.eclipse.impl.stack.immutable.primitive.ImmutableIntStackFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.stack.primitive.ImmutableLongStackFactory", "com.m3.collections.eclipse.impl.stack.immutable.primitive.ImmutableLongStackFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.stack.primitive.ImmutableShortStackFactory", "com.m3.collections.eclipse.impl.stack.immutable.primitive.ImmutableShortStackFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.stack.primitive.MutableBooleanStackFactory", "com.m3.collections.eclipse.impl.stack.mutable.primitive.MutableBooleanStackFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.stack.primitive.MutableByteStackFactory", "com.m3.collections.eclipse.impl.stack.mutable.primitive.MutableByteStackFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.stack.primitive.MutableCharStackFactory", "com.m3.collections.eclipse.impl.stack.mutable.primitive.MutableCharStackFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.stack.primitive.MutableDoubleStackFactory", "com.m3.collections.eclipse.impl.stack.mutable.primitive.MutableDoubleStackFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.stack.primitive.MutableFloatStackFactory", "com.m3.collections.eclipse.impl.stack.mutable.primitive.MutableFloatStackFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.stack.primitive.MutableIntStackFactory", "com.m3.collections.eclipse.impl.stack.mutable.primitive.MutableIntStackFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.stack.primitive.MutableLongStackFactory", "com.m3.collections.eclipse.impl.stack.mutable.primitive.MutableLongStackFactoryImpl");
        FACTORY_IMPL.put("com.m3.collections.eclipse.api.factory.stack.primitive.MutableShortStackFactory", "com.m3.collections.eclipse.impl.stack.mutable.primitive.MutableShortStackFactoryImpl");
    }

    private ServiceLoaderUtils()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    public static <T> T loadServiceClass(Class<T> serviceClass)
    {
        T result =
                ServiceLoaderUtils.loadServiceClass(serviceClass, Thread.currentThread().getContextClassLoader());
        if (result == null)
        {
            result = ServiceLoaderUtils.loadServiceClass(serviceClass, ServiceLoaderUtils.class.getClassLoader());
        }
        if (result == null)
        {
            result = ServiceLoaderUtils.loadByReflection(serviceClass, Thread.currentThread().getContextClassLoader());
        }
        if (result == null)
        {
            result = ServiceLoaderUtils.loadByReflection(serviceClass, ServiceLoaderUtils.class.getClassLoader());
        }
        if (result == null)
        {
            String message = "Could not find any implementations of "
                    + serviceClass.getSimpleName()
                    + ". Check that eclipse-collections.jar is on the classpath and that its META-INF/services directory is intact.";
            result = ServiceLoaderUtils.createProxyInstance(serviceClass, message);
        }
        return result;
    }

    private static <T> T loadServiceClass(Class<T> serviceClass, ClassLoader loader)
    {
        List<T> factories = new ArrayList<>();
        for (T factory : ServiceLoader.load(serviceClass, loader))
        {
            factories.add(factory);
        }
        if (factories.isEmpty())
        {
            return null;
        }
        if (factories.size() > 1)
        {
            String message = String.format(
                    "Found multiple implementations of %s on the classpath. Check that there is only one copy of eclipse-collections.jar on the classpath. Found implementations: %s.",
                    serviceClass.getSimpleName(),
                    factories.stream()
                            .map(T::getClass)
                            .map(Class::getSimpleName)
                            .collect(Collectors.joining(", ")));
            return ServiceLoaderUtils.createProxyInstance(serviceClass, message);
        }
        return factories.get(0);
    }

    private static <T> T loadByReflection(Class<T> serviceClass, ClassLoader loader)
    {
        String fallbackName = FACTORY_IMPL.get(serviceClass.getName());
        try
        {
            Class<T> fallbackClass = (Class<T>) Class.forName(fallbackName, true, loader);
            return fallbackClass.newInstance();
        }
        catch (Exception e)
        {
            // ignore
        }
        return null;
    }

    private static <T> T createProxyInstance(Class<T> serviceClass, String message)
    {
        InvocationHandler handler = new ThrowingInvocationHandler(message);
        Object proxyInstance = Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                handler);
        return serviceClass.cast(proxyInstance);
    }
}
