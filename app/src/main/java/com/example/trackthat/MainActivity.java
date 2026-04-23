package com.example.trackthat;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.trackthat.ui.calendar.CalendarFragment;
import com.example.trackthat.ui.habits.HabitsFragment;
import com.example.trackthat.ui.stats.StatsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.trackthat.ui.options.OptionsFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottomNavigation);

        // Kalender als Startansicht
        showFragment(new CalendarFragment());
        new android.os.Handler().post(() -> resetNavigation());

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

            if (id == R.id.nav_habits) {
                if (current instanceof HabitsFragment) {
                    showFragment(new CalendarFragment());
                    resetNavigation();
                    return false;
                } else {
                    showFragment(new HabitsFragment());
                    bottomNav.getMenu().findItem(R.id.nav_habits).setTitle("Zurück");
                    bottomNav.getMenu().findItem(R.id.nav_habits).setIcon(android.R.drawable.ic_media_previous);
                    bottomNav.getMenu().findItem(R.id.nav_stats).setTitle("Statistiken");
                    bottomNav.getMenu().findItem(R.id.nav_stats).setIcon(android.R.drawable.ic_menu_report_image);
                    bottomNav.getMenu().findItem(R.id.nav_options).setTitle("Optionen");
                    bottomNav.getMenu().findItem(R.id.nav_options).setIcon(android.R.drawable.ic_menu_preferences);
                    return true;
                }
            } else if (id == R.id.nav_stats) {
                if (current instanceof StatsFragment) {
                    showFragment(new CalendarFragment());
                    resetNavigation();
                    return false;
                } else {
                    showFragment(new StatsFragment());
                    bottomNav.getMenu().findItem(R.id.nav_stats).setTitle("Zurück");
                    bottomNav.getMenu().findItem(R.id.nav_stats).setIcon(android.R.drawable.ic_media_previous);
                    bottomNav.getMenu().findItem(R.id.nav_habits).setTitle("Gewohnheiten");
                    bottomNav.getMenu().findItem(R.id.nav_habits).setIcon(android.R.drawable.ic_menu_edit);
                    bottomNav.getMenu().findItem(R.id.nav_options).setTitle("Optionen");
                    bottomNav.getMenu().findItem(R.id.nav_options).setIcon(android.R.drawable.ic_menu_preferences);
                    return true;
                }
            } else if (id == R.id.nav_options) {
                if (current instanceof OptionsFragment) {
                    showFragment(new CalendarFragment());
                    resetNavigation();
                    return false;
                } else {
                    showFragment(new OptionsFragment());
                    bottomNav.getMenu().findItem(R.id.nav_options).setTitle("Zurück");
                    bottomNav.getMenu().findItem(R.id.nav_options).setIcon(android.R.drawable.ic_media_previous);
                    bottomNav.getMenu().findItem(R.id.nav_habits).setTitle("Gewohnheiten");
                    bottomNav.getMenu().findItem(R.id.nav_habits).setIcon(android.R.drawable.ic_menu_edit);
                    bottomNav.getMenu().findItem(R.id.nav_stats).setTitle("Statistiken");
                    bottomNav.getMenu().findItem(R.id.nav_stats).setIcon(android.R.drawable.ic_menu_report_image);
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
        bottomNav.getMenu().findItem(R.id.nav_stats).setTitle("Statistiken");
        bottomNav.getMenu().findItem(R.id.nav_stats).setIcon(android.R.drawable.ic_menu_report_image);
        bottomNav.getMenu().findItem(R.id.nav_habits).setTitle("Gewohnheiten");
        bottomNav.getMenu().findItem(R.id.nav_habits).setIcon(android.R.drawable.ic_menu_edit);
        bottomNav.getMenu().findItem(R.id.nav_options).setTitle("Optionen");
        bottomNav.getMenu().findItem(R.id.nav_options).setIcon(android.R.drawable.ic_menu_preferences);
        bottomNav.getMenu().setGroupCheckable(0, true, false);
        for (int i = 0; i < bottomNav.getMenu().size(); i++) {
            bottomNav.getMenu().getItem(i).setChecked(false);
        }
        bottomNav.getMenu().setGroupCheckable(0, true, true);
    }

    public void navigateToFragment(Fragment fragment) {
        showFragment(fragment);
        bottomNav.getMenu().findItem(R.id.nav_habits).setTitle("Zurück");
        bottomNav.getMenu().findItem(R.id.nav_habits).setIcon(android.R.drawable.ic_media_previous);
    }
}