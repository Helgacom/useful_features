package com.helga.lib.java24.gatherer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

public record BiggestInt(int limit) implements Gatherer<Integer, List<Integer>, Integer> {

    // see usage StreamGatherersExample psvm ->

    @Override
    public Supplier<List<Integer>> initializer() {
        return () -> new ArrayList<Integer>(1);
    }

    @Override
    public Integrator<List<Integer>, Integer, Integer> integrator() {
        return Integrator.of(
                (max, element, downstream) -> {
                    if (max.isEmpty()) max.addFirst(element);
                    else if (element > max.getFirst()) max.set(0, element);
                    if (element > limit) {
                        downstream.push(element);
                        return false;
                    }
                    return true;
                }
        );
    }

    @Override
    public BinaryOperator<List<Integer>> combiner() {
        return (leftMax, rightMax) -> {
            if (leftMax.isEmpty()) return rightMax;
            if (rightMax.isEmpty()) return leftMax;
            int leftVal = leftMax.getFirst();
            int rightVal = rightMax.getFirst();
            if (leftVal > rightVal) return leftMax;
            else return rightMax;
        };
    }


    @Override
    public BiConsumer<List<Integer>, Downstream<? super Integer>> finisher() {
        return (max, downstream) -> {
            if (!max.isEmpty()) {
                downstream.push(max.getFirst());
            }
        };
    }
}
