package com.example.biblio;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.biblio.ChoiceUserActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 3000; // 3 secondes
    private ImageView logoImageView;
    private TextView appNameTextView;
    private TextView welcomeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Initialiser les vues
        logoImageView = findViewById(R.id.logoImageView);
        appNameTextView = findViewById(R.id.appNameTextView);
        welcomeTextView = findViewById(R.id.welcomeTextView);

        // Charger les animations
        Animation logoAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_animation);
        Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);

        // Configurer l'animation du logo
        logoAnimation.setStartOffset(500); // Démarrer après 500ms
        logoImageView.startAnimation(logoAnimation);

        // Configurer les animations du texte
        appNameTextView.setAlpha(0f);
        welcomeTextView.setAlpha(0f);
        
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            appNameTextView.animate()
                    .alpha(1f)
                    .setDuration(1000)
                    .start();
            
            welcomeTextView.animate()
                    .alpha(1f)
                    .setDuration(1000)
                    .start();
        }, 1000);

        // Utiliser un Handler pour retarder le lancement de ChoiceUserActivity
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Créer une Intent pour lancer ChoiceUserActivity
                Intent intent = new Intent(SplashScreenActivity.this, ChoiceUserActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish(); // Terminer SplashScreenActivity pour qu'elle ne soit pas dans la pile d'activités
            }
        }, SPLASH_DELAY);
    }
} 