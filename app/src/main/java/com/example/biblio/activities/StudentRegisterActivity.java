package com.example.biblio.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.biblio.R;
import com.example.biblio.database.DatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class StudentRegisterActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextInputEditText nomInput;
    private TextInputEditText prenomInput;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private MaterialButton registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);

        // Initialiser la base de données
        dbHelper = new DatabaseHelper(this);

        // Initialiser les vues
        nomInput = findViewById(R.id.nomInput);
        prenomInput = findViewById(R.id.prenomInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        registerButton = findViewById(R.id.registerButton);

        // Gestion du clic sur le bouton d'inscription
        registerButton.setOnClickListener(v -> registerStudent());
    }

    private void registerStudent() {
        String nom = nomInput.getText().toString().trim();
        String prenom = prenomInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validation des champs
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showCustomToast("Veuillez remplir tous les champs", false);
            return;
        }

        // Ajouter l'étudiant à la base de données
        long result = dbHelper.addUser(nom, prenom, email, password, "student");

        if (result > 0) {
            showCustomToast("Inscription réussie !", true);
            
            // Redirection vers l'espace étudiant après une courte pause
            new Thread(() -> {
                try {
                    Thread.sleep(1500); // Attendre 1.5 secondes
                    runOnUiThread(() -> {
                        Intent intent = new Intent(StudentRegisterActivity.this, StudentHomeActivity.class);
                        startActivity(intent);
                        finish();
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            showCustomToast("Erreur lors de l'inscription", false);
        }
    }

    private void showCustomToast(String message, boolean isSuccess) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, null);

        TextView text = layout.findViewById(R.id.text);
        text.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 0);
        
        // Ajuster la durée en fonction du type de message
        if (isSuccess) {
            toast.setDuration(Toast.LENGTH_SHORT); // Court pour le succès
        } else {
            toast.setDuration(Toast.LENGTH_LONG); // Long pour les erreurs
        }
        
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
