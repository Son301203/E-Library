package com.example.e_library;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.*;

public class MainActivity extends AppCompatActivity {

    private ListView bookListView;
    private SearchView searchView;
    private BookList bookAdapter;
    private List<Book> bookList = new ArrayList<>();
    private List<Book> filteredList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bookListView = findViewById(R.id.bookListView);
        searchView = findViewById(R.id.searchView);

        // Initialize adapter and set to ListView
        bookAdapter = new BookList(this, filteredList); // Use filteredList for display
        bookListView.setAdapter(bookAdapter);

        // Fetch data from Firestore
        fetchBooksFromFirestore();

        // Set up search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterBooks(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterBooks(newText);
                return true;
            }
        });
    }

    private void fetchBooksFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("books")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    bookList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Book book = document.toObject(Book.class);
                        bookList.add(book);
                    }

                    Collections.sort(bookList, new Comparator<Book>() {
                        @Override
                        public int compare(Book book1, Book book2) {
                            return book1.getTitle().compareToIgnoreCase(book2.getTitle());
                        }
                    });

                    // Initially show all books
                    filteredList.clear();
                    filteredList.addAll(bookList);
                    bookAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error fetching data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void filterBooks(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(bookList); // Show all books if query is empty
        } else {
            for (Book book : bookList) {
                if (book.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(book);
                }
            }
        }
        bookAdapter.notifyDataSetChanged();
    }
}
