package com.example.trackthat;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.trackthat.ui.habits.HabitsFragment;
import com.example.trackthat.ui.stats.StatsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentContainer = findViewById(R.id.fragmentContainer);
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

    private void showFragment(Fragment fragment) {
        fragmentContainer.setVisibility(android.view.View.VISIBLE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}