package model;

import java.io.Serializable;

public class Volunteer implements Serializable {
    private static final long serialVersionUID = 1L;

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

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Volunteer{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", contact='" + contact + '\'' +
               ", status='" + status + '\'' +
               '}';
    }
}