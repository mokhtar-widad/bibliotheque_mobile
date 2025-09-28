package com.example.biblio.models;

public class Book {
    private long id;
    private String title;
    private String author;
    private String isbn;
    private boolean available;
    private String description;
    private String coverUrl;
    private String borrowerName;
    private int borrowerId;
    private boolean favorite = false; // Par défaut, un livre n'est pas en favori

    public Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.available = true;
    }

    public Book(long id, String title, String author, String isbn, boolean available, String description, String coverUrl) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.available = available;
        this.description = description;
        this.coverUrl = coverUrl;
    }

    // Getters
    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public boolean isAvailable() {
        return available;
    }

    public String getDescription() {
        return description;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    /**
     * Récupère le nom de l'emprunteur actuel du livre
     * @return Le nom de l'emprunteur
     */
    public String getBorrowerName() {
        return borrowerName;
    }

    public int getBorrowerId() {
        return borrowerId;
    }

    // Setters
    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    public void setBorrowerId(int borrowerId) {
        this.borrowerId = borrowerId;
    }
    
    public boolean isFavorite() {
        return favorite;
    }
    
    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
} 