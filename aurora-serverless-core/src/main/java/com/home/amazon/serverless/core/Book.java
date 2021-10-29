package com.home.amazon.serverless.core;

import com.google.gson.annotations.SerializedName;

public class Book {

    @SerializedName("id")
    private final int id;

    @SerializedName("author")
    private String author;

    @SerializedName("name")
    private String name;

    public Book(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
