package com.example.biblio.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblio.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.StudentViewHolder> {
    private List<Map<String, Object>> students;
    private List<Map<String, Object>> allStudents; // Liste complète pour la recherche
    private Map<Integer, Boolean> selectedStudents; // Map pour stocker les positions des étudiants sélectionnés
    private OnStudentSelectedListener listener;

    public interface OnStudentSelectedListener {
        void onStudentSelected(Map<String, Object> student);
    }

    public StudentListAdapter(OnStudentSelectedListener listener) {
        this.students = new ArrayList<>();
        this.allStudents = new ArrayList<>();
        this.selectedStudents = new HashMap<>();
        this.listener = listener;
    }

    public void setStudents(List<Map<String, Object>> students) {
        this.students = new ArrayList<>(students);
        this.allStudents = new ArrayList<>(students);
        notifyDataSetChanged();
    }
    
    public void filter(String query) {
        students.clear();
        
        if (query.isEmpty()) {
            students.addAll(allStudents);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Map<String, Object> student : allStudents) {
                String fullName = (String) student.get("fullName");
                if (fullName != null && fullName.toLowerCase().contains(lowerCaseQuery)) {
                    students.add(student);
                }
            }
        }
        
        // Mettre à jour la map des sélections pour correspondre aux éléments visibles
        Map<Integer, Boolean> updatedSelections = new HashMap<>();
        for (int i = 0; i < students.size(); i++) {
            Map<String, Object> student = students.get(i);
            Object studentId = student.get("id");
            
            // Vérifier si cet étudiant était sélectionné précédemment
            boolean wasSelected = false;
            for (int j = 0; j < allStudents.size(); j++) {
                if (selectedStudents.containsKey(j) && selectedStudents.get(j) 
                    && allStudents.get(j).get("id").equals(studentId)) {
                    wasSelected = true;
                    break;
                }
            }
            
            if (wasSelected) {
                updatedSelections.put(i, true);
            }
        }
        
        selectedStudents = updatedSelections;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Map<String, Object> student = students.get(position);
        holder.studentName.setText((String) student.get("fullName"));
        
        // Gérer la sélection multiple avec CheckBox
        holder.checkBox.setChecked(selectedStudents.containsKey(position) && selectedStudents.get(position));
        
        // Définir le listener pour le clic sur la checkbox
        holder.checkBox.setOnClickListener(v -> {
            boolean isChecked = holder.checkBox.isChecked();
            selectedStudents.put(position, isChecked);
            
            // Appeler le listener seulement si sélectionné
            if (isChecked) {
                listener.onStudentSelected(student);
            }
        });
        
        // Définir le listener pour le clic sur l'élément
        holder.itemView.setOnClickListener(v -> {
            boolean isCurrentlyChecked = selectedStudents.containsKey(position) && selectedStudents.get(position);
            holder.checkBox.setChecked(!isCurrentlyChecked);
            selectedStudents.put(position, !isCurrentlyChecked);
            
            // Appeler le listener seulement si sélectionné
            if (!isCurrentlyChecked) {
                listener.onStudentSelected(student);
            }
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public List<Map<String, Object>> getSelectedStudents() {
        List<Map<String, Object>> selectedStudentsList = new ArrayList<>();
        
        for (Integer position : selectedStudents.keySet()) {
            if (selectedStudents.get(position) && position < students.size()) {
                selectedStudentsList.add(students.get(position));
            }
        }
        
        return selectedStudentsList;
    }
    
    // Maintenir cette méthode pour la compatibilité avec le code existant
    public Map<String, Object> getSelectedStudent() {
        for (Integer position : selectedStudents.keySet()) {
            if (selectedStudents.get(position) && position < students.size()) {
                // Retourner le premier étudiant sélectionné trouvé
                return students.get(position);
            }
        }
        return null;
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView studentName;
        CheckBox checkBox;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.studentName);
            checkBox = itemView.findViewById(R.id.studentRadioButton);
        }
    }
}
