package com.home.amazon.serverless.core;

import com.google.gson.annotations.SerializedName;

public class Book {

    @SerializedName("id")
    private final Long id;

    @SerializedName("author")
    private String author;

    @SerializedName("name")
    private String name;

    public Book(Long id) {
        this.id = id;
    }

    public Long getId() {
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
