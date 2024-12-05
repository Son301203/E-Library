package com.example.e_library;

public class BorrowedBook {
    private String title;
    private String author;
    private String description;
    private String publisher;
    private String yearRelease;
    private String image;
    private long borrowedAt;

    // Default constructor required for Firestore
    public BorrowedBook() {}

    // Constructor
    public BorrowedBook(String title, String author, String description, String publisher, String yearRelease, String image, long borrowedAt) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.publisher = publisher;
        this.yearRelease = yearRelease;
        this.image = image;
        this.borrowedAt = borrowedAt;
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getYearRelease() {
        return yearRelease;
    }

    public void setYearRelease(String yearRelease) {
        this.yearRelease = yearRelease;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getBorrowedAt() {
        return borrowedAt;
    }

    public void setBorrowedAt(long borrowedAt) {
        this.borrowedAt = borrowedAt;
    }
}
