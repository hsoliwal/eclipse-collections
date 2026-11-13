<!-- SPDX-License-Identifier: EPL-1.0 OR EDL-1.0 -->
# FastList test backing-lane qualification (2026-11-13)

On the real Eclipse Collections Maven reactor, all four new tests in draft
PR #29 initially failed with `ClassCastException: Object[] cannot be cast to
Integer[]`. Production `FastList` compiled; the defect was in the test's
inspection of the generic `FastList<Integer>.items` field, which inserts a
runtime `Integer[]` checkcast although the actual backing array is `Object[]`.

The hash-pinned Java 21 source recipe changes the nine test inspections to a
non-generic `Object[]` projection through `FastList<?>`, leaving production
code untouched. Run from the repository root:

```sh
java m3/recipes/fastlist-test-erased-backing-20261113/FastListTestErasedBackingRecipe.java \
  --apply unit-tests/src/test/java/com/m3/collections/eclipse/impl/list/mutable/FastListEmptyTrimArrayTest.java
java m3/recipes/fastlist-test-erased-backing-20261113/FastListTestErasedBackingRecipe.java \
  --check unit-tests/src/test/java/com/m3/collections/eclipse/impl/list/mutable/FastListEmptyTrimArrayTest.java
```

This is a test-only remedy, not a new optimization or a claim of whole-reactor
qualification. Record the actual Maven result in the PR before promotion.
