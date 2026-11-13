// SPDX-License-Identifier: Apache-2.0
// Exploratory actual Eclipse FastList benchmark. Not JMH.
import com.m3.collections.eclipse.impl.block.factory.Predicates2;
import com.m3.collections.eclipse.impl.list.mutable.FastList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/** Compare the inherited membership predicate with the guarded array HashSet path. */
public final class FastListBulkBenchmark {
    private static volatile long sink;

    private static long measure(List<Integer> base, ArrayList<Integer> remove, boolean bulk) {
        FastList<Integer> list = FastList.newList(base);
        long before = System.nanoTime();
        boolean changed;
        if (bulk) {
            changed = list.removeAll(remove);
        } else {
            int size = list.size();
            list.removeIfWith(Predicates2.in(), remove);
            changed = list.size() != size;
        }
        long elapsed = System.nanoTime() - before;
        if (!changed || list.size() != base.size() - remove.size()) {
            throw new AssertionError("FASTLIST_BULK_CONTRACT_DRIFT");
        }
        sink += list.size();
        return elapsed;
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("USAGE: arraySize removeCount samples");
        }
        int count = Integer.parseInt(args[0]);
        int removed = Integer.parseInt(args[1]);
        int samples = Integer.parseInt(args[2]);
        if (count <= 0 || removed < 1 || removed >= count || samples < 3) {
            throw new IllegalArgumentException("INVALID_BENCH_ARGUMENTS");
        }
        ArrayList<Integer> base = new ArrayList<>(count);
        ArrayList<Integer> removal = new ArrayList<>(removed);
        for (int i = 0; i < count; i++) {
            base.add(i);
        }
        for (int i = 0; i < removed; i++) {
            removal.add((i * 3) % count);
        }
        for (int i = 0; i < 3; i++) {
            measure(base, removal, false);
            measure(base, removal, true);
        }
        long[] original = new long[samples];
        long[] optimized = new long[samples];
        for (int i = 0; i < samples; i++) {
            if ((i & 1) == 0) {
                original[i] = measure(base, removal, false);
                optimized[i] = measure(base, removal, true);
            } else {
                optimized[i] = measure(base, removal, true);
                original[i] = measure(base, removal, false);
            }
        }
        Arrays.sort(original);
        Arrays.sort(optimized);
        System.out.printf(Locale.ROOT,
                "FASTLIST_BULK_BENCH n=%d m=%d baseline_ns=%d candidate_ns=%d samples=%d sink=%d%n",
                count, removed, original[samples / 2], optimized[samples / 2], samples, sink);
    }
}
