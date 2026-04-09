package com.example.trackthat.model;

public class HabitEntry {
    private String id;
    private String habitId;
    private String date; // Format: "YYYY-MM-DD"

    public HabitEntry() {} // Pflicht für Firestore

    public HabitEntry(String id, String habitId, String date) {
        this.id = id;
        this.habitId = habitId;
        this.date = date;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getHabitId() { return habitId; }
    public void setHabitId(String habitId) { this.habitId = habitId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}