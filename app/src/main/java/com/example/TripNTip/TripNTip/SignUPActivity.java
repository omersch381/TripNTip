package com.example.TripNTip;

import android.content.Intent;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.Utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUPActivity extends AppCompatActivity implements Constants {

    private boolean wasCreated = false;
    private FirebaseAuth mAuth;
    private DatabaseReference mDataBase;
    private TNTUser newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);

        mDataBase = FirebaseDatabase.getInstance().getReference("user");
        System.out.println(mDataBase.getRef());
        mAuth = FirebaseAuth.getInstance();
        Button signUp = findViewById(R.id.signUp);
        signUp.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                createAccount();
            }
        });
    }


    private void createAccount() {
        EditText emailText = findViewById(R.id.email);
        EditText passwordText = findViewById(R.id.Password);
//        EditText countryText = (EditText) findViewById(R.id.country);
        final String email = emailText.getText().toString();
        final String password = passwordText.getText().toString();
        //TODO: country
        final String country = "Israel";
        final String username = "niv naory";
        CredentialsChecker checker = new CredentialsChecker(email, password, country);
        boolean isValid = checker.areTheCredentialsValid();
        if (isValid) {
            newUser = handleNewUser(email, username, password, country);
            launchTravelFeed();
        } else
            handleInvalidSignUpRequest(checker);
    }

    public TNTUser handleNewUser(String email, String userName, String password, String country) {
        //TODO: we need to check how to store a TNTUSer record in the Firebase DB
        handleFirebaseNewUserCreation(email, password);

        //if (!wasCreated)
        //  return null;

        return new TNTUser(email, userName, password, country);

    }

    private void handleFirebaseNewUserCreation(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            wasCreated = true;
                            Toast.makeText(SignUPActivity.this, R.string.signUpSucceededMsg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUPActivity.this, R.string.signUpFailedMsg, Toast.LENGTH_SHORT).show();
                            wasCreated = false;
                        }
                    }
                });
    }

    private void handleInvalidSignUpRequest(CredentialsChecker checker) {
        String statusMessage = getInvalidStatusMessage(checker.getCheckerStatus());
        Toast.makeText(SignUPActivity.this, statusMessage, Toast.LENGTH_LONG).show();
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

    ///NIV NAORY THE KING
    private void launchTravelFeed() {
        String key = mDataBase.push().getKey();
        mDataBase.child(key).setValue(newUser);
        Intent intent = new Intent(SignUPActivity.this, TravelFeedActivity.class);
        SignUPActivity.this.startActivity(intent);
    }
}