package com.heymeowcat.codefest2021androidproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.heymeowcat.codefest2021androidproject.Model.Customers;

import java.util.ArrayList;
import java.util.UUID;

public class CustomerRegister extends AppCompatActivity {


    private static final int FILE_CHOSE_ACTIVITY_CUSTOMER_PHOTO_REUSLT_CODE = 7;
    Button registerBtn;
    TextInputLayout nameTIL, nicTIL, emailTIL, phoneTIL;


    CardView selectCustomerCard;

    TextView customerPhotoText;

    ImageView customerImageView;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Uri selectedCustomerPhotoURI;


    RadioGroup genderRadio;
    RadioButton radioButton;

    StorageReference mStorage;

    LinearProgressIndicator progressIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_register);
        registerBtn = findViewById(R.id.registerBtn);

        nameTIL = findViewById(R.id.nameTIL);
        nicTIL = findViewById(R.id.nicTIL);
        emailTIL = findViewById(R.id.emailTIL);
        phoneTIL = findViewById(R.id.phonenoTIL);
        selectCustomerCard = findViewById(R.id.customerPhotoCard);

        customerPhotoText = findViewById(R.id.customerPhotoText);

        customerImageView = findViewById(R.id.customerImageview);
        progressIndicator= findViewById(R.id.progress_bar);

        mStorage = FirebaseStorage.getInstance().getReference();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String email = bundle.getString("auth_email");
        String name = bundle.getString("auth_name");

        genderRadio =findViewById(R.id.genderRadio);

        nameTIL.getEditText().setText(name);
        emailTIL.getEditText().setText(email);


        selectCustomerCard.setOnClickListener(view -> {
            Intent filechooser1 = new Intent();
            filechooser1.setAction(Intent.ACTION_GET_CONTENT);
            filechooser1.setType("image/*");
            startActivityForResult(Intent.createChooser(filechooser1, "Select Customer Photo"), FILE_CHOSE_ACTIVITY_CUSTOMER_PHOTO_REUSLT_CODE);
        });



        registerBtn.setOnClickListener(view -> {

            Customers rCustomer = new Customers();
            progressIndicator.setVisibility(View.VISIBLE);
            DocumentReference ref = db.collection("Customers").document();
            String myId = ref.getId();
            rCustomer.setDocid(myId);

            String Rname = nameTIL.getEditText().getText().toString();
            String RNIC = nicTIL.getEditText().getText().toString();
            String Remail = emailTIL.getEditText().getText().toString();
            String Rphone = phoneTIL.getEditText().getText().toString();
            int selectedId = genderRadio.getCheckedRadioButtonId();
            radioButton = findViewById(selectedId);
            String Rgender = (String) radioButton.getText();


            rCustomer.setName(Rname);
            rCustomer.setEmail(Remail);
            rCustomer.setNic(RNIC);
            rCustomer.setTelephone(Rphone);
            rCustomer.setGender(Rgender);



            StorageReference driverphotoreference = mStorage.child("CustomerPhotos").child(Remail).child(UUID.randomUUID().toString());



            UploadTask uploadTaskdriverphoto = driverphotoreference.putFile(selectedCustomerPhotoURI);
            uploadTaskdriverphoto.addOnFailureListener(exception -> Toast.makeText(CustomerRegister.this, "Customer Photo Upload Failed", Toast.LENGTH_SHORT).show()).addOnSuccessListener(taskSnapshot -> {
                uploadTaskdriverphoto.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return driverphotoreference.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        rCustomer.setProfilePicDownloadUrl(downloadUri.toString());


                        db.collection("Customers").document(myId).set(rCustomer).addOnSuccessListener(documentReference -> {
                            Toast.makeText(this, "Customer Registered Successfully !", Toast.LENGTH_SHORT).show();
                            progressIndicator.setVisibility(View.GONE);
                            Intent riderHomeIntent = new Intent(CustomerRegister.this, MainActivity.class);
                            startActivity(riderHomeIntent);

                            finish();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(this, "Customer Save Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressIndicator.setVisibility(View.GONE);
                        });


                    }
                });
            });


        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (FILE_CHOSE_ACTIVITY_CUSTOMER_PHOTO_REUSLT_CODE == requestCode) {
            if (resultCode == RESULT_OK) {
                selectedCustomerPhotoURI = data.getData();
                Glide
                        .with(CustomerRegister.this)
                        .load(selectedCustomerPhotoURI)
                        .centerCrop()
                        .into(customerImageView);

                customerPhotoText.setVisibility(View.GONE);
            } else {
                Toast.makeText(this, "Customer Photo Not Selected " + resultCode, Toast.LENGTH_SHORT).show();
                customerPhotoText.setVisibility(View.VISIBLE);

            }
        }
    }


}