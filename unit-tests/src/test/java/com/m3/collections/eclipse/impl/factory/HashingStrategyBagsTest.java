/*
 * Copyright (c) 2022 Bhavana Hindupur and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package com.m3.collections.eclipse.impl.factory;

import com.m3.collections.eclipse.api.bag.MutableBag;
import com.m3.collections.eclipse.api.factory.bag.strategy.MutableHashingStrategyBagFactory;
import com.m3.collections.eclipse.api.list.MutableList;
import com.m3.collections.eclipse.impl.bag.mutable.HashBag;
import com.m3.collections.eclipse.impl.bag.strategy.mutable.HashBagWithHashingStrategy;
import com.m3.collections.eclipse.impl.block.factory.HashingStrategies;
import com.m3.collections.eclipse.impl.list.mutable.FastList;
import com.m3.collections.eclipse.impl.test.Verify;
import com.m3.collections.eclipse.impl.test.domain.Person;
import org.junit.Assert;
import org.junit.Test;

public class HashingStrategyBagsTest
{
    @Test
    public void mutable()
    {
        MutableHashingStrategyBagFactory factory = HashingStrategyBags.mutable;
        Assert.assertEquals(HashBag.newBag(), factory.of(HashingStrategies.defaultStrategy()));
        Verify.assertInstanceOf(MutableBag.class, factory.of(HashingStrategies.defaultStrategy()));
        Assert.assertEquals(HashBag.newBag(), factory.empty(HashingStrategies.defaultStrategy()));
        Verify.assertInstanceOf(MutableBag.class, factory.empty(HashingStrategies.defaultStrategy()));
        Assert.assertEquals(HashBag.newBagWith(1, 2), factory.of(HashingStrategies.defaultStrategy(), 1, 2));
        Verify.assertInstanceOf(MutableBag.class, factory.of(HashingStrategies.defaultStrategy(), 1, 2));
        Assert.assertEquals(HashBag.newBagWith(1, 2, 3, 4), factory.of(HashingStrategies.defaultStrategy(), 1, 2, 3, 4));
        Verify.assertInstanceOf(MutableBag.class, factory.of(HashingStrategies.defaultStrategy(), 1, 2, 3, 4));
        Assert.assertEquals(HashBag.newBagWith(1, 2, 3, 4, 5, 6), factory.of(HashingStrategies.defaultStrategy(), 1, 2, 3, 4, 5, 6));
        Verify.assertInstanceOf(MutableBag.class, factory.of(HashingStrategies.defaultStrategy(), 1, 2, 3, 4, 5, 6));
        Assert.assertEquals(HashBag.newBagWith(1, 2, 3, 4, 5, 6, 7, 8), factory.of(HashingStrategies.defaultStrategy(), 1, 2, 3, 4, 5, 6, 7, 8));
        Verify.assertInstanceOf(MutableBag.class, factory.of(HashingStrategies.defaultStrategy(), 1, 2, 3, 4, 5, 6, 7, 8));
        Assert.assertEquals(HashBag.newBagWith(1, 2, 3, 4, 5, 6, 7, 8), factory.ofAll(HashingStrategies.defaultStrategy(), FastList.newListWith(1, 2, 3, 4, 5, 6, 7, 8)));
        Verify.assertInstanceOf(MutableBag.class, factory.of(HashingStrategies.defaultStrategy(), FastList.newListWith(1, 2, 3, 4, 5, 6, 7, 8)));

        MutableList<Person> people =
                Lists.mutable.of(new Person("Alex", "Smith"), new Person("John", "Smith"), new Person("John", "Brown"));

        Assert.assertEquals(HashBagWithHashingStrategy.newBagWith(HashingStrategies.fromFunction(Person::getLastName)).withAll(people),
                factory.fromFunction(Person::getLastName).withAll(people));

        Assert.assertEquals(HashBagWithHashingStrategy.newBagWith(HashingStrategies.fromFunction(Person::getFirstName)).withAll(people),
                factory.fromFunction(Person::getFirstName).withAll(people));
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(HashingStrategyBags.class);
    }
}
