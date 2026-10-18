// SPDX-License-Identifier: Apache-2.0
package com.m3.collections.eclipse.impl;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Random;

/** Complete Eclipse source package-only interval/iterator differential on JDK 21. */
public final class M3EclipsePackageRelocationProof {
    private static long checks;
    private M3EclipsePackageRelocationProof() { }

    private static void eq(Object original, Object relocated, String why) {
        checks++;
        if (!Objects.equals(original, relocated))
            throw new AssertionError(why + " original=" + original + " relocated=" + relocated);
    }
    private static String failure(Runnable action) {
        try {action.run();return "OK";}
        catch (Throwable error) {return error.getClass().getName() + ":" + error.getMessage();}
    }
    private static void interval(long from, long to, long step, long point, int index) {
        eq(failure(() -> org.eclipse.collections.impl.list.IntervalUtils.checkArguments(from, to, step)),
                failure(() -> com.m3.collections.eclipse.impl.list.IntervalUtils.checkArguments(from, to, step)),
                "step/range contract");
        if (step == 0) return;
        eq(org.eclipse.collections.impl.list.IntervalUtils.intSize(from, to, step),
                com.m3.collections.eclipse.impl.list.IntervalUtils.intSize(from, to, step), "intSize");
        eq(org.eclipse.collections.impl.list.IntervalUtils.contains(point, from, to, step),
                com.m3.collections.eclipse.impl.list.IntervalUtils.contains(point, from, to, step), "contains");
        eq(org.eclipse.collections.impl.list.IntervalUtils.isWithinBoundaries(point, from, to, step),
                com.m3.collections.eclipse.impl.list.IntervalUtils.isWithinBoundaries(point, from, to, step),
                "within boundaries");
        eq(org.eclipse.collections.impl.list.IntervalUtils.indexOf(point, from, to, step),
                com.m3.collections.eclipse.impl.list.IntervalUtils.indexOf(point, from, to, step), "logical index");
        eq(org.eclipse.collections.impl.list.IntervalUtils.valueAtIndex(index, from, to, step),
                com.m3.collections.eclipse.impl.list.IntervalUtils.valueAtIndex(index, from, to, step),
                "physical value");
        eq(org.eclipse.collections.impl.list.IntervalUtils.binarySearch(point, from, to, step),
                com.m3.collections.eclipse.impl.list.IntervalUtils.binarySearch(point, from, to, step),
                "binary search");
        eq(org.eclipse.collections.impl.list.IntervalUtils.calculateAdjustedStep((int)from,(int)to,(int)step),
                com.m3.collections.eclipse.impl.list.IntervalUtils.calculateAdjustedStep(
                        (int)from,(int)to,(int)step), "direction");
    }
    private static void emptyIterator() {
        var donor = org.eclipse.collections.impl.EmptyIterator.<Integer>getInstance();
        var moved = com.m3.collections.eclipse.impl.EmptyIterator.<Integer>getInstance();
        eq(donor.hasNext(), moved.hasNext(), "hasNext");
        eq(donor.hasPrevious(), moved.hasPrevious(), "hasPrevious");
        eq(donor.nextIndex(), moved.nextIndex(), "nextIndex");
        eq(donor.previousIndex(), moved.previousIndex(), "previousIndex");
        eq(failure(donor::next), failure(moved::next), "next exception");
        eq(failure(donor::previous), failure(moved::previous), "previous exception");
        eq(failure(donor::remove), failure(moved::remove), "remove refusal");
        eq(failure(() -> donor.set(1)), failure(() -> moved.set(1)), "set refusal");
        eq(failure(() -> donor.add(1)), failure(() -> moved.add(1)), "add refusal");
    }
    public static void main(String[] args) {
        if (args.length != 0) throw new IllegalArgumentException("no args");
        emptyIterator();
        long[] extrema = {Long.MIN_VALUE, Long.MIN_VALUE + 1, -100, -1, 0,
                1, 100, Long.MAX_VALUE - 1, Long.MAX_VALUE};
        for (long from : extrema) for (long to : extrema) {
            for (long step : new long[]{-7,-2,-1,0,1,2,7}) {
                interval(from, to, step, 0, 3);
            }
        }
        Random rng = new Random(0x4d3345434c495053L);
        for (int trial=0;trial<12000;trial++) {
            long from=rng.nextInt(5000)-2500, to=rng.nextInt(5000)-2500;
            long step=rng.nextInt(33)-16, point=rng.nextInt(6000)-3000;
            interval(from,to,step,point,rng.nextInt(256));
        }
        System.out.println("PASS M3_ECLIPSE_PACKAGE_RELOCATION checks="+checks);
    }
}
