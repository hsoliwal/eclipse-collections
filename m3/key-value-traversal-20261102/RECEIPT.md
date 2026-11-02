<!-- SPDX-License-Identifier: Apache-2.0 -->
<!-- SPDX-FileCopyrightText: 2026 Hitesh Soliwal and Contributors to the Synexia Project -->
# Key/value traversal maintenance receipt

Classification: routine maintenance of the existing public Eclipse Collections fork.
The existing indexed tree, successor algorithm, key/value lanes, public contracts,
and source licence notices are retained. New tests retain their emitted notices.

The receiving base is `4ab421e6e451ad79193a146218054cac71fe3881` and the bounded-clear predecessor is `04142d62e6f41a8a6a1d4b83bb4433199a73bd8e`.
This branch receives the predecessor clear implementation/test, then the key/value
traversal implementation and its additive tests. Changes are additive to that base.

`IndexedRedBlackMap` visits paired key/value array lanes with the existing successor
and callback boundary. `IndexedSortedMap` routes its inherited Java Map traversal
through that cursor. `TreeSortedMap` preserves its logical-empty procedure guard and
uses its existing value iterator for prefix partitioning. Public Entry identity,
detachment, reuse, comparator order, and fail-fast behavior remain covered by the
original tests. The public Entry cache remains unchanged.

## Validation

The original seven-module Maven test lifecycle compiled all 2,945 unit-test sources
and executed 985 selected sorted-map tests with zero failures, errors, or skips.
After the allocation test gained the existing provider's capability assumptions,
the exact native JUnit 6.1.3 closure reran the same 985 tests with zero failures,
errors, skips, or aborts. The production sources were unchanged by that test fix.
The focused JIT and interpreter runs each passed 24 tests with zero skips.

For a fresh 4,096-row map after separate-map warmup, key/value traversal allocated
262,264 bytes before this change and 56 bytes afterward in both JIT and interpreter
runs. This measures allocation; it is not a CPU-throughput claim. Unsupported
allocation instrumentation is handled by capability assumptions in the public test.
The qualifying environment was JDK 21.0.13 with Java 17 source compatibility.

## Received Java source digests

| File | SHA-256 |
| --- | --- |
| `IndexedRedBlackMap.java` | `2fe868617c9a0851d0ccb85011854a1cbf43ca4f95c33990da71f245e9c59771` |
| `IndexedSortedMap.java` | `6adf1b81e291fc8293e1aed2cf46238501dbb98e0eba251cf319c24f8a8ef3d7` |
| `TreeSortedMap.java` | `4d238d88781de2457b18628c022bb2e69959b5288059187416ddd6f060ed32bd` |
| `M3IndexedSortedMapClearProjectionTest.java` | `b3b6aac6ddcf5a88d7a8b4ec6a6d42f616306c21dd6ec8ff202dea5d1ee429e5` |
| `M3TreeTraversalProjectionTest.java` | `0f559a3efda61f5986e463aa641338180a0a5d965676656a69903cb9b2b63cdf` |
