package gui.screens;

import gui.SceneManager;
import gui.NotificationPopup;
import model.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.application.Platform;
import rmi.TesfaRMIClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class AdminDashboard extends BorderPane {
    private Stage stage;
    private TableView<Donation> pendingTable;
    private StackPane content;
    private NotificationPopup notificationPopup;

    public AdminDashboard(Stage stage) {
        this.stage = stage;
        setupUI();
    }

    private void setupUI() {
        // Header
        HBox header = new HBox(20);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #2c3e50;");

        Label title = new Label("Admin Dashboard - Welcome, " + CurrentUser.getName());
        title.setFont(Font.font("Arial", 20));
        title.setTextFill(Color.WHITE);

        // Notification bell
        Label notificationBell = new Label("🔔");
        notificationBell.setFont(Font.font("Arial", 20));
        notificationBell.setTextFill(Color.WHITE);
        notificationBell.setOnMouseClicked(e -> {
            if (TesfaRMIClient.isConnected()) {
                Platform.runLater(() -> {
                    notificationPopup = new NotificationPopup(stage, CurrentUser.getId());
                    notificationPopup.show();
                });
            } else {
                showAlert("Not connected to RMI server. Please try again.");
            }
        });

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        logoutBtn.setOnAction(e -> {
            CurrentUser.clear();
            SceneManager.showLogin(stage);
        });

        header.getChildren().addAll(title, notificationBell, logoutBtn);
        HBox.setHgrow(logoutBtn, Priority.ALWAYS);
        header.setAlignment(Pos.CENTER_LEFT);

        // Sidebar
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #34495e; -fx-pref-width: 200;");

        Button verifyBtn = new Button("  Verify Donations");
        Button needsBtn = new Button("  Manage Needs");
        Button volunteersBtn = new Button("  View Volunteers");
        Button approveVolunteersBtn = new Button("  Approve Volunteers");
        Button manageTasksBtn = new Button("  Manage Tasks");
        Button donorsBtn = new Button("  View Donors");
        Button reportsBtn = new Button("  Generate Reports");
        Button adminDetailsBtn = new Button("  Admin Details");
        Button volunteerRequestsBtn = new Button("  Volunteer Requests");
        Button logoutBtn2 = new Button("  Logout");

        styleButton(verifyBtn, "#3498db");
        styleButton(needsBtn, "#2ecc71");
        styleButton(volunteersBtn, "#f39c12");
        styleButton(approveVolunteersBtn, "#1abc9c");
        styleButton(manageTasksBtn, "#3498db");
        styleButton(donorsBtn, "#9b59b6");
        styleButton(reportsBtn, "#e67e22");
        styleButton(adminDetailsBtn, "#8e44ad");
        styleButton(volunteerRequestsBtn, "#e67e22");
        styleButton(logoutBtn2, "#e74c3c");

        sidebar.getChildren().addAll(
            new Label("Admin Menu") {
                {
                    setTextFill(Color.WHITE);
                }
            },
            verifyBtn, needsBtn, volunteersBtn, approveVolunteersBtn, manageTasksBtn,
            donorsBtn, reportsBtn, adminDetailsBtn, volunteerRequestsBtn, logoutBtn2
        );

        // Main content
        content = new StackPane();
        content.setStyle("-fx-background-color: #ecf0f1;");

        pendingTable = new TableView<>();
        setupPendingTable();
        content.getChildren().add(pendingTable);

        // Button actions
        verifyBtn.setOnAction(e -> showVerifyDonations());
        needsBtn.setOnAction(e -> showNeedsManagement());
        volunteersBtn.setOnAction(e -> showVolunteers());
        approveVolunteersBtn.setOnAction(e -> showApproveVolunteers());
        manageTasksBtn.setOnAction(e -> showManageTasks());
        donorsBtn.setOnAction(e -> showDonors());
        reportsBtn.setOnAction(e -> showReports());
        adminDetailsBtn.setOnAction(e -> showAdminDetails());
        volunteerRequestsBtn.setOnAction(e -> showVolunteerRequests());
        logoutBtn2.setOnAction(e -> {
            CurrentUser.clear();
            SceneManager.showLogin(stage);
        });

        setTop(header);
        setLeft(sidebar);
        setCenter(content);
    }

    private void styleButton(Button btn, String color) {
        btn.setStyle("-fx-background-color: " + color +
            "; -fx-text-fill: white; -fx-pref-width: 180px; -fx-pref-height: 40px; -fx-font-size: 14px;");
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle() + "; -fx-background-radius: 5;"));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace("; -fx-background-radius: 5;", "")));
    }

    private void setupPendingTable() {
        TableColumn<Donation, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Donation, String> donorCol = new TableColumn<>("Donor");
        donorCol.setCellValueFactory(cellData -> {
            try {