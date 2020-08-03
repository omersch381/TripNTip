package com.example.TripNTip.FeatureScreens;

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

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private FirebaseDatabase mDataBase;
    Bundle bundle=new Bundle();
    DataSnapshot ds;


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
                                String email=ds.child(EMAIL).getValue().toString();
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
        }
