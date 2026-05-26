// src/model/VolunteerTask.java
package model;

public class VolunteerTask {
    private int id;
    private String name;
    private String category;
    private String description;

    public VolunteerTask(int id, String name, String category, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
}