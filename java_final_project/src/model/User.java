package model;

public class User {
    private int id;
    private String name;
    private String role;
    private String email;
    private String password;
    private String telegramUsername;

    public User(int id, String name, String role, String email, String password, String telegramUsername) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.email = email;
        this.password = password;
        this.telegramUsername = telegramUsername;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getTelegramUsername() { return telegramUsername; }
}