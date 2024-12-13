package com.example.e_library;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import java.util.List;

public class BookList extends BaseAdapter {

    private Context context;
    private List<Book> bookList;

    public BookList(Context context, List<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    @Override
    public int getCount() {
        return bookList.size();
    }

    @Override
    public Object getItem(int position) {
        return bookList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.book_list, parent, false);
        }

        Book book = bookList.get(position);

        // Bind views
        TextView bookTitle = convertView.findViewById(R.id.bookTitle);
        TextView bookAuthor = convertView.findViewById(R.id.bookAuthor);
        ImageView bookImage = convertView.findViewById(R.id.bookImage);

        bookTitle.setText(book.getTitle());
        bookAuthor.setText(book.getAuthor());

        // Load image using Glide
        Glide.with(context)
                .load(book.getImage())
                .into(bookImage);

        // Set onClickListener for item
        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookDetailActivity.class);
            intent.putExtra("title", book.getTitle());
            intent.putExtra("author", book.getAuthor());
            intent.putExtra("description", book.getDescription());
            intent.putExtra("publisher", book.getPublisher());
            intent.putExtra("yearRelease", book.getYearRelease());
            intent.putExtra("image", book.getImage());
            context.startActivity(intent);
        });

        return convertView;
    }


}
