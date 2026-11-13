<!-- SPDX-License-Identifier: EPL-1.0 OR BSD-3-Clause -->
# Eclipse FastList: reuse existing ZERO_SIZED_ARRAY after empty trim

Owner: `hsoliwal/eclipse-collections@3a1e6abf9f558b5e5571954a59998b4236aad8df` `eclipse-collections/src/main/java/com/m3/collections/eclipse/impl/list/mutable/FastList.java`; current original Git blob `d5358d01d5d28ac6de736b4bd19acb2c32e92d24`.

The fork already owns two distinct static empty arrays: `DEFAULT_SIZED_EMPTY_ARRAY` means first add grows to 10 and `ZERO_SIZED_ARRAY` means first add grows to 1. The current `trimToSize()` allocates a fresh zero-length `Object[]` on every successful empty trim. For exact runtime class `FastList` only and `size==0`, reuse the already defined `ZERO_SIZED_ARRAY`. Keep the original transfer path for subclasses and nonempty lists. The condition `size < items.length` remains the outer gate; the default constructor's untouched empty sentinel still grows to ten. No new collection, array, persistent index, public/protected API, serialized field, element callback, iterator or JNI code.

Local Java21 strict `javac -Xlint:all -Werror` and exact structural model: 1,030 comparisons across ten capacities and repeated refill/trim cycles. `ThreadMXBean` tracked retained-owner operations: 200,000 add/remove/empty trim cycles produced ~11,197,928 bytes baseline versus ~7,997,928 bytes candidate in repeated warmed runs, saving **3,200,000 bytes / 16 bytes per successful empty trim** on this JVM. Timing was variable; this is an **allocation** optimization only, not a blanket throughput improvement. The model is not a full Eclipse build.

Add fork-owned JUnit5 for default constructor initial growth, explicit zero initial capacity, 64+ capacity trim/re-add, stable zero-array reuse, and subclass original behavior. Full Eclipse Maven/unit-tests, installed runtime, heap and GC cross-platform proof **NOT_RUN** here. Source and test on a draft branch; never merge automatically or move into M3JDK.
