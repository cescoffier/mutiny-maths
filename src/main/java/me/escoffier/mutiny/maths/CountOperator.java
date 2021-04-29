package me.escoffier.mutiny.maths;

import io.smallrye.mutiny.Multi;

import java.util.function.Function;

/**
 * Count operator emitting the current count.
 * Everytime it gets an item from upstream, it emits the <em>count</em>.
 * If the stream emits the completion event without having emitting any item before, 0 is emitted, following by the
 * completion event.
 * If the upstream emits a failure, the failure is propagated.
 *
 * @param <T> type of the incoming items.
 */
public class CountOperator<T>
        implements Function<Multi<T>, Multi<Long>> {

    private long count = 0;

    @Override
    public Multi<Long> apply(Multi<T> multi) {
        return multi
                .onItem().transform(x -> {
                    count = count + 1;
                    return count;
                })
                .onCompletion().ifEmpty().continueWith(0L);
    }
}
