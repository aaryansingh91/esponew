package com.app.espotask;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class WithdrawSelectionAdapter extends RecyclerView.Adapter<WithdrawSelectionAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(WithdrawSelectionItem item);
    }

    private List<WithdrawSelectionItem> itemList;
    private OnItemClickListener listener;

    public WithdrawSelectionAdapter(List<WithdrawSelectionItem> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.withdraw_selection_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WithdrawSelectionItem item = itemList.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.itemTitle);
            description = itemView.findViewById(R.id.itemDescription);
            icon = itemView.findViewById(R.id.itemImage);
        }

        public void bind(final WithdrawSelectionItem item, final OnItemClickListener listener) {
            title.setText(item.getTitle());
            description.setText(item.getDescription());
            Glide.with(itemView.getContext()).load(item.getImgIcon()).into(icon);

            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}
