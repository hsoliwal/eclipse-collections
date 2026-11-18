# Eclipse Collections agent instructions

## Clean output rule (owner directive, 2026-11-14) - read first

This rule overrides any older text in this file.

- This fork is clean output: one coherent Eclipse Collections repository. M3 work lives
  inside the project's own classes and packages, written the way the project
  writes its code. It is not stamped across folders.
- Do not add `m3/`, `.m3/`, `docs/m3/` or `tools/m3*` folders.
- Do not add any of the following. They belong in `hsoliwal/com.synexia`, the
  convergence workspace:
  - qualification bundles;
  - evidence, receipts and benchmark tables;
  - work orders;
  - donor copies;
  - recipe sources or recipe resources.
- Do not add parallel `M3`-prefixed types or `*.m3` packages where an existing
  class owns the behaviour. Change that class in place, keep it internal unless
  the owner admits new API, and follow the project's naming.
- A pull request here contains only the product change, plus its tests in the
  project's own test layout. Put the proof material in the paired `com.synexia`
  pull request and link to it.
- This fork's merged pull-request history shows the intended direction.
- Collection forks: arrayify and leetcodify the implementations here, then consolidate them into M3JDK (hsoliwal/M3jdk21), which owns the final implementations.


## Priority 0 — additive superset, best-proven algorithms and full-history checkpoints (2026-11-18)

All work on this fork must preserve the **backward-compatible optimized superset** of applicable previously admitted upstream and fork capabilities, stable public API/ABI, behavior, tests and proven fixes/optimizations. Select the best *validated* candidate after comparing incumbent implementations and old/new branch and PR variants for correctness, speed, retained allocation and memory, portability, and maintainability. Add compatible APIs; never silently remove features, change contracts or discard good earlier code. Before every source promotion or merge, inspect earliest reachable ancestors plus relevant current/archived branches, tags, merged/closed/reverted PRs, merge bases and both merge parents for losses, bad commits and reintroduced regressions. Do not rebase `develop`. Record last checked date and exact commit SHA per ref, scan coverage/root, last verified anchor, findings and status; metadata-only or latest HEAD samples stay PENDING until fully examined. Canonical Synexia Maven/OpenRewrite recipes, provenance/history gates and repository-role boundaries govern source-changing work. New, separable first-party material prefers Apache-2.0; preserve all upstream/donor licenses, NOTICE and copyright. LLM-assisted atomization/patternization is not automatic independent authorship or relicensing. Canonical policy and PENDING 236-repo audit inventory: [com.synexia PR #18764](https://github.com/hsoliwal/com.synexia/pull/18764). Product-only forks must obey their existing clean postimage-only AGENTS boundaries.



## Superset merge and history-audit rule (2026-11-18)

Read [.github/MERGE_RULE.md](.github/MERGE_RULE.md) before changing an existing implementation or integrating any branch or PR.
Preserve the supported additive API/behavior superset and select implementations by correctness, predictability, measured performance, then elegance.
Audit all reachable history back to every root, all branches/tags and all PR states for lost code or regressions; preserve append-only history.
Record actual UTC coverage and remaining work in [.github/HISTORY_AUDIT.tsv](.github/HISTORY_AUDIT.tsv); policy installation is not a completed source-history audit.
Prefer verified Apache-2.0 sources and independent implementations while preserving all existing third-party rights; LLM rewriting does not remove license obligations.
Detailed private recipe/proof records remain in canonical Synexia; the compact local policy and checkpoint make the rule discoverable here.
<!-- MERGE-SUPERSET-HISTORY-20261118-2 -->

## Owner directive: best-of-best additive convergence and full-history checkpoint (2026-11-18)

Inspect all accessible Git history to its roots, all branches/tags and paginated open/closed/merged/rejected PRs with their head/merge refs before replacing code. Compare actual pre/post source and public API/behavior/test inventories; a merged PR/ancestry alone is not proof of surviving functionality. Identify and recover lost verified optimizations, API extensions and tests via the canonical Synexia Maven/OpenRewrite recipe, not destructive rebase.

Converge the strongest compatible correct algorithms into an additive superset; retain original type identity, source/binary/reflection/serialization contracts, native/resource guarantees, regressions and build/test gates. Reusable recipes and evidence belong to `hsoliwal/com.synexia`; this repository receives only appropriate product code and tests.

Prefer Apache-2.0 for separately original and legally eligible new work; retain all upstream GPL/EPL/Apache/LLVM exception or other per-file licensing and NOTICE as applicable. A new, independently expressed implementation of an abstract algorithm may be used when permitted; LLM paraphrase cannot erase another author's code copyright or patent rights.

Write exact last-check date, repo/default SHA, root/ref/PR coverage, delivered-source loss findings and test status to `hsoliwal/com.synexia/docs/M3-SCALE/invariants/SYNEXIA-HISTORY-REVIEW-LAST-CHECKED.tsv`. Until complete, mark PARTIAL; do not claim full-history audit from a sampled head.
