package com.example.trackthat.ui.stats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.trackthat.R;

public class StatsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.cardCurrentMonth).setOnClickListener(v ->
                navigate(new CurrentMonthFragment()));

        view.findViewById(R.id.cardAverage).setOnClickListener(v ->
                navigate(new AverageMonthFragment()));

        view.findViewById(R.id.cardTopMonth).setOnClickListener(v ->
                navigate(new TopMonthFragment()));

        view.findViewById(R.id.cardStreaks).setOnClickListener(v ->
                navigate(new StreaksFragment()));
    }

    private void navigate(Fragment fragment) {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}