package com.app.earnstation;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class LuckyNumberWinnerActivity extends AppCompatActivity {

    private RecyclerView winnerRecyclerView;
    private WinnerAdapter adapter;
    private ArrayList<WinnerModel> winnerList = new ArrayList<>();
    private ImageView backButton;
    private TextView luckyNumberText;
    private RequestQueue requestQueue;
    private String luckyNumberId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lucky_number_winner_activity);

        initializeViews();
        setupRecyclerView();

        luckyNumberId = getIntent().getStringExtra("lucky_number_id");
        if (luckyNumberId == null) {
            finish();
            return;
        }

        requestQueue = Volley.newRequestQueue(this);
        backButton.setOnClickListener(v -> onBackPressed());

        fetchWinners();
    }

    private void initializeViews() {
        winnerRecyclerView = findViewById(R.id.winnerRecyclerView);
        luckyNumberText = findViewById(R.id.luckyNumberText);
        backButton = findViewById(R.id.back_button);
    }

    private void setupRecyclerView() {
        winnerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WinnerAdapter(this, winnerList);
        winnerRecyclerView.setAdapter(adapter);
    }

    private void fetchWinners() {
        String url = getResources().getString(R.string.app_url) +
                "/get_lucky_number_winner.php?lucky_number_id=" + luckyNumberId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                this::handleWinnerResponse,
                error -> {
                    Log.e("API_ERROR", "Error fetching winners", error);
                    Toast.makeText(this, "Failed to load winners", Toast.LENGTH_SHORT).show();
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(30000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }

    private void handleWinnerResponse(JSONObject response) {
        try {
            if (response.getBoolean("success")) {
                JSONArray dataArray = response.getJSONArray("data");

                if (dataArray.length() > 0) {
                    setLuckyNumber(dataArray.getJSONObject(0));
                    populateWinnerList(dataArray);
                    adapter.notifyDataSetChanged();
                }
            } else {
                Toast.makeText(this, "No winners found", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Log.e("PARSE_ERROR", "Error parsing response", e);
            Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show();
        }
    }

    private void setLuckyNumber(JSONObject firstWinner) throws JSONException {
        if (!firstWinner.isNull("unique_number")) {
            int uniqueNumber = firstWinner.getInt("unique_number");
            luckyNumberText.setText(String.valueOf(uniqueNumber));
        }
    }

    private void populateWinnerList(JSONArray dataArray) throws JSONException {
        winnerList.clear();
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject obj = dataArray.getJSONObject(i);
            WinnerModel winner = new WinnerModel();
            winner.setUsername(obj.getString("username"));
            winner.setCoins(obj.getInt("number_coins"));
            winner.setDate(obj.getString("created_at"));
            winnerList.add(winner);
        }
    }
}