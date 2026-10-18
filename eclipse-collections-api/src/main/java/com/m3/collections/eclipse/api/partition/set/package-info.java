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
 * This package contains interfaces for {@link com.m3.collections.eclipse.api.partition.set.PartitionSet}.
 * <p>
 *     A PartitionSet is the result of splitting a set into two sets based on a Predicate.
 * <p>
 * This package contains 4 interfaces:
 * <ul>
 *     <li>
 *         {@link com.m3.collections.eclipse.api.partition.set.PartitionSet} - a read-only PartitionSet API.
 *     </li>
 *     <li>
 *         {@link com.m3.collections.eclipse.api.partition.set.PartitionMutableSet} - a modifiable PartitionSet.
 *     </li>
 *     <li>
 *         {@link com.m3.collections.eclipse.api.partition.set.PartitionImmutableSet} - the non-modifiable equivalent interface to {@link com.m3.collections.eclipse.api.partition.set.PartitionMutableSet}.
 *     </li>
 *     <li>
 *         {@link com.m3.collections.eclipse.api.partition.set.PartitionUnsortedSet} - the result of splitting two UnsortedSetIterables based on a Predicate.
 *     </li>
 * </ul>
 */
package com.m3.collections.eclipse.api.partition.set;
