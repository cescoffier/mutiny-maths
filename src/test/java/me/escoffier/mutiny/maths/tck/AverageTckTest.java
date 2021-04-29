package me.escoffier.mutiny.maths.tck;

import io.smallrye.mutiny.Multi;
import me.escoffier.mutiny.maths.Maths;
import org.reactivestreams.Publisher;
import org.reactivestreams.tck.TestEnvironment;
import org.reactivestreams.tck.flow.support.TestException;
import org.reactivestreams.tck.junit5.PublisherVerification;

import static me.escoffier.mutiny.maths.tck.TckHelper.iterate;

public class AverageTckTest extends PublisherVerification<Double> {
    public AverageTckTest() {
        super(new TestEnvironment(100));
    }

    @Override
    public Publisher<Double> createPublisher(long elements) {
        Multi<Long> multi = Multi.createFrom().iterable(iterate(elements));
        return multi
                .plug(Maths.average());
    }

    @Override
    public Publisher<Double> createFailedPublisher() {
        return Multi.createFrom().<Long>failure(new TestException())
                .plug(Maths.average());
    }
}
