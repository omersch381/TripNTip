package com.example.TripNTip.FeatureScreens;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.example.TripNTip.R;
import com.example.TripNTip.TripNTip.TravelFeedActivity;
import com.example.TripNTip.TripNTip.TravelGuide;
import com.example.TripNTip.TripNTip.Trip;

import java.util.Objects;

public class TripDetailsFragment extends DialogFragment {

    private View content;
    private Trip trip;
    private TravelGuide travelGuide;

    public TripDetailsFragment(Trip trip, String apiKey, Context context) {
        this.trip = trip;
        travelGuide = new TravelGuide(apiKey, context);
    }

    public static TripDetailsFragment newInstance(String title, Trip trip, String apiKey, Context context) {
        return new TripDetailsFragment(trip, apiKey, context);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View content = inflater.inflate(R.layout.trip_details_fragment, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(content);
        alertDialogBuilder.create();
        return alertDialogBuilder.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.trip_details_fragment, container, false);
        TextView tripName = v.findViewById(R.id.trip_details_name);
        TextView tripDescription = v.findViewById(R.id.trip_details_description);
        TextView isSummerTrip = v.findViewById(R.id.trip_details_isSummerTrip);
        TextView isDayTrip = v.findViewById(R.id.trip_details_isDayTrip);
        final Button recommendationButton = v.findViewById(R.id.trip_current_recommendation_button);
        final TextView recommendationTextView = v.findViewById(R.id.trip_current_recommendation_text_view);

        tripName.setText("Trip's Name: " + trip.getName());
        tripDescription.setText("Trip's Description: " + trip.getDescription());

        boolean isSummerTripBool = trip.getSummerTrip();
        String summerTrip = isSummerTripBool ? " Is a summer trip" : " Is not a summer trip";
        isSummerTrip.setText(trip.getName() + summerTrip);

        boolean isDayTripBool = trip.getDayTrip();
        String dayTrip = isDayTripBool ? " Is a day trip" : " Is not a day trip";
        isDayTrip.setText(trip.getName() + dayTrip);

        recommendationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recommendationTextView.setText("Our Recommendation For Doing The Trip Right Now: " + travelGuide.howMuchShouldITravelNowIn(trip) + "/10");
                recommendationTextView.setVisibility(View.VISIBLE);
            }
        });

//        final EditText tripNameET = (EditText) content.findViewById(R.id.trip_details_name);
//        tripNameET.setText("hello");
//        Log.d("Travel guide", " recommendation to travel: " + travelGuide.howMuchShouldITravelNowIn(trip) + "/10");

        return v;
    }
}