package com.usc.firebasepractice;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    LinearLayout dotsLayout;
    List<String[]> onboardingData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPager);
        dotsLayout = findViewById(R.id.dotsIndicator);

        onboardingData = new ArrayList<>();
        onboardingData.add(new String[]{"Welcome", "To Lugar Lang!"});
        onboardingData.add(new String[]{"Find Places", "Discover nearby locations"});
        onboardingData.add(new String[]{"Get Started", "Let’s begin!"});

        OnboardingAdapter adapter = new OnboardingAdapter(onboardingData);
        viewPager.setAdapter(adapter);

        setupDots(onboardingData.size());

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                setActiveDot(position);
            }
        });
    }

    private void setupDots(int count) {
        TextView[] dots = new TextView[count];

        for (int i = 0; i < count; i++) {
            dots[i] = new TextView(this);
            dots[i].setText("•");
            dots[i].setTextSize(35);
            dots[i].setTextColor(Color.GRAY);
            dotsLayout.addView(dots[i]);
        }

        setActiveDot(0);
    }

    private void setActiveDot(int position) {
        int childCount = dotsLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            TextView dot = (TextView) dotsLayout.getChildAt(i);
            dot.setTextColor(i == position ? Color.WHITE : Color.GRAY);
        }
    }
}