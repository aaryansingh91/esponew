package com.app.espotask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class WinnerAdapter extends RecyclerView.Adapter<WinnerAdapter.WinnerViewHolder> {

    Context context;
    List<WinnerModel> winnerList;

    public WinnerAdapter(Context context, List<WinnerModel> winnerList) {
        this.context = context;
        this.winnerList = winnerList;
    }

    @NonNull
    @Override
    public WinnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_winner_layout, parent, false);
        return new WinnerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WinnerViewHolder holder, int position) {
        WinnerModel winner = winnerList.get(position);
        holder.name.setText(winner.getName());
        holder.amount.setText(winner.getAmount());
        holder.date.setText(winner.getDate());

        Glide.with(context).load(winner.getImageUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return winnerList.size();
    }

    class WinnerViewHolder extends RecyclerView.ViewHolder {
        TextView name, amount, date;
        ImageView imageView;

        public WinnerViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.winnerName);
            amount = itemView.findViewById(R.id.winnerAmount);
            date = itemView.findViewById(R.id.winnerDate);
            imageView = itemView.findViewById(R.id.winnerImage);
        }
    }
}
