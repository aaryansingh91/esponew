package com.app.espotask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LuckySliderAdapter extends RecyclerView.Adapter<LuckySliderAdapter.SliderViewHolder> {

    Context context;
    List<Integer> sliderImages;

    public LuckySliderAdapter(Context context, List<Integer> sliderImages) {
        this.context = context;
        this.sliderImages = sliderImages;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_slider_layout, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.imageView.setImageResource(sliderImages.get(position));
    }

    @Override
    public int getItemCount() {
        return sliderImages.size();
    }

    class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.sliderImageView);

        }
    }
}
