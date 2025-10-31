package com.rewards.espotask;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SessionHelper {

    public interface SessionValidationCallback {
        void onSessionValid();
        void onSessionInvalid(String reason, String message);
    }

    public static void validateSession(Context context, SessionValidationCallback callback) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("EspoTaskApp", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userID", "NULL");
        String sessionToken = sharedPreferences.getString("sessionToken", "NULL");
        String deviceId = sharedPreferences.getString("deviceId", "NULL");

        if (userId.equals("NULL") || sessionToken.equals("NULL") || deviceId.equals("NULL")) {
            callback.onSessionInvalid("no_session", "No active session found");
            return;
        }

        String qry = context.getString(R.string.app_url) + "/accounts/validate_session.php?user_id=" + userId +
                "&session_token=" + sessionToken + "&deviceid=" + deviceId;

        class ValidateSessionTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String furl = params[0];
                try {
                    URL url = new URL(furl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    return br.readLine();
                } catch (Exception ex) {
                    return "{\"valid\":false,\"reason\":\"network_error\",\"message\":\"" + ex.getMessage() + "\"}";
                }
            }

            @Override
            protected void onPostExecute(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    boolean isValid = jsonObject.getBoolean("valid");

                    if (isValid) {
                        callback.onSessionValid();
                    } else {
                        String reason = jsonObject.getString("reason");
                        String message = jsonObject.getString("message");
                        callback.onSessionInvalid(reason, message);
                    }
                } catch (JSONException e) {
                    callback.onSessionInvalid("parse_error", "Failed to parse response");
                }
            }
        }

        ValidateSessionTask task = new ValidateSessionTask();
        task.execute(qry);
    }

    public static void logout(Context context, LogoutCallback callback) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("EspoTaskApp", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userID", "NULL");
        String sessionToken = sharedPreferences.getString("sessionToken", "NULL");

        if (!userId.equals("NULL") && !sessionToken.equals("NULL")) {
            String qry = context.getString(R.string.app_url) + "/accounts/logout.php?user_id=" + userId +
                    "&session_token=" + sessionToken;

            class LogoutTask extends AsyncTask<String, Void, String> {
                @Override
                protected String doInBackground(String... params) {
                    String furl = params[0];
                    try {
                        URL url = new URL(furl);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        return br.readLine();
                    } catch (Exception ex) {
                        return "{\"success\":false,\"message\":\"" + ex.getMessage() + "\"}";
                    }
                }

                @Override
                protected void onPostExecute(String data) {
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        boolean success = jsonObject.getBoolean("success");

                        // Clear local session data regardless of server response
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("userID");
                        editor.remove("userName");
                        editor.remove("sessionToken");
                        editor.remove("deviceId");
                        editor.remove("status");
                        editor.remove("email");
                        editor.remove("owncode");
                        editor.remove("balance");
                        editor.remove("kyc");
                        editor.remove("name");
                        editor.apply();

                        if (callback != null) {
                            callback.onLogoutComplete(success);
                        }

                    } catch (JSONException e) {
                        if (callback != null) {
                            callback.onLogoutComplete(false);
                        }
                    }
                }
            }

            LogoutTask task = new LogoutTask();
            task.execute(qry);
        } else {
            // Clear local data even if no server session
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            if (callback != null) {
                callback.onLogoutComplete(true);
            }
        }
    }

    public interface LogoutCallback {
        void onLogoutComplete(boolean success);
    }

    public static void handleSessionExpired(Context context) {
        // Clear all session data
        SharedPreferences sharedPreferences = context.getSharedPreferences("EspoTaskApp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Redirect to login
        Intent intent = new Intent(context, OnboardingDisclosureActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        Toast.makeText(context, "Your session has expired. Please login again.", Toast.LENGTH_LONG).show();
    }
}