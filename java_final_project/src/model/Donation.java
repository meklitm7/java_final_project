package model;

public class Donation {
    private int id;
    private int donorId;
    private String type;
    private String itemName;
    private int quantity;
    private double amount;
    private int companyId;
    private String screenshot;
    private String status;

    public Donation(int id, int donorId, String type, String itemName, int quantity,
                    double amount, int companyId, String screenshot, String status) {
        this.id = id;
        this.donorId = donorId;
        this.type = type;
        this.itemName = itemName;
        this.quantity = quantity;
        this.amount = amount;
        this.companyId = companyId;
        this.screenshot = screenshot;
        this.status = status;
    }

    // Getters and Setters
    public int getId() { return id; }
    public int getDonorId() { return donorId; }
    public String getType() { return type; }
    public String getItemName() { return itemName; }
    public int getQuantity() { return quantity; }
    public double getAmount() { return amount; }
    public int getCompanyId() { return companyId; }
    public String getScreenshot() { return screenshot; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}