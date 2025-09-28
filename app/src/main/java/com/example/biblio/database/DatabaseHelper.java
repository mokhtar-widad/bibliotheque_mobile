package com.example.biblio.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.biblio.models.Book;
import com.example.biblio.models.BlacklistedStudent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "biblio.db";
    private static final int DATABASE_VERSION = 2;

    // Tables
    public static final String TABLE_BOOKS = "books";
    public static final String TABLE_USERS = "users";
    public static final String TABLE_BORROWINGS = "borrowings";
    public static final String TABLE_BLACKLIST = "blacklist";
    public static final String TABLE_FAVORITES = "favorites";

    // Colonnes communes
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CREATED_AT = "created_at";

    // Colonnes de la table books
    public static final String COLUMN_TITRE = "titre";
    public static final String COLUMN_AUTEUR = "auteur";
    public static final String COLUMN_ISBN = "isbn";
    public static final String COLUMN_DISPONIBLE = "disponible";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_COVER_URL = "cover_url";

    // Colonnes de la table users
    public static final String COLUMN_NOM = "nom";
    public static final String COLUMN_PRENOM = "prenom";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ROLE = "role";
    public static final String COLUMN_IS_BLACKLISTED = "is_blacklisted";
    public static final String COLUMN_BLACKLIST_REASON = "blacklist_reason";

    // Colonnes de la table borrowings
    public static final String COLUMN_BOOK_ID = "book_id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_DATE_EMPRUNT = "date_emprunt";
    public static final String COLUMN_DATE_RETOUR = "date_retour";
    public static final String COLUMN_RENDU = "rendu";

    // Colonnes de la table blacklist
    public static final String COLUMN_RAISON = "raison";
    public static final String COLUMN_DATE_FIN = "date_fin";

    // Création des tables
    private static final String CREATE_TABLE_BOOKS = "CREATE TABLE " + TABLE_BOOKS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_TITRE + " TEXT NOT NULL,"
            + COLUMN_AUTEUR + " TEXT NOT NULL,"
            + COLUMN_ISBN + " TEXT NOT NULL,"
            + COLUMN_DISPONIBLE + " INTEGER DEFAULT 1,"
            + COLUMN_DESCRIPTION + " TEXT,"
            + COLUMN_COVER_URL + " TEXT,"
            + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ")";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NOM + " TEXT NOT NULL,"
            + COLUMN_PRENOM + " TEXT NOT NULL,"
            + COLUMN_EMAIL + " TEXT UNIQUE NOT NULL,"
            + COLUMN_PASSWORD + " TEXT NOT NULL,"
            + COLUMN_ROLE + " TEXT NOT NULL,"
            + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ")";

    private static final String CREATE_TABLE_BORROWINGS = "CREATE TABLE " + TABLE_BORROWINGS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_BOOK_ID + " INTEGER NOT NULL,"
            + COLUMN_USER_ID + " INTEGER NOT NULL,"
            + COLUMN_DATE_EMPRUNT + " DATETIME NOT NULL,"
            + COLUMN_DATE_RETOUR + " DATETIME NOT NULL,"
            + COLUMN_RENDU + " INTEGER DEFAULT 0,"
            + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + "FOREIGN KEY(" + COLUMN_BOOK_ID + ") REFERENCES " + TABLE_BOOKS + "(" + COLUMN_ID + "),"
            + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")"
            + ")";

    private static final String CREATE_TABLE_BLACKLIST = "CREATE TABLE " + TABLE_BLACKLIST + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_ID + " INTEGER NOT NULL,"
            + COLUMN_RAISON + " TEXT NOT NULL,"
            + COLUMN_DATE_FIN + " DATETIME NOT NULL,"
            + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")"
            + ")";

    private static final String CREATE_TABLE_FAVORITES = "CREATE TABLE " + TABLE_FAVORITES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_ID + " INTEGER NOT NULL,"
            + COLUMN_BOOK_ID + " INTEGER NOT NULL,"
            + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + "UNIQUE (" + COLUMN_USER_ID + ", " + COLUMN_BOOK_ID + "),"
            + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "),"
            + "FOREIGN KEY(" + COLUMN_BOOK_ID + ") REFERENCES " + TABLE_BOOKS + "(" + COLUMN_ID + ")"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Création des tables
        db.execSQL(CREATE_TABLE_BOOKS);
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_BORROWINGS);
        db.execSQL(CREATE_TABLE_BLACKLIST);
        db.execSQL(CREATE_TABLE_FAVORITES);

        // Ajout d'un bibliothécaire par défaut
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOM, "Admin");
        values.put(COLUMN_PRENOM, "Admin");
        values.put(COLUMN_EMAIL, "admin@biblio.com");
        values.put(COLUMN_PASSWORD, "admin123");
        values.put(COLUMN_ROLE, "librarian");
        db.insert(TABLE_USERS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Ajouter les colonnes pour la liste noire
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_IS_BLACKLISTED + " INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_BLACKLIST_REASON + " TEXT");
        }
    }

    // Méthodes pour les livres
    public long addBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITRE, book.getTitle());
        values.put(COLUMN_AUTEUR, book.getAuthor());
        values.put(COLUMN_ISBN, book.getIsbn());
        values.put(COLUMN_DISPONIBLE, book.isAvailable() ? 1 : 0);
        values.put(COLUMN_DESCRIPTION, book.getDescription());
        values.put(COLUMN_COVER_URL, book.getCoverUrl());
        return db.insert(TABLE_BOOKS, null, values);
    }
    /**
     * Récupère tous les livres disponibles dans la base de données.
     * Un livre est considéré comme disponible si sa colonne COLUMN_DISPONIBLE est à 1.
     * Les livres sont triés par ordre alphabétique du titre.
     * 
     * @return Une liste de tous les livres disponibles
     */

    public List<Book> getAvailableBooks() {
        List<Book> books = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
            TABLE_BOOKS,
            null,
            COLUMN_DISPONIBLE + " = 1",
            null,
            null,
            null,
            COLUMN_TITRE + " ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                Book book = new Book(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITRE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTEUR)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ISBN)),
                    true,
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COVER_URL))
                );
                books.add(book);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return books;
    }
    /**
     * Récupère tous les livres actuellement empruntés dans la base de données.
     * Cette méthode effectue une jointure entre les tables books, borrowings et users
     * pour obtenir les détails des livres empruntés ainsi que les informations sur leurs emprunteurs.
     * Un livre est considéré comme emprunté si sa colonne COLUMN_RENDU dans la table borrowings est à 0.
     *
     * @return Une liste de tous les livres actuellement empruntés, avec pour chaque livre
     *         les informations de base (titre, auteur, etc.) et le nom complet de l'emprunteur
     */

    public List<Book> getBorrowedBooks() {
        List<Book> books = new ArrayList<>();
        String selectQuery = "SELECT b.*, u." + COLUMN_NOM + ", u." + COLUMN_PRENOM +
                " FROM " + TABLE_BOOKS + " b " +
                "INNER JOIN " + TABLE_BORROWINGS + " br ON b." + COLUMN_ID + " = br." + COLUMN_BOOK_ID + " " +
                "INNER JOIN " + TABLE_USERS + " u ON br." + COLUMN_USER_ID + " = u." + COLUMN_ID + " " +
                "WHERE br." + COLUMN_RENDU + " = 0";
                
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Book book = new Book(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITRE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTEUR)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ISBN)),
                    false,
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COVER_URL))
                );
                
                // Récupérer le nom et prénom de l'emprunteur
                String nom = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOM));
                String prenom = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRENOM));
                book.setBorrowerName(prenom + " " + nom);
                
                books.add(book);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return books;
    }

    public int updateBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITRE, book.getTitle());
        values.put(COLUMN_AUTEUR, book.getAuthor());
        values.put(COLUMN_ISBN, book.getIsbn());
        values.put(COLUMN_DISPONIBLE, book.isAvailable() ? 1 : 0);
        values.put(COLUMN_DESCRIPTION, book.getDescription());
        values.put(COLUMN_COVER_URL, book.getCoverUrl());
        return db.update(TABLE_BOOKS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(book.getId())});
    }

    public int deleteBook(long bookId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_BOOKS, COLUMN_ID + " = ?", new String[]{String.valueOf(bookId)});
    }

    // Méthodes pour les utilisateurs
    public long addUser(String nom, String prenom, String email, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOM, nom);
        values.put(COLUMN_PRENOM, prenom);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_ROLE, role);
        return db.insert(TABLE_USERS, null, values);
    }

    public String getLibrarianName() {
        String selectQuery = "SELECT " + COLUMN_NOM + ", " + COLUMN_PRENOM + " FROM " + TABLE_USERS
                + " WHERE " + COLUMN_ROLE + " = 'librarian' LIMIT 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        String name = "Bibliothécaire";
        if (cursor.moveToFirst()) {
            String nom = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOM));
            String prenom = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRENOM));
            name = prenom + " " + nom;
        }
        cursor.close();
        return name;
    }


    public boolean checkBibliothecaireCredentials(String email, String password) {
        String selectQuery = "SELECT * FROM " + TABLE_USERS
                + " WHERE " + COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ? AND " + COLUMN_ROLE + " = 'librarian'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public String getBibliothecaireName(String email) {
        String selectQuery = "SELECT " + COLUMN_NOM + ", " + COLUMN_PRENOM + " FROM " + TABLE_USERS
                + " WHERE " + COLUMN_EMAIL + " = ? AND " + COLUMN_ROLE + " = 'librarian'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{email});

        String name = "Bibliothécaire";
        if (cursor.moveToFirst()) {
            String nom = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOM));
            String prenom = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRENOM));
            name = prenom + " " + nom;
        }
        cursor.close();
        return name;
    }

    public boolean checkStudentCredentials(String email, String password) {
        String selectQuery = "SELECT * FROM " + TABLE_USERS
                + " WHERE " + COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ? AND " + COLUMN_ROLE + " = 'student'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public String getStudentName(String email) {
        String selectQuery = "SELECT " + COLUMN_NOM + ", " + COLUMN_PRENOM + " FROM " + TABLE_USERS
                + " WHERE " + COLUMN_EMAIL + " = ? AND " + COLUMN_ROLE + " = 'student'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{email});

        String name = "Étudiant";
        if (cursor.moveToFirst()) {
            String nom = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOM));
            String prenom = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRENOM));
            name = prenom + " " + nom;
        }
        cursor.close();
        return name;
    }

    public String getStudentId(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String id = null;
        
        Cursor cursor = db.query(
            TABLE_USERS,
            new String[]{COLUMN_ID},
            COLUMN_EMAIL + "=? AND " + COLUMN_ROLE + "=?",
            new String[]{email, "student"},
            null,
            null,
            null
        );
        
        if (cursor != null && cursor.moveToFirst()) {
            id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID));
        }
        
        if (cursor != null) {
            cursor.close();
        }
        
        return id;
    }

    // Méthodes pour la liste noire
    public List<BlacklistedStudent> getBlacklistedStudents() {
        List<BlacklistedStudent> students = new ArrayList<>();
        
        // Requête avec jointure pour récupérer les noms des étudiants
        String selectQuery = "SELECT b.*, u." + COLUMN_NOM + ", u." + COLUMN_PRENOM +
                " FROM " + TABLE_BLACKLIST + " b " +
                " INNER JOIN " + TABLE_USERS + " u ON b." + COLUMN_USER_ID + " = u." + COLUMN_ID;
                
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                // Récupération du nom complet de l'étudiant
                String nom = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOM));
                String prenom = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRENOM));
                String studentName = prenom + " " + nom;
                
                BlacklistedStudent student = new BlacklistedStudent(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                    studentName,
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RAISON)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_FIN)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT))
                );
                students.add(student);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return students;
    }

    public long addToBlacklist(int userId, String reason, String endDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_RAISON, reason);
        values.put(COLUMN_DATE_FIN, endDate);
        return db.insert(TABLE_BLACKLIST, null, values);
    }
    
    /**
     * Vérifie si un étudiant est sur la liste noire et si sa période d'exclusion est toujours active
     * @param studentId L'ID de l'étudiant à vérifier
     * @return Un objet Map contenant les informations sur le statut de liste noire:
     *         "isBlacklisted" (boolean) : si l'étudiant est sur la liste noire
     *         "reason" (String) : la raison de la mise sur liste noire (si applicable)
     *         "endDate" (String) : la date de fin de la période d'exclusion (si applicable)
     */
    public Map<String, Object> isStudentBlacklisted(String studentId) {
        Map<String, Object> result = new HashMap<>();
        result.put("isBlacklisted", false);
        
        if (studentId == null || studentId.isEmpty()) {
            return result;
        }
        
        SQLiteDatabase db = this.getReadableDatabase();
        
        // Convertir studentId en int si nécessaire
        int studentIdInt;
        try {
            studentIdInt = Integer.parseInt(studentId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return result; // Retourner non blacklisté si l'ID n'est pas valide
        }
        
        // Vérifier si l'étudiant est sur la liste noire et si sa période d'exclusion est toujours active
        String query = "SELECT " + COLUMN_ID + ", " + COLUMN_RAISON + ", " + COLUMN_DATE_FIN + 
                       " FROM " + TABLE_BLACKLIST + 
                       " WHERE " + COLUMN_USER_ID + " = ? " +
                       " AND date(" + COLUMN_DATE_FIN + ") >= date('now')";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentIdInt)});
        
        if (cursor != null && cursor.moveToFirst()) {
            // L'étudiant est sur la liste noire et sa période d'exclusion est active
            result.put("isBlacklisted", true);
            result.put("blacklistId", cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            result.put("reason", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RAISON)));
            result.put("endDate", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_FIN)));
            
            // Log pour le débogage
            android.util.Log.d("DatabaseHelper", "Étudiant " + studentId + " est sur liste noire jusqu'au " + 
                              cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_FIN)));
            
            cursor.close();
        } else if (cursor != null) {
            android.util.Log.d("DatabaseHelper", "Étudiant " + studentId + " n'est PAS sur liste noire");
            cursor.close();
        }
        
        return result;
    }

    public void removeFromBlacklist(int blacklistId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BLACKLIST, COLUMN_ID + " = ?", new String[]{String.valueOf(blacklistId)});
    }

    public boolean isUserBlacklisted(int userId) {
        String selectQuery = "SELECT * FROM " + TABLE_BLACKLIST
                + " WHERE " + COLUMN_USER_ID + " = ? AND " + COLUMN_DATE_FIN + " > datetime('now')";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(userId)});
        boolean isBlacklisted = cursor.getCount() > 0;
        cursor.close();
        return isBlacklisted;
    }

    public String getUserName(int userId) {
        String selectQuery = "SELECT " + COLUMN_NOM + ", " + COLUMN_PRENOM + " FROM " + TABLE_USERS
                + " WHERE " + COLUMN_ID + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(userId)});

        String name = "Utilisateur #" + userId;
        if (cursor != null && cursor.moveToFirst()) {
            String nom = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOM));
            String prenom = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRENOM));
            name = prenom + " " + nom;
        }
        cursor.close();
        return name;
    }

    public int getBorrowedBooksCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_BORROWINGS + " WHERE " + COLUMN_RENDU + " = 0", null);
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public int getTotalStudents() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USERS + " WHERE " + COLUMN_ROLE + " = 'student'", null);
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public int getBlacklistedStudentsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        // Modification de la requête pour compter tous les étudiants en liste noire
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_BLACKLIST, null);
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public Book getBookById(long bookId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Book book = null;

        Cursor cursor = db.query(
            "books",
            new String[]{COLUMN_ID, COLUMN_TITRE, COLUMN_AUTEUR, COLUMN_ISBN, COLUMN_DESCRIPTION, COLUMN_DISPONIBLE},
            "id = ?",
            new String[]{String.valueOf(bookId)},
            null,
            null,
            null
        );

        if (cursor != null && cursor.moveToFirst()) {
            book = new Book(0, "", "", "", true, "", "");
            book.setId(cursor.getLong(0));
            book.setTitle(cursor.getString(1));
            book.setAuthor(cursor.getString(2));
            book.setIsbn(cursor.getString(3));
            book.setDescription(cursor.getString(4));
            book.setAvailable(cursor.getInt(5) == 1);
        }
        cursor.close();
        return book;
    }

    public int getTotalBooks() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_BOOKS, null);
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public int getAvailableBooksCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_BOOKS + " WHERE " + COLUMN_DISPONIBLE + " = 1", null);
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public boolean borrowBook(long bookId, String studentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // Log pour le débogage
        android.util.Log.d("DatabaseHelper", "Tentative d'emprunt - bookId=" + bookId + ", studentId=" + studentId);
        
        // Validation de l'ID étudiant
        if (studentId == null || studentId.isEmpty()) {
            android.util.Log.e("DatabaseHelper", "Emprunt impossible: studentId null ou vide");
            return false;
        }
        
        // S'assurer que l'ID étudiant est bien un entier pour la cohérence avec la base de données
        int studentIdInt;
        try {
            studentIdInt = Integer.parseInt(studentId);
        } catch (NumberFormatException e) {
            android.util.Log.e("DatabaseHelper", "Emprunt impossible: format d'ID invalide: " + studentId, e);
            return false; // Erreur de conversion, impossible d'emprunter
        }
        
        // Vérification directe de la liste noire avec une requête SQL
        String blacklistQuery = "SELECT COUNT(*) FROM " + TABLE_BLACKLIST + 
                                " WHERE " + COLUMN_USER_ID + " = ? " +
                                " AND date(" + COLUMN_DATE_FIN + ") >= date('now')";
        
        Cursor blacklistCursor = db.rawQuery(blacklistQuery, new String[]{String.valueOf(studentIdInt)});
        boolean isBlacklisted = false;
        
        if (blacklistCursor != null && blacklistCursor.moveToFirst()) {
            isBlacklisted = blacklistCursor.getInt(0) > 0;
            blacklistCursor.close();
        }
        
        if (isBlacklisted) {
            android.util.Log.w("DatabaseHelper", "Emprunt refusé: étudiant " + studentId + " est sur liste noire");
            return false;
        }
        
        // Démarrer une transaction pour garantir la cohérence
        db.beginTransaction();
        try {
            // 1. Ajouter l'emprunt dans la table des emprunts
            ContentValues borrowValues = new ContentValues();
            borrowValues.put(COLUMN_BOOK_ID, bookId);
            borrowValues.put(COLUMN_USER_ID, studentIdInt); // Utiliser l'ID converti en entier
            borrowValues.put(COLUMN_RENDU, 0); // 0 signifie que le livre n'est pas encore rendu
            
            // Calculer les dates d'emprunt et de retour
            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();
            calendar.add(Calendar.DAY_OF_MONTH, 14); // 14 jours (2 semaines) pour la période d'emprunt
            Date returnDate = calendar.getTime();
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            borrowValues.put(COLUMN_DATE_EMPRUNT, dateFormat.format(today));
            borrowValues.put(COLUMN_DATE_RETOUR, dateFormat.format(returnDate));
            
            long borrowResult = db.insert(TABLE_BORROWINGS, null, borrowValues);
            
            // 2. Mettre à jour la disponibilité du livre
            ContentValues bookValues = new ContentValues();
            bookValues.put(COLUMN_DISPONIBLE, 0); // Non disponible
            
            int bookUpdateResult = db.update(TABLE_BOOKS, bookValues, 
                                        COLUMN_ID + " = ?", 
                                        new String[]{String.valueOf(bookId)});
            
            // Valider la transaction si les deux opérations ont réussi
            if (borrowResult != -1 && bookUpdateResult > 0) {
                db.setTransactionSuccessful();
                return true;
            } else {
                return false;
            }
        } finally {
            db.endTransaction();
        }
    }
    
    // Méthode pour obtenir la date actuelle au format "yyyy-MM-dd"
    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date());
    }
    
    // Méthode pour obtenir la date de retour (date actuelle + x jours)
    private String getReturnDate(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, days);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    public boolean addFavoriteBook(String studentId, long bookId) {
        // Vérifier d'abord si le livre est déjà dans les favoris
        if (isFavoriteBook(studentId, bookId)) {
            // Le livre est déjà dans les favoris, on considère que c'est un succès
            return true;
        }
        
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, studentId);
        values.put(COLUMN_BOOK_ID, bookId);
        long result = db.insert(TABLE_FAVORITES, null, values);
        return result != -1;
    }

    public List<Book> getFavoriteBooks(String studentId) {
        List<Book> favoriteBooks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT b.* FROM " + TABLE_BOOKS + " b " +
                "INNER JOIN " + TABLE_FAVORITES + " f ON b." + COLUMN_ID + " = f." + COLUMN_BOOK_ID + " " +
                "WHERE f." + COLUMN_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{studentId});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITRE));
                String author = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTEUR));
                String isbn = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ISBN));
                int isAvailable = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DISPONIBLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                String coverUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COVER_URL));

                Book book = new Book(id, title, author, isbn, isAvailable == 1, description, coverUrl);
                
                // Marquer le livre comme favori
                book.setFavorite(true);
                
                // Si le livre est emprunté, récupérer le nom de l'emprunteur
                if (!book.isAvailable()) {
                    String borrowerQuery = "SELECT u." + COLUMN_NOM + ", u." + COLUMN_PRENOM + 
                           " FROM " + TABLE_USERS + " u" +
                           " JOIN " + TABLE_BORROWINGS + " br ON u." + COLUMN_ID + " = br." + COLUMN_USER_ID +
                           " WHERE br." + COLUMN_BOOK_ID + " = ? AND br." + COLUMN_RENDU + " = 0";
                    
                    Cursor borrowerCursor = db.rawQuery(borrowerQuery, new String[]{String.valueOf(book.getId())});
                    
                    if (borrowerCursor != null && borrowerCursor.moveToFirst()) {
                        String nom = borrowerCursor.getString(borrowerCursor.getColumnIndexOrThrow(COLUMN_NOM));
                        String prenom = borrowerCursor.getString(borrowerCursor.getColumnIndexOrThrow(COLUMN_PRENOM));
                        book.setBorrowerName(prenom + " " + nom);
                        borrowerCursor.close();
                    }
                }
                
                favoriteBooks.add(book);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return favoriteBooks;
    }

    public boolean isFavoriteBook(String studentId, long bookId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_FAVORITES + 
                " WHERE " + COLUMN_USER_ID + " = ? AND " + COLUMN_BOOK_ID + " = ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{studentId, String.valueOf(bookId)});
        boolean isFavorite = cursor.getCount() > 0;
        cursor.close();
        return isFavorite;
    }

    public boolean removeFavoriteBook(String studentId, long bookId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_FAVORITES, 
                COLUMN_USER_ID + " = ? AND " + COLUMN_BOOK_ID + " = ?", 
                new String[]{studentId, String.valueOf(bookId)}) > 0;
    }

    // Méthode pour restituer un livre emprunté
    public boolean returnBook(long bookId, String studentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // Démarrer une transaction pour garantir la cohérence
        db.beginTransaction();
        try {
            // 1. Mettre à jour l'emprunt comme rendu
            ContentValues borrowValues = new ContentValues();
            borrowValues.put(COLUMN_RENDU, 1); // 1 = rendu
            
            int borrowUpdateResult = db.update(TABLE_BORROWINGS, 
                                             borrowValues, 
                                             COLUMN_BOOK_ID + " = ? AND " + COLUMN_USER_ID + " = ? AND " + COLUMN_RENDU + " = 0", 
                                             new String[]{String.valueOf(bookId), studentId});
            
            // 2. Remettre le livre comme disponible
            ContentValues bookValues = new ContentValues();
            bookValues.put(COLUMN_DISPONIBLE, 1); // 1 = disponible
            
            int bookUpdateResult = db.update(TABLE_BOOKS, 
                                           bookValues, 
                                           COLUMN_ID + " = ?", 
                                           new String[]{String.valueOf(bookId)});
            
            // Valider la transaction si les deux opérations ont réussi
            if (borrowUpdateResult > 0 && bookUpdateResult > 0) {
                db.setTransactionSuccessful();
                return true;
            } else {
                return false;
            }
        } finally {
            db.endTransaction();
        }
    }

    // Méthode pour récupérer l'email d'un étudiant à partir de son ID
    public String getStudentEmail(String studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String email = null;
        
        Cursor cursor = db.query(
            TABLE_USERS,
            new String[]{COLUMN_EMAIL},
            COLUMN_ID + " = ? AND " + COLUMN_ROLE + " = ?",
            new String[]{studentId, "student"},
            null,
            null,
            null
        );
        
        if (cursor != null && cursor.moveToFirst()) {
            email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
            cursor.close();
        }
        
        return email;
    }
    
    // Méthode pour récupérer tous les étudiants
    public List<Map<String, Object>> getAllStudents() {
        List<Map<String, Object>> students = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        // Requête pour récupérer tous les étudiants qui ne sont pas du tout dans la liste noire
        String query = "SELECT u." + COLUMN_ID + ", u." + COLUMN_NOM + ", u." + COLUMN_PRENOM +
                       " FROM " + TABLE_USERS + " u" +
                       " WHERE u." + COLUMN_ROLE + " = 'student'" +
                       " AND u." + COLUMN_ID + " NOT IN (SELECT " + COLUMN_USER_ID + 
                       " FROM " + TABLE_BLACKLIST + ")";
        
        Cursor cursor = db.rawQuery(query, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Map<String, Object> student = new HashMap<>();
                student.put("id", cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                student.put("nom", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOM)));
                student.put("prenom", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRENOM)));
                student.put("fullName", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRENOM)) + 
                                      " " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOM)));
                students.add(student);
            } while (cursor.moveToNext());
        }
        
        if (cursor != null) {
            cursor.close();
        }
        
        return students;
    }


    
    // Méthode pour récupérer les livres empruntés par un étudiant
    public List<Book> getBorrowedBooksByStudent(String studentId) {
        List<Book> borrowedBooks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        // Requête pour récupérer les livres empruntés par cet étudiant
        String query = "SELECT b.*, u." + COLUMN_NOM + ", u." + COLUMN_PRENOM + 
                       " FROM " + TABLE_BOOKS + " b" +
                       " JOIN " + TABLE_BORROWINGS + " br ON b." + COLUMN_ID + " = br." + COLUMN_BOOK_ID +
                       " JOIN " + TABLE_USERS + " u ON br." + COLUMN_USER_ID + " = u." + COLUMN_ID +
                       " WHERE br." + COLUMN_USER_ID + " = ? AND br." + COLUMN_RENDU + " = 0";
        
        Cursor cursor = db.rawQuery(query, new String[]{studentId});
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Book book = new Book(
                    cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITRE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTEUR)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ISBN)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DISPONIBLE)) == 1,
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COVER_URL))
                );
                
                // Ajouter le nom de l'emprunteur
                String nom = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOM));
                String prenom = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRENOM));
                book.setBorrowerName(prenom + " " + nom);
                
                borrowedBooks.add(book);
            } while (cursor.moveToNext());
        }
        
        if (cursor != null) {
            cursor.close();
        }
        
        return borrowedBooks;
    }
    
    // Méthode pour récupérer l'historique complet des emprunts d'un étudiant (livres empruntés et rendus)
    public List<Map<String, Object>> getBorrowingHistory(String studentId) {
        List<Map<String, Object>> historyItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        // Requête pour récupérer tous les emprunts (rendus et en cours) avec les détails des livres
        String query = "SELECT b." + COLUMN_ID + " AS book_id, b." + COLUMN_TITRE + ", b." + COLUMN_AUTEUR + 
                       ", b." + COLUMN_ISBN + ", br." + COLUMN_RENDU + ", br." + COLUMN_DATE_EMPRUNT + 
                       ", br." + COLUMN_DATE_RETOUR + ", br." + COLUMN_CREATED_AT + 
                       " FROM " + TABLE_BORROWINGS + " br " +
                       " JOIN " + TABLE_BOOKS + " b ON br." + COLUMN_BOOK_ID + " = b." + COLUMN_ID +
                       " WHERE br." + COLUMN_USER_ID + " = ?" +
                       " ORDER BY br." + COLUMN_CREATED_AT + " DESC";  // Les plus récents d'abord
        
        Cursor cursor = db.rawQuery(query, new String[]{studentId});
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Map<String, Object> item = new HashMap<>();
                
                // Détails du livre
                item.put("bookId", cursor.getLong(cursor.getColumnIndexOrThrow("book_id")));
                item.put("title", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITRE)));
                item.put("author", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTEUR)));
                item.put("isbn", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ISBN)));
                
                // Détails de l'emprunt
                item.put("isReturned", cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RENDU)) == 1);
                item.put("borrowDate", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_EMPRUNT)));
                item.put("returnDeadline", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_RETOUR)));
                
                // Calcul du statut de l'emprunt
                boolean isReturned = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RENDU)) == 1;
                String status = isReturned ? "Rendu" : "En cours";
                
                // Vérifier si la date limite est dépassée pour les emprunts en cours
                if (!isReturned) {
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        Date today = new Date();
                        Date returnDate = dateFormat.parse(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_RETOUR)));
                        
                        if (returnDate != null && today.after(returnDate)) {
                            status = "En retard";
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                
                item.put("status", status);
                
                historyItems.add(item);
            } while (cursor.moveToNext());
        }
        
        if (cursor != null) {
            cursor.close();
        }
        
        return historyItems;
    }
}