package gui.screens;

import gui.SceneManager;
import gui.NotificationPopup;
import model.CurrentUser;
import model.VolunteerTask;
import model.VolunteerTaskAssignment;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.application.Platform;
import rmi.TesfaRMIClient;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VolunteerDashboard extends BorderPane {
    private Stage stage;
    private Set<Integer> selectedTaskIds = new HashSet<>();
    private TableView<VolunteerTask> tasksTable;
    private ComboBox<String> categoryCombo;
    private DatePicker datePicker;
    private NotificationPopup notificationPopup;

    public VolunteerDashboard(Stage stage) {
        this.stage = stage;
        setupUI();
    }

    private void setupUI() {
        // Header
        HBox header = new HBox(20);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #2c3e50;");

        Label titleLabel = new Label("Volunteer Dashboard - Welcome, " + CurrentUser.getName());
        titleLabel.setFont(Font.font("Arial", 20));
        titleLabel.setTextFill(Color.WHITE);

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

        header.getChildren().addAll(titleLabel, notificationBell, logoutBtn);
        HBox.setHgrow(logoutBtn, Priority.ALWAYS);
        header.setAlignment(Pos.CENTER_LEFT);

        // Sidebar
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #34495e; -fx-pref-width: 200;");

        Button viewTasksBtn = new Button("  View Available Tasks");
        Button myAssignmentsBtn = new Button("  My Assignments");
        Button selectTasksBtn = new Button("  Select Tasks");
        Button makeRequestBtn = new Button("  Make Request");
        Button logoutBtn2 = new Button("  Logout");

        styleButton(viewTasksBtn, "#3498db");
        styleButton(myAssignmentsBtn, "#2ecc71");
        styleButton(selectTasksBtn, "#f39c12");
        styleButton(makeRequestBtn, "#9b59b6");
        styleButton(logoutBtn2, "#e74c3c");

        sidebar.getChildren().addAll(
                new Label("Menu") {
                    {
                        setTextFill(Color.WHITE);
                    }
                },
                viewTasksBtn, myAssignmentsBtn, selectTasksBtn, makeRequestBtn, logoutBtn2);

        // Main content
        StackPane content = new StackPane();
        content.setStyle("-fx-background-color: #ecf0f1;");

        // Default: Show available tasks
        showAvailableTasks(content);

        // Button actions
        viewTasksBtn.setOnAction(e -> showAvailableTasks(content));
        myAssignmentsBtn.setOnAction(e -> showMyAssignments(content));
        selectTasksBtn.setOnAction(e -> showTaskSelection(content));
        makeRequestBtn.setOnAction(e -> showMakeRequest(content));
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

    // ========== VIEW AVAILABLE TASKS ==========
    private void showAvailableTasks(StackPane content) {
        VBox tasksBox = new VBox(15);
        tasksBox.setPadding(new Insets(20));

        Label title = new Label("Available Volunteer Tasks");
        title.setFont(Font.font("Arial", 18));
        title.setTextFill(Color.web("#2c3e50"));

        // Category filter
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        Label categoryLabel = new Label("Filter by Category:");
        categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll("All", "Home Chores", "Children Care", "Teaching");
        categoryCombo.setValue("All");
        categoryCombo.setOnAction(e -> loadTasks());

        filterBox.getChildren().addAll(categoryLabel, categoryCombo);

        // Tasks table
        tasksTable = new TableView<>();
        setupTasksTable();
        loadTasks();

        tasksBox.getChildren().addAll(title, filterBox, tasksTable);
        content.getChildren().setAll(tasksBox);
    }

    private void setupTasksTable() {
        TableColumn<VolunteerTask, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<VolunteerTask, String> nameCol = new TableColumn<>("Task");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<VolunteerTask, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<VolunteerTask, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        tasksTable.getColumns().addAll(idCol, nameCol, categoryCol, descCol);
        tasksTable.setPrefWidth(800);
        tasksTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadTasks() {
        if (TesfaRMIClient.isConnected()) {
            String category = categoryCombo.getValue();
            List<VolunteerTask> tasks = category.equals("All") ?
                TesfaRMIClient.getAllVolunteerTasks() :
                filterTasksByCategory(TesfaRMIClient.getAllVolunteerTasks(), category);
            tasksTable.setItems(FXCollections.observableArrayList(tasks));
        } else {
            showAlert("Not connected to RMI server. Please try again.");
        }
    }

    private List<VolunteerTask> filterTasksByCategory(List<VolunteerTask> tasks, String category) {
        return tasks.stream()
                .filter(task -> task.getCategory().equals(category))
                .toList();
    }

    // ========== MY ASSIGNMENTS ==========
    private void showMyAssignments(StackPane content) {
        VBox assignmentsBox = new VBox(15);
        assignmentsBox.setPadding(new Insets(20));

        Label title = new Label("My Task Assignments");
        title.setFont(Font.font("Arial", 18));
        title.setTextFill(Color.web("#2c3e50"));

        TableView<VolunteerTaskAssignment> assignmentsTable = new TableView<>();
        setupAssignmentsTable(assignmentsTable);
        loadMyAssignments(assignmentsTable);

        assignmentsBox.getChildren().addAll(title, assignmentsTable);
        content.getChildren().setAll(assignmentsBox);
    }

    private void setupAssignmentsTable(TableView<VolunteerTaskAssignment> table) {
        TableColumn<VolunteerTaskAssignment, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<VolunteerTaskAssignment, String> taskCol = new TableColumn<>("Task");
        taskCol.setCellValueFactory(new PropertyValueFactory<>("taskName"));

        TableColumn<VolunteerTaskAssignment, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("taskCategory"));

        TableColumn<VolunteerTaskAssignment, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getAssignmentDate().toString()
            )
        );

        TableColumn<VolunteerTaskAssignment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(idCol, taskCol, categoryCol, dateCol, statusCol);
        table.setPrefWidth(800);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadMyAssignments(TableView<VolunteerTaskAssignment> table) {
        if (TesfaRMIClient.isConnected()) {
            List<VolunteerTaskAssignment> assignments = TesfaRMIClient.getTaskAssignmentsByVolunteer(CurrentUser.getId());
            table.setItems(FXCollections.observableArrayList(assignments));
        } else {
            showAlert("Not connected to RMI server. Please try again.");
        }
    }

    // ========== SELECT TASKS ==========
    private void showTaskSelection(StackPane content) {
        VBox selectionBox = new VBox(15);
        selectionBox.setPadding(new Insets(20));

        Label title = new Label("Select Tasks You Can Do");
        title.setFont(Font.font("Arial", 18));
        title.setTextFill(Color.web("#2c3e50"));

        // Category filter
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        Label categoryLabel = new Label("Filter by Category:");
        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll("All", "Home Chores", "Children Care", "Teaching");
        categoryCombo.setValue("All");

        // Create table
        TableView<VolunteerTask> selectionTable = new TableView<>();
        setupSelectionTable(selectionTable);

        // Load data into the table
        loadTasksForSelection(selectionTable, categoryCombo.getValue());

        categoryCombo.setOnAction(e -> loadTasksForSelection(selectionTable, categoryCombo.getValue()));

        filterBox.getChildren().addAll(categoryLabel, categoryCombo);

        // Date picker
        HBox dateBox = new HBox(10);
        dateBox.setAlignment(Pos.CENTER_LEFT);
        datePicker = new DatePicker();
        dateBox.getChildren().addAll(new Label("Select Date:"), datePicker);

        // Submit button
        Button submitBtn = new Button("Submit Selected Tasks");
        submitBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");

        Label msg = new Label();
        msg.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

        submitBtn.setOnAction(e -> {
            LocalDate selectedDate = datePicker.getValue();
            if (selectedDate == null) {
                msg.setText("Please select a date!");
                return;
            }

            if (selectedTaskIds.isEmpty()) {
                msg.setText("Please select at least one task!");
                return;
            }

            if (TesfaRMIClient.isConnected()) {
                boolean allAssigned = true;
                for (Integer taskId : selectedTaskIds) {
                    boolean success = TesfaRMIClient.assignTask(
                        CurrentUser.getId(),
                        taskId,
                        selectedDate,
                        CurrentUser.getId()
                    );
                    if (!success) {
                        allAssigned = false;
                        break;
                    }
                }

                if (allAssigned) {
                    msg.setText("✅ " + selectedTaskIds.size() + " tasks submitted for approval!");
                    selectedTaskIds.clear();
                    loadTasksForSelection(selectionTable, categoryCombo.getValue());
                } else {
                    msg.setText("❌ Failed to submit some tasks. Please try again.");
                }
            } else {
                msg.setText("Not connected to RMI server. Please try again.");
            }
        });

        selectionBox.getChildren().addAll(
            title, filterBox, selectionTable, dateBox, submitBtn, msg
        );
        content.getChildren().setAll(selectionBox);
    }

    private void setupSelectionTable(TableView<VolunteerTask> table) {
        TableColumn<VolunteerTask, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<VolunteerTask, String> nameCol = new TableColumn<>("Task");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<VolunteerTask, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<VolunteerTask, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Checkbox column
        TableColumn<VolunteerTask, Boolean> selectCol = new TableColumn<>("Select");
        selectCol.setCellFactory(param -> new TableCell<VolunteerTask, Boolean>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(e -> {
                    VolunteerTask task = getTableView().getItems().get(getIndex());
                    if (checkBox.isSelected()) {
                        selectedTaskIds.add(task.getId());
                    } else {
                        selectedTaskIds.remove(task.getId());
                    }
                });
            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(checkBox);
                    // Set checkbox state based on selection
                    checkBox.setSelected(selectedTaskIds.contains(getTableView().getItems().get(getIndex()).getId()));
                }
            }
        });

        table.getColumns().addAll(selectCol, idCol, nameCol, categoryCol, descCol);
        table.setPrefWidth(800);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadTasksForSelection(TableView<VolunteerTask> table, String category) {
        if (TesfaRMIClient.isConnected()) {
            List<VolunteerTask> tasks = category.equals("All") ?
                TesfaRMIClient.getAllVolunteerTasks() :
                filterTasksByCategory(TesfaRMIClient.getAllVolunteerTasks(), category);
            table.setItems(FXCollections.observableArrayList(tasks));
        } else {
            showAlert("Not connected to RMI server. Please try again.");
        }
    }

    // ========== MAKE REQUEST ==========
    private void showMakeRequest(StackPane content) {
        VBox requestBox = new VBox(15);
        requestBox.setPadding(new Insets(20));
        requestBox.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 5;");

        Label titleLabel = new Label("Make a Request to Admin");
        titleLabel.setFont(Font.font("Arial", 18));
        titleLabel.setTextFill(Color.web("#2c3e50"));

        // Request type
        HBox typeBox = new HBox(10);
        typeBox.setAlignment(Pos.CENTER_LEFT);
        Label typeLabel = new Label("Request Type:");
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Task Assignment", "Leave", "Equipment", "Other");
        typeCombo.setValue("Task Assignment");
        typeBox.getChildren().addAll(typeLabel, typeCombo);

        // Title
        HBox titleBox = new HBox(10);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        Label titleFieldLabel = new Label("Title:");
        TextField titleField = new TextField();
        titleField.setPromptText("Brief title for your request");
        titleBox.getChildren().addAll(titleFieldLabel, titleField);

        // Message
        HBox messageBox = new HBox(10);
        messageBox.setAlignment(Pos.CENTER_LEFT);
        Label messageLabel = new Label("Message:");
        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Describe your request in detail");
        messageArea.setPrefRowCount(5);
        messageArea.setPrefWidth(400);
        messageBox.getChildren().addAll(messageLabel, messageArea);

        // Submit button
        Button submitBtn = new Button("Submit Request");
        submitBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");

        Label msg = new Label();
        msg.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

        submitBtn.setOnAction(e -> {
            String requestType = typeCombo.getValue();
            String requestTitle = titleField.getText();
            String requestMessage = messageArea.getText();

            if (requestType == null || requestTitle == null || requestTitle.isEmpty() || requestMessage == null || requestMessage.isEmpty()) {
                msg.setText("All fields are required!");
                return;
            }

            // FINAL FIX: Using TesfaRMIClient.addVolunteerRequest
            if (TesfaRMIClient.isConnected()) {
                boolean success = TesfaRMIClient.addVolunteerRequest(
                    CurrentUser.getId(),
                    requestType,
                    requestTitle,
                    requestMessage
                );

                if (success) {
                    msg.setText("✅ Request submitted successfully! Admins will review it soon.");
                    titleField.clear();
                    messageArea.clear();
                } else {
                    msg.setText("❌ Failed to submit request. Please try again.");
                }
            } else {
                msg.setText("Not connected to RMI server. Please try again.");
            }
        });

        requestBox.getChildren().addAll(
            titleLabel, new Separator(),
            typeBox, titleBox, messageBox,
            submitBtn, msg
        );

        content.getChildren().setAll(requestBox);
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