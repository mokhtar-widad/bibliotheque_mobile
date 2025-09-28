package com.example.biblio.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.biblio.R;
import com.example.biblio.adapters.BookAdapter;
import com.example.biblio.database.DatabaseHelper;
import com.example.biblio.models.Book;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class AvailableBooksActivity extends AppCompatActivity implements BookAdapter.OnBookActionListener {
    private RecyclerView booksRecyclerView;
    private TextView emptyView;
    private BookAdapter bookAdapter;
    private DatabaseHelper dbHelper;
    private List<Book> allBooks;
    private TextInputEditText searchTitleInput;
    private TextInputEditText searchAuthorInput;
    private View rootView;
    private static final int EDIT_BOOK_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_books);

        // Configuration de la toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Livres disponibles");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialisation de la base de données
        dbHelper = new DatabaseHelper(this);

        // Initialisation des vues
        rootView = findViewById(android.R.id.content).getRootView();
        booksRecyclerView = findViewById(R.id.booksRecyclerView);
        emptyView = findViewById(R.id.emptyView);
        searchTitleInput = findViewById(R.id.searchTitleInput);
        searchAuthorInput = findViewById(R.id.searchAuthorInput);

        // Configuration du RecyclerView
        booksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        allBooks = new ArrayList<>();
        bookAdapter = new BookAdapter(this);
        bookAdapter.setLibrarianMode(true);
        booksRecyclerView.setAdapter(bookAdapter);

        // Configuration de la recherche
        setupSearchListeners();

        // Chargement des livres
        loadBooks();
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
        for (Book book : allBooks) {
            boolean matchesTitle = book.getTitle().toLowerCase().contains(titleQuery);
            boolean matchesAuthor = book.getAuthor().toLowerCase().contains(authorQuery);
            if (matchesTitle && matchesAuthor) {
                filteredBooks.add(book);
            }
        }

        bookAdapter.setBooks(filteredBooks);
        updateEmptyView();
    }

    private void loadBooks() {
        // Récupérer tous les livres de la base de données
        allBooks = dbHelper.getAvailableBooks();
        
        bookAdapter.setBooks(allBooks);
        updateEmptyView();
    }

    private void updateEmptyView() {
        if (bookAdapter.getItemCount() == 0) {
            booksRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            booksRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }



    @Override
    public void onBorrowClick(Book book) {
        // TODO: Implémenter l'emprunt du livre
        Snackbar.make(rootView, "Emprunter le livre : " + book.getTitle(), Snackbar.LENGTH_LONG)
                .setBackgroundTint(getResources().getColor(R.color.primary))
                .setTextColor(getResources().getColor(R.color.white))
                .show();
    }

    @Override
    public void onFavoriteClick(Book book) {
        // TODO: Implémenter l'ajout aux favoris
        Snackbar.make(rootView, "Ajouter aux favoris : " + book.getTitle(), Snackbar.LENGTH_LONG)
                .setBackgroundTint(getResources().getColor(R.color.secondary))
                .setTextColor(getResources().getColor(R.color.white))
                .show();
    }

    @Override
    public void onDeleteClick(Book book) {
        // Créer une instance de dialogue personnalisé
        Dialog deleteDialog = new Dialog(this);
        deleteDialog.setContentView(R.layout.dialog_confirm_delete);
        
        // Configurer la fenêtre de dialogue
        Window window = deleteDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        
        // Configurer le message avec le titre du livre
        TextView messageText = deleteDialog.findViewById(R.id.dialog_message);
        messageText.setText("Êtes-vous sûr de vouloir supprimer \"" + book.getTitle() + "\" ?");
        
        // Configurer les boutons
        Button cancelButton = deleteDialog.findViewById(R.id.btn_cancel);
        Button deleteButton = deleteDialog.findViewById(R.id.btn_delete);
        
        // Configurer l'action d'annulation
        cancelButton.setOnClickListener(v -> deleteDialog.dismiss());
        
        // Configurer l'action de suppression
        deleteButton.setOnClickListener(v -> {
            // Supprimer le livre de la base de données
            int result = dbHelper.deleteBook(book.getId());
            if (result > 0) {
                // Supprimer le livre de la liste et mettre à jour l'affichage
                allBooks.remove(book);
                bookAdapter.setBooks(allBooks);
                updateEmptyView();
                
                // Afficher un message de succès
                Snackbar.make(rootView, "Livre supprimé avec succès", Snackbar.LENGTH_LONG)
                        .setBackgroundTint(getResources().getColor(R.color.success))
                        .setTextColor(getResources().getColor(R.color.white))
                        .show();
            } else {
                // Afficher un message d'erreur
                Snackbar.make(rootView, "Erreur lors de la suppression du livre", Snackbar.LENGTH_LONG)
                        .setBackgroundTint(getResources().getColor(R.color.error))
                        .setTextColor(getResources().getColor(R.color.white))
                        .show();
            }
            
            // Fermer le dialogue
            deleteDialog.dismiss();
        });
        
        // Afficher le dialogue
        deleteDialog.show();
    }

    @Override
    public void onEditClick(Book book) {
        // Lancer l'activité d'édition de livre
        editBook(book);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBooks(); // Recharger les livres quand on revient sur l'activité
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void editBook(Book book) {
        Intent intent = new Intent(this, EditBookActivity.class);
        intent.putExtra("book_id", book.getId());
        startActivityForResult(intent, EDIT_BOOK_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_BOOK_REQUEST && resultCode == RESULT_OK) {
            // Recharger la liste des livres après une modification
            loadBooks();
        }
    }
} 