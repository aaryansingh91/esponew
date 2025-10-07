package com.rewards.espotask;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.UnityAdsShowOptions;
import com.unity3d.ads.IUnityAdsInitializationListener;

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

    int userCoins = 0;
    int userTickets = 0;
    int entryFeeCoins = 0;
    int entryFeeTickets = 0;
    String entryType = "coin";
    String joinType = "coin";

    // Unity Ads variables
    private boolean isUnityAdsEnabled = false;
    private String unityInterstitialId = null;
    private boolean isUnityInitialized = false;

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

        // Fetch user ID from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("EspoTaskApp", MODE_PRIVATE);
        String userIdStr = prefs.getString("userID", null);
        try {
            userId = (userIdStr != null && !userIdStr.isEmpty()) ? Integer.parseInt(userIdStr) : -1;
        } catch (NumberFormatException e) {
            Log.e(TAG, "Failed to parse userID: " + userIdStr, e);
            userId = -1;
        }
        Log.d(TAG, "User ID from SharedPreferences: " + userId);

        // Fetch and setup Unity ads from backend
        fetchAndSetupUnityAds();

        // Get userId and tournamentId values before this
        if (userId > 0 && tournamentId > 0) {
            checkIfAlreadyJoinedAndSetupUI();
        } else {
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }

        // Set up ViewPager2 and TabLayout
        MatchDetailsAdapter adapter = new MatchDetailsAdapter(this, tournamentId);
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
        fetchUserData(userId);
    }

    // Fetch Unity Ads configuration from backend
    private void fetchAndSetupUnityAds() {
        String url = getString(R.string.app_url) + "/get_active_ads.php";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getString("status").equals("success")) {
                            JSONObject data = json.getJSONObject("data");
                            String provider = data.getString("provider_name");

                            Log.d(TAG, "Ad Provider: " + provider);

                            if (provider.equalsIgnoreCase("Unity")) {
                                String unityAppId = data.getString("app_id");

                                // Initialize Unity Ads if not already initialized
                                if (!UnityAds.isInitialized()) {
                                    UnityAds.initialize(this, unityAppId, false, new IUnityAdsInitializationListener() {
                                        @Override
                                        public void onInitializationComplete() {
                                            isUnityInitialized = true;
                                            Log.d(TAG, "Unity Ads initialized successfully");
                                        }

                                        @Override
                                        public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
                                            isUnityInitialized = false;
                                            Log.e(TAG, "Unity Ads initialization failed: " + message);
                                        }
                                    });
                                } else {
                                    isUnityInitialized = true;
                                }

                                // Check if interstitial ads are enabled
                                if (data.optBoolean("is_interstitial_enabled", false)) {
                                    isUnityAdsEnabled = true;
                                    unityInterstitialId = data.optString("interstitial_ad_id", "");
                                    loadUnityInterstitialAd(unityInterstitialId);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "Error fetching ads: " + e.getMessage());
                    }
                },
                error -> Log.e(TAG, "Ad Fetch Error: " + error.toString())
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    // Load Unity Interstitial Ad
    private void loadUnityInterstitialAd(String adUnitId) {
        UnityAds.load(adUnitId, new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(String placementId) {
                Log.d(TAG, "Unity Interstitial Loaded: " + placementId);
            }

            @Override
            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                Log.e(TAG, "Interstitial Load Failed: " + message);
            }
        });
    }

    private void fetchUserData(int userId) {
        String url = getString(R.string.app_url) + "/get_user_info_api.php?id=" + userId;

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("success")) {
                            JSONObject user = jsonObject.getJSONObject("user");

                            userCoins = user.getInt("coins");
                            userTickets = user.getInt("tickets");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                });

        queue.add(stringRequest);
    }

    private void updateMatchDetails(int tournamentId) {
        String url = getResources().getString(R.string.app_url) + "/tournament_details_api.php?id=" + tournamentId;
        Log.d(TAG, "Fetching match details from: " + url);
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.d(TAG, "Match details API response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean status = jsonResponse.getBoolean("status");
                        if (!status) {
                            Toast.makeText(TournamentMatchDetail.this, "Match not found! Please try again.", Toast.LENGTH_LONG).show();
                            joinButton.setEnabled(false);
                            finish();
                            return;
                        }

                        JSONObject match = jsonResponse.getJSONObject("data");
                        matchType = match.optString("type", "Unknown").trim();
                        String matchStatus = match.optString("match_status", "1").trim();
                        String videoUrl = match.optString("match_url", "").trim();

                        // Pass match data to DescriptionFragment
                        Bundle bundle = new Bundle();
                        bundle.putString("match_type", matchType);
                        bundle.putString("match_name", match.optString("match_name", "Unknown"));
                        bundle.putString("map", match.optString("MAP", "Unknown"));
                        bundle.putInt("entry_fee_coins", match.optInt("entry_fee_coins", 0));
                        bundle.putInt("entry_fee_tickets", match.optInt("entry_fee_tickets", 0));
                        bundle.putString("entry_type", match.optString("entry_type", "any"));
                        bundle.putInt("per_kill", match.optInt("per_kill", 0));
                        bundle.putString("match_time", match.optString("match_time", "Unknown"));
                        bundle.putString("prize_details", "Winning Prize: " + match.optInt("win_prize", 0) +
                                "\n" + match.optString("prize_description", ""));
                        bundle.putString("match_desc", match.optString("match_desc", ""));
                        bundle.putString("match_banner", match.optString("match_banner", ""));
                        bundle.putString("room_description", match.optString("room_description", ""));

                        entryFeeCoins = match.optInt("entry_fee_coins", 0);
                        entryFeeTickets = match.optInt("entry_fee_tickets", 0);
                        entryType = match.optString("entry_type", "any");

                        // Update DescriptionFragment with match data
                        Fragment fragment = getSupportFragmentManager().findFragmentByTag("f0");
                        if (fragment instanceof DescriptionFragment) {
                            ((DescriptionFragment) fragment).updateMatchDetails(bundle);
                        }

                        // Decide action based on match_status
                        if (matchStatus.equals("3") && videoUrl != null && !videoUrl.isEmpty()) {
                            joinButton.setText("Watch Video");
                            joinButton.setOnClickListener(v -> {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                                startActivity(intent);
                            });
                        } else {
                            // Enable join button only if match type is valid
                            if (!matchType.equalsIgnoreCase("Unknown") && !matchType.isEmpty()) {
                                joinButton.setEnabled(true);
                                Log.d(TAG, "Join button enabled with matchType: " + matchType);
                            } else {
                                Toast.makeText(TournamentMatchDetail.this, "Invalid match type: " + matchType, Toast.LENGTH_LONG).show();
                                joinButton.setEnabled(false);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Match details JSON error: " + e.getMessage() + ", Raw response: " + response);
                        Toast.makeText(TournamentMatchDetail.this, "Error parsing match data: " + response, Toast.LENGTH_LONG).show();
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
                    Toast.makeText(TournamentMatchDetail.this, errorMsg, Toast.LENGTH_LONG).show();
                    joinButton.setEnabled(false);
                    finish();
                });

        queue.add(request);
        Log.d(TAG, "Match details API request queued");
    }

    private void sendJoinRequest(String joinType, String inGameUsername) {
        if (matchType.isEmpty() || matchType.equalsIgnoreCase("Unknown")) {
            Toast.makeText(this, "Match type not loaded: " + matchType, Toast.LENGTH_LONG).show();
            return;
        }

        if (userId <= 0 || tournamentId <= 0) {
            Toast.makeText(this, "Invalid parameters - userId: " + userId + ", tournamentId: " + tournamentId, Toast.LENGTH_LONG).show();
            return;
        }

        Log.d(TAG, "Join button clicked - user_id: " + userId + ", match_id: " + tournamentId + ", match_type: " + matchType);

        String url = getString(R.string.app_url) + "/join_tournament_api.php";
        Log.d(TAG, "Join API URL: " + url);

        Map<String, String> params = new HashMap<>();
        params.put("user", String.valueOf(userId));
        params.put("match", String.valueOf(tournamentId));
        params.put("match_type", matchType);
        params.put("entry_type", joinType);
        params.put("in_game_username", inGameUsername);

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
                            sendJoinRequest(joinType, inGameUsername);
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
                            joinButton.setBackgroundTintList(ColorStateList.valueOf(0xFF888888));
                        } else {
                            String message = jsonResponse.optString("message", "Failed to join");
                            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Join JSON error: " + e.getMessage() + ", Raw response: " + response);
                        if (retryCount < MAX_RETRIES) {
                            retryCount++;
                            Log.d(TAG, "Retrying request due to JSON error (attempt " + retryCount + ")");
                            sendJoinRequest(joinType, inGameUsername);
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
                        sendJoinRequest(joinType, inGameUsername);
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

    private void checkIfAlreadyJoinedAndSetupUI() {
        String checkUrl = getString(R.string.app_url) +
                "/join_tournament_api.php?user=" + userId + "&match=" + tournamentId;

        StringRequest request = new StringRequest(Request.Method.GET, checkUrl,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.optBoolean("status", false)) {
                            boolean alreadyJoined = jsonResponse.optBoolean("already_joined", false);
                            boolean slotsFull = jsonResponse.optBoolean("slots_full", false);

                            if (alreadyJoined) {
                                joinButton.setEnabled(false);
                                joinButton.setText("Already Joined");
                                joinButton.setBackgroundTintList(ColorStateList.valueOf(0xFF888888));
                            } else if (slotsFull) {
                                joinButton.setEnabled(false);
                                joinButton.setText("Match Full");
                                joinButton.setBackgroundTintList(ColorStateList.valueOf(0xFF888888));
                            } else {
                                joinButton.setEnabled(true);
                                joinButton.setText("Join Match");
                                joinButton.setOnClickListener(v -> showJoinMethodPopup(userCoins, userTickets, entryFeeCoins, entryFeeTickets, entryType));
                            }
                        } else {
                            Toast.makeText(this, "API error: " + jsonResponse.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing join status", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Failed to check join status", Toast.LENGTH_SHORT).show()
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void showJoinMethodPopup(int userCoins, int userTickets, int entryCoins, int entryTickets, String entryType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Join Match");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_join_match, null);
        builder.setView(dialogView);

        // Force white background
        dialogView.setBackgroundColor(Color.WHITE);

        EditText editInGameUsername = dialogView.findViewById(R.id.editInGameUsername);
        RadioGroup radioGroupJoinType = dialogView.findViewById(R.id.radioGroupJoinType);
        RadioButton radioCoin = dialogView.findViewById(R.id.radioCoin);
        RadioButton radioTickets = dialogView.findViewById(R.id.radioTickets);

        Drawable coinDrawable = ContextCompat.getDrawable(this, R.drawable.coin_icon);
        Drawable ticketDrawable = ContextCompat.getDrawable(this, R.drawable.ic_ticket_24);

        // Resize icons
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
        if (coinDrawable != null) coinDrawable.setBounds(0, 0, size, size);
        if (ticketDrawable != null) ticketDrawable.setBounds(0, 0, size, size);

        radioCoin.setCompoundDrawables(coinDrawable, null, null, null);
        radioTickets.setCompoundDrawables(ticketDrawable, null, null, null);

        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        radioCoin.setCompoundDrawablePadding(padding);
        radioTickets.setCompoundDrawablePadding(padding);

        radioCoin.setText(String.valueOf(entryCoins));
        radioTickets.setText(String.valueOf(entryTickets));

        if (entryType.equals("coin")) {
            radioTickets.setVisibility(View.GONE);
        } else if (entryType.equals("tickets")) {
            radioCoin.setVisibility(View.GONE);
        }

        builder.setPositiveButton("Join", null);
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Force white background regardless of theme
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        }

        // Override positive button to prevent dialog auto-dismiss
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String inGameUsername = editInGameUsername.getText().toString().trim();
            int selectedId = radioGroupJoinType.getCheckedRadioButtonId();

            if (inGameUsername.isEmpty()) {
                Toast.makeText(this, "Please enter your in-game username", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedId == -1) {
                Toast.makeText(this, "Please select a join method", Toast.LENGTH_SHORT).show();
                return;
            }

            String selectedType = selectedId == R.id.radioCoin ? "coin" : "tickets";
            if (selectedType.equals("coin") && userCoins >= entryCoins) {
                joinType = "coin";
                dialog.dismiss();
                // Show ad before joining
                showInterstitialAdBeforeJoin(joinType, inGameUsername);
            } else if (selectedType.equals("tickets") && userTickets >= entryTickets) {
                joinType = "tickets";
                dialog.dismiss();
                // Show ad before joining
                showInterstitialAdBeforeJoin(joinType, inGameUsername);
            } else {
                Toast.makeText(this, "Not enough " + selectedType, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Show Unity Interstitial Ad before joining match
    private void showInterstitialAdBeforeJoin(String joinType, String inGameUsername) {
        // Check if Unity Ads are enabled and initialized
        if (isUnityAdsEnabled && isUnityInitialized && unityInterstitialId != null && !unityInterstitialId.isEmpty()) {
            Log.d(TAG, "Attempting to show Unity Interstitial Ad");

            UnityAds.show(this, unityInterstitialId, new UnityAdsShowOptions(), new IUnityAdsShowListener() {
                @Override
                public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
                    Log.d(TAG, "Unity Ad completed with state: " + state);
                    // Ad completed, proceed with join request
                    runOnUiThread(() -> sendJoinRequest(joinType, inGameUsername));

                    // Reload the ad for next time
                    loadUnityInterstitialAd(unityInterstitialId);
                }

                @Override
                public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
                    Log.e(TAG, "Unity Ad failed to show: " + message);
                    // Ad failed, still proceed with join request
                    runOnUiThread(() -> {
                        Toast.makeText(TournamentMatchDetail.this, "Ad not available", Toast.LENGTH_SHORT).show();
                        sendJoinRequest(joinType, inGameUsername);
                    });
                }

                @Override
                public void onUnityAdsShowStart(String placementId) {
                    Log.d(TAG, "Unity Ad started showing");
                }

                @Override
                public void onUnityAdsShowClick(String placementId) {
                    Log.d(TAG, "Unity Ad clicked");
                }
            });
        } else {
            // Unity Ads not configured or not ready, proceed directly
            Log.d(TAG, "Unity Ads not enabled/ready, proceeding without ad");
            sendJoinRequest(joinType, inGameUsername);
        }
    }
}