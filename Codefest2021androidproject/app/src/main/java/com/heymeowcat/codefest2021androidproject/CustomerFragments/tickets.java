package com.heymeowcat.codefest2021androidproject.CustomerFragments;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.heymeowcat.codefest2021androidproject.Model.Customers;
import com.heymeowcat.codefest2021androidproject.Model.Products;
import com.heymeowcat.codefest2021androidproject.Model.Purchases;
import com.heymeowcat.codefest2021androidproject.Model.Tickets;
import com.heymeowcat.codefest2021androidproject.NewTicket;
import com.heymeowcat.codefest2021androidproject.R;

import java.util.Date;
import java.util.List;


public class tickets extends Fragment {

    Button placeNewTicketBtn;
    Customers loggedCustomer;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Query query;

    FirestoreRecyclerAdapter adapter;
    RecyclerView recyclerView;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tickets_fragment, container, false);

        placeNewTicketBtn =view.findViewById(R.id.placeaNewTicketBtn);
        placeNewTicketBtn.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), NewTicket.class);
            startActivity(intent);
        });


        query= db.collection("Tickets");

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        db.collection("Customers")
                .whereEqualTo("email", user.getEmail()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Customers> customersList = queryDocumentSnapshots.toObjects(Customers.class);
            if (!customersList.isEmpty()) {

                loggedCustomer = customersList.get(0);
            }
        });




        FirestoreRecyclerOptions<Tickets> options= new FirestoreRecyclerOptions.Builder<Tickets>().setQuery(query,Tickets.class).build();



        adapter = new FirestoreRecyclerAdapter<Tickets, TicketsHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull TicketsHolder holder, int position, @NonNull Tickets model) {

                holder.ticketTitle.setText(model.getTicketTitle());
                holder.ticketBody.setText("Ticket Body: "+model.getTicketBody());
                holder.ticketOption.setText("Ticket Option :"+ model.getOption());
                if(loggedCustomer!=null){
                    holder.tikcetPlacedBy.setText("Ticket Placed By :"+ loggedCustomer.getName() +"("+loggedCustomer.getEmail()+")");
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




        return view;
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