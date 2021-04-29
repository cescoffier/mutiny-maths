package me.escoffier.mutiny.maths;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.tuples.Tuple2;

import java.util.function.Function;

/**
 * Index operator emitting a {@link io.smallrye.mutiny.tuples.Tuple2 Tuple2&lt;Long, T&gt;}, with the index (0-based) of the element
 * and the element from upstream.
 * If the upstream emits a failure, the failure is propagated.
 *
 * @param <T> type of the incoming items.
 */
public class IndexOperator<T>
        implements Function<Multi<T>, Multi<Tuple2<Long, T>>> {

    private long index = 0;

    @Override
    public Multi<Tuple2<Long, T>> apply(Multi<T> multi) {
        return multi
                .onItem().transform(x -> Tuple2.of(index++, x));
    }
}
