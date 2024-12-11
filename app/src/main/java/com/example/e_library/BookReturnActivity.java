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
public class BookReturnActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ListView booksListView;
    private BookReturnAdapter adapter;
    private List<BorrowedBook> returnBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_return);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        booksListView = findViewById(R.id.booksListViewBorrowing);
        returnBook = new ArrayList<>();
        adapter = new BookReturnAdapter(this, returnBook);
        booksListView.setAdapter(adapter);

        ImageView backIcon = findViewById(R.id.backIcon);
        backIcon.setOnClickListener(view -> finish());
        // Thiết lập tab
        TabUtils.setupTabs(this);

        fetchReturnBooks();
    }

    private void fetchReturnBooks() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId)
                .collection("return_books")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    returnBook.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        BorrowedBook book = document.toObject(BorrowedBook.class);
//                        book.setId(document.getId()); // Gán ID của tài liệu từ Firestore
                        returnBook.add(book);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Không tải được sách mượn: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }


}
