package com.aksofts.mgamerapp;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    int storedID ;

    public HistoryFragment() {
        // Required empty public constructor

    }

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("pgamerapp", Context.MODE_PRIVATE);

        String userIdStr = sharedPreferences.getString("userID", "0");
        try {
            storedID = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            storedID = 0;
        }


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        historyItems = new ArrayList<>();
        adapter = new HistoryAdapter(getContext(), historyItems);
        recyclerView.setAdapter(adapter);

        // Populate the RecyclerView with data based on the type (Coins or Tickets)
        populateHistoryList();
        return view;
    }

    private void populateHistoryList() {
        historyItems.clear();

        int userId = storedID; // You can dynamically fetch this from SharedPreferences or login session
        String url = getString(R.string.app_url) +"/get_user_account_statement.php?user_id=" + userId;

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray records = response.getJSONArray("records");
                        for (int i = 0; i < records.length(); i++) {
                            JSONObject obj = records.getJSONObject(i);

                            String note = obj.getString("note");
                            String createdDate = obj.getString("created_date");
                            String typeStr = obj.getString("currency_type"); // coins or tickets
                            String creditDebit = obj.getString("type"); // <-- Add this
                            double amount = obj.getDouble("amount");

                            int itemType = typeStr.equalsIgnoreCase("coins") ? HistoryItem.TYPE_COIN : HistoryItem.TYPE_TICKET;

                            if (itemType == type) { // Only add items that match tab type
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                });

        queue.add(jsonObjectRequest);
    }

}