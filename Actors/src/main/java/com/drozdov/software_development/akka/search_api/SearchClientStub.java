package com.drozdov.software_development.akka.search_api;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SearchClientStub implements SearchClient {
    private SearchAggregator searchAggregator;

    public SearchClientStub(SearchAggregator searchAggregator) {
        this.searchAggregator = searchAggregator;
    }

    public static String generateRandomWord() {
        Random random = new Random();
        char[] word = new char[random.nextInt(8) + 3];
        for (int j = 0; j < word.length; j++) {
            word[j] = (char) ('a' + random.nextInt(26));
        }
        return new String(word);
    }

    public SearchResponse searchTop5(String searchRequest) {
        return search(searchRequest, 5);
    }

    public SearchResponse search(String searchRequest, int topNumber) {
        List<String> curResponse = IntStream.range(0, topNumber)
                .mapToObj(v -> generateSearchResponse(searchRequest))
                .collect(Collectors.toList());
        return new SearchResponse(curResponse);
    }

    private String generateSearchResponse(String searchRequest) {
        //тестирование залипания
        if ("testTimeout".equals(searchRequest) && searchAggregator == SearchAggregator.BING) {
            try {
                Thread.sleep(6 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return String.format("%s response for '%s' is %s", searchAggregator, searchRequest, generateRandomWord());
    }
}
