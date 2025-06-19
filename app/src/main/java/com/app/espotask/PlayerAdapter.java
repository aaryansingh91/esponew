package com.app.espotask;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.ViewHolder> {

    private final List<PlayerModel> players;

    public PlayerAdapter(List<PlayerModel> players) {
        this.players = players;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.player_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayerModel player = players.get(position);

        // Show rank
        holder.playerRank.setText("#" + player.rank);

        holder.playerName.setText(player.ingame_name);
        holder.playerKills.setText(String.valueOf(player.kills));
        holder.playerWinning.setText(String.valueOf(player.winning));
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView playerRank, playerName, playerKills, playerWinning;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playerRank = itemView.findViewById(R.id.playerRank);
            playerName = itemView.findViewById(R.id.playerName);
            playerKills = itemView.findViewById(R.id.playerKills);
            playerWinning = itemView.findViewById(R.id.playerWinning);
        }
    }
}
