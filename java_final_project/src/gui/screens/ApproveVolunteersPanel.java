// src/gui/screens/ApproveVolunteersPanel.java
package gui.screens;

import db.VolunteerTaskAssignmentDAO;
import model.VolunteerTaskAssignment;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import java.sql.Connection;
import java.util.List;

public class ApproveVolunteersPanel extends VBox {
    private Connection conn;

    public ApproveVolunteersPanel(Connection conn) {
        this.conn = conn;
        setPadding(new Insets(20));
        setSpacing(15);
        setupUI();
    }

    private void setupUI() {
        Label title = new Label("Approve Volunteer Task Assignments");
        title.setFont(Font.font("Arial", 20));
        title.setTextFill(Color.web("#2c3e50"));

        TableView<VolunteerTaskAssignment> table = new TableView<>();
        setupTable(table);
        loadPendingAssignments(table);

        getChildren().addAll(title, table);
    }

    private void setupTable(TableView<VolunteerTaskAssignment> table) {
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
        actionCol.setCellFactory(param -> new TableCell<>() {
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

    private void loadPendingAssignments(TableView<VolunteerTaskAssignment> table) {
        VolunteerTaskAssignmentDAO dao = new VolunteerTaskAssignmentDAO(conn);
        table.setItems(FXCollections.observableArrayList(dao.getPendingAssignments()));
    }
}