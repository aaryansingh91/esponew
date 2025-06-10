package com.aksofts.espotask;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MatchDetailsAdapter extends FragmentStateAdapter {

    public MatchDetailsAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new DescriptionFragment();
            case 1:
                return new JoinedMemberFragment();
            default:
                return new DescriptionFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}