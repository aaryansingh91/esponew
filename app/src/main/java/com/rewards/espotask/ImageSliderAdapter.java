package com.rewards.espotask;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.SliderViewHolder> {

    private List<SliderModel> sliderList;
    private Context context;

    public ImageSliderAdapter(Context context, List<SliderModel> sliderList) {
        this.context = context;
        this.sliderList = sliderList;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_layout, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        SliderModel slider = sliderList.get(position);

        Log.d("SLIDER_DEBUG", "Loading image: " + slider.getImageUrl());

        // Load image from URL
        new DownloadImageTask(holder.imageView, slider.getImageUrl()).execute(slider.getImageUrl());

        // Set click listener if URL exists
        if (slider.hasClickUrl()) {
            holder.itemView.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(slider.getClickUrl()));
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, "Unable to open link", Toast.LENGTH_SHORT).show();
                    Log.e("SLIDER_CLICK", "Error: " + e.getMessage());
                }
            });
        } else {
            holder.itemView.setOnClickListener(null);
            holder.itemView.setClickable(false);
        }
    }

    @Override
    public int getItemCount() {
        return sliderList != null ? sliderList.size() : 0;
    }

    public void updateSliders(List<SliderModel> newSliders) {
        this.sliderList = newSliders;
        notifyDataSetChanged();
    }

    public static class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    // Image Downloader with better error handling
    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;
        private String imageUrl;

        public DownloadImageTask(ImageView imageView, String imageUrl) {
            this.imageView = imageView;
            this.imageUrl = imageUrl;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // You can set a placeholder here if needed
            Log.d("SLIDER_LOAD", "Starting to load: " + imageUrl);
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap bitmap = null;

            try {
                Log.d("SLIDER_LOAD", "Attempting to download from: " + urlDisplay);

                URL url = new URL(urlDisplay);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setConnectTimeout(10000); // 10 seconds timeout
                connection.setReadTimeout(10000);
                connection.connect();

                int responseCode = connection.getResponseCode();
                Log.d("SLIDER_LOAD", "Response code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream input = connection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(input);
                    input.close();

                    if (bitmap != null) {
                        Log.d("SLIDER_LOAD", "Successfully loaded image: " + urlDisplay);
                    } else {
                        Log.e("SLIDER_LOAD", "Bitmap is null after decode");
                    }
                } else {
                    Log.e("SLIDER_LOAD", "HTTP error code: " + responseCode);
                }

                connection.disconnect();
            } catch (Exception e) {
                Log.e("SLIDER_LOAD", "Error loading image: " + e.getMessage());
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null && imageView != null) {
                imageView.setImageBitmap(result);
                Log.d("SLIDER_LOAD", "Image set to ImageView successfully");
            } else {
                Log.e("SLIDER_LOAD", "Failed to set image. Bitmap: " + (result != null) + ", ImageView: " + (imageView != null));
                // Set a placeholder or error image if needed
                if (imageView != null) {
                    // imageView.setImageResource(R.drawable.placeholder_image);
                }
            }
        }
    }
}