package gui.screens;

import db.*;
import gui.SceneManager;
import model.CurrentUser;
import model.Donation;
import model.User;
import model.Company;
import model.VolunteerTaskAssignment;
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

 
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

public class AdminDashboard extends BorderPane {
    private Stage stage;
    private Connection conn;
    private TableView<Donation> pendingTable;
    private StackPane content;

    public AdminDashboard(Stage stage) {
        this.stage = stage;
        this.conn = DBConnection.connect();
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

        Button verifyBtn = new Button("  Verify Donations");
        Button needsBtn = new Button("  Manage Needs");
        Button volunteersBtn = new Button("  View Volunteers");
        Button approveVolunteersBtn = new Button("  Approve Volunteers");
        Button manageTasksBtn = new Button("  Manage Tasks");
        Button donorsBtn = new Button("  View Donors");
        Button reportsBtn = new Button("  Generate Reports");
        Button adminDetailsBtn = new Button("  Admin Details");
        Button logoutBtn2 = new Button("  Logout");

        styleButton(verifyBtn, "#3498db");
        styleButton(needsBtn, "#2ecc71");
        styleButton(volunteersBtn, "#f39c12");
        styleButton(approveVolunteersBtn, "#1abc9c");
        styleButton(manageTasksBtn, "#3498db");
        styleButton(donorsBtn, "#9b59b6");
        styleButton(reportsBtn, "#e67e22");
        styleButton(adminDetailsBtn, "#8e44ad");
        styleButton(logoutBtn2, "#e74c3c");

        sidebar.getChildren().addAll(
                new Label("Admin Menu") {
                    {
                        setTextFill(Color.WHITE);
                    }
                },
                verifyBtn, needsBtn, volunteersBtn, approveVolunteersBtn, manageTasksBtn,
                donorsBtn, reportsBtn, adminDetailsBtn, logoutBtn2);

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

    private void setupPendingTable() {
        TableColumn<Donation, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Donation, String> donorCol = new TableColumn<>("Donor");
        donorCol.setCellValueFactory(cellData -> {
            try {
                UserDAO userDAO = new UserDAO(conn);
                User user = userDAO.getUserById(cellData.getValue().getDonorId());
                return new javafx.beans.property.SimpleStringProperty(user != null ? user.getName() : "Unknown");
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("Unknown");
            }
        });

        TableColumn<Donation, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Donation, String> itemCol = new TableColumn<>("Item");
        itemCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));

