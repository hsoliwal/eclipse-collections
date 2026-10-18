/*
 * Copyright (c) 2021 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.bag.immutable;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import com.m3.collections.eclipse.api.bag.Bag;
import com.m3.collections.eclipse.api.bag.ImmutableBag;
import com.m3.collections.eclipse.api.bag.MutableBag;
import com.m3.collections.eclipse.api.bag.primitive.ImmutableBooleanBag;
import com.m3.collections.eclipse.api.bag.sorted.MutableSortedBag;
import com.m3.collections.eclipse.api.block.function.Function;
import com.m3.collections.eclipse.api.block.predicate.Predicate;
import com.m3.collections.eclipse.api.list.MutableList;
import com.m3.collections.eclipse.api.map.sorted.MutableSortedMap;
import com.m3.collections.eclipse.api.partition.bag.PartitionImmutableBag;
import com.m3.collections.eclipse.api.set.ImmutableSet;
import com.m3.collections.eclipse.api.tuple.Pair;
import com.m3.collections.eclipse.api.tuple.primitive.ObjectIntPair;
import com.m3.collections.eclipse.impl.bag.mutable.HashBag;
import com.m3.collections.eclipse.impl.bag.mutable.primitive.BooleanHashBag;
import com.m3.collections.eclipse.impl.bag.sorted.mutable.TreeBag;
import com.m3.collections.eclipse.impl.block.factory.Comparators;
import com.m3.collections.eclipse.impl.block.factory.Functions;
import com.m3.collections.eclipse.impl.block.factory.Predicates;
import com.m3.collections.eclipse.impl.block.factory.Predicates2;
import com.m3.collections.eclipse.impl.block.function.PassThruFunction0;
import com.m3.collections.eclipse.impl.factory.Bags;
import com.m3.collections.eclipse.impl.factory.Sets;
import com.m3.collections.eclipse.impl.list.mutable.FastList;
import com.m3.collections.eclipse.impl.list.primitive.IntInterval;
import com.m3.collections.eclipse.impl.map.mutable.UnifiedMap;
import com.m3.collections.eclipse.impl.map.sorted.mutable.TreeSortedMap;
import com.m3.collections.eclipse.impl.set.mutable.UnifiedSet;
import com.m3.collections.eclipse.impl.test.Verify;
import com.m3.collections.eclipse.impl.tuple.primitive.PrimitiveTuples;
import org.junit.Assert;
import org.junit.Test;

import static com.m3.collections.eclipse.impl.factory.Iterables.iBag;

public class ImmutableEmptyBagTest extends ImmutableBagTestCase
{
    public static final Predicate<String> ERROR_THROWING_PREDICATE = each ->
    {
        throw new AssertionError();
    };

    public static final Predicates2<String, Class<Integer>> ERROR_THROWING_PREDICATE_2 = new Predicates2<String, Class<Integer>>()
    {
        public boolean accept(String argument1, Class<Integer> argument2)
        {
            throw new AssertionError();
        }
    };

    @Override
    protected ImmutableBag<String> newBag()
    {
        return (ImmutableBag<String>) ImmutableEmptyBag.INSTANCE;
    }

    @Override
    protected int numKeys()
    {
        return 0;
    }

    @Test
    public void testFactory()
    {
        Verify.assertInstanceOf(ImmutableEmptyBag.class, Bags.immutable.of());
    }

    @Override
    @Test
    public void anySatisfyWithOccurrences()
    {
        ImmutableBag<String> bag = this.newBag();
        Assert.assertFalse(bag.anySatisfyWithOccurrences((object, value) -> true));
        Assert.assertFalse(bag.anySatisfyWithOccurrences((object, value) -> false));
    }

    @Override
    @Test
    public void allSatisfyWithOccurrences()
    {
        ImmutableBag<String> bag = this.newBag();
        Assert.assertTrue(bag.allSatisfyWithOccurrences((object, value) -> true));
        Assert.assertTrue(bag.allSatisfyWithOccurrences((object, value) -> false));
    }

    @Override
    @Test
    public void noneSatisfyWithOccurrences()
    {
        ImmutableBag<String> bag = this.newBag();
        Assert.assertTrue(bag.noneSatisfyWithOccurrences((object, value) -> true));
        Assert.assertTrue(bag.noneSatisfyWithOccurrences((object, value) -> false));
    }

    @Override
    @Test
    public void detectWithOccurrences()
    {
        ImmutableBag<String> bag = this.newBag();
        Assert.assertNull(bag.detectWithOccurrences((object, value) -> true));
        Assert.assertNull(bag.detectWithOccurrences((object, value) -> false));
    }

    @Test
    @Override
    public void newWith()
    {
        ImmutableBag<String> bag = this.newBag();
        ImmutableBag<String> newBag = bag.newWith("1");
        Assert.assertNotEquals(bag, newBag);
        Assert.assertEquals(newBag.size(), bag.size() + 1);
        ImmutableBag<String> newBag2 = bag.newWith("5");
        Assert.assertNotEquals(bag, newBag2);
        Assert.assertEquals(newBag2.size(), bag.size() + 1);
        Assert.assertEquals(1, newBag2.sizeDistinct());
    }

    @Override
    @Test
    public void selectDuplicates()
    {
        Assert.assertEquals(
                Bags.immutable.empty(),
                this.newBag().selectDuplicates());
    }

    @Test
    @Override
    public void select()
    {
        ImmutableBag<String> strings = this.newBag();
        Verify.assertIterableEmpty(strings.select(Predicates.lessThan("0")));
    }

    @Test
    @Override
    public void reject()
    {
        ImmutableBag<String> strings = this.newBag();
        Verify.assertIterableEmpty(strings.reject(Predicates.greaterThan("0")));
    }

    /**
     * @since 9.1.
     */
    @Override
    @Test
    public void collectWithOccurrences()
    {
        Bag<String> bag = this.newBag();
        Bag<ObjectIntPair<String>> actual =
                bag.collectWithOccurrences(PrimitiveTuples::pair, Bags.mutable.empty());
        Bag<ObjectIntPair<String>> expected = Bags.immutable.empty();
        Assert.assertEquals(expected, actual);

        Set<ObjectIntPair<String>> actual2 =
                bag.collectWithOccurrences(PrimitiveTuples::pair, Sets.mutable.empty());
        ImmutableSet<ObjectIntPair<String>> expected2 = Sets.immutable.empty();
        Assert.assertEquals(expected2, actual2);
    }

    @Override
    public void partition()
    {
        PartitionImmutableBag<String> partition = this.newBag().partition(Predicates.lessThan("0"));
        Verify.assertIterableEmpty(partition.getSelected());
        Verify.assertIterableEmpty(partition.getRejected());
    }

    @Override
    public void partitionWith()
    {
        PartitionImmutableBag<String> partition = this.newBag().partitionWith(Predicates2.lessThan(), "0");
        Verify.assertIterableEmpty(partition.getSelected());
        Verify.assertIterableEmpty(partition.getRejected());
    }

    @Override
    @Test
    public void selectInstancesOf()
    {
        ImmutableBag<Number> numbers = Bags.immutable.of();
        Assert.assertEquals(iBag(), numbers.selectInstancesOf(Integer.class));
        Assert.assertEquals(iBag(), numbers.selectInstancesOf(Double.class));
        Assert.assertEquals(iBag(), numbers.selectInstancesOf(Number.class));
    }

    @Override
    @Test
    public void testToString()
    {
        super.testToString();
        Assert.assertEquals("[]", this.newBag().toString());
    }

    @Override
    @Test
    public void testSize()
    {
        Verify.assertIterableSize(0, this.newBag());
    }

    @Override
    @Test
    public void newWithout()
    {
        Assert.assertSame(this.newBag(), this.newBag().newWithout("1"));
    }

    @Override
    public void toStringOfItemToCount()
    {
        Assert.assertEquals("{}", Bags.immutable.of().toStringOfItemToCount());
    }

    @Override
    @Test
    public void detect()
    {
        Assert.assertNull(this.newBag().detect("1"::equals));
    }

    @Override
    @Test
    public void detectWith()
    {
        Assert.assertNull(this.newBag().detectWith(Predicates2.greaterThan(), "3"));
    }

    @Override
    @Test
    public void detectWithIfNone()
    {
        Assert.assertEquals("Not Found", this.newBag().detectWithIfNone(Object::equals, "1", new PassThruFunction0<>("Not Found")));
    }

    @Override
    public void detectIfNone()
    {
        super.detectIfNone();

        Assert.assertEquals("Not Found", this.newBag().detectIfNone("2"::equals, new PassThruFunction0<>("Not Found")));
    }

    @Override
    @Test
    public void allSatisfy()
    {
        ImmutableBag<String> strings = this.newBag();
        Assert.assertTrue(strings.allSatisfy(ERROR_THROWING_PREDICATE));
    }

    @Override
    @Test
    public void anySatisfy()
    {
        ImmutableBag<String> strings = this.newBag();
        Assert.assertFalse(strings.anySatisfy(ERROR_THROWING_PREDICATE));
    }

    @Override
    @Test
    public void noneSatisfy()
    {
        ImmutableBag<String> strings = this.newBag();
        Assert.assertTrue(strings.noneSatisfy(ERROR_THROWING_PREDICATE));
    }

    @Override
    @Test
    public void allSatisfyWith()
    {
        ImmutableBag<String> strings = this.newBag();
        Assert.assertTrue(strings.allSatisfyWith(ERROR_THROWING_PREDICATE_2, Integer.class));
    }

    @Override
    @Test
    public void anySatisfyWith()
    {
        ImmutableBag<String> strings = this.newBag();
        Assert.assertFalse(strings.anySatisfyWith(ERROR_THROWING_PREDICATE_2, Integer.class));
    }

    @Override
    @Test
    public void noneSatisfyWith()
    {
        ImmutableBag<String> strings = this.newBag();
        Assert.assertTrue(strings.noneSatisfyWith(ERROR_THROWING_PREDICATE_2, Integer.class));
    }

    @Override
    @Test
    public void getFirst()
    {
        Assert.assertNull(this.newBag().getFirst());
    }

    @Override
    @Test
    public void getLast()
    {
        Assert.assertNull(this.newBag().getLast());
    }

    @Override
    @Test
    public void getOnly()
    {
        Assert.assertThrows(IllegalStateException.class, () -> this.newBag().getOnly());
    }

    @Override
    @Test
    public void isEmpty()
    {
        ImmutableBag<String> bag = this.newBag();
        Assert.assertTrue(bag.isEmpty());
        Assert.assertFalse(bag.notEmpty());
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void min()
    {
        this.newBag().min(String::compareTo);
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void max()
    {
        this.newBag().max(String::compareTo);
    }

    @Test
    @Override
    public void min_null_throws()
    {
        super.min_null_throws();
    }

    @Test
    @Override
    public void max_null_throws()
    {
        super.max_null_throws();
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void min_without_comparator()
    {
        this.newBag().min();
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void max_without_comparator()
    {
        this.newBag().max();
    }

    @Test
    @Override
    public void min_null_throws_without_comparator()
    {
        // Not applicable for empty collections
        super.min_null_throws_without_comparator();
    }

    @Test
    @Override
    public void max_null_throws_without_comparator()
    {
        // Not applicable for empty collections
        super.max_null_throws_without_comparator();
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void minBy()
    {
        this.newBag().minBy(String::valueOf);
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void maxBy()
    {
        this.newBag().maxBy(String::valueOf);
    }

    @Override
    @Test
    public void zip()
    {
        ImmutableBag<String> immutableBag = this.newBag();
        List<Object> nulls = Collections.nCopies(immutableBag.size(), null);
        List<Object> nullsPlusOne = Collections.nCopies(immutableBag.size() + 1, null);

        ImmutableBag<Pair<String, Object>> pairs = immutableBag.zip(nulls);
        Assert.assertEquals(
                immutableBag,
                pairs.collect((Function<Pair<String, ?>, String>) Pair::getOne));
        Assert.assertEquals(
                HashBag.newBag(nulls),
                pairs.collect((Function<Pair<?, Object>, Object>) Pair::getTwo));

        ImmutableBag<Pair<String, Object>> pairsPlusOne = immutableBag.zip(nullsPlusOne);
        Assert.assertEquals(
                immutableBag,
                pairsPlusOne.collect((Function<Pair<String, ?>, String>) Pair::getOne));
        Assert.assertEquals(
                HashBag.newBag(nulls),
                pairsPlusOne.collect((Function<Pair<?, Object>, Object>) Pair::getTwo));

        Assert.assertEquals(immutableBag.zip(nulls), immutableBag.zip(nulls, HashBag.newBag()));
    }

    @Override
    @Test
    public void zipWithIndex()
    {
        ImmutableBag<String> immutableBag = this.newBag();
        ImmutableSet<Pair<String, Integer>> pairs = immutableBag.zipWithIndex();

        Assert.assertEquals(UnifiedSet.<String>newSet(), pairs.collect((Function<Pair<String, ?>, String>) Pair::getOne));
        Assert.assertEquals(UnifiedSet.<Integer>newSet(), pairs.collect((Function<Pair<?, Integer>, Integer>) Pair::getTwo));

        Assert.assertEquals(immutableBag.zipWithIndex(), immutableBag.zipWithIndex(UnifiedSet.newSet()));
    }

    @Override
    @Test
    public void chunk()
    {
        Assert.assertEquals(this.newBag(), this.newBag().chunk(2));
    }

    @Override
    @Test(expected = IllegalArgumentException.class)
    public void chunk_zero_throws()
    {
        this.newBag().chunk(0);
    }

    @Override
    @Test
    public void chunk_large_size()
    {
        Assert.assertEquals(this.newBag(), this.newBag().chunk(10));
        Verify.assertInstanceOf(ImmutableBag.class, this.newBag().chunk(10));
    }

    @Override
    @Test
    public void toSortedMap()
    {
        MutableSortedMap<String, String> map = this.newBag().toSortedMap(Functions.getStringPassThru(), Functions.getStringPassThru());
        Verify.assertEmpty(map);
        Verify.assertInstanceOf(TreeSortedMap.class, map);
    }

    @Override
    @Test
    public void toSortedMap_with_comparator()
    {
        MutableSortedMap<String, String> map = this.newBag().toSortedMap(Comparators.reverseNaturalOrder(),
                Functions.getStringPassThru(), Functions.getStringPassThru());
        Verify.assertEmpty(map);
        Verify.assertInstanceOf(TreeSortedMap.class, map);
        Assert.assertEquals(Comparators.<String>reverseNaturalOrder(), map.comparator());
    }

    @Override
    @Test
    public void toSortedMapBy()
    {
        MutableSortedMap<String, String> map = this.newBag().toSortedMapBy(Integer::valueOf,
                Functions.getStringPassThru(), Functions.getStringPassThru());
        Verify.assertEmpty(map);
        Verify.assertInstanceOf(TreeSortedMap.class, map);
    }

    @Override
    @Test
    public void serialization()
    {
        ImmutableBag<String> bag = this.newBag();
        Verify.assertPostSerializedIdentity(bag);
    }

    @Override
    @Test
    public void collectBoolean()
    {
        ImmutableBooleanBag result = this.newBag().collectBoolean("4"::equals);
        Assert.assertEquals(0, result.sizeDistinct());
        Assert.assertEquals(0, result.occurrencesOf(true));
        Assert.assertEquals(0, result.occurrencesOf(false));
    }

    @Override
    @Test
    public void collectBooleanWithTarget()
    {
        BooleanHashBag target = new BooleanHashBag();
        BooleanHashBag result = this.newBag().collectBoolean("4"::equals, target);
        Assert.assertSame("Target sent as parameter not returned", target, result);
        Assert.assertEquals(0, result.sizeDistinct());
        Assert.assertEquals(0, result.occurrencesOf(true));
        Assert.assertEquals(0, result.occurrencesOf(false));
    }

    @Override
    @Test
    public void collect_target()
    {
        MutableList<Integer> targetCollection = FastList.newList();
        MutableList<Integer> actual = this.newBag().collect(object ->
        {
            throw new AssertionError();
        }, targetCollection);
        Assert.assertEquals(targetCollection, actual);
        Assert.assertSame(targetCollection, actual);
    }

    @Override
    @Test
    public void collectWith_target()
    {
        MutableList<Integer> targetCollection = FastList.newList();
        MutableList<Integer> actual = this.newBag().collectWith((argument1, argument2) ->
        {
            throw new AssertionError();
        }, 1, targetCollection);
        Assert.assertEquals(targetCollection, actual);
        Assert.assertSame(targetCollection, actual);
    }

    @Override
    @Test
    public void groupByUniqueKey()
    {
        Assert.assertEquals(UnifiedMap.newMap().toImmutable(), this.newBag().groupByUniqueKey(id -> id));
    }

    @Override
    @Test
    public void groupByUniqueKey_throws()
    {
        super.groupByUniqueKey_throws();
        Assert.assertEquals(UnifiedMap.newMap().toImmutable(), this.newBag().groupByUniqueKey(id -> id));
    }

    @Override
    @Test
    public void groupByUniqueKey_target()
    {
        Assert.assertEquals(UnifiedMap.newMap(), this.newBag().groupByUniqueKey(id -> id, UnifiedMap.newMap()));
    }

    @Override
    @Test
    public void groupByUniqueKey_target_throws()
    {
        super.groupByUniqueKey_target_throws();
        Assert.assertEquals(UnifiedMap.newMap(), this.newBag().groupByUniqueKey(id -> id, UnifiedMap.newMap()));
    }

    @Test
    public void countByEach()
    {
        Assert.assertEquals(Bags.immutable.empty(), this.newBag().countByEach(each -> IntInterval.oneTo(5).collect(i -> each + i)));
    }

    @Test
    public void countByEach_target()
    {
        MutableBag<String> target = Bags.mutable.empty();
        Assert.assertEquals(target, this.newBag().countByEach(each -> IntInterval.oneTo(5).collect(i -> each + i), target));
    }

    @Override
    @Test
    public void toSortedBag()
    {
        ImmutableBag<String> immutableBag = this.newBag();
        MutableSortedBag<String> sortedBag = immutableBag.toSortedBag();

        Verify.assertSortedBagsEqual(TreeBag.newBag(), sortedBag);

        MutableSortedBag<String> reverse = immutableBag.toSortedBag(Comparator.reverseOrder());
        Verify.assertSortedBagsEqual(TreeBag.newBag(Comparator.<String>reverseOrder()), reverse);

        ImmutableBag<String> immutableBag1 = this.newBag();
        MutableSortedBag<String> sortedBag1 = immutableBag1.toSortedBag(Comparator.reverseOrder());
        Verify.assertSortedBagsEqual(TreeBag.newBag(), sortedBag1.toSortedBag());

        ImmutableBag<String> immutableBag2 = this.newBag();
        MutableSortedBag<String> sortedBag2 = immutableBag2.toSortedBag(Comparator.reverseOrder());
        Verify.assertSortedBagsEqual(TreeBag.newBag(Comparator.<String>reverseOrder()), sortedBag2);
    }

    @Test
    public void toSortedBag_empty()
    {
        ImmutableBag<String> immutableBag = Bags.immutable.of();

        MutableSortedBag<String> sortedBag = immutableBag.toSortedBag(Comparators.reverseNaturalOrder());
        sortedBag.addOccurrences("apple", 3);
        sortedBag.addOccurrences("orange", 2);

        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Comparators.reverseNaturalOrder(), "orange", "orange", "apple", "apple", "apple"), sortedBag);
    }

    @Test
    public void toSortedBagBy_empty()
    {
        ImmutableBag<Integer> immutableBag = Bags.immutable.of();

        Function<Integer, Integer> function = object -> object * -1;
        MutableSortedBag<Integer> sortedBag = immutableBag.toSortedBagBy(function);
        sortedBag.addOccurrences(1, 3);
        sortedBag.addOccurrences(10, 2);

        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Comparators.byFunction(function), 10, 10, 1, 1, 1), sortedBag);
    }

    @Override
    @Test
    public void toSortedBagBy()
    {
        ImmutableBag<String> immutableBag = this.newBag();
        MutableSortedBag<String> sortedBag = immutableBag.toSortedBagBy(String::valueOf);
        TreeBag<Object> expectedBag = TreeBag.newBag(Comparators.byFunction(String::valueOf));

        Verify.assertSortedBagsEqual(expectedBag, sortedBag);
    }

    @Override
    @Test
    public void selectUnique()
    {
        super.selectUnique();

        ImmutableBag<String> bag = this.newBag();
        ImmutableSet<String> expected = Sets.immutable.empty();
        ImmutableSet<String> actual = bag.selectUnique();
        Assert.assertEquals(expected, actual);
    }
}
