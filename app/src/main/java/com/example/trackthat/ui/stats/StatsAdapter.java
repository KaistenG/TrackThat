package com.example.trackthat.ui.stats;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackthat.R;
import com.example.trackthat.model.Habit;

import java.util.ArrayList;
import java.util.List;

public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.ViewHolder> {

    public static class StatItem {
        public Habit habit;
        public String value;

        public StatItem(Habit habit, String value) {
            this.habit = habit;
            this.value = value;
        }
    }

    private List<StatItem> items = new ArrayList<>();

    public void setItems(List<StatItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_habit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StatItem item = items.get(position);

        holder.textViewName.setText(item.habit.getName());
        holder.textViewValue.setText(item.value);

        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setColor(item.habit.getColor());
        holder.colorIndicator.setBackground(circle);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewValue;
        View colorIndicator;

        ViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewHabitName);
            textViewValue = itemView.findViewById(R.id.textViewVisualType);
            colorIndicator = itemView.findViewById(R.id.colorIndicator);
        }
    }
}