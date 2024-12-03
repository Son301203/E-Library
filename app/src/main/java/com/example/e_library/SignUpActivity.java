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

        signUpButton.setOnClickListener(v -> signUpUser());
    }

    private void signUpUser() {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String username = userInput.getText().toString();

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Email, Password and Username are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = auth.getCurrentUser().getUid();
                        saveUserToFirestore(userId, email, password, username);
                        finish();
                    } else {
                        Toast.makeText(this, "Sign Up Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToFirestore(String userId, String email, String password, String username) {
        Map<String, Object> user = new HashMap<>();
        user.put("user_name", username); // Default name
        user.put("email", email);
        user.put("password", password);

        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "User Registered Successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to Save User: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
