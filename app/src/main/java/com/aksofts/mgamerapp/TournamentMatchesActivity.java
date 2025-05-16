package com.aksofts.mgamerapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TournamentMatchesActivity extends AppCompatActivity {

    private RecyclerView matchesRecyclerView;
    private TournamentMatchesAdaptor matchesAdapter;
    private List<TournamentMatchesItem> matchesItemList;
    private LinearLayout loadinganim;

    private TextView ongoingTab;
    private TextView upcomingTab;
    private TextView resultsTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_matches);

        // Initialize views
        loadinganim = findViewById(R.id.loadinganim);
        matchesRecyclerView = findViewById(R.id.matches_recycler_view);
        matchesRecyclerView.setVisibility(View.GONE);
        matchesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ongoingTab = findViewById(R.id.ongoing_tab);
        upcomingTab = findViewById(R.id.upcoming_tab);
        resultsTab = findViewById(R.id.results_tab);

        // Initialize matches list
        matchesItemList = new ArrayList<>();

        // Set up tab click listeners (optional, for filtering)
        ongoingTab.setOnClickListener(v -> {
            // TODO: Implement filtering for ongoing matches
        });

        upcomingTab.setOnClickListener(v -> {
            // TODO: Implement filtering for upcoming matches
        });

        resultsTab.setOnClickListener(v -> {
            // TODO: Implement filtering for results
        });

        // Handle back button
        View header = findViewById(R.id.header);
        if (header != null) {
            header.setOnClickListener(v -> onBackPressed());
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Fetch tournament matches
        fetchTournamentMatches();
    }

    private void fetchTournamentMatches() {
        String apiUrl = getResources().getString(R.string.app_url) + "/app-apis/tournaments/all-tournaments-matches.php?id=1";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, apiUrl, null,
                response -> {
                    try {
                        Log.d("TournamentMatchesActivity", "API Response: " + response.toString());
                        matchesItemList.clear(); // Clear existing data

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);

                            // Parse JSON fields, including m_id
                            int id = obj.getInt("m_id"); // Map to m_id
                            String matchName = obj.getString("match_name");
                            String matchTime = obj.getString("match_time");
                            int entryFee = obj.getInt("entry_fee");
                            int winPrize = obj.getInt("win_prize");
                            int perKill = obj.getInt("per_kill");
                            String type = obj.getString("type");
                            String map = obj.getString("MAP");
                            String banner = obj.getString("match_banner");
                            String noOfPlayer = obj.getString("no_of_player");
                            String version = obj.optString("match_type", "TPP"); // Default to TPP

                            // Create TournamentMatchesItem with id
                            TournamentMatchesItem item = new TournamentMatchesItem(
                                    id, banner, matchName, matchTime, entryFee, winPrize, perKill,
                                    type, version, map, noOfPlayer
                            );
                            matchesItemList.add(item);
                        }

                        // Set up adapter
                        if (matchesAdapter == null) {
                            matchesAdapter = new TournamentMatchesAdaptor(matchesItemList);
                            matchesRecyclerView.setAdapter(matchesAdapter);
                        } else {
                            matchesAdapter.notifyDataSetChanged();
                        }

                        // Update UI
                        loadinganim.setVisibility(View.GONE);
                        matchesRecyclerView.setVisibility(View.VISIBLE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(TournamentMatchesActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(TournamentMatchesActivity.this, "Failed to fetch tournament list", Toast.LENGTH_SHORT).show();
                    loadinganim.setVisibility(View.GONE);
                });

        queue.add(request);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}