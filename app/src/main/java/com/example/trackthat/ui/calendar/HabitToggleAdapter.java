package com.example.trackthat.ui.calendar;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackthat.R;
import com.example.trackthat.model.Habit;

import java.util.ArrayList;
import java.util.List;

public class HabitToggleAdapter extends RecyclerView.Adapter<HabitToggleAdapter.ViewHolder> {

    public interface OnHabitToggleListener {
        void onToggle(Habit habit, boolean isActive);
    }

    private List<Habit> habits = new ArrayList<>();
    private List<String> activeHabitIds = new ArrayList<>();
    private OnHabitToggleListener listener;

    public HabitToggleAdapter(OnHabitToggleListener listener) {
        this.listener = listener;
    }

    public void setHabits(List<Habit> habits) {
        this.habits = habits;
        notifyDataSetChanged();
    }

    public void setActiveHabitIds(List<String> ids) {
        this.activeHabitIds = ids;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_habit_toggle, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Habit habit = habits.get(position);
        boolean isActive = activeHabitIds.contains(habit.getId());

        holder.textViewName.setText(habit.getName());

        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setColor(habit.getColor());
        holder.colorIndicator.setBackground(circle);

        holder.itemView.setAlpha(isActive ? 1.0f : 0.4f);

        holder.itemView.setOnClickListener(v -> {
            listener.onToggle(habit, !isActive);
        });
    }

    @Override
    public int getItemCount() {
        return habits.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        ImageView colorIndicator;

        ViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewHabitName);
            colorIndicator = itemView.findViewById(R.id.colorIndicator);
        }
    }
}