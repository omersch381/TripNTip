package com.example.TripNTip.FeatureScreens;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
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
import com.example.TripNTip.TripNTip.TravelFeedActivity;
import com.example.TripNTip.TripNTip.TravelGuide;
import com.example.TripNTip.TripNTip.Trip;
import com.example.TripNTip.Utils.Constants;
import com.google.api.SystemParameterOrBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class TripDetailsFragment extends DialogFragment implements Constants {

    private View content;
    private Trip trip;
    private TravelGuide travelGuide;
    private Bitmap tripBitmap;
    private Fragment commentsListFragment;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private String userName;
    private boolean wasWritten;


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
        //todo niv or omer -read commnet  from firebase and show on list view
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

        //TODO Omer: use string file
        replyButton.setText("reply");
        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onReply(v);
            }
        });

//        commentsListFragment.setArguments(bundle);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        commentsListFragment = new CommentsListFragment();
        transaction.add(R.id.list_view_fragment_container, commentsListFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        return v;
    }
    //todo-niv or omer: change to constant string !
    private void onReply(View v) {
        getUserName();// get specific user name of current user
        rootRef = FirebaseDatabase.getInstance().getReference().child("trips");
        mAuth = FirebaseAuth.getInstance();
        //need to check if its the first comment if not we initiate new child
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());

        builder.setTitle("new comment ");
        final EditText input = new EditText(this.getContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String message = input.getText().toString();//get input comment
                Comment comment = new Comment(message);//create new comment
                comment.setAuthor(userName);
                handleWriteComment(comment);// insert to firebase
                wasWritten = false;
            }
        });

        builder.show();
    }

    public void getUserName() {

        final FirebaseDatabase mDataBase = FirebaseDatabase.getInstance();
        final DatabaseReference reference = mDataBase.getReference(USER);
        reference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String emailOfCurrentUsre = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String emailOnDataBase = Objects.requireNonNull(ds.child("email").getValue()).toString();
                    if (emailOnDataBase.toLowerCase().equals(emailOfCurrentUsre)) {
                        userName = Objects.requireNonNull(ds.child(USERNAME).getValue()).toString();

                    }

                }
            }

            //todo niv or omer : create subitle toast
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //oast.makeText(getApplicationContext(), R.string.not_choose_photo, Toast.LENGTH_LONG).show();

            }
        });

    }


    public boolean handleWriteComment(Comment comment) {
        trip.addComment(comment);
        wasWritten = writeCommentToFirebase();
        if (!wasWritten)
            trip.getComments().remove(this);
        return wasWritten;
    }

    public boolean writeCommentToFirebase() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference tripsRef = rootRef.child("trips");
        //while(trip.getComments().getAuthor==null);
        tripsRef.child(trip.getName()).child("comments").setValue(trip.getComments(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
//                String result;
//                if (error == null)
//                    result = getResources().getString(R.string.tripWritingSuccessMsg);
//                else
//                    result = getResources().getString(R.string.tripWritingFailureMsg);
//
//                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                wasWritten = error == null;
            }
        });
        return wasWritten;
    }
}