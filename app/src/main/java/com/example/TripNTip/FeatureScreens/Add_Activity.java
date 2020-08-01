package com.example.TripNTip.FeatureScreens;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.TripNTip.R;


public class Add_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity);
    }

    // We should use it when we have the frontend implemented
//    BaseWeatherAPI baseWeatherApi = new IsraeliWeatherAPIHandler(this);
//    int tripId = baseWeatherApi.getID(trip.getName());
//    writeNewTrip(String.valueOf(tripId), "Jerusalem", "description of Jerusalem", true, true);
//
//    private void writeNewTrip(String tripId, String name, String description, boolean isDayTrip, boolean isSummerTrip) {
//        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//        final DatabaseReference tripsRef = rootRef.child("trips");
//
//        Trip trip = new Trip(name, description, isSummerTrip, isDayTrip);
//        tripsRef.child(name).setValue(trip);
//    }
}