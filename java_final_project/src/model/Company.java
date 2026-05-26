// src/model/Company.java
package model;

public class Company {
    private int id;
    private String name;
    private String location;
    private String accountNumber;
    private String accountOwnerName;  // NEW: Account owner's name
    private String telebirrPhone;
    private String telebirrOwnerName; // NEW: Telebirr owner's name
    private String adminTelegram;
    private int adminId;

    public Company(int id, String name, String location, String accountNumber,
                  String accountOwnerName, String telebirrPhone, String telebirrOwnerName,
                  String adminTelegram, int adminId) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.accountNumber = accountNumber;
        this.accountOwnerName = accountOwnerName;
        this.telebirrPhone = telebirrPhone;
        this.telebirrOwnerName = telebirrOwnerName;
        this.adminTelegram = adminTelegram;
        this.adminId = adminId;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getAccountNumber() { return accountNumber; }
    public String getAccountOwnerName() { return accountOwnerName; }  // NEW
    public String getTelebirrPhone() { return telebirrPhone; }
    public String getTelebirrOwnerName() { return telebirrOwnerName; }  // NEW
    public String getAdminTelegram() { return adminTelegram; }
    public int getAdminId() { return adminId; }
}