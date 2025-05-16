package com.aksofts.mgamerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.card.MaterialCardView;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private MaterialCardView btnLogin;
    private TextView btnSkip, btnRepeat;
    private WormDotsIndicator dotsIndicator;
    private int[] layouts = {
            R.layout.onboarding_screen1,
            R.layout.onboarding_screen2,
            R.layout.onboarding_screen3
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        btnLogin = findViewById(R.id.btnLogin);
        btnSkip = findViewById(R.id.btnSkip);
        btnRepeat = findViewById(R.id.btnRepeat);
        dotsIndicator = findViewById(R.id.dotsIndicator);

        OnboardingAdapter adapter = new OnboardingAdapter(layouts);
        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(new DepthPageTransformer());
        dotsIndicator.attachTo(viewPager);

        // Show "Go to Login" button on last screen
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                btnLogin.setVisibility(position == layouts.length - 1 ? View.VISIBLE : View.INVISIBLE);
            }
        });

        btnSkip.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, OnboardingDisclosureActivity.class)));
        btnRepeat.setOnClickListener(v -> viewPager.setCurrentItem(0));
        btnLogin.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, OnboardingDisclosureActivity.class)));
    }
}