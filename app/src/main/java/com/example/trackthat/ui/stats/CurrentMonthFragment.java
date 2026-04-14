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

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CurrentMonthFragment extends Fragment {

    private HabitRepository repository;
    private StatsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_current_month, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new HabitRepository();
        adapter = new StatsAdapter();

        RecyclerView recycler = view.findViewById(R.id.recyclerViewStats);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);

        // Titel setzen
        Calendar today = Calendar.getInstance();
        String[] months = new DateFormatSymbols(Locale.GERMAN).getMonths();
        String monthName = months[today.get(Calendar.MONTH)];
        monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1);
        ((android.widget.TextView) view.findViewById(R.id.textViewTitle))
                .setText(monthName + " " + today.get(Calendar.YEAR));

        loadStats();
    }

    private void loadStats() {
        Calendar today = Calendar.getInstance();
        String yearMonth = String.format("%04d-%02d",
                today.get(Calendar.YEAR), today.get(Calendar.MONTH) + 1);

        repository.getHabits(new HabitRepository.OnHabitsLoadedListener() {
            @Override
            public void onLoaded(List<Habit> habits) {
                repository.getEntriesForMonth(yearMonth, new HabitRepository.OnEntriesLoadedListener() {
                    @Override
                    public void onLoaded(List<HabitEntry> entries) {
                        List<StatsAdapter.StatItem> items = new ArrayList<>();
                        for (Habit habit : habits) {
                            int count = 0;
                            for (HabitEntry entry : entries) {
                                if (entry.getHabitId().equals(habit.getId())) count++;
                            }
                            items.add(new StatsAdapter.StatItem(habit, count + "x"));
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
}