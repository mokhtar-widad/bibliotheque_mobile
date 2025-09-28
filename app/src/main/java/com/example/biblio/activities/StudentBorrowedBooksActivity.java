package com.example.biblio.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblio.R;
import com.example.biblio.ChoiceUserActivity;
import com.example.biblio.adapters.BookAdapter;
import com.example.biblio.database.DatabaseHelper;
import com.example.biblio.models.Book;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class StudentBorrowedBooksActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BookAdapter.OnBookActionListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private RecyclerView borrowedBooksRecyclerView;
    private TextView emptyView;
    private BookAdapter bookAdapter;
    private DatabaseHelper dbHelper;
    private String studentId;
    private TextInputEditText searchTitleInput;
    private TextInputEditText searchAuthorInput;
    private List<Book> allBorrowedBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_borrowed_books);

        // Récupérer l'ID de l'étudiant depuis l'intent
        studentId = getIntent().getStringExtra("student_id");
        if (studentId == null) {
            finish();
            return;
        }

        // Initialisation de la base de données
        dbHelper = new DatabaseHelper(this);

        // Configuration de la toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mes Emprunts");

        // Configuration du drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Initialisation des vues
        borrowedBooksRecyclerView = findViewById(R.id.borrowedBooksRecyclerView);
        emptyView = findViewById(R.id.emptyView);

        // Mise à jour de l'en-tête du navigation drawer avec les informations de l'étudiant
        View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {
            // Récupérer les informations de l'étudiant depuis la base de données
            String studentEmail = dbHelper.getStudentEmail(studentId);
            
            // Mettre à jour l'image, le nom et l'email de l'étudiant dans l'en-tête
            ImageView userIcon = headerView.findViewById(R.id.nav_header_icon);
            if (userIcon != null) {
                userIcon.setImageResource(R.drawable.ic_student);
            }
            
            TextView userTypeTextView = headerView.findViewById(R.id.nav_header_user_type);
            if (userTypeTextView != null) {
                userTypeTextView.setText("Étudiant");
            }
            
            TextView emailTextView = headerView.findViewById(R.id.nav_header_email);
            if (emailTextView != null && studentEmail != null) {
                emailTextView.setText(studentEmail);
            }
        }

        // Configuration du RecyclerView
        borrowedBooksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        borrowedBooksRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        bookAdapter = new BookAdapter(this);
        bookAdapter.setLibrarianMode(false);
        bookAdapter.setBorrowedMode(true); // Activer le mode "emprunts"
        borrowedBooksRecyclerView.setAdapter(bookAdapter);

        // Configuration de la recherche
        searchTitleInput = findViewById(R.id.searchTitleInput);
        searchAuthorInput = findViewById(R.id.searchAuthorInput);
        setupSearchListeners();

        // Marquer l'item actif dans le menu
        navigationView.setCheckedItem(R.id.nav_my_books);

        // Charger les livres empruntés
        loadBorrowedBooks();
    }

    private void setupSearchListeners() {
        TextWatcher searchWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBooks();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        searchTitleInput.addTextChangedListener(searchWatcher);
        searchAuthorInput.addTextChangedListener(searchWatcher);
    }

    private void filterBooks() {
        String titleQuery = searchTitleInput.getText().toString().toLowerCase();
        String authorQuery = searchAuthorInput.getText().toString().toLowerCase();

        List<Book> filteredBooks = new ArrayList<>();
        for (Book book : allBorrowedBooks) {
            boolean matchesTitle = book.getTitle().toLowerCase().contains(titleQuery);
            boolean matchesAuthor = book.getAuthor().toLowerCase().contains(authorQuery);
            if (matchesTitle && matchesAuthor) {
                filteredBooks.add(book);
            }
        }

        bookAdapter.setBooks(filteredBooks);
        updateEmptyView();
    }

    private void loadBorrowedBooks() {
        // Récupérer les livres empruntés par l'étudiant
        allBorrowedBooks = dbHelper.getBorrowedBooksByStudent(studentId);
        
        // Marquer les livres qui sont des favoris de l'étudiant
        for (Book book : allBorrowedBooks) {
            book.setFavorite(dbHelper.isFavoriteBook(studentId, book.getId()));
        }
        
        // Mettre à jour l'adaptateur avec les livres empruntés
        bookAdapter.setBooks(allBorrowedBooks);
        
        // Mettre à jour la vue vide
        updateEmptyView();
    }

    private void updateEmptyView() {
        if (bookAdapter.getItemCount() == 0) {
            borrowedBooksRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            borrowedBooksRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_available_books) {
            // Naviguer vers la page des livres disponibles
            Intent intent = new Intent(this, StudentHomeActivity.class);
            intent.putExtra("student_id", studentId);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_my_books) {
            // Déjà sur cette page
        } else if (id == R.id.nav_favorites) {
            // Navigation vers la page des favoris
            Intent intent = new Intent(this, StudentFavoriteBooksActivity.class);
            intent.putExtra("student_id", studentId);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_history) {
            // Navigation vers la page d'historique des emprunts
            Intent intent = new Intent(this, StudentBorrowHistoryActivity.class);
            intent.putExtra("student_id", studentId);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_logout) {
            // Déconnexion
            Intent intent = new Intent(this, ChoiceUserActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    @Override
    public void onBorrowClick(Book book) {
        // Remplacer cette méthode par onReturnClick pour restituer le livre
        returnBook(book);
    }

    // Nouvelle méthode pour restituer un livre
    private void returnBook(Book book) {
        // Restituer le livre à la bibliothèque
        boolean success = dbHelper.returnBook(book.getId(), studentId);
        
        if (success) {
            Snackbar.make(borrowedBooksRecyclerView, "Livre restitué avec succès : " + book.getTitle(), Snackbar.LENGTH_LONG)
                    .setBackgroundTint(getResources().getColor(R.color.success))
                    .setTextColor(getResources().getColor(R.color.white))
                    .show();
            
            // Recharger la liste des livres empruntés
            loadBorrowedBooks();
        } else {
            Snackbar.make(borrowedBooksRecyclerView, "Erreur lors de la restitution du livre", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(getResources().getColor(R.color.error))
                    .setTextColor(getResources().getColor(R.color.white))
                    .show();
        }
    }

    @Override
    public void onFavoriteClick(Book book) {
        boolean success;
        String message;
        
        // Vérifie si le livre est déjà un favori
        if (book.isFavorite()) {
            // Retirer le livre des favoris
            success = dbHelper.removeFavoriteBook(studentId, book.getId());
            message = success ? "Livre retiré des favoris : " + book.getTitle() : "Erreur lors du retrait des favoris";
            
            // Mettre à jour l'état du livre
            if (success) {
                book.setFavorite(false);
            }
        } else {
            // Ajouter le livre aux favoris
            success = dbHelper.addFavoriteBook(studentId, book.getId());
            message = success ? "Livre ajouté aux favoris : " + book.getTitle() : "Erreur lors de l'ajout aux favoris";
            
            // Mettre à jour l'état du livre
            if (success) {
                book.setFavorite(true);
            }
        }
        
        // Notifier l'adaptateur du changement pour mettre à jour l'apparence du bouton
        bookAdapter.notifyDataSetChanged();
        
        // Afficher le message approprié
        Snackbar.make(borrowedBooksRecyclerView, message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getResources().getColor(success ? R.color.success : R.color.error))
                .setTextColor(getResources().getColor(R.color.white))
                .show();
    }

    @Override
    public void onDeleteClick(Book book) {
        // Non utilisé dans cette activité
    }

    @Override
    public void onEditClick(Book book) {
        // Non utilisé dans cette activité
    }
}
