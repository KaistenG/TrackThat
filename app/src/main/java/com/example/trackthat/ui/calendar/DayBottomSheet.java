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
import java.util.Collections;
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
    private String currentMoodColor = null;
    private View lastSelectedMood = null;

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
        setupMoodSelector(view);

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
            // Sofort UI aktualisieren (optimistic update)
            if (isActive) {
                if (!activeHabitIds.contains(habit.getId())) {
                    activeHabitIds.add(habit.getId());
                }
            } else {
                activeHabitIds.remove(habit.getId());
            }
            adapter.setActiveHabitIds(activeHabitIds);
            updatePreview();

            // Firestore im Hintergrund aktualisieren
            repository.toggleEntry(habit.getId(), dateString, new HabitRepository.OnSuccessListener() {
                @Override
                public void onSuccess() {}

                @Override
                public void onFailure(String error) {
                    // Bei Fehler: UI zurücksetzen
                    if (isActive) {
                        activeHabitIds.remove(habit.getId());
                    } else {
                        if (!activeHabitIds.contains(habit.getId())) {
                            activeHabitIds.add(habit.getId());
                        }
                    }
                    adapter.setActiveHabitIds(activeHabitIds);
                    updatePreview();
                }
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
        for (Habit habit : adapter.getAllHabits()) {
            if (activeHabitIds.contains(habit.getId())) {
                activeHabits.add(habit);
            }
        }
        List<Habit> sortedAllHabits = new ArrayList<>(allHabits);
        Collections.sort(sortedAllHabits, (a, b) -> Integer.compare(a.getOrder(), b.getOrder()));
        dayPreviewView.setAllHabits(sortedAllHabits);
        dayPreviewView.setActiveHabits(activeHabits);
        dayPreviewView.setMoodColor(currentMoodColor);
        updateClearButton();
    }
    private void updateClearButton() {
        if (activeHabitIds.isEmpty()) {
            buttonClearAll.setTextColor(0xFFCCCCCC);
        } else {
            buttonClearAll.setTextColor(0xFFE53935);
        }
    }
    private void setupMoodSelector(View view) {
        int[] moodIds = {R.id.moodRed, R.id.moodOrange, R.id.moodYellow, R.id.moodLightGreen, R.id.moodGreen};
        String[] moodColors = {"#F44336", "#FF9800", "#FFEB3B", "#8BC34A", "#4CAF50"};

        // Aktuelle Stimmung laden
        repository.getDayMood(dateString, new HabitRepository.OnMoodLoadedListener() {
            @Override
            public void onLoaded(String color) {
                if (color != null) {
                    currentMoodColor = color;
                    for (int i = 0; i < moodColors.length; i++) {
                        if (moodColors[i].equals(color)) {
                            View moodView = view.findViewById(moodIds[i]);
                            moodView.setScaleX(1.3f);
                            moodView.setScaleY(1.3f);
                            lastSelectedMood = moodView;
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(String error) {}
        });

        // Click-Listener für jeden Mood-Button
        for (int i = 0; i < moodIds.length; i++) {
            final String moodColor = moodColors[i];
            View moodView = view.findViewById(moodIds[i]);
            moodView.setOnClickListener(v -> {
                if (lastSelectedMood != null) {
                    lastSelectedMood.setScaleX(1.0f);
                    lastSelectedMood.setScaleY(1.0f);
                }
                if (lastSelectedMood == v) {
                    currentMoodColor = null;
                    lastSelectedMood = null;
                    dayPreviewView.setMoodColor(null);
                } else {
                    v.setScaleX(1.3f);
                    v.setScaleY(1.3f);
                    lastSelectedMood = v;
                    currentMoodColor = moodColor;
                    dayPreviewView.setMoodColor(moodColor);
                }

                repository.setDayMood(dateString, currentMoodColor != null ? currentMoodColor : "",
                        new HabitRepository.OnSuccessListener() {
                            @Override
                            public void onSuccess() {}

                            @Override
                            public void onFailure(String error) {}
                        });
            });
        }
    }
}