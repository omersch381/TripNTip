package com.example.FeatureScreens;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.TripNTip.R;
import com.example.TripNTip.TNTUser;
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
    final String WAIT = "Please wait...";
    //DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");
    private List<TNTUser> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.profile_activity);
        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance();
        reference = mDataBase.getReference("user");
        final ProgressDialog progressDialog = ProgressDialog.show(this, "", WAIT);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setContentView(R.layout.profile_activity);
                List<String> keys = new ArrayList<>();
                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    keys.add(datas.getKey());
                    TNTUser user = datas.getValue(TNTUser.class);
                    users.add(user);
                }
                showDetails();
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

    public void showDetails() {
        String email = mAuth.getCurrentUser().getEmail();
        System.out.println("user size" + users.size());
        for (TNTUser user : users) {
            if (user.getEmail().equals(email)) {
                System.out.println("im here!" + user.getEmail());
                TextView userName = findViewById(R.id.profile_name);
                TextView country = findViewById(R.id.profile_country);
                userName.setText(user.getUsername());
                country.setText(user.getCountry());
            }
        }
    }
}