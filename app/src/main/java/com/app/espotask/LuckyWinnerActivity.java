package com.app.espotask;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class LuckyWinnerActivity extends AppCompatActivity {

    RecyclerView winnerRecyclerView;
    ViewPager2 imageSlider;
    List<WinnerModel> winnerList = new ArrayList<>();
    List<Integer> sliderImages = Arrays.asList(
            R.drawable.slider1,
            R.drawable.slider2,
            R.drawable.slider3
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lucky_number_winner_activity);

        winnerRecyclerView = findViewById(R.id.winnerRecyclerView);
        imageSlider = findViewById(R.id.imageSlider);

        LuckySliderAdapter sliderAdapter = new LuckySliderAdapter(this, sliderImages);
        imageSlider.setAdapter(sliderAdapter);

        winnerList.add(new WinnerModel("Aman Verma", "₹1000", "01 July 2025", "https://i.imgur.com/xyz.png"));
        winnerList.add(new WinnerModel("Neha Singh", "₹750", "30 June 2025", "https://i.imgur.com/abc.png"));

        WinnerAdapter winnerAdapter = new WinnerAdapter(this, winnerList);
        winnerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        winnerRecyclerView.setAdapter(winnerAdapter);
    }
}
