package gui.screens;

import db.*;
import gui.SceneManager;
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
import java.sql.Connection;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VolunteerDashboard extends BorderPane {
    private Stage stage;
    private Connection conn;
    private Set<Integer> selectedTaskIds = new HashSet<>();
    private TableView<VolunteerTask> tasksTable;
    private ComboBox<String> categoryCombo;
    private DatePicker datePicker;

    public VolunteerDashboard(Stage stage) {
        this.stage = stage;
        this.conn = DBConnection.connect();
        setupUI();
    }

    private void setupUI() {
        // Header
        HBox header = new HBox(20);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #2c3e50;");

        Label title = new Label("Volunteer Dashboard - Welcome, " + CurrentUser.getName());
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

        Button viewTasksBtn = new Button("  View Available Tasks");
        Button myAssignmentsBtn = new Button("  My Assignments");
        Button selectTasksBtn = new Button("  Select Tasks");
        Button logoutBtn2 = new Button("  Logout");

        styleButton(viewTasksBtn, "#3498db");
        styleButton(myAssignmentsBtn, "#2ecc71");
        styleButton(selectTasksBtn, "#f39c12");
        styleButton(logoutBtn2, "#e74c3c");

        sidebar.getChildren().addAll(
                new Label("Menu") {
                    {
                        setTextFill(Color.WHITE);
                    }
                },
                viewTasksBtn, myAssignmentsBtn, selectTasksBtn, logoutBtn2);

        // Main content
        StackPane content = new StackPane();
        content.setStyle("-fx-background-color: #ecf0f1;");

        // Default: Show available tasks
        showAvailableTasks(content);

        // Button actions
        viewTasksBtn.setOnAction(e -> showAvailableTasks(content));
        myAssignmentsBtn.setOnAction(e -> showMyAssignments(content));
        selectTasksBtn.setOnAction(e -> showTaskSelection(content));
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
        VolunteerTaskDAO dao = new VolunteerTaskDAO(conn);
        String category = categoryCombo.getValue();
        List<VolunteerTask> tasks = category.equals("All") ? dao.getAllTasks() : dao.getTasksByCategory(category);
        tasksTable.setItems(FXCollections.observableArrayList(tasks));
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
        dateCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getAssignmentDate().toString()));

        TableColumn<VolunteerTaskAssignment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(idCol, taskCol, categoryCol, dateCol, statusCol);
        table.setPrefWidth(800);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadMyAssignments(TableView<VolunteerTaskAssignment> table) {
        VolunteerTaskAssignmentDAO dao = new VolunteerTaskAssignmentDAO(conn);
        table.setItems(FXCollections.observableArrayList(
                dao.getAssignmentsByVolunteer(CurrentUser.getId())));
    }

    // ========== SELECT TASKS ==========
 // REPLACE this method in VolunteerDashboard.java:
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

    // Create table FIRST
    TableView<VolunteerTask> selectionTable = new TableView<>();
    setupSelectionTable(selectionTable);

    // THEN load data into THIS table
    loadTasksForSelection(selectionTable, categoryCombo.getValue());

    categoryCombo.setOnAction(e -> loadTasksForSelection(selectionTable, categoryCombo.getValue()));

    filterBox.getChildren().addAll(categoryLabel, categoryCombo);

    // Date picker
    HBox dateBox = new HBox(10);
    dateBox.setAlignment(Pos.CENTER_LEFT);
    DatePicker datePicker = new DatePicker();
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

        VolunteerTaskAssignmentDAO dao = new VolunteerTaskAssignmentDAO(conn);
        for (Integer taskId : selectedTaskIds) {
            dao.assignTask(CurrentUser.getId(), taskId, selectedDate, CurrentUser.getId());
        }

        msg.setText("✅ " + selectedTaskIds.size() + " tasks submitted for approval!");
        selectedTaskIds.clear();
        loadTasksForSelection(selectionTable, categoryCombo.getValue());
    });

    selectionBox.getChildren().addAll(
        title, filterBox, selectionTable, dateBox, submitBtn, msg
    );
    content.getChildren().setAll(selectionBox);
}

// ADD this new method:
private void loadTasksForSelection(TableView<VolunteerTask> table, String category) {
    try {
        VolunteerTaskDAO dao = new VolunteerTaskDAO(conn);
        List<VolunteerTask> tasks = category.equals("All") ?
            dao.getAllTasks() : dao.getTasksByCategory(category);
        table.setItems(FXCollections.observableArrayList(tasks));
    } catch (Exception e) {
        System.out.println("Error loading tasks: " + e.getMessage());
        e.printStackTrace();
    }
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

    private void loadTasksForSelection(String category) {
        VolunteerTaskDAO dao = new VolunteerTaskDAO(conn);
        List<VolunteerTask> tasks = category.equals("All") ? dao.getAllTasks() : dao.getTasksByCategory(category);
        tasksTable.setItems(FXCollections.observableArrayList(tasks));
    }
}