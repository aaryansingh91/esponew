package com.app.espotask;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
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
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LuckyNumber extends AppCompatActivity {

    private ShimmerFrameLayout shimmerLayout;
    private LinearLayout shimmerContainer;
    private LinearLayout container;
    private RequestQueue requestQueue;
    private ImageView backButton;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lucky_number);

        shimmerLayout = findViewById(R.id.shimmer_layout);
        shimmerContainer = findViewById(R.id.shimmer_container);
        container = findViewById(R.id.container);
        backButton = findViewById(R.id.back_button);

        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmer();
        shimmerContainer.removeAllViews();

        for (int i = 0; i < 3; i++) {
            View shimmerItem = LayoutInflater.from(this).inflate(R.layout.lucky_draw_shimmer, shimmerContainer, false);
            shimmerContainer.addView(shimmerItem);
        }

        SharedPreferences prefs = getSharedPreferences("EspoTaskApp", MODE_PRIVATE);
        String userIdStr = prefs.getString("userID", null);
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            finish();
            return;
        }

        try {
            userId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            finish();
            return;
        }

        requestQueue = Volley.newRequestQueue(this);
        backButton.setOnClickListener(v -> finish());

        fetchLuckyNumbers();
    }

    private void fetchLuckyNumbers() {
        String url = getResources().getString(R.string.app_url) + "/lucky_number_api.php?user_id=" + userId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        boolean status = response.getBoolean("status");
                        if (status && !response.isNull("data")) {
                            container.removeAllViews();
                            JSONArray data = response.getJSONArray("data");

                            if (data.length() == 0) {
                                showMessage("No lucky numbers available");
                            } else {
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject draw = data.getJSONObject(i);
                                    addLuckyNumberCard(draw);
                                }
                            }
                        } else {
                            showMessage(response.optString("message", "Failed to fetch lucky numbers"));
                        }
                    } catch (JSONException e) {
                        showMessage("Parsing error: " + e.getMessage());
                    } finally {
                        shimmerLayout.stopShimmer();
                        shimmerLayout.setVisibility(View.GONE);
                        container.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    String errorMessage = error.getMessage() != null ? error.getMessage() : "Network error";
                    showMessage("Error: " + errorMessage);
                });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);
    }

    private void addLuckyNumberCard(JSONObject draw) {
        try {
            String id = draw.getString("id");
            String luckyNumber = draw.getString("lucky_number");
            int totalSlots = draw.getInt("total_slots");
            int takenSlots = draw.getInt("taken_slots");
            boolean joined = draw.has("joined") && draw.getBoolean("joined");

            View cardView = LayoutInflater.from(this).inflate(R.layout.item_lucky_number, container, false);

            TextView tvWinAmount = cardView.findViewById(R.id.tv_win_amount);
            TextView tvDrawId = cardView.findViewById(R.id.tv_draw_id);
            TextView tvSlotsLeft = cardView.findViewById(R.id.tv_slots_left);
            TextView tvPercentageFilled = cardView.findViewById(R.id.tv_percentage_filled);
            ProgressBar progressBar = cardView.findViewById(R.id.progress_bar);
            Button btnCheckWinners = cardView.findViewById(R.id.btn_check_winners);
            MaterialButton btnGetFreeEntry = cardView.findViewById(R.id.btn_get_free_entry);

            tvWinAmount.setText("Win " + luckyNumber);
            tvDrawId.setText("#" + id);
            tvSlotsLeft.setText("Left: " + (totalSlots - takenSlots) + "/" + totalSlots);

            int percentage = (int) ((takenSlots * 100.0) / totalSlots);
            tvPercentageFilled.setText(percentage + "% Filled");
            progressBar.setProgress(percentage);

            if (joined) {
                btnGetFreeEntry.setText("Participated");
                btnGetFreeEntry.setEnabled(false);
                btnGetFreeEntry.setAlpha(0.5f);
            } else {
                btnGetFreeEntry.setOnClickListener(v -> showNumberPickerDialog(id));
            }

            btnCheckWinners.setOnClickListener(v -> {
                // Optional: implement winners view
            });

            container.addView(cardView);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showMessage(String message) {
        container.removeAllViews();
        TextView errorText = new TextView(this);
        errorText.setText(message);
        errorText.setTextSize(16);
        errorText.setTextColor(0xFF000000);
        errorText.setPadding(16, 16, 16, 16);
        errorText.setGravity(android.view.Gravity.CENTER);
        container.addView(errorText);
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
    }

    private void joinLuckyNumber(String luckyNumberId, int uniqueNumber) {
        String url = getResources().getString(R.string.app_url) + "/join_lucky_number.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        String status = json.getString("status");
                        String message = json.getString("message");

                        Toast.makeText(LuckyNumber.this, message, Toast.LENGTH_SHORT).show();

                        if (status.equals("success")) {
                            fetchLuckyNumbers(); // Refresh UI if joined
                        }
                    } catch (JSONException e) {
                        Toast.makeText(LuckyNumber.this, "Invalid response format", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(LuckyNumber.this, "Network error", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("lucky_number_id", luckyNumberId); // matches PHP variable
                params.put("unique_number", String.valueOf(uniqueNumber));
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(stringRequest);
    }


}
