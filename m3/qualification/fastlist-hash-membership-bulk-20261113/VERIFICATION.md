<!-- SPDX-License-Identifier: Apache-2.0 -->
# Eclipse FastList guarded array membership — JDK21 method proof

Pinned receiver `hsoliwal/eclipse-collections/master@03ed01fd19a158a15dec00b7db372d8a598f626d`.
Existing source `eclipse-collections/src/main/java/com/m3/collections/eclipse/impl/list/mutable/FastList.java`.
Before Git blob `d5358d01d5d28ac6de736b4bd19acb2c32e92d24`;
candidate Git blob `378a350936ec57c4e8b86639e29eacd5207f1c25`.

## Concrete class-level operation

Inherited `AbstractMutableList.removeAll(Collection)` passed a list
membership predicate into `FastList.removeIfWith`, which already compacts
its internal array efficiently but performs O(m) `ArrayList.contains` for
each retained item. For an exact FastList and large exact ArrayList input
holding stable immutable values only, the override builds a bounded
`HashSet` once then passes `membership::contains` into the **existing**
`FastList.removeIf` array compactor. Runtime subclasses, custom equality
and other collections use the unchanged `super.removeAll` implementation.
The existing public inherited signature is preserved; no new public
contract, serialized field, JNI symbol or class relocation was added.

The first `8192/512` preflight offered negligible benefit in an isolated
method benchmark; the **final** gate is `size>=16384`, `512<=m<=65536`
and 64-bit pair work `size*m>=8000000`. This corrected admission was
documented before the source threshold patch.

## Executed isolated proof (not actual Eclipse reactor)

A Java21 **method-level reference model** using the same original
list-membership and candidate hash-membership compaction strategies was
compiled using `javac --release 21 -proc:none -Xlint:all -Werror`, exit
zero with no diagnostics (compiler log SHA-256
`e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855`).

- 3,840 seeded baseline-vs-candidate checks PASS, original order/size/return
  values identical; raw log SHA-256
  `9d3efbfc7e20fcd05c4ac6923ce361af8f8a33486c07eb033631db3cc8efeb4d`.
- 48-check bounded replay PASS in mixed, `-Xint`, `-Xcomp`; the three
  outputs are byte-identical with SHA-256
  `4d3c6602ba2741b14ceb65ffea289ecd82ea02426e5481c0378706f8eed2ccea`.
- Exploratory method-only `-Xbatch` median:
  `n=16384,m=512` 3,904,558 ns original vs 428,865 ns candidate;
  `n=16384,m=1024` 7,000,561 vs 390,098 ns. These are **not**
  measurements of the full Eclipse Collections class, JMH, or global
  throughput/GC effects. The 8,192-entry case now falls back.

Five fork-native JUnit 5 test methods are added in
`unit-tests/src/test/java/com/m3/collections/eclipse/impl/list/mutable/FastListBulkRemovalTest.java`.
They cover ordered removals, seeded inputs/thresholds, linked collections,
subclass `removeIfWith` overrides, custom equality and null refusal.
These are **authored, not run under the full Maven/JUnit engine**.
Actual-class benchmark source lives separately under this task directory.

## Auditable artifact and HOLD

A portable conversation proof ZIP,
`eclipse_fastlist_bulk_membership_method_proof_20261113.zip`, has
SHA-256 `3eb3fd9e8dec39ca8005b225be0844726ed958da233c1e73b2040b601cbf3d52`.
The ZIP contains 20 members with a checked SHA256SUMS manifest, strict
compiler log, all three Java runtime replay logs, original/candidate
method model and standalone benchmark. ZIP CRC and all file digests PASS.
No binary/test shim was copied into the fork source tree.

**HOLD:** actual full FastList dependency-closure compilation, incumbent
Eclipse JUnit/Maven tests, new test execution, proper JMH, hosted CI,
M3JDK21 product/JNI/jtreg receiving parity and licence gate.
This is one bounded collection-source optimization, not the entire fork's
qualification; do not automatically merge.
