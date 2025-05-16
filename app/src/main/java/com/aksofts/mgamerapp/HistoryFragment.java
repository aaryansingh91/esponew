package com.aksofts.mgamerapp;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private static final String ARG_TYPE = "type";
    private int type;
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private List<HistoryItem> historyItems;

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
        // Clear any existing items
        historyItems.clear();

        // Add dummy data for demonstration.  Replace with your actual data source.
        if (type == HistoryItem.TYPE_COIN) {
            historyItems.add(new HistoryItem("1st May", "Coin Reward", "CR123", 10, HistoryItem.TYPE_COIN));
            historyItems.add(new HistoryItem("1st May", "Coin Bonus", "CB456", 5, HistoryItem.TYPE_COIN));
            historyItems.add(new HistoryItem("30th April", "Coin Gift", "CG789", 20, HistoryItem.TYPE_COIN));
            // Add more coin-related history items
        } else if (type == HistoryItem.TYPE_TICKET) {
            historyItems.add(new HistoryItem("1st May", "Video Task", "1L3PEBBCIO", 1, HistoryItem.TYPE_TICKET));
            historyItems.add(new HistoryItem("1st May", "Daily Bonus", "kCW82ZTTGc", 2, HistoryItem.TYPE_TICKET));
            historyItems.add(new HistoryItem("30th April", "Ticket Prize", "TP246", 3, HistoryItem.TYPE_TICKET));
            // Add more ticket-related history items
        }
        adapter.notifyDataSetChanged();
    }
}