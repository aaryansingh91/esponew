package com.app.espotask;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LuckyNumber extends AppCompatActivity {

    private LinearLayout container;
    private RequestQueue requestQueue;
    private ImageView backButton;
    // अगर API की जरूरी हो, तो सही की डालें, वरना खाली छोड़ें
    private static final String API_KEY = ""; // उदाहरण: "abc123"
    private int userId;
    private static final String TAG = "LuckyNumber";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lucky_number);

        // SharedPreferences से user_id लें
        SharedPreferences prefs = getSharedPreferences("pgamerapp", MODE_PRIVATE);
        String userIdStr = prefs.getString("userID", null);
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        container = findViewById(R.id.container);
        backButton = findViewById(R.id.back_button);
        requestQueue = Volley.newRequestQueue(this);

        backButton.setOnClickListener(v -> finish());

        // डिबगिंग के लिए टेम्पररी SSL बायपास (प्रोडक्शन में हटाएं)
        /*try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    }
            };
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            Log.e(TAG, "SSL setup error: " + e.getMessage());
        }*/

        fetchLuckyNumbers();
    }

    private void fetchLuckyNumbers() {
        String url = getResources().getString(R.string.app_url) + "/lucky_number_api.php";
        // अगर API की जरूरी हो, तो इस लाइन को अनकमेंट करें:
        // String url = "https://mg.amsit.in/amsit-adm/lucky_number_api.php?api_key=" + API_KEY + "&user_id=" + userId;
        Log.d(TAG, "Fetching lucky numbers from: " + url);
        Log.d(TAG, "User ID: " + userId);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d(TAG, "API response: " + response.toString());
                    try {
                        boolean status = response.getBoolean("status") && !response.isNull("data");
                        if (status) {
                            container.removeAllViews();
                            JSONArray data = response.getJSONArray("data");
                            Log.d(TAG, "Number of draws: " + data.length());
                            if (data.length() == 0) {
                                TextView noDrawsText = new TextView(this);
                                noDrawsText.setText("No lucky draws available");
                                noDrawsText.setTextSize(16);
                                noDrawsText.setTextColor(0xFF000000);
                                noDrawsText.setPadding(16, 16, 16, 16);
                                noDrawsText.setGravity(android.view.Gravity.CENTER);
                                container.addView(noDrawsText);
                            } else {
                                boolean hasActiveDraws = false;
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject draw = data.getJSONObject(i);
                                    Log.d(TAG, "Processing draw: " + draw.toString());
                                    String statusDraw = draw.getString("status").trim();
                                    Log.d(TAG, "Draw ID: " + draw.getString("id") + ", Status: " + statusDraw);
                                    if (statusDraw.equals("active")) {
                                        hasActiveDraws = true;
                                        String id = draw.getString("id");
                                        String luckyNumber = draw.getString("lucky_number");
                                        int totalSlots = Integer.parseInt(draw.getString("total_slots"));
                                        int takenSlots = Integer.parseInt(draw.getString("taken_slots"));
                                        boolean joined = draw.has("joined") ? draw.getBoolean("joined") : false;
                                        addLuckyNumberCard(id, luckyNumber, totalSlots, takenSlots, joined);
                                    }
                                }
                                if (!hasActiveDraws) {
                                    TextView noActiveDrawsText = new TextView(this);
                                    noActiveDrawsText.setText("No active lucky draws available");
                                    noActiveDrawsText.setTextSize(16);
                                    noActiveDrawsText.setTextColor(0xFF000000);
                                    noActiveDrawsText.setPadding(16, 16, 16, 16);
                                    noActiveDrawsText.setGravity(android.view.Gravity.CENTER);
                                    container.addView(noActiveDrawsText);
                                }
                            }
                        } else {
                            String message = response.optString("message", "Failed to fetch lucky numbers");
                            Log.e(TAG, "API error: " + message);
                            Toast.makeText(LuckyNumber.this, message, Toast.LENGTH_SHORT).show();
                            TextView errorText = new TextView(this);
                            errorText.setText("Error: " + message);
                            errorText.setTextSize(16);
                            errorText.setTextColor(0xFF000000);
                            errorText.setPadding(16, 16, 16, 16);
                            errorText.setGravity(android.view.Gravity.CENTER);
                            container.addView(errorText);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                        Toast.makeText(LuckyNumber.this, "Error parsing data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        TextView errorText = new TextView(this);
                        errorText.setText("Error parsing data: " + e.getMessage());
                        errorText.setTextSize(16);
                        errorText.setTextColor(0xFF000000);
                        errorText.setPadding(16, 16, 16, 16);
                        errorText.setGravity(android.view.Gravity.CENTER);
                        container.addView(errorText);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Number parsing error: " + e.getMessage());
                        Toast.makeText(LuckyNumber.this, "Error parsing numbers: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        TextView errorText = new TextView(this);
                        errorText.setText("Error parsing numbers: " + e.getMessage());
                        errorText.setTextSize(16);
                        errorText.setTextColor(0xFF000000);
                        errorText.setPadding(16, 16, 16, 16);
                        errorText.setGravity(android.view.Gravity.CENTER);
                        container.addView(errorText);
                    }
                },
                error -> {
                    String errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown network error";
                    error.printStackTrace();
                    Log.e(TAG, "Volley error: " + errorMessage);
                    Log.e(TAG, "Volley error class: " + error.getClass().getSimpleName());
                    if (error.networkResponse != null) {
                        Log.e(TAG, "HTTP status code: " + error.networkResponse.statusCode);
                        try {
                            String responseBody = new String(error.networkResponse.data, "UTF-8");
                            Log.e(TAG, "Response data: " + responseBody);
                        } catch (Exception e) {
                            Log.e(TAG, "Error decoding response: " + e.getMessage());
                        }
                    }
                    Toast.makeText(LuckyNumber.this, "Error fetching data: " + errorMessage, Toast.LENGTH_LONG).show();
                    TextView errorText = new TextView(this);
                    errorText.setText("Network error: " + errorMessage);
                    errorText.setTextSize(16);
                    errorText.setTextColor(0xFF000000);
                    errorText.setPadding(16, 16, 16, 16);
                    errorText.setGravity(android.view.Gravity.CENTER);
                    container.addView(errorText);
                });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000, // 30 सेकंड टाइमआउट
                2, // 2 रीट्राई
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);
    }

    private void addLuckyNumberCard(String id, String luckyNumber, int totalSlots, int takenSlots, boolean joined) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View cardView = inflater.inflate(R.layout.item_lucky_number, container, false);

        TextView tvWinAmount = cardView.findViewById(R.id.tv_win_amount);
        TextView tvDrawId = cardView.findViewById(R.id.tv_draw_id);
        TextView tvSlotsLeft = cardView.findViewById(R.id.tv_slots_left);
        TextView tvPercentageFilled = cardView.findViewById(R.id.tv_percentage_filled);
        ProgressBar progressBar = cardView.findViewById(R.id.progress_bar);
        Button btnCheckWinners = cardView.findViewById(R.id.btn_check_winners);
        Button btnGetFreeEntry = cardView.findViewById(R.id.btn_get_free_entry);

        tvWinAmount.setText("Win " + luckyNumber);
        tvDrawId.setText("#" + id);
        tvSlotsLeft.setText("Left: " + (totalSlots - takenSlots) + "/" + totalSlots);

        int percentage = (int) ((takenSlots * 100.0) / totalSlots);
        tvPercentageFilled.setText(percentage + "% Filled");
        progressBar.setProgress(percentage);

        if (joined) {
            btnGetFreeEntry.setText("Participated");
            btnGetFreeEntry.setEnabled(false);
            btnGetFreeEntry.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF888888));
        } else {
            btnGetFreeEntry.setOnClickListener(v -> showNumberPickerDialog(id));
        }

        btnCheckWinners.setOnClickListener(v -> {
            Toast.makeText(this, "Check Winners for Draw #" + id, Toast.LENGTH_SHORT).show();
            // TODO: विजेताओं की जांच लागू करें
        });

        container.addView(cardView);
        Log.d(TAG, "Added card for draw ID: " + id);
    }

    private void showNumberPickerDialog(String coinId) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_number_picker);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        RecyclerView rvNumbers = dialog.findViewById(R.id.rv_numbers);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 9; i++) numbers.add(i);

        NumberAdapter adapter = new NumberAdapter(numbers, number -> {
            joinLuckyNumber(coinId, number);
            dialog.dismiss();
        });
        rvNumbers.setLayoutManager(new GridLayoutManager(this, 3));
        rvNumbers.setAdapter(adapter);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        Log.d(TAG, "Showing number picker dialog for coinId: " + coinId);
    }

    private void joinLuckyNumber(String coinId, int uniqueNumber) {
        String url = getResources().getString(R.string.app_url) + "/join_lucky_number.php";
        Log.d(TAG, "Joining lucky number for coinId: " + coinId + ", uniqueNumber: " + uniqueNumber);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d(TAG, "Join response: " + response);
                    try {
                        JSONObject json = new JSONObject(response);
                        String status = json.getString("status");
                        String message = json.getString("message");
                        Toast.makeText(LuckyNumber.this, message, Toast.LENGTH_SHORT).show();
                        if (status.equals("success")) {
                            fetchLuckyNumbers(); // UI रिफ्रेश करें
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Join JSON error: " + e.getMessage());
                        Toast.makeText(LuckyNumber.this, "Error parsing join response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown network error";
                    error.printStackTrace();
                    Log.e(TAG, "Join Volley error: " + errorMessage);
                    Log.e(TAG, "Join error class: " + error.getClass().getSimpleName());
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Join HTTP status code: " + error.networkResponse.statusCode);
                        try {
                            String responseBody = new String(error.networkResponse.data, "UTF-8");
                            Log.e(TAG, "Join response data: " + responseBody);
                        } catch (Exception e) {
                            Log.e(TAG, "Error decoding join response: " + e.getMessage());
                        }
                    }
                    Toast.makeText(LuckyNumber.this, "Failed to join lucky number: " + errorMessage, Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("coin_id", coinId);
                params.put("unique_number", String.valueOf(uniqueNumber));
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }
}