package com.example.e_library;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.*;

public class BookListActivity extends AppCompatActivity {

    private ListView bookListView;
    private SearchView searchView;
    private BookList bookAdapter;
    private List<Book> bookList = new ArrayList<>();
    private List<Book> filteredList = new ArrayList<>();

    private boolean isAscending = true; // Biến kiểm tra hướng sắp xếp (true = A đến Z, false = Z đến A)

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        bookListView = findViewById(R.id.bookListView);
        searchView = findViewById(R.id.searchView);

        ImageView backIcon = findViewById(R.id.backIcon);
        ImageView searchIcon = findViewById(R.id.searchIcon);
        TextView titleText = findViewById(R.id.titleText);
        ConstraintLayout parentLayout = findViewById(R.id.parentLayout);
        ImageView filterIcon = findViewById(R.id.filterIcon);  // Lọc icon

        // Nút Back
        backIcon.setOnClickListener(view -> finish());

        // Nút Search - Ẩn/Hiện SearchView khi nhấn
        searchIcon.setOnClickListener(view -> {
            if (searchView.getVisibility() == View.VISIBLE) {
                searchView.setVisibility(View.GONE); // Ẩn SearchView nếu đang hiển thị
            } else {
                searchView.setVisibility(View.VISIBLE); // Hiển thị SearchView nếu chưa hiển thị
            }
        });

        // Đặt sự kiện khi đóng SearchView
        searchView.setOnCloseListener(() -> {
            searchView.setVisibility(View.GONE);      // Ẩn SearchView
            return false;
        });

        // Thu nhỏ SearchView khi nhấn ra ngoài
        parentLayout.setOnTouchListener((v, event) -> {
            if (searchView.getVisibility() == View.VISIBLE && event.getAction() == MotionEvent.ACTION_DOWN) {
                // Đưa giao diện trở về trạng thái ban đầu
                searchView.setVisibility(View.GONE);      // Ẩn SearchView
            }
            return false; // Cho phép các sự kiện khác vẫn được xử lý
        });

        // Initialize adapter and set to ListView
        bookAdapter = new BookList(this, filteredList);
        bookListView.setAdapter(bookAdapter);

        // Fetch data from Firestore
        fetchBooksFromFirestore();

        // Set up search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterBooks(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterBooks(newText);
                return true;
            }
        });

        // Set up filter functionality
        filterIcon.setOnClickListener(v -> {
            toggleSortOrder(); // Lọc theo thứ tự A-Z hoặc Z-A
            sortBooks(); // Sắp xếp sách
        });
        // Thiết lập tab
        TabUtils.setupTabs(this);
    }

    private void fetchBooksFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("books")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    bookList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Book book = document.toObject(Book.class);
                        bookList.add(book);
                    }

                    // Sắp xếp danh sách sách theo tên
                    Collections.sort(bookList, new Comparator<Book>() {
                        @Override
                        public int compare(Book book1, Book book2) {
                            return book1.getTitle().compareToIgnoreCase(book2.getTitle());
                        }
                    });

                    // Hiển thị tất cả sách ban đầu
                    filteredList.clear();
                    filteredList.addAll(bookList);
                    bookAdapter.notifyDataSetChanged();

                    // Cập nhật số lượng sách
                    updateBookCount(); // Gọi hàm cập nhật số lượng sách
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi tìm nạp dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void toggleSortOrder() {
        // Chuyển đổi giữa A-Z và Z-A
        isAscending = !isAscending;
    }

    private void sortBooks() {
        // Sắp xếp danh sách sách theo tên (A-Z hoặc Z-A)
        if (isAscending) {
            Collections.sort(bookList, new Comparator<Book>() {
                @Override
                public int compare(Book book1, Book book2) {
                    return book1.getTitle().compareToIgnoreCase(book2.getTitle());
                }
            });
        } else {
            Collections.sort(bookList, new Comparator<Book>() {
                @Override
                public int compare(Book book1, Book book2) {
                    return book2.getTitle().compareToIgnoreCase(book1.getTitle());
                }
            });
        }

        // Cập nhật danh sách đã lọc
        filteredList.clear();
        filteredList.addAll(bookList);
        bookAdapter.notifyDataSetChanged();
    }


    private void updateBookCount() {
        TextView productCountTextView = findViewById(R.id.productCount); // Lấy TextView theo ID
        int bookCount = filteredList.size(); // Đếm số lượng sách trong danh sách đã lọc
        String countText = bookCount + " sản phẩm"; // Tạo chuỗi hiển thị số lượng sách
        productCountTextView.setText(countText); // Cập nhật vào TextView
    }


    private void filterBooks(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(bookList); // Show all books if query is empty
        } else {
            for (Book book : bookList) {
                if (book.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(book);
                }
            }
        }
        bookAdapter.notifyDataSetChanged();
    }

}
