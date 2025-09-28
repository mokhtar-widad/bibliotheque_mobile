package com.example.biblio.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.biblio.ChoiceUserActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblio.R;
import com.example.biblio.adapters.BorrowingHistoryAdapter;
import com.example.biblio.database.DatabaseHelper;
import com.google.android.material.navigation.NavigationView;

import java.util.List;
import java.util.Map;

public class StudentBorrowHistoryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private RecyclerView historyRecyclerView;
    private TextView emptyView;
    private DatabaseHelper dbHelper;
    private String studentId;
    private BorrowingHistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_borrow_history);

        // Initialiser la base de données
        dbHelper = new DatabaseHelper(this);
        
        // Récupérer l'ID de l'étudiant depuis l'intent
        studentId = getIntent().getStringExtra("student_id");
        if (studentId == null) {
            finish();
            return;
        }
        
        // Configurer la toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // Configurer le DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        
        // Configurer la navigation
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_history);
        
        // Récupérer les références aux vues
        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        emptyView = findViewById(R.id.emptyView);
        
        // Configurer le RecyclerView
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Charger l'historique des emprunts
        loadBorrowingHistory();
    }

    private void loadBorrowingHistory() {
        // Récupérer l'historique des emprunts de l'étudiant
        List<Map<String, Object>> historyItems = dbHelper.getBorrowingHistory(studentId);
        
        if (historyItems.isEmpty()) {
            historyRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            historyRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            
            // Initialiser et définir l'adaptateur
            historyAdapter = new BorrowingHistoryAdapter(this, historyItems);
            historyRecyclerView.setAdapter(historyAdapter);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.nav_available_books) {
            Intent intent = new Intent(this, StudentHomeActivity.class);
            intent.putExtra("student_id", studentId);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_my_books) {
            Intent intent = new Intent(this, StudentBorrowedBooksActivity.class);
            intent.putExtra("student_id", studentId);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_favorites) {
            Intent intent = new Intent(this, StudentFavoriteBooksActivity.class);
            intent.putExtra("student_id", studentId);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_history) {
            // Déjà sur cette page
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_logout) {
            // Déconnexion
            getSharedPreferences("UserSession", MODE_PRIVATE).edit().clear().apply();
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
}
