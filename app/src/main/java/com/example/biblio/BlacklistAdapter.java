package com.example.biblio;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

/**
 * Adaptateur pour afficher la liste des étudiants blacklistés dans un RecyclerView
 * Gère l'affichage et les interactions avec la liste des étudiants en liste noire
 */
public class BlacklistAdapter extends RecyclerView.Adapter<BlacklistAdapter.BlacklistViewHolder> {
    private List<BlacklistedStudent> students;
    private OnRemoveClickListener onRemoveClickListener;

    /**
     * Interface pour gérer les clics sur le bouton de suppression
     * Permet de notifier l'activité parente lorsqu'un étudiant est retiré de la liste noire
     */
    public interface OnRemoveClickListener {
        void onRemoveClick(int studentId);
    }

    /**
     * Constructeur de l'adaptateur
     * @param students Liste des étudiants blacklistés à afficher
     * @param listener Interface pour gérer les clics sur le bouton de suppression
     */
    public BlacklistAdapter(List<BlacklistedStudent> students, OnRemoveClickListener listener) {
        this.students = students;
        this.onRemoveClickListener = listener;
    }

    /**
     * Crée une nouvelle vue pour chaque élément de la liste
     * @param parent Le ViewGroup parent
     * @param viewType Le type de vue
     * @return Un nouveau ViewHolder contenant la vue
     */
    @NonNull
    @Override
    public BlacklistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_blacklist, parent, false);
        return new BlacklistViewHolder(view);
    }

    /**
     * Remplit les données dans la vue pour un élément spécifique
     * @param holder Le ViewHolder à remplir
     * @param position La position de l'élément dans la liste
     */
    @Override
    public void onBindViewHolder(@NonNull BlacklistViewHolder holder, int position) {
        BlacklistedStudent student = students.get(position);
        holder.nameTextView.setText(student.getName());
        holder.emailTextView.setText(student.getEmail());
        holder.numberTextView.setText("Numéro étudiant: " + student.getStudentNumber());
        holder.reasonTextView.setText("Raison: " + student.getReason());

        holder.removeButton.setOnClickListener(v -> {
            if (onRemoveClickListener != null) {
                onRemoveClickListener.onRemoveClick(student.getId());
            }
        });
    }

    /**
     * Retourne le nombre total d'éléments dans la liste
     * @return Le nombre d'étudiants blacklistés
     */
    @Override
    public int getItemCount() {
        return students.size();
    }

    /**
     * Classe interne qui contient les vues pour chaque élément de la liste
     * Gère les références aux éléments de l'interface utilisateur
     */
    static class BlacklistViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView emailTextView;
        TextView numberTextView;
        TextView reasonTextView;
        MaterialButton removeButton;

        /**
         * Constructeur du ViewHolder
         * Initialise les références aux vues de l'interface utilisateur
         * @param itemView La vue de l'élément
         */
        BlacklistViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.studentName);
            emailTextView = itemView.findViewById(R.id.studentEmail);
            numberTextView = itemView.findViewById(R.id.studentNumber);
            reasonTextView = itemView.findViewById(R.id.blacklistReason);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }
} 