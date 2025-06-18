package com.app.espotask;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class TournamentMatchesAdaptor extends RecyclerView.Adapter<TournamentMatchesAdaptor.ViewHolder> {

    private List<TournamentMatchesItem> matchesList;
    private static final int VIEW_TYPE_UPCOMING = 0;
    private static final int VIEW_TYPE_ONGOING = 1;
    private static final int VIEW_TYPE_RESULT = 2;


    public TournamentMatchesAdaptor(List<TournamentMatchesItem> matchesList) {
        this.matchesList = matchesList;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_UPCOMING) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tournament_upcoming, parent, false);
        } else if (viewType == VIEW_TYPE_ONGOING) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tournament_ongoing, parent, false);
        } else { // result
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tournament_results, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        int status = matchesList.get(position).getMatchStatus();
        if (status == 1) return VIEW_TYPE_UPCOMING;
        else if (status == 3) return VIEW_TYPE_ONGOING;
        else if (status == 2) return VIEW_TYPE_RESULT;
        else return VIEW_TYPE_UPCOMING; // fallback
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TournamentMatchesItem currentItem = matchesList.get(position);

        if (holder.watchButton != null) {
            String url = currentItem.getMatchUrl();
            holder.watchButton.setOnClickListener(v -> {
                if (url != null && !url.isEmpty()) {
                    Context context = holder.itemView.getContext();
                    Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url));
                    context.startActivity(intent);
                } else {
                    Toast.makeText(holder.itemView.getContext(), "No video URL available", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if ("1".equals(currentItem.getUserJoined())) {
            if (holder.joinButton != null) {
                holder.joinButton.setText("Joined");
                holder.joinButton.setEnabled(false);
                holder.joinButton.setAlpha(0.5f);
            }

            if (holder.join_button_results != null) {
                holder.join_button_results.setText("Joined");
                holder.join_button_results.setEnabled(false);
                holder.join_button_results.setAlpha(0.5f);
            }
        } else {
            if (holder.joinButton != null) {
                holder.joinButton.setText("Join Now");
                holder.joinButton.setEnabled(true);
                holder.joinButton.setAlpha(1f);
            }

            if (holder.join_button_results != null) {
                holder.join_button_results.setText("Not Joined");
                holder.join_button_results.setEnabled(false);
                holder.join_button_results.setAlpha(1f);
            }
        }




        // Bind data to views
        holder.matchTitleTextView.setText(currentItem.getTitle());
        holder.matchTimeTextView.setText(currentItem.getTime());
        holder.prizePoolTextView.setText(String.valueOf(currentItem.getPrizePool()));
        holder.perKillTextView.setText(String.valueOf(currentItem.getPerKill()));
//        holder.entryFeeTextView.setText(String.valueOf(currentItem.getEntryFee()));
        holder.typeTextView.setText(currentItem.getType());
        holder.versionTextView.setText(currentItem.getVersion());
        holder.mapTextView.setText(currentItem.getMap());
        holder.slotsTextView.setText(currentItem.getSlots());
        int filled = currentItem.getFilledPositions();
        int total = currentItem.getNoOfPlayer();

        // Load image with Glide, with fallback to imageResource
        if (currentItem.getImageUrl() != null && !currentItem.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(currentItem.getImageUrl())
                    .placeholder(R.drawable.ic_freefire_banner)
                    .error(R.drawable.ic_freefire_banner)
                    .into(holder.matchImageView);
        } else {
            holder.matchImageView.setImageResource(currentItem.getImageResource());
        }
        if (total > 0) {
            int progressPercent = (int) ((filled / (float) total) * 100);
            holder.slotProgressBar.setMax(100);
            holder.slotProgressBar.setProgress(progressPercent);
            holder.slotProgressText.setText(filled + "/" + total + " Slots Filled");
        } else {
            // fallback if total is zero
            holder.slotProgressBar.setProgress(0);
            holder.slotProgressText.setText("0/0 Slots Filled");
        }
        String entryType = currentItem.getEntryType();
        int entryFeeDisplay;

        if ("coin".equals(entryType)) {
            holder.coinIcon.setVisibility(View.VISIBLE);
            holder.coinText.setVisibility(View.VISIBLE);

            holder.ticketIcon.setVisibility(View.GONE);
            holder.ticketText.setVisibility(View.GONE);
            holder.slashText.setVisibility(View.GONE);

            holder.coinText.setText(String.valueOf(currentItem.getEntryFeeCoins()));
        } else if ("tickets".equals(entryType)) {
            holder.coinIcon.setVisibility(View.GONE);
            holder.coinText.setVisibility(View.GONE);

            holder.ticketIcon.setVisibility(View.VISIBLE);
            holder.ticketText.setVisibility(View.VISIBLE);
            holder.slashText.setVisibility(View.GONE);

            holder.ticketText.setText(String.valueOf(currentItem.getEntryFeeTickets()));
        } else if ("any".equals(entryType)) {
            holder.coinIcon.setVisibility(View.VISIBLE);
            holder.coinText.setVisibility(View.VISIBLE);

            holder.ticketIcon.setVisibility(View.VISIBLE);
            holder.ticketText.setVisibility(View.VISIBLE);
            holder.slashText.setVisibility(View.VISIBLE);

            holder.coinText.setText(String.valueOf(currentItem.getEntryFeeCoins()));
            holder.ticketText.setText(String.valueOf(currentItem.getEntryFeeTickets()));
        } else {
            // fallback: hide all
            holder.coinIcon.setVisibility(View.GONE);
            holder.coinText.setVisibility(View.GONE);
            holder.ticketIcon.setVisibility(View.GONE);
            holder.ticketText.setVisibility(View.GONE);
            holder.slashText.setVisibility(View.GONE);
        }



        // Handle join button click
        holder.joinButton.setOnClickListener(v -> {
            Log.d("TournamentMatchesAdaptor", "Clicked ID: " + currentItem.getId());
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, TournamentMatchDetail.class);
            intent.putExtra("TOURNAMENT_ID", currentItem.getId()); // Pass m_id only
            context.startActivity(intent);
        });
        holder.match_item_click.setOnClickListener(v -> {
            Log.d("TournamentMatchesAdaptor", "Clicked ID: " + currentItem.getId());
            Context context = holder.itemView.getContext();
            Intent intent;

            if (currentItem.getMatchStatus() == 2) {
                // If match status is "result"
                intent = new Intent(context, TournamentResult.class);
            } else {
                // For "upcoming" or "ongoing"
                intent = new Intent(context, TournamentMatchDetail.class);
            }

            intent.putExtra("TOURNAMENT_ID", currentItem.getId());
            context.startActivity(intent);
        });

    }


    @Override
    public int getItemCount() {
        return matchesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView matchImageView, coinIcon, ticketIcon;
        TextView matchTitleTextView, matchTimeTextView, prizePoolTextView, perKillTextView,
                entryFeeTextView, typeTextView, versionTextView, mapTextView, slotsTextView, slotProgressText, coinText, ticketText, slashText;
        Button joinButton, watchButton, join_button_results;
        ProgressBar slotProgressBar;
        RelativeLayout match_item_click;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            matchImageView = itemView.findViewById(R.id.match_image_view);
            matchTitleTextView = itemView.findViewById(R.id.match_title_text_view);
            matchTimeTextView = itemView.findViewById(R.id.match_time_text_view);
            prizePoolTextView = itemView.findViewById(R.id.prize_pool_text_view);
            perKillTextView = itemView.findViewById(R.id.per_kill_text_view);
//            entryFeeTextView = itemView.findViewById(R.id.entry_fee_text_view);
            typeTextView = itemView.findViewById(R.id.type_text_view);
            versionTextView = itemView.findViewById(R.id.version_text_view);
            mapTextView = itemView.findViewById(R.id.map_text_view);
            slotsTextView = itemView.findViewById(R.id.slots_text_view);
            joinButton = itemView.findViewById(R.id.join_button);
            join_button_results = itemView.findViewById(R.id.join_button_results);
            slotProgressBar = itemView.findViewById(R.id.slot_progress_bar);
            slotProgressText = itemView.findViewById(R.id.slot_progress_text);
            match_item_click = itemView.findViewById(R.id.match_item_click);
            coinIcon = itemView.findViewById(R.id.coinIcon);
            ticketIcon = itemView.findViewById(R.id.ticketIcon);
            coinText = itemView.findViewById(R.id.coinText);
            ticketText = itemView.findViewById(R.id.ticketText);
            slashText = itemView.findViewById(R.id.slashText);
            watchButton = itemView.findViewById(R.id.watch_button); // initialize

        }
    }
}