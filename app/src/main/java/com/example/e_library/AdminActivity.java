package com.example.e_library;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AdminActivity extends AppCompatActivity {

    private static final String TAG = "AdminActivity";

    private TextView qrResultText;
    private TextView userEmailText;
    private ListView borrowedBooksListView;
    private FirebaseFirestore db;
    private BorrowedBooksAdapter borrowedBooksAdapter;
    private List<Book> borrowedBooksList;
    private Button returnBtn;
    private Button lendBtn;
    private Button scanBtn;
    private Button toggleViewBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        scanBtn = findViewById(R.id.scanner);
        lendBtn = findViewById(R.id.lendButton);
        returnBtn = findViewById(R.id.returnButton);

        toggleViewBtn = findViewById(R.id.toggleView);
        qrResultText = findViewById(R.id.text);
        userEmailText = findViewById(R.id.userEmail);
        borrowedBooksListView = findViewById(R.id.borrowedBooksList);

        db = FirebaseFirestore.getInstance();
        borrowedBooksList = new ArrayList<>();
        borrowedBooksAdapter = new BorrowedBooksAdapter(this, borrowedBooksList);
        borrowedBooksListView.setAdapter(borrowedBooksAdapter);

        scanBtn.setOnClickListener(v -> {
            IntentIntegrator intentIntegrator = new IntentIntegrator(AdminActivity.this);
            intentIntegrator.setOrientationLocked(false);
            intentIntegrator.setPrompt("Quét mã QR");
            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            intentIntegrator.initiateScan();
        });

        returnBtn.setVisibility(View.GONE);
        lendBtn.setVisibility(View.GONE);
        toggleViewBtn.setVisibility(View.GONE);

        lendBtn.setOnClickListener(v -> lendSelectedBooks());

        returnBtn.setOnClickListener(v -> returnSelectedBooks());

        toggleViewBtn.setOnClickListener(v -> toggleViewMode());
    }

    private void lendSelectedBooks() {
        List<Book> selectedBooks = borrowedBooksAdapter.getSelectedBooks();

        if (selectedBooks.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn sách để mượn", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = qrResultText.getText().toString();
        AtomicInteger successCount = new AtomicInteger();

        for (Book book : selectedBooks) {
            db.collection("users").document(userId).collection("borrowed_books")
                    .document(book.getId()).delete()
                    .addOnSuccessListener(avoid -> {
                        db.collection("users").document(userId).collection("borrowing_books")
                                .document(book.getId()).set(book)
                                .addOnSuccessListener(avoid2 -> {
                                    successCount.getAndIncrement();
                                    if (successCount.get() == selectedBooks.size()) {
                                        Toast.makeText(this, "Đã cho mượn " + successCount + " sách thành công", Toast.LENGTH_SHORT).show();
                                        fetchBorrowedBooks(userId);

                                        borrowedBooksAdapter.clearSelection();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Lỗi khi cập nhật sách mượn", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "Lỗi khi cập nhật sách mượn", e);
                                });
                    });
        }
    }

    private void returnSelectedBooks() {
        List<Book> selectedBooks = borrowedBooksAdapter.getSelectedBooks();

        if (selectedBooks.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn sách để trả", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = qrResultText.getText().toString();
        AtomicInteger successCount = new AtomicInteger();

        for (Book book : selectedBooks) {
            db.collection("users").document(userId).collection("borrowing_books")
                    .document(book.getId()).delete()
                    .addOnSuccessListener(avoid -> {
                        db.collection("users").document(userId).collection("return_books")
                                .document(book.getId()).set(book)
                                .addOnSuccessListener(avoid2 -> {
                                    // Increment success count and show toast if all books processed
                                    successCount.getAndIncrement();
                                    if (successCount.get() == selectedBooks.size()) {
                                        Toast.makeText(this, "Đã trả " + successCount + " sách thành công", Toast.LENGTH_SHORT).show();
                                        fetchBorrowingBooks(userId);

                                        borrowedBooksAdapter.clearSelection();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Lỗi khi cập nhật sách trả", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "Lỗi khi cập nhật sách trả", e);
                                });
                    });
        }
    }

    private boolean isBorrowedBooksView = true;

    private void toggleViewMode() {
        String userId = qrResultText.getText().toString();
        if (isBorrowedBooksView) {
            fetchBorrowingBooks(userId);
            returnBtn.setVisibility(View.VISIBLE);
            lendBtn.setVisibility(View.GONE);
            ((Button) findViewById(R.id.toggleView)).setText("Xem danh sách đăng ký");
        } else {
            fetchBorrowedBooks(userId);
            returnBtn.setVisibility(View.GONE);
            lendBtn.setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.toggleView)).setText("Xem danh sách đang mượn");
        }
        isBorrowedBooksView = !isBorrowedBooksView;
    }

    private void fetchBorrowingBooks(String userId) {
        db.collection("users").document(userId).collection("borrowing_books").get()
                .addOnSuccessListener(querySnapshot -> {
                    borrowedBooksList.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Book book = doc.toObject(Book.class);
                        borrowedBooksList.add(book);
                    }
                    borrowedBooksAdapter.notifyDataSetChanged();

                    if (borrowedBooksList.isEmpty()) {
                        Toast.makeText(this, "Không có sách đang mượn", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi tìm sách mượn", e);
                    Toast.makeText(this, "Không thể tải danh sách sách mượn", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            String scannedId = result.getContents();
            if (scannedId != null) {
                qrResultText.setText(scannedId);
                fetchUserData(scannedId);
                lendBtn.setVisibility(View.VISIBLE);
                toggleViewBtn.setVisibility(View.VISIBLE);
                userEmailText.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Không thể quét mã QR", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchUserData(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String email = documentSnapshot.getString("email");
                        userEmailText.setText("Email: " + email);

                        // Fetch borrowed books
                        fetchBorrowedBooks(userId);
                    } else {
                        Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi lấy dữ liệu người dùng", e);
                    Toast.makeText(this, "Không thể tải thông tin người dùng", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchBorrowedBooks(String userId) {
        db.collection("users").document(userId).collection("borrowed_books").get()
                .addOnSuccessListener(querySnapshot -> {
                    borrowedBooksList.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Book book = doc.toObject(Book.class);
                        if (book != null) {
                            book.setId(doc.getId()); // Gán ID của tài liệu từ Firestore
                            borrowedBooksList.add(book);
                        }
                    }
                    borrowedBooksAdapter.notifyDataSetChanged();

                    if (borrowedBooksList.isEmpty()) {
                        Toast.makeText(this, "Không có sách đã mượn", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi tìm sách đã mượn", e);
                    Toast.makeText(this, "Không thể tải danh sách sách đã mượn", Toast.LENGTH_SHORT).show();
                });
    }
}