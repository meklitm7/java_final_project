package model;

import java.io.Serializable;
import java.time.LocalDate;

public class Donation implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int donorId;
    private String type;
    private String itemName;
    private int quantity;
    private double amount;
    private int companyId;
    private String screenshot;
    private String status;
    private LocalDate date;

    public Donation(int id, int donorId, String type, String itemName, int quantity,
                    double amount, int companyId, String screenshot, String status, LocalDate date) {
        this.id = id;
        this.donorId = donorId;
        this.type = type;
        this.itemName = itemName;
        this.quantity = quantity;
        this.amount = amount;
        this.companyId = companyId;
        this.screenshot = screenshot;
        this.status = status;
        this.date = date;
    }

    // Overloaded constructor without date
    public Donation(int id, int donorId, String type, String itemName, int quantity,
                    double amount, int companyId, String screenshot, String status) {
        this(id, donorId, type, itemName, quantity, amount, companyId, screenshot, status, LocalDate.now());
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDonorId() { return donorId; }
    public void setDonorId(int donorId) { this.donorId = donorId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }

    public String getScreenshot() { return screenshot; }
    public void setScreenshot(String screenshot) { this.screenshot = screenshot; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    @Override
    public String toString() {
        return "Donation{" +
               "id=" + id +
               ", donorId=" + donorId +
               ", type='" + type + '\'' +
               ", itemName='" + itemName + '\'' +
               ", quantity=" + quantity +
               ", amount=" + amount +
               ", companyId=" + companyId +
               ", status='" + status + '\'' +
               ", date=" + date +
               '}';
    }
}