        TableColumn<Donation, Integer> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Donation, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<Donation, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Donation, Void> screenshotCol = new TableColumn<>("Receipt");
        screenshotCol.setCellFactory(param -> new TableCell<Donation, Void>() {
    private final Button viewBtn = new Button("View");
    {
        viewBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        viewBtn.setOnAction(e -> {
            Donation donation = getTableView().getItems().get(getIndex());
            // FIX: Use getScreenshot() (not getScreenshotPath)
            if (donation.getScreenshot() != null && !donation.getScreenshot().isEmpty()) {
                showScreenshot(donation.getScreenshot());
            } else {
                showAlert("⚠️ No receipt uploaded for this donation.");
            }
        });
    }
     

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Donation donation = getTableView().getItems().get(getIndex());
                    if ("Money".equals(donation.getType()) && donation.getScreenshot() != null) {
                        setGraphic(viewBtn);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        TableColumn<Donation, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(param -> new TableCell<Donation, Void>() {
            private final Button approveBtn = new Button("Approve");
            private final Button rejectBtn = new Button("Reject");
            private final HBox buttons = new HBox(5, approveBtn, rejectBtn);

            {
                approveBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
                rejectBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

                approveBtn.setOnAction(e -> {
                    Donation donation = getTableView().getItems().get(getIndex());
                    DonationDAO donationDAO = new DonationDAO(conn);
                    if (donationDAO.approveDonation(donation.getId())) {
                        donation.setStatus("Approved");
                        getTableView().refresh();
                    }
                });

                rejectBtn.setOnAction(e -> {
                    Donation donation = getTableView().getItems().get(getIndex());
                    DonationDAO donationDAO = new DonationDAO(conn);
                    if (donationDAO.rejectDonation(donation.getId())) {
                        donation.setStatus("Rejected");
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
                    setGraphic(buttons);
                }
            }
        });

        pendingTable.getColumns().addAll(idCol, donorCol, typeCol, itemCol, qtyCol, amountCol, statusCol, screenshotCol,
                actionCol);
        pendingTable.setPrefWidth(1000);
        pendingTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void showVerifyDonations() {
        loadPendingDonations();
        content.getChildren().setAll(pendingTable);
    }

    private void loadPendingDonations() {
        DonationDAO donationDAO = new DonationDAO(conn);
        pendingTable.setItems(FXCollections.observableArrayList(donationDAO.getPendingDonations()));
    }

  private void showScreenshot(String imagePath) {
    try {
        // Debug output (keep these)
        System.out.println("[DEBUG] Raw path from DB: " + imagePath);
        String path = imagePath.replace("\\", "/");
        System.out.println("[DEBUG] Normalized path: " + path);
        File projectRoot = new File("").getAbsoluteFile();
        System.out.println("[DEBUG] Project root: " + projectRoot.getAbsolutePath());
        File imageFile = new File(projectRoot, path);
        System.out.println("[DEBUG] Trying to load: " + imageFile.getAbsolutePath());
        System.out.println("[DEBUG] File exists: " + imageFile.exists());
        System.out.println("[DEBUG] Can read: " + imageFile.canRead());

        if (!imageFile.exists()) {
            imageFile = new File(imagePath);
            System.out.println("[DEBUG] Trying original path: " + imageFile.getAbsolutePath());
            if (!imageFile.exists()) {
                showAlert("❌ Receipt not found at:\n" + path);
                return;
            }
        }

        // FIX: Use FileInputStream + Image constructor (no SwingFXUtils needed)
        try (FileInputStream fis = new FileInputStream(imageFile)) {
            Image image = new Image(fis);

            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(600);
            imageView.setFitHeight(600);
            imageView.setPreserveRatio(true);

            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.initOwner(stage);
            popup.setTitle("Donation Receipt - " + imageFile.getName());

            ScrollPane scroll = new ScrollPane(imageView);
            scroll.setFitToWidth(true);
            scroll.setFitToHeight(true);

            StackPane root = new StackPane(scroll);
            root.setStyle("-fx-background-color: white; -fx-padding: 10;");

            Scene scene = new Scene(root, 650, 700);
            popup.setScene(scene);
            popup.showAndWait();
        }

    } catch (IOException e) {
        System.out.println("[DEBUG] IOException: " + e.getMessage());
        showAlert("❌ Error loading image: " + e.getMessage());
    } catch (Exception e) {
        System.out.println("[DEBUG] General Exception: " + e.getMessage());
        showAlert("❌ Unexpected error: " + e.getMessage());
    }
}
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showNeedsManagement() {
        NeedManagementPanel needsPanel = new NeedManagementPanel(conn);
        content.getChildren().setAll(needsPanel);
    }

    private void showVolunteers() {
        VolunteerDAO volunteerDAO = new VolunteerDAO(conn);
        List<model.Volunteer> volunteers = volunteerDAO.getAllVolunteers();

        TableView<model.Volunteer> table = new TableView<>();
        TableColumn<model.Volunteer, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<model.Volunteer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<model.Volunteer, String> contactCol = new TableColumn<>("Contact");
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));

        TableColumn<model.Volunteer, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(idCol, nameCol, contactCol, statusCol);
        table.setItems(FXCollections.observableArrayList(volunteers));
        table.setPrefWidth(800);

        content.getChildren().setAll(table);
    }

    private void showApproveVolunteers() {
        VBox mainBox = new VBox(15);
        mainBox.setPadding(new Insets(20));
        mainBox.setStyle("-fx-background-color: #ecf0f1;");

        Label title = new Label("Approve Volunteer Task Assignments");
        title.setFont(Font.font("Arial", 20));
        title.setTextFill(Color.web("#2c3e50"));

        TableView<VolunteerTaskAssignment> table = new TableView<>();
        setupVolunteerApprovalTable(table);
        loadPendingVolunteerAssignments(table);

        Label msg = new Label();
        msg.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

        mainBox.getChildren().addAll(title, table, msg);
        content.getChildren().setAll(mainBox);
    }

    private void setupVolunteerApprovalTable(TableView<VolunteerTaskAssignment> table) {
        TableColumn<VolunteerTaskAssignment, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<VolunteerTaskAssignment, String> volunteerCol = new TableColumn<>("Volunteer");
        volunteerCol.setCellValueFactory(new PropertyValueFactory<>("volunteerName"));

        TableColumn<VolunteerTaskAssignment, String> taskCol = new TableColumn<>("Task");
        taskCol.setCellValueFactory(new PropertyValueFactory<>("taskName"));

        TableColumn<VolunteerTaskAssignment, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("taskCategory"));

        TableColumn<VolunteerTaskAssignment, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getAssignmentDate().toString()));

        TableColumn<VolunteerTaskAssignment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<VolunteerTaskAssignment, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(param -> new TableCell<VolunteerTaskAssignment, Void>() {
            private final Button approveBtn = new Button("Approve");
            private final Button rejectBtn = new Button("Reject");
            private final HBox buttons = new HBox(5, approveBtn, rejectBtn);

            {
                approveBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
                rejectBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

                approveBtn.setOnAction(e -> {
                    VolunteerTaskAssignment assignment = getTableView().getItems().get(getIndex());
                    VolunteerTaskAssignmentDAO dao = new VolunteerTaskAssignmentDAO(conn);
                    if (dao.approveAssignment(assignment.getId())) {
                        assignment.setStatus("Approved");
                        getTableView().refresh();
                    }
                });

                rejectBtn.setOnAction(e -> {
                    VolunteerTaskAssignment assignment = getTableView().getItems().get(getIndex());
                    VolunteerTaskAssignmentDAO dao = new VolunteerTaskAssignmentDAO(conn);
                    if (dao.rejectAssignment(assignment.getId())) {
                        assignment.setStatus("Rejected");
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
                    setGraphic(buttons);
                }
            }
        });

        table.getColumns().addAll(idCol, volunteerCol, taskCol, categoryCol, dateCol, statusCol, actionCol);
        table.setPrefWidth(1000);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadPendingVolunteerAssignments(TableView<VolunteerTaskAssignment> table) {
        VolunteerTaskAssignmentDAO dao = new VolunteerTaskAssignmentDAO(conn);
        table.setItems(FXCollections.observableArrayList(dao.getPendingAssignments()));
    }

    private void showManageTasks() {
        try {
            VBox mainBox = new VBox(15);
            mainBox.setPadding(new Insets(20));
            mainBox.setStyle("-fx-background-color: #ecf0f1;");

            Label title = new Label("Manage Volunteer Tasks");
            title.setFont(Font.font("Arial", 20));
            title.setTextFill(Color.web("#2c3e50"));

            HBox filterBox = new HBox(10);
            filterBox.setAlignment(Pos.CENTER_LEFT);
            Label categoryLabel = new Label("Filter by Category:");
            ComboBox<String> categoryCombo = new ComboBox<>();
            categoryCombo.getItems().addAll("All", "Home Chores", "Children Care", "Teaching");
            categoryCombo.setValue("All");

            TableView<model.VolunteerTask> tasksTable = new TableView<>();
            setupTasksTable(tasksTable);
            loadTasksInManagePanel(tasksTable, categoryCombo.getValue());

            categoryCombo.setOnAction(e -> loadTasksInManagePanel(tasksTable, categoryCombo.getValue()));

            filterBox.getChildren().addAll(categoryLabel, categoryCombo);

            HBox addForm = new HBox(10);
            addForm.setAlignment(Pos.CENTER_LEFT);
            TextField taskNameField = new TextField();
            taskNameField.setPromptText("Task Name");
            ComboBox<String> taskCategoryCombo = new ComboBox<>();
            taskCategoryCombo.getItems().addAll("Home Chores", "Children Care", "Teaching");
            TextField descField = new TextField();
            descField.setPromptText("Description");
            Button addBtn = new Button("Add Task");
            addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");

            addForm.getChildren().addAll(
                    new Label("Name:"), taskNameField,
                    new Label("Category:"), taskCategoryCombo,
                    new Label("Description:"), descField,
                    addBtn);

            HBox assignForm = new HBox(10);
            assignForm.setAlignment(Pos.CENTER_LEFT);
            ComboBox<String> volunteerCombo = new ComboBox<>();
            loadVolunteers(volunteerCombo);
            ComboBox<String> taskCombo = new ComboBox<>();
            loadTaskNames(taskCombo);
            DatePicker datePicker = new DatePicker();
            Button assignBtn = new Button("Assign Task");
            assignBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

            assignForm.getChildren().addAll(
                    new Label("Volunteer:"), volunteerCombo,
                    new Label("Task:"), taskCombo,
                    new Label("Date:"), datePicker,
                    assignBtn);

            Label msg = new Label();
            msg.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

            addBtn.setOnAction(e -> {
                String name = taskNameField.getText();
                String category = taskCategoryCombo.getValue();
                String desc = descField.getText();

                if (name.isEmpty() || category == null) {
                    msg.setText("Name and Category are required!");
                    return;
                }

                VolunteerTaskDAO dao = new VolunteerTaskDAO(conn);
                if (dao.addTask(name, category, desc)) {
                    msg.setText("Task added successfully!");
                    loadTasksInManagePanel(tasksTable, categoryCombo.getValue());
                    loadTaskNames(taskCombo);
                    taskNameField.clear();
                    descField.clear();
                } else {
                    msg.setText("Failed to add task.");
                }
            });

            assignBtn.setOnAction(e -> {
                String volunteerName = volunteerCombo.getValue();
                String taskName = taskCombo.getValue();
                java.time.LocalDate date = datePicker.getValue();

                if (volunteerName == null || taskName == null || date == null) {
                    msg.setText("All fields are required!");
                    return;
                }

                int volunteerId = getVolunteerIdByName(volunteerName);
                int taskId = getTaskIdByName(taskName);

                if (volunteerId <= 0 || taskId <= 0) {
                    msg.setText("Invalid volunteer or task selection!");
                    return;
                }

                VolunteerTaskAssignmentDAO dao = new VolunteerTaskAssignmentDAO(conn);
                if (dao.assignTask(volunteerId, taskId, date, CurrentUser.getId())) {
                    msg.setText("Task assigned successfully! Pending approval.");
                    loadTasksInManagePanel(tasksTable, categoryCombo.getValue());
                } else {
                    msg.setText("Failed to assign task.");
                }
            });

            mainBox.getChildren().addAll(
                    title, filterBox, addForm, new Separator(),
                    new Label("Current Tasks:") {
                        {
                            setStyle("-fx-font-weight: bold;");
                        }
                    },
                    tasksTable, new Separator(),
                    new Label("Assign Task to Volunteer:") {
                        {
                            setStyle("-fx-font-weight: bold;");
                        }
                    },
                    assignForm, msg);

            content.getChildren().setAll(mainBox);
        } catch (Exception e) {
            System.out.println("Error in showManageTasks: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadTasksInManagePanel(TableView<model.VolunteerTask> table, String category) {
        try {
            VolunteerTaskDAO dao = new VolunteerTaskDAO(conn);
            List<model.VolunteerTask> tasks = category.equals("All") ? dao.getAllTasks()
                    : dao.getTasksByCategory(category);
            table.setItems(FXCollections.observableArrayList(tasks));
        } catch (Exception e) {
            System.out.println("Error loading tasks in manage panel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupTasksTable(TableView<model.VolunteerTask> table) {
        TableColumn<model.VolunteerTask, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<model.VolunteerTask, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<model.VolunteerTask, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<model.VolunteerTask, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<model.VolunteerTask, Void> deleteCol = new TableColumn<>("Delete");
        deleteCol.setCellFactory(param -> new TableCell<model.VolunteerTask, Void>() {
            private final Button deleteBtn = new Button("Delete");
            {
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                deleteBtn.setOnAction(e -> {
                    model.VolunteerTask task = getTableView().getItems().get(getIndex());
                    VolunteerTaskDAO dao = new VolunteerTaskDAO(conn);
                    if (dao.deleteTask(task.getId())) {
                        getTableView().getItems().remove(task);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteBtn);
                }
            }
        });

        table.getColumns().addAll(idCol, nameCol, categoryCol, descCol, deleteCol);
        table.setPrefWidth(800);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadVolunteers(ComboBox<String> combo) {
        VolunteerDAO dao = new VolunteerDAO(conn);
        for (model.Volunteer v : dao.getAllVolunteers()) {
            combo.getItems().add(v.getName());
        }
    }

    private void loadTaskNames(ComboBox<String> combo) {
        VolunteerTaskDAO dao = new VolunteerTaskDAO(conn);
        combo.getItems().clear();
        for (model.VolunteerTask t : dao.getAllTasks()) {
            combo.getItems().add(t.getName());
        }
    }

    private int getVolunteerIdByName(String name) {
        VolunteerDAO dao = new VolunteerDAO(conn);
        for (model.Volunteer v : dao.getAllVolunteers()) {
            if (v.getName().equals(name)) {
                return v.getId();
            }
        }
        return -1;
    }

    private int getTaskIdByName(String name) {
        VolunteerTaskDAO dao = new VolunteerTaskDAO(conn);
        for (model.VolunteerTask t : dao.getAllTasks()) {
            if (t.getName().equals(name)) {
                return t.getId();
            }
        }
        return -1;
    }

    private void showDonors() {
        UserDAO userDAO = new UserDAO(conn);
        List<User> donors = userDAO.getUsersByRole("Donor");

        TableView<User> table = new TableView<>();
        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<User, String> telegramCol = new TableColumn<>("Telegram");
        telegramCol.setCellValueFactory(new PropertyValueFactory<>("telegramUsername"));

        table.getColumns().addAll(idCol, nameCol, emailCol, telegramCol);
        table.setItems(FXCollections.observableArrayList(donors));
        table.setPrefWidth(800);

        content.getChildren().setAll(table);
    }

    private void showReports() {
        ReportPanel reportPanel = new ReportPanel(conn);
        content.getChildren().setAll(reportPanel);
    }

    private void showAdminDetails() {
        VBox detailsBox = new VBox(15);
        detailsBox.setPadding(new Insets(20));
        detailsBox.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        Label title = new Label("Admin Information");
        title.setFont(Font.font("Arial", 18));
        title.setTextFill(Color.web("#2c3e50"));

        UserDAO userDAO = new UserDAO(conn);
        List<User> admins = userDAO.getUsersByRole("Admin");

        for (User admin : admins) {
            VBox adminCard = new VBox(5);
            adminCard.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 5; -fx-padding: 10;");

            Label nameLabel = new Label("Name: " + admin.getName());
            Label emailLabel = new Label("Email: " + admin.getEmail());
            Label telegramLabel = new Label("Telegram: @" + admin.getTelegramUsername());

            adminCard.getChildren().addAll(nameLabel, emailLabel, telegramLabel);
            detailsBox.getChildren().add(adminCard);
        }

        CompanyDAO companyDAO = new CompanyDAO(conn);
        List<Company> companies = companyDAO.listCompanies();

        Label companiesTitle = new Label("Company Bank Details");
        companiesTitle.setFont(Font.font("Arial", 18));
        companiesTitle.setTextFill(Color.web("#2c3e50"));
        companiesTitle.setStyle("-fx-margin-top: 20;");

        for (Company company : companies) {
            VBox companyCard = new VBox(5);
            companyCard.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 5; -fx-padding: 10;");

            Label companyName = new Label("Company: " + company.getName());
            companyName.setFont(Font.font("Arial", 14));
            Label accountLabel = new Label("Account: " + company.getAccountNumber());
            Label telebirrLabel = new Label("Telebirr: " + company.getTelebirrPhone());
            Label adminLabel = new Label("Admin Telegram: @" + company.getAdminTelegram());

            companyCard.getChildren().addAll(companyName, accountLabel, telebirrLabel, adminLabel);
            detailsBox.getChildren().add(companyCard);
        }

        content.getChildren().setAll(detailsBox);
    }
}