package com.example.e_library;

import android.app.Activity;
import android.widget.LinearLayout;

public class TabUtils {
    public static void setupTabs(Activity activity) {
        // Tìm các LinearLayout của từng tab
        LinearLayout homeTab = activity.findViewById(R.id.home_tab);
        LinearLayout booksTab = activity.findViewById(R.id.book_tab);
        LinearLayout bookLoanTab = activity.findViewById(R.id.book_loan_tab);
        LinearLayout bookReturnTab = activity.findViewById(R.id.book_return_tab);
        LinearLayout settingsTab = activity.findViewById(R.id.settings_tab);

        // Khởi tạo TabManager
        TabManager tabManager = new TabManager(activity);

        // Kiểm tra null trước khi gọi setupTab
        tabManager.setupTab(homeTab, booksTab,bookLoanTab,bookReturnTab, settingsTab);
    }
}
