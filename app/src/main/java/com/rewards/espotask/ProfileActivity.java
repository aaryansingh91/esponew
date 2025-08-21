package com.rewards.espotask;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    TextInputEditText editFirstName, editLastName, editEmail, editMobile, editDob, editOldPass, editNewPass;
    RadioGroup genderGroup;
    Button btnUpdateProfile, btnChangePassword;
    ImageView profileImage;

    String userId = "0"; // 👈 define globally

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        initViews();

        // Setup date picker
        setupDatePicker();
        SharedPreferences sharedPreferences = getSharedPreferences("EspoTaskApp", MODE_PRIVATE);
        userId = sharedPreferences.getString("userID", "0"); // 👈 assign value to global variable

        // Fetch user details
        fetchUserDetails();

        // Set click listeners
        btnUpdateProfile.setOnClickListener(v -> updateProfile());
        btnChangePassword.setOnClickListener(v -> changePassword());
    }

    private void initViews() {
        editFirstName = findViewById(R.id.edit_first_name);
        editLastName = findViewById(R.id.edit_last_name);
        editEmail = findViewById(R.id.edit_email);
        editMobile = findViewById(R.id.edit_mobile);
        editDob = findViewById(R.id.edit_dob);
        editOldPass = findViewById(R.id.edit_old_password);
        editNewPass = findViewById(R.id.edit_new_password);
        genderGroup = findViewById(R.id.gender_group);
        btnUpdateProfile = findViewById(R.id.update_profile_button);
        btnChangePassword = findViewById(R.id.reset_button);
        profileImage = findViewById(R.id.profile_image);
    }

    private void setupDatePicker() {
        editDob.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = String.format("%04d-%02d-%02d",
                                selectedYear, selectedMonth + 1, selectedDay);
                        editDob.setText(selectedDate);
                    }, year, month, day);

            datePickerDialog.show();
        });
    }

    private void fetchUserDetails() {
        String url = getString(R.string.app_url) + "/fetch_user_profile.php?user_id=" + userId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            JSONObject user = obj.getJSONObject("user");

                            String name = user.getString("name");
                            String[] parts = name.split(" ", 2);
                            editFirstName.setText(parts[0]);
                            if (parts.length > 1) editLastName.setText(parts[1]);

                            editEmail.setText(user.getString("email"));
                            editMobile.setText(user.getString("mobile"));
                            editDob.setText(user.optString("dob", ""));

                            String gender = user.optString("gender", "");
                            if (gender.equalsIgnoreCase("Male")) {
                                genderGroup.check(R.id.radio_male);
                            } else if (gender.equalsIgnoreCase("Female")) {
                                genderGroup.check(R.id.radio_female);
                            }

                            String avatar = user.optString("avatar", "");
                            if (!TextUtils.isEmpty(avatar)) {
                                Glide.with(this).load(avatar).into(profileImage);
                            }
                        } else {
                            Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void updateProfile() {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        String url = getString(R.string.app_url) + "/update_profile.php";

        int selectedId = genderGroup.getCheckedRadioButtonId();
        String selectedGender = "";

        if (selectedId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedId);
            selectedGender = selectedRadioButton.getText().toString();
        }

        String finalSelectedGender = selectedGender;

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                        if (obj.getBoolean("success")) {
                            // Profile updated successfully
                            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("name", editFirstName.getText().toString().trim() + " " +
                        editLastName.getText().toString().trim());
                params.put("email", editEmail.getText().toString().trim());
                params.put("mobile", editMobile.getText().toString().trim());
                params.put("dob", editDob.getText().toString().trim());
                params.put("gender", finalSelectedGender);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void changePassword() {
        String oldPass = editOldPass.getText().toString().trim();
        String newPass = editNewPass.getText().toString().trim();

        if (TextUtils.isEmpty(oldPass)) {
            editOldPass.setError("Enter old password");
            editOldPass.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(newPass)) {
            editNewPass.setError("Enter new password");
            editNewPass.requestFocus();
            return;
        }

        if (newPass.length() < 6) {
            editNewPass.setError("Password must be at least 6 characters");
            editNewPass.requestFocus();
            return;
        }

        String url = getString(R.string.app_url) + "/change_password.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                        if (obj.getBoolean("success")) {
                            // Password changed successfully, clear fields
                            editOldPass.setText("");
                            editNewPass.setText("");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error changing password", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Password change failed", Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("old_password", oldPass);
                params.put("new_password", newPass);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private boolean validateInputs() {
        String firstName = editFirstName.getText().toString().trim();
        String lastName = editLastName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String mobile = editMobile.getText().toString().trim();

        if (TextUtils.isEmpty(firstName)) {
            editFirstName.setError("First name is required");
            editFirstName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(lastName)) {
            editLastName.setError("Last name is required");
            editLastName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            editEmail.setError("Email is required");
            editEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Enter valid email");
            editEmail.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(mobile)) {
            editMobile.setError("Mobile number is required");
            editMobile.requestFocus();
            return false;
        }

        if (mobile.length() < 10) {
            editMobile.setError("Enter valid mobile number");
            editMobile.requestFocus();
            return false;
        }

        return true;
    }
}