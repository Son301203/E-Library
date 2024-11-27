package com.example.e_library;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class BookDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        // Bind views
        ImageView bookImage = findViewById(R.id.bookImage);
        TextView bookTitle = findViewById(R.id.bookTitle);
        TextView bookAuthor = findViewById(R.id.bookAuthor);
        TextView bookDescription = findViewById(R.id.bookDescription);
        TextView bookPublisher = findViewById(R.id.bookPublisher);
        TextView bookYearRelease = findViewById(R.id.bookYearRelease);

        // Get data from intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String author = intent.getStringExtra("author");
        String description = intent.getStringExtra("description");
        String publisher = intent.getStringExtra("publisher");
        String yearRelease = intent.getStringExtra("yearRelease");
        String imageUrl = intent.getStringExtra("image");

        // Set data to views
        bookTitle.setText(title);
        bookAuthor.setText(author);
        bookDescription.setText(description);
        bookPublisher.setText(publisher);
        bookYearRelease.setText(yearRelease);

        Glide.with(this)
                .load(imageUrl)// Optional placeholder
                .into(bookImage);
    }
}
