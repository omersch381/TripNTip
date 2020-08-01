package com.example.TripNTip.TripNTip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.TripNTip.CredentialsChecker;
import com.example.TripNTip.R;
import com.example.TripNTip.SignUPActivity;
import com.example.TripNTip.TravelFeedActivity;
import com.example.TripNTip.Trip;
import com.example.Utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SignInActivity extends AppCompatActivity implements Constants {
    private FirebaseAuth mAuth;
    private String apiKey;
    private HashMap<String, Trip> trips;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_activity);

        mAuth = FirebaseAuth.getInstance();
        Button login = findViewById(R.id.login);

        trips = new HashMap<>();

//        // For Testing Purposes only!!
//        Intent intent = new Intent(SignInActivity.this, TravelFeedActivity.class);
//        SignInActivity.this.startActivity(intent);

        // For Testing Purposes only!!
//        launchTravelFeed("niv@gmail.com", "NivNiv1993");

        loadInitialData();

        login.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                SignIn();
            }
        });
    }

    private void loadInitialData() {
        loadTrips();
        loadAPIKey();
    }

    private void loadTrips() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference tripsRef = rootRef.child("trips");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Trip currentTrip = ds.getValue(Trip.class);
                    trips.put(currentTrip.getName(), currentTrip);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.exit(1);
            }
        };
        tripsRef.addListenerForSingleValueEvent(eventListener);
    }

    private void loadAPIKey() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference apiKeysRef = rootRef.child("apiKeys");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                    apiKey = (String) ds.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.exit(1);
            }
        };
        apiKeysRef.addListenerForSingleValueEvent(eventListener);
    }

    private void SignIn() {
        EditText emailText = findViewById(R.id.email);
        EditText passwordText = findViewById(R.id.Password);

        final String email = emailText.getText().toString();
        final String password = passwordText.getText().toString();

        CredentialsChecker checker = new CredentialsChecker(email, password);
        boolean isSignInValid = checker.areTheCredentialsValid();

        if (isSignInValid)
            launchTravelFeed(email, password);
        else
            handleInvalidSignInRequest(checker);
    }

    // launch to sign up activity
    public void SignUp(View v) {
        Intent intent = new Intent(SignInActivity.this, SignUPActivity.class);
        SignInActivity.this.startActivity(intent);
    }

    // try to change this in way that laucnh will get user insted of email,password
    private void launchTravelFeed(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            //TODO: Niv - please use Strings file instead of Strings
                            Toast.makeText(SignInActivity.this, "Authentication Succeeded!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignInActivity.this, TravelFeedActivity.class);
                            intent.putExtra("apiKey", apiKey);
                            intent.putExtra("trips", trips);
                            SignInActivity.this.startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignInActivity.this, "Authentication Failed!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void handleInvalidSignInRequest(CredentialsChecker checker) {
        String statusMessage = getInvalidStatusMessage(checker.getCheckerStatus());
        Toast.makeText(SignInActivity.this, statusMessage, Toast.LENGTH_LONG).show();
    }

    private String getInvalidStatusMessage(int checkerStatus) {
        // This method is required because we don't allow the CredentialsChecker access the R file

        String statusMessage = "";
        switch (checkerStatus) {
            case EMPTY_EMAIL_MESSAGE:
                statusMessage = getString(R.string.emptyEmailSignUpAttemptMsg);
                break;
            case INVALID_EMAIL_MESSAGE:
                statusMessage = getString(R.string.invalidEmailSignUpAttemptMsg);
                break;
            case EMPTY_PASSWORD_MESSAGE:
                statusMessage = getString(R.string.emptyPasswordSignUpAttemptMsg);
                break;
            case INVALID_PASSWORD_LENGTH_MESSAGE:
                statusMessage = getString(R.string.invalidPasswordLengthSignUpAttemptMsg);
                break;
            case INVALID_PASSWORD_CASE_MESSAGE:
                statusMessage = getString(R.string.invalidPasswordCaseSignUpAttemptMsg);
                break;
            default: // which is PASSWORD_CONTAINS_USERNAME_MESSAGE
                statusMessage = getString(R.string.passwordContainsUsernameSignUpAttemptMsg);
                break;
        }
        return statusMessage;
    }
}
