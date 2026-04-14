package com.example.trackthat.ui.stats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackthat.R;
import com.example.trackthat.model.Habit;
import com.example.trackthat.model.HabitEntry;
import com.example.trackthat.repository.HabitRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Calendar;

public class StreaksFragment extends Fragment {

    private HabitRepository repository;
    private StatsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_streaks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new HabitRepository();
        adapter = new StatsAdapter();

        RecyclerView recycler = view.findViewById(R.id.recyclerViewStats);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);

        ((android.widget.TextView) view.findViewById(R.id.textViewTitle))
                .setText("Streaks");

        loadStats();
    }

    private void loadStats() {
        repository.getHabits(new HabitRepository.OnHabitsLoadedListener() {
            @Override
            public void onLoaded(List<Habit> habits) {
                repository.getAllEntries(new HabitRepository.OnEntriesLoadedListener() {
                    @Override
                    public void onLoaded(List<HabitEntry> entries) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                        // Einträge pro Habit gruppieren
                        Map<String, List<Date>> datesPerHabit = new HashMap<>();
                        for (HabitEntry entry : entries) {
                            try {
                                Date date = sdf.parse(entry.getDate());
                                if (!datesPerHabit.containsKey(entry.getHabitId())) {
                                    datesPerHabit.put(entry.getHabitId(), new ArrayList<>());
                                }
                                datesPerHabit.get(entry.getHabitId()).add(date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        List<StatsAdapter.StatItem> items = new ArrayList<>();
                        for (Habit habit : habits) {
                            if (!habit.isStreakable()) continue;

                            List<Date> dates = datesPerHabit.get(habit.getId());
                            if (dates == null || dates.isEmpty()) {
                                items.add(new StatsAdapter.StatItem(habit, "🔥 0 Tage | Max: 0"));
                                continue;
                            }

                            Collections.sort(dates);

                            int currentStreak = calculateCurrentStreak(dates);
                            int longestStreak = calculateLongestStreak(dates);

                            items.add(new StatsAdapter.StatItem(habit,
                                    "🔥 " + currentStreak + " Tage | Max: " + longestStreak));
                        }
                        adapter.setItems(items);
                    }

                    @Override
                    public void onFailure(String error) {}
                });
            }

            @Override
            public void onFailure(String error) {}
        });
    }

    private int calculateCurrentStreak(List<Date> sortedDates) {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        int streak = 0;
        Calendar check = (Calendar) today.clone();

        for (int i = sortedDates.size() - 1; i >= 0; i--) {
            Calendar entryDay = Calendar.getInstance();
            entryDay.setTime(sortedDates.get(i));
            entryDay.set(Calendar.HOUR_OF_DAY, 0);
            entryDay.set(Calendar.MINUTE, 0);
            entryDay.set(Calendar.SECOND, 0);
            entryDay.set(Calendar.MILLISECOND, 0);

            if (entryDay.equals(check)) {
                streak++;
                check.add(Calendar.DAY_OF_MONTH, -1);
            } else if (entryDay.before(check)) {
                break;
            }
        }
        return streak;
    }

    private int calculateLongestStreak(List<Date> sortedDates) {
        int longest = 1;
        int current = 1;

        for (int i = 1; i < sortedDates.size(); i++) {
            Calendar prev = Calendar.getInstance();
            prev.setTime(sortedDates.get(i - 1));
            Calendar curr = Calendar.getInstance();
            curr.setTime(sortedDates.get(i));

            prev.add(Calendar.DAY_OF_MONTH, 1);
            if (isSameDay(prev, curr)) {
                current++;
                longest = Math.max(longest, current);
            } else {
                current = 1;
            }
        }
        return longest;
    }

    private boolean isSameDay(Calendar a, Calendar b) {
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR)
                && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR);
    }
}