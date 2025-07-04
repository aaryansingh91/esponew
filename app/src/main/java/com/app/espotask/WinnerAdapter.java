package com.app.espotask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WinnerAdapter extends RecyclerView.Adapter<WinnerAdapter.WinnerViewHolder> {

    Context context;
    ArrayList<WinnerModel> list;

    public WinnerAdapter(Context context, ArrayList<WinnerModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public WinnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lucky_number_winner, parent, false);
        return new WinnerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WinnerViewHolder holder, int position) {
        WinnerModel model = list.get(position);
        holder.winnerName.setText(model.getUsername());
        holder.winnerAmount.setText(String.valueOf(model.getCoins()));
        holder.winnerDate.setText(model.getDate());
        // Optional: holder.winnerImage.setImageResource(...) if dynamic
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class WinnerViewHolder extends RecyclerView.ViewHolder {
        TextView winnerName, winnerAmount, winnerDate;
        ImageView winnerImage;

        public WinnerViewHolder(@NonNull View itemView) {
            super(itemView);
            winnerName = itemView.findViewById(R.id.winnerName);
            winnerAmount = itemView.findViewById(R.id.winnerAmount);
            winnerDate = itemView.findViewById(R.id.winnerDate);
            winnerImage = itemView.findViewById(R.id.winnerImage);
        }
    }
}
