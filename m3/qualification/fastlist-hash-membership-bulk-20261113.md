<!-- SPDX-License-Identifier: Apache-2.0 -->
# Eclipse Collections: FastList.removeAll with array-backed HashSet membership

Receiver `hsoliwal/eclipse-collections/master@03ed01fd19a158a15dec00b7db372d8a598f626d`.
Owner `eclipse-collections/src/main/java/com/m3/collections/eclipse/impl/list/mutable/FastList.java`;
original Git blob `d5358d01d5d28ac6de736b4bd19acb2c32e92d24`.

## Source classification and preserved behavior

Inherited `AbstractMutableList.removeAll(Collection)` calls
`removeIfWith(Predicates2.in(), collection)`. FastList's `removeIfWith`
compacts its existing array efficiently; however, for an `ArrayList`
argument, each membership test `collection.contains(element)` scans an
array, producing worst-case O(n*m) probes for n list elements and m
candidate removals.

A strictly bounded **FastList-only override of the existing inherited public
signature** `removeAll(Collection<?>)` can use HashSet membership for
array compaction without changing the effective public interface. Admit the
optimized path only for exact runtime `FastList.class`, exact input
`java.util.ArrayList.class`, `size>=8192`, `512<=candidateSize<=65536`,
and estimated pair work at least four million. Every input and stored item
must have stable, final built-in equality: `null`, `String`, `Integer`,
or `Long`. Then construct one bounded HashSet from candidate values and
call the existing `removeIf(HashSet::contains)` array-compaction owner.

For all other types (including subclasses overriding `removeIfWith`,
mutable/asymmetric `equals` types, arrays of custom entries, linked
collections, small or oversized candidate lists) preserve
`super.removeAll(collection)` exactly. This prevents changing equality
call direction and order for objects with side effects, null behavior and
subclass dispatch. There is no additional list storage, JNI symbol,
serialization field or new public API contract; the new explicit override
merely specializes an already inherited Java Collection operation.

## Qualification

Documentation first. Require exact old/new Git blobs and hash-checked
source changes; JDK21 strict method-level original-vs-candidate differential
for small/large/null/duplicate values, mutation-sensitive custom equality,
subclasses and aliasing, plus 3-mode replay. Benchmark separately (no
unmeasured universal speedup claim). Add focused fork-native JUnit tests;
Maven/JUnit/CI and M3JDK21 downstream integration/ABI/JNI/jtreg remain HOLD
until their actual compiler/runtime receipts exist. Other existing Eclipse
UnifiedMap and FastList trim PRs remain untouched.
