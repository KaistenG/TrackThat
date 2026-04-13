package com.example.trackthat.model;

public class Habit {
    private String id;
    private String name;
    private int color;
    private String visualType; // "VERTICAL", "HORIZONTAL", "BORDER"

    private boolean streakable; //für sinnvolle Streaks

    private String groupId;
    private int order;

    public Habit() {} // Pflicht für Firestore

    public Habit(String id, String name, int color, String visualType, boolean streakable, String groupId, int order) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.visualType = visualType;
        this.streakable = streakable;
        this.groupId = groupId;
        this.order = order;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getColor() { return color; }
    public void setColor(int color) { this.color = color; }

    public String getVisualType() { return visualType; }
    public void setVisualType(String visualType) { this.visualType = visualType; }

    public boolean isStreakable() { return streakable; }
    public void setStreakable(boolean streakable) { this.streakable = streakable; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }
}