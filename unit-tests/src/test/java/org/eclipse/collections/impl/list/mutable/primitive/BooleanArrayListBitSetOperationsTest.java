/*
 * Copyright (c) 2026 Synexia, Hitesh Soliwal and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.list.mutable.primitive;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import org.eclipse.collections.api.BooleanIterable;
import org.eclipse.collections.api.factory.primitive.BooleanLists;
import org.eclipse.collections.api.list.primitive.BooleanList;
import org.eclipse.collections.impl.set.mutable.primitive.BooleanHashSet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BooleanArrayListBitSetOperationsTest
{
    @Test
    public void allShortBooleanSequencesAndMemberships()
    {
        for (int size = 0; size <= 8; size++)
        {
            for (int sequence = 0; sequence < 1 << size; sequence++)
            {
                boolean[] values = new boolean[size];
                for (int index = 0; index < size; index++)
                {
                    values[index] = (sequence & 1 << index) != 0;
                }
                this.assertSearch(BooleanArrayList.newListWith(values), values);
                this.assertSearch(BooleanLists.immutable.with(values), values);
                for (int membership = 0; membership < 4; membership++)
                {
                    boolean[] members = this.members(membership);
                    for (boolean retain : new boolean[] {false, true})
                    {
                        boolean[] expected = this.filter(values, members, retain);
                        for (int sourceKind = 0; sourceKind < 3; sourceKind++)
                        {
                            BooleanArrayList target = BooleanArrayList.newListWith(values);
                            boolean changed;
                            if (sourceKind == 0)
                            {
                                changed = retain ? target.retainAll(members) : target.removeAll(members);
                            }
                            else
                            {
                                BooleanIterable source = sourceKind == 1
                                        ? BooleanArrayList.newListWith(members)
                                        : BooleanHashSet.newSetWith(members);
                                changed = retain ? target.retainAll(source) : target.removeAll(source);
                            }
                            assertEquals(expected.length != values.length, changed);
                            assertArrayEquals(expected, target.toArray());
                            this.assertSearch(target, expected);
                        }
                    }
                }
            }
        }
    }

    @Test
    public void searchAcrossWordBoundaries()
    {
        for (int size : new int[] {0, 1, 2, 63, 64, 65, 127, 128, 129, 4097})
        {
            boolean[] values = new boolean[size];
            for (int pattern = 0; pattern < 3; pattern++)
            {
                for (int index = 0; index < size; index++)
                {
                    values[index] = pattern == 1 || pattern == 2 && index % 64 == 63;
                }
                this.assertSearch(BooleanArrayList.newListWith(values), values);
                this.assertSearch(BooleanLists.immutable.with(values), values);
            }
        }
    }

    @Test
    public void masksDoNotReadBeyondLogicalSize() throws Exception
    {
        BooleanArrayList allFalse = BooleanArrayList.newWithNValues(65, false);
        this.storage(allFalse).set(127);
        this.assertSearch(allFalse, new boolean[65]);
        assertFalse(allFalse.removeAll(true));
        assertArrayEquals(new boolean[65], allFalse.toArray());

        BooleanArrayList mixed = BooleanArrayList.newListWith(true, false, true, false);
        this.storage(mixed).set(64, 129);
        assertTrue(mixed.removeAll(BooleanHashSet.newSetWith(false)));
        assertArrayEquals(new boolean[] {true, true}, mixed.toArray());
    }

    @Test
    public void removalClearsTailBeforeFalseValuesAreAppended()
    {
        for (boolean remove : new boolean[] {false, true})
        {
            BooleanArrayList target = BooleanArrayList.newListWith(true, false, true, false, true);
            assertTrue(target.removeAll(remove));
            boolean[] retained = target.toArray();
            target.addAll(false, false, false, false, false);
            assertArrayEquals(Arrays.copyOf(retained, retained.length + 5), target.toArray());
        }
    }

    @Test
    public void selfAliasesPreserveMembership()
    {
        for (boolean[] values : new boolean[][] {{}, {false}, {true}, {true, false, true}})
        {
            BooleanArrayList retained = BooleanArrayList.newListWith(values);
            assertFalse(retained.retainAll(retained));
            assertArrayEquals(values, retained.toArray());
            BooleanArrayList removed = BooleanArrayList.newListWith(values);
            assertEquals(values.length != 0, removed.removeAll(removed));
            assertTrue(removed.isEmpty());
        }
    }

    @Test
    public void customContainsKeepsOrderAndPartialMutationOnFailure()
    {
        BooleanArrayList target = BooleanArrayList.newListWith(true, false, true, false);
        List<String> calls = new ArrayList<>();
        BooleanHashSet source = new BooleanHashSet()
        {
            @Override
            public boolean contains(boolean value)
            {
                calls.add(value + ":" + Arrays.toString(target.toArray()));
                if (calls.size() == 3)
                {
                    throw new IllegalStateException("stop");
                }
                return value;
            }
        };
        assertThrows(IllegalStateException.class, () -> target.removeAll(source));
        assertEquals(List.of("true:[true, false, true, false]", "false:[false, true, false]",
                "true:[false, true, false]"), calls);
        assertArrayEquals(new boolean[] {false, true, false}, target.toArray());
    }

    @Test
    public void customRetainMembershipKeepsCallbackOrderAndAtomicPublication()
    {
        BooleanArrayList target = BooleanArrayList.newListWith(true, false, true);
        List<Boolean> calls = new ArrayList<>();
        BooleanHashSet source = new BooleanHashSet()
        {
            @Override
            public boolean contains(boolean value)
            {
                calls.add(value);
                if (calls.size() == 2)
                {
                    throw new IllegalArgumentException("stop");
                }
                return false;
            }
        };
        assertThrows(IllegalArgumentException.class, () -> target.retainAll(source));
        assertEquals(List.of(true, false), calls);
        assertArrayEquals(new boolean[] {true, false, true}, target.toArray());
    }

    @Test
    public void nullAndEmptySourceContracts()
    {
        BooleanArrayList empty = new BooleanArrayList();
        assertFalse(empty.removeAll((boolean[]) null));
        assertFalse(empty.removeAll((BooleanIterable) null));
        assertThrows(NullPointerException.class, () -> empty.retainAll((boolean[]) null));
        assertThrows(NullPointerException.class, () -> empty.retainAll((BooleanIterable) null));

        BooleanArrayList target = BooleanArrayList.newListWith(false, true);
        assertThrows(NullPointerException.class, () -> target.removeAll((boolean[]) null));
        assertThrows(NullPointerException.class, () -> target.removeAll((BooleanIterable) null));
        assertThrows(NullPointerException.class, () -> target.retainAll((boolean[]) null));
        assertThrows(NullPointerException.class, () -> target.retainAll((BooleanIterable) null));
        assertArrayEquals(new boolean[] {false, true}, target.toArray());
    }

    private BitSet storage(BooleanArrayList list) throws Exception
    {
        Field items = BooleanArrayList.class.getDeclaredField("items");
        items.setAccessible(true);
        return (BitSet) items.get(list);
    }

    private void assertSearch(BooleanList list, boolean[] values)
    {
        for (boolean sought : new boolean[] {false, true})
        {
            int first = -1;
            int last = -1;
            for (int index = 0; index < values.length; index++)
            {
                if (values[index] == sought)
                {
                    if (first == -1)
                    {
                        first = index;
                    }
                    last = index;
                }
            }
            assertEquals(first, list.indexOf(sought));
            assertEquals(last, list.lastIndexOf(sought));
            assertEquals(first != -1, list.contains(sought));
        }
    }

    private boolean[] members(int mask)
    {
        return switch (mask)
        {
            case 0 -> new boolean[0];
            case 1 -> new boolean[] {false, false};
            case 2 -> new boolean[] {true, true};
            default -> new boolean[] {false, true, false};
        };
    }

    private boolean[] filter(boolean[] values, boolean[] members, boolean retain)
    {
        boolean[] result = new boolean[values.length];
        int size = 0;
        for (boolean value : values)
        {
            boolean present = false;
            for (boolean member : members)
            {
                present |= member == value;
            }
            if (present == retain)
            {
                result[size++] = value;
            }
        }
        return Arrays.copyOf(result, size);
    }
}
