package com.app.espotask;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
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

    private TextView matchTitle, matchTime, winningPrize, perKill, entryFee, winnersTitle, entryCoins, entryTickets;
    private ImageView bannerImage, coinIcon, ticketIcon;
    private RecyclerView playersRecycler, winnersRecycler;

    private PlayerAdapter playerAdapter, winnersAdapter;
    private final List<PlayerModel> playerList = new ArrayList<>();
    private final List<PlayerModel> winnerList = new ArrayList<>();
    public String BASE_URL;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_result);

        int matchId = getIntent().getIntExtra("TOURNAMENT_ID", -1);
        BASE_URL = getString(R.string.app_url) + "/get_match_result.php?match_id=";

        matchTitle = findViewById(R.id.matchTitle);
        matchTime = findViewById(R.id.matchTime);
        winningPrize = findViewById(R.id.winningPrize);
        perKill = findViewById(R.id.perKill);
        coinIcon = findViewById(R.id.coinIcon);
        entryCoins = findViewById(R.id.entryCoins);
        ticketIcon = findViewById(R.id.ticketIcon);
        entryTickets = findViewById(R.id.entryTickets);


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

                            // Bind match info
                            matchTitle.setText(match.match_name + " - Match #" + match.m_id);
                            matchTime.setText("Organised on " + match.match_time);
                            winningPrize.setText("Winning Prize : 🪙" + match.win_prize);
                            perKill.setText("Per Kill : 🪙" + match.per_kill);

                            // Handle entry fee display based on type
                            String entryType = matchObj.optString("entry_type", "coins");
                            double coinCount = match.entry_fee_coins;
                            int ticketCount = matchObj.optInt("entry_fee_tickets", 0);

                            // Reset visibility
                            coinIcon.setVisibility(View.GONE);
                            entryCoins.setVisibility(View.GONE);
                            ticketIcon.setVisibility(View.GONE);
                            entryTickets.setVisibility(View.GONE);

                            if ("coin".equals(entryType)) {
                                coinIcon.setVisibility(View.VISIBLE);
                                entryCoins.setVisibility(View.VISIBLE);
                                entryCoins.setText(String.format("%.0f", coinCount));
                            } else if ("tickets".equals(entryType)) {
                                ticketIcon.setVisibility(View.VISIBLE);
                                entryTickets.setVisibility(View.VISIBLE);
                                entryTickets.setText(String.valueOf(ticketCount));
                            } else if ("any".equals(entryType)) {
                                coinIcon.setVisibility(View.VISIBLE);
                                entryCoins.setVisibility(View.VISIBLE);
                                ticketIcon.setVisibility(View.VISIBLE);
                                entryTickets.setVisibility(View.VISIBLE);
                                entryCoins.setText(String.format("%.0f", coinCount) + "  |");
                                entryTickets.setText(String.valueOf(ticketCount));
                            }

                            // Load banner
                            String bannerUrl = match.match_banner.startsWith("http")
                                    ? match.match_banner
                                    : "https://espotask.com/match_banners/" + match.match_banner;
                            Glide.with(this)
                                    .load(bannerUrl)
                                    .into(bannerImage);

                            // Parse players
                            JSONArray pa = response.getJSONArray("players");
                            playerList.clear();
                            winnerList.clear();

                            for (int i = 0; i < pa.length(); i++) {
                                PlayerModel p = new Gson().fromJson(pa.getJSONObject(i).toString(), PlayerModel.class);
                                playerList.add(p);
                            }

                            // Sort & assign ranks
                            playerList.sort((p1, p2) -> Double.compare(p2.winning, p1.winning));
                            for (int i = 0; i < playerList.size(); i++) {
                                playerList.get(i).rank = i + 1;
                            }

                            // Top 3 winners
                            for (int i = 0; i < Math.min(3, playerList.size()); i++) {
                                winnerList.add(playerList.get(i));
                            }

                            // Update UI
                            playerAdapter.notifyDataSetChanged();
                            if (!winnerList.isEmpty()) {
                                winnersTitle.setVisibility(View.VISIBLE);
                                winnersRecycler.setVisibility(View.VISIBLE);
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
