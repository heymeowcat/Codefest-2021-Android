package com.heymeowcat.codefestadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.heymeowcat.codefestadmin.Model.Customers;

import java.util.List;

public class Tickets extends AppCompatActivity {


    Query query;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirestoreRecyclerAdapter adapter;
    RecyclerView recyclerView;

    Customers loggedCustomer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets);


        query= db.collection("Tickets");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        FirestoreRecyclerOptions<com.heymeowcat.codefestadmin.Model.Tickets> options= new FirestoreRecyclerOptions.Builder<com.heymeowcat.codefestadmin.Model.Tickets>().setQuery(query,com.heymeowcat.codefestadmin.Model.Tickets.class).build();



        adapter = new FirestoreRecyclerAdapter<com.heymeowcat.codefestadmin.Model.Tickets, TicketsHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull TicketsHolder holder, int position, @NonNull com.heymeowcat.codefestadmin.Model.Tickets model) {

                holder.ticketTitle.setText(model.getTicketTitle());
                holder.ticketBody.setText("Ticket Body: "+model.getTicketBody());
                holder.ticketOption.setText("Ticket Option :"+ model.getOption());


                if (model.getTicketPlacedBy() != null) {
                    db.collection("Customers").whereEqualTo("docid", model.getTicketPlacedBy()).get().addOnSuccessListener(queryDocumentSnapshots1 -> {
                        List<Customers> customerList = queryDocumentSnapshots1.toObjects(Customers.class);
                        if (!customerList.isEmpty()) {
                            loggedCustomer = customerList.get(0);
                            holder.tikcetPlacedBy.setText("Ticket Placed By :"+ loggedCustomer.getName() +"("+loggedCustomer.getEmail()+")");
                        }

                    });

                }


            }

            @NonNull
            @Override
            public TicketsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticket_item_layout, parent, false);
                return new TicketsHolder(view);
            }


        };

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }


    static class TicketsHolder extends RecyclerView.ViewHolder {

        TextView ticketTitle,ticketBody,ticketOption,tikcetPlacedBy;

        public TicketsHolder(@NonNull View itemView) {
            super(itemView);
            ticketTitle = itemView.findViewById(R.id.ticketTitleinItem);
            ticketBody = itemView.findViewById(R.id.ticketBodyinItem);
            ticketOption =itemView.findViewById(R.id.ticketOptioninItem);
            tikcetPlacedBy =itemView.findViewById(R.id.ticketPlacedBy);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


}