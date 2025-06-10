package com.app.espotask;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class JoinedMemberFragment extends Fragment {

    private static final String TAG = "JoinedMemberFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "JoinedMemberFragment onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "Creating JoinedMemberFragment view");
        View view = inflater.inflate(R.layout.fragment_joined_member, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.joined_member_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new MemberAdapter(getPlaceholderMembers()));
        Log.d(TAG, "JoinedMemberFragment view created with RecyclerView");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "JoinedMemberFragment onViewCreated");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "JoinedMemberFragment onResume");
    }

    private List<String> getPlaceholderMembers() {
        // Placeholder data; replace with actual member data from API
        Log.d(TAG, "Loading placeholder members");
        return Arrays.asList("Player 1", "Player 2", "Player 3");
    }
}

class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    private final List<String> members;

    public MemberAdapter(List<String> members) {
        this.members = members;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        holder.memberName.setText(members.get(position));
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {
        final TextView memberName;

        MemberViewHolder(View itemView) {
            super(itemView);
            memberName = itemView.findViewById(R.id.member_name);
        }
    }
}