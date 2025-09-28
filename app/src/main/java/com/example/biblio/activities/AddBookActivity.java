package com.example.biblio.activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.biblio.R;
import com.example.biblio.database.DatabaseHelper;
import com.example.biblio.models.Book;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.snackbar.Snackbar;

public class AddBookActivity extends AppCompatActivity {
    private TextInputEditText titleInput;
    private TextInputEditText authorInput;
    private TextInputEditText isbnInput;
    private TextInputEditText descriptionInput;
    private MaterialButton addButton;
    private DatabaseHelper dbHelper;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        // Configuration de la toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ajouter un livre");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialisation des vues
        rootView = findViewById(android.R.id.content).getRootView();
        titleInput = findViewById(R.id.titleInput);
        authorInput = findViewById(R.id.authorInput);
        isbnInput = findViewById(R.id.isbnInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        addButton = findViewById(R.id.addButton);
        dbHelper = new DatabaseHelper(this);

        addButton.setOnClickListener(v -> addBook());
    }

    private void addBook() {
        String title = titleInput.getText().toString().trim();
        String author = authorInput.getText().toString().trim();
        String isbn = isbnInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
            Snackbar.make(rootView, "Veuillez remplir tous les champs obligatoires", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(getResources().getColor(R.color.error))
                    .setTextColor(getResources().getColor(R.color.white))
                    .show();
            return;
        }

        Book book = new Book(title, author, isbn);
        book.setDescription(description);
        book.setAvailable(true);

        long result = dbHelper.addBook(book);
        if (result != -1) {
            // Afficher une notification personnalisée de succès
            showSuccessNotification("Livre ajouté avec succès !");
            
            // Effacer les champs pour préparer l'ajout d'un nouveau livre
            clearFields();
            
            // Attendre que la notification soit affichée avant de fermer l'activité
            new Handler().postDelayed(this::finish, 2000);
        } else {
            Snackbar.make(rootView, "Erreur lors de l'ajout du livre", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(getResources().getColor(R.color.error))
                    .setTextColor(getResources().getColor(R.color.white))
                    .show();
        }
    }

    private void showSuccessNotification(String message) {
        // Inflater le layout de notification personnalisé
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.success_toast, null);

        // Définir le message
        TextView text = layout.findViewById(R.id.text);
        text.setText(message);

        // Créer et configurer le toast
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
    
    private void clearFields() {
        titleInput.setText("");
        authorInput.setText("");
        isbnInput.setText("");
        descriptionInput.setText("");
        titleInput.requestFocus();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 