package com.app.earnstation;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
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

public class LuckyDrawWinnerActivity extends AppCompatActivity {

    private RecyclerView winnerRecyclerView;
    private WinnerAdapter adapter;
    private ArrayList<WinnerModel> winnerList = new ArrayList<>();
    private ImageView backButton;
    private RequestQueue requestQueue;
    private String luckyDrawId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lucky_draw_winner);

        initializeViews();
        setupRecyclerView();

        luckyDrawId = getIntent().getStringExtra("lucky_draw_id");
        if (luckyDrawId == null) {
            finish();
            return;
        }

        requestQueue = Volley.newRequestQueue(this);
        backButton.setOnClickListener(v -> onBackPressed());

        fetchWinners();
    }

    private void initializeViews() {
        winnerRecyclerView = findViewById(R.id.winnerRecyclerView);
        backButton = findViewById(R.id.back_button);
    }

    private void setupRecyclerView() {
        winnerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WinnerAdapter(this, winnerList);
        winnerRecyclerView.setAdapter(adapter);
    }

    private void fetchWinners() {
        String url = getString(R.string.app_url) +
                "/get_lucky_draw_winner.php?lucky_draw_id=" + luckyDrawId;

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

    private void populateWinnerList(JSONArray dataArray) throws JSONException {
        winnerList.clear();
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject obj = dataArray.getJSONObject(i);
            WinnerModel winner = new WinnerModel();
            winner.setUsername(obj.getString("username"));
            winner.setCoins(obj.getInt("draw_coins"));
            winner.setDate(obj.getString("created_at"));
            winnerList.add(winner);
        }
    }
}
