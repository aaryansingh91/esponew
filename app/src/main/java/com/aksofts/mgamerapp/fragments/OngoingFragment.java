package com.aksofts.mgamerapp.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aksofts.mgamerapp.R;
import com.aksofts.mgamerapp.TournamentMatchesAdaptor;
import com.aksofts.mgamerapp.TournamentMatchesItem;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OngoingFragment extends Fragment {
    private RecyclerView recyclerView;
    private TournamentMatchesAdaptor adapter;
    private List<TournamentMatchesItem> matches = new ArrayList<>();
    private LinearLayout loadingAnim;
    private TextView tvNoMatches;

    private String gameId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getArguments() != null) {
            gameId = getArguments().getString("game_id");
        }

        View view = inflater.inflate(R.layout.fragment_ongoing, container, false);

        recyclerView = view.findViewById(R.id.recycler_ongoing);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadingAnim = view.findViewById(R.id.loadinganim);
        tvNoMatches = view.findViewById(R.id.tv_no_matches);

        fetchMatches();

        return view;
    }

    private void fetchMatches() {
        loadingAnim.setVisibility(View.VISIBLE);

        String url = getString(R.string.app_url) + "/app-apis/tournaments/all-tournaments-matches.php?id=" + gameId;
        Toast.makeText(requireContext(), "Game ID: " + gameId, Toast.LENGTH_SHORT).show();

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    matches.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);

                            int matchStatus = obj.getInt("match_status");

                            if (matchStatus == 3) {
                                TournamentMatchesItem item = new TournamentMatchesItem(
                                        obj.getInt("m_id"),
                                        obj.getString("match_banner"),
                                        obj.getString("match_name"),
                                        obj.getString("match_time"),
                                        obj.getInt("entry_fee"),
                                        obj.getInt("win_prize"),
                                        obj.getInt("per_kill"),
                                        obj.getString("type"),
                                        obj.optString("match_type", "TPP"),
                                        obj.getString("MAP"),
                                        obj.getString("no_of_player"),
                                        obj.optInt("filled_positions", 0), // new field filled_positions
                                        obj.optInt("no_of_player", 0) // no_of_player as int
                                );
                                matches.add(item);
                            }
                        }

                        if (matches.isEmpty()) {
                            recyclerView.setVisibility(View.GONE);
                            tvNoMatches.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            tvNoMatches.setVisibility(View.GONE);

                            if (adapter == null) {
                                adapter = new TournamentMatchesAdaptor(matches);
                                recyclerView.setAdapter(adapter);
                            } else {
                                adapter.notifyDataSetChanged();
                            }
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
