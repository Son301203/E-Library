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

public class BookAdapter extends android.widget.ArrayAdapter<Book> {

    public enum ViewType {
        BORROWED_BOOKS,
        BORROWING_BOOKS,
        RETURNED_BOOKS
    }

    private final Context context;
    private final List<Book> books;
    private final ViewType viewType;
    private final SparseBooleanArray selectedBooks;

    public BookAdapter(Context context, List<Book> books, ViewType viewType) {
        super(context, 0, books);
        this.context = context;
        this.books = books;
        this.viewType = viewType;
        this.selectedBooks = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            int layoutId;
            switch (viewType) {
                case BORROWED_BOOKS:
                    layoutId = R.layout.item_borrowed_book;
                    break;
                case BORROWING_BOOKS:
                    layoutId = R.layout.item_borrowing_book;
                    break;
                case RETURNED_BOOKS:
                    layoutId = R.layout.item_return_book;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + viewType);
            }
            convertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        }

        // Bind views
        ImageView bookImage = convertView.findViewById(R.id.bookImage);
        TextView bookTitle = convertView.findViewById(R.id.bookTitle);
        TextView bookAuthor = convertView.findViewById(R.id.bookAuthor);
        TextView borrowedAt = convertView.findViewById(R.id.borrowedAt);
        CheckBox bookCheckBox = convertView.findViewById(R.id.bookCheckBox);
        TextView bookCount = convertView.findViewById(R.id.bookCount);

        // Get current book
        Book book = books.get(position);

        // Set common data
        bookTitle.setText(book.getTitle());
        bookAuthor.setText(book.getAuthor());
        Glide.with(context).load(book.getImage()).into(bookImage);

        // Specific data for each view type
        switch (viewType) {
            case BORROWED_BOOKS:
                if (borrowedAt != null) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
                    borrowedAt.setText("Borrowed on: " + dateFormat.format(new Date(book.getBorrowedAt())));
                }
                if (bookCheckBox != null) {
                    bookCheckBox.setOnCheckedChangeListener(null);
                    bookCheckBox.setChecked(selectedBooks.get(position, false));
                    bookCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> selectedBooks.put(position, isChecked));
                }
                break;

            case RETURNED_BOOKS:
                if (bookCount != null) {
                    if (book.getCount() > 1) {
                        bookCount.setVisibility(View.VISIBLE);
                        bookCount.setText("x" + book.getCount());
                    } else {
                        bookCount.setVisibility(View.GONE);
                    }
                }
                break;

            case BORROWING_BOOKS:
                // No additional fields for borrowing books
                break;
        }

        return convertView;
    }

    public List<Book> getSelectedBooks() {
        List<Book> selected = new ArrayList<>();
        for (int i = 0; i < books.size(); i++) {
            if (selectedBooks.get(i, false)) {
                selected.add(books.get(i));
            }
        }
        return selected;
    }

    public void clearSelection() {
        selectedBooks.clear();
        notifyDataSetChanged();
    }
}
