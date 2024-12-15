package com.example.e_library;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private TextView borrowedBooksCount;
    private TextView borrowingBooksCount;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText searchEditText;
    private ListView searchResultsListView;
    private BookList searchResultsAdapter;
    private List<Book> searchResultsList = new ArrayList<>();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Khởi tạo Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Ánh xạ các view
        LinearLayout registeredBooksLayout = findViewById(R.id.registeredBooksLayout);
        LinearLayout borrowingBooksLayout = findViewById(R.id.borrowingBooks);
        LinearLayout returnBookLayout = findViewById(R.id.returnBook);
        borrowedBooksCount = findViewById(R.id.borrowedBooksCount);
        borrowingBooksCount = findViewById(R.id.borrowingBooksCount);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        searchEditText = findViewById(R.id.searchEditText);
        searchResultsListView = findViewById(R.id.searchResultsListView);
        progressBar = findViewById(R.id.progressBar);

        // Thiết lập adapter cho danh sách tìm kiếm
        searchResultsAdapter = new BookList(this, searchResultsList);
        searchResultsListView.setAdapter(searchResultsAdapter);

        // Xử lý sự kiện tìm kiếm
        setupSearch();

        // Thiết lập sự kiện click cho các layout
        setupLayoutClickListeners(registeredBooksLayout, borrowingBooksLayout, returnBookLayout);

        // Thiết lập SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchBorrowedBooksCount();
            fetchBorrowingBooksCount();
            swipeRefreshLayout.setRefreshing(false);
        });

        // Thiết lập sự kiện cho biểu tượng tài khoản
        ImageView accountIcon = findViewById(R.id.accountIcon);
        accountIcon.setOnClickListener(v -> {
            if (auth.getCurrentUser() != null) {
                Intent intent = new Intent(HomeActivity.this, UserInfoActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(HomeActivity.this, "Bạn cần đăng nhập trước!", Toast.LENGTH_SHORT).show();
            }
        });

        // Thiết lập tab
        TabUtils.setupTabs(this);

        // Lấy dữ liệu ban đầu
        fetchBorrowedBooksCount();
        fetchBorrowingBooksCount();
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (!query.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    performSearch(query);
                } else {
                    searchResultsList.clear();
                    searchResultsAdapter.notifyDataSetChanged();
                    searchResultsListView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupLayoutClickListeners(LinearLayout registeredBooksLayout, LinearLayout borrowingBooksLayout, LinearLayout returnBookLayout) {
        if (registeredBooksLayout != null) {
            registeredBooksLayout.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, RegisteredBooksActivity.class);
                startActivity(intent);
            });
        }

        if (borrowingBooksLayout != null) {
            borrowingBooksLayout.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, BorrowingBookActivity.class);
                startActivity(intent);
            });
        }

        if (returnBookLayout != null) {
            returnBookLayout.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, BookReturnActivity.class);
                startActivity(intent);
            });
        }
    }

    private void performSearch(String query) {
        db.collection("books")
                .get() // Tải toàn bộ dữ liệu
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    searchResultsList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Book book = document.toObject(Book.class);

                        // Kiểm tra nếu tiêu đề chứa ký tự hoặc chuỗi
                        if (book.getTitle().toLowerCase().contains(query.toLowerCase())) {
                            searchResultsList.add(book);
                        }
                    }

                    updateSearchResults(query);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tìm kiếm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void fetchBorrowedBooksCount() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Bạn cần đăng nhập trước!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId)
                .collection("borrowed_books")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int count = queryDocumentSnapshots.size();
                    borrowedBooksCount.setText(String.valueOf(count));
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi khi lấy số lượng sách đã mượn: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void fetchBorrowingBooksCount() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Bạn cần đăng nhập trước!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId)
                .collection("borrowing_books")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int count = queryDocumentSnapshots.size();
                    borrowingBooksCount.setText(String.valueOf(count));
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi khi lấy số lượng sách đang mượn: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
    private void updateSearchResults(String query) {
        if (!searchResultsList.isEmpty()) {
            searchResultsAdapter.notifyDataSetChanged();
            searchResultsListView.setVisibility(View.VISIBLE);
        } else {
            // Thử tìm kiếm theo tác giả nếu không tìm thấy kết quả theo tiêu đề
            db.collection("books")
                    .whereGreaterThanOrEqualTo("author", query)
                    .whereLessThanOrEqualTo("author", query + "\uf8ff")
                    .get()
                    .addOnSuccessListener(authorQueryDocumentSnapshots -> {
                        searchResultsList.clear();
                        for (QueryDocumentSnapshot document : authorQueryDocumentSnapshots) {
                            Book book = document.toObject(Book.class);
                            searchResultsList.add(book);
                        }

                        if (!searchResultsList.isEmpty()) {
                            searchResultsAdapter.notifyDataSetChanged();
                            searchResultsListView.setVisibility(View.VISIBLE);
                        } else {
                            searchResultsListView.setVisibility(View.GONE);
                            Toast.makeText(HomeActivity.this, "Không tìm thấy sách", Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    });
        }
        progressBar.setVisibility(View.GONE);
    }
}
