package com.zilpher.webdev2;

public class Book {
    private Long id;
    private String name;
    private Double price;
    private Category category;

    public Book() {}

    public Book(Long id, String name, Double price, Category category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public Long getId() {
        return id;
    }public String getName() {
        return name;
    }public Double getPrice() {
        return price;
    }public Category getCategory() {
        return category;
    }

    public void setId(Long id) {
        this.id = id;
    }public void setName(String name) {
        this.name = name;
    }public void setPrice(Double price) {
        this.price = price;
    }public void setCategory(Category category) {
        this.category = category;
    }
}