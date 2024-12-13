package com.example.e_library;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BorrowingBookActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ListView booksListView;
    private BookAdapter adapter;
    private List<Book> borrowingBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrowing_book);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        booksListView = findViewById(R.id.booksListViewBorrowing);
        borrowingBooks = new ArrayList<>();
        adapter = new BookAdapter(this, borrowingBooks, BookAdapter.ViewType.BORROWING_BOOKS);
        booksListView.setAdapter(adapter);

        ImageView backIcon = findViewById(R.id.backIcon);
        backIcon.setOnClickListener(view -> finish());
        // Thiết lập tab
        TabUtils.setupTabs(this);


        fetchBorrowedBooks();
    }

    private void fetchBorrowedBooks() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId)
                .collection("borrowing_books")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    borrowingBooks.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Book book = document.toObject(Book.class);
                        borrowingBooks.add(book);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Không tải được sách mượn: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }


}
