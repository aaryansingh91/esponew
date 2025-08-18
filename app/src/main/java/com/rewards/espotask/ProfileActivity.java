package com.rewards.espotask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ProfileActivity extends AppCompatActivity {

    // View references
    private TextInputLayout inputFirstName, inputLastName, inputUsername, inputEmail, inputMobile, inputDob;
    private TextInputEditText editFirstName, editLastName, editUsername, editEmail, editMobile, editDob;
    private MaterialRadioButton radioMale, radioFemale;
    private TextInputLayout inputOldPassword, inputNewPassword;
    private TextInputEditText editOldPassword, editNewPassword;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "UserProfile";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_DOB = "dob";
    private static final String KEY_GENDER = "gender";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Initialize views
        initViews();
        loadUserData();
        setupClickListeners();
    }

    private void initViews() {
        inputFirstName = findViewById(R.id.input_first_name);
        editFirstName = findViewById(R.id.edit_first_name);

        inputLastName = findViewById(R.id.input_last_name);
        editLastName = findViewById(R.id.edit_last_name);

        inputUsername = findViewById(R.id.input_username);
        editUsername = findViewById(R.id.edit_username);

        inputEmail = findViewById(R.id.input_email);
        editEmail = findViewById(R.id.edit_email);

        inputMobile = findViewById(R.id.input_mobile);
        editMobile = findViewById(R.id.edit_mobile);

        inputDob = findViewById(R.id.input_dob);
        editDob = findViewById(R.id.edit_dob);

        radioMale = findViewById(R.id.radio_male);
        radioFemale = findViewById(R.id.radio_female);

        inputOldPassword = findViewById(R.id.input_old_password);
        editOldPassword = findViewById(R.id.edit_old_password);

        inputNewPassword = findViewById(R.id.input_new_password);
        editNewPassword = findViewById(R.id.edit_new_password);

        findViewById(R.id.update_profile_button).setOnClickListener(v -> updateProfile());
        findViewById(R.id.reset_button).setOnClickListener(v -> resetPassword());
    }

    private void loadUserData() {
        editFirstName.setText(sharedPreferences.getString(KEY_FIRST_NAME, "Aaryan"));
        editLastName.setText(sharedPreferences.getString(KEY_LAST_NAME, "Singh"));
        editUsername.setText(sharedPreferences.getString(KEY_USERNAME, "aaryansingh"));
        editEmail.setText(sharedPreferences.getString(KEY_EMAIL, "aaryansingh1356@gmail.com"));
        editMobile.setText(sharedPreferences.getString(KEY_MOBILE, "+91 9598321356"));
        editDob.setText(sharedPreferences.getString(KEY_DOB, ""));
        String gender = sharedPreferences.getString(KEY_GENDER, "male");
        if ("male".equals(gender)) {
            radioMale.setChecked(true);
        } else {
            radioFemale.setChecked(true);
        }
    }

    private void setupClickListeners() {
        // Click listeners are set in initViews for buttons
    }

    private void updateProfile() {
        if (!validateInputs()) {
            return;
        }

        String firstName = editFirstName.getText().toString().trim();
        String lastName = editLastName.getText().toString().trim();
        String username = editUsername.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String mobile = editMobile.getText().toString().trim();
        String dob = editDob.getText().toString().trim();
        String gender = radioMale.isChecked() ? "male" : "female";

        saveUserData(firstName, lastName, username, email, mobile, dob, gender);
        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
    }

    private void resetPassword() {
        if (!validatePasswordInputs()) {
            return;
        }

        String oldPassword = editOldPassword.getText().toString().trim();
        String newPassword = editNewPassword.getText().toString().trim();

        // Simulate password reset (replace with actual API call)
        saveUserData(
                editFirstName.getText().toString().trim(),
                editLastName.getText().toString().trim(),
                editUsername.getText().toString().trim(),
                editEmail.getText().toString().trim(),
                editMobile.getText().toString().trim(),
                editDob.getText().toString().trim(),
                radioMale.isChecked() ? "male" : "female"
        );
        Toast.makeText(this, "Password reset successfully", Toast.LENGTH_SHORT).show();
        clearPasswordFields();
    }

    private boolean validateInputs() {
        boolean isValid = true;

        clearErrors();

        String firstName = editFirstName.getText().toString().trim();
        if (TextUtils.isEmpty(firstName)) {
            inputFirstName.setError("First name is required");
            isValid = false;
        }

        String lastName = editLastName.getText().toString().trim();
        if (TextUtils.isEmpty(lastName)) {
            inputLastName.setError("Last name is required");
            isValid = false;
        }

        String username = editUsername.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            inputUsername.setError("Username is required");
            isValid = false;
        }

        String email = editEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Email is required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Please enter a valid email");
            isValid = false;
        }

        String mobile = editMobile.getText().toString().trim();
        if (TextUtils.isEmpty(mobile)) {
            inputMobile.setError("Mobile number is required");
            isValid = false;
        } else if (mobile.length() < 10) {
            inputMobile.setError("Please enter a valid mobile number");
            isValid = false;
        }

        return isValid;
    }

    private boolean validatePasswordInputs() {
        boolean isValid = true;

        clearErrors();

        String oldPassword = editOldPassword.getText().toString().trim();
        if (TextUtils.isEmpty(oldPassword)) {
            inputOldPassword.setError("Old password is required");
            isValid = false;
        }

        String newPassword = editNewPassword.getText().toString().trim();
        if (TextUtils.isEmpty(newPassword)) {
            inputNewPassword.setError("New password is required");
            isValid = false;
        } else if (newPassword.length() < 6) {
            inputNewPassword.setError("Password must be at least 6 characters");
            isValid = false;
        }

        return isValid;
    }

    private void clearErrors() {
        inputFirstName.setError(null);
        inputLastName.setError(null);
        inputUsername.setError(null);
        inputEmail.setError(null);
        inputMobile.setError(null);
        inputDob.setError(null);
        inputOldPassword.setError(null);
        inputNewPassword.setError(null);
    }

    private void saveUserData(String firstName, String lastName, String username, String email, String mobile, String dob, String gender) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_FIRST_NAME, firstName);
        editor.putString(KEY_LAST_NAME, lastName);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_MOBILE, mobile);
        editor.putString(KEY_DOB, dob);
        editor.putString(KEY_GENDER, gender);
        editor.apply();
    }

    private void clearPasswordFields() {
        editOldPassword.setText("");
        editNewPassword.setText("");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}