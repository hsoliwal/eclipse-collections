<!--
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->

# DOOP key/value source receipt

Bounded IndexedSortedMap.clear traverses its existing key iterator and removes slots without creating entry projections.

Canonical recipe and qualification: [Synexia commit cfa05713fe53](https://github.com/hsoliwal/com.synexia/commit/cfa05713fe5395694af36df74373dcfe1e7e9031).

Receiving parent: `4ab421e6e451ad79193a146218054cac71fe3881`. Canonical datasets: `doop-key-value-eclipse-20261102`. All Java files below are actual verified SDK Result outputs.

## Executed verification

Seven-module native Maven reactor passed: 2,147 main/generated and 2,944 unit-test sources compiled, 9/9 selected tests passed. Measured allocation 262,248 to 88 bytes per 4,096 removals. The final allocation test warms separate maps and passes alone with its original threshold.

## Concurrent source update

The earlier receiving PR20 was merged while this pass was running. The new parent contains that work. Its IndexedSortedMap source is byte-identical to the qualified original; all build inputs are unchanged. A fresh exact-parent checkout with these same two SDK outputs passed the seven-module native build and nine selected tests. The two concurrently added ArrayAdapter test classes were compiled among the 2,944 unit-test sources; they were not selected for execution.

## Exact output blobs

| Path | Git blob |
|---|---|
| `eclipse-collections/src/main/java/com/m3/collections/eclipse/impl/map/sorted/mutable/IndexedSortedMap.java` | `f7c2af900884ef86cb0031ca50ca6e43863b069c` |
| `unit-tests/src/test/java/com/m3/collections/eclipse/impl/map/sorted/mutable/M3IndexedSortedMapClearProjectionTest.java` | `c0e80d17ee4738931a4a6a0a686c42a961bd75de` |

## Acceptance scope

IndexedTreeArena still weakly caches EntryView identity and requires a separate lifetime redesign. The clear change is finite and does not qualify that cache or the complete library.

The existing broader branch acceptance gates remain in force. This receipt does not claim whole-repository or fleet-wide conversion, universal throughput gains, or default-branch integration.
