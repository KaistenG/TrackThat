package com.example.trackthat;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.trackthat.ui.calendar.CalendarView;
import com.example.trackthat.ui.habits.HabitsFragment;
import com.example.trackthat.ui.stats.StatsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DateFormatSymbols;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private FrameLayout fragmentContainer;
    private CalendarView calendarView;
    private TextView textViewMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarView = findViewById(R.id.calendarView);
        textViewMonth = findViewById(R.id.textViewMonth);
        fragmentContainer = findViewById(R.id.fragmentContainer);

        ImageButton btnPrev = findViewById(R.id.buttonPreviousMonth);
        ImageButton btnNext = findViewById(R.id.buttonNextMonth);

        updateMonthLabel();

        calendarView.setOnDayClickListener((y, m, d) -> {
            Toast.makeText(this, "Tag: " + d + "." + (m + 1) + "." + y, Toast.LENGTH_SHORT).show();
        });
        btnPrev.setOnClickListener(v -> {
            calendarView.previousMonth();
            updateMonthLabel();

        });

        btnNext.setOnClickListener(v -> {
            calendarView.nextMonth();
            updateMonthLabel();
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_habits) {
                showFragment(new HabitsFragment());
                return true;
            } else if (id == R.id.nav_stats) {
                showFragment(new StatsFragment());
                return true;
            }
            return false;
        });
    }

    private void updateMonthLabel() {
        String[] months = new DateFormatSymbols(Locale.GERMAN).getMonths();
        String monthName = months[calendarView.getMonth()];
        monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1);
        textViewMonth.setText(monthName + " " + calendarView.getYear());
    }

    private void showFragment(Fragment fragment) {
        fragmentContainer.setVisibility(android.view.View.VISIBLE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}