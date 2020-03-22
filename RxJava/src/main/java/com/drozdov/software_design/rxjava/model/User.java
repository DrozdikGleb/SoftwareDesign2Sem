package com.drozdov.software_design.rxjava.model;

import org.bson.Document;

public class User {
    public final int id;
    public final String login;
    public final String currency;


    public User(Document doc) {
        this(doc.getInteger("id"), doc.getString("login"), doc.getString("currency"));
    }

    public User(int id, String login, String currency) {
        this.id = id;
        this.login = login;
        this.currency = currency;
    }
    
    public Document convertToMongoDocument(){
        Document userDocument = new Document();
        userDocument.append("id", id)
                .append("login", login)
                .append("currency", currency);
        return userDocument;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", currency='" + currency + '\'' +
                "}\n";
    }
}
