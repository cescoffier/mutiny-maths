package me.escoffier.mutiny.maths.tck;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.tuples.Tuple2;
import me.escoffier.mutiny.maths.Maths;
import org.reactivestreams.Publisher;
import org.reactivestreams.tck.TestEnvironment;
import org.reactivestreams.tck.flow.support.TestException;
import org.reactivestreams.tck.junit5.PublisherVerification;

import static me.escoffier.mutiny.maths.tck.TckHelper.iterate;

public class IndexOperatorTckTest extends PublisherVerification<Tuple2<Long, Long>> {
    public IndexOperatorTckTest() {
        super(new TestEnvironment(100));
    }

    @Override
    public Publisher<Tuple2<Long, Long>> createPublisher(long elements) {
        Multi<Long> multi = Multi.createFrom().iterable(iterate(elements));
        return multi.plug(Maths.index());
    }

    @Override
    public Publisher<Tuple2<Long, Long>> createFailedPublisher() {
        return Multi.createFrom().<Long>failure(new TestException())
                .plug(Maths.index());
    }
}
