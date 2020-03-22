package com.drozdov.software_development.akka.search_api;

import java.util.List;

public class SearchResponse {
    private final List<String> items;

    public SearchResponse(List<String> items) {
        this.items = items;
    }

    public List<String> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "SearchResponse{" +
                "itemList=" + items +
                '}';
    }
}
