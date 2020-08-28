package com.example.TripNTip.FeatureScreens;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.TripNTip.R;
import com.example.TripNTip.TripNTip.Comment;
import com.example.TripNTip.TripNTip.TravelGuide;
import com.example.TripNTip.TripNTip.Trip;
import com.example.TripNTip.Utils.Constants;
import com.example.TripNTip.WeatherAPI.TravelGuideRecommendation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class TripDetailsFragment extends DialogFragment implements Constants {

    /**
     * TripDetailsFragment displays the trips details.
     *
     * It received the data from the GridFragment and shows it in an AlertDialog
     */


    private Trip trip;
    private TravelGuide travelGuide;
    private Bitmap tripBitmap;
    private Fragment commentsListFragment;
    private FirebaseAuth mAuth;
    private String userName;
    private Resources res;

    public TripDetailsFragment() {
    }

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
        final Button recommendationButton = v.findViewById(R.id.trip_current_recommendation_button);
        Button replyButton = v.findViewById(R.id.reply_button);
        boolean isSummerTripBool = trip.getSummerTrip();
        boolean isDayTripBool = trip.getDayTrip();

        tripImage.setImageBitmap(tripBitmap);

        String tripName = res.getString(R.string.TripDetailsName) + trip.getName();
        tripNameTextView.setText(tripName);

        String tripDescription = res.getString(R.string.TripDetailsDescription) + trip.getDescription();
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

        replyButton.setText(getResources().getString(R.string.ReplyButtonText));
        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onReply();
            }
        });

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        commentsListFragment = new CommentsListFragment(trip.getComments());

//        FrameLayout frameLayout = v.findViewById(R.id.list_view_fragment_container);
//        getResources().getInteger(R.attr.listPreferredItemHeightSmall);
//        frameLayout.setMinimumHeight(frameLayout.ge);
//
//        Log.d("NOTEEEEEEEE", getItemHeight());

        transaction.add(R.id.list_view_fragment_container, commentsListFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        return v;
    }

    private void onReply() {
        getUserName();// get specific user name of current user
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("trips");
        mAuth = FirebaseAuth.getInstance();
        //need to check if its the first comment if not we initiate new child
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());

        // FixMe Niv: change to a string from the string file
        builder.setTitle("new comment ");
        final EditText input = new EditText(this.getContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        // FixMe Niv: change to a string from the string file
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String message = input.getText().toString();
                Comment comment = new Comment(message);
                comment.setAuthor(userName);

                handleWriteComment(comment); // write to firebase
            }
        });

        builder.show();
    }

    public void getUserName() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference(USER);
        reference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String emailOfCurrentUser = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String emailOnDataBase = Objects.requireNonNull(ds.child("email").getValue()).toString();
                    assert emailOfCurrentUser != null;
                    if (emailOnDataBase.toLowerCase().equals(emailOfCurrentUser.toLowerCase()))
                        userName = Objects.requireNonNull(ds.child(USERNAME).getValue()).toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), R.string.UserNameFromFirebaseError, Toast.LENGTH_LONG).show();
            }
        });
    }


    public void handleWriteComment(Comment comment) {
        trip.addComment(comment);
        writeCommentToFirebase();

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        commentsListFragment = new CommentsListFragment(trip.getComments());
        transaction.replace(R.id.list_view_fragment_container, commentsListFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void writeCommentToFirebase() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference tripsRef = rootRef.child("trips");
        tripsRef.child(trip.getName()).child("comments").setValue(trip.getComments());
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