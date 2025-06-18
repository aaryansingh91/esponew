package com.app.espotask.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.espotask.R;
import com.app.espotask.TournamentMatchesAdaptor;
import com.app.espotask.TournamentMatchesItem;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UpcomingFragment extends Fragment {
    private RecyclerView recyclerView;
    private TournamentMatchesAdaptor adapter;
    private List<TournamentMatchesItem> matches = new ArrayList<>();
    private LinearLayout loadingAnim;
    // 🔥 Declare gameId as a class-level variable
    private String gameId;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // ✅ Retrieve game_id and store it in the class variable
        if (getArguments() != null) {
            gameId = getArguments().getString("game_id");
        }
        View view = inflater.inflate(R.layout.fragment_upcoming, container, false);

        recyclerView = view.findViewById(R.id.recycler_upcoming);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadingAnim = view.findViewById(R.id.loadinganim);

        // ✅ Get user ID from SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("EspoTaskApp", getContext().MODE_PRIVATE);
        String userIdStr = prefs.getString("userID", null);

        // Optional: Check if userId exists
        if (userIdStr != null) {
            fetchMatches(userIdStr);
        } else {
            Toast.makeText(requireContext(), "User not logged in!", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private void fetchMatches(String userId) {
        loadingAnim.setVisibility(View.VISIBLE);
        // ✅ Use class-level gameId here
        String url = getString(R.string.app_url) + "/all-tournaments-matches.php?id=" + gameId + "&user_id=" + userId;

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    matches.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);

                            int matchStatus = obj.getInt("match_status");

                            if (matchStatus == 1) {
                                TournamentMatchesItem item = new TournamentMatchesItem(
                                        obj.getInt("m_id"),
                                        obj.getString("match_banner"),
                                        obj.getString("match_name"),
                                        obj.getString("match_time"),
                                        obj.getInt("entry_fee_coins"),
                                        obj.getInt("entry_fee_tickets"),
                                        obj.getString("entry_type"),
                                        obj.getInt("win_prize"),
                                        obj.getInt("per_kill"),
                                        obj.getString("type"),
                                        obj.optString("match_type", "TPP"),
                                        obj.getString("MAP"),
                                        obj.getString("no_of_player"),
                                        obj.optInt("filled_positions", 0),
                                        obj.optInt("no_of_player", 0),
                                        matchStatus,
                                        obj.optString("match_url"),
                                        obj.optString("user_joined", "0") // 🔥 Add this
                                );
                                matches.add(item);
                            }

                        }
                        if (adapter == null) {
                            adapter = new TournamentMatchesAdaptor(matches);
                            recyclerView.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        loadingAnim.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Parsing error", Toast.LENGTH_SHORT).show();
                        loadingAnim.setVisibility(View.GONE);
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    loadingAnim.setVisibility(View.GONE);
                });

        queue.add(request);
    }
}
