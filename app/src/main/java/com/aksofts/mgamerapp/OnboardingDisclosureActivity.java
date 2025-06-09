package com.aksofts.mgamerapp;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

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

    TextInputEditText phone_number_input,login_password_input;
    TextInputEditText signup_name_input,signup_email_input, signup_number_input, signup_password_input, signup_refer_input;
    Dialog loading_dialog;

//    MaterialCardView  signup_with_number_layout ;
    MaterialCardView disclosure_box , login_selectionbox;
    ScrollView signup_with_number_layout, login_section_number_pass;

    int retry = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboardingdisclosure);


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
        if (loading_dialog.getWindow()!=null)
        {
            loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        loading_dialog.setCancelable(false);
        loading_dialog.show();


        btnCancel.setOnClickListener(v -> finish()); // Close Activity

        btnAgree.setOnClickListener(v -> {
            // Navigate to the main screen after agreement
            disclosure_box.setVisibility(View.GONE);
            login_selectionbox.setVisibility(View.VISIBLE);
        });


        loading_dialog.hide();
    }

    public void openPrivacyPolicy(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://cmhost.in"));
        startActivity(browserIntent);
    }

    public void login_with_google_btn(View view) {
        Toast.makeText(this, "Google Login Selected", Toast.LENGTH_SHORT).show();
        // Add your Google login logic here

    }

    public void login_with_fb_btn(View view) {
        Toast.makeText(this, "Facebook Login Selected", Toast.LENGTH_SHORT).show();
    }

    public void login_with_phone_btn(View view) {
        login_selectionbox.setVisibility(View.GONE);
        login_section_number_pass.setVisibility(View.VISIBLE);
        signup_with_number_layout.setVisibility(View.GONE);
    }

    public void sign_in_btn_fn(View view){
        if (!phone_number_input.getText().toString().trim().isEmpty()){
            if (!login_password_input.getText().toString().trim().isEmpty()){
                login_thread();
            } else {
                Toast.makeText(this, "Please Enter Valid Password", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Please Enter Valid Phone Number", Toast.LENGTH_SHORT).show();
        }
    }

    public void sign_up_btn_fn(View view){
        if (!signup_number_input.getText().toString().trim().isEmpty()){
            if (!signup_password_input.getText().toString().trim().isEmpty()){
                signup_thread();
                loading_dialog.show();
            } else {
                Toast.makeText(this, "Please Enter Your Registered Password", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Please Enter Your Registered Phone Number", Toast.LENGTH_SHORT).show();
        }
    }


    public void login_thread(){
        String loginid_encoded = phone_number_input.getText().toString().trim();
        String loginpass_encoded = login_password_input.getText().toString().trim();
        try {
            loginid_encoded = URLEncoder.encode(phone_number_input.getText().toString().trim(), "UTF-8");
            loginpass_encoded = URLEncoder.encode(login_password_input.getText().toString().trim(), "UTF-8");
        } catch(Exception e){
            loginid_encoded = phone_number_input.getText().toString().trim();
            loginpass_encoded = login_password_input.getText().toString().trim();
        }

        String qry = getResources().getString(R.string.app_url) + "/accounts/loginapi.php?u=" + loginid_encoded + "&p=" + loginpass_encoded;
        class dbprocess extends AsyncTask<String, Void, String> implements com.aksofts.mgamerapp.dbprocess {
            @Override
            protected void onPostExecute(String data) {
                if (data.equals("0")){
                    Toast.makeText(getApplicationContext(), "Invalid Credentials ! - Please Try Again", Toast.LENGTH_SHORT).show();
                    String preview_message = null;
                    try {
                        preview_message = "Login Failed - Data "+data + " <br>Request - "+ URLEncoder.encode(phone_number_input.getText().toString().trim(), "UTF-8") + " and pass = " + URLEncoder.encode(login_password_input.getText().toString().trim(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        sendErrortoServer.sendErrorToServer(
                                OnboardingDisclosureActivity.this,  // Context
                                phone_number_input.getText().toString().trim(),
                                "Login Failed - Data " + data + " Request - " + URLEncoder.encode(qry, "UTF-8"),
                                "Login Screen Issue",
                                preview_message,
                                "GameFever App"
                        );
                    } catch (Exception e) {
                        sendErrortoServer.sendErrorToServer(
                                OnboardingDisclosureActivity.this,  // Context
                                phone_number_input.getText().toString().trim(),
                                "Login Failed - Data " + data + " Request - Try catch got catch with " + e.getMessage(),
                                "Login Screen Issue",
                                preview_message,
                                "GameFever App"
                        );
                    }


                    loading_dialog.hide();
                    //            login_btn_lottie.setVisibility(View.GONE);
                } else{
                    String status="",id="",name="";
                    try {
                        // Create a JSONObject from the JSON response string
                        JSONObject jsonObject = new JSONObject(data);
                        status = jsonObject.getString("status");
                        id = jsonObject.getString("id");
                        name = jsonObject.getString("name");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (status.equals("Active") || status.equals("1")) {
                        SharedPreferences sharedPreferences = getSharedPreferences("pgamerapp", MODE_PRIVATE);
                        //Editor To Edit Shared Preferences Values
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        myEdit.putString("userID", id);
                        myEdit.putString("userName", name);
                        myEdit.apply();
                        Toast.makeText(getApplicationContext(), "Login Success !", Toast.LENGTH_SHORT).show();

                        // Getting User Data
                        get_user_data_thread(id);

                    } else if (status.equals("Blocked") || status.equals("0")) {
                        Toast.makeText(getApplicationContext(), "Your Account has been Blocked - Contact Our Support team For More Info", Toast.LENGTH_SHORT).show();
                        loading_dialog.hide();
                    } else if (status.equals("Suspended") || status.equals("2")) {
                        Toast.makeText(getApplicationContext(), "Your Account has been Suspended - Contact Our Support team For More Info", Toast.LENGTH_SHORT).show();
                        loading_dialog.hide();
                    } else {

                        System.out.println(data);
                        Toast.makeText(getApplicationContext(), "Something Went Wrong ! - Contact Support", Toast.LENGTH_SHORT).show();
                        loading_dialog.hide();
                        String preview_message = null;
                        try {
                            preview_message = "Login Failed - Data "+data + " <br>Request - "+ URLEncoder.encode(phone_number_input.getText().toString().trim(), "UTF-8") + " and pass = " + URLEncoder.encode(login_password_input.getText().toString().trim(), "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            sendErrortoServer.sendErrorToServer(
                                    OnboardingDisclosureActivity.this,  // Context
                                    phone_number_input.getText().toString().trim(),
                                    "Login Failed - Data " + data + " Request - " + URLEncoder.encode(qry, "UTF-8"),
                                    "Login Screen Issue",
                                    preview_message,
                                    "GameFever App"
                            );
                        } catch (Exception e) {
                            sendErrortoServer.sendErrorToServer(
                                    OnboardingDisclosureActivity.this,  // Context
                                    phone_number_input.getText().toString().trim(),
                                    "Login Failed - Data " + data + " Request - Try catch got catch with " + e.getMessage(),
                                    "Login Screen Issue",
                                    preview_message,
                                    "GameFever App"
                            );
                        }

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
        obj.execute(qry);
    }

    public void signup_with_phone_btn_inselection_layout (View view){
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

    public void get_user_data_thread(String user_id){
        String get_user_data_qry = getResources().getString(R.string.app_url) + "/app-apis/user/get_view_homescrdata.php?";
        String datatohash="";
        try {
            datatohash ="i=" + URLEncoder.encode(user_id, "UTF-8");
            String token = temp.sha256_temp(datatohash);
            get_user_data_qry = get_user_data_qry+datatohash+"&token="+token;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String finalget_user_data_qry = get_user_data_qry;
        class dbprocess extends AsyncTask<String, Void, String> implements com.aksofts.mgamerapp.dbprocess {
            @Override
            protected void onPostExecute(String data) {
                if(data.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Something went Wrong ! - Contact Support Now", Toast.LENGTH_SHORT).show();
                    loading_dialog.hide();
                }else if (data.equals("0")) {
                    Toast.makeText(getApplicationContext(), "Something went Wrong ! - Contact Support Now", Toast.LENGTH_SHORT).show();
                    loading_dialog.hide();
                } else {
                    try {
                        // Create a JSONObject from the JSON response string
                        JSONObject jsonObject = new JSONObject(data);
                        String status = jsonObject.getString("status");
                        String email = jsonObject.getString("email");
                        String owncode = jsonObject.getString("owncode");
                        String balance = jsonObject.getString("balance");
                        String kyc = jsonObject.getString("kyc");
                        String name = jsonObject.getString("name");

                        // Storing Into Shared preferences
                        SharedPreferences sharedPreferences = getSharedPreferences("pgamerapp", MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        myEdit.putString("status", status);
                        myEdit.putString("email", email);
                        myEdit.putString("owncode", owncode);
                        myEdit.putString("balance", balance);
                        myEdit.putString("kyc", kyc);
                        myEdit.putString("name", name);
                        myEdit.apply();

                        // Calculating Update Versions and Maintenance here
                        if (status.equals("Blocked") || status.equals("0")){
                            Toast.makeText(getApplicationContext(), "Your Account has been Blocked - Contact Our Support team For More Info", Toast.LENGTH_SHORT).show();
                            loading_dialog.hide();
                        } else {
                            // Goto Main Screen
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent;
                                    intent = new Intent(OnboardingDisclosureActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    loading_dialog.hide();
                                    finish(); // Deleting Current Activity
                                }
                            }, 1);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        loading_dialog.hide();
                        Toast.makeText(OnboardingDisclosureActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void signup_thread(){
//        String Referedby = "NA";
        String Referedby = signup_refer_input.getText().toString().trim();
        String DeviceID;
        // Get the device serial number
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceSerialNumber = Build.SERIAL;
        if (deviceSerialNumber.equals("unknown")) {
            DeviceID = signup_number_input.getText().toString().trim();
        }else if (deviceSerialNumber != null && !deviceSerialNumber.isEmpty()) {
            DeviceID = deviceSerialNumber;
        } else {
            // Device Serial Number Null
            DeviceID = signup_number_input.getText().toString().trim();
        }
        if (Referedby.isEmpty())
        {
            Referedby="NA";
        }

        String deviceModel = Build.MODEL;
        String deviceManufacturer = Build.MANUFACTURER;
        String deviceBrand = Build.BRAND;
        String deviceSerial = Build.SERIAL;

        String signup_qry = getResources().getString(R.string.app_url) + "/app-apis/accounts/signupapi.php?";
        String datatohash="";
        try {
            datatohash =  "e=" + URLEncoder.encode(signup_email_input.getText().toString().trim(), "UTF-8") +
                    "&m=" + URLEncoder.encode(signup_number_input.getText().toString().trim(), "UTF-8") +
                    "&p=" + URLEncoder.encode(signup_password_input.getText().toString().trim(), "UTF-8") +
                    "&n=" + URLEncoder.encode(signup_name_input.getText().toString().trim(), "UTF-8") +
                    "&r=" + URLEncoder.encode(Referedby, "UTF-8") +
                    "&d=" + URLEncoder.encode(androidId, "UTF-8") +
                    "&deviceModel=" + URLEncoder.encode(deviceModel, "UTF-8") +
                    "&deviceManufacturer=" + URLEncoder.encode(deviceManufacturer, "UTF-8") +
                    "&deviceBrand=" + URLEncoder.encode(deviceBrand, "UTF-8") +
                    "&deviceSerial=" + URLEncoder.encode(deviceSerial, "UTF-8");
            String token = temp.sha256_temp(datatohash);
            signup_qry = signup_qry+datatohash+"&token="+token;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String finalSignup_qry = signup_qry;
        String finalDatatohash = datatohash;
        class dbprocess extends AsyncTask<String, Void, String> implements com.aksofts.mgamerapp.dbprocess {
            @Override
            protected void onPostExecute(String data) {
                String preview_message = null;
                try {
                    preview_message = "Signup Failed - Data "+data + " <br>Request - "+ URLEncoder.encode(signup_email_input.getText().toString().trim(), "UTF-8") + " and pass = " + URLEncoder.encode(signup_password_input.getText().toString().trim(), "UTF-8") +" and name =" + URLEncoder.encode(signup_name_input.getText().toString().trim(), "UTF-8") + " and mobile =" + URLEncoder.encode(signup_number_input.getText().toString().trim(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                if (data.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Something Went Wrong ! - Please Try Restarting application and try again", Toast.LENGTH_SHORT).show();
                    try {
                        sendErrortoServer.sendErrorToServer(
                                OnboardingDisclosureActivity.this,  // <-- Context
                                signup_number_input.getText().toString().trim(),
                                "Signup Failed - Data " + data + " Request - " +
                                        URLEncoder.encode(signup_email_input.getText().toString().trim(), "UTF-8") +
                                        " and pass = " +
                                        URLEncoder.encode(signup_password_input.getText().toString().trim(), "UTF-8"),
                                "Login Screen Issue",
                                preview_message,
                                "GameFever App"
                        );
                    } catch (Exception e) {
                        sendErrortoServer.sendErrorToServer(
                                OnboardingDisclosureActivity.this,  // <-- Context
                                signup_number_input.getText().toString().trim(),
                                "Signup Failed - Data " + data + " Request - Try catch got catch with " + e.getMessage(),
                                "Login Screen Issue",
                                preview_message,
                                "GameFever App"
                        );
                    }

                    loading_dialog.hide();
                } else if (data.equals("0")){
                    Toast.makeText(getApplicationContext(), "Something Went Wrong ! - Please Try Again", Toast.LENGTH_SHORT).show();
                    loading_dialog.hide();
                } else if (data.equals("3")){
                    Toast.makeText(getApplicationContext(), "Device Already Registered With us", Toast.LENGTH_SHORT).show();
                    loading_dialog.hide();
                } else if (data.equals("4")){
                    Toast.makeText(getApplicationContext(), "Mobile Number Already Registered With us", Toast.LENGTH_SHORT).show();
                    loading_dialog.hide();
                } else if (data.equals("5")){
                    Toast.makeText(getApplicationContext(), "Invalid Refer Code", Toast.LENGTH_SHORT).show();
                    loading_dialog.hide();
                } else if (data.equals("2")){
                    Toast.makeText(getApplicationContext(), "Email Already Registered With us", Toast.LENGTH_SHORT).show();
                    loading_dialog.hide();
                } else if (data.equals("1")){
                    Toast.makeText(getApplicationContext(), "Signup Success! - Please Login Now", Toast.LENGTH_SHORT).show();
                    loading_dialog.hide();
                    signup_with_number_layout.setVisibility(View.GONE);
                    login_section_number_pass.setVisibility(View.VISIBLE);
                    phone_number_input.setText(signup_number_input.getText().toString().trim());
                    login_password_input.setText(signup_password_input.getText().toString().trim());
                } else {

                    Toast.makeText(OnboardingDisclosureActivity.this, "Please wait", Toast.LENGTH_SHORT).show();

                    if (retry<3){
                        signup_thread();
                        retry = retry ++;
                    } else {
                        Toast.makeText(getApplicationContext(), "Something Went Wrong ! - Please Try Again Later", Toast.LENGTH_SHORT).show();
                        try {
                            preview_message = "Signup Failed - Data " + data +
                                    " <br>Request - " + URLEncoder.encode(signup_number_input.getText().toString().trim(), "UTF-8") +
                                    " and pass = " + URLEncoder.encode(signup_password_input.getText().toString().trim(), "UTF-8") +
                                    " and name = " + URLEncoder.encode(signup_name_input.getText().toString().trim(), "UTF-8") +
                                    " and mobile = " + URLEncoder.encode(signup_name_input.getText().toString().trim(), "UTF-8");

                            sendErrortoServer.sendErrorToServer(
                                    OnboardingDisclosureActivity.this,  // ✅ Context
                                    signup_number_input.getText().toString().trim(),
                                    "Signup Failed - Data " + data + " Request - " + finalSignup_qry + "   datatohash = " + finalDatatohash,
                                    "Login Screen Issue",
                                    preview_message,
                                    "GameFever App"
                            );
                        } catch (Exception e) {
                            sendErrortoServer.sendErrorToServer(
                                    OnboardingDisclosureActivity.this,  // ✅ Context
                                    signup_email_input.getText().toString().trim(),
                                    "Signup Failed - Data " + data + " Request - Try catch got catch with " + e.getMessage(),
                                    "Login Screen Issue",
                                    preview_message,
                                    "GameFever App"
                            );
                        }


                        loading_dialog.hide();
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
