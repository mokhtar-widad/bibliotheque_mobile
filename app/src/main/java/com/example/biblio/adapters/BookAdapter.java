package com.example.biblio.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblio.R;
import com.example.biblio.models.Book;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private static final int ANIMATION_DURATION = 100;
    private final OnBookActionListener listener;
    
    // Constantes pour les différents modes
    public static final int MODE_LIBRARIAN = 1;
    public static final int MODE_STUDENT = 2;
    public static final int MODE_STUDENT_BORROWED = 3;
    public static final int MODE_STUDENT_FAVORITE = 4;
    public static final int MODE_LIBRARIAN_BORROWED = 5;
    
    private int currentMode = MODE_STUDENT;
    
    private boolean isLibrarian = false;
    private boolean isBorrowedMode = false;
    private boolean isLibrarianBorrowedMode = false;
    private boolean isFavoriteMode = false;

    public interface OnBookActionListener {
        void onEditClick(Book book);
        void onDeleteClick(Book book);
        void onBorrowClick(Book book);
        void onFavoriteClick(Book book);
    }

    private List<Book> allBooks = new ArrayList<>();
    private List<Book> filteredBooks = new ArrayList<>();

    public BookAdapter(OnBookActionListener listener) {
        this.listener = listener;
        this.allBooks = new ArrayList<>();
        this.filteredBooks = new ArrayList<>();
    }
    
    public BookAdapter(List<Book> books, OnBookActionListener listener, int mode) {
        this.listener = listener;
        this.currentMode = mode;
        setBooks(books);
        
        // Configurer les drapeaux en fonction du mode
        switch (mode) {
            case MODE_LIBRARIAN:
                isLibrarian = true;
                isBorrowedMode = false;
                isLibrarianBorrowedMode = false;
                break;
            case MODE_STUDENT:
                isLibrarian = false;
                isBorrowedMode = false;
                isLibrarianBorrowedMode = false;
                break;
            case MODE_STUDENT_BORROWED:
                isLibrarian = false;
                isBorrowedMode = true;
                isLibrarianBorrowedMode = false;
                break;
            case MODE_STUDENT_FAVORITE:
                isLibrarian = false;
                isBorrowedMode = false;
                isLibrarianBorrowedMode = false;
                break;
            case MODE_LIBRARIAN_BORROWED:
                isLibrarian = false;
                isBorrowedMode = false;
                isLibrarianBorrowedMode = true;
                break;
        }
    }

    public void setBooks(List<Book> books) {
        allBooks.clear();
        allBooks.addAll(books);
        filterBooks("", "");
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = filteredBooks.get(position);
        holder.bind(book);
        
        // Ajouter l'animation
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(
                holder.itemView.getContext(), R.anim.item_animation));
    }

    @Override
    public int getItemCount() {
        return filteredBooks != null ? filteredBooks.size() : 0;
    }

    public void filterBooks(String titleQuery, String authorQuery) {
        filteredBooks.clear();
        for (Book book : allBooks) {
            if (book.getTitle().toLowerCase().contains(titleQuery.toLowerCase()) &&
                book.getAuthor().toLowerCase().contains(authorQuery.toLowerCase())) {
                filteredBooks.add(book);
            }
        }
        notifyDataSetChanged();
    }

    // Variables déplacées en haut de la classe

    public void setLibrarianMode(boolean isLibrarian) {
        this.isLibrarian = isLibrarian;
    }
    
    public void setBorrowedMode(boolean isBorrowedMode) {
        this.isBorrowedMode = isBorrowedMode;
    }
    
    public void setLibrarianBorrowedMode(boolean isLibrarianBorrowedMode) {
        this.isLibrarianBorrowedMode = isLibrarianBorrowedMode;
    }
    
    public void setFavoriteMode(boolean isFavoriteMode) {
        this.isFavoriteMode = isFavoriteMode;
    }

    public class BookViewHolder extends RecyclerView.ViewHolder {
        private ImageView bookIcon;
        private TextView titleText;
        private TextView authorText;
        private TextView isbnText;
        private TextView borrowerText;
        private MaterialButton editButton;
        private MaterialButton deleteButton;
        private MaterialButton borrowButton;
        private MaterialButton favoriteButton;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            bookIcon = itemView.findViewById(R.id.bookIcon);
            titleText = itemView.findViewById(R.id.titleText);
            authorText = itemView.findViewById(R.id.authorText);
            isbnText = itemView.findViewById(R.id.isbnText);
            borrowerText = itemView.findViewById(R.id.borrowerText);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            borrowButton = itemView.findViewById(R.id.borrowButton);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
        }

        public void bind(Book book) {
            titleText.setText(book.getTitle());
            authorText.setText(book.getAuthor());
            isbnText.setText(book.getIsbn());
            
            if (book.getBorrowerName() != null) {
                borrowerText.setVisibility(View.VISIBLE);
                borrowerText.setText("Emprunté par : " + book.getBorrowerName());
            } else {
                borrowerText.setVisibility(View.GONE);
            }

            // Afficher les boutons selon le rôle
            if (isLibrarianBorrowedMode || currentMode == MODE_LIBRARIAN_BORROWED) {
                // Mode bibliothécaire pour les livres empruntés (sans boutons)
                editButton.setVisibility(View.GONE);
                deleteButton.setVisibility(View.GONE);
                borrowButton.setVisibility(View.GONE);
                favoriteButton.setVisibility(View.GONE);
            } else if (isLibrarian || currentMode == MODE_LIBRARIAN) {
                editButton.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.VISIBLE);
                borrowButton.setVisibility(View.GONE);
                favoriteButton.setVisibility(View.GONE);

                editButton.setOnClickListener(v -> {
                    v.startAnimation(AnimationUtils.loadAnimation(v.getContext(), R.anim.button_click));
                    listener.onEditClick(book);
                });

                deleteButton.setOnClickListener(v -> {
                    v.startAnimation(AnimationUtils.loadAnimation(v.getContext(), R.anim.button_click));
                    listener.onDeleteClick(book);
                });
            } else if (isFavoriteMode || currentMode == MODE_STUDENT_FAVORITE) {
                // Mode favoris - seulement un bouton "Retirer des favoris"
                editButton.setVisibility(View.GONE);
                deleteButton.setVisibility(View.GONE);
                borrowButton.setVisibility(View.GONE);
                favoriteButton.setVisibility(View.VISIBLE);
                
                favoriteButton.setText("Retirer du favori");
                favoriteButton.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(favoriteButton.getContext(), R.color.error)));
                favoriteButton.setOnClickListener(v -> {
                    v.startAnimation(AnimationUtils.loadAnimation(v.getContext(), R.anim.button_click));
                    listener.onFavoriteClick(book);
                });
            } else if (isBorrowedMode || currentMode == MODE_STUDENT_BORROWED) {
                // Mode livres empruntés avec boutons "Restituer" et "Favoris"
                editButton.setVisibility(View.GONE);
                deleteButton.setVisibility(View.GONE);
                borrowButton.setVisibility(View.VISIBLE);
                favoriteButton.setVisibility(View.VISIBLE);
                
                borrowButton.setText("Restituer");
                borrowButton.setOnClickListener(v -> {
                    v.startAnimation(AnimationUtils.loadAnimation(v.getContext(), R.anim.button_click));
                    listener.onBorrowClick(book);
                });
                
                // Configurer le bouton Favoris en fonction de l'état du livre
                if (book.isFavorite()) {
                    // Style rouge pour "Retirer du favori"
                    favoriteButton.setText("Retirer du favori");
                    favoriteButton.setBackgroundTintList(ColorStateList.valueOf(
                            ContextCompat.getColor(favoriteButton.getContext(), R.color.error)));
                } else {
                    // Style normal pour "Favoris"
                    favoriteButton.setText("Favoris");
                    favoriteButton.setBackgroundTintList(ColorStateList.valueOf(
                            ContextCompat.getColor(favoriteButton.getContext(), R.color.secondary)));
                }

                favoriteButton.setOnClickListener(v -> {
                    v.startAnimation(AnimationUtils.loadAnimation(v.getContext(), R.anim.button_click));
                    listener.onFavoriteClick(book);
                });
            } else {
                // Mode normal étudiant avec boutons "Emprunter" et "Favoris"
                editButton.setVisibility(View.GONE);
                deleteButton.setVisibility(View.GONE);
                borrowButton.setVisibility(View.VISIBLE);
                favoriteButton.setVisibility(View.VISIBLE);
                
                borrowButton.setText("Emprunter");
                borrowButton.setOnClickListener(v -> {
                    v.startAnimation(AnimationUtils.loadAnimation(v.getContext(), R.anim.button_click));
                    listener.onBorrowClick(book);
                });
                
                // Configurer le bouton Favoris en fonction de l'état du livre
                if (book.isFavorite()) {
                    // Style rouge pour "Retirer du favori"
                    favoriteButton.setText("Retirer du favori");
                    favoriteButton.setBackgroundTintList(ColorStateList.valueOf(
                            ContextCompat.getColor(favoriteButton.getContext(), R.color.error)));
                } else {
                    // Style normal pour "Favoris"
                    favoriteButton.setText("Favoris");
                    favoriteButton.setBackgroundTintList(ColorStateList.valueOf(
                            ContextCompat.getColor(favoriteButton.getContext(), R.color.secondary)));
                }

                favoriteButton.setOnClickListener(v -> {
                    v.startAnimation(AnimationUtils.loadAnimation(v.getContext(), R.anim.button_click));
                    listener.onFavoriteClick(book);
                });
            }
        }
    }
}