package model;

import java.io.Serializable;

public class Company implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String location;
    private String accountNumber;
    private String accountOwnerName;
    private String telebirrPhone;
    private String telebirrOwnerName;
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

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getAccountOwnerName() { return accountOwnerName; }
    public void setAccountOwnerName(String accountOwnerName) { this.accountOwnerName = accountOwnerName; }

    public String getTelebirrPhone() { return telebirrPhone; }
    public void setTelebirrPhone(String telebirrPhone) { this.telebirrPhone = telebirrPhone; }

    public String getTelebirrOwnerName() { return telebirrOwnerName; }
    public void setTelebirrOwnerName(String telebirrOwnerName) { this.telebirrOwnerName = telebirrOwnerName; }

    public String getAdminTelegram() { return adminTelegram; }
    public void setAdminTelegram(String adminTelegram) { this.adminTelegram = adminTelegram; }

    public int getAdminId() { return adminId; }
    public void setAdminId(int adminId) { this.adminId = adminId; }

    @Override
    public String toString() {
        return "Company{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", location='" + location + '\'' +
               ", accountNumber='" + accountNumber + '\'' +
               ", adminTelegram='" + adminTelegram + '\'' +
               '}';
    }
}