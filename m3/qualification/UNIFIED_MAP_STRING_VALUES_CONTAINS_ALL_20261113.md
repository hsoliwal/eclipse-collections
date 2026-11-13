<!-- SPDX-License-Identifier: EPL-1.0 OR BSD-3-Clause -->
# Eclipse Collections UnifiedMap values batch membership — exact owner, guarded array path

Target: `hsoliwal/eclipse-collections@0094cc5ca858fef12a8bfa576249f8d582334d6f`,
`eclipse-collections/src/main/java/com/m3/collections/eclipse/impl/map/mutable/UnifiedMap.java`.
Old Git blob `ca40e87c811ce56e6f2c658a3467482942966c52`; old complete UTF-8 SHA-256 `5f2ec4bed18d26cf771c83e60c90db2cd677cd93643f9d91643fe1ecdd46ba0c`. V2 containsAll-only SHA-256 `b9e06c1f4c647fcd38a562842ff4f4164d3a8c6bcb8e3b1db19a694ebc7d295c`; current combined-operations PR source SHA-256 `44214ec1bb91ea9f705fe9632c33684f13d4da7c70c3918002e64d72a003f4e1` (Git blob `64c1e0163f334c7716db1477213120910f87fe81`).

This is a **direct original-fork production candidate**, not a new M3 implementation, adapter, linked-entry map or additional persistent index. This document owns the nested `ValuesCollection.containsAll(Collection<?>)` sub-atom; the same review branch now also qualifies the sibling `removeAll` and `retainAll` operations in `UNIFIED_MAP_STRING_VALUES_BULK_MUTATION_20261113.md`. The combined fork source is pinned separately below. Existing `Object[] table` and collision-chain arrays remain authoritative.

The original method scans map values for each query: O(Q*V). Admit a narrowly bounded fast path only for exact runtime class `UnifiedMap`, exact runtime `java.util.ArrayList` query collection, both sizes >= 128, at most 65,536 mapped values, and exclusively non-null `java.lang.String` objects on both sides. String is final/immutable, with natural ordering consistent with equality, so no user-supplied equals/hashCode/iterator hooks are eliminated. The V2 fast path first checks up to 16 initial String queries with the original values-view lookup to preserve cheap early misses. Only continuing batches create one operation-local `String[]`, sort once, and use binary search for remaining queries: O(V log V + Q log V) time, O(V) ephemeral references, **zero extra retained entries**.

Any null, arbitrary value type, subclass or smaller collection uses the **identical original expression** `Iterate.allSatisfy(collection, Predicates.in(this))`. No change to public/protected API, JVM descriptors, serialization, map insertion, deletion, table ownership, comparator semantics, JNI, module boundaries or source package.

Standalone Java 21 exact-method atom proof executed:
- `javac --release 21 -Xlint:all -Werror`: PASS.
- `UnifiedMapValuesContainsAllProbe` 10,648 deterministic parity checks: PASS normal JVM, PASS `-Xint`.
- Exact `ArrayList` and exact-map gates, duplicate values, missing and present queries, null/mixed-type fallback, ArrayList subclass fallback and observable equality callbacks exercised.
- Initial V1 size-sweep showed large improvements on all-present batches but was **rejected** after an early-miss counterexample: first query absent at 128/512/2048/4096 had candidate/baseline ratios **4.61/3.83/10.11/5.77**, respectively. V2 adds bounded original-order prechecks. The first V2 local run passed the same **10,648 JIT and 10,648 interpreted parity checks**, and candidate/baseline ratios for all-present batches at 128/256/512/2048/4096 were **0.8030/0.4170/0.1430/0.0487/0.0284**. First-miss ratios at 128/512/2048/4096 were **0.85/0.45/1.20/1.13**; the small residual variation remains a **PERFORMANCE_HOLD**, not a claim of universal speedup. Standalone oracle only, not a full Eclipse implementation benchmark. Below both size thresholds the old path is retained.

Full Eclipse `unit-tests`, acceptance tests, `javap` of complete classes, checkstyle, default build, GC/retained heap/latency and native consumer tests are **NOT_RUN** locally. User contracts, OOME and unsupported concurrent mutations remain product qualification work. Do not merge to the default branch or present this as complete Eclipse Collections optimization until those gates pass. Only source-pinned patch, test and proof receipts belong in this draft.

Reference harness maintained in approved conversation workspace, not vendored as a parallel transformation framework. This special-case optimization should be reconciled under Synexia's canonical recipe system before M3JDK acceptance; it does not modify M3JDK.

## Candidate lineage / invalidation

V1 postimage SHA-256 `7f1d35df0d044c070d0ee70956716059353898ba713221c6b84970f3470817d1` is superseded and not admitted. V2 proposed postimage SHA-256 `b9e06c1f4c647fcd38a562842ff4f4164d3a8c6bcb8e3b1db19a694ebc7d295c` preserves old `containsAll` behavior for the first 16 ordered queries before building the temporary sorted array. The initial result remains review-only until actual Eclipse runtime/JMH gates pass.
