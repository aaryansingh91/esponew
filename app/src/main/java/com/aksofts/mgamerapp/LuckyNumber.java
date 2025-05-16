package com.aksofts.mgamerapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LuckyNumber extends AppCompatActivity {

    private LinearLayout container;
    private RequestQueue requestQueue;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lucky_number);

        container = findViewById(R.id.container);
        backButton = findViewById(R.id.back_button);
        requestQueue = Volley.newRequestQueue(this);

        // Set back button click listener
        backButton.setOnClickListener(v -> finish());

        fetchLuckyDraws();
    }

    private void fetchLuckyDraws() {
        String url = "https://mg.amsit.in/amsit-adm/lucky_draw_api.php";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                JSONArray data = response.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject draw = data.getJSONObject(i);
                                    String id = draw.getString("id");
                                    String coins = draw.getString("coins");
                                    int totalSlots = draw.getInt("total_slots");
                                    int takenSlots = draw.getInt("taken_slots");
                                    String statusDraw = draw.getString("status");

                                    if (statusDraw.equals("active")) {
                                        addLuckyDrawCard(id, coins, totalSlots, takenSlots);
                                    }
                                }
                            } else {
                                Toast.makeText(LuckyNumber.this, "Failed to fetch lucky draws", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LuckyNumber.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(LuckyNumber.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void addLuckyDrawCard(String id, String coins, int totalSlots, int takenSlots) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View cardView = inflater.inflate(R.layout.card_lucky_draw, container, false);

        TextView tvWinAmount = cardView.findViewById(R.id.tv_win_amount);
        TextView tvDrawId = cardView.findViewById(R.id.tv_draw_id);
        TextView tvSlotsLeft = cardView.findViewById(R.id.tv_slots_left);
        TextView tvPercentageFilled = cardView.findViewById(R.id.tv_percentage_filled);
        ProgressBar progressBar = cardView.findViewById(R.id.progress_bar);
        Button btnCheckWinners = cardView.findViewById(R.id.btn_check_winners);
        Button btnGetFreeEntry = cardView.findViewById(R.id.btn_get_free_entry);

        tvWinAmount.setText("Win " + coins);
        tvDrawId.setText("#" + id);
        tvSlotsLeft.setText("Left: " + (totalSlots - takenSlots) + "/" + totalSlots);

        int percentage = (int) ((takenSlots * 100.0) / totalSlots);
        tvPercentageFilled.setText(percentage + "% Filled");
        progressBar.setProgress(percentage);

        btnCheckWinners.setOnClickListener(v -> {
            Toast.makeText(this, "Check Winners for Draw #" + id, Toast.LENGTH_SHORT).show();
            // Implement navigation or action for checking winners
        });

        btnGetFreeEntry.setOnClickListener(v -> {
            Toast.makeText(this, "Get Free Entry for Draw #" + id, Toast.LENGTH_SHORT).show();
            // Implement action for getting free entry
        });

        container.addView(cardView);
    }
}