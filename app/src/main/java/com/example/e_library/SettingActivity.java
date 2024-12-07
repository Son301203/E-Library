package com.example.e_library;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings); // Đảm bảo layout đúng
        TabUtils.setupTabs(this);
        setupListeners();
        setupSwitchColors(); // Gọi hàm thiết lập màu cho Switch
    }

    /**
     * Hàm thiết lập sự kiện click và các lắng nghe khác
     */
    private void setupListeners() {
        // Nhấn vào "Account Settings"
        ImageView tvAccountSettings = findViewById(R.id.tvAccountSettings);
        tvAccountSettings.setOnClickListener(v ->
                Toast.makeText(this, "Account Settings clicked", Toast.LENGTH_SHORT).show()
        );

        // Tìm Switch thông báo và xử lý sự kiện bật/tắt
        Switch switchNotification = findViewById(R.id.switchNotification);
        switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(this, "Thông báo đã được bật", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Thông báo đã được tắt", Toast.LENGTH_SHORT).show();
            }
        });

        // Nhấn vào "About"
        TextView tvAbout = findViewById(R.id.tvAbout);
        tvAbout.setOnClickListener(v ->
                Toast.makeText(this, "About clicked", Toast.LENGTH_SHORT).show()
        );
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
