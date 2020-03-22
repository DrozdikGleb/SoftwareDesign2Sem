package com.drozdov.software_design.rxjava.model;

import org.bson.Document;

public class Product {
    public final int id;
    public final String name;
    public final double price;

    public Product(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Product(Document doc) {
        this(doc.getInteger("id"), doc.getString("name"), doc.getDouble("price"));
    }

    public Document convertToMongoDocument(){
        Document productDocument = new Document();
        productDocument.append("id", id)
                .append("name", name)
                .append("price", price);
        return productDocument;
    }
    
    public Product convertedCurrencyProduct(double multiplier) {
        return new Product(id, name, price * multiplier);
    } 

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                "}\n";
    }
}
