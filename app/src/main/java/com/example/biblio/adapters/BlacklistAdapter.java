package com.example.biblio.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblio.R;
import com.example.biblio.models.BlacklistedStudent;

import java.util.ArrayList;
import java.util.List;

public class BlacklistAdapter extends RecyclerView.Adapter<BlacklistAdapter.BlacklistViewHolder> {
    private List<BlacklistedStudent> blacklistedStudents;
    private List<BlacklistedStudent> allBlacklistedStudents; // Liste complète pour la recherche
    private OnBlacklistActionListener listener;

    public interface OnBlacklistActionListener {
        void onRemoveFromBlacklist(BlacklistedStudent student);
    }

    public BlacklistAdapter(OnBlacklistActionListener listener) {
        this.blacklistedStudents = new ArrayList<>();
        this.allBlacklistedStudents = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public BlacklistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_blacklisted_student, parent, false);
        return new BlacklistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlacklistViewHolder holder, int position) {
        BlacklistedStudent student = blacklistedStudents.get(position);
        holder.bind(student);
    }

    @Override
    public int getItemCount() {
        return blacklistedStudents.size();
    }

    public void setBlacklistedStudents(List<BlacklistedStudent> students) {
        this.blacklistedStudents = new ArrayList<>(students);
        this.allBlacklistedStudents = new ArrayList<>(students);
        notifyDataSetChanged();
    }
    
    public void filter(String query) {
        blacklistedStudents.clear();
        
        if (query.isEmpty()) {
            blacklistedStudents.addAll(allBlacklistedStudents);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (BlacklistedStudent student : allBlacklistedStudents) {
                // Filtrer par nom d'étudiant ou par raison
                if (student.getStudentName().toLowerCase().contains(lowerCaseQuery) || 
                    student.getReason().toLowerCase().contains(lowerCaseQuery)) {
                    blacklistedStudents.add(student);
                }
            }
        }
        
        notifyDataSetChanged();
    }

    class BlacklistViewHolder extends RecyclerView.ViewHolder {
        private TextView studentNameText;
        private TextView reasonText;
        private TextView endDateText;

        BlacklistViewHolder(@NonNull View itemView) {
            super(itemView);
            studentNameText = itemView.findViewById(R.id.studentNameText);
            reasonText = itemView.findViewById(R.id.reasonText);
            endDateText = itemView.findViewById(R.id.endDateText);
        }

        void bind(BlacklistedStudent student) {
            // Utiliser le nom de l'étudiant maintenant disponible
            studentNameText.setText(student.getStudentName());
            reasonText.setText(student.getReason());
            endDateText.setText("Fin: " + student.getEndDate());

            itemView.findViewById(R.id.removeButton).setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveFromBlacklist(student);
                }
            });
        }
    }
}