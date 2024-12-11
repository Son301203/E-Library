package com.example.e_library;

import android.app.Activity;
import android.widget.LinearLayout;

public class TabUtils {
    public static void setupTabs(Activity activity) {
        // Tìm các LinearLayout của từng tab
        LinearLayout homeTab = activity.findViewById(R.id.home_tab);
        LinearLayout booksTab = activity.findViewById(R.id.book_tab);
        LinearLayout booksTabMenu = activity.findViewById(R.id.book_tab_menu);
        LinearLayout bookLoanTab = activity.findViewById(R.id.book_loan_tab);
        LinearLayout bookReturnTab = activity.findViewById(R.id.book_return_tab);
        LinearLayout settingsTab = activity.findViewById(R.id.settings_tab);

        // Các LinearLayout mới thêm vào cho "Xem gần đây"
        LinearLayout recentlyViewedBookTabMenu = activity.findViewById(R.id.recentlyViewedBookTabMenu); // Thêm ID cho "Sách"
        LinearLayout recentlyViewedRegisteredBooksLayout = activity.findViewById(R.id.recentlyViewedRegisteredBooksLayout); // Thêm ID cho "Sách đã đăng ký"
        LinearLayout recentlyViewedBorrowingBooks = activity.findViewById(R.id.recentlyViewedBorrowingBooks); // Thêm ID cho "Sách đang mượn"

        // Khởi tạo TabManager
        TabManager tabManager = new TabManager(activity);

        // Kiểm tra null trước khi gọi setupTab
        tabManager.setupTab(homeTab, booksTab, booksTabMenu, bookLoanTab, bookReturnTab, settingsTab
                , recentlyViewedBookTabMenu, recentlyViewedRegisteredBooksLayout, recentlyViewedBorrowingBooks);
    }
}
