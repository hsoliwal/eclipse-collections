/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.bag.mutable;

import java.util.Set;

import com.m3.collections.eclipse.api.RichIterable;
import com.m3.collections.eclipse.api.bag.Bag;
import com.m3.collections.eclipse.api.bag.ImmutableBag;
import com.m3.collections.eclipse.api.bag.MutableBag;
import com.m3.collections.eclipse.api.list.MutableList;
import com.m3.collections.eclipse.api.set.ImmutableSet;
import com.m3.collections.eclipse.api.set.MutableSet;
import com.m3.collections.eclipse.api.tuple.Pair;
import com.m3.collections.eclipse.api.tuple.primitive.ObjectIntPair;
import com.m3.collections.eclipse.impl.bag.mutable.primitive.BooleanHashBag;
import com.m3.collections.eclipse.impl.bag.mutable.primitive.ByteHashBag;
import com.m3.collections.eclipse.impl.bag.mutable.primitive.CharHashBag;
import com.m3.collections.eclipse.impl.bag.mutable.primitive.DoubleHashBag;
import com.m3.collections.eclipse.impl.bag.mutable.primitive.FloatHashBag;
import com.m3.collections.eclipse.impl.bag.mutable.primitive.IntHashBag;
import com.m3.collections.eclipse.impl.bag.mutable.primitive.LongHashBag;
import com.m3.collections.eclipse.impl.bag.mutable.primitive.ShortHashBag;
import com.m3.collections.eclipse.impl.block.factory.PrimitiveFunctions;
import com.m3.collections.eclipse.impl.block.factory.primitive.IntPredicates;
import com.m3.collections.eclipse.impl.collection.mutable.UnmodifiableMutableCollectionTestCase;
import com.m3.collections.eclipse.impl.factory.Bags;
import com.m3.collections.eclipse.impl.factory.Lists;
import com.m3.collections.eclipse.impl.factory.Sets;
import com.m3.collections.eclipse.impl.list.mutable.FastList;
import com.m3.collections.eclipse.impl.map.mutable.UnifiedMap;
import com.m3.collections.eclipse.impl.test.SerializeTestHelper;
import com.m3.collections.eclipse.impl.test.Verify;
import com.m3.collections.eclipse.impl.tuple.Tuples;
import com.m3.collections.eclipse.impl.tuple.primitive.PrimitiveTuples;
import org.junit.Assert;
import org.junit.Test;

import static com.m3.collections.eclipse.impl.factory.Iterables.iBag;

/**
 * Abstract JUnit test for {@link UnmodifiableBag}.
 */
