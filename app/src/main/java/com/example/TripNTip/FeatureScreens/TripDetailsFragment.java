package com.example.TripNTip.FeatureScreens;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.TripNTip.R;
import com.example.TripNTip.TripNTip.TravelGuide;
import com.example.TripNTip.TripNTip.Trip;

public class TripDetailsFragment extends DialogFragment {

    View content;
    Trip trip;
    TravelGuide travelGuide;

    public TripDetailsFragment(Trip trip, String apiKey, Context context) {
        this.trip = trip;
        travelGuide = new TravelGuide(apiKey, context);
    }

    public static TripDetailsFragment newInstance(String title, Trip trip, String apiKey, Context context) {
        return new TripDetailsFragment(trip, apiKey, context);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        content = inflater.inflate(R.layout.fragment_trip_details, null);

        //TODO Niv: implement the following values in the view when it's done
        Log.d("Trip's name: ", trip.getName());
        Log.d("Trip's description: ", trip.getDescription());
        Log.d("Travel guide", " recommendation to travel: " + travelGuide.howMuchShouldITravelNowIn(trip) + "/10");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(content);
        alertDialogBuilder.create();
        return alertDialogBuilder.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trip_details, container, false);
    }
}