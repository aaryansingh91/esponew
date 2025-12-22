package com.app.earnstation;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LuckyDrawActivity extends AppCompatActivity {

    private LinearLayout luckyDrawContainer;
    private ImageView back_button;
    private int USER_ID;
    private ShimmerFrameLayout shimmerLayout;
    private LinearLayout shimmerContainer;
    private Dialog loadingDialog;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lucky_draw);

        initializeViews();
        setupShimmer();

        if (!loadUserId()) {
            finish();
            return;
        }

        requestQueue = Volley.newRequestQueue(this);
        back_button.setOnClickListener(view -> finish());
        new FetchLuckyDraws().execute();
    }

    private void initializeViews() {
        shimmerLayout = findViewById(R.id.shimmer_layout);
        shimmerContainer = findViewById(R.id.shimmer_container);
        luckyDrawContainer = findViewById(R.id.lucky_draw_container);
        back_button = findViewById(R.id.back_button);
    }

    private void setupShimmer() {
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < 5; i++) {
            View shimmerCard = inflater.inflate(R.layout.lucky_draw_shimmer, shimmerContainer, false);
            shimmerContainer.addView(shimmerCard);
        }
    }

    private boolean loadUserId() {
        SharedPreferences prefs = getSharedPreferences("EspoTaskApp", MODE_PRIVATE);
        String userIdStr = prefs.getString("userID", null);

        if (userIdStr != null) {
            USER_ID = Integer.parseInt(userIdStr);
            return true;
        }
        return false;
    }

    private void hideShimmer() {
        shimmerLayout.stopShimmer();
        shimmerLayout.setVisibility(View.GONE);
        luckyDrawContainer.setVisibility(View.VISIBLE);
    }

    private void checkWinners(String luckyDrawId) {
        showLoadingDialog();

        String url = getString(R.string.app_url) + "/get_lucky_draw_winner.php?lucky_draw_id=" + luckyDrawId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    dismissLoadingDialog();
                    handleWinnerCheckResponse(response, luckyDrawId);
                },
                error -> {
                    dismissLoadingDialog();
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(30000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }

    private void handleWinnerCheckResponse(JSONObject response, String luckyDrawId) {
        try {
            boolean success = response.getBoolean("success");
            if (success) {
                openWinnerActivity(luckyDrawId);
            } else {
                showResultNotDeclaredDialog();
            }
        } catch (JSONException e) {
            Toast.makeText(this, "Invalid response", Toast.LENGTH_SHORT).show();
        }
    }

    private void openWinnerActivity(String luckyDrawId) {
        Intent intent = new Intent(this, LuckyDrawWinnerActivity.class);
        intent.putExtra("lucky_draw_id", luckyDrawId);
        startActivity(intent);
    }

    private void showLoadingDialog() {
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        loadingDialog.show();
    }

    private void dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    private void showResultNotDeclaredDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_result_not_declared);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ImageView closeIcon = dialog.findViewById(R.id.close_icon);
        Button btnOkay = dialog.findViewById(R.id.btn_okay);

        closeIcon.setOnClickListener(v -> dialog.dismiss());
        btnOkay.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    class FetchLuckyDraws extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                String baseUrl = getString(R.string.app_url);
                String finalUrl = baseUrl + "/lucky_draw_api.php?user_id=" + USER_ID;
                URL url = new URL(finalUrl);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String json) {
            if (json != null) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray dataArray = jsonObject.getJSONArray("data");

                    luckyDrawContainer.removeAllViews();

                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject draw = dataArray.getJSONObject(i);
                        addLuckyDrawCard(draw);
                    }
                    hideShimmer();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addLuckyDrawCard(JSONObject draw) throws JSONException {
        String id = draw.getString("id");
        int coins = draw.getInt("coins");
        int totalSlots = draw.getInt("total_slots");
        int takenSlots = draw.getInt("taken_slots");
        boolean joined = draw.getBoolean("joined");

        int filledPercent = (int) (((float) takenSlots / totalSlots) * 100);
        int leftSlots = totalSlots - takenSlots;

        View cardView = LayoutInflater.from(this).inflate(R.layout.item_lucky_draw, luckyDrawContainer, false);

        setupCardViews(cardView, id, coins, leftSlots, totalSlots, filledPercent, joined);
        luckyDrawContainer.addView(cardView);
    }

    private void setupCardViews(View cardView, String id, int coins, int leftSlots, int totalSlots, int filledPercent, boolean joined) {
        ((TextView) cardView.findViewById(R.id.tvTitle)).setText("Win " + coins);
        ((TextView) cardView.findViewById(R.id.tvDrawId)).setText("#" + id);
        ((TextView) cardView.findViewById(R.id.tvLeftSlots)).setText("Left : " + leftSlots + "/" + totalSlots);
        ((TextView) cardView.findViewById(R.id.tvFilledPercentage)).setText(filledPercent + "% Filled");
        ((ProgressBar) cardView.findViewById(R.id.progressBar)).setProgress(filledPercent);

        MaterialButton btnGetFreeEntry = cardView.findViewById(R.id.btn_get_free_entry);
        MaterialButton btnCheckWinners = cardView.findViewById(R.id.btn_check_winners);

        setupEntryButton(btnGetFreeEntry, joined, id);
        btnCheckWinners.setOnClickListener(v -> checkWinners(id));
    }

    private void setupEntryButton(MaterialButton btnGetFreeEntry, boolean joined, String id) {
        if (joined) {
            btnGetFreeEntry.setText("Participated");
            btnGetFreeEntry.setEnabled(false);
            btnGetFreeEntry.setAlpha(0.5f);
        } else {
            btnGetFreeEntry.setText("Get Free Entry");
            btnGetFreeEntry.setEnabled(true);
            btnGetFreeEntry.setOnClickListener(v -> {
                new JoinLuckyDrawTask(USER_ID, Integer.parseInt(id), btnGetFreeEntry).execute();
            });
        }
    }

    private class JoinLuckyDrawTask extends AsyncTask<Void, Void, String> {
        private final int userId;
        private final int luckyDrawId;
        private final MaterialButton button;

        JoinLuckyDrawTask(int userId, int luckyDrawId, MaterialButton button) {
            this.userId = userId;
            this.luckyDrawId = luckyDrawId;
            this.button = button;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                String baseUrl = getString(R.string.app_url);
                String finalUrl = baseUrl + "/join_lucky_draw.php";
                URL url = new URL(finalUrl);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String postData = "user_id=" + userId + "&lucky_draw_id=" + luckyDrawId;

                OutputStream os = new BufferedOutputStream(conn.getOutputStream());
                os.write(postData.getBytes());
                os.flush();
                os.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();

                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    Toast.makeText(LuckyDrawActivity.this, message, Toast.LENGTH_SHORT).show();

                    if (status.equals("success")) {
                        button.setText("Participated");
                        button.setEnabled(false);
                        button.setAlpha(0.5f);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LuckyDrawActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LuckyDrawActivity.this, "Failed to join lucky draw", Toast.LENGTH_SHORT).show();
            }
        }
    }
}