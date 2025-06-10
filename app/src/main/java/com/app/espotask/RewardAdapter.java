package com.aksofts.espotask;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.ViewHolder> {

    private List<RewardModel> rewardList;
    private Context context;


    public RewardAdapter(Context context, List<RewardModel> rewardList) {
        this.context = context;
        this.rewardList = rewardList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reward, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RewardModel model = rewardList.get(position);

        holder.tvAmount.setText("₹" + model.amount);
        holder.tvCoins.setText("Coins Used: " + model.coins_used);
        holder.tvType.setText("Type: " + model.withdraw_type);
        holder.tvStatus.setText(model.status);
        holder.tvCreated.setText("Created: " + model.created_at);
        holder.tvUpdated.setText("Updated: " + model.updated_at);

        String status = model.status;; // "pending", "approved", or "rejected"

        holder.tvStatus.setText(status.substring(0, 1).toUpperCase() + status.substring(1)); // Capitalize

        switch (status.toLowerCase()) {
            case "approved":
                holder.tvStatus.setBackgroundResource(R.drawable.status_approved_bg);
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.newgreen)); // or your success color
                break;

            case "rejected":
                holder.tvStatus.setBackgroundResource(R.drawable.status_rejected_bg);
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.newred)); // or your error color
                break;

            default: // pending
                holder.tvStatus.setBackgroundResource(R.drawable.status_pending_bg);
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.neworange)); // or default
                break;
        }

    }

    @Override
    public int getItemCount() {
        return rewardList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAmount, tvCoins, tvType, tvStatus, tvCreated, tvUpdated;

        ViewHolder(View view) {
            super(view);
            tvAmount = view.findViewById(R.id.tvAmount);
            tvCoins = view.findViewById(R.id.tvCoins);
            tvType = view.findViewById(R.id.tvType);
            tvStatus = view.findViewById(R.id.tvStatus);
            tvCreated = view.findViewById(R.id.tvCreated);
            tvUpdated = view.findViewById(R.id.tvUpdated);
        }
    }
}
