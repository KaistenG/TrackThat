package com.example.trackthat.ui.habits;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackthat.R;
import com.example.trackthat.model.Group;
import com.example.trackthat.model.Habit;

import java.util.ArrayList;
import java.util.List;

public class HabitGroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_HABIT = 1;

    public interface OnHabitClickListener {
        void onHabitClick(Habit habit);
    }

    // Listenelement – entweder Gruppe oder Habit
    private static class ListItem {
        Group group;
        Habit habit;

        ListItem(Group group) { this.group = group; }
        ListItem(Habit habit) { this.habit = habit; }

        boolean isHeader() { return group != null; }
    }

    private List<ListItem> items = new ArrayList<>();
    private OnHabitClickListener listener;

    public HabitGroupAdapter(OnHabitClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<Group> groups, List<Habit> habits) {
        items.clear();

        // Habits ohne Gruppe zuerst
        List<Habit> ungrouped = new ArrayList<>();
        for (Habit habit : habits) {
            if (habit.getGroupId() == null) ungrouped.add(habit);
        }
        if (!ungrouped.isEmpty()) {
            items.add(new ListItem(new Group(null, "Ohne Gruppe", -1)));
            for (Habit habit : ungrouped) items.add(new ListItem(habit));
        }

        // Habits nach Gruppe
        for (Group group : groups) {
            List<Habit> groupHabits = new ArrayList<>();
            for (Habit habit : habits) {
                if (group.getId().equals(habit.getGroupId())) groupHabits.add(habit);
            }
            if (!groupHabits.isEmpty()) {
                items.add(new ListItem(group));
                for (Habit habit : groupHabits) items.add(new ListItem(habit));
            }
        }

        notifyDataSetChanged();
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
                    .inflate(R.layout.item_habit, parent, false);
            return new HabitViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).textViewGroupName
                    .setText(items.get(position).group.getName());
        } else {
            Habit habit = items.get(position).habit;
            HabitViewHolder habitHolder = (HabitViewHolder) holder;

            habitHolder.textViewName.setText(habit.getName());

            switch (habit.getVisualType()) {
                case "VERTICAL": habitHolder.textViewVisualType.setText("Vertikaler Streifen"); break;
                case "HORIZONTAL": habitHolder.textViewVisualType.setText("Horizontaler Streifen"); break;
                case "BORDER": habitHolder.textViewVisualType.setText("Umrandung"); break;
            }

            GradientDrawable circle = new GradientDrawable();
            circle.setShape(GradientDrawable.OVAL);
            circle.setColor(habit.getColor());
            habitHolder.colorIndicator.setBackground(circle);

            habitHolder.itemView.setOnClickListener(v -> listener.onHabitClick(habit));
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
        TextView textViewVisualType;
        View colorIndicator;
        HabitViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewHabitName);
            textViewVisualType = itemView.findViewById(R.id.textViewVisualType);
            colorIndicator = itemView.findViewById(R.id.colorIndicator);
        }
    }
}