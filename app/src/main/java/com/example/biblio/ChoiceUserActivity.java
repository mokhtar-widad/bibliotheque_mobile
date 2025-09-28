package com.example.biblio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
import com.example.biblio.activities.LibrarianLoginActivity;
import com.example.biblio.activities.StudentAuthActivity;

public class ChoiceUserActivity extends AppCompatActivity {

    private MaterialCardView studentCard;
    private MaterialCardView librarianCard;
    private TextView welcomeText;
    private TextView subtitleText;
    private ImageView logoImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_user);

        // Initialisation des vues
        studentCard = findViewById(R.id.studentCard);
        librarianCard = findViewById(R.id.librarianCard);
        welcomeText = findViewById(R.id.welcomeText);
        subtitleText = findViewById(R.id.subtitleText);
        logoImage = findViewById(R.id.logoImage);

        // Animation pour le logo
        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        logoImage.startAnimation(rotate);

        // Animation des textes
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        welcomeText.startAnimation(fadeIn);
        subtitleText.startAnimation(fadeIn);

        // Animation des cartes
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        studentCard.startAnimation(slideUp);
        librarianCard.startAnimation(slideUp);

        // Gestion des clics sur les cartes
        studentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Animation de clic
                Animation scaleDown = AnimationUtils.loadAnimation(ChoiceUserActivity.this, R.anim.scale_down);
                v.startAnimation(scaleDown);

                // Redirection vers l'activité d'authentification des étudiants
                Intent intent = new Intent(ChoiceUserActivity.this, StudentAuthActivity.class);
                startActivity(intent);
            }
        });

        librarianCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Animation de clic
                Animation scaleDown = AnimationUtils.loadAnimation(ChoiceUserActivity.this, R.anim.scale_down);
                v.startAnimation(scaleDown);

                // Redirection vers l'activité de connexion des bibliothécaires
                Intent intent = new Intent(ChoiceUserActivity.this, LibrarianLoginActivity.class);
                startActivity(intent);
            }
        });
    }
}