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

public class AddHabitFragment extends Fragment {

    private HabitRepository repository;
    private int selectedColor = Color.parseColor("#F44336");
    private View lastSelectedColor = null;

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

        GridLayout colorGrid = view.findViewById(R.id.colorGrid);
        for (int i = 0; i < colorGrid.getChildCount(); i++) {
            View colorView = colorGrid.getChildAt(i);
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

        view.findViewById(R.id.buttonSave).setOnClickListener(v -> saveHabit(view));
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
        boolean streakable = checkBox.isChecked();

        repository.getHabits(new HabitRepository.OnHabitsLoadedListener() {
            @Override
            public void onLoaded(List<Habit> habits) {
                Habit habit = new Habit(null, name, selectedColor, visualType, streakable, habits.size());
                repository.addHabit(habit, new HabitRepository.OnSuccessListener() {
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

            @Override
            public void onFailure(String error) {}
        });
    }
}