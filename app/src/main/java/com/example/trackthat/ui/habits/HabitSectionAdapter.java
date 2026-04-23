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
import java.util.Collections;
import java.util.List;

public class HabitSectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_HABIT = 1;

    public interface OnHabitActionListener {
        void onEdit(Habit habit);
        void onDelete(Habit habit);
        void onMoveUp(int position);
        void onMoveDown(int position);
    }

    private static class ListItem {
        String header;
        Habit habit;

        ListItem(String header) { this.header = header; }
        ListItem(Habit habit) { this.habit = habit; }

        boolean isHeader() { return header != null; }
    }

    private List<ListItem> items = new ArrayList<>();
    private OnHabitActionListener listener;
    private int editModePosition = -1;

    public HabitSectionAdapter(OnHabitActionListener listener) {
        this.listener = listener;
    }

    public void setHabits(List<Habit> habits) {
        items.clear();
        editModePosition = -1;

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

    public List<Habit> getHabitsInOrder() {
        List<Habit> habits = new ArrayList<>();
        for (ListItem item : items) {
            if (!item.isHeader()) habits.add(item.habit);
        }
        return habits;
    }

    public void swapHabits(int pos1, int pos2) {
        ListItem item1 = items.get(pos1);
        ListItem item2 = items.get(pos2);
        items.set(pos1, item2);
        items.set(pos2, item1);
        editModePosition = pos2; // Edit-Mode mitverschieben
        notifyItemMoved(pos1, pos2);
        notifyItemChanged(pos1);
        notifyItemChanged(pos2);
    }

    public boolean isHeader(int position) {
        return items.get(position).isHeader();
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
            ((HeaderViewHolder) holder).textViewGroupName.setText(items.get(position).header);
        } else {
            Habit habit = items.get(position).habit;
            HabitViewHolder h = (HabitViewHolder) holder;
            boolean isEditMode = editModePosition == position;

            h.textViewName.setText(habit.getName());
            h.textViewVisualType.setText(habit.getVisualType().equals("VERTICAL") ? "Weekly" : "Daily");

            GradientDrawable circle = new GradientDrawable();
            circle.setShape(GradientDrawable.OVAL);
            circle.setColor(habit.getColor());
            h.colorIndicator.setBackground(circle);

            h.habitEditView.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
            h.habitEditView.setOnClickListener(v -> {});

            h.itemView.setOnLongClickListener(v -> {
                int prev = editModePosition;
                editModePosition = (editModePosition == position) ? -1 : position;
                if (prev != -1) notifyItemChanged(prev);
                notifyItemChanged(position);
                return true;
            });

            h.itemView.setOnClickListener(v -> {
                if (editModePosition != -1) {
                    int prev = editModePosition;
                    editModePosition = -1;
                    notifyItemChanged(prev);
                }
            });

            h.buttonEdit.setOnClickListener(v -> {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                listener.onEdit(habit);
            });

            h.buttonDelete.setOnClickListener(v -> {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                listener.onDelete(habit);
            });

            h.buttonMoveUp.setOnClickListener(v -> {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                listener.onMoveUp(h.getAdapterPosition());
            });

            h.buttonMoveDown.setOnClickListener(v -> {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                listener.onMoveDown(h.getAdapterPosition());
            });
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
        View habitEditView;
        TextView buttonMoveUp;
        TextView buttonMoveDown;
        TextView buttonEdit;
        TextView buttonDelete;

        HabitViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewHabitName);
            textViewVisualType = itemView.findViewById(R.id.textViewVisualType);
            colorIndicator = itemView.findViewById(R.id.colorIndicator);
            habitEditView = itemView.findViewById(R.id.habitEditView);
            buttonMoveUp = itemView.findViewById(R.id.buttonMoveUp);
            buttonMoveDown = itemView.findViewById(R.id.buttonMoveDown);
            buttonEdit = itemView.findViewById(R.id.buttonEditHabit);
            buttonDelete = itemView.findViewById(R.id.buttonDeleteHabit);
        }
    }
}