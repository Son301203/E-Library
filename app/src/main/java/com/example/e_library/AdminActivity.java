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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

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
            intentIntegrator.setPrompt("Scan a QR Code");
            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            intentIntegrator.initiateScan();
        });

        lendBtn.setOnClickListener(v -> lendSelectedBooks());

        toggleViewBtn.setOnClickListener(v -> toggleViewMode());
    }

    private void lendSelectedBooks() {
        List<BorrowedBook> selectedBooks = borrowedBooksAdapter.getSelectedBooks();
        String userId = qrResultText.getText().toString().replace("Scanned ID: ", "");

        for (BorrowedBook book : selectedBooks) {
            db.collection("users").document(userId).collection("borrowed_books")
                    .document(book.getId()).delete();

            db.collection("users").document(userId).collection("borrowing_books")
                    .document(book.getId()).set(book);
        }

        fetchBorrowedBooks(userId);
    }

    private boolean isBorrowedBooksView = true;

    private void toggleViewMode() {
        String userId = qrResultText.getText().toString().replace("Scanned ID: ", "");
        if (isBorrowedBooksView) {
            fetchBorrowingBooks(userId);
            ((Button) findViewById(R.id.toggleView)).setText("Toggle to Borrowed Books");
        } else {
            fetchBorrowedBooks(userId);
            ((Button) findViewById(R.id.toggleView)).setText("Toggle to Borrowing Books");
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
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching borrowing books", e));
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
                qrResultText.setText("No result found");
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
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching user data", e));
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
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching borrowed books", e));
    }
}
