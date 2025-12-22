package com.app.earnstation;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;

public class OnboardingDisclosureActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 101;
    TextInputEditText phone_number_input, login_password_input;
    TextInputEditText signup_name_input, signup_email_input, signup_number_input, signup_password_input, signup_refer_input;
    Dialog loading_dialog;

    MaterialCardView disclosure_box, login_selectionbox, signup_with_number_layout, login_section_number_pass;

    int retry = 0;
    String pendingSessionToken = "";
    boolean isForceLogoutPending = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboardingdisclosure);
        FirebaseApp.initializeApp(this);
        checkNotificationPermission();

        MaterialCardView btnCancel = findViewById(R.id.btnCancel);
        MaterialCardView btnAgree = findViewById(R.id.btnAgree);

        disclosure_box = findViewById(R.id.disclosure_box);
        login_selectionbox = findViewById(R.id.login_selectionbox);

        phone_number_input = findViewById(R.id.phone_number_input);
        login_password_input = findViewById(R.id.login_password_input);

        signup_name_input = findViewById(R.id.signup_name_input);
        signup_email_input = findViewById(R.id.signup_email_input);
        signup_number_input = findViewById(R.id.signup_number_input);
        signup_password_input = findViewById(R.id.signup_password_input);
        signup_refer_input = findViewById(R.id.signup_refer_input);

        login_section_number_pass = findViewById(R.id.login_with_number_1st_box);
        signup_with_number_layout = findViewById(R.id.signup_with_number_layout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.purple_bg));
        }

        loading_dialog = new Dialog(this);
        loading_dialog.setContentView(R.layout.loading_layout);
        if (loading_dialog.getWindow() != null) {
            loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        loading_dialog.setCancelable(false);
        loading_dialog.show();

        btnCancel.setOnClickListener(v -> finish());

        btnAgree.setOnClickListener(v -> {
            disclosure_box.setVisibility(View.GONE);
            login_selectionbox.setVisibility(View.VISIBLE);
        });

        loading_dialog.hide();
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_CODE_POST_NOTIFICATIONS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void openPrivacyPolicy(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://cmhost.in"));
        startActivity(browserIntent);
    }

    public void login_with_google_btn(View view) {
        Toast.makeText(this, "Google Login Selected", Toast.LENGTH_SHORT).show();
    }

    public void login_with_fb_btn(View view) {
        Toast.makeText(this, "Facebook Login Selected", Toast.LENGTH_SHORT).show();
    }

    public void login_with_phone_btn(View view) {
        login_selectionbox.setVisibility(View.GONE);
        login_section_number_pass.setVisibility(View.VISIBLE);
        signup_with_number_layout.setVisibility(View.GONE);
    }

    public void sign_in_btn_fn(View view) {
        if (!phone_number_input.getText().toString().trim().isEmpty()) {
            if (!login_password_input.getText().toString().trim().isEmpty()) {
                loading_dialog.show();
                login_thread(false);
            } else {
                Toast.makeText(this, "Please Enter Valid Password", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please Enter Valid Phone Number", Toast.LENGTH_SHORT).show();
        }
    }

    public void sign_up_btn_fn(View view) {
        if (!signup_number_input.getText().toString().trim().isEmpty()) {
            if (!signup_password_input.getText().toString().trim().isEmpty()) {
                if (!signup_email_input.getText().toString().trim().isEmpty()) {
                    if (!signup_name_input.getText().toString().trim().isEmpty()) {
                        signup_thread();
                        loading_dialog.show();
                    } else {
                        Toast.makeText(this, "Please Enter Your Name", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Please Enter Your Email", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please Enter Your Password", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please Enter Your Phone Number", Toast.LENGTH_SHORT).show();
        }
    }

    private String getUniqueDeviceId() {
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceSerial = Build.SERIAL;
        if (deviceSerial != null && !deviceSerial.isEmpty() && !deviceSerial.equals("unknown")) {
            return deviceSerial;
        } else {
            return androidId;
        }
    }

    private String getDeviceInfoString() {
        return Build.BRAND + " " + Build.MODEL;
    }

    public void login_thread(boolean forceLogout) {
        String loginid_encoded = phone_number_input.getText().toString().trim();
        String loginpass_encoded = login_password_input.getText().toString().trim();
        String deviceId = getUniqueDeviceId();
        String deviceInfo = getDeviceInfoString();

        try {
            loginid_encoded = URLEncoder.encode(phone_number_input.getText().toString().trim(), "UTF-8");
            loginpass_encoded = URLEncoder.encode(login_password_input.getText().toString().trim(), "UTF-8");
        } catch (Exception e) {
            loginid_encoded = phone_number_input.getText().toString().trim();
            loginpass_encoded = login_password_input.getText().toString().trim();
        }

        String apiEndpoint = forceLogout ? "/accounts/force_logout.php" : "/accounts/loginapi.php";
        String qry = getResources().getString(R.string.app_url) + apiEndpoint +
                "?u=" + loginid_encoded +
                "&p=" + loginpass_encoded +
                "&d=" + deviceId +
                "&device_info=" + deviceInfo;

        if (forceLogout) {
            qry += "&force_logout=1";
        }

        String finalQry = qry;

        class dbprocess extends AsyncTask<String, Void, String> implements com.app.earnstation.dbprocess {
            @Override
            protected void onPostExecute(String data) {
                loading_dialog.hide();

                if (data.equals("0")) {
                    Toast.makeText(getApplicationContext(), "Invalid Credentials! Please Try Again", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    JSONObject jsonObject = new JSONObject(data);
                    String status = jsonObject.getString("status");

                    if (status.equals("user_blocked")) {
                        String message = jsonObject.getString("message");
                        showBlockedDialog("Account Blocked", message);
                        return;
                    }

                    if (status.equals("device_blocked")) {
                        String message = jsonObject.getString("message");
                        showBlockedDialog("Device Blocked", message);
                        return;
                    }

                    if (status.equals("already_logged_in")) {
                        String message = jsonObject.getString("message");
                        pendingSessionToken = jsonObject.getString("session_token");
                        showForceLogoutDialog(message);
                        return;
                    }

                    if (status.equals("0") || status.equals("Blocked")) {
                        Toast.makeText(getApplicationContext(), "Your Account has been Blocked - Contact Support", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (status.equals("2") || status.equals("Suspended")) {
                        Toast.makeText(getApplicationContext(), "Your Account has been Suspended - Contact Support", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (status.equals("Active") || status.equals("1")) {
                        String id = jsonObject.getString("id");
                        String name = jsonObject.getString("name");
                        String sessionToken = jsonObject.getString("session_token");

                        FirebaseMessaging.getInstance().getToken()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        String deviceToken = task.getResult();
                                        sendTokenToServer(id, deviceToken);
                                    }
                                });

                        FirebaseMessaging.getInstance().subscribeToTopic("all");

                        SharedPreferences sharedPreferences = getSharedPreferences("EspoTaskApp", MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        myEdit.putString("userID", id);
                        myEdit.putString("userName", name);
                        myEdit.putString("sessionToken", sessionToken);
                        myEdit.putString("deviceId", deviceId);
                        myEdit.apply();

                        Toast.makeText(getApplicationContext(), "Login Success!", Toast.LENGTH_SHORT).show();

                        get_user_data_thread(id);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Something Went Wrong! Contact Support", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                String furl = params[0];
                try {
                    URL url = new URL(furl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    return br.readLine();
                } catch (Exception ex) {
                    return ex.getMessage();
                }
            }
        }

        dbprocess obj = new dbprocess();
        obj.execute(qry);
    }

    private void showBlockedDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
    }

    private void showForceLogoutDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Already Logged In")
                .setMessage(message)
                .setPositiveButton("Yes, Continue Here", (dialog, which) -> {
                    loading_dialog.show();
                    login_thread(true);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
    }

    private void sendTokenToServer(String userId, String token) {
        String url = getResources().getString(R.string.app_url) + "/accounts/save_token.php?user_id=" + userId + "&token=" + token;
        new Thread(() -> {
            try {
                URL obj = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.getResponseCode();
            } catch (Exception e) {
                Log.e("FCM", "Error sending token: " + e.getMessage());
            }
        }).start();
    }

    public void signup_with_phone_btn_inselection_layout(View view) {
        login_selectionbox.setVisibility(View.GONE);
        login_section_number_pass.setVisibility(View.GONE);
        signup_with_number_layout.setVisibility(View.VISIBLE);
    }

    public void goback_to_login_options(View view) {
        login_selectionbox.setVisibility(View.VISIBLE);
        login_section_number_pass.setVisibility(View.GONE);
        signup_with_number_layout.setVisibility(View.GONE);
    }

    public void goback_to_login_from_signup(View view) {
        login_selectionbox.setVisibility(View.GONE);
        login_section_number_pass.setVisibility(View.VISIBLE);
        signup_with_number_layout.setVisibility(View.GONE);
    }

    public void goto_signup_from_login(View view) {
        login_selectionbox.setVisibility(View.GONE);
        login_section_number_pass.setVisibility(View.GONE);
        signup_with_number_layout.setVisibility(View.VISIBLE);
    }

    public void get_user_data_thread(String user_id) {
        String get_user_data_qry = getResources().getString(R.string.app_url) + "/user/get_view_homescrdata.php?";
        String datatohash = "";
        try {
            datatohash = "i=" + URLEncoder.encode(user_id, "UTF-8");
            String token = temp.sha256_temp(datatohash);
            get_user_data_qry = get_user_data_qry + datatohash + "&token=" + token;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String finalget_user_data_qry = get_user_data_qry;

        class dbprocess extends AsyncTask<String, Void, String> implements com.app.earnstation.dbprocess {
            @Override
            protected void onPostExecute(String data) {
                if (data.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Something went Wrong! Contact Support Now", Toast.LENGTH_SHORT).show();
                    loading_dialog.hide();
                } else if (data.equals("0")) {
                    Toast.makeText(getApplicationContext(), "Something went Wrong! Contact Support Now", Toast.LENGTH_SHORT).show();
                    loading_dialog.hide();
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        String status = jsonObject.getString("status");
                        String email = jsonObject.getString("email");
                        String owncode = jsonObject.getString("owncode");
                        String balance = jsonObject.getString("balance");
                        String kyc = jsonObject.getString("kyc");
                        String name = jsonObject.getString("name");

                        SharedPreferences sharedPreferences = getSharedPreferences("EspoTaskApp", MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        myEdit.putString("status", status);
                        myEdit.putString("email", email);
                        myEdit.putString("owncode", owncode);
                        myEdit.putString("balance", balance);
                        myEdit.putString("kyc", kyc);
                        myEdit.putString("name", name);
                        myEdit.apply();

                        if (status.equals("Blocked") || status.equals("0")) {
                            Toast.makeText(getApplicationContext(), "Your Account has been Blocked - Contact Support", Toast.LENGTH_SHORT).show();
                            loading_dialog.hide();
                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(OnboardingDisclosureActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    loading_dialog.hide();
                                    finish();
                                }
                            }, 1);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        loading_dialog.hide();
                        Toast.makeText(OnboardingDisclosureActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            protected String doInBackground(String... params) {
                String furl = params[0];
                try {
                    URL url = new URL(furl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    return br.readLine();
                } catch (Exception ex) {
                    return ex.getMessage();
                }
            }
        }

        dbprocess obj = new dbprocess();
        obj.execute(finalget_user_data_qry);
    }

    public void signup_thread() {
        String Referedby = signup_refer_input.getText().toString().trim();
        String DeviceID = getUniqueDeviceId();

        if (Referedby.isEmpty()) {
            Referedby = "NA";
        }

        String deviceModel = Build.MODEL;
        String deviceManufacturer = Build.MANUFACTURER;
        String deviceBrand = Build.BRAND;
        String deviceSerial = Build.SERIAL;

        String signup_qry = getResources().getString(R.string.app_url) + "/accounts/signupapi.php?";
        String datatohash = "";
        try {
            datatohash = "e=" + URLEncoder.encode(signup_email_input.getText().toString().trim(), "UTF-8") +
                    "&m=" + URLEncoder.encode(signup_number_input.getText().toString().trim(), "UTF-8") +
                    "&p=" + URLEncoder.encode(signup_password_input.getText().toString().trim(), "UTF-8") +
                    "&n=" + URLEncoder.encode(signup_name_input.getText().toString().trim(), "UTF-8") +
                    "&r=" + URLEncoder.encode(Referedby, "UTF-8") +
                    "&d=" + URLEncoder.encode(DeviceID, "UTF-8") +
                    "&deviceModel=" + URLEncoder.encode(deviceModel, "UTF-8") +
                    "&deviceManufacturer=" + URLEncoder.encode(deviceManufacturer, "UTF-8") +
                    "&deviceBrand=" + URLEncoder.encode(deviceBrand, "UTF-8") +
                    "&deviceSerial=" + URLEncoder.encode(deviceSerial, "UTF-8");
            String token = temp.sha256_temp(datatohash);
            signup_qry = signup_qry + datatohash + "&token=" + token;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String finalSignup_qry = signup_qry;

        class dbprocess extends AsyncTask<String, Void, String> implements com.app.earnstation.dbprocess {
            @Override
            protected void onPostExecute(String data) {
                loading_dialog.hide();

                if (data.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Something Went Wrong! Please Try Again", Toast.LENGTH_SHORT).show();
                } else if (data.equals("0")) {
                    Toast.makeText(getApplicationContext(), "Something Went Wrong! Please Try Again", Toast.LENGTH_SHORT).show();
                } else if (data.equals("device_blocked")) {
                    showBlockedDialog("Device Blocked", "This device has been blocked and cannot be used to create a new account. Please contact support.");
                } else if (data.equals("3")) {
                    Toast.makeText(getApplicationContext(), "Device Already Registered With Us", Toast.LENGTH_SHORT).show();
                } else if (data.equals("4")) {
                    Toast.makeText(getApplicationContext(), "Mobile Number Already Registered With Us", Toast.LENGTH_SHORT).show();
                } else if (data.equals("5")) {
                    Toast.makeText(getApplicationContext(), "Invalid Refer Code", Toast.LENGTH_SHORT).show();
                } else if (data.equals("2")) {
                    Toast.makeText(getApplicationContext(), "Email Already Registered With Us", Toast.LENGTH_SHORT).show();
                } else if (data.equals("1")) {
                    Toast.makeText(getApplicationContext(), "Signup Success! Please Login Now", Toast.LENGTH_SHORT).show();
                    signup_with_number_layout.setVisibility(View.GONE);
                    login_section_number_pass.setVisibility(View.VISIBLE);
                    phone_number_input.setText(signup_number_input.getText().toString().trim());
                    login_password_input.setText(signup_password_input.getText().toString().trim());
                } else {
                    if (retry < 3) {
                        retry++;
                        signup_thread();
                    } else {
                        Toast.makeText(getApplicationContext(), "Something Went Wrong! Please Try Again Later", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            protected String doInBackground(String... params) {
                String furl = params[0];
                try {
                    URL url = new URL(furl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    return br.readLine();
                } catch (Exception ex) {
                    return ex.getMessage();
                }
            }
        }

        dbprocess obj = new dbprocess();
        obj.execute(signup_qry);
    }
}