/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

/**
 * This package contains implementations of {@link com.m3.collections.eclipse.api.tuple.Pair}, {@link com.m3.collections.eclipse.api.tuple.Twin},
 * {@link com.m3.collections.eclipse.api.tuple.Triple} and {@link com.m3.collections.eclipse.api.tuple.Triplet} interfaces.
 * <p>
 *     A tuple is a container that holds two related objects.
 * <p>
 *     This package contains the following implementations:
 * <ul>
 *     <li>
 *          {@link com.m3.collections.eclipse.impl.tuple.ImmutableEntry} - an immutable {@link java.util.Map.Entry}.
 *     </li>
 *     <li>
 *          {@link com.m3.collections.eclipse.impl.tuple.PairImpl} - a container that holds two related objects.
 *     </li>
 *     <li>
 *          {@link com.m3.collections.eclipse.impl.tuple.Tuples} - a container that holds two related objects.
 *     </li>
 *     <li>
 *          {@link com.m3.collections.eclipse.impl.tuple.TwinImpl} - a convenience class for PairImpl, that has the same type for both items.
 *     </li>
 * </ul>
 */
package com.m3.collections.eclipse.impl.tuple;
