package com.heymeowcat.codefest2021androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.heymeowcat.codefest2021androidproject.Model.Customers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Login extends AppCompatActivity {

    private static final int RC_SIGN_IN =123;
    SignInButton signInButton;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(view -> {
            createSignInIntent();
        });



    }


    public void createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());


        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_create_intent]
    }

    // [START auth_fui_result]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                String email =user.getEmail();
                String name = user.getDisplayName();

                db.collection("Customers").whereEqualTo("email",email).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Customers> riderList = queryDocumentSnapshots.toObjects(Customers.class);
                    if(!riderList.isEmpty()){
                        Intent intent = new Intent(Login.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Intent registerCustomerIntent = new Intent(Login.this,CustomerRegister.class);
                        registerCustomerIntent.putExtra("auth_email",email);
                        registerCustomerIntent.putExtra("auth_name",name);
                        registerCustomerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(registerCustomerIntent);
                        finish();
                    }
                });




            } else {

                Toast.makeText(this, "Sign in abort by user", Toast.LENGTH_SHORT).show();
            }
        }
    }




}