package com.heymeowcat.codefest2021androidproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.heymeowcat.codefest2021androidproject.Model.Customers;
import com.heymeowcat.codefest2021androidproject.Model.News;

import org.json.JSONArray;

import java.util.List;
import java.util.Objects;

public class ViewMoreDetails extends AppCompatActivity {
    public String newsEventId;
    TextView title,description,distance,duration;

    News SelectedNews;
    Customers loggedCustomer;



    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_more_details);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        newsEventId = bundle.getString("NewsEventId");
        Button backBtn =findViewById(R.id.gobackBtn);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        backBtn.setOnClickListener(view -> finish());
        distance = findViewById(R.id.DistanceTextinAct);
        duration = findViewById(R.id.DurationTextinAct);






        db.collection("Customers")
                .whereEqualTo("email", user.getEmail()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Customers> customersList = queryDocumentSnapshots.toObjects(Customers.class);
            if (!customersList.isEmpty()) {

                loggedCustomer = customersList.get(0);
            }
        });


        if (newsEventId != null) {


            db.collection("NewsOrEvents").whereEqualTo("docid", newsEventId).get().addOnSuccessListener(queryDocumentSnapshots -> {
                List<News> newsList = queryDocumentSnapshots.toObjects(News.class);
                if (!newsList.isEmpty()) {

                    SelectedNews = newsList.get(0);
                    if (SelectedNews != null) {

                        title.setText(SelectedNews.getNews_EventTitle());
                        description.setText(SelectedNews.getNews_EventDescription());


                    }

                }


            });




        }

    }



    public void setDistance(String s){
        distance.setText("Distance\n"+s);
    }

    public void setDuration(String s){
        duration.setText("Duration\n"+s);
    }

}