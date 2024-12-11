package com.example.e_library;

import android.os.Bundle;
import android.util.Log;
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
    private List<BorrowedBook> borrowedBooksList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Button scanBtn = findViewById(R.id.scanner);
        Button lendBtn = findViewById(R.id.lendButton);
        Button returnBtn = findViewById(R.id.returnButton);
        Button toggleViewBtn = findViewById(R.id.toggleView);
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

        lendBtn.setOnClickListener(v -> lendSelectedBooks());

        returnBtn.setOnClickListener(v -> returnSelectedBooks());

        toggleViewBtn.setOnClickListener(v -> toggleViewMode());
    }

    private void lendSelectedBooks() {
        List<BorrowedBook> selectedBooks = borrowedBooksAdapter.getSelectedBooks();

        if (selectedBooks.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn sách để mượn", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = qrResultText.getText().toString().replace("Scanned ID: ", "");
        AtomicInteger successCount = new AtomicInteger();

        for (BorrowedBook book : selectedBooks) {
            db.collection("users").document(userId).collection("borrowed_books")
                    .document(book.getId()).delete()
                    .addOnSuccessListener(avoid -> {
                        db.collection("users").document(userId).collection("borrowing_books")
                                .document(book.getId()).set(book)
                                .addOnSuccessListener(avoid2 -> {
                                    // Increment success count and show toast if all books processed
                                    // Note: This is a simplified approach and might need more robust synchronization
                                    successCount.getAndIncrement();
                                    if (successCount.get() == selectedBooks.size()) {
                                        Toast.makeText(this, "Đã cho mượn " + successCount + " sách thành công", Toast.LENGTH_SHORT).show();
                                        fetchBorrowedBooks(userId);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Lỗi khi cập nhật sách mượn", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "Lỗi khi cập nhật sách mượn", e);
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi khi xóa sách khỏi danh sách", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Lỗi khi xóa sách khỏi danh sách", e);
                    });
        }
    }

    private void returnSelectedBooks() {
        List<BorrowedBook> selectedBooks = borrowedBooksAdapter.getSelectedBooks();

        if (selectedBooks.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn sách để trả", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = qrResultText.getText().toString().replace("Scanned ID: ", "");
        AtomicInteger successCount = new AtomicInteger();

        for (BorrowedBook book : selectedBooks) {
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
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Lỗi khi cập nhật sách trả", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "Lỗi khi cập nhật sách trả", e);
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi khi xóa sách khỏi danh sách mượn", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Lỗi khi xóa sách khỏi danh sách mượn", e);
                    });
        }
    }

    private boolean isBorrowedBooksView = true;

    private void toggleViewMode() {
        String userId = qrResultText.getText().toString().replace("Scanned ID: ", "");
        if (isBorrowedBooksView) {
            fetchBorrowingBooks(userId);
            ((Button) findViewById(R.id.toggleView)).setText("Chuyển sang Sách đã mượn");
        } else {
            fetchBorrowedBooks(userId);
            ((Button) findViewById(R.id.toggleView)).setText("Chuyển sang Mượn sách");
        }
        isBorrowedBooksView = !isBorrowedBooksView;
    }

    private void fetchBorrowingBooks(String userId) {
        db.collection("users").document(userId).collection("borrowing_books").get()
                .addOnSuccessListener(querySnapshot -> {
                    borrowedBooksList.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        BorrowedBook book = doc.toObject(BorrowedBook.class);
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
                qrResultText.setText("Scanned ID: " + scannedId);
                fetchUserData(scannedId);
            } else {
                qrResultText.setText("Không tìm thấy kết quả");
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
                        BorrowedBook book = doc.toObject(BorrowedBook.class);
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