package com.app.espotask;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private Context context;
    private List<HistoryItem> historyItems;

    public HistoryAdapter(Context context, List<HistoryItem> historyItems) {
        this.context = context;
        this.historyItems = historyItems;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_coin, parent, false);
        return new HistoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryItem currentItem = historyItems.get(position);
        holder.textDescription.setText(currentItem.getDescription());
        holder.textTransactionId.setText(currentItem.getCreditDebit().toUpperCase());
        holder.textAmount.setText(String.valueOf(currentItem.getAmount()));
        holder.textDate.setText(currentItem.getDate()); // Make sure your data has a date

        if (currentItem.getType() == HistoryItem.TYPE_TICKET) {
            holder.imageIcon.setImageResource(R.drawable.ic_ticket_24); // Replace with your ticket icon
        } else if (currentItem.getType() == HistoryItem.TYPE_COIN) {
            holder.imageIcon.setImageResource(R.drawable.ic_coin_24); // Replace with your coin icon
        }
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        public TextView textDescription;
        public TextView textTransactionId;
        public TextView textAmount;
        public ImageView imageIcon;
        public TextView textDate;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textDescription = itemView.findViewById(R.id.textDescription);
            textTransactionId = itemView.findViewById(R.id.textTransactionId);
            textAmount = itemView.findViewById(R.id.textAmount);
            imageIcon = itemView.findViewById(R.id.imageIcon);
            textDate = itemView.findViewById(R.id.textDate);
        }
    }
}
