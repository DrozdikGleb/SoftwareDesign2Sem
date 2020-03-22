package com.drozdov.software_design.rxjava.db;

import com.drozdov.software_design.rxjava.model.Product;
import com.drozdov.software_design.rxjava.model.User;
import com.mongodb.rx.client.MongoClient;
import com.mongodb.rx.client.MongoClients;
import com.mongodb.rx.client.MongoCollection;
import com.mongodb.rx.client.MongoDatabase;
import org.bson.Document;
import rx.Observable;

public class ShopServiceImpl implements ShopService {
    private final static String PRODUCTS_COLLECTION_NAME = "products";
    private final static String USERS_COLLECTION_NAME = "users";
    private final MongoDatabase mongoDatabase;

    public ShopServiceImpl(MongoClient mongoClient) {
        this.mongoDatabase = mongoClient.getDatabase("shop");
    }
    
    public ShopServiceImpl() {
        this.mongoDatabase = MongoClients.create("mongodb://localhost:27017").getDatabase("shop");
    }

    private MongoCollection<Document> getProductCollection() {
        return mongoDatabase.getCollection(PRODUCTS_COLLECTION_NAME);
    }

    private MongoCollection<Document> getUsersCollection() {
        return mongoDatabase.getCollection(USERS_COLLECTION_NAME);
    }

    @Override
    public Observable<String> registerUser(User user) {
        return getUsersCollection()
                .insertOne(user.convertToMongoDocument())
                .asObservable()
                .isEmpty()
                .map(v -> !v ? "Successfully register user" : "Error occurred during user registration");
    }

    @Override
    public Observable<String> addProduct(Product product) {
        return getProductCollection()
                .insertOne(product.convertToMongoDocument())
                .asObservable()
                .isEmpty()
                .map(v -> !v ? "Successfully added product" : "Error occurred during adding product");
    }

    @Override
    public Observable<User> getUsers() {
        return getUsersCollection().find().toObservable().map(User::new);
    }

    @Override
    public Observable<Product> getProductsByUserId(int userId) {
        return getUsersCollection()
                .find()
                .toObservable()
                .filter(user -> user.getInteger("id") == userId)
                .map(it -> new User(it).currency)
                .map(this::getCurrencyMultiplier)
                .flatMap(multiplier -> getProductCollection()
                        .find()
                        .toObservable()
                        .map(it -> new Product(it).convertedCurrencyProduct(multiplier))
                );
    }

    private double getCurrencyMultiplier(String currency) {
        switch (currency) {
            case "RUB":
                return 80.0;
            case "EUR":
                return 0.93;
            case "USD":
                return 1.00;
        }
        return 1.00;
    }
}
