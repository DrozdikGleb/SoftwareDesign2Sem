package com.drozdov.software_design.rxjava.db;


import com.drozdov.software_design.rxjava.model.Product;
import com.drozdov.software_design.rxjava.model.User;
import rx.Observable;

public interface ShopService {
    Observable<String> registerUser(User user);
    Observable<String> addProduct(Product product);
    Observable<User> getUsers();
    Observable<Product> getProductsByUserId(int userId);
}
