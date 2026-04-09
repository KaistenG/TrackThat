package com.example.trackthat;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.trackthat.ui.calendar.CalendarFragment;
import com.example.trackthat.ui.habits.HabitsFragment;
import com.example.trackthat.ui.stats.StatsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottomNavigation);

        // Kalender als Startansicht
        showFragment(new CalendarFragment());

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            // Wenn bereits ein nicht-Kalender Fragment aktiv ist -> zurück zum Kalender
            Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
            if (!(current instanceof CalendarFragment)) {
                showFragment(new CalendarFragment());
                bottomNav.getMenu().findItem(R.id.nav_habits).setTitle("Gewohnheiten");
                bottomNav.getMenu().findItem(R.id.nav_habits).setIcon(android.R.drawable.ic_menu_edit);
                bottomNav.getMenu().findItem(R.id.nav_stats).setTitle("Statistiken");
                bottomNav.getMenu().findItem(R.id.nav_stats).setIcon(android.R.drawable.ic_menu_report_image);
                return false;
            }

            if (id == R.id.nav_habits) {
                showFragment(new HabitsFragment());
                item.setTitle("Zurück");
                item.setIcon(android.R.drawable.ic_media_previous);
                return true;
            } else if (id == R.id.nav_stats) {
                showFragment(new StatsFragment());
                item.setTitle("Zurück");
                item.setIcon(android.R.drawable.ic_media_previous);
                return true;
            }
            return false;
        });
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}