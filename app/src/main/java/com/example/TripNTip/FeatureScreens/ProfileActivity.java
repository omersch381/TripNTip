package com.example.TripNTip.FeatureScreens;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
// FixMe Niv: some import are not used
import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.example.TripNTip.R;
import com.example.TripNTip.Utils.Constants;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import android.app.ProgressDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity implements Constants {
    // FixMe Niv: a final which is not in use
    private static final int SELECT_IMAGE = 1;
    private FirebaseAuth mAuth;
    // FixMe Niv: might be as well a local variable
    private DatabaseReference reference;
    private FirebaseDatabase mDataBase;
    FirebaseStorage storageInstance;
    StorageReference storageRef;
    private String emailOfCurrentUser;
    // FixMe Niv: a variable which is not used
    DataSnapshot ds;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance();
        reference = mDataBase.getReference(USER);
        // FixMe Niv: Please use a string from the string file instead of a constant
        final ProgressDialog progressDialog = ProgressDialog.show(this, "", WAIT);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                emailOfCurrentUser = mAuth.getCurrentUser().getEmail();
                setContentView(R.layout.profile_activity);
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String emailOnDataBase = ds.child(EMAIL).getValue().toString();
                    if (emailOnDataBase.equals(emailOfCurrentUser)) {
                        String userName = ds.child(USERNAME).getValue().toString();
                        showDetails(emailOfCurrentUser, userName);
                    }

                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // FixMe Niv: Please use a msg from the strings file
                System.out.println("Error read from database");

            }
        });

    }

    public void changePersonalDetails(View view) {
        // FixMe Niv: unnecessary String.valueOf. Also the title argument is unnecessary
        UpdateUserFragment alertDialog = UpdateUserFragment.newInstance(String.valueOf("Trip information"));
        alertDialog.show(getSupportFragmentManager(), "");

    }

    //Show the specific details of the current user.
    public void showDetails(String currentEmail, String currentUserName) {
        TextView userName = findViewById(R.id.profile_name);
        TextView email = findViewById(R.id.profile_eamail);
        imageView = findViewById(R.id.profile_image);
        initiateImage();

        userName .setText(currentUserName);
        email.setText(currentEmail);
        //now need to get the current image
    }

    // FixMe Niv: unnecessary exception throw
    public void changeProfilePicture(View v) throws FileNotFoundException {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // FixMe Niv: Please use a string from the string file
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);

    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        // FixMe Niv: it seems like RESULT_OK == -1. Is that the correct constant?
        if (resultCode == RESULT_OK) {
                final Uri imageUri = data.getData();
                imageView = findViewById(R.id.profile_image);
            try {
                final  InputStream  imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                //insert the selected image into imageview and represent him on the app
                imageView.setImageBitmap(selectedImage);
                saveImageTofireBase(imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        } else {
            // FixMe Niv: Please use a string from the string file
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    public void initiateImage() {
        storageInstance = FirebaseStorage.getInstance();
        storageRef = storageInstance.getReference(IMEGES).child(USER).child(emailOfCurrentUser);

        try {
            final File localFile = File.createTempFile(IMEGES, "bmp");
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener< FileDownloadTask.TaskSnapshot >() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    final Bitmap selectedImage = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    imageView.setImageBitmap(selectedImage);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
        // FixMe Niv: type with the word Fire in the method name
        public void saveImageTofireBase(Uri imageUri){
        if(imageUri!=null) {
            storageInstance = FirebaseStorage.getInstance();
            storageRef = storageInstance.getReference("images").child("users").child(emailOfCurrentUser);
            storageRef.putFile(imageUri);
        }else{
            // FixMe Niv: Please use a string from the string file
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }
        }
