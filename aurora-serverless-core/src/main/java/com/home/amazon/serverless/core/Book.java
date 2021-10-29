package com.home.amazon.serverless.core;

public class Book {

    private static final String JSON_TEMPLATE = "{\"id\":%d,\"name\":\"%s\",\"author\":\"%s\"}";

    private final Long id;
    private String author;
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

    @Override
    public String toString() {
        return String.format(JSON_TEMPLATE, id, name, author);
    }
}
