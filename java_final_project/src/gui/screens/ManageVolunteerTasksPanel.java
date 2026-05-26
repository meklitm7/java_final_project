package gui.screens;

import db.VolunteerDAO;
import db.VolunteerTaskDAO;
import db.VolunteerTaskAssignmentDAO;
import model.CurrentUser;
import model.Volunteer;
import model.VolunteerTask; // <-- MISSING IMPORT (This was the error!)
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory; // <-- Also needed
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

public class ManageVolunteerTasksPanel extends VBox {
  private Connection conn;
  private TableView<VolunteerTask> tasksTable;

  public ManageVolunteerTasksPanel(Connection conn) {
    this.conn = conn;
    setPadding(new Insets(20));
    setSpacing(15);
    setupUI();
  }

  private void setupUI() {
    Label title = new Label("Manage Volunteer Tasks");
    title.setFont(Font.font("Arial", 20));
    title.setTextFill(Color.web("#2c3e50"));

    // Category filter
    HBox filterBox = new HBox(10);
    filterBox.setAlignment(Pos.CENTER_LEFT);
    Label categoryLabel = new Label("Filter by Category:");
    ComboBox<String> categoryCombo = new ComboBox<>();
    categoryCombo.getItems().addAll("All", "Home Chores", "Children Care", "Teaching");
    categoryCombo.setValue("All");
    categoryCombo.setOnAction(e -> loadTasks(categoryCombo.getValue()));

    filterBox.getChildren().addAll(categoryLabel, categoryCombo);

    // Add Task Form
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

    // Tasks Table
    tasksTable = new TableView<>();
    setupTasksTable();
    loadTasks("All");

    // Assign Task Form
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
        msg.setText("✅ Task added successfully!");
        loadTasks(categoryCombo.getValue());
        loadTaskNames(taskCombo);
        taskNameField.clear();
        descField.clear();
      } else {
        msg.setText("❌ Failed to add task.");
      }
    });

    assignBtn.setOnAction(e -> {
      String volunteerName = volunteerCombo.getValue();
      String taskName = taskCombo.getValue();
      LocalDate date = datePicker.getValue();

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
        msg.setText("✅ Task assigned successfully! Pending approval.");
        loadTasks(categoryCombo.getValue());
      } else {
        msg.setText("❌ Failed to assign task.");
      }
    });

    getChildren().addAll(
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
  }

  private void setupTasksTable() {
    TableColumn<VolunteerTask, Integer> idCol = new TableColumn<>("ID");
    idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

    TableColumn<VolunteerTask, String> nameCol = new TableColumn<>("Name");
    nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

    TableColumn<VolunteerTask, String> categoryCol = new TableColumn<>("Category");
    categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

    TableColumn<VolunteerTask, String> descCol = new TableColumn<>("Description");
    descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

    TableColumn<VolunteerTask, Void> deleteCol = new TableColumn<>("Delete");
    deleteCol.setCellFactory(param -> new TableCell<VolunteerTask, Void>() {
      private final Button deleteBtn = new Button("Delete");
      {
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> {
          VolunteerTask task = getTableView().getItems().get(getIndex());
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

    tasksTable.getColumns().addAll(idCol, nameCol, categoryCol, descCol, deleteCol);
    tasksTable.setPrefWidth(800);
    tasksTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
  }

  private void loadTasks(String category) {
    VolunteerTaskDAO dao = new VolunteerTaskDAO(conn);
    List<VolunteerTask> tasks = category.equals("All") ? dao.getAllTasks() : dao.getTasksByCategory(category);
    tasksTable.setItems(FXCollections.observableArrayList(tasks));
  }

  private void loadVolunteers(ComboBox<String> combo) {
    VolunteerDAO dao = new VolunteerDAO(conn);
    for (Volunteer v : dao.getAllVolunteers()) {
      combo.getItems().add(v.getName());
    }
  }

  private void loadTaskNames(ComboBox<String> combo) {
    VolunteerTaskDAO dao = new VolunteerTaskDAO(conn);
    combo.getItems().clear();
    for (VolunteerTask t : dao.getAllTasks()) {
      combo.getItems().add(t.getName());
    }
  }

  private int getVolunteerIdByName(String name) {
    VolunteerDAO dao = new VolunteerDAO(conn);
    for (Volunteer v : dao.getAllVolunteers()) {
      if (v.getName().equals(name)) {
        return v.getId();
      }
    }
    return -1;
  }

  private int getTaskIdByName(String name) {
    VolunteerTaskDAO dao = new VolunteerTaskDAO(conn);
    for (VolunteerTask t : dao.getAllTasks()) {
      if (t.getName().equals(name)) {
        return t.getId();
      }
    }
    return -1;
  }
}