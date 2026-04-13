package com.example.trackthat.ui.habits;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.trackthat.R;
import com.example.trackthat.model.Habit;

import android.widget.CheckBox;

public class AddHabitDialog extends DialogFragment {

    public interface OnHabitAddedListener {
        void onHabitAdded(Habit habit);
    }

    private OnHabitAddedListener listener;
    private int selectedColor = Color.parseColor("#F44336");
    private View lastSelectedColorView = null;

    public void setOnHabitAddedListener(OnHabitAddedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_add_habit, null);

        EditText editTextName = view.findViewById(R.id.editTextHabitName);
        RadioGroup radioGroup = view.findViewById(R.id.radioGroupVisualType);
        CheckBox checkBoxStreakable = view.findViewById(R.id.checkBoxStreakable);
        GridLayout colorGrid = view.findViewById(R.id.colorGrid);

        // Farbauswahl verdrahten
        for (int i = 0; i < colorGrid.getChildCount(); i++) {
            View colorView = colorGrid.getChildAt(i);
            colorView.setOnClickListener(v -> {
                String colorHex = (String) v.getTag();
                selectedColor = Color.parseColor(colorHex);

                // Auswahl visuell markieren
                if (lastSelectedColorView != null) {
                    lastSelectedColorView.setScaleX(1.0f);
                    lastSelectedColorView.setScaleY(1.0f);
                }
                v.setScaleX(1.3f);
                v.setScaleY(1.3f);
                lastSelectedColorView = v;
            });
        }

        return new AlertDialog.Builder(requireContext())
                .setTitle("Gewohnheit anlegen")
                .setView(view)
                .setPositiveButton("Speichern", (dialog, which) -> {
                    String name = editTextName.getText().toString().trim();
                    if (name.isEmpty()) return;

                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    String visualType;
                    if (selectedId == R.id.radioHorizontal) {
                        visualType = "HORIZONTAL";
                    } else if (selectedId == R.id.radioBorder) {
                        visualType = "BORDER";
                    } else {
                        visualType = "VERTICAL";
                    }

                    Habit habit = new Habit(null, name, selectedColor, visualType, checkBoxStreakable.isChecked());
                    if (listener != null) listener.onHabitAdded(habit);
                })
                .setNegativeButton("Abbrechen", null)
                .create();
    }
}