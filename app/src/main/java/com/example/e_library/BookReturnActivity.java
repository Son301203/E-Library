package com.example.e_library;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class BookReturnActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_return); // Ensure the layout is correct

        // Setting up tabs (using TabUtils to initialize the tabs for navigation)
        TabUtils.setupTabs(this);

    }
}
