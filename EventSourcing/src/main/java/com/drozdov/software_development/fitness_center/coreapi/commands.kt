package com.drozdov.software_development.fitness_center.coreapi

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.time.Duration
import java.time.Instant
import java.util.*

data class CreateSubscriptionCommand (
        @TargetAggregateIdentifier val subscriptionId: UUID,
        val owner: String,
        val startDate: Instant, 
        val finishDate: Instant
)

data class UpdateSubscriptionCommand (
        @TargetAggregateIdentifier val subscriptionId: UUID,
        val duration: Duration
)

data class EnterClientCommand(
        @TargetAggregateIdentifier val subscriptionId: UUID
)

data class ExitClientCommand(
        @TargetAggregateIdentifier val subscriptionId: UUID
)



