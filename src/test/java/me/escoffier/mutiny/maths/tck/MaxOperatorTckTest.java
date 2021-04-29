package me.escoffier.mutiny.maths.tck;

import io.smallrye.mutiny.Multi;
import me.escoffier.mutiny.maths.Maths;
import org.reactivestreams.Publisher;
import org.reactivestreams.tck.TestEnvironment;
import org.reactivestreams.tck.flow.support.TestException;
import org.reactivestreams.tck.junit5.PublisherVerification;

import static me.escoffier.mutiny.maths.tck.TckHelper.iterate;

public class MaxOperatorTckTest extends PublisherVerification<Long> {
    public MaxOperatorTckTest() {
        super(new TestEnvironment(100));
    }

    // NOTE: It works as the upstream generates always increasing numbers.

    @Override
    public Publisher<Long> createPublisher(long elements) {
        Multi<Long> multi = Multi.createFrom().iterable(iterate(elements));
        return multi.plug(Maths.max());
    }

    @Override
    public Publisher<Long> createFailedPublisher() {
        return Multi.createFrom().<Long>failure(new TestException())
                .plug(Maths.max());
    }
}
