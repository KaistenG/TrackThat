package com.example.trackthat.ui.options;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.trackthat.R;
import com.example.trackthat.ui.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class OptionsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_options, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.buttonLogout).setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(getContext())
                    .setTitle("Abmelden")
                    .setMessage("Möchtest du dich wirklich abmelden?")
                    .setPositiveButton("Abmelden", (dialog, which) -> {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                    })
                    .setNegativeButton("Abbrechen", null)
                    .show();
        });

        view.findViewById(R.id.buttonHowTo).setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new HowToFragment())
                    .addToBackStack(null)
                    .commit();
        });

        view.findViewById(R.id.buttonImprint).setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new ImprintFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }
}