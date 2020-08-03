package com.example.TripNTip.FeatureScreens;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.TripNTip.R;
import com.example.TripNTip.TripNTip.SignInActivity;
import com.example.TripNTip.TripNTip.TravelFeedActivity;
import com.example.TripNTip.TripNTip.Trip;
import com.example.TripNTip.Utils.Constants;
import com.example.TripNTip.WeatherAPI.IsraeliWeatherAPIHandler;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddTripActivity extends AppCompatActivity implements Constants {

    private boolean summerTrip;
    private boolean dayTrip;
    private AutoCompleteTextView countryChooser;
    private IsraeliWeatherAPIHandler handler;

    public AddTripActivity() {
        summerTrip = false;
        dayTrip = false;
    }

    //TODO: take care of the picture upload

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_trip_activity);

        handler = new IsraeliWeatherAPIHandler(getApplicationContext());

        handleCountryChooser();

        setOnClicks();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleCountryChooser() {
        ArrayAdapter<String> countryArrayAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, handler.getCities());
        countryChooser = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        countryChooser.setThreshold(1);
        countryChooser.setAdapter(countryArrayAdapter);
        countryChooser.setTextColor(Color.BLACK);
    }

    private void setOnClicks() {
        Button addTripButton = findViewById(R.id.add_trip_button);
        CheckBox summerTripCB = findViewById(R.id.add_trip_isSummerTrip);
        CheckBox dayTripCB = findViewById(R.id.add_trip_isDayTrip);

        addTripButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                addTrip();
            }
        });
        summerTripCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                summerTrip = true;
            }
        });
        dayTripCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dayTrip = true;
            }
        });
    }

    private void addTrip() {
        writeTripToFirebase(createTripFromUserData());
    }

    private void writeTripToFirebase(Trip trip) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference tripsRef = rootRef.child("trips");

        int id = handler.getID(trip.getLocation());
        trip.setId(id);

        tripsRef.child(trip.getName()).setValue(trip, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                String result;
                if (error == null)
                    result = getResources().getString(R.string.tripWritingSuccessMsg);
                else
                    result = getResources().getString(R.string.tripWritingFailureMsg);

                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            }
        });
        Intent intent = new Intent(AddTripActivity.this, TravelFeedActivity.class);
        intent.putExtra(SHOULD_WE_LOAD_THE_TRIPS, true);
        intent.putExtra(SHOULD_WE_LOAD_THE_API_KEY, true);
        AddTripActivity.this.startActivity(intent);
    }

    private Trip createTripFromUserData() {
        EditText tripNameET = findViewById(R.id.add_trip_name);
        EditText descriptionNameET = findViewById(R.id.add_trip_description);

        return new Trip(tripNameET.getText().toString(), descriptionNameET.getText().toString(), summerTrip, dayTrip, countryChooser.getText().toString());
    }
}
