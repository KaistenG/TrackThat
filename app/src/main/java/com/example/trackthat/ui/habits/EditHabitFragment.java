package com.example.trackthat.ui.habits;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.trackthat.R;
import com.example.trackthat.model.Habit;
import com.example.trackthat.repository.HabitRepository;

import java.util.List;

public class EditHabitFragment extends Fragment {

    private static final String ARG_HABIT_ID = "habit_id";

    private HabitRepository repository;
    private Habit habit;
    private int selectedColor;
    private View lastSelectedColor = null;

    public static EditHabitFragment newInstance(String habitId) {
        EditHabitFragment fragment = new EditHabitFragment();
        Bundle args = new Bundle();
        args.putString(ARG_HABIT_ID, habitId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_habit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new HabitRepository();
        String habitId = getArguments().getString(ARG_HABIT_ID);

        repository.getHabits(new HabitRepository.OnHabitsLoadedListener() {
            @Override
            public void onLoaded(List<Habit> habits) {
                for (Habit h : habits) {
                    if (h.getId().equals(habitId)) {
                        habit = h;
                        break;
                    }
                }
                if (habit != null) populateForm(view);
            }

            @Override
            public void onFailure(String error) {}
        });

        // Löschen via LongPress auf Speichern
        view.findViewById(R.id.buttonSave).setOnLongClickListener(v -> {
            new android.app.AlertDialog.Builder(getContext())
                    .setTitle("Habit löschen")
                    .setMessage("Möchtest du diesen Habit wirklich löschen?")
                    .setPositiveButton("Löschen", (dialog, which) -> {
                        repository.deleteHabit(habit.getId(), new HabitRepository.OnSuccessListener() {
                            @Override
                            public void onSuccess() {
                                getParentFragmentManager().popBackStack();
                            }

                            @Override
                            public void onFailure(String error) {
                                Toast.makeText(getContext(), "Fehler: " + error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Abbrechen", null)
                    .show();
            return true;
        });

        view.findViewById(R.id.buttonSave).setOnClickListener(v -> saveHabit(view));
    }

    private void populateForm(View view) {
        selectedColor = habit.getColor();

        ((EditText) view.findViewById(R.id.editTextName)).setText(habit.getName());

        RadioGroup radioGroup = view.findViewById(R.id.radioGroupVisualType);
        if (habit.getVisualType().equals("HORIZONTAL")) {
            radioGroup.check(R.id.radioHorizontal);
        } else {
            radioGroup.check(R.id.radioVertical);
        }

        GridLayout colorGrid = view.findViewById(R.id.colorGrid);
        for (int i = 0; i < colorGrid.getChildCount(); i++) {
            View colorView = colorGrid.getChildAt(i);
            String tag = (String) colorView.getTag();
            if (Color.parseColor(tag) == selectedColor) {
                colorView.setScaleX(1.3f);
                colorView.setScaleY(1.3f);
                lastSelectedColor = colorView;
            }
            colorView.setOnClickListener(v -> {
                selectedColor = Color.parseColor((String) v.getTag());
                if (lastSelectedColor != null) {
                    lastSelectedColor.setScaleX(1.0f);
                    lastSelectedColor.setScaleY(1.0f);
                }
                v.setScaleX(1.3f);
                v.setScaleY(1.3f);
                lastSelectedColor = v;
            });
        }

        ((CheckBox) view.findViewById(R.id.checkBoxStreakable)).setChecked(habit.isStreakable());
    }

    private void saveHabit(View view) {
        EditText editTextName = view.findViewById(R.id.editTextName);
        String name = editTextName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Bitte einen Namen eingeben", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioGroup radioGroup = view.findViewById(R.id.radioGroupVisualType);
        int checkedId = radioGroup.getCheckedRadioButtonId();
        String visualType;
        if (checkedId == R.id.radioHorizontal) visualType = "HORIZONTAL";
        else visualType = "VERTICAL";

        CheckBox checkBox = view.findViewById(R.id.checkBoxStreakable);

        habit.setName(name);
        habit.setColor(selectedColor);
        habit.setVisualType(visualType);
        habit.setStreakable(checkBox.isChecked());

        repository.updateHabit(habit, new HabitRepository.OnSuccessListener() {
            @Override
            public void onSuccess() {
                getParentFragmentManager().popBackStack();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getContext(), "Fehler: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}