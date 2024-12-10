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

        // Kiểm tra trong danh sách borrowing_books
        db.collection("users").document(userId)
                .collection("borrowing_books")
                .whereEqualTo("title", title)
                .whereEqualTo("author", author)
                .get()
                .addOnSuccessListener(borrowingSnapshots -> {
                    if (!borrowingSnapshots.isEmpty()) {
                        // Nếu sách đã có trong borrowing_books
                        Toast.makeText(BookDetailActivity.this, "Bạn đang mượn sách này rồi!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Tiếp tục kiểm tra trong borrowed_books
                        db.collection("users").document(userId)
                                .collection("borrowed_books")
                                .whereEqualTo("title", title)
                                .whereEqualTo("author", author)
                                .get()
                                .addOnSuccessListener(borrowedSnapshots -> {
                                    if (!borrowedSnapshots.isEmpty()) {
                                        // Nếu sách đã có trong borrowed_books
                                        Toast.makeText(BookDetailActivity.this, "Bạn đã đăng ký mượn sách này trước đó!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Sách chưa được đăng ký, thêm vào borrowed_books
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
                                                        Toast.makeText(BookDetailActivity.this, "Đăng ký mượn sách thành công!", Toast.LENGTH_SHORT).show()
                                                )
                                                .addOnFailureListener(e ->
                                                        Toast.makeText(BookDetailActivity.this, "Lỗi khi đăng ký mượn sách: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                                );
                                    }
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(BookDetailActivity.this, "Lỗi kiểm tra danh sách borrowed_books: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                );
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(BookDetailActivity.this, "Lỗi kiểm tra danh sách borrowing_books: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

}
