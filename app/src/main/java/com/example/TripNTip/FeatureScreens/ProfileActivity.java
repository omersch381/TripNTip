package com.example.TripNTip.FeatureScreens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.TripNTip.R;
import com.example.TripNTip.TripNTip.TravelFeedActivity;
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
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseDatabase mDataBase = FirebaseDatabase.getInstance();
        final DatabaseReference reference = mDataBase.getReference(USER);
        imageView = findViewById(R.id.profile_image);

        final ProgressDialog progressDialog = ProgressDialog.show(this, "", getResources().getString(R.string.wait));
        reference.addValueEventListener(new ValueEventListener() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                emailOfCurrentUser = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                setContentView(R.layout.profile_activity);
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String emailOnDataBase = Objects.requireNonNull(ds.child(EMAIL).getValue()).toString();
                    if (emailOnDataBase.toLowerCase().equals(emailOfCurrentUser)) {
                        String userName = Objects.requireNonNull(ds.child(USERNAME).getValue()).toString();
                        showDetails(emailOfCurrentUser, userName);
                    }

                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), R.string.not_choose_photo, Toast.LENGTH_LONG).show();

            }
        });

    }


    //Show the specific details of the current user.
    public void showDetails(String currentEmail, String currentUserName) {
        TextView userName = findViewById(R.id.profile_name);
        TextView email = findViewById(R.id.profile_eamail);



        initiateImage();
        userName.setText("UserName:\n"+currentUserName);
        email.setText("Email\n"+currentEmail);

    }

    public void changeProfilePicture(View v)  {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent,getResources().getString(R.string.select_image)) ,1);

    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        imageView = findViewById(R.id.profile_image);
        if (resultCode == RESULT_OK) {
            final Uri imageUri = data.getData();
            try {
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                //insert the selected image into imageView and represent him on the app
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
        imageView = findViewById(R.id.profile_image);
        final FirebaseStorage storageInstance = FirebaseStorage.getInstance();
        final StorageReference storageRef = storageInstance.getReference(IMAGES).child(USER).child(emailOfCurrentUser);
        try {
            final File localFile = File.createTempFile(IMAGES, "bmp");
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


    public void saveImageToDataBase(Uri imageUri) {
        if (imageUri != null) {
            final FirebaseStorage storageInstance = FirebaseStorage.getInstance();
            final StorageReference storageRef = storageInstance.getReference(IMAGES).child(USER).child(emailOfCurrentUser);
            storageRef.putFile(imageUri);
        } else {
            Toast.makeText(this, R.string.Eror_database, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ProfileActivity.this, TravelFeedActivity.class);
        intent.putExtra(SHOULD_WE_LOAD_THE_API_KEY, true);
        intent.putExtra(SHOULD_WE_LOAD_THE_TRIPS, true);
        ProfileActivity.this.startActivity(intent);
    }
}
