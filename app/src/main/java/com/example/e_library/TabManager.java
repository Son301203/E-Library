package com.example.e_library;

import android.content.Context;
import android.content.Intent;
import android.widget.LinearLayout;

public class TabManager {

    private final Context context;

    public TabManager(Context context) {
        this.context = context;
    }

    public void setupTab(LinearLayout homeTab, LinearLayout booksTab, LinearLayout booksTabMenu, LinearLayout bookLoanTab, LinearLayout bookReturnTab, LinearLayout settingsTab, LinearLayout recentlyViewedBookTabMenu, LinearLayout recentlyViewedRegisteredBooksLayout, LinearLayout recentlyViewedBorrowingBooks) {
        // Home Tab
        if (homeTab != null) {
            homeTab.setOnClickListener(v -> {
                Intent intent = new Intent(context, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
            });
        }

        // Books Tab
        if (booksTab != null) {
            booksTab.setOnClickListener(v -> {
                Intent intent = new Intent(context, BookListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
            });
        }
        // Books Tab
        if (booksTabMenu != null) {
            booksTabMenu.setOnClickListener(v -> {
                Intent intent = new Intent(context, BookListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
            });
        }

        // recentlyViewedBookTabMenu
        if (recentlyViewedBookTabMenu != null) {
            recentlyViewedBookTabMenu.setOnClickListener(v -> {
                Intent intent = new Intent(context, BookListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
            });
        }
        // recentlyViewedRegisteredBooksLayout
        if (recentlyViewedRegisteredBooksLayout != null) {
            recentlyViewedRegisteredBooksLayout.setOnClickListener(v -> {
                Intent intent = new Intent(context, RegisteredBooksActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
            });
        }


        // Book Loan Tab
        if (bookLoanTab != null) {
            bookLoanTab.setOnClickListener(v -> {
                Intent intent = new Intent(context, BorrowingBookActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
            });
        }

        //recentlyViewedBorrowingBooks
        if (recentlyViewedBorrowingBooks != null) {
            recentlyViewedBorrowingBooks.setOnClickListener(v -> {
                Intent intent = new Intent(context, BorrowingBookActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
            });
        }

        // Book Return Tab
        if (bookReturnTab != null) {
            bookReturnTab.setOnClickListener(v -> {
                Intent intent = new Intent(context, BookReturnActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
            });
        }

        // Settings Tab
        if (settingsTab != null) {
            settingsTab.setOnClickListener(v -> {
                Intent intent = new Intent(context, SettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
            });
        }
    }
}
