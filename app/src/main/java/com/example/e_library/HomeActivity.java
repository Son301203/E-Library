package com.example.e_library;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private TextView borrowedBooksCount;
    private TextView borrowingBooksCount;
    private SwipeRefreshLayout swipeRefreshLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); // Thêm dòng này trước khi findViewById
        LinearLayout registeredBooksLayout = findViewById(R.id.registeredBooksLayout);
        LinearLayout borrowingBooksLayout = findViewById(R.id.borrowingBooks);
        LinearLayout returnBookLayout = findViewById(R.id.returnBook);


        // Add the correct ID
        registeredBooksLayout.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, RegisteredBooksActivity.class);
            startActivity(intent);
        });

        borrowingBooksLayout.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, BorrowingBookActivity.class);
            startActivity(intent);
        });

        returnBookLayout.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, BookReturnActivity.class);
            startActivity(intent);
        });



        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        borrowedBooksCount = findViewById(R.id.borrowedBooksCount);
        borrowingBooksCount = findViewById(R.id.borrowingBooksCount);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchBorrowedBooksCount();
            swipeRefreshLayout.setRefreshing(false);
        });



        ImageView accountIcon = findViewById(R.id.accountIcon);
        accountIcon.setOnClickListener(v -> {
            if (auth.getCurrentUser() != null) {
                Intent intent = new Intent(HomeActivity.this, UserInfoActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(HomeActivity.this, "You need to log in first!", Toast.LENGTH_SHORT).show();
            }
        });
        // Thiết lập tab
        TabUtils.setupTabs(this);
        // Fetch and display borrowed books count
        fetchBorrowedBooksCount();
        fetchBorrowingBooksCount();
    }

    private void fetchBorrowedBooksCount() {

        String userId = auth.getCurrentUser().getUid();

        // Access the "borrowed_books" collection for the current user
        db.collection("users").document(userId)
                .collection("borrowed_books")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int count = queryDocumentSnapshots.size();
                    borrowedBooksCount.setText(String.valueOf(count)); // Update the TextView
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to fetch borrowed books count: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void fetchBorrowingBooksCount() {
        String userId = auth.getCurrentUser().getUid();

        // Access the "borrowed_books" collection for the current user
        db.collection("users").document(userId)
                .collection("borrowing_books")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int count = queryDocumentSnapshots.size();
                    borrowingBooksCount.setText(String.valueOf(count)); // Update the TextView
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to fetch borrowed books count: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
