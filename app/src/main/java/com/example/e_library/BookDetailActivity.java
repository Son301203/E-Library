package com.example.e_library;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class BookDetailActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        ImageView backIcon = findViewById(R.id.backIcon);
        // Nút Back
        backIcon.setOnClickListener(view -> finish());

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Bind views
        ImageView bookImage = findViewById(R.id.bookImage);
        TextView bookTitle = findViewById(R.id.bookTitle);
        TextView bookAuthor = findViewById(R.id.bookAuthor);
        TextView bookDescription = findViewById(R.id.bookDescription);
        TextView bookPublisher = findViewById(R.id.bookPublisher);
        TextView bookYearRelease = findViewById(R.id.bookYearRelease);
        Button registerBorrowButton = findViewById(R.id.registerBorrowButton);

        // Get data from intent
        String title = getIntent().getStringExtra("title");
        String author = getIntent().getStringExtra("author");
        String description = getIntent().getStringExtra("description");
        String publisher = getIntent().getStringExtra("publisher");
        String yearRelease = getIntent().getStringExtra("yearRelease");
        String imageUrl = getIntent().getStringExtra("image");

        // Set data to views
        bookTitle.setText(title);
        bookAuthor.setText(author);
        bookDescription.setText(description);
        bookPublisher.setText(publisher);
        bookYearRelease.setText(yearRelease);

        Glide.with(this)
                .load(imageUrl)
                .into(bookImage);
        // Thiết lập tab
        TabUtils.setupTabs(this);
        // Register borrow button functionality
        registerBorrowButton.setOnClickListener(v -> registerBorrowedBook(title, author, description, publisher, yearRelease, imageUrl));
    }

    private void registerBorrowedBook(String title, String author, String description, String publisher, String yearRelease, String imageUrl) {
        String userId = auth.getCurrentUser().getUid();

        db.collection("users").document(userId)
                .collection("borrowed_books")
                .whereEqualTo("title", title)
                .whereEqualTo("author", author)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // User already borrowed the book
                        Toast.makeText(BookDetailActivity.this, "You have already borrowed this book!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Book is not borrowed yet; proceed to add it
                        Map<String, Object> borrowedBook = new HashMap<>();
                        borrowedBook.put("title", title);
                        borrowedBook.put("author", author);
                        borrowedBook.put("description", description);
                        borrowedBook.put("publisher", publisher);
                        borrowedBook.put("yearRelease", yearRelease);
                        borrowedBook.put("image", imageUrl);
                        borrowedBook.put("borrowedAt", System.currentTimeMillis());

                        db.collection("users").document(userId)
                                .collection("borrowed_books")
                                .add(borrowedBook)
                                .addOnSuccessListener(documentReference ->
                                        Toast.makeText(BookDetailActivity.this, "Book borrowed successfully!", Toast.LENGTH_SHORT).show()
                                )
                                .addOnFailureListener(e ->
                                        Toast.makeText(BookDetailActivity.this, "Failed to borrow book: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                );
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(BookDetailActivity.this, "Error checking borrowed books: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

}