public class UnmodifiableBagTest
        extends UnmodifiableMutableCollectionTestCase<String>
{
    @Override
    protected MutableBag<String> getCollection()
    {
        return Bags.mutable.of("").asUnmodifiable();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addOccurrences()
    {
        this.getCollection().addOccurrences(null, 1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removeOccurrences()
    {
        this.getCollection().removeOccurrences(null, 1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setOccurrences()
    {
        this.getCollection().setOccurrences(null, 1);
    }

    @Test
    public void asUnmodifiable()
    {
        MutableBag<?> bag = this.getCollection();
        Verify.assertInstanceOf(UnmodifiableBag.class, bag.asUnmodifiable());
    }

    @Test
    public void asSynchronized()
    {
        MutableBag<?> bag = this.getCollection();
        Verify.assertInstanceOf(SynchronizedBag.class, bag.asSynchronized());
    }

    @Test
    public void toImmutable()
    {
        MutableBag<?> bag = this.getCollection();
        Verify.assertInstanceOf(ImmutableBag.class, bag.toImmutable());
    }

    @Test
    public void equalsAndHashCode()
    {
        Verify.assertEqualsAndHashCode(this.getCollection(), Bags.mutable.of(""));
        MutableBag<Number> numbers = UnmodifiableBag.of(HashBag.newBagWith(1, 1, 2, 2, 2, 3));
        Verify.assertPostSerializedEqualsAndHashCode(numbers);
        Verify.assertInstanceOf(UnmodifiableBag.class, SerializeTestHelper.serializeDeserialize(numbers));
    }

    @Test
    public void forEachWithOccurrences()
    {
        MutableList<Pair<Object, Integer>> list = Lists.mutable.of();
        this.getCollection().forEachWithOccurrences((each, index) -> list.add(Tuples.pair(each, index)));
        Assert.assertEquals(FastList.newListWith(Tuples.pair("", 1)), list);
    }

    /**
     * @since 9.1.
     */
    @Test
    public void collectWithOccurrences()
    {
        Bag<Integer> bag = Bags.mutable.with(3, 3, 3, 2, 2, 1).asUnmodifiable();
        Bag<ObjectIntPair<Integer>> actual =
                bag.collectWithOccurrences(PrimitiveTuples::pair, Bags.mutable.empty());
        Bag<ObjectIntPair<Integer>> expected =
                Bags.immutable.with(
                        PrimitiveTuples.pair(Integer.valueOf(3), 3),
                        PrimitiveTuples.pair(Integer.valueOf(2), 2),
                        PrimitiveTuples.pair(Integer.valueOf(1), 1));
        Assert.assertEquals(expected, actual);

        Set<ObjectIntPair<Integer>> actual2 =
                bag.collectWithOccurrences(PrimitiveTuples::pair, Sets.mutable.empty());
        ImmutableSet<ObjectIntPair<Integer>> expected2 =
                Sets.immutable.with(
                        PrimitiveTuples.pair(Integer.valueOf(3), 3),
                        PrimitiveTuples.pair(Integer.valueOf(2), 2),
                        PrimitiveTuples.pair(Integer.valueOf(1), 1));
        Assert.assertEquals(expected2, actual2);
    }

    @Test
    public void toMapOfItemToCount()
    {
        Assert.assertEquals(UnifiedMap.newWithKeysValues("", 1), this.getCollection().toMapOfItemToCount());
    }

    @Test
    public void selectInstancesOf()
    {
        MutableBag<Number> numbers = UnmodifiableBag.of(HashBag.newBagWith(1, 2.0, 3, 4.0, 5));
        Assert.assertEquals(iBag(1, 3, 5), numbers.selectInstancesOf(Integer.class));
        Assert.assertEquals(iBag(1, 2.0, 3, 4.0, 5), numbers.selectInstancesOf(Number.class));
    }

    @Test
    public void selectByOccurrences()
    {
        MutableBag<Integer> integers = UnmodifiableBag.of(HashBag.newBagWith(1, 1, 1, 1, 2, 2, 2, 3, 3, 4));
        Assert.assertEquals(iBag(1, 1, 1, 1, 3, 3), integers.selectByOccurrences(IntPredicates.isEven()));
    }

    @Test
    public void selectDuplicates()
    {
        Assert.assertEquals(
                UnmodifiableBag.of(HashBag.newBagWith(1, 1, 1, 1, 2, 2, 2, 3, 3)),
                UnmodifiableBag.of(HashBag.newBagWith(0, 1, 1, 1, 1, 2, 2, 2, 3, 3, 4, 5)).selectDuplicates());
    }

    @Override
    @Test
    public void collectBoolean()
    {
        MutableBag<Integer> integers = UnmodifiableBag.of(HashBag.newBagWith(0, 1, 2, 2));
        Assert.assertEquals(
                BooleanHashBag.newBagWith(false, true, true, true),
                integers.collectBoolean(PrimitiveFunctions.integerIsPositive()));
    }

    @Override
    @Test
    public void collectByte()
    {
        MutableBag<Integer> integers = UnmodifiableBag.of(HashBag.newBagWith(1, 2, 2, 3, 3, 3));
        Assert.assertEquals(
                ByteHashBag.newBagWith((byte) 1, (byte) 2, (byte) 2, (byte) 3, (byte) 3, (byte) 3),
                integers.collectByte(PrimitiveFunctions.unboxIntegerToByte()));
    }

    @Override
    @Test
    public void collectChar()
    {
        MutableBag<Integer> integers = UnmodifiableBag.of(HashBag.newBagWith(1, 2, 2, 3, 3, 3));
        Assert.assertEquals(
                CharHashBag.newBagWith('A', 'B', 'B', 'C', 'C', 'C'),
                integers.collectChar(integer -> (char) (integer.intValue() + 64)));
    }

    @Override
    @Test
    public void collectDouble()
    {
        MutableBag<Integer> integers = UnmodifiableBag.of(HashBag.newBagWith(1, 2, 2, 3, 3, 3));
        Assert.assertEquals(
                DoubleHashBag.newBagWith(1.0d, 2.0d, 2.0d, 3.0d, 3.0d, 3.0d),
                integers.collectDouble(PrimitiveFunctions.unboxIntegerToDouble()));
    }

    @Override
    @Test
    public void collectFloat()
    {
        MutableBag<Integer> integers = UnmodifiableBag.of(HashBag.newBagWith(1, 2, 2, 3, 3, 3));
        Assert.assertEquals(
                FloatHashBag.newBagWith(1.0f, 2.0f, 2.0f, 3.0f, 3.0f, 3.0f),
                integers.collectFloat(PrimitiveFunctions.unboxIntegerToFloat()));
    }

    @Override
    @Test
    public void collectInt()
    {
        MutableBag<Integer> integers = UnmodifiableBag.of(HashBag.newBagWith(1, 2, 2, 3, 3, 3));
        Assert.assertEquals(
                IntHashBag.newBagWith(1, 2, 2, 3, 3, 3),
                integers.collectInt(PrimitiveFunctions.unboxIntegerToInt()));
    }

    @Override
    @Test
    public void collectLong()
    {
        MutableBag<Integer> integers = UnmodifiableBag.of(HashBag.newBagWith(1, 2, 2, 3, 3, 3));
        Assert.assertEquals(
                LongHashBag.newBagWith(1L, 2L, 2L, 3L, 3L, 3L),
                integers.collectLong(PrimitiveFunctions.unboxIntegerToLong()));
    }

    @Override
    @Test
    public void collectShort()
    {
        MutableBag<Integer> integers = UnmodifiableBag.of(HashBag.newBagWith(1, 2, 2, 3, 3, 3));
        Assert.assertEquals(
                ShortHashBag.newBagWith((short) 1, (short) 2, (short) 2, (short) 3, (short) 3, (short) 3),
                integers.collectShort(PrimitiveFunctions.unboxIntegerToShort()));
    }

    @Test
    public void topOccurrences()
    {
        MutableBag<String> mutable = HashBag.newBag();
        mutable.addOccurrences("one", 1);
        mutable.addOccurrences("two", 2);
        mutable.addOccurrences("three", 3);
        mutable.addOccurrences("four", 4);
        mutable.addOccurrences("five", 5);
        mutable.addOccurrences("six", 6);
        mutable.addOccurrences("seven", 7);
        mutable.addOccurrences("eight", 8);
        mutable.addOccurrences("nine", 9);
        mutable.addOccurrences("ten", 10);
        MutableBag<String> strings = mutable.asUnmodifiable();
        MutableList<ObjectIntPair<String>> top5 = strings.topOccurrences(5);
        Verify.assertSize(5, top5);
        Assert.assertEquals("ten", top5.getFirst().getOne());
        Assert.assertEquals(10, top5.getFirst().getTwo());
        Assert.assertEquals("six", top5.getLast().getOne());
        Assert.assertEquals(6, top5.getLast().getTwo());
    }

    @Test
    public void bottomOccurrences()
    {
        MutableBag<String> mutable = HashBag.newBag();
        mutable.addOccurrences("one", 1);
        mutable.addOccurrences("two", 2);
        mutable.addOccurrences("three", 3);
        mutable.addOccurrences("four", 4);
        mutable.addOccurrences("five", 5);
        mutable.addOccurrences("six", 6);
        mutable.addOccurrences("seven", 7);
        mutable.addOccurrences("eight", 8);
        mutable.addOccurrences("nine", 9);
        mutable.addOccurrences("ten", 10);
        MutableBag<String> strings = mutable.asUnmodifiable();
        MutableList<ObjectIntPair<String>> bottom5 = strings.bottomOccurrences(5);
        Verify.assertSize(5, bottom5);
        Assert.assertEquals("one", bottom5.getFirst().getOne());
        Assert.assertEquals(1, bottom5.getFirst().getTwo());
        Assert.assertEquals("five", bottom5.getLast().getOne());
        Assert.assertEquals(5, bottom5.getLast().getTwo());
    }

    @Test
    public void selectUnique()
    {
        MutableBag<String> bag = Bags.mutable.with("0", "1", "1", "1", "1", "2", "2", "2", "3", "3", "4", "5").asUnmodifiable();
        MutableSet<String> expected = Sets.mutable.with("0", "4", "5");
        MutableSet<String> actual = bag.selectUnique();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void distinctView()
    {
        MutableBag<String> bag = Bags.mutable.of("1", "2", "2", "3", "3", "3", "3", "4", "5", "5", "6");
        RichIterable<String> expected = bag.toSet();
        RichIterable<String> actual = bag.asUnmodifiable().distinctView();
        Assert.assertEquals(expected, actual);
    }
}
