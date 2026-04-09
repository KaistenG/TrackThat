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
import com.example.trackthat.repository.HabitRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HabitsFragment extends Fragment {

    private HabitRepository repository;
    private HabitAdapter adapter;

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

        adapter = new HabitAdapter(habit -> {
            // TODO: Habit bearbeiten/löschen
        });

        RecyclerView recycler = view.findViewById(R.id.recyclerViewHabits);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.buttonAddHabit);
        fab.setOnClickListener(v -> {
            AddHabitDialog dialog = new AddHabitDialog();
            dialog.setOnHabitAddedListener(habit -> {
                repository.addHabit(habit, new HabitRepository.OnSuccessListener() {
                    @Override
                    public void onSuccess() {
                        loadHabits();
                    }

                    @Override
                    public void onFailure(String error) {}
                });
            });
            dialog.show(getParentFragmentManager(), "AddHabitDialog");
        });

        loadHabits();
    }

    private void loadHabits() {
        repository.getHabits(new HabitRepository.OnHabitsLoadedListener() {
            @Override
            public void onLoaded(java.util.List<com.example.trackthat.model.Habit> habits) {
                adapter.setHabits(habits);
            }

            @Override
            public void onFailure(String error) {}
        });
    }
}