package com.aksofts.mgamerapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class WithdrawListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WithdrawAdapter adapter;
    private List<WithdrawItem> withdrawItemList;
    TextView coinCount ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_list); // Create this layout

        coinCount = findViewById(R.id.coinCount);

// Fetch userId from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("pgamerapp", MODE_PRIVATE);
        String userIdStr = sharedPreferences.getString("userID", "0");

        try {
            int userId = Integer.parseInt(userIdStr);
            fetchUserData(userId);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show();
        }


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        withdrawItemList = new ArrayList<>();
        adapter = new WithdrawAdapter(this, withdrawItemList);
        recyclerView.setAdapter(adapter);
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> onBackPressed());
        // Get the selected category from the intent
        String selectedCategory = getIntent().getStringExtra("category");

        // Retrieve JSON from SharedPreferences
        String withdrawDataJson = sharedPreferences.getString("withdraw_data_setting", "");

        // Parse the JSON data
        Gson gson = new Gson();
        Type type = new TypeToken<List<WithdrawItem>>() {}.getType();
        List<WithdrawItem> allWithdrawItems = gson.fromJson(withdrawDataJson, type);

        // Filter data based on the selected category
        if (allWithdrawItems != null && selectedCategory != null) {
            for (WithdrawItem item : allWithdrawItems) {
                if (item.getCategory().equals(selectedCategory)) {
                    withdrawItemList.add(item);
                }
            }
            adapter.notifyDataSetChanged();

        } else {
            Toast.makeText(this, "No data found for the selected category.", Toast.LENGTH_SHORT).show();
        }
    }
    private void fetchUserData(int userId) {
        String url = getString(R.string.app_url) + "/amsit-adm/get_user_info_api.php?id=" + userId;

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("success")) {
                            JSONObject user = jsonObject.getJSONObject("user");
                            int coins = user.getInt("coins");
                            coinCount.setText(String.valueOf(coins));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                });

        queue.add(stringRequest);
    }

}