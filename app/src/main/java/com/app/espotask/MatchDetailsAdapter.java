package com.app.espotask;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MatchDetailsAdapter extends FragmentStateAdapter {

    private final int tournamentId;

    public MatchDetailsAdapter(@NonNull FragmentActivity fa, int tournamentId) {
        super(fa);
        this.tournamentId = tournamentId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new DescriptionFragment(); // No change if it doesn’t need tournamentId
            case 1:
                JoinedMemberFragment fragment = new JoinedMemberFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("match_id", tournamentId);
                fragment.setArguments(bundle);
                return fragment;
            default:
                return new Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
