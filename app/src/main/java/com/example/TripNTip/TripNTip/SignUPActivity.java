package com.example.TripNTip.TripNTip;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.TripNTip.R;
import com.example.TripNTip.Utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUPActivity extends AppCompatActivity implements Constants {

    private boolean wasCreated = false;
    private FirebaseAuth mAuth;
    private DatabaseReference mDataBase;
    private TNTUser newUser;
    private  final String USERS= "users";
    private final String  USER="user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);

        mDataBase = FirebaseDatabase.getInstance().getReference(USERS);


        mAuth = FirebaseAuth.getInstance();
        Button signUp = findViewById(R.id.signUp);

        signUp.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                createAccount();
            }
        });
    }


    private void createAccount() {
        //TODO: Niv - does the method signs you up every run?

        EditText emailText = findViewById(R.id.email);
        EditText passwordText = findViewById(R.id.Password);
        EditText usernameText=findViewById(R.id.userName);
//        EditText countryText = (EditText) findViewById(R.id.country);
        final String email = emailText.getText().toString();
        final String password = passwordText.getText().toString();
        final String username=usernameText.getText().toString();

        CredentialsChecker checker = new CredentialsChecker(email, password);
        boolean isValid = checker.areTheCredentialsValid();
        TNTUser user=new TNTUser(email,username,password);
        if (isValid) {

             handleNewUser(email, password);
             launchSignIn(user);
        } else
            handleInvalidSignUpRequest(checker);
    }

    public void handleNewUser(String email,String password) {
        handleFirebaseNewUserCreation(email,password);


    }

    private void handleFirebaseNewUserCreation(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        wasCreated = task.isSuccessful();
                        String signUpMsg = wasCreated ? getResources().getString(R.string.signUpSucceededMsg) : getResources().getString(R.string.signUpFailedMsg);
                        Toast.makeText(SignUPActivity.this, signUpMsg, Toast.LENGTH_SHORT).show();
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


    private void launchSignIn(TNTUser user) {
        String key = mDataBase.push().getKey();
        mDataBase.child(user.getUsername()).setValue(user);
        Intent intent = new Intent(SignUPActivity.this, SignInActivity.class);
        SignUPActivity.this.startActivity(intent);
    }
}