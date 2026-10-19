# Prototype provenance

IndexedTreeArena adapts the reusable array-slot, free-list and occupancy-mask
pattern from M3LinkedList, hsoliwal/M3jdk21 commit
a3e88976ee8664d1ef5d0cade96aec1206b2133d, blob
744d1c488b8342c98e49d2cfd3465c0f558b199d. Apache-2.0;
copyright 2026 Hitesh Soliwal and Contributors to the Synexia Project.
Weak API-projection detachment is the new delta.

IndexedRedBlackTree's rotations and deletion repair adapt the KEY-lane state
operations in Apache Commons Collections TreeBidiMap, user fork
hsoliwal/commons-collections commit03f5ae07596f2fd648419cd69e7c4f3dc01d7420,
blob97a9068cdaf1e2c6f93d1482acdafe150ad0b1eb, lines1286-1358 and2029-2077.
License and NOTICE are retained beside this file. The source credits Sun TreeMap
as algorithm history; no OpenJDK source is imported into this prototype.
Modifications: replace dual-tree Node storage with single-tree integer slots and
packed black/live masks; make root publication include arena identity; express
null links as-1; supply successor-payload deletion rather than donor position
swapping; correct insertion parent-side selection; preserve null-safe fixup
transitions with foreign-root noncompletion qualified separately in bounded jobs.
Sorted construction is the standard inorder median split pattern already used
by the SDK's qualified array AVL builder, with RB level coloring to be checked
against the unchanged JDK21 oracle. No fastutil threaded-entry code is copied.

All changes remain experimental until executed proof and integration gates pass.
