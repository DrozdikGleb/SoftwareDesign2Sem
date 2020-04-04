package com.drozdov.software_development.fitness_center.command;

import com.drozdov.software_development.fitness_center.coreapi.*;
import com.drozdov.software_development.fitness_center.query.Action;
import com.drozdov.software_development.fitness_center.query.SubscriptionHistory;
import com.drozdov.software_development.fitness_center.query.SubscriptionHistoryProjector;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.DAYS;

public class SubscriptionTest {
    private FixtureConfiguration<Subscription> fixture;

    @Before
    public void setUp() {
        fixture = new AggregateTestFixture<>(Subscription.class);
    }

    @Test
    public void testCreateSubscriptionCommand() {
        UUID subsId = UUID.randomUUID();
        Instant startTime = Instant.now();
        Instant endTime = startTime.plus(Duration.of(4, DAYS));
        String owner = "gleb";
        fixture.givenNoPriorActivity()
                .when(new CreateSubscriptionCommand(subsId, owner, startTime, endTime))
                .expectSuccessfulHandlerExecution()
                .expectEvents(new SubscriptionCreatedEvent(subsId, owner, startTime, endTime));
    }

    @Test
    public void testUpdateSubscriptionCommand() {
        UUID subsId = UUID.randomUUID();
        Instant startTime = Instant.now();
        Instant endTime = startTime.plus(Duration.of(4, DAYS));
        String owner = "gleb";
        fixture.given(new SubscriptionCreatedEvent(subsId, owner, startTime, endTime))
                .when(new UpdateSubscriptionCommand(subsId, Duration.of(4, DAYS)))
                .expectEvents(new SubscriptionUpdatedEvent(subsId, Duration.of(4, DAYS)));
    }

    @Test
    public void testExpireSubmission() {
        UUID subsId = UUID.randomUUID();
        Instant startTime = Instant.now();
        Instant endTime = startTime.minus(Duration.of(4, DAYS));
        String owner = "gleb";
        fixture.given(new SubscriptionCreatedEvent(subsId, owner, startTime, endTime))
                .when(new EnterClientCommand(subsId))
                .expectException(SubscriptionExpiredException.class);
    }

    @Test
    public void testNotEnoughAverageDuration() {
        String actualAverageDuration = SubscriptionHistoryProjector.getAverageVisitDuration(Collections.emptyList());
        Assert.assertEquals("Not enough data for average visit duration statistics", actualAverageDuration);
    }

    @Test
    public void testAverageDuration() {
        UUID uuid = UUID.randomUUID();
        List<SubscriptionHistory> histories = Arrays.asList(
                new SubscriptionHistory(UUID.randomUUID(), uuid, Instant.now(), Action.ENTER),
                new SubscriptionHistory(UUID.randomUUID(), uuid, Instant.now().plusSeconds(100), Action.EXIT),
                new SubscriptionHistory(UUID.randomUUID(), uuid, Instant.now().plusSeconds(100), Action.ENTER),
                new SubscriptionHistory(UUID.randomUUID(), uuid, Instant.now().plusSeconds(300), Action.EXIT)
        );
        String actualAverageDuration = SubscriptionHistoryProjector.getAverageVisitDuration(histories);
        Assert.assertEquals("Average visit duration is 150,00 seconds", actualAverageDuration);
    }

    @Test
    public void testVisitsPerDay() {
        UUID uuid = UUID.randomUUID();
        List<SubscriptionHistory> histories = Arrays.asList(
                new SubscriptionHistory(UUID.randomUUID(), uuid, Instant.now(), Action.ENTER),
                new SubscriptionHistory(UUID.randomUUID(), uuid, Instant.now().plusSeconds(100), Action.ENTER),
                new SubscriptionHistory(UUID.randomUUID(), uuid, Instant.now().plus(Duration.of(2, DAYS)), Action.ENTER),
                new SubscriptionHistory(UUID.randomUUID(), uuid, Instant.now().plus(Duration.of(2, DAYS)), Action.ENTER)
        );
        String actualAverageDuration = SubscriptionHistoryProjector.getVisitsByDay(histories);
        Assert.assertEquals("06 04 2020 were 2 visits\n04 04 2020 were 2 visits", actualAverageDuration);
    }
}
