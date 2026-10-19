# Indexed red-black TreeSortedMap backend

This source change replaces the private backing tree of the existing public
TreeSortedMap with parallel dynamic arrays, integer links/free slots and packed
live/color state. Entry objects are weakly cached API projections, never primary
stored nodes. Live entry identity, detached entries and successor payload copy
are preserved through iterator mutation.

The pure apply_recipe.transform function takes an exact five-path mapping of
preimage bytes (None for absent additions), the postimage byte mapping and recipe
JSON. It returns a complete validated mapping. Reverse mode restores the exact
owner and removes only the four exact added postimages from that mapping. The
function performs no disk writes. Callers must separately guard filesystem paths,
storage, exclusive writes and whole-tree publication. Unknown input refuses.

The normal Jupiter regressions belong to the existing unit-tests module. The three
main-based proofs under proofs/ are standalone datasets; they are not represented
as Maven-discovered tests. Full current EC14 reactor/generator/dependency, JPMS,
OSGi and single-provider artifact qualification remains open. This draft source
addition does not establish full library, JNI, five-library or peak-performance
completion. See PROOF_SCOPE.json and CATALOGUE.json for executed boundaries.

Nine normal Jupiter tests pass, including three comparator callback tests using a shipped sealed original resource. JDK21 and the current original owner use the existing target-receiver order; the contrary review finding was refuted without changing production source. Full reactor and runtime admission remain open.
