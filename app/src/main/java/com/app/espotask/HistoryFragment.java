package com.app.espotask;
import android.content.Context;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private static final String ARG_TYPE = "type";
    private int type;
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private List<HistoryItem> historyItems;

    private LinearLayout shimmerContainer;
    private int storedID;

    public HistoryFragment() {}

    public static HistoryFragment newInstance(int type) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(ARG_TYPE);
        }

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("EspoTaskApp", Context.MODE_PRIVATE);
        String userIdStr = sharedPreferences.getString("userID", "0");
        try {
            storedID = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            storedID = 0;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        shimmerContainer = view.findViewById(R.id.shimmerContainer);
        recyclerView = view.findViewById(R.id.recyclerViewHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        historyItems = new ArrayList<>();
        adapter = new HistoryAdapter(getContext(), historyItems);
        recyclerView.setAdapter(adapter);

        recyclerView.setVisibility(View.GONE);
        shimmerContainer.setVisibility(View.VISIBLE);

        populateHistoryList();

        return view;
    }

    private void populateHistoryList() {
        historyItems.clear();

        int userId = storedID;
        String url = getString(R.string.app_url) + "/get_user_account_statement.php?user_id=" + userId;

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray records = response.getJSONArray("records");
                        for (int i = 0; i < records.length(); i++) {
                            JSONObject obj = records.getJSONObject(i);

                            String note = obj.getString("note");
                            String createdDate = obj.getString("created_date");
                            String typeStr = obj.getString("currency_type");
                            String creditDebit = obj.getString("type");
                            double amount = obj.getDouble("amount");

                            int itemType = typeStr.equalsIgnoreCase("coins") ? HistoryItem.TYPE_COIN : HistoryItem.TYPE_TICKET;

                            if (itemType == type) {
                                historyItems.add(new HistoryItem(
                                        createdDate,
                                        note,
                                        creditDebit,
                                        amount,
                                        itemType
                                ));
                            }
                        }

                        adapter.notifyDataSetChanged();

                        // hide shimmer, show recycler
                        shimmerContainer.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Failed to parse data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                });

        queue.add(jsonObjectRequest);
    }
}
