package com.drozdov.software_development.fitness_center.coreapi

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.time.Duration
import java.time.Instant
import java.util.*

data class SubscriptionCreatedEvent (
        @TargetAggregateIdentifier val subscriptionId: UUID,
        val owner: String,
        val startDate: Instant,
        val finishDate: Instant
)

data class SubscriptionUpdatedEvent (
        @TargetAggregateIdentifier val subscriptionId: UUID,
        val duration: Duration
)

data class ClientEnteredEvent(
        @TargetAggregateIdentifier val subscriptionId: UUID
)

data class ClientExitedEvent(
        @TargetAggregateIdentifier val subscriptionId: UUID
)


