package com.example.biblio.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblio.ChoiceUserActivity;
import com.example.biblio.R;
import com.example.biblio.adapters.BookAdapter;
import com.example.biblio.database.DatabaseHelper;
import com.example.biblio.models.Book;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class StudentFavoriteBooksActivity extends AppCompatActivity implements 
        NavigationView.OnNavigationItemSelectedListener, 
        BookAdapter.OnBookActionListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private RecyclerView favoriteRecyclerView;
    private TextView emptyView;
    private TextInputEditText searchTitleInput, searchAuthorInput;
    private DatabaseHelper dbHelper;
    private BookAdapter bookAdapter;
    private List<Book> favoriteBooks = new ArrayList<>();
    private String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_favorite_books);

        // Récupérer l'ID de l'étudiant depuis l'intent
        studentId = getIntent().getStringExtra("student_id");
        if (studentId == null) {
            finish();
            return;
        }

        // Initialisation des vues
        initViews();

        // Configuration de la toolbar et du drawer
        setupToolbarAndDrawer();

        // Configuration du recycler view
        setupRecyclerView();

        // Configuration des filtres de recherche
        setupSearchFilters();
        
        // Charger les livres favoris
        loadFavoriteBooks();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        favoriteRecyclerView = findViewById(R.id.favoriteRecyclerView);
        emptyView = findViewById(R.id.emptyView);
        searchTitleInput = findViewById(R.id.searchTitleInput);
        searchAuthorInput = findViewById(R.id.searchAuthorInput);
        dbHelper = new DatabaseHelper(this);
    }

    private void setupToolbarAndDrawer() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mes Favoris");
        
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, 
                R.string.navigation_drawer_open, 
                R.string.navigation_drawer_close
        );
        
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        
        navigationView.setNavigationItemSelectedListener(this);
        
        // Mettre à jour l'en-tête du navigation drawer avec les informations de l'étudiant
        View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {
            // Récupérer les informations de l'étudiant depuis la base de données
            String studentEmail = dbHelper.getStudentEmail(studentId);
            
            // Mettre à jour l'image, le nom et l'email de l'étudiant dans l'en-tête
            ImageView userIcon = headerView.findViewById(R.id.nav_header_icon);
            if (userIcon != null) {
                userIcon.setImageResource(R.drawable.ic_student);
            }
            
            TextView userTypeTextView = headerView.findViewById(R.id.nav_header_user_type);
            if (userTypeTextView != null) {
                userTypeTextView.setText("Étudiant");
            }
            
            TextView emailTextView = headerView.findViewById(R.id.nav_header_email);
            if (emailTextView != null && studentEmail != null) {
                emailTextView.setText(studentEmail);
            }
        }
        
        // Mettre en surbrillance l'élément du menu favoris
        navigationView.setCheckedItem(R.id.nav_favorites);
    }

    private void setupRecyclerView() {
        favoriteRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        favoriteRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        
        // Initialisation de l'adaptateur en mode favoris
        bookAdapter = new BookAdapter(this);
        bookAdapter.setFavoriteMode(true); // Mode spécial pour la vue des favoris
        favoriteRecyclerView.setAdapter(bookAdapter);
    }

    private void loadFavoriteBooks() {
        favoriteBooks.clear();
        List<Book> books = dbHelper.getFavoriteBooks(studentId);
        
        if (books.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            favoriteRecyclerView.setVisibility(View.GONE);
        } else {
            favoriteBooks.addAll(books);
            favoriteRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            
            // Assurons-nous que l'adaptateur contient bien les livres
            bookAdapter.setBooks(favoriteBooks);
        }
        
        bookAdapter.notifyDataSetChanged();
        
        // Log pour le débogage
        android.util.Log.d("FAVORIS", "Nombre de livres favoris chargés: " + favoriteBooks.size());
    }

    private void setupSearchFilters() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBooks();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
        
        searchTitleInput.addTextChangedListener(textWatcher);
        searchAuthorInput.addTextChangedListener(textWatcher);
    }

    private void filterBooks() {
        String titleQuery = searchTitleInput.getText().toString().toLowerCase().trim();
        String authorQuery = searchAuthorInput.getText().toString().toLowerCase().trim();
        
        List<Book> filteredBooks = new ArrayList<>();
        List<Book> allFavoriteBooks = dbHelper.getFavoriteBooks(studentId);
        
        for (Book book : allFavoriteBooks) {
            boolean matchesTitle = titleQuery.isEmpty() || 
                    book.getTitle().toLowerCase().contains(titleQuery);
            boolean matchesAuthor = authorQuery.isEmpty() || 
                    book.getAuthor().toLowerCase().contains(authorQuery);
            
            if (matchesTitle && matchesAuthor) {
                filteredBooks.add(book);
            }
        }
        
        favoriteBooks.clear();
        favoriteBooks.addAll(filteredBooks);
        bookAdapter.notifyDataSetChanged();
        
        if (filteredBooks.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            favoriteRecyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            favoriteRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_available_books) {
            // Navigation vers la page des livres disponibles
            Intent intent = new Intent(this, StudentHomeActivity.class);
            intent.putExtra("student_id", studentId);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_my_books) {
            // Navigation vers la page des livres empruntés
            Intent intent = new Intent(this, StudentBorrowedBooksActivity.class);
            intent.putExtra("student_id", studentId);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_favorites) {
            // Déjà sur la page des favoris
        } else if (id == R.id.nav_history) {
            // Navigation vers la page d'historique des emprunts
            Intent intent = new Intent(this, StudentBorrowHistoryActivity.class);
            intent.putExtra("student_id", studentId);
            startActivity(intent);
            finish();
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
    public void onBorrowClick(Book book) {
        // Non utilisé dans cette vue
    }

    @Override
    public void onEditClick(Book book) {
        // Non utilisé dans cette vue
    }

    @Override
    public void onDeleteClick(Book book) {
        // Non utilisé dans cette vue
    }

    @Override
    public void onFavoriteClick(Book book) {
        // Supprimer le livre des favoris directement (pas besoin de vérification préalable)
        boolean success = dbHelper.removeFavoriteBook(studentId, book.getId());
        
        if (success) {
            // Récupérer la position du livre dans la liste
            int position = -1;
            for (int i = 0; i < favoriteBooks.size(); i++) {
                if (favoriteBooks.get(i).getId() == book.getId()) {
                    position = i;
                    break;
                }
            }
            
            if (position != -1) {
                // Supprimer le livre de la liste
                favoriteBooks.remove(position);
                
                // Rafraîchir complètement l'adaptateur au lieu de notifier juste un item supprimé
                bookAdapter.setBooks(favoriteBooks);
                bookAdapter.notifyDataSetChanged();
                
                // Afficher message de confirmation
                Snackbar.make(favoriteRecyclerView, "Livre retiré des favoris : " + book.getTitle(), Snackbar.LENGTH_LONG)
                        .setBackgroundTint(getResources().getColor(R.color.success))
                        .setTextColor(getResources().getColor(R.color.white))
                        .show();
                
                // Vérifier si la liste est devenue vide
                if (favoriteBooks.isEmpty()) {
                    emptyView.setVisibility(View.VISIBLE);
                    favoriteRecyclerView.setVisibility(View.GONE);
                }
            } else {
                // Le livre n'a pas été trouvé dans la liste, rechargeons toute la liste
                loadFavoriteBooks();
                
                Snackbar.make(favoriteRecyclerView, "Livre retiré des favoris", Snackbar.LENGTH_LONG)
                        .setBackgroundTint(getResources().getColor(R.color.success))
                        .setTextColor(getResources().getColor(R.color.white))
                        .show();
            }
        } else {
            // En cas d'erreur, informer l'utilisateur et recharger la liste pour éviter un état incohérent
            Snackbar.make(favoriteRecyclerView, "Erreur lors du retrait des favoris", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(getResources().getColor(R.color.error))
                    .setTextColor(getResources().getColor(R.color.white))
                    .show();
            
            // Recharger la liste des favoris pour s'assurer que l'UI est cohérente
            loadFavoriteBooks();
        }
    }
}
