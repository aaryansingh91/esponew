// LuckyDrawActivity.java
package com.aksofts.mgamerapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LuckyDrawActivity extends AppCompatActivity {



    LinearLayout luckyDrawContainer;

    ImageView back_button;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lucky_draw);

        luckyDrawContainer = findViewById(R.id.lucky_draw_container);
        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(view -> {
//            Intent intent = new Intent(this, HomeActivity.class);
//            startActivity(intent);
            finish(); // मौजूदा Activity बंद हो जाएगी
        });
        new FetchLuckyDraws().execute();
    }

    class FetchLuckyDraws extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("https://mg.amsit.in/amsit-adm/lucky_draw_api.php");
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

                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject draw = dataArray.getJSONObject(i);

                        String id = draw.getString("id");
                        int coins = draw.getInt("coins");
                        int totalSlots = draw.getInt("total_slots");
                        int takenSlots = draw.getInt("taken_slots");

                        int filledPercent = (int) (((float) takenSlots / totalSlots) * 100);
                        int leftSlots = totalSlots - takenSlots;

                        LayoutInflater inflater = LayoutInflater.from(LuckyDrawActivity.this);
                        View cardView = inflater.inflate(R.layout.item_lucky_draw, null);

                        ((TextView) cardView.findViewById(R.id.tvTitle)).setText("Win " + coins);
                        ((TextView) cardView.findViewById(R.id.tvDrawId)).setText("#" + id);
                        ((TextView) cardView.findViewById(R.id.tvLeftSlots)).setText("Left : " + leftSlots + "/" + totalSlots);
                        ((TextView) cardView.findViewById(R.id.tvFilledPercentage)).setText(filledPercent + "% Filled");
                        ((ProgressBar) cardView.findViewById(R.id.progressBar)).setProgress(filledPercent);

                        luckyDrawContainer.addView(cardView);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
