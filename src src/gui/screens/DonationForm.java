package gui.screens;

import gui.SceneManager;
import model.CurrentUser;
import model.Company;
import model.Donation;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import rmi.TesfaRMIClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;

public class DonationForm extends ScrollPane {
    private Stage stage;
    private RadioButton moneyRadio, itemRadio;
    private ToggleGroup donationTypeGroup;
    private ComboBox<String> orphanageCombo;
    private ComboBox<String> itemCategoryCombo, adminCombo;
    private TextField amountField, quantityField, descriptionField;
    private Button uploadBtn, submitBtn;
    private Label uploadLabel;
    private String selectedFilePath = "";
    private VBox moneyFieldsContainer, itemFieldsContainer;
    private GridPane formGrid;
    private static final String UPLOAD_DIR = "uploads";

    public DonationForm(Stage stage) {
        this.stage = stage;
        setupUI();
        createUploadDirectory();
    }

    private void createUploadDirectory() {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }

    private void setupUI() {
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setStyle("-fx-background-color: #f8f9fa;");

        Label title = new Label("Make a Donation");
        title.setFont(Font.font("Arial", 20));
        title.setTextFill(Color.web("#2c3e50"));
        title.setStyle("-fx-font-weight: bold;");

        donationTypeGroup = new ToggleGroup();
        moneyRadio = new RadioButton("Money Donation");
        moneyRadio.setToggleGroup(donationTypeGroup);
        moneyRadio.setSelected(true);

        itemRadio = new RadioButton("Item Donation");
        itemRadio.setToggleGroup(donationTypeGroup);

        HBox typeBox = new HBox(10, new Label("Donation Type:"), moneyRadio, itemRadio);
        typeBox.setAlignment(Pos.CENTER_LEFT);

        orphanageCombo = new ComboBox<>();
        loadOrphanages();
        HBox orphanageBox = new HBox(10, new Label("Select Orphanage:"), orphanageCombo);
        orphanageBox.setAlignment(Pos.CENTER_LEFT);

        Label companyDetails = new Label();
        companyDetails.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 12px; -fx-font-weight: bold;");
        orphanageCombo.setOnAction(e -> updateCompanyDetails(companyDetails));
        updateCompanyDetails(companyDetails);

        formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(10));
        formGrid.setStyle("-fx-background-color: white; -fx-background-radius: 5;");

        amountField = new TextField();
        amountField.setPromptText("Amount in ETB");

        uploadBtn = new Button("Upload Receipt");
        uploadBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        uploadLabel = new Label("No file selected");
        uploadLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");
        uploadBtn.setOnAction(e -> uploadReceipt());

        moneyFieldsContainer = new VBox(5,
                new Label("Amount:"),
                amountField,
                new HBox(5, uploadBtn, uploadLabel));
        moneyFieldsContainer.setVisible(true);

        itemCategoryCombo = new ComboBox<>();
        itemCategoryCombo.getItems().addAll("Food", "Clothing", "Books", "Toys", "Other");
        itemCategoryCombo.setPromptText("Select item type");

        quantityField = new TextField();
        quantityField.setPromptText("Number of items");

        adminCombo = new ComboBox<>();
        loadAdmins();
        adminCombo.setPromptText("Select admin for pickup");

        descriptionField = new TextField();
        descriptionField.setPromptText("Description (optional)");

        itemFieldsContainer = new VBox(5,
                new Label("Item Category:"),
                itemCategoryCombo,
                new Label("Quantity:"),
                quantityField,
                new Label("Select Admin for Pickup:"),
                adminCombo,
                new Label("Description:"),
                descriptionField);
        itemFieldsContainer.setVisible(false);

        moneyRadio.setOnAction(e -> {
            moneyFieldsContainer.setVisible(true);
            itemFieldsContainer.setVisible(false);
        });
        itemRadio.setOnAction(e -> {
            moneyFieldsContainer.setVisible(false);
            itemFieldsContainer.setVisible(true);
        });

        submitBtn = new Button("Submit Donation");
        submitBtn.setStyle(
                "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 200px; -fx-pref-height: 40px;");
        submitBtn.setOnAction(e -> submitDonation());

        HBox buttonBox = new HBox(submitBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        formGrid.add(typeBox, 0, 0, 2, 1);
        formGrid.add(orphanageBox, 0, 1, 2, 1);
        formGrid.add(companyDetails, 0, 2, 2, 1);
        formGrid.add(moneyFieldsContainer, 0, 3, 2, 1);
        formGrid.add(itemFieldsContainer, 0, 3, 2, 1);

        mainContainer.getChildren().addAll(
                title,
                new Separator(),
                formGrid,
                buttonBox);

        setContent(mainContainer);
        setFitToWidth(true);
        setFitToHeight(true);
        setStyle("-fx-background-color: transparent;");
    }

