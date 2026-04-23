package com.example.trackthat.repository;

import com.example.trackthat.model.Habit;
import com.example.trackthat.model.HabitEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;

import com.google.firebase.firestore.PersistentCacheSettings;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;

public class HabitRepository {

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    public HabitRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
                .build();
        db.setFirestoreSettings(settings);
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

    public void updateHabit(Habit habit, OnSuccessListener listener) {
        db.collection("users").document(getUserId())
                .collection("habits").document(habit.getId())
                .set(habit)
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void deleteHabit(String habitId, OnSuccessListener listener) {
        db.collection("users").document(getUserId())
                .collection("habits").document(habitId)
                .delete()
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

    public void setDayMood(String date, String color, OnSuccessListener listener) {
        db.collection("users").document(getUserId())
                .collection("moods")
                .document(date)
                .set(new java.util.HashMap<String, Object>() {{ put("color", color); }})
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void getDayMood(String date, OnMoodLoadedListener listener) {
        db.collection("users").document(getUserId())
                .collection("moods")
                .document(date)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        listener.onLoaded((String) doc.get("color"));
                    } else {
                        listener.onLoaded(null);
                    }
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

    public void updateHabitOrder(List<Habit> habits, OnSuccessListener listener) {
        com.google.firebase.firestore.WriteBatch batch = db.batch();
        for (int i = 0; i < habits.size(); i++) {
            batch.update(
                    db.collection("users").document(getUserId())
                            .collection("habits").document(habits.get(i).getId()),
                    "order", i
            );
        }
        batch.commit()
                .addOnSuccessListener(unused -> listener.onSuccess())
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
    public ListenerRegistration listenToMoodsForMonth(String yearMonth, OnMoodsLoadedListener listener) {
        return db.collection("users").document(getUserId())
                .collection("moods")
                .whereGreaterThanOrEqualTo(com.google.firebase.firestore.FieldPath.documentId(), yearMonth + "-01")
                .whereLessThanOrEqualTo(com.google.firebase.firestore.FieldPath.documentId(), yearMonth + "-31")
                .addSnapshotListener((query, error) -> {
                    if (error != null || query == null) return;
                    java.util.Map<String, String> moods = new java.util.HashMap<>();
                    for (com.google.firebase.firestore.DocumentSnapshot doc : query.getDocuments()) {
                        moods.put(doc.getId(), (String) doc.get("color"));
                    }
                    listener.onLoaded(moods);
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

    public interface OnMoodLoadedListener {
        void onLoaded(String color);
        void onFailure(String error);
    }

    public interface OnMoodsLoadedListener {
        void onLoaded(java.util.Map<String, String> moods);
        void onFailure(String error);
    }
}