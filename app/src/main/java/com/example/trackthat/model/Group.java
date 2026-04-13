package com.example.trackthat.model;

public class Group {
    private String id;
    private String name;
    private int order;

    public Group() {} // Pflicht für Firestore

    public Group(String id, String name, int order) {
        this.id = id;
        this.name = name;
        this.order = order;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }
}