<!-- SPDX-License-Identifier: Apache-2.0 -->
# Eclipse package-first M3 donor working area

Source repository: hsoliwal/eclipse-collections (fork of
eclipse-collections/eclipse-collections). Complete `IntervalUtils`
and `EmptyIterator` source bodies are copied under
`com.m3.collections.eclipse.impl.*` by modifying only the package
declarations. The original Eclipse `org.eclipse.collections.*`
classes remain unchanged and continue to provide the public API.

`IntervalUtils` contains binary search, indexing, bounds, offset,
and interval stride semantics useful for M3 index/mask catalogues.
`EmptyIterator` retains the JDK ListIterator refusal contract.
No upstream bodies are redesigned. No new JNI symbols or runtime
owner; this is a pure licensed donor first-pass mechanical relocation.

Original Eclipse Public License and Eclipse Distribution License
copyright headers are preserved verbatim (EPL-1.0/EDL-1.0) and must
remain in any shipping source or derivative. The new independent
contract test code is Apache-2.0 first party. LeetCode lower bound,
HackerRank array iteration, GFG binary search are taxonomy only.

Qualification uses strict real Java21 compile of both original and
relocated sources, deterministic randomized differential outcomes,
source hash/package-only check, and canonical Maven/OpenRewrite
replay/fixed point. M3JDK21 receives only after actual qualification.
