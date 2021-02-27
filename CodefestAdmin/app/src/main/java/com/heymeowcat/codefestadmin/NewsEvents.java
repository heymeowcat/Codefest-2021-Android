package com.heymeowcat.codefestadmin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.heymeowcat.codefestadmin.Model.CustomLatLng;
import com.heymeowcat.codefestadmin.Model.News;
import com.heymeowcat.codefestadmin.Model.Products;
import com.heymeowcat.codefestadmin.fcmHelper.FCMClient;

import java.util.UUID;

public class NewsEvents extends AppCompatActivity {


    Button addNewsorEvent;
    TextInputLayout nameTIL,descriptionTIL;
    public CustomLatLng eventLocation;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Uri selectedCustomerPhotoURI;

    LinearProgressIndicator progressIndicator;
    FCMClient myFCMClient;

    public CustomLatLng getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(CustomLatLng eventLocation) {
        this.eventLocation = eventLocation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_events);

        nameTIL = findViewById(R.id.titleTIL);
        descriptionTIL =findViewById(R.id.descriptionTIL);
        addNewsorEvent = findViewById(R.id.addNewsEventBtn);
        progressIndicator =findViewById(R.id.progress_bar);
        addNewsorEvent = findViewById(R.id.addNewsEventBtn);
        myFCMClient = new FCMClient();

        addNewsorEvent.setOnClickListener(view -> {
            News rNewsEvent = new News();
            progressIndicator.setVisibility(View.VISIBLE);
            DocumentReference ref = db.collection("NewsOrEvents").document();
            String myId = ref.getId();
            rNewsEvent.setDocid(myId);
            rNewsEvent.setType("News");
            rNewsEvent.setNews_EventTitle(nameTIL.getEditText().getText().toString());
            rNewsEvent.setNews_EventDescription(descriptionTIL.getEditText().getText().toString());
            if(eventLocation!=null){
                rNewsEvent.setType("Event");
                rNewsEvent.setDirections(eventLocation);
            }
            db.collection("NewsOrEvents").document(myId).set(rNewsEvent).addOnSuccessListener(documentReference -> {

                myFCMClient.execute(
                        "News, Event Update",""+rNewsEvent.getNews_EventTitle()+" "+rNewsEvent.getNews_EventDescription());

                Toast.makeText(this, "News/Event Added Successfully !", Toast.LENGTH_SHORT).show();
                progressIndicator.setVisibility(View.GONE);
                Intent riderHomeIntent = new Intent(NewsEvents.this, MainActivity.class);
                startActivity(riderHomeIntent);
                finish();
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "News/Event Save Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                progressIndicator.setVisibility(View.GONE);
            });


        });





    }




}