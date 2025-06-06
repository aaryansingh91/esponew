package com.aksofts.mgamerapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyRewardActivity extends AppCompatActivity {

    RecyclerView rewardRecyclerView;
    List<RewardModel> rewardList = new ArrayList<>();
    RewardAdapter adapter;
    String userId;
    TextView coinCount, ticketCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reward);

        TextView coinCount = findViewById(R.id.coinCount);
        TextView ticketCount = findViewById(R.id.ticketCount);
        rewardRecyclerView = findViewById(R.id.rewardRecyclerView);
        rewardRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sp = getSharedPreferences("pgamerapp", MODE_PRIVATE);
        userId = sp.getString("userID", "0");
        int coins = sp.getInt("coins", 0);
        int tickets = sp.getInt("tickets", 0);

        adapter = new RewardAdapter(this, rewardList);
        rewardRecyclerView.setAdapter(adapter);

        // Back button
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> onBackPressed());
        coinCount.setText(String.valueOf(coins));
        ticketCount.setText(String.valueOf(tickets));

        fetchRewards();
    }

    private void fetchRewards() {
        String url = getString(R.string.app_url) + "/amsit-adm/get_withdrawals_history.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getString("status").equals("success")) {
                            JSONArray array = json.getJSONArray("withdrawals");

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                RewardModel model = new RewardModel();
                                model.amount = obj.getString("amount");
                                model.coins_used = obj.getString("coins_used");
                                model.withdraw_type = obj.getString("withdraw_type");
                                model.status = obj.getString("status");
                                model.created_at = obj.getString("created_at");
                                model.updated_at = obj.getString("updated_at");
                                rewardList.add(model);
                            }

                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Network Error", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("user_id", userId);
                return map;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
