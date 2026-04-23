package com.example.trackthat.ui.habits;

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

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.ViewHolder> {

    public interface OnHabitClickListener {
        void onHabitClick(Habit habit);
    }

    private List<Habit> habits = new ArrayList<>();
    private OnHabitClickListener listener;

    public HabitAdapter(OnHabitClickListener listener) {
        this.listener = listener;
    }

    public void setHabits(List<Habit> habits) {
        this.habits = habits;
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
        Habit habit = habits.get(position);

        holder.textViewName.setText(habit.getName());

        switch (habit.getVisualType()) {
            case "VERTICAL":
                holder.textViewVisualType.setText("Vertikaler Streifen");
                break;
            case "HORIZONTAL":
                holder.textViewVisualType.setText("Horizontaler Streifen");
                break;
        }

        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setColor(habit.getColor());
        holder.colorIndicator.setBackground(circle);

        holder.itemView.setOnClickListener(v -> listener.onHabitClick(habit));
    }

    @Override
    public int getItemCount() {
        return habits.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewVisualType;
        View colorIndicator;

        ViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewHabitName);
            textViewVisualType = itemView.findViewById(R.id.textViewVisualType);
            colorIndicator = itemView.findViewById(R.id.colorIndicator);
        }
    }
}