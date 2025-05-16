package com.aksofts.mgamerapp;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class WithdrawAdapter extends RecyclerView.Adapter<WithdrawAdapter.WithdrawViewHolder> {

    private List<WithdrawItem> withdrawItems;
    private Context context;

    public WithdrawAdapter(Context context, List<WithdrawItem> withdrawItems) {
        this.context = context;
        this.withdrawItems = withdrawItems;
    }

    @NonNull
    @Override
    public WithdrawViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_withdraw, parent, false);
        return new WithdrawViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WithdrawViewHolder holder, int position) {
        WithdrawItem currentItem = withdrawItems.get(position);
        holder.nameTextView.setText(currentItem.getName());
        holder.rewardAmountTextView.setText(currentItem.getReward_amount());
        holder.coinsAmountTextView.setText(currentItem.getCoins_amount());

        if (currentItem.getType().equals("diamonds")) {
            holder.iconImageView.setImageResource(R.drawable.ic_ticket_24);
        } else if (currentItem.getType().equals("inr")) {
            holder.iconImageView.setImageResource(R.drawable.ic_rupee_24);
        }
    }

    @Override
    public int getItemCount() {
        return withdrawItems.size();
    }

    public static class WithdrawViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView rewardAmountTextView;
        TextView coinsAmountTextView;
        ImageView iconImageView;

        public WithdrawViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            rewardAmountTextView = itemView.findViewById(R.id.rewardAmountTextView);
            coinsAmountTextView = itemView.findViewById(R.id.coinsAmountTextView);
            iconImageView = itemView.findViewById(R.id.iconImageView);
        }
    }
}