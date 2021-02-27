package com.heymeowcat.codefestadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button products = findViewById(R.id.addProducts);
        Button tickets = findViewById(R.id.ticketsBtn);
        Button newsevents = findViewById(R.id.NewsEventsBtn);
        products.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddProducts.class);
            startActivity(intent);
        });

        tickets.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Tickets.class);
            startActivity(intent);
        });

        newsevents.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NewsEvents.class);
            startActivity(intent);
        });


    }


}