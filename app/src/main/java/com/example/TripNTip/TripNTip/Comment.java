package com.example.TripNTip.TripNTip;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.TripNTip.FeatureScreens.AddTripActivity;
import com.example.TripNTip.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class Comment implements Serializable {

    private String author;
    private String timestamp;
    private String message;
    private FirebaseAuth mAuth;
    private boolean wasWritten;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Comment(String message) {
        this.timestamp = generateTimestamp();
        this.message = message;
        this.wasWritten = false;
    }

    public Comment() {
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setauthor() {
        mAuth = FirebaseAuth.getInstance();
        final FirebaseDatabase mDataBase = FirebaseDatabase.getInstance();
        final DatabaseReference reference = mDataBase.getReference("users");

        reference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String emailOfCurrentUsre = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String emailOnDataBase = Objects.requireNonNull(ds.child("email").getValue()).toString();
                    if (emailOnDataBase.toLowerCase().equals(emailOfCurrentUsre)) {
                        author = Objects.requireNonNull(ds.child("username").getValue()).toString();
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }
    private String generateTimestamp() {
        Date date = Calendar.getInstance().getTime();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return formatter.format(date);
    }

    public boolean handleWriteComment(Trip trip) {
        trip.getComments().add(this);
        boolean wasWritten = writeCommentToFirebase(trip);
        if (!wasWritten)
            trip.getComments().remove(this);
        return wasWritten;
    }

    public boolean writeCommentToFirebase(Trip trip) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference tripsRef = rootRef.child("trips");

        tripsRef.child(trip.getName()).child("comments").setValue(trip.getComments(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
//                String result;
//                if (error == null)
//                    result = getResources().getString(R.string.tripWritingSuccessMsg);
//                else
//                    result = getResources().getString(R.string.tripWritingFailureMsg);
//
//                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                wasWritten = error == null;
            }
        });
        return wasWritten;
    }

    public String getAuthor() {
        return author;
    }
    public void  setAuthor(String author){
        this.author=author;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
