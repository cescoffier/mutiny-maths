package me.escoffier.mutiny.maths;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.tuples.Tuple2;

import java.util.function.Function;

/**
 * Min operator emitting the min of the emitted items from upstream.
 * If the upstream emits a failure, the failure is propagated.
 *
 * It only emits the minimum if the received item is smaller than the previously emitted minimum.
 *
 * Note that this operator relies on {@link Comparable}.
 *
 * @param <T> type of the incoming items.
 */
public class MinOperator<T extends Comparable<T>>
        implements Function<Multi<T>, Multi<T>> {

    private T min = null;

    @Override
    public Multi<T> apply(Multi<T> multi) {
        return multi
                .onItem().transformToMultiAndConcatenate(item -> {
                    if (min == null || min.compareTo(item) > 0) {
                        min = item;
                        return Multi.createFrom().item(min);
                    }
                    return Multi.createFrom().empty();
                });
    }
}
