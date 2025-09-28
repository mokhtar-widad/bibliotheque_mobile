package com.example.biblio.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.biblio.R;
import com.example.biblio.database.DatabaseHelper;
import com.example.biblio.models.Book;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class EditBookActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextInputEditText titleInput, authorInput, isbnInput, descriptionInput;
    private MaterialButton saveButton;
    private long bookId;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);
        rootView = findViewById(R.id.editBookLayout);

        // Initialiser la base de données
        dbHelper = new DatabaseHelper(this);

        // Configurer la toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Modifier le livre");

        // Initialiser les vues
        titleInput = findViewById(R.id.titleInput);
        authorInput = findViewById(R.id.authorInput);
        isbnInput = findViewById(R.id.isbnInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        saveButton = findViewById(R.id.saveButton);

        // Récupérer l'ID du livre depuis l'intent
        bookId = getIntent().getLongExtra("book_id", -1);
        if (bookId == -1) {
            Toast.makeText(this, "Erreur: ID du livre non trouvé", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Charger les détails du livre
        loadBookDetails();

        // Configurer le bouton de sauvegarde
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBookChanges();
            }
        });
    }

    private void loadBookDetails() {
        Book book = dbHelper.getBookById(bookId);
        if (book != null) {
            titleInput.setText(book.getTitle());
            authorInput.setText(book.getAuthor());
            isbnInput.setText(book.getIsbn());
            descriptionInput.setText(book.getDescription());
        }
    }

    private void saveBookChanges() {
        String title = titleInput.getText().toString().trim();
        String author = authorInput.getText().toString().trim();
        String isbn = isbnInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();

        // Validation des champs
        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
            showCustomToast("Veuillez remplir tous les champs obligatoires", false);
            return;
        }

        // Créer un objet Book avec les nouvelles données
        Book updatedBook = new Book(bookId, title, author, isbn, true, description, "");
        updatedBook.setId(bookId);
        updatedBook.setTitle(title);
        updatedBook.setAuthor(author);
        updatedBook.setIsbn(isbn);
        updatedBook.setDescription(description);

        // Mettre à jour le livre dans la base de données
        int rowsUpdated = dbHelper.updateBook(updatedBook);

        if (rowsUpdated > 0) {
            showCustomToast("Livre modifié avec succès !", true);
            
            // Redirection vers la page des livres disponibles
            Intent intent = new Intent(EditBookActivity.this, AvailableBooksActivity.class);
            startActivity(intent);
            finish();
        } else {
            showCustomToast("Erreur lors de la modification du livre", false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showCustomToast(String message, boolean success) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, 
            findViewById(R.id.custom_toast_container));

        TextView text = layout.findViewById(R.id.text);
        text.setText(message);

        if (success) {
            layout.setBackgroundResource(R.drawable.toast_success_background);
        } else {
            layout.setBackgroundResource(R.drawable.toast_error_background);
        }

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
} 