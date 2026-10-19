# Source provenance

The public owner is the current hsoliwal/eclipse-collections TreeSortedMap at
489c8e63b973aaf52eb87f08f5908a152a4b8f22, blob
5bd0fc5ac4e972c031d3e72b7fd5597dce9df660. Only its private backing type and
construction sites change. Preserve its existing Eclipse license headers and
current source variations. No project-wide package rename is performed here.

The array/free-slot pattern comes from M3LinkedList in hsoliwal/M3jdk21 at
a3e88976ee8664d1ef5d0cade96aec1206b2133d, blob
744d1c488b8342c98e49d2cfd3465c0f558b199d. Red-black rotation and deletion-repair
patterns are adapted from the Apache Commons TreeBidiMap KEY lane at
03f5ae07596f2fd648419cd69e7c4f3dc01d7420, blob
97a9068cdaf1e2c6f93d1482acdafe150ad0b1eb. The complete donor owner is unsuitable:
it stores nodes, requires unique values and swaps node positions. This implementation
uses indexed links, successor payload copy and one stable count/root/epoch owner.
The insertion parent-side condition is separately corrected and structurally proved.
Apache license and notice are retained. No OpenJDK source is imported.

The supplied user note describes array-index links and a free chain, which M3LinkedList
already uses. Its claims of eliminating GC or beating all benchmark submissions are
not implementation or performance evidence. No throughput improvement is claimed.

The publication parent advances to 0d48359990e199a55fa0b1fd2d5c443108960a98.
Its public owner is byte-identical to the qualified preimage; the three intervening
commits change only Checkstyle suppression and the relocation proof fixture.
V6 callback refinement reuses the indexed slot owner and black-box original traces;
installed JDK source was inspected as behavioral reference, with no source imported.

Publication parent e460a03da7ac45602f795e4bd9783b2a03de09c0 preserves later FastList/ArrayAdapter work. The sorted-map preimage remains exact. The comparator-review proposal was rejected using actual original-first JDK21/current-owner observations; all five production payloads remain identical to the full V6 review. No proposed source-receiver alternative is included.
