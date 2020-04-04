package com.drozdov.software_development.fitness_center.query;

import com.drozdov.software_development.fitness_center.coreapi.*;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SubscriptionHistoryProjector {
    private static SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy");
    private final SubscriptionHistoryRepository subscriptionHistoryRepository;

    public SubscriptionHistoryProjector(SubscriptionHistoryRepository subscriptionHistoryRepository) {
        this.subscriptionHistoryRepository = subscriptionHistoryRepository;
    }

    @EventHandler
    public void on(ClientEnteredEvent event) {
        subscriptionHistoryRepository.save(new SubscriptionHistory(UUID.randomUUID(), event.getSubscriptionId(), Instant.now(), Action.ENTER));
    }

    @EventHandler
    public void on(ClientExitedEvent event) {
        subscriptionHistoryRepository.save(new SubscriptionHistory(UUID.randomUUID(), event.getSubscriptionId(), Instant.now(), Action.EXIT));
    }

    @QueryHandler
    public List<SubscriptionHistory> getFullHistory(FindAllSubscriptionsHistoryQuery query) {
        return subscriptionHistoryRepository.findAll();
    }

    @QueryHandler
    public String getAverageVisitDuration(GetAverageVisitDuration query) {
        return getAverageVisitDuration(subscriptionHistoryRepository.findAll());
    }

    @QueryHandler
    public String getVisitsByDayHistory(GetVisitsByDayQuery query) {
        return getVisitsByDay(subscriptionHistoryRepository.findAll());
    }

    public static String getAverageVisitDuration(List<SubscriptionHistory> histories) {
        histories.sort(Comparator.comparing(SubscriptionHistory::getTimestamp));
        double totalSum = 0.0;
        int k = 0;
        for (int i = 0; i < histories.size() - 1; i++) {
            SubscriptionHistory curHistory = histories.get(i);
            if (curHistory.getAction() == Action.ENTER) {
                for (int j = i + 1; j < histories.size(); j++) {
                    SubscriptionHistory endHistory = histories.get(j);
                    if (endHistory.getAction() == Action.EXIT && endHistory.getSubscriptionId().equals(curHistory.getSubscriptionId())) {
                        totalSum += endHistory.getTimestamp().getEpochSecond() - curHistory.getTimestamp().getEpochSecond();
                        k++;
                        break;
                    }
                }
            }
        }
        return k == 0 ? "Not enough data for average visit duration statistics" : String.format("Average visit duration is %4.2f seconds", (totalSum / (double) k));
    }

    public static String getVisitsByDay(List<SubscriptionHistory> histories) {
        Map<String, Integer> map = new HashMap<>();
        for (SubscriptionHistory sub : histories) {
            if (sub.getAction() == Action.ENTER) {
                String day = formatter.format(Date.from(sub.getTimestamp()));
                if (map.containsKey(day)) {
                    map.put(day, map.get(day) + 1);
                } else {
                    map.put(day, 1);
                }
            }
        }
        return map.entrySet().stream().map(entry -> String.format("%s were %d visits", entry.getKey(), entry.getValue())).collect(Collectors.joining("\n"));
    }
}
