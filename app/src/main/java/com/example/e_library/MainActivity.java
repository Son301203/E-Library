package com.example.e_library;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText emailInput, passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);

        Button loginButton = findViewById(R.id.loginButton);
        Button goToSignUpButton = findViewById(R.id.goToSignUpButton);
        TextView forgotPasswordText = findViewById(R.id.forgotPasswordText);

        loginButton.setOnClickListener(v -> loginUser());
        goToSignUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
        forgotPasswordText.setOnClickListener(v -> {
            String email = emailInput.getText().toString();

            if (email.isEmpty()) {
                Toast.makeText(MainActivity.this, "Vui lòng nhập email của bạn!", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Đã gửi email đặt lại mật khẩu!", Toast.LENGTH_SHORT).show();

                            // Optionally, update Firestore with a placeholder or mark password as reset
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("users").whereEqualTo("email", email).get()
                                    .addOnSuccessListener(querySnapshot -> {
                                        if (!querySnapshot.isEmpty()) {
                                            querySnapshot.getDocuments().get(0).getReference()
                                                    .update("password", "RESET");
                                        }
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Error fetching user document: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        } else {
                            Toast.makeText(MainActivity.this, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
        // Thiết lập tab
        TabUtils.setupTabs(this);
    }

    private void loginUser() {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email và Mật khẩu là bắt buộc!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(email.equals("admin@gmail.com")) {
            startActivity(new Intent(MainActivity.this, AdminActivity.class));
            finish();
        }

        else {
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Đăng nhập thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

}
