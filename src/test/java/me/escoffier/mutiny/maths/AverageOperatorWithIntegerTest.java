package me.escoffier.mutiny.maths;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.helpers.test.AssertSubscriber;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AverageOperatorWithIntegerTest {

    @Test
    public void testWithEmpty() {
        AssertSubscriber<Double> subscriber = Multi.createFrom().<Integer>empty()
                .plug(Maths.average())
                .subscribe().withSubscriber(AssertSubscriber.create(10));

        subscriber.awaitItems(1)
                .awaitCompletion();
    }

    @Test
    public void testWithNever() {
        AssertSubscriber<Double> subscriber = Multi.createFrom().<Integer>nothing()
                .plug(Maths.average())
                .subscribe().withSubscriber(AssertSubscriber.create(10));

        subscriber.cancel();
        subscriber.assertNotTerminated();
        Assertions.assertEquals(0, subscriber.getItems().size());
    }

    @Test
    public void testWithItems() {
        AssertSubscriber<Double> subscriber = Multi.createFrom().items(1, 2, 3, 4, 2, 5)
                .runSubscriptionOn(Infrastructure.getDefaultExecutor())
                .plug(Maths.average())
                .subscribe().withSubscriber(AssertSubscriber.create(3));

        subscriber.awaitItems(3)
                .assertItems(1.0, 1.5, 2.0)
                .request(10)
                .awaitItems(6)
                .assertItems(1.0, 1.5, 2.0, 10.0 / 4, 12.0 / 5, 17.0 / 6)
                .awaitCompletion();
    }

    @Test
    public void testLatest() {
        double average = Multi.createFrom().items(1, 2, 3, 4, 2, 5)
                .runSubscriptionOn(Infrastructure.getDefaultExecutor())
                .plug(Maths.average())
                .collect().last()
                .await().indefinitely();

        Assertions.assertEquals(17.0 / 6, average);
    }

    @Test
    public void testWithItemsAndFailure() {
        AssertSubscriber<Double> subscriber =
                Multi.createBy().concatenating().streams(
                    Multi.createFrom().items(1, 2, 3, 4, 2),
                    Multi.createFrom().failure(new Exception("boom")))
                .runSubscriptionOn(Infrastructure.getDefaultExecutor())
                .plug(Maths.average())
                .subscribe().withSubscriber(AssertSubscriber.create(3));

        subscriber.awaitItems(3)
                .assertItems(1.0, 1.5, 2.0)
                .request(10)
                .awaitItems(5)
                .assertItems(1.0, 1.5, 2.0, 10.0 / 4, 12.0 / 5)
                .awaitFailure();
    }
}
