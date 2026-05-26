package model;

public class Volunteer {
    private int id;
    private String name;
    private String contact;
    private String status;

    public Volunteer(int id, String name, String contact, String status) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.status = status;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getContact() { return contact; }
    public String getStatus() { return status; }
}