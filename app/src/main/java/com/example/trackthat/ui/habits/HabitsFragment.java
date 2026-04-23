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

import java.util.List;

public class HabitsFragment extends Fragment {

    private HabitRepository repository;
    private HabitSectionAdapter adapter;

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
        adapter = new HabitSectionAdapter(new HabitSectionAdapter.OnHabitActionListener() {
            @Override
            public void onEdit(Habit habit) {
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, EditHabitFragment.newInstance(habit.getId()))
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onDelete(Habit habit) {
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

            @Override
            public void onMoveUp(int position) {
                if (position <= 1) return;
                if (adapter.isHeader(position - 1)) return;
                adapter.swapHabits(position, position - 1);
                repository.updateHabitOrder(adapter.getHabitsInOrder(),
                        new HabitRepository.OnSuccessListener() {
                            @Override
                            public void onSuccess() {}
                            @Override
                            public void onFailure(String error) {}
                        });
            }

            @Override
            public void onMoveDown(int position) {
                if (position >= adapter.getItemCount() - 1) return;
                if (adapter.isHeader(position + 1)) return;
                adapter.swapHabits(position, position + 1);
                repository.updateHabitOrder(adapter.getHabitsInOrder(),
                        new HabitRepository.OnSuccessListener() {
                            @Override
                            public void onSuccess() {}
                            @Override
                            public void onFailure(String error) {}
                        });
            }
        });

        RecyclerView recycler = view.findViewById(R.id.recyclerViewHabits);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.buttonAddHabit);
        fab.setOnClickListener(v ->
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new AddHabitFragment())
                        .addToBackStack(null)
                        .commit());

        loadHabits();
    }

    private void loadHabits() {
        repository.getHabitsSorted(new HabitRepository.OnHabitsLoadedListener() {
            @Override
            public void onLoaded(List<Habit> habits) {
                adapter.setHabits(habits);
            }

            @Override
            public void onFailure(String error) {}
        });
    }
}