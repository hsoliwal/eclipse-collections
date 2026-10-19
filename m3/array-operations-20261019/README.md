<!-- SPDX-FileCopyrightText: 2026 Hitesh Soliwal and Contributors to the Synexia Project -->
<!-- SPDX-License-Identifier: Apache-2.0 -->
# Array-operation candidate

ArrayAdapter.withAll constructs an intermediate mutable list, grows its backing arrays, then materializes another array. For another nonempty ArrayAdapter, this candidate builds the final Object[] once and copies both ranges. The general Iterable path, empty identity, nulls, aliases, exceptions and result independence are preserved.

1,420,476 checks per version; matching fallback callback trace. Changed-owner Checkstyle passes with the existing SuperClone warning. The fixed-CPU 8+8 JMH case measured about 89.97 -> 16.82 ns/op and 256 -> 96 B/op.

Full native Checkstyle fails on 88 existing relocation-proof violations. Full product tests, device workloads and universal performance admission remain pending.

Canonical source: [Synexia PR 12484](https://github.com/hsoliwal/com.synexia/pull/12484), commit `da475085ca326d91e20314c2fd7f895accc2b6cc`.

[Recipe, exact pins and replay instructions](https://github.com/hsoliwal/com.synexia/blob/da475085ca326d91e20314c2fd7f895accc2b6cc/synexia-openrewrite-recipes/crates/m3-array-fork-operations-20261019/README.md), [all timings and confidence intervals](https://github.com/hsoliwal/com.synexia/blob/da475085ca326d91e20314c2fd7f895accc2b6cc/synexia-openrewrite-recipes/crates/m3-array-fork-operations-20261019/benchmark-summary.json), and [raw evidence archive](https://github.com/hsoliwal/com.synexia/blob/da475085ca326d91e20314c2fd7f895accc2b6cc/synexia-openrewrite-recipes/crates/m3-array-fork-operations-20261019/evidence.tar.gz). The test-only Maven recipes materialized these production files exactly; their hashes are in `source-pins.json`.

All field/method descriptors match the baseline. The standalone Java contract and JMH benchmark are copied unchanged from that canonical packet. Compile against the actual relocated baseline class directory and run with either baseline or candidate-first classpaths. The TopK candidate retention assertion uses `-Dm3.candidate=true`. These witnesses do not stand in for the owning product native Maven suites.

This draft covers the listed owner only. All collection families in all hsoliwal forks remain in scope and incomplete. No default branch is changed and no merge is performed. Upstream source notices and licences remain intact; the new first-party witnesses use Apache-2.0.
