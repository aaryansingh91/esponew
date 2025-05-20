package com.aksofts.mgamerapp;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.aksofts.mgamerapp.fragments.OngoingFragment;
import com.aksofts.mgamerapp.fragments.ResultsFragment;
import com.aksofts.mgamerapp.fragments.UpcomingFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.w3c.dom.Text;

public class TournamentListActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private TextView game_name ;
    private ViewPager2 viewPager;
    private final String[] tabTitles = new String[]{"ONGOING", "UPCOMING", "RESULTS"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_list); // your layout XML filename

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            finish();  // This closes the activity and goes back to previous screen
        });
        // Get game_id and game_name from intent
        String gameId = getIntent().getStringExtra("game_id");
        String gameName = getIntent().getStringExtra("game_name");

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        TextView gameNameTextView = findViewById(R.id.game_name);

        viewPager.setUserInputEnabled(true); // enable swiping

        // Pass both gameId and gameName to the adapter
        viewPager.setAdapter(new MatchesPagerAdapter(this, gameId, gameName));

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabTitles[position])
        ).attach();
        // / Set Upcoming tab as default (position 1)
            viewPager.setCurrentItem(1, false);

        if (gameName != null) {
            gameNameTextView.setText(gameName);
        } else {
            gameNameTextView.setText("Game Name Not Available");
        }
    }

    private static class MatchesPagerAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {

        private final String gameId;
        private final String gameName;

        // Update constructor to accept gameName
        public MatchesPagerAdapter(@NonNull AppCompatActivity activity, String gameId, String gameName) {
            super(activity);
            this.gameId = gameId;
            this.gameName = gameName;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Bundle bundle = new Bundle();
            bundle.putString("game_id", gameId);
            bundle.putString("game_name", gameName);  // Pass gameName in bundle

            Fragment fragment;
            switch (position) {
                case 0:
                    fragment = new OngoingFragment();
                    break;
                case 1:
                    fragment = new UpcomingFragment();
                    break;
                case 2:
                    fragment = new ResultsFragment();
                    break;
                default:
                    fragment = new UpcomingFragment();
            }
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}
