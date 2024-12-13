package com.example.e_library;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.*;

import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;

import java.util.*;

public class BookReturnActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ListView booksListView;
    private BookAdapter adapter;
    private List<Book> returnBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_return);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        booksListView = findViewById(R.id.booksListViewBorrowing);
        returnBook = new ArrayList<>();
        adapter = new BookAdapter(this, returnBook, BookAdapter.ViewType.RETURNED_BOOKS);
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

                    // Map để đếm số lượng sách trùng
                    Map<String, Integer> bookCountMap = new HashMap<>();
                    Map<String, Book> uniqueBooksMap = new HashMap<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Book book = document.toObject(Book.class);

                        // Tạo key duy nhất dựa trên title, author và publisher
                        String key = book.getTitle() + "|" + book.getAuthor() + "|" + book.getPublisher();

                        // Đếm số lượng sách
                        bookCountMap.put(key, bookCountMap.getOrDefault(key, 0) + 1);

                        // Chỉ lưu thông tin sách một lần
                        if (!uniqueBooksMap.containsKey(key)) {
                            uniqueBooksMap.put(key, book);
                        }
                    }

                    // Thêm sách vào danh sách hiển thị
                    for (String key : uniqueBooksMap.keySet()) {
                        Book book = uniqueBooksMap.get(key);
                        book.setCount(bookCountMap.get(key)); // Gán số lượng
                        returnBook.add(book);
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Không tải được sách mượn: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }


}
