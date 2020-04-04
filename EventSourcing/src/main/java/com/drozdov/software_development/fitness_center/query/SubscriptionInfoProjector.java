package com.drozdov.software_development.fitness_center.query;

import com.drozdov.software_development.fitness_center.coreapi.FindAllSubscriptions;
import com.drozdov.software_development.fitness_center.coreapi.FindSubscriptionById;
import com.drozdov.software_development.fitness_center.coreapi.SubscriptionCreatedEvent;
import com.drozdov.software_development.fitness_center.coreapi.SubscriptionUpdatedEvent;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SubscriptionInfoProjector {
    private final SubscriptionInfoRepository subscriptionInfoRepository;

    public SubscriptionInfoProjector(SubscriptionInfoRepository subscriptionInfoRepository) {
        this.subscriptionInfoRepository = subscriptionInfoRepository;
    }

    @EventHandler
    public void on(SubscriptionCreatedEvent event) {
        subscriptionInfoRepository.save(new SubscriptionInfo(event.getSubscriptionId(), event.getOwner(), event.getStartDate(), event.getFinishDate()));
    }

    @EventHandler
    public void on(SubscriptionUpdatedEvent event) {
        subscriptionInfoRepository.findById(event.getSubscriptionId()).map(subscription -> {
            subscription.setEndDate(subscription.getEndDate().plus(event.getDuration()));
            return subscription;
        });
    }

    @QueryHandler
    public List<SubscriptionInfo> handle(FindAllSubscriptions query) {
        return subscriptionInfoRepository.findAll();
    }

    @QueryHandler
    public SubscriptionInfo handle(FindSubscriptionById query) {
        return subscriptionInfoRepository.findById(query.getSubscriptionId()).orElse(null);
    }
}
