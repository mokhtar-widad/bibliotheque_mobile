package com.example.biblio.activities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblio.R;
import com.example.biblio.adapters.BlacklistAdapter;
import com.example.biblio.adapters.StudentListAdapter;
import com.example.biblio.database.DatabaseHelper;
import com.example.biblio.models.BlacklistedStudent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BlacklistActivity extends AppCompatActivity implements BlacklistAdapter.OnBlacklistActionListener {
    private RecyclerView blacklistRecyclerView;
    private TextView emptyView;
    private BlacklistAdapter blacklistAdapter;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklist);

        // Initialiser la base de données
        databaseHelper = new DatabaseHelper(this);

        // Configurer la barre d'outils
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Liste noire");

        // Initialiser les vues
        blacklistRecyclerView = findViewById(R.id.blacklistRecyclerView);
        emptyView = findViewById(R.id.emptyView);
        FloatingActionButton addButton = findViewById(R.id.addButton);
        androidx.appcompat.widget.SearchView searchView = findViewById(R.id.searchView);

        // Configurer le RecyclerView
        blacklistRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        blacklistAdapter = new BlacklistAdapter(this);
        blacklistRecyclerView.setAdapter(blacklistAdapter);

        // Configurer la SearchView
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterStudents(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterStudents(newText);
                return true;
            }
        });

        // Configurer le bouton d'ajout
        addButton.setOnClickListener(v -> {
            showStudentSelectionDialog();
        });

        // Charger la liste noire
        loadBlacklist();
    }

    private void filterStudents(String query) {
        if (blacklistAdapter != null) {
            blacklistAdapter.filter(query);
            updateEmptyView(blacklistAdapter.getItemCount() == 0);
        }
    }

    private void loadBlacklist() {
        List<BlacklistedStudent> blacklistedStudents = databaseHelper.getBlacklistedStudents();
        blacklistAdapter.setBlacklistedStudents(blacklistedStudents);
        updateEmptyView(blacklistedStudents.isEmpty());
    }

    private void updateEmptyView(boolean isEmpty) {
        blacklistRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onRemoveFromBlacklist(BlacklistedStudent student) {
        databaseHelper.removeFromBlacklist(student.getId());
        loadBlacklist();
        Toast.makeText(this, "Étudiant retiré de la liste noire", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showStudentSelectionDialog() {
        // Créer le dialogue personnalisé
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_student_list);

        // Configurer la fenêtre du dialogue
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        // Initialiser les vues du dialogue
        RecyclerView studentsRecyclerView = dialog.findViewById(R.id.studentsRecyclerView);
        TextView emptyStudentListView = dialog.findViewById(R.id.emptyStudentListView);
        EditText reasonInput = dialog.findViewById(R.id.blacklistReasonInput);
        EditText endDateInput = dialog.findViewById(R.id.blacklistEndDateInput);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);
        Button addButton = dialog.findViewById(R.id.addToBlacklistButton);
        androidx.appcompat.widget.SearchView studentSearchView = dialog.findViewById(R.id.studentSearchView);

        // Configurer la date actuelle
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        endDateInput.setText(currentDate);

        // Configurer le RecyclerView avec l'adaptateur
        StudentListAdapter adapter = new StudentListAdapter(student -> {
            // Rien à faire ici, juste pour la sélection
        });
        studentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        studentsRecyclerView.setAdapter(adapter);

        // Charger les étudiants depuis la base de données
        List<Map<String, Object>> students = databaseHelper.getAllStudents();
        adapter.setStudents(students);

        // Configurer la barre de recherche
        studentSearchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                updateEmptyStudentView(adapter.getItemCount() == 0, emptyStudentListView, studentsRecyclerView);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                updateEmptyStudentView(adapter.getItemCount() == 0, emptyStudentListView, studentsRecyclerView);
                return true;
            }
        });

        // Afficher un message si la liste est vide
        if (students.isEmpty()) {
            updateEmptyStudentView(true, emptyStudentListView, studentsRecyclerView);
            addButton.setEnabled(false);
        } else {
            updateEmptyStudentView(false, emptyStudentListView, studentsRecyclerView);
        }

        // Configurer les boutons
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        addButton.setOnClickListener(v -> {
            // Récupérer tous les étudiants sélectionnés
            List<Map<String, Object>> selectedStudents = adapter.getSelectedStudents();
            if (selectedStudents.isEmpty()) {
                Toast.makeText(this, "Veuillez sélectionner au moins un étudiant", Toast.LENGTH_SHORT).show();
                return;
            }

            // Vérifier que les champs sont remplis
            String reason = reasonInput.getText().toString().trim();
            String endDate = endDateInput.getText().toString().trim();

            if (TextUtils.isEmpty(reason)) {
                reasonInput.setError("La raison est requise");
                return;
            }

            if (TextUtils.isEmpty(endDate)) {
                endDateInput.setError("La date de fin est requise");
                return;
            }

            // Compteurs pour suivre le nombre d'ajouts réussis et échoués
            int successCount = 0;
            int failCount = 0;

            // Ajouter chaque étudiant sélectionné à la liste noire
            for (Map<String, Object> selectedStudent : selectedStudents) {
                try {
                    // Récupérer et convertir l'ID de l'étudiant
                    int studentId;
                    Object idValue = selectedStudent.get("id");

                    // Gérer les différents types possibles d'ID
                    if (idValue instanceof Integer) {
                        studentId = (Integer) idValue;
                    } else if (idValue instanceof Long) {
                        studentId = ((Long) idValue).intValue();
                    } else if (idValue instanceof String) {
                        studentId = Integer.parseInt((String) idValue);
                    } else {
                        failCount++;
                        continue; // Passer à l'étudiant suivant
                    }

                    // Ajouter l'étudiant à la liste noire
                    long result = databaseHelper.addToBlacklist(studentId, reason, endDate);

                    if (result != -1) {
                        successCount++;
                    } else {
                        failCount++;
                    }
                } catch (Exception e) {
                    failCount++;
                }
            }

            // Afficher un message adapté selon les résultats
            if (successCount > 0) {
                String message = successCount + " étudiant" + (successCount > 1 ? "s" : "") +
                        " ajouté" + (successCount > 1 ? "s" : "") + " à la liste noire avec succès";
                if (failCount > 0) {
                    message += " (échec pour " + failCount + " étudiant" + (failCount > 1 ? "s" : "") + ")";
                }

                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                        .setBackgroundTint(getResources().getColor(R.color.success))
                        .setTextColor(getResources().getColor(R.color.white))
                        .show();

                // Recharger la liste
                loadBlacklist();

                // Fermer le dialogue
                dialog.dismiss();
            } else if (failCount > 0) {
                // Uniquement des échecs
                Snackbar.make(findViewById(android.R.id.content),
                        "Erreur lors de l'ajout à la liste noire", Snackbar.LENGTH_LONG)
                        .setBackgroundTint(getResources().getColor(R.color.error))
                        .setTextColor(getResources().getColor(R.color.white))
                        .show();
            }
        });

        // Afficher le dialogue
        dialog.show();
    }

    private void updateEmptyStudentView(boolean isEmpty, TextView emptyView, RecyclerView recyclerView) {
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        if (isEmpty) {
            emptyView.setText("Aucun étudiant trouvé");
        } else {
            emptyView.setText("Aucun étudiant disponible");
        }
    }
}