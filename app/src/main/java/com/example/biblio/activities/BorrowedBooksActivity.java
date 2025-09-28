package com.example.biblio.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblio.R;
import com.example.biblio.adapters.BookAdapter;
import com.example.biblio.database.DatabaseHelper;
import com.example.biblio.models.Book;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class BorrowedBooksActivity extends AppCompatActivity implements BookAdapter.OnBookActionListener {
    private RecyclerView booksRecyclerView;
    private TextView emptyView;
    private BookAdapter bookAdapter;
    private TextInputEditText searchTitleInput;
    private TextInputEditText searchAuthorInput;
    private List<Book> allBooks;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrowed_books);

        // Configuration de la toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Livres empruntés");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialisation de la base de données
        dbHelper = new DatabaseHelper(this);

        // Initialisation des vues
        booksRecyclerView = findViewById(R.id.booksRecyclerView);
        emptyView = findViewById(R.id.emptyView);
        searchTitleInput = findViewById(R.id.searchTitleInput);
        searchAuthorInput = findViewById(R.id.searchAuthorInput);

        // Configuration du RecyclerView
        booksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookAdapter = new BookAdapter(this);
        bookAdapter.setLibrarianMode(true);
        bookAdapter.setLibrarianBorrowedMode(true); // Activer le mode bibliothécaire emprunt
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
        allBooks = dbHelper.getBorrowedBooks();
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
        // TODO: Implémenter le retour du livre
        // Pour l'instant, on affiche un message
        Snackbar.make(booksRecyclerView, "Retour du livre : " + book.getTitle(), Snackbar.LENGTH_LONG)
                .setBackgroundTint(getResources().getColor(R.color.success))
                .setTextColor(getResources().getColor(R.color.white))
                .show();
    }

    @Override
    public void onFavoriteClick(Book book) {
        // TODO: Implémenter l'ajout aux favoris
        Snackbar.make(booksRecyclerView, "Ajouter aux favoris : " + book.getTitle(), Snackbar.LENGTH_LONG)
                .setBackgroundTint(getResources().getColor(R.color.secondary))
                .setTextColor(getResources().getColor(R.color.white))
                .show();
    }

    @Override
    public void onDeleteClick(Book book) {
        // TODO: Implémenter la suppression du livre
        Snackbar.make(booksRecyclerView, "Supprimer le livre : " + book.getTitle(), Snackbar.LENGTH_LONG)
                .setBackgroundTint(getResources().getColor(R.color.error))
                .setTextColor(getResources().getColor(R.color.white))
                .show();
    }

    @Override
    public void onEditClick(Book book) {
        // TODO: Implémenter l'édition du livre
        Snackbar.make(booksRecyclerView, "Éditer le livre : " + book.getTitle(), Snackbar.LENGTH_LONG)
                .setBackgroundTint(getResources().getColor(R.color.info))
                .setTextColor(getResources().getColor(R.color.white))
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 