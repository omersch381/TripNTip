package com.example.TripNTip.FeatureScreens;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import com.example.TripNTip.TripNTip.TravelFeedActivity;
import com.example.TripNTip.TripNTip.Trip;
import com.example.TripNTip.Utils.Constants;
import com.example.TripNTip.WeatherAPI.IsraeliWeatherAPIHandler;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddTripActivity extends AppCompatActivity implements Constants {

    private boolean summerTrip;
    private boolean dayTrip;
    private AutoCompleteTextView locationChooser;
    private IsraeliWeatherAPIHandler handler;
    private boolean imageUploadSucceeded;
    private String[] listOfCities;

    public AddTripActivity() {
        summerTrip = false;
        dayTrip = false;
        imageUploadSucceeded = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_trip_activity);
        //todo omer check about the base and protected
        handler = new IsraeliWeatherAPIHandler(getApplicationContext());

        listOfCities = handler.getCities();

        handleCountryChooser();

        setOnClicks();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleCountryChooser() {
        ArrayAdapter<String> countryArrayAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, listOfCities);
        locationChooser = findViewById(R.id.autoCompleteTextView);
        locationChooser.setThreshold(1);
        locationChooser.setAdapter(countryArrayAdapter);
        locationChooser.setTextColor(Color.BLACK);
    }

    private void setOnClicks() {
        Button uploadPictureButton = findViewById(R.id.uploadTripPicture);
        Button addTripButton = findViewById(R.id.add_trip_button);
        CheckBox summerTripCB = findViewById(R.id.add_trip_isSummerTrip);
        CheckBox dayTripCB = findViewById(R.id.add_trip_isDayTrip);

        uploadPictureButton.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {
                uploadTripPictureFromGallery();
            }
        });
        addTripButton.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
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


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addTrip() {
        String status;
        if ((status = allConditionApply()).equals(TRIP_CREATION_SUCCEED))
            writeTripToDatabase(createTripFromUserData());
        else
            Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String allConditionApply() {
        EditText tripNameET = findViewById(R.id.add_trip_name);
        EditText descriptionNameET = findViewById(R.id.add_trip_description);
        if (tripNameET.getText().toString().isEmpty())
            return getResources().getString(R.string.AddTripNameIsEmptyMsg);
        else if (descriptionNameET.getText().toString().isEmpty())
            return getResources().getString(R.string.AddTripDescriptionIsEmptyMsg);
        else if (!isLocationInTheLocationsList(locationChooser.getText().toString()))
            return getResources().getString(R.string.AddTripLocationNotInList);
        else if (!imageUploadSucceeded)
            return getResources().getString(R.string.AddTripImageUploadFailed);
        else
            return TRIP_CREATION_SUCCEED;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isLocationInTheLocationsList(String location) {
        if (location.isEmpty())
            return false;
        for (String locationInList : listOfCities)
            if (location.equals(locationInList))
                return true;
        return false;
    }

    private void writeTripToDatabase(Trip trip) {
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
        intent.putExtra(SHOULD_WE_LOAD_THE_API_KEY, true);
        intent.putExtra(SHOULD_WE_LOAD_THE_TRIPS, true);
        AddTripActivity.this.startActivity(intent);
    }

    private Trip createTripFromUserData() {
        EditText tripNameET = findViewById(R.id.add_trip_name);
        EditText descriptionNameET = findViewById(R.id.add_trip_description);

        return new Trip(tripNameET.getText().toString(), descriptionNameET.getText().toString(), summerTrip, dayTrip, locationChooser.getText().toString());
    }

    private void uploadTripPictureFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.AddTripImageUploadTitle)), 1);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_OK)
            saveImageToDataBase(data.getData());
        else
            Toast.makeText(this, R.string.not_choose_photo, Toast.LENGTH_LONG).show();
    }

    private void saveImageToDataBase(Uri imageUri) {
        if (imageUri != null) {
            final ProgressDialog progressDialog = ProgressDialog.show(AddTripActivity.this, "", getResources().getString(R.string.waitMessage));
            final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            String tripId = String.valueOf(handler.getID(locationChooser.getText().toString()));

            storageRef.child("images").child("trips").child(tripId + ".png").putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageUploadSucceeded = true;
                    progressDialog.dismiss();
                }
            });
        } else
            Toast.makeText(this, R.string.Eror_database, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AddTripActivity.this, TravelFeedActivity.class);
        intent.putExtra(SHOULD_WE_LOAD_THE_API_KEY, true);
        intent.putExtra(SHOULD_WE_LOAD_THE_TRIPS, true);
        AddTripActivity.this.startActivity(intent);
    }
}
