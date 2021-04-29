package me.escoffier.mutiny.maths;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.helpers.test.AssertSubscriber;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TopOperatorTest {

    @Test
    public void testWithEmpty() {
        AssertSubscriber<List<Long>> subscriber = Multi.createFrom().<Long>empty()
                .plug(Maths.top(10))
                .subscribe().withSubscriber(AssertSubscriber.create(10));

        subscriber
                .awaitCompletion()
                .assertHasNotReceivedAnyItem();
    }

    @Test
    public void testWithNever() {
        AssertSubscriber<List<Long>> subscriber = Multi.createFrom().<Long>nothing()
                .plug(Maths.top(3))
                .subscribe().withSubscriber(AssertSubscriber.create(10));

        subscriber.cancel();
        subscriber.assertNotTerminated();
        Assertions.assertEquals(0, subscriber.getItems().size());
    }

    @Test
    public void testWithItems() {
        AssertSubscriber<List<String>> subscriber = Multi.createFrom().items("a", "b", "a", "e", "f", "b", "a", "g", "g", "e", "z")
                .runSubscriptionOn(Infrastructure.getDefaultExecutor())
                .plug(Maths.top(3))
                .subscribe().withSubscriber(AssertSubscriber.create(3));

        subscriber.awaitItems(3)
                .assertItems(List.of("a"), List.of("b", "a"), List.of("e", "b", "a"))
                .request(10)
                .awaitItems(6)
                .assertItems(List.of("a"), List.of("b", "a"), List.of("e", "b", "a"), List.of("f", "e", "b"), List.of("g", "f", "e"), List.of("z", "g", "f"))
                .awaitCompletion();
    }

    @Test
    public void testLast() {
        List<String> last  = Multi.createFrom().items("a", "b", "a", "e", "f", "b", "a", "g", "g", "e", "z")
                .runSubscriptionOn(Infrastructure.getDefaultExecutor())
                .plug(Maths.top(3))
                .collect().last()
                .await().indefinitely();

        Assertions.assertEquals(List.of("z", "g", "f"), last);
    }

    @Test
    public void testEquality() {
        AssertSubscriber<List<String>> subscriber = Multi.createFrom().items("a", "b", "c", "a", "b", "c", "c", "b", "a", "a", "c")
                .runSubscriptionOn(Infrastructure.getDefaultExecutor())
                .plug(Maths.top(3))
                .subscribe().withSubscriber(AssertSubscriber.create(3));

        subscriber.awaitItems(3)
                .assertItems(List.of("a"), List.of("b", "a"), List.of("c", "b", "a"))
                .request(10)
                .awaitCompletion()
                .assertItems(List.of("a"), List.of("b", "a"), List.of("c", "b", "a"));
    }

    @Test
    public void testWithItemsAndFailure() {
        AssertSubscriber<List<String>> subscriber =
                Multi.createBy().concatenating().streams(
                        Multi.createFrom().items("a", "b", "a", "e", "f", "b", "g", "g", "e", "z"),
                        Multi.createFrom().failure(new Exception("boom")))
                        .runSubscriptionOn(Infrastructure.getDefaultExecutor())
                        .plug(Maths.top(3))
                        .subscribe().withSubscriber(AssertSubscriber.create(3));

        subscriber.awaitItems(3)
                .assertItems(List.of("a"), List.of("b", "a"), List.of("e", "b", "a"))
                .request(10)
                .awaitItems(6)
                .assertItems(List.of("a"), List.of("b", "a"), List.of("e", "b", "a"), List.of("f", "e", "b"), List.of("g", "f", "e"), List.of("z", "g", "f"))
                .awaitFailure();
    }

}
