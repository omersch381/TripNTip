package com.example.TripNTip.FeatureScreens;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.TripNTip.R;
import com.example.TripNTip.Utils.Constants;
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

import android.app.ProgressDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity implements Constants {
    private FirebaseAuth mAuth;
    private String emailOfCurrentUser;
    // FixMe Niv: a variable which is not used. below it is being overridden
    DataSnapshot ds;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseDatabase mDataBase = FirebaseDatabase.getInstance();
        final DatabaseReference reference = mDataBase.getReference(USER);

        //FixMe Niv: please use a string from the strings file
        final ProgressDialog progressDialog = ProgressDialog.show(this, "", WAIT);
        reference.addValueEventListener(new ValueEventListener() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                emailOfCurrentUser = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                setContentView(R.layout.profile_activity);
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String emailOnDataBase = Objects.requireNonNull(ds.child(EMAIL).getValue()).toString();
                    if (emailOnDataBase.equals(emailOfCurrentUser)) {
                        String userName = Objects.requireNonNull(ds.child(USERNAME).getValue()).toString();
                        showDetails(emailOfCurrentUser, userName);
                    }

                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // FixMe Niv: Please use a msg from the strings file
                System.out.println(R.string.Eror_database);

            }
        });

    }


    //Show the specific details of the current user.
    public void showDetails(String currentEmail, String currentUserName) {
        TextView userName = findViewById(R.id.profile_name);
        TextView email = findViewById(R.id.profile_eamail);

        //FixMe Niv: unnecessary line
        imageView = findViewById(R.id.profile_image);

        initiateImage();
        userName.setText(currentUserName);
        email.setText(currentEmail);

    }

    // FixMe Niv: unnecessary exception throw
    public void changeProfilePicture(View v) throws FileNotFoundException {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // FixMe Niv: Please use a string from the string file
        startActivityForResult(Intent.createChooser(intent, "select picture"), 1);

    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        // FixMe Niv: it seems like RESULT_OK == -1. Is that the correct constant?
        if (resultCode == RESULT_OK) {
            final Uri imageUri = data.getData();
            imageView = findViewById(R.id.profile_image);
            try {
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                //insert the selected image into imageview and represent him on the app
                imageView.setImageBitmap(selectedImage);
                saveImageToDataBase(imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        } else {
            Toast.makeText(this, R.string.not_choose_photo, Toast.LENGTH_LONG).show();
        }
    }

    public void initiateImage() {
        final FirebaseStorage storageInstance = FirebaseStorage.getInstance();
        //FixMe: type - IMAGES
        final StorageReference storageRef = storageInstance.getReference(IMEGES).child(USER).child(emailOfCurrentUser);
        try {
            final File localFile = File.createTempFile(IMEGES, "bmp");
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
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
    public void saveImageToDataBase(Uri imageUri) {
        if (imageUri != null) {
            final FirebaseStorage storageInstance = FirebaseStorage.getInstance();
            final StorageReference storageRef = storageInstance.getReference(IMEGES).child(USER).child(emailOfCurrentUser);
            storageRef.putFile(imageUri);
        } else {
            Toast.makeText(this, R.string.Eror_database, Toast.LENGTH_LONG).show();
        }
    }
}
