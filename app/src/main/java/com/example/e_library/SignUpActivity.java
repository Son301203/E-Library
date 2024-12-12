package com.example.e_library;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private EditText emailInput, passwordInput, userInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        userInput = findViewById(R.id.userInput);

        Button signUpButton = findViewById(R.id.signUpButton);
        Button goToLoginButton = findViewById(R.id.goToLoginButton); // Nút quay lại đăng nhập

        signUpButton.setOnClickListener(v -> signUpUser());

        // Xử lý quay lại màn hình đăng nhập
        goToLoginButton.setOnClickListener(v -> {
            finish(); // Kết thúc màn hình đăng ký
        });
    }


    private void signUpUser() {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String username = userInput.getText().toString();

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Các trường không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = auth.getCurrentUser().getUid();
                        saveUserToFirestore(userId, email, password, username);
                        finish();
                    } else {
                        Toast.makeText(this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToFirestore(String userId, String email, String password, String username) {
        Map<String, Object> user = new HashMap<>();
        user.put("user_name", username); // Default name
        user.put("email", email);
        user.put("password", password);
        user.put("fullname", "");
        user.put("gender", "");
        user.put("ID_verification", "");
        user.put("date_of_birth", "");
        user.put("phone_number", "");

        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to Save User: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
