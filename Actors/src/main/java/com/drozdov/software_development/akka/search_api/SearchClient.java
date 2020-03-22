package com.drozdov.software_development.akka.search_api;

public interface SearchClient {
    SearchResponse searchTop5(String searchRequest);
    SearchResponse search(String searchRequest, int topNumber);
}
