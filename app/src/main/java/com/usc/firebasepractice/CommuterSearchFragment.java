package com.usc.firebasepractice;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class CommuterSearchFragment extends Fragment {

    public CommuterSearchFragment() {
        super(R.layout.fragment_commuter_search);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView backBtn = view.findViewById(R.id.btnBack);

        backBtn.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new CommuterHomeFragment())
                    .commit();

            // Optional: sync bottom nav selection if needed
        });
    }
}