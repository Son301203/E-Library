package com.example.e_library;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private TextView profileUsername;
    private ImageView logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings); // Đảm bảo layout đúng
        TabUtils.setupTabs(this);
        setupListeners();
        setupSwitchColors(); // Gọi hàm thiết lập màu cho Switch

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        profileUsername = findViewById(R.id.profileUsername);
        logoutButton = findViewById((R.id.logoutBtn));

        fetchUserInfo();

        logoutButton.setOnClickListener(v -> {
            auth.signOut();
            Toast.makeText(SettingActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SettingActivity.this, MainActivity.class);
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
                        String username = documentSnapshot.getString("fullname");
                        profileUsername.setText(username);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error fetching user info: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    /**
     * Hàm thiết lập sự kiện click và các lắng nghe khác
     */
    private void setupListeners() {

        // Tìm Switch thông báo và xử lý sự kiện bật/tắt
        Switch switchNotification = findViewById(R.id.switchNotification);
        switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(this, "Thông báo đã được bật", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Thông báo đã được tắt", Toast.LENGTH_SHORT).show();
            }
        });

        TextView changeInfo = findViewById(R.id.changeInfo);
        changeInfo.setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, ChangeUserInfoActivity.class);
            startActivity(intent);
        });


        // Nhấn vào "About"
        TextView tvAbout = findViewById(R.id.tvAbout);
        tvAbout.setOnClickListener(v ->
                Toast.makeText(this, "About clicked", Toast.LENGTH_SHORT).show()
        );

        Button btnCreateQr = findViewById(R.id.btnCreateQr);
        btnCreateQr.setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, GeneratorFragment.class);
            startActivity(intent);
        });
    }

    /**
     * Thiết lập màu cho Switch
     */
    private void setupSwitchColors() {
        // Tìm Switch thông báo
        Switch switchNotification = findViewById(R.id.switchNotification);

        // Tạo ColorStateList cho thumb (nút tròn)
        ColorStateList thumbStates = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked}, // Khi bật
                        new int[]{} // Khi tắt
                },
                new int[]{
                        Color.parseColor("#62A6F1"), // Màu khi bật
                        Color.WHITE // Màu khi tắt
                }
        );

        // Tạo ColorStateList cho track (thanh nền)
        ColorStateList trackStates = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked}, // Khi bật
                        new int[]{} // Khi tắt
                },
                new int[]{
                        Color.parseColor("#D6E9FF"), // Màu khi bật
                        Color.parseColor("#808080") // Màu khi tắt
                }
        );

        // Áp dụng màu cho Switch
        switchNotification.setThumbTintList(thumbStates);
        switchNotification.setTrackTintList(trackStates);
    }
}
