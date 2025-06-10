package com.aksofts.espotask;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NumberAdapter extends RecyclerView.Adapter<NumberAdapter.NumberViewHolder> {
    private final List<Integer> numbers;
    private final OnNumberClickListener listener;

    public interface OnNumberClickListener {
        void onNumberClick(int number);
    }

    public NumberAdapter(List<Integer> numbers, OnNumberClickListener listener) {
        this.numbers = numbers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_number, parent, false);
        return new NumberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NumberViewHolder holder, int position) {
        int number = numbers.get(position);
        holder.tvNumber.setText(String.valueOf(number));
        holder.itemView.setOnClickListener(v -> listener.onNumberClick(number));
    }

    @Override
    public int getItemCount() {
        return numbers.size();
    }

    static class NumberViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumber;

        NumberViewHolder(View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tv_number);
        }
    }
}