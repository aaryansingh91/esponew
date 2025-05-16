package com.aksofts.mgamerapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


import com.airbnb.lottie.LottieAnimationView;

import com.onesignal.Continue;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;

public class SplashActivity extends AppCompatActivity {

    String update_app_link ="https://cmhost.in";



    LinearLayout main_splash, update_splash, maintenance_splash , blocked_splash_layout;
    TextView maintenance_main_message, maintenance_progress_message;

    LottieAnimationView splashMaintenanceAnim, splashUpdateAvailableAnim ;
    int localappversion=3;

    String onesignalAppID = "42ea77df-dc24-4b99-9264-7e56de8e217b";

    // Splash Ad Items
    LinearLayout splash_ad_layout;
    ImageView splash_ad_image_cover;
    TextView splash_ad_text , splash_ad_sub_text;
    Boolean is_splash_clicked = false;
    String  storedID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        main_splash = findViewById(R.id.main_splash_layout);
        update_splash = findViewById(R.id.updateavailable_splash_layout);
        maintenance_splash = findViewById(R.id.maintenance_splash_layout);
        blocked_splash_layout = findViewById(R.id.blocked_splash_layout);

        splashMaintenanceAnim    = findViewById(R.id.lottie_splash_maintenance);
        splashUpdateAvailableAnim    = findViewById(R.id.lottie_splash_update_available);

        maintenance_main_message = findViewById(R.id.maintenance_message_lbl);
        maintenance_progress_message = findViewById(R.id.maintenance_progress_message_lbl);


        SharedPreferences sharedPreferences = getSharedPreferences("pgamerapp", MODE_PRIVATE);
        storedID = sharedPreferences.getString("userID", "NULL");


        OneSignal.initWithContext(this, onesignalAppID);
        OneSignal.getNotifications().requestPermission(true, Continue.with(r -> {
            if (r.isSuccess()) {
//                if (r.getData()) {
//                }
//                else {
//                }
            }
            else {
            }
        }));

        if (!storedID.equals("NULL")){
            OneSignal.login(storedID); //    call when something occurs well and update every time user login
        }

