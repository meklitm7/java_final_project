package gui.screens;

import gui.NotificationPopup;
import model.CurrentUser;
import model.Company;
import model.Need;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.application.Platform;
import rmi.TesfaRMIClient;

import java.util.List;
import java.util.Optional;

public class NeedManagementPanel extends VBox {
    private Stage stage;
    private TableView<Need> needsTable;

    public NeedManagementPanel(Stage stage) {
        this.stage = stage;
        setPadding(new Insets(20));
        setSpacing(15);
        setupUI();
    }

    private void setupUI() {
        Label title = new Label("Manage Needs");
        title.setFont(Font.font("Arial", 20));
        title.setTextFill(Color.web("#2c3e50"));

        // Form for adding needs
        HBox form = new HBox(10);
        form.setPadding(new Insets(10));
        form.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 5;");

        ComboBox<String> companyCombo = new ComboBox<>();
        loadCompanies(companyCombo);

        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll("Food", "Clothes", "Books", "Pads", "Shoes", "Medicine", "Money", "Other");

        TextField descField = new TextField();
        descField.setPromptText("Description");

        Button addBtn = new Button("Add Need");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");

        form.getChildren().addAll(
            new Label("Orphanage:"), companyCombo,
            new Label("Category:"), categoryCombo,
            new Label("Description:"), descField,
            addBtn
        );

        // Table
        needsTable = new TableView<>();
        setupTable();
        loadNeeds();

        // Add button action
        addBtn.setOnAction(e -> {
            String companyName = companyCombo.getValue();
            String category = categoryCombo.getValue();
            String desc = descField.getText();

            if (companyName == null || category == null || desc.isEmpty()) {
                showAlert("All fields are required!");
                return;
            }

            // Get company ID from name
            int companyId = getCompanyIdByName(companyName);
            if (companyId <= 0) {
                showAlert("Invalid orphanage selection!");
                return;
            }

            // Show confirmation dialog
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirm Add Need");
            confirmDialog.setHeaderText("Add New Need");
            confirmDialog.setContentText("Are you sure you want to add this need?");

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (TesfaRMIClient.isConnected()) {
                    boolean success = TesfaRMIClient.addNeed(companyId, category, desc, "Pending");
                    if (success) {
                        loadNeeds();
                        descField.clear();
                        showAlert("Need added successfully!");
                    } else {
                        showAlert("Failed to add need.");
                    }
                } else {
                    showAlert("Not connected to RMI server. Please try again.");
                }
            }
        });

        getChildren().addAll(title, form, needsTable);
    }

    private void setupTable() {
        TableColumn<Need, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Need, String> orphanageCol = new TableColumn<>("Orphanage");
        orphanageCol.setCellValueFactory(new PropertyValueFactory<>("orphanageName"));

        TableColumn<Need, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Need, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Need, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Need, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Need, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(param -> new TableCell<Need, Void>() {
            private final Button markBtn = new Button("Mark Fulfilled");
            {
                markBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                markBtn.setOnAction(e -> {
                    Need need = getTableView().getItems().get(getIndex());
                    if (TesfaRMIClient.isConnected()) {
                        boolean success = TesfaRMIClient.markNeedFulfilled(need.getId());
                        if (success) {
                            need.setStatus("Fulfilled");
                            Platform.runLater(() -> getTableView().refresh());
                            showAlert("Need marked as fulfilled!");
                        } else {
                            showAlert("Failed to mark need as fulfilled.");
                        }
                    } else {
                        showAlert("Not connected to RMI server. Please try again.");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(markBtn);
                }
            }
        });

        needsTable.getColumns().addAll(idCol, orphanageCol, categoryCol, descCol, statusCol, quantityCol, actionCol);
        needsTable.setPrefWidth(800);
        needsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadCompanies(ComboBox<String> combo) {
        if (TesfaRMIClient.isConnected()) {
            List<Company> companies = TesfaRMIClient.getAllCompanies();
            for (Company company : companies) {
                combo.getItems().add(company.getName());
            }
            if (!combo.getItems().isEmpty()) {
                combo.setValue(combo.getItems().get(0));
            }
        } else {
            showAlert("Not connected to RMI server. Please try again.");
        }
    }

    private int getCompanyIdByName(String name) {
        if (TesfaRMIClient.isConnected()) {
            List<Company> companies = TesfaRMIClient.getAllCompanies();
            for (Company company : companies) {
                if (company.getName().equals(name)) {
                    return company.getId();
                }
            }
        }
        return -1;
    }

    private void loadNeeds() {
        if (TesfaRMIClient.isConnected()) {
            List<Need> needs = TesfaRMIClient.getAllNeeds();
            Platform.runLater(() -> {
                needsTable.setItems(javafx.collections.FXCollections.observableArrayList(needs));
            });
        } else {
            showAlert("Not connected to RMI server. Please try again.");
        }
    }

    private void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}