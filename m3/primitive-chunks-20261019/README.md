<!-- SPDX-FileCopyrightText: 2026 Hitesh Soliwal and Contributors to the Synexia Project -->
<!-- SPDX-License-Identifier: Apache-2.0 -->
# Primitive immutable chunk materialization

The seven-type generator and packed Boolean owner materialize immutable chunks
from existing payload ranges. Temporary mutable primitive chunks and duplicate
payload copies are removed. Singleton factories, covering-chunk identity, raw
floating bits and serialization behavior are retained.

The preceding ArrayAdapter and FastList changes already merged on master are
preserved. Both current-base native Maven packages and all contract checks pass.
The selected package reactor does not include the separate full unit-test reactor.
Run `mvn -B -ntp -pl eclipse-collections -am package` with JDK 21. On the shared
verification host the baseline compiler needed
`-Dmaven.compiler.meminitial=64m -Dmaven.compiler.maxmem=768m`; checks were unchanged.

The standalone contract exercises all eight types: 3,283,582 checks per version
and matching transcript -6431703903772675072. The seven native generated outputs
match the sealed source hashes. All field/method descriptors are unchanged.
JMH allocation falls in tested chunk shapes; some timing cells are inconclusive.
Full product/device qualification and universal performance admission remain open.

Canonical recipe, manifests, licenses, failure history and exact replay commands:
[6edef08c6ab5a3b29dad74a13d3f20df687e0ef5](https://github.com/hsoliwal/com.synexia/tree/6edef08c6ab5a3b29dad74a13d3f20df687e0ef5/synexia-openrewrite-recipes/crates/m3-all-fork-array-pass-20261019).

This receiver retains proof witnesses only. Reusable recipe ownership remains in
Synexia; the collection runtime has no Synexia dependency. First-party witnesses
are Apache-2.0; existing target source keeps its original upstream notices.
