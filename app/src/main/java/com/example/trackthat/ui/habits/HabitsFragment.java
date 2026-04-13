package com.example.trackthat.ui.habits;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackthat.R;
import com.example.trackthat.model.Group;
import com.example.trackthat.model.Habit;
import com.example.trackthat.repository.HabitRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import androidx.recyclerview.widget.ItemTouchHelper;

public class HabitsFragment extends Fragment {

    private HabitRepository repository;
    private HabitGroupAdapter adapter;

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
        adapter = new HabitGroupAdapter(habit -> {
            // TODO: Habit bearbeiten/löschen
        });

        RecyclerView recycler = view.findViewById(R.id.recyclerViewHabits);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);

        HabitDragCallback dragCallback = new HabitDragCallback(new HabitDragCallback.OnItemMovedListener() {
            @Override
            public void onItemMoved(int fromPosition, int toPosition) {
                adapter.moveItem(fromPosition, toPosition);
            }

            @Override
            public void onDragFinished() {
                repository.updateHabitOrder(adapter.getHabitsInOrder(),
                        new HabitRepository.OnSuccessListener() {
                            @Override
                            public void onSuccess() {}

                            @Override
                            public void onFailure(String error) {}
                        });
            }
        });

        new ItemTouchHelper(dragCallback).attachToRecyclerView(recycler);

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
        repository.getGroups(new HabitRepository.OnGroupsLoadedListener() {
            @Override
            public void onLoaded(List<Group> groups) {
                repository.getHabitsSorted(new HabitRepository.OnHabitsLoadedListener() {
                    @Override
                    public void onLoaded(List<Habit> habits) {
                        adapter.setData(groups, habits);
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