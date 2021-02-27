package com.heymeowcat.codefest2021androidproject.CustomerFragments;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.heymeowcat.codefest2021androidproject.MainActivity;
import com.heymeowcat.codefest2021androidproject.Model.Customers;
import com.heymeowcat.codefest2021androidproject.Model.News;
import com.heymeowcat.codefest2021androidproject.R;
import com.heymeowcat.codefest2021androidproject.ViewMoreDetails;

import java.util.List;

public class news extends Fragment {


    Customers loggedCustomer;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Query query;

    FirestoreRecyclerAdapter adapter;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_fragment, container, false);

        query= db.collection("NewsOrEvents");

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        db.collection("Customers")
                .whereEqualTo("email", user.getEmail()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Customers> customersList = queryDocumentSnapshots.toObjects(Customers.class);
            if (!customersList.isEmpty()) {

                loggedCustomer = customersList.get(0);
            }
        });




        FirestoreRecyclerOptions<News> options= new FirestoreRecyclerOptions.Builder<News>().setQuery(query,News.class).build();



        adapter = new FirestoreRecyclerAdapter<News, news.NewsEventHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull news.NewsEventHolder holder, int position, @NonNull News model) {

                holder.nETitle.setText(model.getNews_EventTitle());
                holder.nEDescription.setText("Description : "+model.getNews_EventDescription());
                holder.neType.setText("Type :"+ model.getType());
                holder.viewMoreBtn.setOnClickListener(view1 -> {
                    Intent view = new Intent(getContext(), ViewMoreDetails.class);
                    view.putExtra("NewsEventId",model.getDocid());
                    startActivity(view);
                });

            }

            @NonNull
            @Override
            public news.NewsEventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_event_item_layout, parent, false);
                return new news.NewsEventHolder(view);
            }


        };

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();




        return view;
    }



    static class NewsEventHolder extends RecyclerView.ViewHolder {

        TextView nETitle,nEDescription,neType;
        Button viewMoreBtn;

        public NewsEventHolder(@NonNull View itemView) {
            super(itemView);
            nETitle = itemView.findViewById(R.id.titleInItem);
            nEDescription = itemView.findViewById(R.id.descriptionInItem);
            neType =itemView.findViewById(R.id.typeinItem);
            viewMoreBtn =itemView.findViewById(R.id.ViewMoreBtn);
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