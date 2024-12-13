package com.example.e_library;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserInfoActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private TextView emailTextView, usernameTextView, fullNameTextView, genderTextView, phoneNumberTextView, dateOfBirthTextView, idVerificationTextView;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        ImageView backIcon = findViewById(R.id.backIcon);
        emailTextView = findViewById(R.id.emailTextView);
        usernameTextView = findViewById(R.id.usernameTextView);
        logoutButton = findViewById(R.id.logoutButton);
        fullNameTextView = findViewById(R.id.fullNameTextView);
        genderTextView = findViewById(R.id.genderTextView);
        phoneNumberTextView = findViewById(R.id.phoneNumberTextView);
        dateOfBirthTextView = findViewById(R.id.dobTextView);
        idVerificationTextView = findViewById(R.id.idVerificationTextView);
        // Nút Back
        backIcon.setOnClickListener(view -> finish());

        // Fetch and display user information
        fetchUserInfo();

        // Logout functionality
        logoutButton.setOnClickListener(v -> {
            auth.signOut();
            Toast.makeText(UserInfoActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UserInfoActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void fetchUserInfo() {
        String userId = auth.getCurrentUser().getUid();

        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String email = documentSnapshot.getString("email");
                        String username = documentSnapshot.getString("user_name");
                        String fullName = documentSnapshot.getString("fullname");
                        String phoneNumber = documentSnapshot.getString("phone_number");
                        String dateOfBirth = documentSnapshot.getString("date_of_birth");
                        String gender = documentSnapshot.getString("gender");
                        String idVerification = documentSnapshot.getString("ID_verification");

                        usernameTextView.setText(username);
                        emailTextView.setText(email);
                        fullNameTextView.setText(fullName);
                        phoneNumberTextView.setText(phoneNumber);
                        dateOfBirthTextView.setText(dateOfBirth);
                        idVerificationTextView.setText(idVerification);
                        genderTextView.setText(gender);
                    } else {
                        Toast.makeText(this, "Không tìm thấy dữ liệu người dùng!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi tìm nạp thông tin người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
