package com.example.trackthat.ui.habits;

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
import com.example.trackthat.repository.HabitRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HabitsFragment extends Fragment {

    private HabitRepository repository;
    private HabitSectionAdapter weeklyAdapter;
    private HabitSectionAdapter dailyAdapter;
    private RecyclerView recyclerWeekly;
    private RecyclerView recyclerDaily;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_habits, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new HabitRepository();

        weeklyAdapter = new HabitSectionAdapter(new HabitSectionAdapter.OnHabitActionListener() {
            @Override
            public void onEdit(Habit habit) { navigateToEdit(habit); }

            @Override
            public void onDelete(Habit habit) { showDeleteDialog(habit); }

            @Override
            public void onMoveUp(int position) {
                if (position <= 0) return;
                weeklyAdapter.swapHabits(position, position - 1);
                saveOrder();
            }

            @Override
            public void onMoveDown(int position) {
                if (position >= weeklyAdapter.getItemCount() - 1) return;
                weeklyAdapter.swapHabits(position, position + 1);
                saveOrder();
            }
        });

        dailyAdapter = new HabitSectionAdapter(new HabitSectionAdapter.OnHabitActionListener() {
            @Override
            public void onEdit(Habit habit) { navigateToEdit(habit); }

            @Override
            public void onDelete(Habit habit) { showDeleteDialog(habit); }

            @Override
            public void onMoveUp(int position) {
                if (position <= 0) return;
                dailyAdapter.swapHabits(position, position - 1);
                saveOrder();
            }

            @Override
            public void onMoveDown(int position) {
                if (position >= dailyAdapter.getItemCount() - 1) return;
                dailyAdapter.swapHabits(position, position + 1);
                saveOrder();
            }
        });

        recyclerWeekly = view.findViewById(R.id.recyclerViewWeekly);
        recyclerDaily = view.findViewById(R.id.recyclerViewDaily);

        recyclerWeekly.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() { return false; }
        });
        recyclerWeekly.setAdapter(weeklyAdapter);

        recyclerDaily.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() { return false; }
        });
        recyclerDaily.setAdapter(dailyAdapter);

        FloatingActionButton fab = view.findViewById(R.id.buttonAddHabit);
        fab.setOnClickListener(v ->
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new AddHabitFragment())
                        .addToBackStack(null)
                        .commit());

        loadHabits();
    }

    private void navigateToEdit(Habit habit) {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, EditHabitFragment.newInstance(habit.getId()))
                .addToBackStack(null)
                .commit();
    }

    private void showDeleteDialog(Habit habit) {
        new android.app.AlertDialog.Builder(getContext())
                .setTitle("Habit löschen")
                .setMessage("Möchtest du \"" + habit.getName() + "\" wirklich löschen?")
                .setPositiveButton("Löschen", (dialog, which) -> {
                    repository.deleteHabit(habit.getId(), new HabitRepository.OnSuccessListener() {
                        @Override
                        public void onSuccess() { loadHabits(); }
                        @Override
                        public void onFailure(String error) {}
                    });
                })
                .setNegativeButton("Abbrechen", null)
                .show();
    }

    private void saveOrder() {
        List<Habit> all = new ArrayList<>();
        all.addAll(weeklyAdapter.getHabitsInOrder());
        all.addAll(dailyAdapter.getHabitsInOrder());
        repository.updateHabitOrder(all, new HabitRepository.OnSuccessListener() {
            @Override
            public void onSuccess() {}
            @Override
            public void onFailure(String error) {}
        });
    }

    private void loadHabits() {
        repository.getHabitsSorted(new HabitRepository.OnHabitsLoadedListener() {
            @Override
            public void onLoaded(List<Habit> habits) {
                List<Habit> weeklies = new ArrayList<>();
                List<Habit> dailies = new ArrayList<>();
                for (Habit h : habits) {
                    if (h.getVisualType().equals("VERTICAL")) weeklies.add(h);
                    else dailies.add(h);
                }
                weeklyAdapter.setHabits(weeklies);
                dailyAdapter.setHabits(dailies);
            }
            @Override
            public void onFailure(String error) {}
        });
    }
}