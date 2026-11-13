<!-- SPDX-License-Identifier: EPL-1.0 OR BSD-3-Clause -->
# Eclipse Collections UnifiedMap values batch membership — exact owner, guarded array path

Target: `hsoliwal/eclipse-collections@0094cc5ca858fef12a8bfa576249f8d582334d6f`,
`eclipse-collections/src/main/java/com/m3/collections/eclipse/impl/map/mutable/UnifiedMap.java`.
Old Git blob `ca40e87c811ce56e6f2c658a3467482942966c52`; old complete UTF-8 SHA-256 `5f2ec4bed18d26cf771c83e60c90db2cd677cd93643f9d91643fe1ecdd46ba0c`. Proposed complete UTF-8 SHA-256 `7f1d35df0d044c070d0ee70956716059353898ba713221c6b84970f3470817d1`.

This is a **direct original-fork production candidate**, not a new M3 implementation, adapter, linked-entry map or additional persistent index. The only Java production edit is the nested `ValuesCollection.containsAll(Collection<?>)` method. Existing `Object[] table` and collision-chain arrays remain authoritative.

The original method scans map values for each query: O(Q*V). Admit a narrowly bounded fast path only for exact runtime class `UnifiedMap`, exact runtime `java.util.ArrayList` query collection, both sizes >= 128, at most 65,536 mapped values, and exclusively non-null `java.lang.String` objects on both sides. String is final/immutable, with natural ordering consistent with equality, so no user-supplied equals/hashCode/iterator hooks are eliminated. The fast path creates one operation-local `String[]`, sorts once, and uses binary search for each query: O(V log V + Q log V) time, O(V) ephemeral references, **zero extra retained entries**.

Any null, arbitrary value type, subclass or smaller collection uses the **identical original expression** `Iterate.allSatisfy(collection, Predicates.in(this))`. No change to public/protected API, JVM descriptors, serialization, map insertion, deletion, table ownership, comparator semantics, JNI, module boundaries or source package.

Standalone Java 21 exact-method atom proof executed:
- `javac --release 21 -Xlint:all -Werror`: PASS.
- `UnifiedMapValuesContainsAllProbe` 10,648 deterministic parity checks: PASS normal JVM, PASS `-Xint`.
- Exact `ArrayList` and exact-map gates, duplicate values, missing and present queries, null/mixed-type fallback, ArrayList subclass fallback and observable equality callbacks exercised.
- Three fresh size-sweep processes used seven alternating warmed samples per size. Candidate/old median time ratio at 128: **0.4261 / 0.3070 / 0.3181**; 512: **0.1090 / 0.1025 / 0.0922**; 2048: **0.0383 / 0.0379 / 0.0429**; 4096: **0.0198 / 0.0222 / 0.0215**. Standalone oracle only, not a full Eclipse implementation benchmark. At sizes under 128 the optimized path is not used.

Full Eclipse `unit-tests`, acceptance tests, `javap` of complete classes, checkstyle, default build, GC/retained heap/latency and native consumer tests are **NOT_RUN** locally. User contracts, OOME and unsupported concurrent mutations remain product qualification work. Do not merge to the default branch or present this as complete Eclipse Collections optimization until those gates pass. Only source-pinned patch, test and proof receipts belong in this draft.

Reference harness maintained in approved conversation workspace, not vendored as a parallel transformation framework. This special-case optimization should be reconciled under Synexia's canonical recipe system before M3JDK acceptance; it does not modify M3JDK.
