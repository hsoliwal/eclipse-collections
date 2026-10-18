/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.factory;

import java.util.List;

import com.m3.collections.eclipse.api.factory.list.FixedSizeListFactory;
import com.m3.collections.eclipse.api.factory.list.ImmutableListFactory;
import com.m3.collections.eclipse.api.factory.list.MultiReaderListFactory;
import com.m3.collections.eclipse.api.factory.list.MutableListFactory;
import com.m3.collections.eclipse.api.list.MutableList;
import com.m3.collections.eclipse.impl.list.fixed.FixedSizeListFactoryImpl;
import com.m3.collections.eclipse.impl.list.immutable.ImmutableListFactoryImpl;
import com.m3.collections.eclipse.impl.list.mutable.ListAdapter;
import com.m3.collections.eclipse.impl.list.mutable.MultiReaderMutableListFactory;
import com.m3.collections.eclipse.impl.list.mutable.MutableListFactoryImpl;

/**
 * This class should be used to create instances of MutableList, ImmutableList and FixedSizeList
 * <p>
 * Mutable Examples:
 *
 * <pre>
 * MutableList&lt;String&gt; emptyList = Lists.mutable.empty();
 * MutableList&lt;String&gt; listWith = Lists.mutable.with("a", "b", "c");
 * MutableList&lt;String&gt; listOf = Lists.mutable.of("a", "b", "c");
 * </pre>
 *
 * Immutable Examples:
 *
 * <pre>
 * ImmutableList&lt;String&gt; emptyList = Lists.immutable.empty();
 * ImmutableList&lt;String&gt; listWith = Lists.immutable.with("a", "b", "c");
 * ImmutableList&lt;String&gt; listOf = Lists.immutable.of("a", "b", "c");
 * </pre>
 *
 * FixedSize Examples:
 *
 * <pre>
 * FixedSizeList&lt;String&gt; emptyList = Lists.fixedSize.empty();
 * FixedSizeList&lt;String&gt; listWith = Lists.fixedSize.with("a", "b", "c");
 * FixedSizeList&lt;String&gt; listOf = Lists.fixedSize.of("a", "b", "c");
 * </pre>
 */
@SuppressWarnings("ConstantNamingConvention")
public final class Lists
{
    public static final ImmutableListFactory immutable = ImmutableListFactoryImpl.INSTANCE;
    public static final MutableListFactory mutable = MutableListFactoryImpl.INSTANCE;
    public static final FixedSizeListFactory fixedSize = FixedSizeListFactoryImpl.INSTANCE;
    public static final MultiReaderListFactory multiReader = MultiReaderMutableListFactory.INSTANCE;

    private Lists()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    /**
     * @since 9.0.
     */
    public static <T> MutableList<T> adapt(List<T> list)
    {
        return ListAdapter.adapt(list);
    }
}
