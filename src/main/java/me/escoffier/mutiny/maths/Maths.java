package me.escoffier.mutiny.maths;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.tuples.Tuple2;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * A set of Mutiny operators related to various mathematical functions.
 * These operators are intended to be used using {@code plug}. For example:
 * {@code multi.plug(Maths.count())}
 */
public class Maths {

    /**
     * Emits the number of items emitted by the upstream.
     * On each received item, the new count is emitted downstream.
     * <p>
     * The final count can be retrieved using {@code multi.plug(Maths.count()).collect().last()}.
     * Do not use that approach on unbounded streams.
     * <p>
     * If the upstream emits a failure, the failure is propagated downstream.
     * If the upstream completes without having emitted any item, 0 is emitted, followed by the completion event.
     *
     * @param <T> the type of item emitted by the upstream
     * @return a multi emitting the number of items emitted by the upstream
     */
    public static <T> Function<Multi<T>, Multi<Long>> count() {
        return new CountOperator<>();
    }

    /**
     * Emits the sum of all the items previously emitted by the upstream.
     * On each received item, the new sum is emitted downstream.
     * <p>
     * The final sum can be retrieved using {@code multi.plug(Maths.sum()).collect().last()}.
     * Do not use that approach on unbounded streams.
     * <p>
     * If the upstream emits a failure, the failure is propagated downstream.
     * If the upstream completes without having emitted any item, 0 is emitted, followed by the completion event.
     * <p>
     * Note the the sum are provided as {@link Double}.
     *
     * @param <T> the type of item emitted by the upstream
     * @return a multi emitting the sum of the items emitted by the upstream
     */
    public static <T extends Number> Function<Multi<T>, Multi<Double>> sum() {
        return new SumOperator<>();
    }

    /**
     * Emits the index (0-based) of items emitted by the upstream.
     * On each received item, the new index structure is emitted downstream.
     * <p>
     * The index is given as a {@link Tuple2 Tuple2&lt;Long, T&gt;}, with the first item is the index and the second item
     * is the item.
     * <p>
     * If the upstream emits a failure, the failure is propagated downstream.
     * If the upstream completes without having emitted any item, it sends the completion event.
     *
     * @param <T> the type of item emitted by the upstream
     * @return a multi emitting the tuples containing the index and the item emitted by the upstream
     */
    public static <T> Function<Multi<T>, Multi<Tuple2<Long, T>>> index() {
        return new IndexOperator<>();
    }

    /**
     * Emits the average of the items previously emitted by the upstream.
     * On each received item, the new average is emitted downstream.
     * <p>
     * The final sum can be retrieved using {@code multi.plug(Maths.average()).collect().last()}.
     * Do not use that approach on unbounded streams.
     * <p>
     * If the upstream emits a failure, the failure is propagated downstream.
     * If the upstream completes without having emitted any item, 0.0 is emitted, followed by the completion event.
     * <p>
     *
     * @param <T> the type of item emitted by the upstream
     * @return a multi emitting the average of the items emitted by the upstream
     */
    public static <T extends Number> Function<Multi<T>, Multi<Double>> average() {
        return new AverageOperator<>();
    }

    /**
     * Emits the minimum of the item emitted by the upstream.
     * Each time that the upstream emits an item, this operator check if this item is <em>smaller</em> than the
     * previous minimum. If so, it emits the new minimum downstream.
     * <p>
     * The final minimum can be retrieved using {@code multi.plug(Maths.min()).collect().last()}.
     * Do not use that approach on unbounded streams.
     * <p>
     * This operator uses {@link Comparable} items, the the {@link Comparable#compareTo(Object)} method is used to determine
     * the minimum.
     * <p>
     * If the upstream emits a failure, the failure is propagated downstream.
     * If the upstream completes without having emitted any item, 0 is emitted, followed by the completion event.
     *
     * @param <T> the type of item emitted by the upstream
     * @return a multi emitting the <em>smallest</em> item emitted by the upstream. The operator emits a new minimum every time a new minimum is received from the upstream.
     */
    public static <T extends Comparable<T>> Function<Multi<T>, Multi<T>> min() {
        return new MinOperator<>();
    }

    /**
     * Emits the maximum of the item emitted by the upstream.
     * Each time that the upstream emits an item, this operator check if this item is <em>larger</em> than the
     * previous maximum. If so, it emits the new maximum downstream.
     * <p>
     * The final minimum can be retrieved using {@code multi.plug(Maths.max()).collect().last()}.
     * Do not use that approach on unbounded streams.
     * <p>
     * This operator uses {@link Comparable} items, the the {@link Comparable#compareTo(Object)} method is used to determine
     * the maximum.
     * <p>
     * If the upstream emits a failure, the failure is propagated downstream.
     * If the upstream completes without having emitted any item, 0 is emitted, followed by the completion event.
     *
     * @param <T> the type of item emitted by the upstream
     * @return a multi emitting the <em>largest</em> item emitted by the upstream. The operator emits a new maximum every time a new maximum is received from the upstream.
     */
    public static <T extends Comparable<T>> Function<Multi<T>, Multi<T>> max() {
        return new MaxOperator<>();
    }

    /**
     * Emits the top {@code count} items that have been emitted by the upstream.
     * This operator sorts the top {@code count} items from the upstream and emits the ranking.
     * <p>
     * On each received item, the new ranking is emitted downstream if the ranking changes.
     * <p>
     * The final ranking can be retrieved using {@code multi.plug(Maths.top(3)).collect().last()}.
     * Do not use that approach on unbounded streams.
     * <p>
     * If the upstream emits a failure, the failure is propagated downstream.
     * If the upstream completes without having emitted any item, it emits the completion event.
     * <p>
     * This operator maintains a sorted ranking of the item emitted by the upstream. The hold structured is cleared on termination (including on cancellation).
     * It compares the item using the {@link Comparable#compareTo(Object)} method. Each time that the maintained ranking changes, it emits the newly computed ranking downstream.
     * That emitted structured is a {@link List List&lt;T&gt;} containing at most {@code count} items.
     *
     * @param count the number of items composing the ranking, for example, 3 for a top 3, 10 for a top 10.
     * @param <T>   the type of item emitted by the upstream
     * @return a multi emitting the top x items emitted by the upstream
     */
    public static <T extends Comparable<T>> Function<Multi<T>, Multi<List<T>>> top(int count) {
        return new TopOperator<>(count);
    }

    /**
     * Emits the number of occurrences of each item emitted by the upstream.
     * This operators keep track all of the item emitted by the upstream and stores how many times each items is emitted.
     * Do not uses this operators if the item domain is unbounded.
     * <p>
     * After each emitted item, this operator emits a {@link Map Map&lt;T, Long&gt;} containing for each seen item, how many times
     * they have been seen.
     * <p>
     * If the upstream sends the completion even beofre having sent any item, this operators emits an empty map, followed
     * with the completion event.
     * If the upstream emits a failure, the failure is passed downstream.
     * <p>
     * On termination, including cancellation, the hold counts are cleared.
     *
     * @param <T> the type of item, must be a valid {@link java.util.HashMap} key.
     * @return the multi emitting the number of occurrences for each item.
     */
    public static <T> Function<Multi<T>, Multi<Map<T, Long>>> occurrence() {
        return new OccurrenceOperator<>();
    }
}
