package com.app.espotask;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TournamentResult extends AppCompatActivity {

    private TextView matchTitle, matchTime, winningPrize, perKill, entryFee, winnersTitle;
    private ImageView bannerImage;
    private RecyclerView playersRecycler, winnersRecycler;

    private PlayerAdapter playerAdapter, winnersAdapter;
    private final List<PlayerModel> playerList = new ArrayList<>();
    private final List<PlayerModel> winnerList = new ArrayList<>();
    private final String BASE_URL = "https://espotask.com/app-apis/get_match_result.php?match_id=";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_result);

        int matchId = getIntent().getIntExtra("TOURNAMENT_ID", -1);

        matchTitle = findViewById(R.id.matchTitle);
        matchTime = findViewById(R.id.matchTime);
        winningPrize = findViewById(R.id.winningPrize);
        perKill = findViewById(R.id.perKill);
        entryFee = findViewById(R.id.entryFee);
        bannerImage = findViewById(R.id.bannerImage);
        playersRecycler = findViewById(R.id.playersRecycler);
        winnersRecycler = findViewById(R.id.winnersRecycler);
        winnersTitle = findViewById(R.id.winnersTitle);

        playersRecycler.setLayoutManager(new LinearLayoutManager(this));
        winnersRecycler.setLayoutManager(new LinearLayoutManager(this));

        playerAdapter = new PlayerAdapter(playerList);
        winnersAdapter = new PlayerAdapter(winnerList);

        playersRecycler.setAdapter(playerAdapter);
        winnersRecycler.setAdapter(winnersAdapter);

        if (matchId != -1) {
            fetchMatchData(matchId);
        } else {
            Toast.makeText(this, "Match ID not provided", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchMatchData(int matchId) {
        String url = BASE_URL + matchId;
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getBoolean("status")) {
                            JSONObject matchObj = response.getJSONObject("match");
                            MatchModel match = new Gson().fromJson(matchObj.toString(), MatchModel.class);

                            matchTitle.setText(match.match_name + " - Match #" + match.m_id);
                            matchTime.setText("Organised on " + match.match_time);
                            winningPrize.setText("Winning Prize : 🪙" + match.win_prize);
                            perKill.setText("Per Kill : 🪙" + match.per_kill);
                            entryFee.setText("Entry Fee : 🪙" + match.entry_fee_coins);

                            Glide.with(this)
                                    .load("https://espotask.com/match_banners/" + match.match_banner)
                                    .into(bannerImage);

                            JSONArray playersArray = response.getJSONArray("players");
                            playerList.clear();
                            winnerList.clear();

                            // Parse all players
                            for (int i = 0; i < playersArray.length(); i++) {
                                PlayerModel player = new Gson().fromJson(playersArray.getJSONObject(i).toString(), PlayerModel.class);
                                playerList.add(player);
                            }

                            // Sort by winning descending
                            playerList.sort((p1, p2) -> Double.compare(p2.winning, p1.winning));

                            // Assign ranks
                            for (int i = 0; i < playerList.size(); i++) {
                                playerList.get(i).rank = i + 1;
                            }

                            // Top 3 for winners list
                            for (int i = 0; i < Math.min(3, playerList.size()); i++) {
                                winnerList.add(playerList.get(i));
                            }

                            // Update adapters
                            playerAdapter.notifyDataSetChanged();
                            if (!winnerList.isEmpty()) {
                                winnersTitle.setVisibility(TextView.VISIBLE);
                                winnersRecycler.setVisibility(RecyclerView.VISIBLE);
                                winnersAdapter.notifyDataSetChanged();
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Parse error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Volley error", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }

}
