# Eclipse Collections namespace rename

## Result

Applied recursive package/import relocation `org.eclipse.collections` → `com.m3.collections.eclipse` on branch `m3/package-import-rename-20261018`. Production generator, Maven plugin, API, implementation, testutils and the unit-test consumer closure compile successfully. Selected consumers passed **198 tests**, with zero failures, errors or skips. These Maven runs explicitly used diagnostic `checkstyle.skip=true`; the independent real lint gate remains **FAILED_BASELINE**.

The immutable original differential passed **97,402 checks** against the actual relocated EmptyIterator and IntervalUtils. The runtime factory proof validated all **255** relocated mapping entries, instantiated and checked **239** real providers, and strictly admitted exactly **16** Boolean-key primitive map pairs already absent in the pinned donor source. No product behavior was changed to fill that upstream inventory gap.

## Applied closure

The qualified OpenRewrite 8.17.1 namespace owner parsed 2,808 inventoried Java files and changed 2,806; two preexisting target leaves were exact matching postimages. The literal owner changed 517 exact Java literal tokens in three files. Structured resource adaptation changed 389 files: 373 generator templates, 12 Scala sources, two bnd files, root POM patterns and Checkstyle exclusions. A separately qualified import-order pass changed only import blocks in 15 owners, preserving import multisets, body/header bytes and import comments.

The incumbent exact POM snapshot configures plugin-plugin helpPackageName so generated HelpMojo also uses the new namespace without changing Maven coordinates. The incumbent workflow snapshot now recovers original classes and the independent original proof from immutable Git commit `fbaa8605dad1a84900fd38a1f6954de31b733f07`; its hash/oracle preparation was locally executed and passed. The workflow itself was not executed on GitHub during this work. The active standalone proof now explicitly refuses a same-class comparison, while the immutable original proof remains unchanged.

A separate hash-pinned Java snapshot corrected 465 active symbolic Javadoc references across 114 package-info files. Historical prose and URLs were preserved. Bounded JDK 21 javadoc generation over all 114 corrected files plus RichIterable succeeded. The proof guard compiled and its expected refusal was observed. Snapshot passes verified unchanged fixed points and drift/missing/duplicate refusal controls; occupied-target control is explicitly not applicable because those review crates add no files.

## Verification evidence

- `FINAL_SOURCE_HASHES.tsv`: all 3,197 distinct final applied source/metadata paths read back with their exact expected hashes, accounting for later import, POM, workflow and Javadoc passes.
- `CORE_DIAGNOSTIC_BUILD.log`: generator/plugin/API/implementation reactor BUILD SUCCESS.
- `CONSUMER_DIAGNOSTIC_TESTS.log`: testutils/unit-tests consumer reactor BUILD SUCCESS; AssertTest 98 tests, ListsTest/SetsTest/MapsTest 100 tests.
- `FACTORY_RUNTIME_CORRECTED.log`, `FACTORY_IMMUTABLE_INVENTORY.tsv`: actual provider closure plus immutable absent-pair inventory.
- `ORIGINAL_DIFFERENTIAL.log`: independent original differential, 97,402 checks.
- `BASELINE_PROOF_CHECKSTYLE.xml`, `BASELINE_PROOF_LINT_COMPARISON.txt`: direct Checkstyle 10.18.2 over immutable original proof reproduced the same 88 findings as the pre-review relocated proof, by line/rule/message multiset. The original source hash is `23c9a956156b9f533785d6f4d5dc0de04709ecad371ef971db192356d0ea27a1`.
- `REVIEW_JAVADOC.log`, `REVIEW_SNAPSHOT.tsv`, `PROOF_GUARD_FORMAT.tsv`, `ACTIVE_PROOF_REFUSAL.log`: bounded review closure checks.

Maven used JDK 21, a 256 MiB Maven heap, 512 MiB compiler heap and one 512 MiB test fork. Source/metadata admission used exact preimage hashes; source changes were serially promoted. The initial namespace-only integrity receipts describe that initial stage. Final bytes also include the separately authorized import ordering, generator help package, CI repair, symbolic Javadoc references and first-party refusal guard.

## Limits and preserved contracts

Class and method simple names, production method bodies, original license/copyright notices, Maven groupId/artifactId/version coordinates, historical provenance and frozen serialization payloads are preserved. The historical proof keeps its Apache license; no relicensing or broad formatting was performed to satisfy the donor EPL-only lint header policy. The local diagnostic lint skip is not a passing lint claim, and the repaired workflow retains its real lint gate.

Frozen Base64 serialized forms embed original class descriptors and remain unchanged; serialization compatibility across this requested namespace change is not claimed. Scala benchmark compilation and release JPMS/OSGi artifact qualification are not claimed. Full donor test suites beyond the named 198 consumer tests are not claimed. The final Javadoc/proof review edits were applied after the consumer reactor; all production edits in that review are comment-only, and the changed standalone proof was separately compiled and exercised. Initial pre-rename baseline attempts encountered disk exhaustion and unexplained fork termination under resource pressure; neither was misreported as source failure.

The working copy fast-forwarded nonoverlapping upstream additions to `2f348a9c6d8b132a066094aa5e9897f744d0fe39`. Original workspace remains preserved. No child commit, push, upstream PR or issue was made.

## Independent metadata review correction

The older `.github/workflows/m3-source-relocation.yml` is retired to a read-only manual notice. Removed-path hashing, transformation and commit/push steps are no longer active. `.m3-relocation/README.md` now names the applied `com.m3.collections.eclipse` namespace, canonical Synexia implementation, applied packet, actual gate results and authoritative current verification workflow. `.m3-relocation/rewrite.yml` remains byte-identical to its historical Git baseline and is explicitly scoped as superseded provenance targeting the obsolete staging namespace.

These two metadata changes were applied through the incumbent hash-pinned text snapshot crate `eclipse-retired-relocation-metadata-20261018`; fixed point and drift/missing/duplicate refusal controls passed. The notice's shell step executed locally and passed. YAML inspection confirmed manual dispatch only, contents:read and no source-writing job. All final applied hashes were rechecked, now covering 3,199 distinct paths. Java source hashes and the current verification workflow were unchanged. No Maven rerun was needed or claimed for this metadata-only correction; the inherited lint gate remains FAILED_BASELINE. See `RETIRED_METADATA_SNAPSHOT.tsv`, its log and `RETIRED_METADATA_VERIFICATION.txt`.
