package com.aksofts.mgamerapp;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

    LinearLayout luckyDrawContainer;
    ImageView back_button;

    private int USER_ID; // Make it accessible to inner classes

    // Static user ID as requested



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lucky_draw);

        SharedPreferences prefs = getSharedPreferences("pgamerapp", MODE_PRIVATE);
        String userIdStr = prefs.getString("userID", null);

        if (userIdStr != null) {
            USER_ID = Integer.parseInt(userIdStr);
        } else {
            USER_ID = -1; // fallback if not found
        }


        luckyDrawContainer = findViewById(R.id.lucky_draw_container);
        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(view -> finish());


        new FetchLuckyDraws().execute();
    }

    class FetchLuckyDraws extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                String baseUrl = getString(R.string.app_url); // from strings.xml
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

                        String id = draw.getString("id");
                        int coins = draw.getInt("coins");
                        int totalSlots = draw.getInt("total_slots");
                        int takenSlots = draw.getInt("taken_slots");
                        boolean joined = draw.getBoolean("joined");

                        int filledPercent = (int) (((float) takenSlots / totalSlots) * 100);
                        int leftSlots = totalSlots - takenSlots;

                        LayoutInflater inflater = LayoutInflater.from(LuckyDrawActivity.this);
                        View cardView = inflater.inflate(R.layout.item_lucky_draw, luckyDrawContainer, false);

                        ((TextView) cardView.findViewById(R.id.tvTitle)).setText("Win " + coins);
                        ((TextView) cardView.findViewById(R.id.tvDrawId)).setText("#" + id);
                        ((TextView) cardView.findViewById(R.id.tvLeftSlots)).setText("Left : " + leftSlots + "/" + totalSlots);
                        ((TextView) cardView.findViewById(R.id.tvFilledPercentage)).setText(filledPercent + "% Filled");
                        ((ProgressBar) cardView.findViewById(R.id.progressBar)).setProgress(filledPercent);

                        // Find or create Get Free Entry button inside your item_lucky_draw.xml
                        Button btnGetFreeEntry = cardView.findViewById(R.id.btn_get_free_entry);
                        if (btnGetFreeEntry != null) {
                            btnGetFreeEntry.setOnClickListener(v -> {
                                // Call AsyncTask to join the lucky draw
                                new JoinLuckyDrawTask(USER_ID, Integer.parseInt(id)).execute();
                            });
                        }
                        if (btnGetFreeEntry != null) {
                            if (joined) {
                                btnGetFreeEntry.setText("Participated");
                                btnGetFreeEntry.setEnabled(false);
                            } else {
                                btnGetFreeEntry.setText("Get Free Entry");
                                btnGetFreeEntry.setEnabled(true);

                                btnGetFreeEntry.setOnClickListener(v -> {
                                    // Call AsyncTask to join the lucky draw
                                    new JoinLuckyDrawTask(USER_ID, Integer.parseInt(id)).execute();
                                });
                            }
                        }
                        luckyDrawContainer.addView(cardView);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class JoinLuckyDrawTask extends AsyncTask<Void, Void, String> {

        private final int userId;
        private final int luckyDrawId;

        JoinLuckyDrawTask(int userId, int luckyDrawId) {
            this.userId = userId;
            this.luckyDrawId = luckyDrawId;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                String baseUrl = getString(R.string.app_url); // from strings.xml
                String finalUrl = baseUrl + "/join_lucky_draw.php?user_id=" + USER_ID;
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
                        recreate();
//                        button.setEnabled(false);
//                        button.setText("Already Joined");

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
