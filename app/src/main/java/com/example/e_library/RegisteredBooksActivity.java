package com.example.e_library;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RegisteredBooksActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ListView booksListView;
    private BorrowedBooksAdapter adapter;
    private List<BorrowedBook> borrowedBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered_books);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        booksListView = findViewById(R.id.booksListView);
        borrowedBooks = new ArrayList<>();
        adapter = new BorrowedBooksAdapter(this, borrowedBooks);
        booksListView.setAdapter(adapter);

        Button unregisterButton = findViewById(R.id.unregisterButton);
        unregisterButton.setOnClickListener(v -> unregisterSelectedBooks());

        fetchBorrowedBooks();
    }

    private void unregisterSelectedBooks() {
        List<BorrowedBook> selectedBooks = adapter.getSelectedBooks();
        String userId = auth.getCurrentUser().getUid();

        for (BorrowedBook book : selectedBooks) {
            db.collection("users")
                    .document(userId)
                    .collection("borrowed_books")
                    .document(book.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Book unregistered: " + book.getTitle(), Toast.LENGTH_SHORT).show();
                        borrowedBooks.remove(book);
                        adapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to unregister: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void fetchBorrowedBooks() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId)
                .collection("borrowed_books")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    borrowedBooks.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        BorrowedBook book = document.toObject(BorrowedBook.class);
                        book.setId(document.getId()); // Gán ID của tài liệu từ Firestore
                        borrowedBooks.add(book);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load borrowed books: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
