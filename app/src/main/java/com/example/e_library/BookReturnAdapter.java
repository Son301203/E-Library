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

public class BookReturnAdapter extends android.widget.ArrayAdapter<BorrowedBook> {

    private final Context context;
    private final List<BorrowedBook> bookReturn;

    public BookReturnAdapter(Context context, List<BorrowedBook> bookReturn) {
        super(context, 0, bookReturn);
        this.context = context;
        this.bookReturn = bookReturn;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_return_book, parent, false);
        }

        ImageView bookImage = convertView.findViewById(R.id.bookImage);
        TextView bookTitle = convertView.findViewById(R.id.bookTitle);
        TextView bookAuthor = convertView.findViewById(R.id.bookAuthor);
        TextView bookCount = convertView.findViewById(R.id.bookCount); // Thêm TextView hiển thị số lượng

        // Get the current book
        BorrowedBook book = bookReturn.get(position);

        // Set data
        bookTitle.setText(book.getTitle());
        bookAuthor.setText(book.getAuthor());
        Glide.with(context).load(book.getImage()).into(bookImage);

        // Hiển thị số lượng sách
        if (book.getCount() > 1) {
            bookCount.setVisibility(View.VISIBLE);
            bookCount.setText("x" + book.getCount());
        } else {
            bookCount.setVisibility(View.GONE);
        }

        return convertView;
    }


}
