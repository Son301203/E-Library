package com.example.e_library;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BorrowedBooksAdapter extends android.widget.ArrayAdapter<BorrowedBook> {

    private final Context context;
    private final List<BorrowedBook> borrowedBooks;

    public BorrowedBooksAdapter(Context context, List<BorrowedBook> borrowedBooks) {
        super(context, 0, borrowedBooks);
        this.context = context;
        this.borrowedBooks = borrowedBooks;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_borrowed_book, parent, false);
        }

        // Bind views
        ImageView bookImage = convertView.findViewById(R.id.bookImage);
        TextView bookTitle = convertView.findViewById(R.id.bookTitle);
        TextView bookAuthor = convertView.findViewById(R.id.bookAuthor);
        TextView borrowedAt = convertView.findViewById(R.id.borrowedAt);

        // Get the current book
        BorrowedBook book = borrowedBooks.get(position);

        // Set data
        bookTitle.setText(book.getTitle());
        bookAuthor.setText(book.getAuthor());
        Glide.with(context).load(book.getImage()).into(bookImage);

        // Format and display the borrowed date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        borrowedAt.setText("Borrowed on: " + dateFormat.format(new Date(book.getBorrowedAt())));

        return convertView;
    }
}
