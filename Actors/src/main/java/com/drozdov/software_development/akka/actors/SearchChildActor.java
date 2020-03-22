package com.drozdov.software_development.akka.actors;

import akka.actor.UntypedAbstractActor;
import com.drozdov.software_development.akka.search_api.SearchClient;

public class SearchChildActor extends UntypedAbstractActor {
    private final SearchClient searchClient;

    public SearchChildActor(SearchClient searchClient) {
        this.searchClient = searchClient;
    }

    public void onReceive(Object message) {
        if (message instanceof String) {
            sender().tell(searchClient.searchTop5((String) message), self());
        }
    }
}
