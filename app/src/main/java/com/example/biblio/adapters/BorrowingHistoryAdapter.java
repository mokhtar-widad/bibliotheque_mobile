package com.example.biblio.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblio.R;

import java.util.List;
import java.util.Map;

public class BorrowingHistoryAdapter extends RecyclerView.Adapter<BorrowingHistoryAdapter.HistoryViewHolder> {

    private final Context context;
    private List<Map<String, Object>> historyItems;

    public BorrowingHistoryAdapter(Context context, List<Map<String, Object>> historyItems) {
        this.context = context;
        this.historyItems = historyItems;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_borrowing_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Map<String, Object> item = historyItems.get(position);
        
        // Remplir les informations du livre
        holder.titleText.setText((String) item.get("title"));
        holder.authorText.setText((String) item.get("author"));
        holder.isbnText.setText("ISBN: " + item.get("isbn"));
        
        // Remplir les informations d'emprunt
        holder.borrowDateText.setText((String) item.get("borrowDate"));
        holder.returnDateText.setText((String) item.get("returnDeadline"));
        
        // Configurer le statut avec la couleur appropriée
        String status = (String) item.get("status");
        holder.statusText.setText(status);
        
        // Définir la couleur en fonction du statut
        switch (status) {
            case "Rendu":
                holder.statusText.setBackgroundColor(context.getResources().getColor(R.color.success));
                break;
            case "En cours":
                holder.statusText.setBackgroundColor(context.getResources().getColor(R.color.primary));
                break;
            case "En retard":
                holder.statusText.setBackgroundColor(context.getResources().getColor(R.color.error));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }
    
    // Méthode pour mettre à jour les données
    public void setHistoryItems(List<Map<String, Object>> historyItems) {
        this.historyItems = historyItems;
        notifyDataSetChanged();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, authorText, isbnText, borrowDateText, returnDateText, statusText;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.historyTitleText);
            authorText = itemView.findViewById(R.id.historyAuthorText);
            isbnText = itemView.findViewById(R.id.historyIsbnText);
            borrowDateText = itemView.findViewById(R.id.historyBorrowDateText);
            returnDateText = itemView.findViewById(R.id.historyReturnDateText);
            statusText = itemView.findViewById(R.id.historyStatusText);
        }
    }
}
