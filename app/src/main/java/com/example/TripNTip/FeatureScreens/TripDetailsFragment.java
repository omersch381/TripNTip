package com.example.TripNTip.FeatureScreens;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.TripNTip.R;
import com.example.TripNTip.TripNTip.Comment;
import com.example.TripNTip.TripNTip.TravelGuide;
import com.example.TripNTip.TripNTip.Trip;
import com.example.TripNTip.Utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class TripDetailsFragment extends DialogFragment implements Constants {

    private Trip trip;
    private TravelGuide travelGuide;
    private Bitmap tripBitmap;
    private Fragment commentsListFragment;
    private FirebaseAuth mAuth;
    private String userName;

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
        final View v = inflater.inflate(R.layout.trip_details_fragment, container, false);
        TextView tripName = v.findViewById(R.id.trip_details_name);
        TextView tripDescription = v.findViewById(R.id.trip_details_description);
        TextView isSummerTrip = v.findViewById(R.id.trip_details_isSummerTrip);
        TextView isDayTrip = v.findViewById(R.id.trip_details_isDayTrip);
        final Button recommendationButton = v.findViewById(R.id.trip_current_recommendation_button);
        final TextView recommendationTextView = v.findViewById(R.id.trip_current_recommendation_text_view);
        final ImageView tripImage = v.findViewById(R.id.trip_image);
        Button replyButton = v.findViewById(R.id.reply_button);


        tripImage.setImageBitmap(tripBitmap);

        tripName.setText("Trip's Name: " + trip.getName());
        tripDescription.setText("Trip's Description: " + trip.getDescription());

        boolean isSummerTripBool = trip.getSummerTrip();

        //TODO Omer: use a string which is from the stings file
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

        replyButton.setText(getResources().getString(R.string.ReplyButtonText));
        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onReply();
            }
        });

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        commentsListFragment = new CommentsListFragment(trip.getComments());
        transaction.add(R.id.list_view_fragment_container, commentsListFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        return v;
    }

    private void onReply() {
        getUserName();// get specific user name of current user
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
}