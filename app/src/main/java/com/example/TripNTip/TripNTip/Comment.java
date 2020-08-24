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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Comment(String message) {
        this.timestamp = generateTimestamp();
        this.message = message;

    }

    public Comment() {
    }

    private String generateTimestamp() {
        Date date = Calendar.getInstance().getTime();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return formatter.format(date);
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