    private void loadOrphanages() {
        if (TesfaRMIClient.isConnected()) {
            List<Company> companies = TesfaRMIClient.getAllCompanies();
            for (Company company : companies) {
                orphanageCombo.getItems().add(company.getName());
            }
            if (!orphanageCombo.getItems().isEmpty()) {
                orphanageCombo.setValue(orphanageCombo.getItems().get(0));
            }
        } else {
            showAlert("Not connected to RMI server. Please try again.");
        }
    }

    private void updateCompanyDetails(Label label) {
        if (TesfaRMIClient.isConnected()) {
            List<Company> companies = TesfaRMIClient.getAllCompanies();
            String selectedName = orphanageCombo.getValue();
            if (selectedName != null) {
                for (Company company : companies) {
                    if (company.getName().equals(selectedName)) {
                        label.setText(String.format(
                                "🏦 Account: %s | 📱 Telebirr: %s | 💬 Admin Telegram: @%s",
                                company.getAccountNumber(),
                                company.getTelebirrPhone(),
                                company.getAdminTelegram()));
                        return;
                    }
                }
            }
        } else {
            label.setText("Not connected to RMI server");
        }
    }

    private void loadAdmins() {
        if (TesfaRMIClient.isConnected()) {
            List<model.User> admins = TesfaRMIClient.getUsersByRole("Admin");
            for (model.User admin : admins) {
                adminCombo.getItems().add(admin.getName());
            }
        } else {
            showAlert("Not connected to RMI server. Please try again.");
        }
    }

    private void uploadReceipt() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Bank Receipt");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            try {
                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                Path destination = Paths.get(UPLOAD_DIR, fileName);

                Files.copy(
                        Paths.get(selectedFile.getAbsolutePath()),
                        destination,
                        StandardCopyOption.REPLACE_EXISTING);

                selectedFilePath = UPLOAD_DIR + "/" + fileName;
                uploadLabel.setText("✓ " + selectedFile.getName());
                uploadLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            } catch (IOException e) {
                showAlert("❌ Error saving receipt: " + e.getMessage());
            }
        }
    }

    private void submitDonation() {
        if (!TesfaRMIClient.isConnected()) {
            showAlert("Not connected to RMI server. Please try again.");
            return;
        }

        String type = moneyRadio.isSelected() ? "Money" : "Item";
        String selectedOrphanage = orphanageCombo.getValue();
        List<Company> companies = TesfaRMIClient.getAllCompanies();
        int companyId = 1; // Default to first company if not found
        for (Company company : companies) {
            if (company.getName().equals(selectedOrphanage)) {
                companyId = company.getId();
                break;
            }
        }

        if (type.equals("Money")) {
            if (amountField.getText().isEmpty()) {
                showAlert("Please enter the donation amount.");
                return;
            }
            if (selectedFilePath.isEmpty()) {
                showAlert("Please upload a bank receipt for money donations.");
                return;
            }
            try {
                double amount = Double.parseDouble(amountField.getText());
                boolean success = TesfaRMIClient.addDonation(
                    CurrentUser.getId(),
                    type,
                    "",
                    0,
                    amount,
                    companyId,
                    selectedFilePath,
                    "Pending"
                );
                if (success) {
                    showAlert("✓ Money donation submitted successfully! Waiting for admin verification.");
                    resetForm();
                } else {
                    showAlert("✗ Failed to submit donation. Please try again.");
                }
            } catch (NumberFormatException e) {
                showAlert("Please enter a valid amount (e.g., 1000.50).");
            }
        } else {
            if (itemCategoryCombo.getValue() == null || itemCategoryCombo.getValue().isEmpty()) {
                showAlert("Please select an item category.");
                return;
            }
            if (quantityField.getText().isEmpty()) {
                showAlert("Please enter the quantity.");
                return;
            }
            if (adminCombo.getValue() == null || adminCombo.getValue().isEmpty()) {
                showAlert("Please select an admin for pickup.");
                return;
            }
            try {
                int quantity = Integer.parseInt(quantityField.getText());
                String description = descriptionField.getText();
                boolean success = TesfaRMIClient.addDonation(
                    CurrentUser.getId(),
                    type,
                    itemCategoryCombo.getValue(),
                    quantity,
                    0.0,
                    companyId,
                    "",
                    "Pending"
                );
                if (success) {
                    showAlert("✓ Item donation submitted successfully! Admin will contact you for pickup.");
                    resetForm();
                } else {
                    showAlert("✗ Failed to submit donation. Please try again.");
                }
            } catch (NumberFormatException e) {
                showAlert("Please enter a valid quantity (whole number).");
            }
        }
    }

    private void resetForm() {
        moneyRadio.setSelected(true);
        amountField.clear();
        quantityField.clear();
        descriptionField.clear();
        uploadLabel.setText("No file selected");
        uploadLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");
        selectedFilePath = "";
        itemCategoryCombo.setValue(null);
        adminCombo.setValue(null);
        moneyFieldsContainer.setVisible(true);
        itemFieldsContainer.setVisible(false);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Donation System");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}