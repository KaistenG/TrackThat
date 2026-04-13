package com.example.trackthat.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackthat.R;
import com.example.trackthat.model.Habit;
import com.example.trackthat.repository.HabitRepository;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class DayBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_YEAR = "year";
    private static final String ARG_MONTH = "month";
    private static final String ARG_DAY = "day";

    private HabitRepository repository;
    private HabitToggleAdapter adapter;
    private DayPreviewView dayPreviewView;
    private List<Habit> allHabits = new ArrayList<>();
    private List<String> activeHabitIds = new ArrayList<>();
    private TextView buttonClearAll;
    private String dateString;

    public static DayBottomSheet newInstance(int year, int month, int day) {
        DayBottomSheet sheet = new DayBottomSheet();
        Bundle args = new Bundle();
        args.putInt(ARG_YEAR, year);
        args.putInt(ARG_MONTH, month);
        args.putInt(ARG_DAY, day);
        sheet.setArguments(args);
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_day, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new HabitRepository();

        int year = getArguments().getInt(ARG_YEAR);
        int month = getArguments().getInt(ARG_MONTH);
        int day = getArguments().getInt(ARG_DAY);

        dateString = String.format("%04d-%02d-%02d", year, month + 1, day);

        TextView dateLabel = view.findViewById(R.id.textViewSelectedDate);
        dateLabel.setText(day + "." + (month + 1) + "." + year);

        dayPreviewView = view.findViewById(R.id.dayPreviewView);

        buttonClearAll = view.findViewById(R.id.buttonClearAll);
        buttonClearAll.setOnClickListener(v -> {
            if (activeHabitIds.isEmpty()) return;
            for (String habitId : new ArrayList<>(activeHabitIds)) {
                repository.toggleEntry(habitId, dateString, new HabitRepository.OnSuccessListener() {
                    @Override
                    public void onSuccess() {
                        activeHabitIds.remove(habitId);
                        adapter.setActiveHabitIds(activeHabitIds);
                        updateClearButton();
                        updatePreview();
                    }

                    @Override
                    public void onFailure(String error) {}
                });
            }
        });

        adapter = new HabitToggleAdapter((habit, isActive) -> {
            repository.toggleEntry(habit.getId(), dateString, new HabitRepository.OnSuccessListener() {
                @Override
                public void onSuccess() {
                    if (isActive) {
                        if (!activeHabitIds.contains(habit.getId())) {
                            activeHabitIds.add(habit.getId());
                        }
                    } else {
                        activeHabitIds.remove(habit.getId());
                    }
                    adapter.setActiveHabitIds(activeHabitIds);
                    updatePreview();
                }

                @Override
                public void onFailure(String error) {}
            });
        });

        RecyclerView recycler = view.findViewById(R.id.recyclerViewHabits);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);

        loadData();
    }

    private void loadData() {
        String yearMonth = dateString.substring(0, 7);

        repository.getHabits(new HabitRepository.OnHabitsLoadedListener() {
            @Override
            public void onLoaded(List<Habit> habits) {
                allHabits = habits;
                adapter.setHabits(habits);

                repository.getEntriesForMonth(yearMonth, new HabitRepository.OnEntriesLoadedListener() {
                    @Override
                    public void onLoaded(List<com.example.trackthat.model.HabitEntry> entries) {
                        activeHabitIds.clear();
                        for (com.example.trackthat.model.HabitEntry entry : entries) {
                            if (entry.getDate().equals(dateString)) {
                                activeHabitIds.add(entry.getHabitId());
                            }
                        }
                        adapter.setActiveHabitIds(activeHabitIds);
                        updatePreview();
                    }

                    @Override
                    public void onFailure(String error) {}
                });
            }

            @Override
            public void onFailure(String error) {}
        });
    }

    private void updatePreview() {
        List<Habit> activeHabits = new ArrayList<>();
        for (Habit habit : allHabits) {
            if (activeHabitIds.contains(habit.getId())) {
                activeHabits.add(habit);
            }
        }
        dayPreviewView.setActiveHabits(activeHabits);
        updateClearButton();
    }
    private void updateClearButton() {
        if (activeHabitIds.isEmpty()) {
            buttonClearAll.setTextColor(0xFFCCCCCC);
        } else {
            buttonClearAll.setTextColor(0xFFE53935);
        }
    }
}