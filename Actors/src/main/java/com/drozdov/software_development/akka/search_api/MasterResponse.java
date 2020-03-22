package com.drozdov.software_development.akka.search_api;

import java.util.List;

public class MasterResponse {
    private final List<String> items;
    private boolean isFailed = false;

    public MasterResponse(List<String> items) {
        this.items = items;
    }
    
    public void setFailed() {
        isFailed = true;
    }
    
    public boolean isFailed() {
        return isFailed;
    }

    public List<String> getItems() {
        return items;
    }
}
