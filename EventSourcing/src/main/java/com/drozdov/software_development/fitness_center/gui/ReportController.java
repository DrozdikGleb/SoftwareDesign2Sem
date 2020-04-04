package com.drozdov.software_development.fitness_center.gui;

import com.drozdov.software_development.fitness_center.coreapi.FindAllSubscriptionsHistoryQuery;
import com.drozdov.software_development.fitness_center.coreapi.GetAverageVisitDuration;
import com.drozdov.software_development.fitness_center.coreapi.GetVisitsByDayQuery;
import com.drozdov.software_development.fitness_center.query.SubscriptionHistory;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequestMapping("/report")
@RestController
public class ReportController {
    private final QueryGateway queryGateway;

    public ReportController(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @GetMapping("/averageVisitDuration")
    public CompletableFuture<String> avgDuration() {
        return queryGateway.query(new GetAverageVisitDuration(), ResponseTypes.instanceOf(String.class));
    }

    @GetMapping("/visitsByDay")
    public CompletableFuture<String> getVisitsByDay() {
        return queryGateway.query(new GetVisitsByDayQuery(), ResponseTypes.instanceOf(String.class));
    }

    @GetMapping("/getFullHistory")
    public CompletableFuture<List<SubscriptionHistory>> getAllHistory() {
        return queryGateway.query(new FindAllSubscriptionsHistoryQuery(), ResponseTypes.multipleInstancesOf(SubscriptionHistory.class));
    }

}
