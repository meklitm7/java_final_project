package model;

public class CurrentUser {
    private static int id;
    private static String name;
    private static String role;
    private static String email;
    private static String telegramUsername;

    public static void setUser(int id, String name, String role, String email, String telegramUsername) {
        CurrentUser.id = id;
        CurrentUser.name = name;
        CurrentUser.role = role;
        CurrentUser.email = email;
        CurrentUser.telegramUsername = telegramUsername;
    }

    // Getters
    public static int getId() { return id; }
    public static String getName() { return name; }
    public static String getRole() { return role; }
    public static String getEmail() { return email; }
    public static String getTelegramUsername() { return telegramUsername; }
    public static void clear() {
        id = 0; name = null; role = null; email = null; telegramUsername = null;
    }
}