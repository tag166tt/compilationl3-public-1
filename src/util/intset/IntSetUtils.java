package util.intset;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class IntSetUtils {
    public static Collector<Integer, Set<Integer>, IntSet> toIntSet(int finalSize) {
        Supplier<Set<Integer>> supplier = HashSet::new;

        BiConsumer<Set<Integer>, Integer> accumulator = Set::add;

        BinaryOperator<Set<Integer>> combiner = (set1, set2) -> {
            HashSet<Integer> n = new HashSet<>(set1);
            n.addAll(set2);
            return n;
        };

        Function<Set<Integer>, IntSet> finisher = set -> {
            IntSet intSet = new IntSet(finalSize);
            for (int element: set)
                intSet.add(element);
            return intSet;
        };

        return Collector.of(supplier, accumulator, combiner, finisher, Collector.Characteristics.UNORDERED);
    }

    public static Set<List<Integer>> twoSetsCartesianProduct(IntSet a, IntSet b) {
        Set<List<Integer>> out = new HashSet<>();

        for (int i = 0; i < a.getSize(); i++) {
            if (!a.isMember(i)) continue;

            for (int j = 0; j < b.getSize(); j++) {
                if (!b.isMember(j)) continue;
                out.add(List.of(i, j));
            }
        }

        return Collections.unmodifiableSet(out);
    }
}
