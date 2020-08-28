package com.example.TripNTip.TripNTip;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.TripNTip.Utils.Constants;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Comment implements Serializable, Constants {

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
        DateFormat formatter = new SimpleDateFormat(COMMENT_FORMAT);
        return formatter.format(date);
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
