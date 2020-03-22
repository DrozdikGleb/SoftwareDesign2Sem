package com.drozdov.software_design.rxjava;

import com.drozdov.software_design.rxjava.http_server.RxNettyHttpServer;

public class Main {
    public static void main(String[] args) {
        RxNettyHttpServer httpServer = new RxNettyHttpServer();
        httpServer.startServer(8080);
    }
}