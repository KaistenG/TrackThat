package com.example.trackthat.model;

public class Habit {
    private String id;
    private String name;
    private int color;
    private String visualType; // "VERTICAL", "HORIZONTAL", "BORDER"

    public Habit() {} // Pflicht für Firestore

    public Habit(String id, String name, int color, String visualType) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.visualType = visualType;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getColor() { return color; }
    public void setColor(int color) { this.color = color; }

    public String getVisualType() { return visualType; }
    public void setVisualType(String visualType) { this.visualType = visualType; }
}