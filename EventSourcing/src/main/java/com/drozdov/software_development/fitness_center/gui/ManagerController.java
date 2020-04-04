package com.drozdov.software_development.fitness_center.gui;

import com.drozdov.software_development.fitness_center.coreapi.CreateSubscriptionCommand;
import com.drozdov.software_development.fitness_center.coreapi.FindAllSubscriptions;
import com.drozdov.software_development.fitness_center.coreapi.FindSubscriptionById;
import com.drozdov.software_development.fitness_center.coreapi.UpdateSubscriptionCommand;
import com.drozdov.software_development.fitness_center.query.SubscriptionInfo;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static java.time.temporal.ChronoUnit.DAYS;

@RequestMapping("/manager")
@RestController
public class ManagerController {

    private static SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy HH:mm:ss");
    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public ManagerController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @GetMapping("/create")
    public CompletableFuture<String> createSubscription(@RequestParam("owner") String owner,
                                                        @RequestParam("durationDays") Integer durationDays) {
        final Instant startDate = Instant.now();
        final Instant endData = startDate.plus(durationDays, DAYS);
        UUID subsId = UUID.randomUUID();
        return commandGateway.send(new CreateSubscriptionCommand(subsId, owner, startDate, endData)).thenApply(v ->
                String.format("Client %s buy subscription with id %s to %s", owner, subsId, formatter.format(Date.from(endData)))
        );
    }

    @GetMapping("/update/{subscriptionId}")
    public CompletableFuture<String> updateSubscription(@PathVariable("subscriptionId") UUID subscriptionId,
                                                        @RequestParam("durationDays") Integer durationDays) {
        return commandGateway.send(new UpdateSubscriptionCommand(subscriptionId, Duration.of(durationDays, DAYS))).thenApply(v ->
                String.format("Subscription with id %s update on %d days", subscriptionId, durationDays)
        );
    }

    @GetMapping("/showInfo/{subscriptionId}")
    public CompletableFuture<SubscriptionInfo> findOne(@PathVariable("subscriptionId") UUID subscriptionId) {
        return queryGateway.query(new FindSubscriptionById(subscriptionId), ResponseTypes.instanceOf(SubscriptionInfo.class));
    }

    @GetMapping("/findAllSubscriptions")
    public CompletableFuture<List<SubscriptionInfo>> findAllSubs() {
        return queryGateway.query(new FindAllSubscriptions(), ResponseTypes.multipleInstancesOf(SubscriptionInfo.class));
    }

}
