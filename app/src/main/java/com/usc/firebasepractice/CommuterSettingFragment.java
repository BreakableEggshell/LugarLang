package com.usc.firebasepractice;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class CommuterSettingFragment extends Fragment {

    public CommuterSettingFragment() {
        super(R.layout.fragment_commuter_setting);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout account = view.findViewById(R.id.layoutAccount);

        account.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), Login_Activity.class);
            startActivity(intent);
        });
    }
}