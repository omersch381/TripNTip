package com.example.TripNTip.TripNTip;

import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.TripNTip.FeatureScreens.AddTripActivity;
import com.example.TripNTip.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class Comment implements Serializable {

    private String author;
    private String timestamp;
    private String message;
    private boolean wasWritten;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Comment(String message) {
        this.author = getUserDisplayName();
        this.timestamp = generateTimestamp();
        this.message = message;
        this.wasWritten = false;
    }

    public Comment() {
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String getUserDisplayName() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
    }

    private String generateTimestamp() {
        Date date = Calendar.getInstance().getTime();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return formatter.format(date);
    }

    public void writeComment(Trip trip) {
        trip.getComments().add(this);
        writeCommentToFirebase(trip);
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

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
