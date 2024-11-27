package com.example.e_library;

public class Book {
    private String title;
    private String author;
    private String image;
    private String description;
    private String publisher;
    private String year_release;

    public Book() {
        // Default constructor required for Firestore
    }

    public Book(String title, String author, String description, String publisher, String year_release, String image) {
        this.title = title;
        this.author = author;
        this.image = image;
        this.description = description;
        this.publisher = publisher;
        this.year_release = year_release;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getYearRelease() {
        return year_release;
    }
}
