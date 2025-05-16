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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class WithdrawListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WithdrawAdapter adapter;
    private List<WithdrawItem> withdrawItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_list); // Create this layout

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        withdrawItemList = new ArrayList<>();
        adapter = new WithdrawAdapter(this, withdrawItemList);
        recyclerView.setAdapter(adapter);

        // Get the selected category from the intent
        String selectedCategory = getIntent().getStringExtra("category");

        // Retrieve JSON from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("pgamerapp", MODE_PRIVATE);
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
}