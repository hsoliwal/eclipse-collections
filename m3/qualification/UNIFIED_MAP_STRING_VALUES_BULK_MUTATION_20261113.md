<!-- SPDX-License-Identifier: EPL-1.0 OR BSD-3-Clause -->
# Direct Eclipse Collections UnifiedMap.ValuesCollection bulk remove/retain optimization
Source fork: hsoliwal/eclipse-collections, PR #27, base master at 0094cc5ca858fef12a8bfa576249f8d582334d6f. This is a direct product source change in the Eclipse Collections fork, not M3JDK code.

## Before code: contract and input admissibility
The existing `ValuesCollection.removeAll` / `retainAll` use `removeByValueCondition` and `removeFromChainByValue` with an ordinary `Collection.contains` lookup per stored map value, including chained collisions. Retain ALL existing table and chain compaction, callback/exception order, live count, serialization, null and iterator behavior on the generic path. The map's `Object[]`/chain `Object[]` arrays remain the only retained owner.

Enable an operation-local sorted `String[]` of membership probes only if:
- outer runtime map is EXACT `UnifiedMap`, and input class is EXACT `java.util.ArrayList`;
- map values count 128..65,536; input size 128..8,192;
- all input probes and all current map values are non-null `String` (final, equality consistent with compareTo).
Preflight MUST complete before any mutation; a rejected preflight invokes the exact original slow path. The sorted array is temporary and never attached to a map or view. Only private helper calls in the two mutation methods change; public/protected methods and signatures, table ownership and donor source provenance remain locked. Null collection is not admitted; the existing null failure behavior is retained by the original call path.

## Executed bounded local evidence
An isolated Java21 exact-table/chain oracle ran `javac -Xlint:all -Werror` then 2,139,648 equality/state checks across sizes 0..8191, input sizes 0..1024, direct and chained slots, mixed/null values, exact/subclass inputs and both remove/retain booleans. The full table, occupied count, chain compaction and value aliases matched the original under admitted inputs. Seven warmed pairs at 2,048 values/queries measured removeAll candidate/base 0.0317..0.0534 and retainAll 0.0301..0.0718 (standalone only). No whole-Eclipse project Maven compile, unit-test/serialization/GC or JMH was run; admission stays DRAFT. The size gate intentionally preserves small generic cases; repeated-value skew and unsupported concurrent modification need actual installed-package workload proof.
    
## Fork test and required next gates
Add real Eclipse `unit-tests` JUnit5 cases for direct/chained values, removeAll and retainAll, null/mixed fallback, String equality, absent input, and subclass hooks. Exact source pre/post Git blob, ordered diff, compile, tests then performance. No default branch modifications or M3JDK receiver transfer. Do not report a synthetic model as compiled Eclipse code.
