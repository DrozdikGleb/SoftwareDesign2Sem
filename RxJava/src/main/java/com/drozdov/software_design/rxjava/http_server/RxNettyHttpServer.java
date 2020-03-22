package com.drozdov.software_design.rxjava.http_server;

import com.drozdov.software_design.rxjava.db.ShopService;
import com.drozdov.software_design.rxjava.db.ShopServiceImpl;
import com.drozdov.software_design.rxjava.model.Product;
import com.drozdov.software_design.rxjava.model.User;
import com.mongodb.rx.client.MongoClients;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServer;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import rx.Observable;

import java.util.List;
import java.util.Map;

public class RxNettyHttpServer {

    private final ShopService shopService;

    public RxNettyHttpServer() {
        shopService = new ShopServiceImpl(MongoClients.create("mongodb://localhost:27017"));
    }

    public RxNettyHttpServer(ShopService shopService) {
        this.shopService = shopService;
    }

    public void startServer(int port) {
        HttpServer
                .newServer(port)
                .start((req, resp) -> {
                    Observable<String> response = chooseOption(req);
                    return resp.writeString(response);
                })
                .awaitShutdown();
    }

    private Observable<String> chooseOption(HttpServerRequest<ByteBuf> request) {
        String path = request.getDecodedPath().substring(1);
        Map<String, List<String>> queryParams = request.getQueryParameters();
        switch (path) {
            case "users":
                return getUsers();
            case "products":
                return getProductsByUserId(queryParams);
            case "add_product":
                return addProduct(queryParams);
            case "add_user":
                return addUser(queryParams);
            default:
                return Observable.just("Unknown path - " + path);
        }
    }

    private Observable<String> getUsers() {
        return shopService.getUsers().map(User::toString);
    }

    private Observable<String> getProductsByUserId(Map<String, List<String>> queryParams) {
        String validationResult = validateParameters(queryParams, "id");
        if (validationResult != null) {
            return Observable.just(validationResult);
        }
        int u_id = Integer.parseInt(queryParams.get("id").get(0));
        return shopService.getProductsByUserId(u_id).map(Product::toString);
    }

    private Observable<String> addUser(Map<String, List<String>> queryParams) {
        String validationResult = validateParameters(queryParams, "id", "login", "currency");
        if (validationResult != null) {
            return Observable.just(validationResult);
        }
        int u_id = Integer.parseInt(queryParams.get("id").get(0));
        String login = queryParams.get("login").get(0);
        String currency = queryParams.get("currency").get(0);
        return shopService.registerUser(new User(u_id, login, currency)).map(String::valueOf);
    }

    private Observable<String> addProduct(Map<String, List<String>> queryParams) {
        String validationResult = validateParameters(queryParams, "id", "name", "price");
        if (validationResult != null) {
            return Observable.just(validationResult);
        }
        int product_id = Integer.parseInt(queryParams.get("id").get(0));
        String name = queryParams.get("name").get(0);
        double price = Double.parseDouble(queryParams.get("price").get(0));
        return shopService.addProduct(new Product(product_id, name, price)).map(String::valueOf);
    }

    private String validateParameters(Map<String, List<String>> queryParams, String... requiredParameters) {
        for (String requiredParameter : requiredParameters) {
            if (!queryParams.containsKey(requiredParameter)) {
                return "Missed required parameter - " + requiredParameter;
            }
        }
        return null;
    }
}
