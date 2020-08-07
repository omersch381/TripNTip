package com.example.TripNTip.FeatureScreens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.TripNTip.R;
import com.example.TripNTip.TripNTip.TNTUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private static final int  SELECT_IMAGE =  1;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private FirebaseDatabase mDataBase;
    DataSnapshot ds;
    ImageView imageView;


    final String WAIT = "Please wait...";
    final String USER="users";

    final String USERNAME="username";
    final String EMAIL="email";
    //DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance();
        reference = mDataBase.getReference(USER);
        final ProgressDialog progressDialog = ProgressDialog.show(this, "", WAIT);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String emailOfCurrentUser = mAuth.getCurrentUser().getEmail();
                setContentView(R.layout.profile_activity);
                        for(DataSnapshot ds:  dataSnapshot.getChildren()) {
                            String emailOnDataBase = ds.child(EMAIL).getValue().toString();
                            if (emailOnDataBase.equals(emailOfCurrentUser)) {
                                String userName=ds.child(USERNAME).getValue().toString();
                                showDetails(emailOfCurrentUser,userName);
                            }

                        }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Error read from database");

            }
        });

    }

    public void changePersonalDetails(View view) {
        UpdateUserFragment alertDialog = UpdateUserFragment.newInstance(String.valueOf("Trip information"));
        alertDialog.show(getSupportFragmentManager(), "");

    }
   //Show the specific details of the current user.
    public void showDetails(String currentEmail,String currentUserName) {
                TextView userName = findViewById(R.id.profile_name);
                TextView email = findViewById(R.id.profile_eamail);

                userName.setText(currentUserName);
                email.setText(currentEmail);
           }

           public void changeProfilePicture(View v) throws FileNotFoundException {
               Intent intent = new Intent();
               intent.setType("image/*");
               intent.setAction(Intent.ACTION_GET_CONTENT);
               startActivityForResult(Intent.createChooser(intent, "Select Picture"),1);

           }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                imageView = findViewById(R.id.profile_image);
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(selectedImage);

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://tripntip-b5655.appspot.com/images/user");
                storageRef.putFile(imageUri);
                
                //StorageReference mountainImagesRef = storageRef.child("images/" + chat_id + Utils.getCurrentTimeStamp() + ".jpg");
                //now we need to uplude the image to firebase and connect this bwtween the current user
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
        }
