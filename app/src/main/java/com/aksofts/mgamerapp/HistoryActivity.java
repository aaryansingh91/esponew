package com.aksofts.mgamerapp;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.android.volley.RequestQueue;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private HistoryPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        // Create fragments
        HistoryFragment coinsFragment = HistoryFragment.newInstance(HistoryItem.TYPE_COIN);
        HistoryFragment ticketsFragment = HistoryFragment.newInstance(HistoryItem.TYPE_TICKET);

        // Add fragments to a list
        List<androidx.fragment.app.Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(coinsFragment);
        fragmentList.add(ticketsFragment);

        // Set up the ViewPager and PagerAdapter
        pagerAdapter = new HistoryPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        // Set tab titles
        tabLayout.getTabAt(0).setText("Coins");
        tabLayout.getTabAt(1).setText("Tickets");


        // 🪙 Fetch coin count and show in TextView
        TextView coinTextView = findViewById(R.id.coinCount);
        SharedPreferences sharedPreferences = getSharedPreferences("pgamerapp", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userID", "0");
        fetchUserData(Integer.parseInt(userId), coinTextView);
    }
    private void fetchUserData(int userId, TextView coinTextView) {
        String url = getString(R.string.app_url) + "/amsit-adm/get_user_info_api.php?id=" + userId;

        RequestQueue queue = com.android.volley.toolbox.Volley.newRequestQueue(this);

        com.android.volley.toolbox.StringRequest stringRequest = new com.android.volley.toolbox.StringRequest(
                com.android.volley.Request.Method.GET,
                url,
                response -> {
                    try {
                        org.json.JSONObject jsonObject = new org.json.JSONObject(response);
                        if (jsonObject.getString("status").equals("success")) {
                            int coins = jsonObject.getJSONObject("user").getInt("coins");
                            coinTextView.setText(String.valueOf(coins)); // ✅ Correct

                        }
                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                        android.widget.Toast.makeText(this, "Error parsing data", android.widget.Toast.LENGTH_SHORT).show();
                    }
                },
                error -> android.widget.Toast.makeText(this, "Failed to fetch user data", android.widget.Toast.LENGTH_SHORT).show()
        );

        queue.add(stringRequest);
    }

}
