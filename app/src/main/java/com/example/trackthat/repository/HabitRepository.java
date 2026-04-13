package com.example.trackthat.repository;

import com.example.trackthat.model.Habit;
import com.example.trackthat.model.HabitEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.ListenerRegistration;

import com.example.trackthat.model.Group;

public class HabitRepository {

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    public HabitRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    private String getUserId() {
        return auth.getCurrentUser().getUid();
    }

    // Habits
    public void addHabit(Habit habit, OnSuccessListener listener) {
        String id = db.collection("users")
                .document(getUserId())
                .collection("habits")
                .document().getId();
        habit.setId(id);
        db.collection("users").document(getUserId())
                .collection("habits").document(id)
                .set(habit)
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void getHabits(OnHabitsLoadedListener listener) {
        db.collection("users").document(getUserId())
                .collection("habits")
                .get()
                .addOnSuccessListener(query -> {
                    listener.onLoaded(query.toObjects(Habit.class));
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void getHabitsSorted(OnHabitsLoadedListener listener) {
        db.collection("users").document(getUserId())
                .collection("habits")
                .orderBy("order")
                .get()
                .addOnSuccessListener(query -> {
                    listener.onLoaded(query.toObjects(Habit.class));
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    // Entries
    public void toggleEntry(String habitId, String date, OnSuccessListener listener) {
        db.collection("users").document(getUserId())
                .collection("entries")
                .whereEqualTo("habitId", habitId)
                .whereEqualTo("date", date)
                .get()
                .addOnSuccessListener(query -> {
                    if (!query.isEmpty()) {
                        // Eintrag existiert -> löschen (toggle off)
                        query.getDocuments().get(0).getReference().delete()
                                .addOnSuccessListener(unused -> listener.onSuccess());
                    } else {
                        // Eintrag existiert nicht -> anlegen (toggle on)
                        String id = db.collection("users").document(getUserId())
                                .collection("entries").document().getId();
                        HabitEntry entry = new HabitEntry(id, habitId, date);
                        db.collection("users").document(getUserId())
                                .collection("entries").document(id)
                                .set(entry)
                                .addOnSuccessListener(unused -> listener.onSuccess());
                    }
                });
    }

    public void getEntriesForMonth(String yearMonth, OnEntriesLoadedListener listener) {
        db.collection("users").document(getUserId())
                .collection("entries")
                .whereGreaterThanOrEqualTo("date", yearMonth + "-01")
                .whereLessThanOrEqualTo("date", yearMonth + "-31")
                .get()
                .addOnSuccessListener(query -> {
                    listener.onLoaded(query.toObjects(HabitEntry.class));
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void getAllEntries(OnEntriesLoadedListener listener) {
        db.collection("users").document(getUserId())
                .collection("entries")
                .get()
                .addOnSuccessListener(query -> {
                    listener.onLoaded(query.toObjects(HabitEntry.class));
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void addGroup(Group group, OnSuccessListener listener) {
        String id = db.collection("users")
                .document(getUserId())
                .collection("groups")
                .document().getId();
        group.setId(id);
        db.collection("users").document(getUserId())
                .collection("groups").document(id)
                .set(group)
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void getGroups(OnGroupsLoadedListener listener) {
        db.collection("users").document(getUserId())
                .collection("groups")
                .orderBy("order")
                .get()
                .addOnSuccessListener(query -> {
                    listener.onLoaded(query.toObjects(Group.class));
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }
    public ListenerRegistration listenToHabits(OnHabitsLoadedListener listener) {
        return db.collection("users").document(getUserId())
                .collection("habits")
                .addSnapshotListener((query, error) -> {
                    if (error != null || query == null) return;
                    listener.onLoaded(query.toObjects(Habit.class));
                });
    }

    public ListenerRegistration listenToEntriesForMonth(String yearMonth, OnEntriesLoadedListener listener) {
        return db.collection("users").document(getUserId())
                .collection("entries")
                .whereGreaterThanOrEqualTo("date", yearMonth + "-01")
                .whereLessThanOrEqualTo("date", yearMonth + "-31")
                .addSnapshotListener((query, error) -> {
                    if (error != null || query == null) return;
                    listener.onLoaded(query.toObjects(HabitEntry.class));
                });
    }

    // Interfaces
    public interface OnSuccessListener {
        void onSuccess();
        void onFailure(String error);
    }

    public interface OnHabitsLoadedListener {
        void onLoaded(java.util.List<Habit> habits);
        void onFailure(String error);
    }

    public interface OnEntriesLoadedListener {
        void onLoaded(java.util.List<HabitEntry> entries);
        void onFailure(String error);
    }

    public interface OnGroupsLoadedListener {
        void onLoaded(java.util.List<Group> groups);
        void onFailure(String error);
    }
}