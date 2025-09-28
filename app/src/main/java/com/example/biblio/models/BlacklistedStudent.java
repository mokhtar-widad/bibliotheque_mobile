package com.example.biblio.models;

/**
 * Classe représentant un étudiant mis sur liste noire
 * Contient les informations sur l'étudiant blacklisté et la raison de sa mise en liste noire
 */
public class BlacklistedStudent {
    private int id;
    private int userId;
    private String studentName; 
    private String reason;
    private String endDate;
    private String createdAt;

    /**
     * Constructeur de la classe BlacklistedStudent
     * Initialise toutes les propriétés d'un étudiant blacklisté
     * @param id Identifiant unique de l'entrée dans la liste noire
     * @param userId Identifiant de l'étudiant
     * @param studentName Nom complet de l'étudiant
     * @param reason Raison de la mise en liste noire
     * @param endDate Date de fin de la période de liste noire
     * @param createdAt Date de création de l'entrée
     */
    public BlacklistedStudent(int id, int userId, String studentName, String reason, String endDate, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.studentName = studentName;
        this.reason = reason;
        this.endDate = endDate;
        this.createdAt = createdAt;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }
    
    public String getStudentName() {
        return studentName;
    }

    public String getReason() {
        return reason;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}