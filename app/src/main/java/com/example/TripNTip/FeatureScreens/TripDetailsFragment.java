package com.example.TripNTip.FeatureScreens;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.TripNTip.R;
import com.example.TripNTip.TripNTip.TravelGuide;
import com.example.TripNTip.TripNTip.Trip;
import com.example.TripNTip.WeatherAPI.TravelGuideRecommendation;

public class TripDetailsFragment extends DialogFragment {

    private Trip trip;
    private TravelGuide travelGuide;
    private Bitmap tripBitmap;
    private Fragment commentsListFragment;
    private Resources res;

    public TripDetailsFragment(Trip trip, String apiKey, Context context, Bitmap tripBitmap) {
        this.trip = trip;
        this.tripBitmap = tripBitmap;
        travelGuide = new TravelGuide(apiKey, context);
    }

    public static TripDetailsFragment newInstance(Trip trip, String apiKey, Context context, Bitmap tripBitmap) {
        return new TripDetailsFragment(trip, apiKey, context, tripBitmap);
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
        res = getResources();
        final View v = inflater.inflate(R.layout.trip_details_fragment, container, false);
        ImageView tripImage = v.findViewById(R.id.trip_image);
        TextView tripNameTextView = v.findViewById(R.id.trip_details_name);
        TextView tripDescriptionTextView = v.findViewById(R.id.trip_details_description);
        TextView isSummerTrip = v.findViewById(R.id.trip_details_isSummerTrip);
        TextView isDayTrip = v.findViewById(R.id.trip_details_isDayTrip);
        Button replyButton = v.findViewById(R.id.reply_button);
        Button recommendationButton = v.findViewById(R.id.trip_current_recommendation_button);
        boolean isSummerTripBool = trip.getSummerTrip();
        boolean isDayTripBool = trip.getDayTrip();

        tripImage.setImageBitmap(tripBitmap);

        String tripName = res.getString(R.string.TripDetailsName) + trip.getName();
        tripNameTextView.setText(tripName);

        String tripDescription = res.getString(R.string.TripDetailsName) + trip.getDescription();
        tripDescriptionTextView.setText(tripDescription);

        String summerTrip = isSummerTripBool ? res.getString(R.string.TripDetailsIsSummerTrip) : res.getString(R.string.TripDetailsIsNotSummerTrip);
        isSummerTrip.setText(trip.getName() + summerTrip);

        String dayTrip = isDayTripBool ? res.getString(R.string.TripDetailsIsDayTrip) : res.getString(R.string.TripDetailsIsNotSummerTrip);
        isDayTrip.setText(trip.getName() + dayTrip);

        recommendationButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                handleRecommendationButtonClicked(v);
            }
        });

        //TODO Omer: use string file
        replyButton.setText("reply");
        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onReply(v);
            }
        });


        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        commentsListFragment = new CommentsListFragment();
        transaction.add(R.id.list_view_fragment_container, commentsListFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        return v;
    }

    private void onReply(View v) {
        EditText replyMessage = v.findViewById(R.id.reply_text);
        replyMessage.setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleRecommendationButtonClicked(final View v) {
        final TravelGuideRecommendation recommendation = travelGuide.howMuchShouldITravelNowIn(trip);
        final TextView showExtendedRecommendationTextViewSuggestion = v.findViewById(R.id.show_extended_recommendation_text_view);

        TextView briefRecommendationTextView = v.findViewById(R.id.trip_current_recommendation_grade_text_view);
        briefRecommendationTextView.setText(res.getString(R.string.OurRecommendationLabel) + recommendation.getRecommendationGrade() + "/10.");
        briefRecommendationTextView.setVisibility(View.VISIBLE);

        showExtendedRecommendationTextViewSuggestion.setText(res.getString(R.string.RecommendationProofLabel));
        showExtendedRecommendationTextViewSuggestion.setVisibility(View.VISIBLE);
        showExtendedRecommendationTextViewSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final TextView extendedRecommendationTextView = v.findViewById(R.id.extended_recommendation_text_view);
                final Button collapseExtendedRecommendationButton = v.findViewById(R.id.collapse_extended_recommendation_button);

                extendedRecommendationTextView.setText(recommendation.getExtendedRecommendation());
                extendedRecommendationTextView.setVisibility(View.VISIBLE);

                collapseExtendedRecommendationButton.setText(res.getString(R.string.CollapseExtendedRecommendation));
                collapseExtendedRecommendationButton.setVisibility(View.VISIBLE);
                collapseExtendedRecommendationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        collapseExtendedRecommendationButton.setVisibility(View.GONE);
                        extendedRecommendationTextView.setVisibility(View.GONE);
                    }
                });
            }
        });
    }
}