        get_data_thread();


    }


    public void get_data_thread(){
        String qry = getResources().getString(R.string.app_url) + "/app-apis/appinfoapi.php";
        class dbprocess extends AsyncTask<String, Void, String> implements com.aksofts.mgamerapp.dbprocess {
            @Override
            protected void onPostExecute(String data) {
                if (data.equals("0")){
                    Toast.makeText(getApplicationContext(), "Something went Wrong ! - Contact Support Now", Toast.LENGTH_SHORT).show();
                } else {
                    String status="",id="",name="";
                    try {
                        // Getting Next Data for News and Promo

                        // Create a JSONObject from the JSON response string
                        JSONObject jsonObject = new JSONObject(data);
                        String app_name = jsonObject.getString("app_name");
                        String app_version = jsonObject.getString("app_version");
                        update_app_link = jsonObject.getString("update_app_link");
                        String app_maintenance_status = jsonObject.getString("app_maintenance_status");
                        String app_maintenance_message = jsonObject.getString("app_maintenance_message");
                        String app_maintenance_progress_message = jsonObject.getString("app_maintenance_progress_message");
                        String app_share_url = jsonObject.getString("app_share_url");
                        String app_share_message_before_refercode = jsonObject.getString("app_share_message_before_refercode");
                        String app_share_message_refercode_link = jsonObject.getString("app_share_message_refercode_link");
                        String app_share_message_after_refercode = jsonObject.getString("app_share_message_after_refercode");
                        String app_internal_message_mining_speed = jsonObject.getString("app_internal_message_mining_speed");
                        String app_internal_message_refer_page_top = jsonObject.getString("app_internal_message_refer_page_top");
                        String app_internal_home_page_marque = jsonObject.getString("app_internal_home_page_marque");
                        String app_internal_settings_page_wallet_page_link = jsonObject.getString("app_internal_settings_page_wallet_page_link");
                        String app_internal_settings_page_notifications_page_link = jsonObject.getString("app_internal_settings_page_notifications_page_link");
                        String app_internal_settings_page_contract_address = jsonObject.getString("app_internal_settings_page_contract_address");
                        String app_internal_settings_page_helpandsupport_page_link = jsonObject.getString("app_internal_settings_page_helpandsupport_page_link");
                        String app_internal_settings_page_telegram_page_link = jsonObject.getString("app_internal_settings_page_telegram_page_link");
                        String app_internal_settings_page_instagram_page_link = jsonObject.getString("app_internal_settings_page_instagram_page_link");
                        String app_internal_settings_page_twitter_page_link = jsonObject.getString("app_internal_settings_page_twitter_page_link");
                        String app_internal_settings_page_whatsapp_page_link = jsonObject.getString("app_internal_settings_page_whatsapp_page_link");
                        String app_internal_settings_page_youtube_page_link = jsonObject.getString("app_internal_settings_page_youtube_page_link");
                        String app_internal_settings_SPLASH_animations_maintenance = jsonObject.getString("app_internal_settings_SPLASH_animations_maintenance");
                        String app_internal_settings_SPLASH_animations_update_available = jsonObject.getString("app_internal_settings_SPLASH_animations_update_available");
                        String app_login_forgot_password_link = jsonObject.getString("app_login_forgot_password_link");
                        String app_login_signup_issue_contact_link = jsonObject.getString("app_login_signup_issue_contact_link");
                        String app_internal_settings_page_privacy_page_link = jsonObject.getString("app_internal_settings_page_privacy_page_link");
                        String app_internal_settings_page_rateus_page_link = jsonObject.getString("app_internal_settings_page_rateus_page_link");
                        String app_home_top_sec_1_game = jsonObject.getString("app_home_top_sec_1_game");
                        String app_home_top_sec_1_game_url = jsonObject.getString("app_home_top_sec_1_game_url");
                        String app_home_top_sec_1_apptask = jsonObject.getString("app_home_top_sec_1_apptask");
                        String app_home_top_sec_1_apptask_url = jsonObject.getString("app_home_top_sec_1_apptask_url");
                        String app_home_top_sec_1_survey = jsonObject.getString("app_home_top_sec_1_survey");
                        String app_home_top_sec_1_survey_url = jsonObject.getString("app_home_top_sec_1_survey_url");
                        String app_home_top_sec_3_game_onoff = jsonObject.getString("app_home_top_sec_3_game");
                        String app_home_top_sec_3_game_url = jsonObject.getString("app_home_top_sec_3_game_url");
                        String app_home_top_sec_3_ffblog_onoff = jsonObject.getString("app_home_top_sec_3_ffblog");
                        String app_home_top_sec_3_ffblog_url = jsonObject.getString("app_home_top_sec_3_ffblog_url");
                        String app_home_top_sec_3_quiz_onoff = jsonObject.getString("app_home_top_sec_3_quiz");
                        String app_home_top_sec_3_quiz_url = jsonObject.getString("app_home_top_sec_3_quiz_url");
                        String withdraw_data_setting = jsonObject.getString("withdraw_data_setting");
                        String withdraw_list_data_setting = jsonObject.getString("withdraw_list_data_setting");

                        // String mining_ad_type = jsonObject.getString("mining_ad_type");


                        // Ads Settings
                        String game_ad_type = jsonObject.getString("game_ad_type"); // Admob - Max - Unity
                        String leaderboard_ad_type = jsonObject.getString("leaderboard_ad_type"); // Admob - Max - Unity

                        //Unity Ads Settings
                        String unity_ads_on_off = jsonObject.getString("unity_ads_on_off"); //ON / OFF
                        String unity_ads_testmode_on_off = jsonObject.getString("unity_ads_testmode_on_off"); //ON / OFF
                        String unity_game_id = jsonObject.getString("unity_game_id");
                        String unity_inter_ads_id = jsonObject.getString("unity_inter_ads_id");
                        String unity_inter_ads_on_off = jsonObject.getString("unity_inter_ads_on_off");
                        String unity_reward_ads_id = jsonObject.getString("unity_reward_ads_id");
                        String unity_reward_ads_on_off = jsonObject.getString("unity_reward_ads_on_off");

                        //Max Ads Settings
                        String max_ads_on_off = jsonObject.getString("max_ads_on_off");
                        String max_reward_ads_id = jsonObject.getString("max_reward_ads_id");
                        String max_banner_ads_id = jsonObject.getString("max_banner_ads_id");
                        String max_banner_ads_on_off = jsonObject.getString("max_banner_ads_on_off");
                        String max_native_ads_id = jsonObject.getString("max_native_ads_id");
                        String max_native_ads_on_off = jsonObject.getString("max_native_ads_on_off");
                        String max_appopen_ads_id = jsonObject.getString("max_appopen_ads_id");
                        String max_appopen_ads_on_off = jsonObject.getString("max_appopen_ads_on_off");
                        String max_inter_ads_id = jsonObject.getString("max_inter_ads_id");
                        String max_inter_ads_on_off = jsonObject.getString("max_inter_ads_on_off");

                        // Admob Ads Settings
                        String admob_ads_app_id = jsonObject.getString("admob_ads_app_id");
                        String admob_all_ads_on_off = jsonObject.getString("admob_all_ads_on_off");
                        String admob_ads_banner_ads_id = jsonObject.getString("admob_ads_banner_ads_id");
                        String admob_ads_banner_ads_on_off = jsonObject.getString("admob_ads_banner_ads_on_off");
                        String admob_ads_rewarded_ads_id = jsonObject.getString("admob_ads_rewarded_ads_id");
                        String admob_ads_rewarded_ads_on_off = jsonObject.getString("admob_ads_rewarded_ads_on_off");
                        String admob_ads_native_ads_id = jsonObject.getString("admob_ads_native_ads_id");
                        String admob_ads_native_ads_id_on_off = jsonObject.getString("admob_ads_native_ads_id_on_off");
                        String admob_ads_inter_ads_id = jsonObject.getString("admob_ads_inter_ads_id");
                        String admob_ads_inter_ads_id_on_off = jsonObject.getString("admob_ads_inter_ads_id_on_off");


                        // Storing Into Shared preferences
                        SharedPreferences sharedPreferences = getSharedPreferences("pgamerapp", MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        myEdit.putString("app_name", app_name);
                        myEdit.putString("app_version", app_version);
                        myEdit.putString("update_app_link", update_app_link);
                        myEdit.putString("app_maintenance_status", app_maintenance_status);
                        myEdit.putString("app_maintenance_message", app_maintenance_message);
                        myEdit.putString("app_maintenance_progress_message", app_maintenance_progress_message);
                        myEdit.putString("app_share_url", app_share_url);
                        myEdit.putString("app_share_message_before_refercode", app_share_message_before_refercode);
                        myEdit.putString("app_share_message_refercode_link", app_share_message_refercode_link);
                        myEdit.putString("app_share_message_after_refercode", app_share_message_after_refercode);
                        myEdit.putString("app_internal_message_mining_speed", app_internal_message_mining_speed);
                        myEdit.putString("app_internal_message_refer_page_top", app_internal_message_refer_page_top);
                        myEdit.putString("app_internal_home_page_marque", app_internal_home_page_marque);
                        myEdit.putString("app_internal_settings_page_wallet_page_link", app_internal_settings_page_wallet_page_link);
                        myEdit.putString("app_internal_settings_page_notifications_page_link", app_internal_settings_page_notifications_page_link);
                        myEdit.putString("app_internal_settings_page_contract_address", app_internal_settings_page_contract_address);
                        myEdit.putString("app_internal_settings_page_helpandsupport_page_link", app_internal_settings_page_helpandsupport_page_link);
                        myEdit.putString("app_internal_settings_page_telegram_page_link", app_internal_settings_page_telegram_page_link);
                        myEdit.putString("app_internal_settings_page_instagram_page_link", app_internal_settings_page_instagram_page_link);
                        myEdit.putString("app_internal_settings_page_twitter_page_link", app_internal_settings_page_twitter_page_link);
                        myEdit.putString("app_internal_settings_page_whatsapp_page_link", app_internal_settings_page_whatsapp_page_link);
                        myEdit.putString("app_internal_settings_page_youtube_page_link", app_internal_settings_page_youtube_page_link);
                        myEdit.putString("app_internal_settings_SPLASH_animations_maintenance", app_internal_settings_SPLASH_animations_maintenance);
                        myEdit.putString("app_internal_settings_SPLASH_animations_update_available", app_internal_settings_SPLASH_animations_update_available);
                        myEdit.putString("app_login_forgot_password_link", app_login_forgot_password_link);
                        myEdit.putString("app_login_signup_issue_contact_link", app_login_signup_issue_contact_link);
                        myEdit.putString("app_internal_settings_page_privacy_page_link", app_internal_settings_page_privacy_page_link);
                        myEdit.putString("app_internal_settings_page_rateus_page_link", app_internal_settings_page_rateus_page_link);
                        myEdit.putString("app_home_top_sec_1_game", app_home_top_sec_1_game);
                        myEdit.putString("app_home_top_sec_1_game_url", app_home_top_sec_1_game_url);
                        myEdit.putString("app_home_top_sec_1_apptask", app_home_top_sec_1_apptask);
                        myEdit.putString("app_home_top_sec_1_apptask_url", app_home_top_sec_1_apptask_url);
                        myEdit.putString("app_home_top_sec_1_survey", app_home_top_sec_1_survey );
                        myEdit.putString("app_home_top_sec_1_survey_url", app_home_top_sec_1_survey_url);
                        myEdit.putString("app_home_top_sec_3_game_onoff", app_home_top_sec_3_game_onoff);
                        myEdit.putString("app_home_top_sec_3_game_url", app_home_top_sec_3_game_url);
                        myEdit.putString("app_home_top_sec_3_ffblog_onoff", app_home_top_sec_3_ffblog_onoff);
                        myEdit.putString("app_home_top_sec_3_ffblog_url", app_home_top_sec_3_ffblog_url);
                        myEdit.putString("app_home_top_sec_3_quiz_onoff", app_home_top_sec_3_quiz_onoff);
                        myEdit.putString("app_home_top_sec_3_quiz_url", app_home_top_sec_3_quiz_url);
                        myEdit.putString("withdraw_data_setting", withdraw_data_setting);
                        myEdit.putString("withdraw_list_data_setting", withdraw_list_data_setting);


// ##########################################################################################################################
// ###################################        ADS SETTINGS     ##############################################################
// ##########################################################################################################################
                        myEdit.putString("game_ad_type", game_ad_type);
                        myEdit.putString("leaderboard_ad_type", leaderboard_ad_type);

// ###################################        Unity ADS       ##############################################################
                        myEdit.putString("unity_game_id", unity_game_id);
                        myEdit.putString("unity_ads_on_off", unity_ads_on_off);
                        myEdit.putString("unity_ads_testmode_on_off", unity_ads_testmode_on_off);
                        myEdit.putString("unity_inter_ads_id", unity_inter_ads_id);
                        myEdit.putString("unity_inter_ads_on_off", unity_inter_ads_on_off);
                        myEdit.putString("unity_reward_ads_id", unity_reward_ads_id);
                        myEdit.putString("unity_reward_ads_on_off", unity_reward_ads_on_off);

// ###################################   Applovin Max ADS       ###########################################################
                        myEdit.putString("max_ads_on_off", max_ads_on_off);
                        myEdit.putString("max_reward_ads_id", max_reward_ads_id);
                        myEdit.putString("max_banner_ads_id", max_banner_ads_id);
                        myEdit.putString("max_banner_ads_on_off", max_banner_ads_on_off);
                        myEdit.putString("max_native_ads_id", max_native_ads_id);
                        myEdit.putString("max_native_ads_on_off", max_native_ads_on_off);
                        myEdit.putString("max_appopen_ads_id", max_appopen_ads_id);
                        myEdit.putString("max_appopen_ads_on_off", max_appopen_ads_on_off);
                        myEdit.putString("max_inter_ads_id", max_inter_ads_id);
                        myEdit.putString("max_inter_ads_on_off", max_inter_ads_on_off);

// ###################################       ADMOB ADS          ###########################################################
                        myEdit.putString("admob_ads_app_id", admob_ads_app_id);
                        myEdit.putString("admob_all_ads_on_off", admob_all_ads_on_off);
                        myEdit.putString("admob_ads_banner_ads_id", admob_ads_banner_ads_id);
                        myEdit.putString("admob_ads_banner_ads_on_off", admob_ads_banner_ads_on_off);
                        myEdit.putString("admob_ads_rewarded_ads_id", admob_ads_rewarded_ads_id);
                        myEdit.putString("admob_ads_rewarded_ads_on_off", admob_ads_rewarded_ads_on_off);
                        myEdit.putString("admob_ads_native_ads_id", admob_ads_native_ads_id);
                        myEdit.putString("admob_ads_native_ads_id_on_off", admob_ads_native_ads_id_on_off);
                        myEdit.putString("admob_ads_inter_ads_id", admob_ads_inter_ads_id);
                        myEdit.putString("admob_ads_inter_ads_id_on_off", admob_ads_inter_ads_id_on_off);

// ###################################     Saving All Settings  ###########################################################
                        myEdit.apply();


                        // Calculating Update Versions and Maintenance here
                        if (app_maintenance_status.equals("ON")){
                            splashMaintenanceAnim.setAnimationFromUrl(app_internal_settings_SPLASH_animations_maintenance);
                            maintenance_main_message.setText(app_maintenance_message);
                            maintenance_progress_message.setText(app_maintenance_progress_message);
                            main_splash.setVisibility(View.GONE);
                            maintenance_splash.setVisibility(View.VISIBLE);
                            blocked_splash_layout.setVisibility(View.GONE);
                        } else if (Integer.parseInt(app_version)>localappversion){
                            splashUpdateAvailableAnim.setAnimationFromUrl(app_internal_settings_SPLASH_animations_update_available);
                            main_splash.setVisibility(View.GONE);
                            maintenance_splash.setVisibility(View.GONE);
                            update_splash.setVisibility(View.VISIBLE);
                            blocked_splash_layout.setVisibility(View.GONE);
                        } else {
                            String storedID = sharedPreferences.getString("userID", "NULL");
                            if (storedID.equals("NULL") || storedID.isEmpty()) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent;
                                        intent = new Intent(SplashActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }, 1);
                            } else {
                                get_user_data_thread(storedID);
                            }
                        }
                    } catch (JSONException e) {
                        System.out.println(data);
                        System.out.println("Exception : "+e.getMessage());
                        String preview_message = "Error in Loading App - Json Exception <br>" + e.getMessage();
                        Toast.makeText(SplashActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        try {
                            sendErrortoServer.sendErrorToServer("N/A" , "App Not Loaded with error " + e.getMessage() , "Splash Screen Issue", preview_message , "GameFever App");
                        } catch (Exception e2){
                            sendErrortoServer.sendErrorToServer("N/A" , "App Not Loaded with error " + e.getMessage() , "Splash Screen Issue", preview_message , "GameFever App");
                        }
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                        Toast.makeText(SplashActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                }
                Toast.makeText(SplashActivity.this, "Welcome to App", Toast.LENGTH_SHORT).show();


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


    public void get_user_data_thread(String user_id) throws NoSuchAlgorithmException {

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
                if (data.equals("0")){
                    Toast.makeText(getApplicationContext(), "Oops! Something went Wrong ! - Contact Support Now", Toast.LENGTH_SHORT).show();
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
                        if (status.equals("0")){
//                            splashAccountLockedAnim.setAnimationFromUrl(app_internal_settings_SPLASH_animations_account_locked);
                            main_splash.setVisibility(View.GONE);
                            maintenance_splash.setVisibility(View.GONE);
                            update_splash.setVisibility(View.GONE);
                            blocked_splash_layout.setVisibility(View.VISIBLE);
                        } else {
                            // Goto Main Screen
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent;
                                    intent = new Intent(SplashActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish(); // Deleting Current Activity
                                }
                            }, 1);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
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


    public void update_app(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(update_app_link));
        startActivity(intent);
    }

}