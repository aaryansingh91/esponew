package com.aksofts.mgamerapp;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

public class TournamentMatchDetail extends AppCompatActivity {

    // Declare views
    private TextView matchTypeOverlay, matchTitle, matchTypeText, matchMapText, entryFeeText,
            perKillText, matchScheduleText, prizeDetailsText, matchDescriptionText;
    private Button joinButton;
    private ImageView topImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_match_detail);

        // Initialize views
        topImage = findViewById(R.id.top_image);
        matchTypeOverlay = findViewById(R.id.match_type_overlay);
        matchTitle = findViewById(R.id.match_title);
        matchTypeText = findViewById(R.id.match_type_text);
        matchMapText = findViewById(R.id.match_map_text);
        entryFeeText = findViewById(R.id.entry_fee_text);
        perKillText = findViewById(R.id.per_kill_text);
        matchScheduleText = findViewById(R.id.match_schedule_text);
        prizeDetailsText = findViewById(R.id.prize_details_text);
        matchDescriptionText = findViewById(R.id.match_description_text);
        joinButton = findViewById(R.id.join_button);

        // Get the tournament ID from the Intent
        int tournamentId = getIntent().getIntExtra("TOURNAMENT_ID", -1); // Default to -1 if not found
        Log.d("TournamentMatchDetail", "Received ID: " + tournamentId);
        if (tournamentId == -1) {
            Toast.makeText(this, "Invalid tournament ID", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if no valid ID
            return;
        }

        // Fetch and update match details using the API
        updateMatchDetails(tournamentId);
    }

    private void updateMatchDetails(int tournamentId) {

        String url =  getResources().getString(R.string.app_url) + "/amsit-adm/tournament_details_api.php?id=" + tournamentId;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        boolean status = response.getBoolean("status");
                        if (!status) {
                            Toast.makeText(this, "Match not found!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONObject match = response.getJSONObject("data");

                        // Map API fields to UI elements
                        matchTypeOverlay.setVisibility(View.VISIBLE);
                        matchTypeOverlay.setText(match.getString("type"));

                        matchTitle.setText(match.getString("match_name"));
                        matchTypeText.setText(match.getString("type"));
                        matchMapText.setText(match.getString("MAP"));
                        entryFeeText.setText(String.valueOf(match.getInt("entry_fee")));
                        perKillText.setText(String.valueOf(match.getInt("per_kill")));
                        matchScheduleText.setText(match.getString("match_time"));
                        prizeDetailsText.setText("Winning Prize: " + match.getInt("win_prize") +
                                "\n" + match.getString("prize_description"));
                        matchDescriptionText.setText(Html.fromHtml(match.getString("match_desc")));

                        joinButton.setText("Join Now");

                        // Load banner image using Glide
                        String bannerUrl = match.getString("match_banner");
                        if (bannerUrl != null && !bannerUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(bannerUrl)
                                    .placeholder(R.drawable.freefire) // Fallback image
                                    .into(topImage);
                        } else {
                            topImage.setImageResource(R.drawable.freefire);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Failed to fetch match details", Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
    }
}