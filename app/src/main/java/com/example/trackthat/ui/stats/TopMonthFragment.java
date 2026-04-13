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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TopMonthFragment extends Fragment {

    private HabitRepository repository;
    private StatsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_top_month, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new HabitRepository();
        adapter = new StatsAdapter();

        RecyclerView recycler = view.findViewById(R.id.recyclerViewStats);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);

        view.findViewById(R.id.buttonBack).setOnClickListener(v ->
                getParentFragmentManager().popBackStack());

        ((android.widget.TextView) view.findViewById(R.id.textViewTitle))
                .setText("Topmonat");

        loadStats();
    }

    private void loadStats() {
        repository.getHabits(new HabitRepository.OnHabitsLoadedListener() {
            @Override
            public void onLoaded(List<Habit> habits) {
                repository.getAllEntries(new HabitRepository.OnEntriesLoadedListener() {
                    @Override
                    public void onLoaded(List<HabitEntry> entries) {
                        Calendar today = Calendar.getInstance();
                        int currentDay = today.get(Calendar.DAY_OF_MONTH);
                        int currentMonth = today.get(Calendar.MONTH) + 1;
                        int currentYear = today.get(Calendar.YEAR);
                        String currentYearMonth = String.format("%04d-%02d", currentYear, currentMonth);

                        // Einträge pro Monat pro Habit zählen
                        Map<String, Map<String, Integer>> countsPerMonth = new HashMap<>();
                        for (HabitEntry entry : entries) {
                            String yearMonth = entry.getDate().substring(0, 7);
                            if (!countsPerMonth.containsKey(entry.getHabitId())) {
                                countsPerMonth.put(entry.getHabitId(), new HashMap<>());
                            }
                            Map<String, Integer> monthMap = countsPerMonth.get(entry.getHabitId());
                            monthMap.put(yearMonth, monthMap.getOrDefault(yearMonth, 0) + 1);
                        }

                        List<StatsAdapter.StatItem> items = new ArrayList<>();
                        for (Habit habit : habits) {
                            Map<String, Integer> monthMap = countsPerMonth.get(habit.getId());
                            int currentCount = 0;
                            float topNormalized = 0;
                            String topMonthLabel = "-";

                            if (monthMap != null) {
                                currentCount = monthMap.getOrDefault(currentYearMonth, 0);

                                for (Map.Entry<String, Integer> e : monthMap.entrySet()) {
                                    if (!e.getKey().equals(currentYearMonth)) {
                                        int daysInMonth = getDaysInMonth(e.getKey());
                                        float normalized = (float) e.getValue() / daysInMonth * currentDay;
                                        if (normalized > topNormalized) {
                                            topNormalized = normalized;
                                            topMonthLabel = e.getKey();
                                        }
                                    }
                                }
                            }

                            String value;
                            if (topNormalized == 0) {
                                value = currentCount + "x (kein Vergleich)";
                            } else {
                                float diff = ((currentCount - topNormalized) / topNormalized) * 100;
                                String sign = diff >= 0 ? "+" : "";
                                value = currentCount + "x vs " + topMonthLabel + " (" + sign + Math.round(diff) + "%)";
                            }

                            items.add(new StatsAdapter.StatItem(habit, value));
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

    private int getDaysInMonth(String yearMonth) {
        String[] parts = yearMonth.split("-");
        Calendar cal = Calendar.getInstance();
        cal.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, 1);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
}