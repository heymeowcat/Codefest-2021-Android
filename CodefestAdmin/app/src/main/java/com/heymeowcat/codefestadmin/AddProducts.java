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
import com.heymeowcat.codefestadmin.Model.Products;

import java.util.UUID;

public class AddProducts extends AppCompatActivity {



    private static final int FILE_CHOSE_ACTIVITY_PRODUCT_PHOTO_REUSLT_CODE = 7;
    Button addProduct;
    TextInputLayout nameTIL,priceTIL;


    CardView selectProductCard;

    TextView productPhotoText;

    ImageView productImageView;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Uri selectedCustomerPhotoURI;

    StorageReference mStorage;

    LinearProgressIndicator progressIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);

        nameTIL = findViewById(R.id.nameTIL);
        priceTIL =findViewById(R.id.priceTIL);
        selectProductCard = findViewById(R.id.productImageCard);
        addProduct = findViewById(R.id.addProductBtn);
        progressIndicator =findViewById(R.id.progress_bar);

        productImageView = findViewById(R.id.productImageView);
        productPhotoText = findViewById(R.id.productImageText);
        mStorage = FirebaseStorage.getInstance().getReference();


        selectProductCard.setOnClickListener(view -> {
            Intent filechooser1 = new Intent();
            filechooser1.setAction(Intent.ACTION_GET_CONTENT);
            filechooser1.setType("image/*");
            startActivityForResult(Intent.createChooser(filechooser1, "Select Product Image"), FILE_CHOSE_ACTIVITY_PRODUCT_PHOTO_REUSLT_CODE);
        });




        addProduct.setOnClickListener(view -> {
            Products rProducts = new Products();
            progressIndicator.setVisibility(View.VISIBLE);
            DocumentReference ref = db.collection("Products").document();
            String myId = ref.getId();
            rProducts.setDocid(myId);

            String Rname = nameTIL.getEditText().getText().toString();
            String RPrice = priceTIL.getEditText().getText().toString();

            rProducts.setProductName(Rname);
            rProducts.setProductPrice(Double.parseDouble(RPrice));


            StorageReference driverphotoreference = mStorage.child("ProductImages").child(UUID.randomUUID().toString());

            UploadTask uploadTaskdriverphoto = driverphotoreference.putFile(selectedCustomerPhotoURI);
            uploadTaskdriverphoto.addOnFailureListener(exception -> Toast.makeText(AddProducts.this, "Product Image Upload Failed", Toast.LENGTH_SHORT).show()).addOnSuccessListener(taskSnapshot -> {
                uploadTaskdriverphoto.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return driverphotoreference.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        rProducts.setProductImgDownURL(downloadUri.toString());

                        db.collection("Products").document(myId).set(rProducts).addOnSuccessListener(documentReference -> {
                            Toast.makeText(this, "Product Added Successfully !", Toast.LENGTH_SHORT).show();
                            progressIndicator.setVisibility(View.GONE);
                            Intent riderHomeIntent = new Intent(AddProducts.this, MainActivity.class);
                            startActivity(riderHomeIntent);

                            finish();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(this, "Products Save Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        if (FILE_CHOSE_ACTIVITY_PRODUCT_PHOTO_REUSLT_CODE == requestCode) {
            if (resultCode == RESULT_OK) {
                selectedCustomerPhotoURI = data.getData();
                Glide
                        .with(AddProducts.this)
                        .load(selectedCustomerPhotoURI)
                        .centerCrop()
                        .into(productImageView);

                productPhotoText.setVisibility(View.GONE);
            } else {
                Toast.makeText(this, "Product Image Not Selected " + resultCode, Toast.LENGTH_SHORT).show();
                productPhotoText.setVisibility(View.VISIBLE);

            }
        }
    }


}