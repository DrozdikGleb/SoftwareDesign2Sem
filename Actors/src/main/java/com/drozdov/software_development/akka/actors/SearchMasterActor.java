package com.drozdov.software_development.akka.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.ReceiveTimeout;
import akka.actor.UntypedAbstractActor;
import com.drozdov.software_development.akka.search_api.MasterResponse;
import com.drozdov.software_development.akka.search_api.SearchAggregator;
import com.drozdov.software_development.akka.search_api.SearchClientStub;
import com.drozdov.software_development.akka.search_api.SearchResponse;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SearchMasterActor extends UntypedAbstractActor {
    private static int childId = 0;
    private final List<SearchResponse> searchResponses;
    private ActorRef reqSender;

    public SearchMasterActor() {
        super();
        this.searchResponses = new ArrayList<>();
        getContext().setReceiveTimeout(Duration.create(5, TimeUnit.SECONDS));
    }

    public void onReceive(Object message) {
        if (message instanceof String) {
            reqSender = getSender();
            String searchRequest = (String) message;
            Arrays.stream(SearchAggregator.values()).forEach(agr -> {
                ActorRef actorRef = getContext().actorOf(Props.create(SearchChildActor.class, new SearchClientStub(agr)), "child" + childId++);
                actorRef.tell(searchRequest, self());
            });
        } else if (message instanceof SearchResponse) {
            searchResponses.add((SearchResponse) message);
            if (searchResponses.size() == 3) {
                List<String> result = searchResponses.stream().flatMap(v -> v.getItems().stream()).collect(Collectors.toList());
                reqSender.tell(new MasterResponse(result), self());
                getContext().stop(self());
            }
        } else if (message instanceof ReceiveTimeout) {
            MasterResponse masterResponse = new MasterResponse(Collections.singletonList("Waiting for response from search aggregators more than 5 seconds"));
            masterResponse.setFailed();
            reqSender.tell(masterResponse, self());
            getContext().stop(self());
            //getContext().system().terminate();
        } else {
            unhandled(message);
        }
    }
}
