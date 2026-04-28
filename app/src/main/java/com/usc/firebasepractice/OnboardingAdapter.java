package com.usc.firebasepractice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.ViewHolder> {

    private List<String[]> data;

    public OnboardingAdapter(List<String[]> data) {
        this.data = data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle;
        Button btnStart;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            subtitle = view.findViewById(R.id.subtitle);
            btnStart = view.findViewById(R.id.btnStart);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_onboarding, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(data.get(position)[0]);
        holder.subtitle.setText(data.get(position)[1]);

        // Show button only on last page
        if (position == data.size() - 1) {
            holder.btnStart.setVisibility(View.VISIBLE);
        } else {
            holder.btnStart.setVisibility(View.GONE);
        }

        holder.btnStart.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, Landing_Activity.class);
            context.startActivity(intent);

            ((Activity) context).finish(); // <-- HERE
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}