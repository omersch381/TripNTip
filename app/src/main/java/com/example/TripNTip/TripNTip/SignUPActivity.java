package com.example.TripNTip.TripNTip;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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

import java.util.Objects;

public class SignUPActivity extends AppCompatActivity implements Constants {

    private boolean wasCreated = false;
    private FirebaseAuth mAuth;
    private DatabaseReference mDataBase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);
        //get referance to users on data base
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
        EditText emailText = findViewById(R.id.email);
        EditText passwordText = findViewById(R.id.Password);
        EditText usernameText = findViewById(R.id.userName);

        final String email = emailText.getText().toString();
        final String password = passwordText.getText().toString();
        final String username = usernameText.getText().toString();
        // final String username = usernameText.getText().toString();
        //new check if user allready exsist in firebase


        CredentialsChecker checker = new CredentialsChecker(email, password);
        boolean areTheCredentialsValid = checker.areTheCredentialsValid();
        TNTUser user = new TNTUser(email, username, password);


        if (areTheCredentialsValid) {
            checkifUserNameAlreadyBeenTaken(user);
        } else
            //progressDialog.dismiss();

            handleInvalidSignUpRequest(checker);
    }

    public void handleNewUser(TNTUser user) {
        handleFirebaseNewUserCreation(user);
    }

    private void handleFirebaseNewUserCreation(TNTUser user) {

        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        wasCreated = task.isSuccessful();
                        String signUpMsg = wasCreated ? getResources().getString(R.string.signUpSucceededMsg) : getResources().getString(R.string.signUpFailedMsg);
                        Toast.makeText(SignUPActivity.this, signUpMsg, Toast.LENGTH_SHORT).show();
                    }
                });
        mDataBase.child(user.getUsername()).setValue(user);
    }

    private void handleInvalidSignUpRequest(CredentialsChecker checker) {
        String statusMessage = getInvalidStatusMessage(checker.getCheckerStatus());
        Toast.makeText(SignUPActivity.this, statusMessage, Toast.LENGTH_LONG).show();
    }

    private String getInvalidStatusMessage(int checkerStatus) {
        // This method is required because we don't allow the CredentialsChecker access the R file/receive activity context

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

    private void launchSignIn() {
        Intent intent = new Intent(SignUPActivity.this, SignInActivity.class);
        SignUPActivity.this.startActivity(intent);
    }

    private void checkifUserNameAlreadyBeenTaken(final TNTUser user) {
        mDataBase.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean uniqe = true;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String userName = Objects.requireNonNull(ds.child(USERNAME).getValue()).toString();
                    String email = Objects.requireNonNull(ds.child(EMAIL).getValue()).toString();
                    if (userName.equals(user.getUsername()) || email.equals(user.getEmail())) {
                        uniqe = false;
                        break;
                    }

                }

                if (uniqe) {
                    handleNewUser(user);
                    launchSignIn();
                    return;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

}