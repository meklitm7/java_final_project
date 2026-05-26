package gui.screens;

import db.*;
import gui.SceneManager;
import model.CurrentUser;
import model.Donation;
import model.Need;
import model.Company;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.sql.Connection;
import java.util.List;

public class DonorDashboard extends BorderPane {
    private Stage stage;
    private Connection conn;
    private TableView<Need> needsTable;
    private StackPane content;

    public DonorDashboard(Stage stage) {
        this.stage = stage;
        this.conn = DBConnection.connect();
        setupUI();
    }

    private void setupUI() {
        // Header
        HBox header = new HBox(20);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #2c3e50;");

        Label title = new Label("Donor Dashboard - Welcome, " + CurrentUser.getName());
        title.setFont(Font.font("Arial", 20));
        title.setTextFill(Color.WHITE);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        logoutBtn.setOnAction(e -> {
            CurrentUser.clear();
            SceneManager.showLogin(stage);
        });

        header.getChildren().addAll(title, logoutBtn);
        HBox.setHgrow(logoutBtn, Priority.ALWAYS);
        header.setAlignment(Pos.CENTER_LEFT);

        // Sidebar
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #34495e; -fx-pref-width: 200;");

        Button donateBtn = new Button("  Donate Now");
        Button viewNeedsBtn = new Button("  View Needs");
        Button historyBtn = new Button("  My History");
        Button bankDetailsBtn = new Button("  Bank Details");
        Button logoutBtn2 = new Button("  Logout");

        styleButton(donateBtn, "#3498db");
        styleButton(viewNeedsBtn, "#2ecc71");
        styleButton(historyBtn, "#f39c12");
        styleButton(bankDetailsBtn, "#9b59b6");
        styleButton(logoutBtn2, "#e74c3c");

        sidebar.getChildren().addAll(
                new Label("Menu") {
                    {
                        setTextFill(Color.WHITE);
                    }
                },
                donateBtn, viewNeedsBtn, historyBtn, bankDetailsBtn, logoutBtn2);

        // Main content area
        content = new StackPane();
        content.setStyle("-fx-background-color: #ecf0f1;");

        // FIXED: Default view is now company details (not needs table)
        showCompanyDetails();

        // Button actions
        donateBtn.setOnAction(e -> showDonationForm());
        viewNeedsBtn.setOnAction(e -> showNeeds()); // FIXED: Now properly shows needs
        historyBtn.setOnAction(e -> showHistory());
        bankDetailsBtn.setOnAction(e -> showCompanyDetails());
        logoutBtn2.setOnAction(e -> {
            CurrentUser.clear();
            SceneManager.showLogin(stage);
        });

        setTop(header);
        setLeft(sidebar);
        setCenter(content);
    }

    private void styleButton(Button btn, String color) {
        btn.setStyle("-fx-background-color: " + color
                + "; -fx-text-fill: white; -fx-pref-width: 180px; -fx-pref-height: 40px; -fx-font-size: 14px;");
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle() + "; -fx-background-radius: 5;"));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace("; -fx-background-radius: 5;", "")));
    }

    private void setupNeedsTable() {
        TableColumn<Need, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Need, String> nameCol = new TableColumn<>("Item/Service");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Need, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Need, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Need, Integer> qtyCol = new TableColumn<>("Quantity Needed");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Need, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Need, String> orphanageCol = new TableColumn<>("Orphanage");
        orphanageCol.setCellValueFactory(new PropertyValueFactory<>("orphanageName"));

        needsTable.getColumns().addAll(idCol, nameCol, categoryCol, descCol, qtyCol, statusCol, orphanageCol);
        needsTable.setPrefWidth(900);
        needsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadNeeds() {
        NeedDAO needDAO = new NeedDAO(conn);
        needsTable.setItems(FXCollections.observableArrayList(needDAO.getAllNeeds()));
    }

    // FIXED: Now properly shows needs table
    private void showNeeds() {
        if (needsTable == null) {
            needsTable = new TableView<>();
            setupNeedsTable();
        }
        loadNeeds();

        VBox needsContainer = new VBox(10);
        needsContainer.setPadding(new Insets(20));
        needsContainer.setStyle("-fx-background-color: white; -fx-background-radius: 5;");

        Label title = new Label("Current Needs");
        title.setFont(Font.font("Arial", 18));
        title.setTextFill(Color.web("#2c3e50"));

        needsContainer.getChildren().addAll(title, needsTable);
        content.getChildren().setAll(needsContainer);
    }

    private void showDonationForm() {
        DonationForm donationForm = new DonationForm(stage, conn);
        content.getChildren().setAll(donationForm);
    }

    private void showHistory() {
        DonationDAO donationDAO = new DonationDAO(conn);
        List<Donation> donations = donationDAO.getDonorHistory(CurrentUser.getId());

        TableView<Donation> historyTable = new TableView<>();
        historyTable.setStyle("-fx-background-color: white;");

        TableColumn<Donation, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Donation, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Donation, String> itemCol = new TableColumn<>("Item");
        itemCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));

        TableColumn<Donation, Integer> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Donation, Double> amountCol = new TableColumn<>("Amount (ETB)");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<Donation, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Donation, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        historyTable.getColumns().addAll(idCol, typeCol, itemCol, qtyCol, amountCol, statusCol, dateCol);
        historyTable.setItems(FXCollections.observableArrayList(donations));
        historyTable.setPrefWidth(900);
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox historyContainer = new VBox(10);
        historyContainer.setPadding(new Insets(20));
        historyContainer.setStyle("-fx-background-color: white; -fx-background-radius: 5;");

        Label title = new Label("My Donation History");
        title.setFont(Font.font("Arial", 18));
        title.setTextFill(Color.web("#2c3e50"));

        historyContainer.getChildren().addAll(title, historyTable);
        content.getChildren().setAll(historyContainer);
    }

    // FIXED: Now shows owner names in bank details
    private void showCompanyDetails() {
        CompanyDAO companyDAO = new CompanyDAO(conn);
        List<Company> companies = companyDAO.listCompanies();

        VBox detailsBox = new VBox(15);
        detailsBox.setPadding(new Insets(20));
        detailsBox.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        Label title = new Label("Orphanage Bank Details for Donations");
        title.setFont(Font.font("Arial", 18));
        title.setTextFill(Color.web("#2c3e50"));

        for (Company company : companies) {
            VBox companyCard = new VBox(10);
            companyCard.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 5; -fx-padding: 15;");

            Label nameLabel = new Label(company.getName());
            nameLabel.setFont(Font.font("Arial", 16));
            nameLabel.setTextFill(Color.web("#2c3e50"));

            // FIXED: Added owner names
            Label accountLabel = new Label("🏦 Account: " + company.getAccountNumber() +
                    " | Owner: " + company.getAccountOwnerName());
            Label telebirrLabel = new Label("📱 Telebirr: " + company.getTelebirrPhone() +
                    " | Owner: " + company.getTelebirrOwnerName());
            Label telegramLabel = new Label("💬 Admin Telegram: @" + company.getAdminTelegram());

            Button donateBtn = new Button("Donate to " + company.getName());
            donateBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
            donateBtn.setOnAction(e -> showDonationForm());

            companyCard.getChildren().addAll(
                    nameLabel,
                    new Separator(),
                    accountLabel,
                    telebirrLabel,
                    telegramLabel,
                    new Separator(),
                    donateBtn);

            detailsBox.getChildren().add(companyCard);
        }

        content.getChildren().setAll(detailsBox);
    }
}