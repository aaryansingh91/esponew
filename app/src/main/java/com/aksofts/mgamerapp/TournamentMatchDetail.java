package com.aksofts.mgamerapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class TournamentMatchDetail extends AppCompatActivity {

    private Button joinButton;
    private int userId = -1;
    private int tournamentId = -1;
    private String matchType = "";
    private int retryCount = 0;
    private static final int MAX_RETRIES = 2;
    private static final String TAG = "TournamentMatchDetail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_details);

        // Initialize views
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        joinButton = findViewById(R.id.join_button);

        // Disable join button until data is loaded
        joinButton.setEnabled(false);

        // Get tournament ID from Intent
        tournamentId = getIntent().getIntExtra("TOURNAMENT_ID", -1);
        Log.d(TAG, "Intent extras: " + (getIntent().getExtras() != null ? getIntent().getExtras().toString() : "null"));
        Log.d(TAG, "Received tournament ID: " + tournamentId);
        if (tournamentId <= 0) {
            Toast.makeText(this, "Invalid tournament ID: " + tournamentId, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Fetch user ID from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("pgamerapp", MODE_PRIVATE);
        Log.d(TAG, "All SharedPreferences: " + prefs.getAll().toString());
        String userIdStr = prefs.getString("userID", null);
        try {
            userId = (userIdStr != null && !userIdStr.isEmpty()) ? Integer.parseInt(userIdStr) : -1;
        } catch (NumberFormatException e) {
            Log.e(TAG, "Failed to parse userID: " + userIdStr, e);
            userId = -1;
        }
        Log.d(TAG, "User ID from SharedPreferences: " + userId);

        if (userId <= 0) {
            Toast.makeText(this, "Please log in to join the tournament", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, OnboardingDisclosureActivity.class).putExtra("FORCE_LOGIN", true));
            finish();
            return;
        }

        // Set up ViewPager2 and TabLayout
        MatchDetailsAdapter adapter = new MatchDetailsAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("DESCRIPTION");
                    break;
                case 1:
                    tab.setText("JOINED MEMBER");
                    break;
            }
        }).attach();

        // Fetch and show match details
        updateMatchDetails(tournamentId);

        // Join Button Logic
        joinButton.setOnClickListener(v -> sendJoinRequest());
    }

    private void sendJoinRequest() {
        if (matchType.isEmpty() || matchType.equalsIgnoreCase("Unknown")) {
            Toast.makeText(this, "Match type not loaded: " + matchType, Toast.LENGTH_LONG).show();
            return;
        }

        if (userId <= 0 || tournamentId <= 0) {
            Toast.makeText(this, "Invalid parameters - userId: " + userId + ", tournamentId: " + tournamentId, Toast.LENGTH_LONG).show();
            return;
        }

        Log.d(TAG, "Join button clicked - user_id: " + userId + ", match_id: " + tournamentId + ", match_type: " + matchType);

        String url = getString(R.string.app_url) + "/amsit-adm/join_tournament_api.php";
        Log.d(TAG, "Join API URL: " + url);

        Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(userId));
        params.put("match_id", String.valueOf(tournamentId));
        params.put("match_type", matchType);

        // Log raw request body
        StringBuilder requestBody = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (requestBody.length() > 0) requestBody.append("&");
                requestBody.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            Log.d(TAG, "Join API raw request body: " + requestBody.toString());
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Error encoding request body: " + e.getMessage());
            Toast.makeText(this, "Request encoding error", Toast.LENGTH_LONG).show();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d(TAG, "Join API response: " + response);
                    if (response == null || response.trim().isEmpty()) {
                        Log.e(TAG, "Empty server response");
                        if (retryCount < MAX_RETRIES) {
                            retryCount++;
                            Log.d(TAG, "Retrying request (attempt " + retryCount + ")");
                            sendJoinRequest();
                        } else {
                            Toast.makeText(this, "Empty server response after retries", Toast.LENGTH_LONG).show();
                        }
                        return;
                    }

                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("status")) {
                            Toast.makeText(this, "Joined successfully", Toast.LENGTH_SHORT).show();
                            joinButton.setEnabled(false);
                            joinButton.setText("Already Joined");
                            joinButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF888888));
                        } else {
                            String message = jsonResponse.optString("message", "Failed to join");
                            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Join JSON error: " + e.getMessage() + ", Raw response: " + response);
                        if (retryCount < MAX_RETRIES) {
                            retryCount++;
                            Log.d(TAG, "Retrying request due to JSON error (attempt " + retryCount + ")");
                            sendJoinRequest();
                        } else {
                            Toast.makeText(this, "Invalid server response: " + response, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                error -> {
                    String errorMsg = "Unknown error";
                    int statusCode = error.networkResponse != null ? error.networkResponse.statusCode : -1;
                    String responseData = error.networkResponse != null && error.networkResponse.data != null ?
                            new String(error.networkResponse.data) : "No response data";
                    if (error instanceof NetworkError || error instanceof NoConnectionError) {
                        errorMsg = "Network error: Check your internet connection";
                    } else if (error instanceof ServerError) {
                        errorMsg = "Server error (HTTP " + statusCode + "): Check debug.log for fatal errors";
                    } else if (error instanceof TimeoutError) {
                        errorMsg = "Timeout: Server took too long to respond";
                    } else if (error instanceof ParseError) {
                        errorMsg = "Parse error: Invalid API response";
                    } else if (error instanceof AuthFailureError) {
                        errorMsg = "Authentication error";
                    }
                    error.printStackTrace();
                    Log.e(TAG, "Join Volley error: " + errorMsg + " (HTTP " + statusCode + ", Response: " + responseData + ")");
                    if (retryCount < MAX_RETRIES && error instanceof ServerError) {
                        retryCount++;
                        Log.d(TAG, "Retrying request due to server error (attempt " + retryCount + ")");
                        sendJoinRequest();
                    } else {
                        Toast.makeText(this, errorMsg + " (Response: " + responseData + ")", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
        Log.d(TAG, "Join API request queued (Retry count: " + retryCount + ")");
    }

    private void updateMatchDetails(int tournamentId) {
        String url = getResources().getString(R.string.app_url) + "/amsit-adm/tournament_details_api.php?id=" + tournamentId;
        Log.d(TAG, "Fetching match details from: " + url);
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.d(TAG, "Match details API response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean status = jsonResponse.getBoolean("status");
                        if (!status) {
                            Toast.makeText(this, "Match not found! Please try again.", Toast.LENGTH_LONG).show();
                            joinButton.setEnabled(false);
                            finish();
                            return;
                        }

                        JSONObject match = jsonResponse.getJSONObject("data");
                        matchType = match.optString("type", "Unknown").trim();

                        // Pass match data to DescriptionFragment
                        Bundle bundle = new Bundle();
                        bundle.putString("match_type", matchType);
                        bundle.putString("match_name", match.optString("match_name", "Unknown"));
                        bundle.putString("map", match.optString("MAP", "Unknown"));
                        bundle.putInt("entry_fee", match.optInt("entry_fee", 0));
                        bundle.putInt("per_kill", match.optInt("per_kill", 0));
                        bundle.putString("match_time", match.optString("match_time", "Unknown"));
                        bundle.putString("prize_details", "Winning Prize: " + match.optInt("win_prize", 0) +
                                "\n" + match.optString("prize_description", ""));
                        bundle.putString("match_desc", match.optString("match_desc", ""));
                        bundle.putString("match_banner", match.optString("match_banner", ""));

                        // Update DescriptionFragment with match data
                        Fragment fragment = getSupportFragmentManager().findFragmentByTag("f0");
                        if (fragment instanceof DescriptionFragment) {
                            ((DescriptionFragment) fragment).updateMatchDetails(bundle);
                        }

                        // Enable join button only if match type is valid
                        if (!matchType.equalsIgnoreCase("Unknown") && !matchType.isEmpty()) {
                            joinButton.setEnabled(true);
                            Log.d(TAG, "Join button enabled with matchType: " + matchType);
                        } else {
                            Toast.makeText(this, "Invalid match type: " + matchType, Toast.LENGTH_LONG).show();
                            joinButton.setEnabled(false);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Match details JSON error: " + e.getMessage() + ", Raw response: " + response);
                        Toast.makeText(this, "Error parsing match data: " + response, Toast.LENGTH_LONG).show();
                        joinButton.setEnabled(false);
                        finish();
                    }
                },
                error -> {
                    String errorMsg = "Unknown error";
                    int statusCode = error.networkResponse != null ? error.networkResponse.statusCode : -1;
                    String responseData = error.networkResponse != null && error.networkResponse.data != null ?
                            new String(error.networkResponse.data) : "No response data";
                    if (error instanceof NetworkError || error instanceof NoConnectionError) {
                        errorMsg = "Network error: Check your internet connection";
                    } else if (error instanceof ServerError) {
                        errorMsg = "Server error (HTTP " + statusCode + "): Check server logs for tournament_details_api.php";
                    } else if (error instanceof TimeoutError) {
                        errorMsg = "Timeout: Server took too long to respond";
                    } else if (error instanceof ParseError) {
                        errorMsg = "Parse error: Invalid API response";
                    } else if (error instanceof AuthFailureError) {
                        errorMsg = "Authentication error";
                    }
                    error.printStackTrace();
                    Log.e(TAG, "Match details Volley error: " + errorMsg + " (HTTP " + statusCode + ", Response: " + responseData + ")");
                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                    joinButton.setEnabled(false);
                    finish();
                });

        queue.add(request);
        Log.d(TAG, "Match details API request queued");
    }
}