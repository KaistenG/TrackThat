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
            Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

            if (id == R.id.nav_habits) {
                if (current instanceof HabitsFragment) {
                    // Zurück zum Kalender
                    showFragment(new CalendarFragment());
                    resetNavigation();
                    return false;
                } else {
                    showFragment(new HabitsFragment());
                    bottomNav.getMenu().findItem(R.id.nav_habits).setTitle("Zurück");
                    bottomNav.getMenu().findItem(R.id.nav_habits).setIcon(android.R.drawable.ic_media_previous);
                    bottomNav.getMenu().findItem(R.id.nav_stats).setTitle("Statistiken");
                    bottomNav.getMenu().findItem(R.id.nav_stats).setIcon(android.R.drawable.ic_menu_report_image);
                    return true;
                }
            } else if (id == R.id.nav_stats) {
                if (current instanceof StatsFragment) {
                    // Zurück zum Kalender
                    showFragment(new CalendarFragment());
                    resetNavigation();
                    return false;
                } else {
                    showFragment(new StatsFragment());
                    bottomNav.getMenu().findItem(R.id.nav_stats).setTitle("Zurück");
                    bottomNav.getMenu().findItem(R.id.nav_stats).setIcon(android.R.drawable.ic_media_previous);
                    bottomNav.getMenu().findItem(R.id.nav_habits).setTitle("Gewohnheiten");
                    bottomNav.getMenu().findItem(R.id.nav_habits).setIcon(android.R.drawable.ic_menu_edit);
                    return true;
                }
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

    private void resetNavigation() {
        bottomNav.getMenu().findItem(R.id.nav_habits).setTitle("Gewohnheiten");
        bottomNav.getMenu().findItem(R.id.nav_habits).setIcon(android.R.drawable.ic_menu_edit);
        bottomNav.getMenu().findItem(R.id.nav_stats).setTitle("Statistiken");
        bottomNav.getMenu().findItem(R.id.nav_stats).setIcon(android.R.drawable.ic_menu_report_image);
    }
}