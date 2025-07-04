package com.app.espotask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class LuckyNumberWinnerActivity extends AppCompatActivity {

    RecyclerView winnerRecyclerView;
    WinnerAdapter adapter;
    ArrayList<WinnerModel> winnerList = new ArrayList<>();
    ImageView backButton;
    TextView luckyNumberText;

    String apiUrl = "https://espotask.com/app-apis/get_lucky_number_winner.php?lucky_number_id=2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lucky_number_winner_activity);

        winnerRecyclerView = findViewById(R.id.winnerRecyclerView);
        luckyNumberText = findViewById(R.id.luckyNumberText);
        backButton = findViewById(R.id.back_button);

        winnerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WinnerAdapter(this, winnerList);
        winnerRecyclerView.setAdapter(adapter);

        backButton.setOnClickListener(v -> onBackPressed());



        new FetchWinnersTask().execute(apiUrl);
    }

    class FetchWinnersTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();
                return result.toString();

            } catch (Exception e) {
                Log.e("API_ERROR", "Error fetching winners", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getBoolean("success")) {
                    JSONArray dataArray = jsonObject.getJSONArray("data");

                    if (dataArray.length() > 0) {
                        // Set lucky number from first winner
                        JSONObject firstObj = dataArray.getJSONObject(0);
                        int uniqueNumber = firstObj.getInt("unique_number");
                        luckyNumberText.setText(String.valueOf(uniqueNumber));
                    }


                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject obj = dataArray.getJSONObject(i);
                        WinnerModel winner = new WinnerModel();
                        winner.setUsername(obj.getString("username"));
                        winner.setCoins(obj.getInt("number_coins"));
                        winner.setDate(obj.getString("created_at"));
                        winnerList.add(winner);
                    }

                    adapter.notifyDataSetChanged();
                }

            } catch (Exception e) {
                Log.e("PARSE_ERROR", "Error parsing response", e);
            }
        }
    }
}
