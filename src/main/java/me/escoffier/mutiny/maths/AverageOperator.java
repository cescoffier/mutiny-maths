package me.escoffier.mutiny.maths;

import io.smallrye.mutiny.Multi;

import java.util.function.Function;

/**
 * Average operator emitting the current count.
 * Everytime it gets an item from upstream, it emits the <em>average</em> of the already received items.
 * If the stream emits the completion event without having emitting any item before, 0 is emitted, following by the
 * completion event.
 * If the upstream emits a failure, the failure is propagated.
 */
public class AverageOperator<T extends Number>
        implements Function<Multi<T>, Multi<Double>> {

    private double sum = 0;
    private long count = 0;

    @Override
    public Multi<Double> apply(Multi<T> multi) {
        return multi
                .onItem().transform(x -> {
                    count = count + 1;
                    sum = sum + x.doubleValue();
                    return sum / count;
                })
                .onCompletion().ifEmpty().continueWith(0.0);
    }
}
