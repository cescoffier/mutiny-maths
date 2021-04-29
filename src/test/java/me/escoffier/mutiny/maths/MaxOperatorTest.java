package me.escoffier.mutiny.maths;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.helpers.test.AssertSubscriber;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MaxOperatorTest {

    @Test
    public void testWithEmpty() {
        AssertSubscriber<Long> subscriber = Multi.createFrom().<Long>empty()
                .plug(Maths.max())
                .subscribe().withSubscriber(AssertSubscriber.create(10));

        subscriber
                .awaitCompletion()
                .assertHasNotReceivedAnyItem();
    }

    @Test
    public void testWithNever() {
        AssertSubscriber<Long> subscriber = Multi.createFrom().<Long>nothing()
                .plug(Maths.max())
                .subscribe().withSubscriber(AssertSubscriber.create(10));

        subscriber.cancel();
        subscriber.assertNotTerminated();
        Assertions.assertEquals(0, subscriber.getItems().size());
    }

    @Test
    public void testWithItems() {
        AssertSubscriber<String> subscriber = Multi.createFrom().items("e", "b", "c", "f", "g", "e")
                .runSubscriptionOn(Infrastructure.getDefaultExecutor())
                .plug(Maths.max())
                .subscribe().withSubscriber(AssertSubscriber.create(3));

        subscriber.awaitItems(3)
                .assertItems("e", "f", "g")
                .request(10)
                .awaitCompletion()
                .assertItems("e", "f", "g");
    }

    @Test
    public void testLatest() {
        String max = Multi.createFrom().items("e", "b", "c", "f", "g", "e")
                .runSubscriptionOn(Infrastructure.getDefaultExecutor())
                .plug(Maths.max())
                .collect().last()
                .await().indefinitely();

        Assertions.assertEquals("g", max);
    }

    @Test
    public void testWithItemsAndFailure() {
        AssertSubscriber<String> subscriber =
                Multi.createBy().concatenating().streams(
                        Multi.createFrom().items("a", "b", "c", "c", "a", "e", "a", "q", "x"),
                        Multi.createFrom().failure(new Exception("boom")))
                        .runSubscriptionOn(Infrastructure.getDefaultExecutor())
                        .plug(Maths.max())
                        .subscribe().withSubscriber(AssertSubscriber.create(3));

        subscriber.awaitItems(3)
                .assertItems("a", "b", "c")
                .request(10)
                .awaitItems(6)
                .assertItems("a", "b", "c", "e", "q", "x")
                .awaitFailure();
    }
}
