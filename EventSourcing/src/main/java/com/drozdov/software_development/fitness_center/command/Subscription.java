package com.drozdov.software_development.fitness_center.command;

import com.drozdov.software_development.fitness_center.coreapi.*;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.time.Instant;
import java.util.UUID;

@Aggregate
public class Subscription {
    
    @AggregateIdentifier
    private UUID subscriptionId;
    private Instant endDate;

    public Subscription() {
        //for axon framework
    }
    
    @CommandHandler
    public Subscription(CreateSubscriptionCommand command) {
        AggregateLifecycle.apply(new SubscriptionCreatedEvent(command.getSubscriptionId(), command.getOwner(), command.getStartDate(), command.getFinishDate()));
    }
    
    @CommandHandler
    public void handle(UpdateSubscriptionCommand command) {
        AggregateLifecycle.apply(new SubscriptionUpdatedEvent(command.getSubscriptionId(), command.getDuration()));
    }
    
    @CommandHandler
    public void handle(EnterClientCommand command) throws SubscriptionExpiredException {
        if (endDate.isBefore(Instant.now())) {
            throw new SubscriptionExpiredException();
        }
        AggregateLifecycle.apply(new ClientEnteredEvent(command.getSubscriptionId()));
    }
    
    @CommandHandler
    public void handle(ExitClientCommand command) {
        AggregateLifecycle.apply(new ClientExitedEvent(command.getSubscriptionId()));
    }
    
    @EventHandler
    public void on(SubscriptionCreatedEvent event) {
        subscriptionId = event.getSubscriptionId();
        endDate = event.getFinishDate();
    }
    
    @EventHandler
    public void on(SubscriptionUpdatedEvent event) {
        endDate.plus(event.getDuration());
    }
}
