package com.example.e_library;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class TabManager {

    private final Context context;

    public TabManager(Context context) {
        this.context = context;
    }

    public void setupTab(TextView homeTab, TextView booksTab, TextView readersTab, TextView settingsTab) {
        // Home Tab
        homeTab.setOnClickListener(v -> {
            // Always allow navigation to MainActivity, even if already on it
            Intent intent = new Intent(context, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);
        });

        // Books Tab
        booksTab.setOnClickListener(v -> {
            // Always allow navigation to BooksActivity, even if already on it
            Intent intent = new Intent(context, BookListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);
        });

        // Readers Tab
        readersTab.setOnClickListener(v -> {
            Toast.makeText(context, "Readers Tab Clicked!", Toast.LENGTH_SHORT).show();
            // TODO: Implement Readers Activity
        });

        // Settings Tab
        settingsTab.setOnClickListener(v -> {
            Toast.makeText(context, "Settings Tab Clicked!", Toast.LENGTH_SHORT).show();
            // TODO: Implement Settings Activity
        });
    }
}