package com.heymeowcat.codefest2021androidproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.heymeowcat.codefest2021androidproject.Model.Customers;
import com.heymeowcat.codefest2021androidproject.Model.Tickets;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NewTicket extends AppCompatActivity {

    TextInputLayout ticketNameTIL, ticketBodyTIL;
    AutoCompleteTextView ticketOption;
    String selectedticketOption;
    Button addTicketBtn;
    Customers loggedCustomer;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    LinearProgressIndicator progressIndicator;

    ArrayList<String> ticketOptionslist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ticket);


        ticketOption = findViewById(R.id.ticketOptions);
        ticketNameTIL = findViewById(R.id.ticketTitle);
        ticketBodyTIL = findViewById(R.id.ticketBody);
        addTicketBtn = findViewById(R.id.placeaTicketBtn);
        progressIndicator = findViewById(R.id.progress_bar);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ticketOptionslist.add("Information Request");
        ticketOptionslist.add("Complain");
        ticketOptionslist.add("Compliment");


        db.collection("Customers")
                .whereEqualTo("email", user.getEmail()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Customers> customersList = queryDocumentSnapshots.toObjects(Customers.class);
            if (!customersList.isEmpty()) {

                loggedCustomer = customersList.get(0);
            }
        });



        ArrayAdapter<String> adapterforticketoptions = new ArrayAdapter<>(NewTicket.this, R.layout.dropdown_menu_item, ticketOptionslist);

        ticketOption.setAdapter(adapterforticketoptions);


        ticketOption.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0) {
                    selectedticketOption = ticketOption.getText().toString();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        addTicketBtn.setOnClickListener(view -> {
            if(loggedCustomer!=null){
                if (selectedticketOption != null) {
                    Tickets rTicket = new Tickets();
                    progressIndicator.setVisibility(View.VISIBLE);
                    DocumentReference ref = db.collection("Tickets").document();
                    String myId = ref.getId();
                    rTicket.setDocid(myId);

                    String Rname = ticketNameTIL.getEditText().getText().toString();
                    String Rbody = ticketBodyTIL.getEditText().getText().toString();

                    rTicket.setTicketTitle(Rname);
                    rTicket.setTicketBody(Rbody);
                    rTicket.setOption(selectedticketOption);
                    rTicket.setTicketPlacedBy(loggedCustomer.getDocid());

                    db.collection("Tickets").document(myId).set(rTicket).addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Ticket Placed Successfully !", Toast.LENGTH_SHORT).show();
                        progressIndicator.setVisibility(View.GONE);
                        Intent riderHomeIntent = new Intent(NewTicket.this, MainActivity.class);
                        startActivity(riderHomeIntent);

                        finish();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, "Ticket Placing Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressIndicator.setVisibility(View.GONE);
                    });

                }


            }

        });


    }
}