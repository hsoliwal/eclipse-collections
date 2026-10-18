<!-- SPDX-License-Identifier: Apache-2.0 -->
<!-- SPDX-FileCopyrightText: 2026 Hitesh Soliwal and contributors -->
# Eclipse namespace relocation: applied state and historical provenance

## Active source and verification

The current source tree has applied the recursive namespace mapping
`org.eclipse.collections` → `com.m3.collections.eclipse`, including package declarations,
imports, generated-source templates and required runtime metadata. Class and method
simple names and production behavior are preserved. Maven coordinates are unchanged.

The canonical implementation owner is the `synexia-openrewrite-recipes` module in
[hsoliwal/com.synexia](https://github.com/hsoliwal/com.synexia): its qualified
`M3CollectionsPackageRenameRecipe` and `M3CollectionsPackageRenameCli` perform the
source transformation. Exact literal/resource/import-order and hash-pinned snapshot
owners handle the separately admitted metadata closure. Use the canonical owner and
sealed manifests; do not rerun the historical local YAML recipe against this tree.

The applied task packet, complete source hashes, stage receipts and actual verification
results are in [`m3/package-import-rename-20261018`](../m3/package-import-rename-20261018/).
Read `TASK_PACKET.json`, `FINAL_SOURCE_HASHES.tsv`, `VERIFY_CONTRACT.tsv` and
`FINAL_REPORT.md` there before making further changes. The authoritative current CI
workflow is [m3-eclipse-package-first-20261018.yml](../.github/workflows/m3-eclipse-package-first-20261018.yml).
It compares the relocated leaves with immutable original source and an independent
original oracle. Its real lint gate remains enforced; the historical Apache proof's
88 inherited Checkstyle findings are recorded as `FAILED_BASELINE`. Successful local
diagnostic Maven runs explicitly used `checkstyle.skip=true` and do not establish a
passing lint gate. Consult the applied report for exact test/runtime evidence and limits.

The older [m3-source-relocation.yml](../.github/workflows/m3-source-relocation.yml)
is retired to a read-only manual notice. It neither hashes removed donor paths nor
transforms, commits or pushes source. It is not a build/test certification workflow.

## Historical recipe and provenance

The retained [`rewrite.yml`](rewrite.yml) is a historical, superseded recipe for an
unapplied staging proposal targeting `com.m3.sdk.eclipse.collections`. That destination
is obsolete for the active applied tree. The YAML is preserved unchanged as provenance,
not an executable instruction for current source. To inspect or reproduce that earlier
proposal, use its exact historical Git revision in a separate isolated worktree; do not
apply it to current source or infer current execution evidence from it.

Historical references, retained without claiming current applicability:

- Original donor fork: `hsoliwal/eclipse-collections`, commit `00a668d624c870652bf746d10f250a4d37a777f8`.
- Earlier canonical recipe proposal: [hsoliwal/com.synexia#12293](https://github.com/hsoliwal/com.synexia/pull/12293), revision `0f96532236f9d2b113b635f8c14884f64a75f6fc`.
- Earlier M3JDK21 product receiver: [#1293](https://github.com/hsoliwal/M3jdk21/pull/1293).
- Immutable donor oracle used by current verification: `fbaa8605dad1a84900fd38a1f6954de31b733f07`.

The earlier workflow and instructions remain available in Git history. Their former
recipe-only status, branch restriction and staging destination describe that historical
proposal; they do not override the applied packet or the currently authorized mapping.

## Licensing and scope

Original donor source retains its EPL-1.0/EDL-1.0 notices and copyright headers.
First-party Apache-2.0 materials retain their own notices. A package rename does not
relicense source. Frozen serialization payloads and release/benchmark compatibility
limits are documented in the applied report; this metadata correction adds no Java
source changes, compiler claims or new runtime architecture.
