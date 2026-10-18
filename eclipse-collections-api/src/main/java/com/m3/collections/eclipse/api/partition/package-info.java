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
 * This package contains interfaces for {@link com.m3.collections.eclipse.api.partition.PartitionIterable}.
 * <p>
 *     A PartitionIterable is the result of splitting an iterable into two iterables based on a {@link com.m3.collections.eclipse.api.block.predicate.Predicate}.
 * <p>
 *      This package contains 3 interfaces:
 * <ul>
 *     <li>
 *         {@link com.m3.collections.eclipse.api.partition.PartitionIterable} - a read-only PartitionIterable API.
 *     </li>
 *     <li>
 *         {@link com.m3.collections.eclipse.api.partition.PartitionMutableCollection} - a modifiable PartitionIterable.
 *     </li>
 *     <li>
 *         {@link com.m3.collections.eclipse.api.partition.PartitionImmutableCollection} - the non-modifiable equivalent interface to {@link com.m3.collections.eclipse.api.partition.PartitionMutableCollection}.
 *     </li>
 * </ul>
 */
package com.m3.collections.eclipse.api.partition;
