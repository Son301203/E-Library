package com.example.e_library;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ChangeUserInfoActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private TextView emailTextView, dateOfBirthTextView;
    private EditText fullNameEditText, phoneNumberEditText, idVerificationEditText;
    private CheckBox maleCheckBox, femaleCheckBox, otherCheckBox;
    private Button updateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_info);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        ImageView backIcon = findViewById(R.id.backIcon);
        emailTextView = findViewById(R.id.emailTextView);
        fullNameEditText = findViewById(R.id.fullNameEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        idVerificationEditText = findViewById(R.id.idVerificationEditText);
        dateOfBirthTextView = findViewById(R.id.dateOfBirthTextView);
        maleCheckBox = findViewById(R.id.maleCheckBox);
        femaleCheckBox = findViewById(R.id.femaleCheckBox);
        otherCheckBox = findViewById(R.id.otherCheckBox);
        updateButton = findViewById(R.id.updateBtn);

        // Back button functionality
        backIcon.setOnClickListener(view -> finish());

        // Fetch and display user information
        fetchUserInfo();

        // Date picker for Date of Birth
        dateOfBirthTextView.setOnClickListener(view -> showDatePicker());

        // Update button functionality
        updateButton.setOnClickListener(view -> updateUserInfo());
    }

    private void fetchUserInfo() {
        String userId = auth.getCurrentUser().getUid();

        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String email = documentSnapshot.getString("email");
                        String fullName = documentSnapshot.getString("fullname");
                        String phoneNumber = documentSnapshot.getString("phone_number");
                        String dateOfBirth = documentSnapshot.getString("date_of_birth");
                        String gender = documentSnapshot.getString("gender");
                        String idVerification = documentSnapshot.getString("ID_verification");

                        emailTextView.setText(email);
                        fullNameEditText.setText(fullName);
                        phoneNumberEditText.setText(phoneNumber);
                        dateOfBirthTextView.setText(dateOfBirth);
                        idVerificationEditText.setText(idVerification);

                        if ("Male".equalsIgnoreCase(gender)) {
                            maleCheckBox.setChecked(true);
                        } else if ("Female".equalsIgnoreCase(gender)) {
                            femaleCheckBox.setChecked(true);
                        } else if ("Other".equalsIgnoreCase(gender)) {
                            otherCheckBox.setChecked(true);
                        }
                    } else {
                        Toast.makeText(this, "User data not found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error fetching user info: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
            dateOfBirthTextView.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void updateUserInfo() {
        String userId = auth.getCurrentUser().getUid();
        String fullName = fullNameEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String idVerification = idVerificationEditText.getText().toString().trim();
        String dateOfBirth = dateOfBirthTextView.getText().toString();
        String gender = "";

        if (maleCheckBox.isChecked()) gender = "Male";
        if (femaleCheckBox.isChecked()) gender = "Female";
        if (otherCheckBox.isChecked()) gender = "Other";

        Map<String, Object> updates = new HashMap<>();
        updates.put("fullname", fullName);
        updates.put("phone_number", phoneNumber);
        updates.put("ID_verification", idVerification);
        updates.put("date_of_birth", dateOfBirth);
        updates.put("gender", gender);

        db.collection("users").document(userId)
                .update(updates)
                .addOnSuccessListener(
                        unused -> {
                            Toast.makeText(this, "User info updated successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ChangeUserInfoActivity.this, SettingActivity.class);
                            startActivity(intent);
                            finish();
                        })
                .addOnFailureListener(e -> Toast.makeText(this, "Error updating user info: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
