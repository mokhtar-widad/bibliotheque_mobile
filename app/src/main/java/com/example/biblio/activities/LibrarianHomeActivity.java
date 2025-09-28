package com.example.biblio.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.biblio.R;
import com.example.biblio.ChoiceUserActivity;
import com.example.biblio.database.DatabaseHelper;
import com.google.android.material.navigation.NavigationView;

public class LibrarianHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView welcomeText;
    private DatabaseHelper dbHelper;
    private TextView totalBooksText;
    private TextView availableBooksText;
    private TextView borrowedBooksText;
    private TextView totalStudentsText;
    private TextView blacklistedStudentsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_librarian_home);

        // Initialisation de la base de données
        dbHelper = new DatabaseHelper(this);

        // Configuration de la toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tableau de bord");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        // Configuration du drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Configuration du texte de bienvenue
        welcomeText = findViewById(R.id.welcomeText);
        String librarianName = dbHelper.getLibrarianName();
        welcomeText.setText("Bienvenue " + librarianName + " !");

        // Initialisation des vues pour les statistiques
        totalBooksText = findViewById(R.id.totalBooksCount);
        availableBooksText = findViewById(R.id.availableBooksCount);
        borrowedBooksText = findViewById(R.id.borrowedBooksCount);
        totalStudentsText = findViewById(R.id.studentsCount);
        blacklistedStudentsText = findViewById(R.id.blacklistedStudentsCount);

        // Chargement initial des statistiques
        updateDashboardStats();
    }

    private void updateDashboardStats() {
        // Récupération des statistiques depuis la base de données
        int totalBooks = dbHelper.getTotalBooks();
        int availableBooks = dbHelper.getAvailableBooksCount();
        int borrowedBooks = dbHelper.getBorrowedBooksCount();
        int totalStudents = dbHelper.getTotalStudents();
        int blacklistedStudents = dbHelper.getBlacklistedStudentsCount();

        // Mise à jour des TextViews
        totalBooksText.setText(String.valueOf(totalBooks));
        availableBooksText.setText(String.valueOf(availableBooks));
        borrowedBooksText.setText(String.valueOf(borrowedBooks));
        totalStudentsText.setText(String.valueOf(totalStudents));
        blacklistedStudentsText.setText(String.valueOf(blacklistedStudents));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Mise à jour des statistiques à chaque retour sur l'activité
        updateDashboardStats();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            // Déjà sur le dashboard
        } else if (id == R.id.nav_add_book) {
            startActivity(new Intent(this, AddBookActivity.class));
        } else if (id == R.id.nav_available_books) {
            startActivity(new Intent(this, AvailableBooksActivity.class));
        } else if (id == R.id.nav_borrowed_books) {
            startActivity(new Intent(this, BorrowedBooksActivity.class));
        } else if (id == R.id.nav_blacklist) {
            startActivity(new Intent(this, BlacklistActivity.class));
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
} 