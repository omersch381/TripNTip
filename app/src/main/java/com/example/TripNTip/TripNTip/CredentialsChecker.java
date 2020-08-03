package com.example.TripNTip.TripNTip;

import com.example.TripNTip.Utils.Constants;

import java.util.regex.Pattern;

public class CredentialsChecker implements Constants {
    private String email = "";
    private String password = "";
    private int checkerStatus = -1;

    public CredentialsChecker(String email, String password) {
        this.email = email;
        this.password = password;
    }



    public  boolean areTheCredentialsValid() {
        // The returnMessage will be assigned inside the checker methods.

        boolean isEmailValid = checkEmail(this.email);
        if (isEmailValid)
            return checkPassword(this.password);
        // Country will be valid, because we let the user choose one from our list.

        return false;
    }

    private boolean checkEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        if (email == null || email.isEmpty()) {
            this.checkerStatus = EMPTY_EMAIL_MESSAGE;
            return false;
        }
        boolean isEmailValid = pattern.matcher(email).matches();
        if (!isEmailValid) {
            this.checkerStatus = INVALID_EMAIL_MESSAGE;
            return false;
        }
        return true;
    }

    private boolean checkPassword(String password) {
        /* A password needs to meet the following conditions:
         * 1. Password's length has to be between 8 to 14 characters
         * 2. Password must contain at least one uppercase and one lowercase character and a digit
         * 3. Password must not contain the username
         * */

        if (password.length() < PASSWORD_MINIMAL_LENGTH || password.length() > PASSWORD_MAXIMAL_LENGTH) {
            this.checkerStatus = INVALID_PASSWORD_LENGTH_MESSAGE;
            return false;
        }
        if (!doesThePasswordHasAtLeastOneUpperAndLowercaseAndDigitChars(password)) {
            this.checkerStatus = INVALID_PASSWORD_CASE_MESSAGE;
            return false;
        }
        if (password.contains(extractUsernameFrom(this.email))) {
            this.checkerStatus = PASSWORD_CONTAINS_USERNAME_MESSAGE;
            return false;
        }
        return true;
    }

    private boolean doesThePasswordHasAtLeastOneUpperAndLowercaseAndDigitChars(String password) {
        boolean containsLower = false;
        boolean containsUpper = false;
        boolean containsDigit = false;
        char currentChar;

        for (int i = 0; i < password.length(); i++) {
            currentChar = password.charAt(i);
            if (Character.isUpperCase(currentChar))
                containsUpper = true;
            else if (Character.isLowerCase(currentChar))
                containsLower = true;
            else if (Character.isDigit(currentChar)) {
                containsDigit = true;
            }
        }
        return containsLower && containsUpper && containsDigit;
    }

    private CharSequence extractUsernameFrom(String email) {
        // From our perspective, username = the part before the @ sign.

        int atIndex = 0;
        for (int index = 0; index < email.length(); index++) {
            if (email.charAt(index) != '@')
                atIndex++;
            else
                return email.substring(0, atIndex);
        }
        return "";
    }


    public int getCheckerStatus() {
        return this.checkerStatus;
    }
}
