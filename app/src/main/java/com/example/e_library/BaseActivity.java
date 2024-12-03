package com.example.e_library;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    private TabManager tabManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        // Tìm các TextView của từng tab
        TextView homeTab = findViewById(R.id.homeTab);
        TextView booksTab = findViewById(R.id.booksTab);
        TextView readersTab = findViewById(R.id.readersTab);
        TextView settingsTab = findViewById(R.id.settingsTab);

        // Khởi tạo TabManager
        tabManager = new TabManager(this);
        tabManager.setupTab(homeTab, booksTab, readersTab, settingsTab);
    }

    // Phương thức trừu tượng để các lớp con cung cấp layout của riêng mình
    protected abstract int getLayoutResourceId();
}