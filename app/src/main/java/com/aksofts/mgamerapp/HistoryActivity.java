package com.aksofts.mgamerapp;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
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
    }
}
