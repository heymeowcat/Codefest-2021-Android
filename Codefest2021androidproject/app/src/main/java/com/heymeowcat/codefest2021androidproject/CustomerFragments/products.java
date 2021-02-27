package com.heymeowcat.codefest2021androidproject.CustomerFragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.heymeowcat.codefest2021androidproject.CustomerRegister;
import com.heymeowcat.codefest2021androidproject.Login;
import com.heymeowcat.codefest2021androidproject.MainActivity;
import com.heymeowcat.codefest2021androidproject.Model.Customers;
import com.heymeowcat.codefest2021androidproject.Model.Products;
import com.heymeowcat.codefest2021androidproject.Model.Purchases;
import com.heymeowcat.codefest2021androidproject.ProductsViewModel;
import com.heymeowcat.codefest2021androidproject.R;

import java.util.Date;
import java.util.List;

public class products extends Fragment {


    Button logout_btn;
    Customers loggedCustomer;
    TextInputLayout searchTIL;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Query query;

    FirestoreRecyclerAdapter adapter;
    RecyclerView recyclerView;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.products_fragment, container, false);

        TextView nameView = view.findViewById(R.id.nameView);
        TextView emailView = view.findViewById(R.id.emailView);
        ImageView userImage = view.findViewById(R.id.userImage);
        searchTIL = view.findViewById(R.id.searchTIL);

        query= db.collection("Products");


        searchTIL.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                adapter.stopListening();
                query.whereArrayContains("productName",searchTIL.getEditText().getText().toString());


            }
        });



        logout_btn = view.findViewById(R.id.logout_btn);
        logout_btn.setOnClickListener(view1 ->{
            signOut();
        });


        recyclerView = view.findViewById(R.id.recyclerView);
        GridLayoutManager manager = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(manager);



        db.collection("Customers")
                .whereEqualTo("email", user.getEmail()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Customers> customersList = queryDocumentSnapshots.toObjects(Customers.class);
            if (!customersList.isEmpty()) {

                loggedCustomer = customersList.get(0);
                nameView.setText(loggedCustomer.getName());
                emailView.setText(loggedCustomer.getEmail());
                Glide.with(getContext()).load(loggedCustomer.getProfilePicDownloadUrl()).into(userImage);
            }
        });





        FirestoreRecyclerOptions<Products> options= new FirestoreRecyclerOptions.Builder<Products>().setQuery(query,Products.class).build();



        adapter = new FirestoreRecyclerAdapter<Products, ProductsHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull ProductsHolder holder, int position, @NonNull Products model) {

                holder.productTitle.setText(model.getProductName());
                Glide.with(getContext()).load(model.getProductImgDownURL()).into(holder.productImage);
                holder.addToCartBtn.setOnClickListener(view -> {
                    Purchases newPurchase = new Purchases();
                    DocumentReference ref = db.collection("Purchases").document();
                    String myId = ref.getId();
                    newPurchase.setDocid(myId);
                    newPurchase.setCustomerid(loggedCustomer.getDocid());
                    newPurchase.setProductid(model.getDocid());
                    newPurchase.setPurchasedDate(new Date(System.currentTimeMillis()));


                    db.collection("Purchases").document(myId).set(newPurchase).addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Product Purchased !", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Purchase Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

                });
            }

            @NonNull
            @Override
            public ProductsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_layout, parent, false);
                return new ProductsHolder(view);
            }


        };

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();



        return view;
    }


    static class ProductsHolder extends RecyclerView.ViewHolder {

        TextView productTitle;
        ImageView productImage;
        Button addToCartBtn;

        public ProductsHolder(@NonNull View itemView) {
            super(itemView);
            productTitle = itemView.findViewById(R.id.productName);
            productImage = itemView.findViewById(R.id.productImage);
            addToCartBtn =itemView.findViewById(R.id.addToCartBtn);
        }
    }



    public void signOut() {
        AuthUI.getInstance()
                .signOut(getContext())
                .addOnCompleteListener(task -> {
                    Intent i = new Intent(getContext(), Login.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(i);
                    getActivity().finish();
                });
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