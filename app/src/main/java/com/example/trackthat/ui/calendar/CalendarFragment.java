package com.example.trackthat.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.trackthat.R;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

import com.example.trackthat.model.Habit;
import com.example.trackthat.model.HabitEntry;
import com.example.trackthat.repository.HabitRepository;
import java.util.List;

import com.google.firebase.firestore.ListenerRegistration;
import java.util.ArrayList;

public class CalendarFragment extends Fragment {

    private CalendarView calendarView;
    private TextView textViewMonth;

    private HabitRepository repository;

    private ListenerRegistration habitsListener;
    private ListenerRegistration entriesListener;
    private List<Habit> currentHabits = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new HabitRepository();

        calendarView = view.findViewById(R.id.calendarView);
        textViewMonth = view.findViewById(R.id.textViewMonth);

        ImageButton btnPrev = view.findViewById(R.id.buttonPreviousMonth);
        ImageButton btnNext = view.findViewById(R.id.buttonNextMonth);

        updateMonthLabel();

        btnPrev.setOnClickListener(v -> {
            calendarView.previousMonth();
            updateMonthLabel();
            loadCalendarData();
        });

        btnNext.setOnClickListener(v -> {
            calendarView.nextMonth();
            updateMonthLabel();
            loadCalendarData();
        });

        TextView buttonToday = view.findViewById(R.id.buttonToday);
        Calendar today = Calendar.getInstance();
        buttonToday.setText(String.valueOf(today.get(Calendar.DAY_OF_MONTH)));
        buttonToday.setOnClickListener(v -> {
            calendarView.jumpToToday();
            updateMonthLabel();
            loadCalendarData();
            Calendar todayCalendar = Calendar.getInstance();
            DayBottomSheet sheet = DayBottomSheet.newInstance(
                    todayCalendar.get(Calendar.YEAR),
                    todayCalendar.get(Calendar.MONTH),
                    todayCalendar.get(Calendar.DAY_OF_MONTH)
            );
            sheet.show(getParentFragmentManager(), "DayBottomSheet");
        });

        calendarView.setOnDayClickListener((y, m, d) -> {
            DayBottomSheet sheet = DayBottomSheet.newInstance(y, m, d);
            sheet.show(getParentFragmentManager(), "DayBottomSheet");
        });
        loadCalendarData();
    }

    private void updateMonthLabel() {
        String[] months = new DateFormatSymbols(Locale.GERMAN).getMonths();
        String monthName = months[calendarView.getMonth()];
        monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1);
        textViewMonth.setText(monthName + " " + calendarView.getYear());
    }

    private void loadCalendarData() {
        String yearMonth = String.format("%04d-%02d", calendarView.getYear(), calendarView.getMonth() + 1);

        if (habitsListener != null) habitsListener.remove();
        if (entriesListener != null) entriesListener.remove();

        habitsListener = repository.listenToHabits(new HabitRepository.OnHabitsLoadedListener() {
            @Override
            public void onLoaded(List<Habit> habits) {
                currentHabits = habits;
                entriesListener = repository.listenToEntriesForMonth(yearMonth, new HabitRepository.OnEntriesLoadedListener() {
                    @Override
                    public void onLoaded(List<HabitEntry> entries) {
                        calendarView.setMonthData(currentHabits, entries);
                    }

                    @Override
                    public void onFailure(String error) {}
                });
            }

            @Override
            public void onFailure(String error) {}
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (habitsListener != null) habitsListener.remove();
        if (entriesListener != null) entriesListener.remove();
    }
}