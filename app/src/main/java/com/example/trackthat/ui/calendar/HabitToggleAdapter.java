package com.example.trackthat.ui.calendar;

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
import java.util.Collections;
import java.util.List;

public class HabitToggleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_HABIT = 1;

    public interface OnHabitToggleListener {
        void onToggle(Habit habit, boolean isActive);
    }

    private static class ListItem {
        String header;
        Habit habit;

        ListItem(String header) { this.header = header; }
        ListItem(Habit habit) { this.habit = habit; }

        boolean isHeader() { return header != null; }
    }

    private List<ListItem> items = new ArrayList<>();
    private List<String> activeHabitIds = new ArrayList<>();
    private OnHabitToggleListener listener;

    public HabitToggleAdapter(OnHabitToggleListener listener) {
        this.listener = listener;
    }

    public void setHabits(List<Habit> habits) {
        items.clear();

        List<Habit> weeklies = new ArrayList<>();
        List<Habit> dailies = new ArrayList<>();

        for (Habit habit : habits) {
            if (habit.getVisualType().equals("VERTICAL")) weeklies.add(habit);
            else dailies.add(habit);
        }
        Collections.sort(weeklies, (a, b) -> Integer.compare(a.getOrder(), b.getOrder()));
        Collections.sort(dailies, (a, b) -> Integer.compare(a.getOrder(), b.getOrder()));

        if (!weeklies.isEmpty()) {
            items.add(new ListItem("Weekly"));
            for (Habit h : weeklies) items.add(new ListItem(h));
        }

        if (!dailies.isEmpty()) {
            items.add(new ListItem("Daily"));
            for (Habit h : dailies) items.add(new ListItem(h));
        }

        notifyDataSetChanged();
    }

    public void setActiveHabitIds(List<String> ids) {
        this.activeHabitIds = ids;
        notifyDataSetChanged();
    }

    public List<Habit> getAllHabits() {
        List<Habit> habits = new ArrayList<>();
        for (ListItem item : items) {
            if (!item.isHeader()) habits.add(item.habit);
        }
        Collections.sort(habits, (a, b) -> Integer.compare(a.getOrder(), b.getOrder()));
        return habits;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).isHeader() ? TYPE_HEADER : TYPE_HABIT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_group_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_habit_toggle, parent, false);
            return new HabitViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).textViewGroupName
                    .setText(items.get(position).header);
        } else {
            Habit habit = items.get(position).habit;
            HabitViewHolder habitHolder = (HabitViewHolder) holder;
            boolean isActive = activeHabitIds.contains(habit.getId());

            habitHolder.textViewName.setText(habit.getName());

            GradientDrawable circle = new GradientDrawable();
            circle.setShape(GradientDrawable.OVAL);
            circle.setColor(habit.getColor());
            habitHolder.colorIndicator.setBackground(circle);

            habitHolder.itemView.setAlpha(isActive ? 1.0f : 0.4f);
            habitHolder.itemView.setOnClickListener(v ->
                    listener.onToggle(habit, !isActive));
        }
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView textViewGroupName;
        HeaderViewHolder(View itemView) {
            super(itemView);
            textViewGroupName = itemView.findViewById(R.id.textViewGroupName);
        }
    }

    static class HabitViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        View colorIndicator;
        HabitViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewHabitName);
            colorIndicator = itemView.findViewById(R.id.colorIndicator);
        }
    }
}