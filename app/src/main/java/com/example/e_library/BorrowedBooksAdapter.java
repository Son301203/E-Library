package com.example.e_library;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BorrowedBooksAdapter extends android.widget.ArrayAdapter<Book> {

    private final Context context;
    private final List<Book> borrowedBooks;
    private final SparseBooleanArray selectedBooks;

    public BorrowedBooksAdapter(Context context, List<Book> borrowedBooks) {
        super(context, 0, borrowedBooks);
        this.context = context;
        this.borrowedBooks = borrowedBooks;
        this.selectedBooks = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_borrowed_book, parent, false);
        }

        // Bind views
        CheckBox bookCheckBox = convertView.findViewById(R.id.bookCheckBox);
        ImageView bookImage = convertView.findViewById(R.id.bookImage);
        TextView bookTitle = convertView.findViewById(R.id.bookTitle);
        TextView bookAuthor = convertView.findViewById(R.id.bookAuthor);
        TextView borrowedAt = convertView.findViewById(R.id.borrowedAt);

        // Get the current book
        Book book = borrowedBooks.get(position);

        // Set data
        bookTitle.setText(book.getTitle());
        bookAuthor.setText(book.getAuthor());
        Glide.with(context).load(book.getImage()).into(bookImage);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        borrowedAt.setText("Borrowed on: " + dateFormat.format(new Date(book.getBorrowedAt())));

        // CheckBox state
        bookCheckBox.setOnCheckedChangeListener(null);
        bookCheckBox.setChecked(selectedBooks.get(position, false));
        bookCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> selectedBooks.put(position, isChecked));

        return convertView;
    }

    public List<Book> getSelectedBooks() {
        List<Book> selected = new ArrayList<>();
        for (int i = 0; i < borrowedBooks.size(); i++) {
            if (selectedBooks.get(i, false)) {
                selected.add(borrowedBooks.get(i));
            }
        }
        return selected;
    }

    public void clearSelection() {
        selectedBooks.clear(); // Xóa tất cả các trạng thái checkbox được chọn
        notifyDataSetChanged(); // Cập nhật giao diện
    }

}
