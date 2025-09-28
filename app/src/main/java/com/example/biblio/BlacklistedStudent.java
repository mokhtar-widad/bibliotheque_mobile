package com.example.biblio;

public class BlacklistedStudent {
    private int id;
    private String name;
    private String email;
    private String studentNumber;
    private String reason;

    public BlacklistedStudent(int id, String name, String email, String studentNumber, String reason) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.studentNumber = studentNumber;
        this.reason = reason;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public String getReason() {
        return reason;
    }
} 