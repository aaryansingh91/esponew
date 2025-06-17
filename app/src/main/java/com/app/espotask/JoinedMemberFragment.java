package com.app.espotask;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JoinedMemberFragment extends Fragment {

    private static final String TAG = "JoinedMemberFragment";
    private RecyclerView recyclerView;
    private MemberAdapter adapter;
    private List<String> memberList = new ArrayList<>();
    int matchId = -1; // declare as class-level variable
    private TextView noMembersText;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_joined_member, container, false);

        if (getArguments() != null) {
            matchId = getArguments().getInt("match_id", -1);
        }

        recyclerView = view.findViewById(R.id.joined_member_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MemberAdapter(memberList);
        recyclerView.setAdapter(adapter);

        noMembersText = view.findViewById(R.id.no_members_text); // ✅ Init

        fetchJoinedMembers();
        return view;
    }

    private void fetchJoinedMembers() {
        String url = getString(R.string.app_url) + "/get_joined_members.php?match_id=" + matchId;

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            JSONArray members = response.getJSONArray("joined_members");
                            memberList.clear();

                            for (int i = 0; i < members.length(); i++) {
                                memberList.add(members.getString(i));
                            }

                            adapter.notifyDataSetChanged();

                            // ✅ Show message if 0 members
                            if (memberList.isEmpty()) {
                                noMembersText.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            } else {
                                noMembersText.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }

                        } else {
                            Toast.makeText(getContext(), "Failed to load members", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e(TAG, "Volley error: " + error.getMessage());
                    Toast.makeText(getContext(), "Error loading members", Toast.LENGTH_SHORT).show();
                });

        queue.add(jsonRequest);
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
        holder.memberName.setText((position + 1) + ". " + members.get(position));
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