package com.example.biblio.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.example.biblio.R;
import com.example.biblio.database.DatabaseHelper;

public class LibrarianLoginActivity extends AppCompatActivity {

    private MaterialButton backButton;
    private MaterialButton loginButton;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private CardView authContainer;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_librarian_login);

        // Initialisation des vues
        backButton = findViewById(R.id.backButton);
        loginButton = findViewById(R.id.loginButton);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        authContainer = findViewById(R.id.authContainer);
        dbHelper = new DatabaseHelper(this);

        // Animation du bouton de retour
        Animation buttonAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        backButton.startAnimation(buttonAnimation);

        // Animation de la carte d'authentification
        Animation cardAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        authContainer.startAnimation(cardAnimation);

        // Gestion du clic sur le bouton de retour
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Animation de sortie
                Animation fadeOut = AnimationUtils.loadAnimation(LibrarianLoginActivity.this, R.anim.fade_out);
                backButton.startAnimation(fadeOut);
                authContainer.startAnimation(fadeOut);

                // Retour à l'écran précédent après l'animation
                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        finish();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            }
        });

        // Gestion du clic sur le bouton de connexion
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    showCustomToast("Veuillez remplir tous les champs", false);
                    return;
                }

                if (dbHelper.checkBibliothecaireCredentials(email, password)) {
                    // Récupérer le nom du bibliothécaire
                    String librarianName = dbHelper.getBibliothecaireName(email);
                    
                    // Afficher le message de succès
                    showCustomToast("Connexion réussie !", true);

                    // Animation de sortie
                    Animation fadeOut = AnimationUtils.loadAnimation(LibrarianLoginActivity.this, R.anim.fade_out);
                    backButton.startAnimation(fadeOut);
                    authContainer.startAnimation(fadeOut);

                    // Redirection vers la page d'accueil après l'animation
                    fadeOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            Intent intent = new Intent(LibrarianLoginActivity.this, LibrarianHomeActivity.class);
                            intent.putExtra("LIBRARIAN_NAME", librarianName);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });
                } else {
                    // Afficher le message d'erreur
                    showCustomToast("Email ou mot de passe incorrect. Veuillez réessayer.", false);
                    
                    // Animation de secousse pour indiquer l'erreur
                    Animation shake = AnimationUtils.loadAnimation(LibrarianLoginActivity.this, R.anim.shake);
                    authContainer.startAnimation(shake);
                }
            }
        });
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