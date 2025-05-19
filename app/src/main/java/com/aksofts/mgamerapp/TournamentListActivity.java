package com.aksofts.mgamerapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.aksofts.mgamerapp.fragments.OngoingFragment;
import com.aksofts.mgamerapp.fragments.ResultsFragment;
import com.aksofts.mgamerapp.fragments.UpcomingFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TournamentListActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private final String[] tabTitles = new String[]{"ONGOING", "UPCOMING", "RESULTS"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_list); // your layout XML filename

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        viewPager.setUserInputEnabled(true); // important for enabling swiping

        viewPager.setAdapter(new MatchesPagerAdapter(this));

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabTitles[position])
        ).attach();
    }

    private static class MatchesPagerAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {

        public MatchesPagerAdapter(@NonNull AppCompatActivity activity) {
            super(activity);
        }

        @NonNull
        @Override
        public androidx.fragment.app.Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new OngoingFragment();
                case 1:
                    return new UpcomingFragment();
                case 2:
                    return new ResultsFragment();
                default:
                    return new UpcomingFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3; // 3 tabs
        }
    }
}