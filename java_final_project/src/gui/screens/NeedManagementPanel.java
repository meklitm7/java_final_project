package gui.screens;

import db.CompanyDAO;
import db.NeedDAO;
import model.Company;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import java.sql.Connection;

public class NeedManagementPanel extends VBox {
    private Connection conn;
    private TableView<model.Need> needsTable;

    public NeedManagementPanel(Connection conn) {
        this.conn = conn;
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
        CompanyDAO companyDAO = new CompanyDAO(conn);
        for (Company company : companyDAO.listCompanies()) {
            companyCombo.getItems().add(company.getName());
        }

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

            NeedDAO needDAO = new NeedDAO(conn);
            Company selectedCompany = companyDAO.getCompanyByName(companyName);
            if (selectedCompany == null) {
                showAlert("Invalid orphanage selection!");
                return;
            }

            boolean success = needDAO.addNeed(selectedCompany.getId(), category, desc, "Pending");

            if (success) {
                loadNeeds();
                descField.clear();
                showAlert("Need added successfully!");
            } else {
                showAlert("Failed to add need.");
            }
        });

        getChildren().addAll(title, form, needsTable);
    }

    private void setupTable() {
        TableColumn<model.Need, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));

        TableColumn<model.Need, String> orphanageCol = new TableColumn<>("Orphanage");
        orphanageCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("orphanageName"));

        TableColumn<model.Need, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("category"));

        TableColumn<model.Need, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("description"));

        TableColumn<model.Need, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("status"));

        TableColumn<model.Need, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button markBtn = new Button("Mark Fulfilled");
            {
                markBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                markBtn.setOnAction(e -> {
                    model.Need need = getTableView().getItems().get(getIndex());
                    NeedDAO needDAO = new NeedDAO(conn);
                    if (needDAO.markNeedFulfilled(need.getId())) {
                        need.setStatus("Fulfilled");
                        getTableView().refresh();
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

        needsTable.getColumns().addAll(idCol, orphanageCol, categoryCol, descCol, statusCol, actionCol);
        needsTable.setPrefWidth(800);
        needsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadNeeds() {
        NeedDAO needDAO = new NeedDAO(conn);
        needsTable.setItems(javafx.collections.FXCollections.observableArrayList(needDAO.getAllNeeds()));
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}