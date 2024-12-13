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

public class BorrowingBookAdapter extends android.widget.ArrayAdapter<Book> {

    private final Context context;
    private final List<Book> borrowingBooks;

    public BorrowingBookAdapter(Context context, List<Book> borrowingBooks) {
        super(context, 0, borrowingBooks);
        this.context = context;
        this.borrowingBooks = borrowingBooks;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_borrowing_book, parent, false);
        }

        ImageView bookImage = convertView.findViewById(R.id.bookImage);
        TextView bookTitle = convertView.findViewById(R.id.bookTitle);
        TextView bookAuthor = convertView.findViewById(R.id.bookAuthor);

        // Get the current book
        Book book = borrowingBooks.get(position);

        // Set data
        bookTitle.setText(book.getTitle());
        bookAuthor.setText(book.getAuthor());
        Glide.with(context).load(book.getImage()).into(bookImage);

        return convertView;
    }

}
