package com.example.TripNTip.TripNTip;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.TripNTip.R;
import com.example.TripNTip.Utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
        final DatabaseReference tripsRef = rootRef.child(TRIPS_REF);

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Trip currentTrip = ds.getValue(Trip.class);
                    assert currentTrip != null;
                    trips.put(currentTrip.getName(), currentTrip);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.exit(1);
            }
        };
        tripsRef.addListenerForSingleValueEvent(eventListener);
    }

    private void loadAPIKey() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference apiKeysRef = rootRef.child(API_KEY_REF);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                    apiKey = (String) ds.getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
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

    // Launch sign up activity
    public void SignUp(View v) {
        Intent intent = new Intent(SignInActivity.this, SignUPActivity.class);
        SignInActivity.this.startActivity(intent);
    }

    private void launchTravelFeed(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(SignInActivity.this, R.string.Authentication_Succeeded, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignInActivity.this, TravelFeedActivity.class);

                            intent.putExtra(SHOULD_WE_LOAD_THE_API_KEY, false);
                            intent.putExtra(API_KEY_LABEL, apiKey);
                            intent.putExtra(SHOULD_WE_LOAD_THE_TRIPS, false);
                            intent.putExtra(TRIPS_LABEL, trips);
                            SignInActivity.this.startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(SignInActivity.this, R.string.Authentication_Faild,
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
