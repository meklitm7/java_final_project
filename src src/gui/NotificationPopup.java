package gui;

import model.Notification;
import rmi.TesfaRMIClient;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.util.List;

public class NotificationPopup {
    private Stage stage;
    private int userId;
    private TableView<Notification> notificationTable;

    public NotificationPopup(Stage stage, int userId) {
        this.stage = stage;
        this.userId = userId;
    }

    public void show() {
        // Create a new stage for the popup
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initOwner(stage);
        popup.setTitle("Notifications");
        popup.setWidth(600);
        popup.setHeight(500);

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: white;");

        Label title = new Label("Your Notifications");
        title.setFont(Font.font("Arial", 18));
        title.setTextFill(Color.web("#2c3e50"));
        title.setAlignment(Pos.CENTER);

        // Table for notifications
        notificationTable = new TableView<>();
        setupNotificationTable();

        // Fetch and display notifications
        loadNotifications();

        // Mark all as read button
        Button markAllAsReadBtn = new Button("Mark All as Read");
        markAllAsReadBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        markAllAsReadBtn.setOnAction(e -> {
            if (TesfaRMIClient.isConnected()) {
                List<Notification> notifications = TesfaRMIClient.getNotifications(userId);
                for (Notification notification : notifications) {
                    if (!notification.isRead()) {
                        TesfaRMIClient.markNotificationRead(notification.getId());
                    }
                }
                // Refresh the table
                Platform.runLater(() -> loadNotifications());
            } else {
                showAlert("Not connected to RMI server. Please try again.");
            }
        });

        root.getChildren().addAll(title, new Separator(), notificationTable, markAllAsReadBtn);

        Scene scene = new Scene(root);
        popup.setScene(scene);
        popup.showAndWait();
    }

    private void setupNotificationTable() {
        // Message column
        TableColumn<Notification, String> messageCol = new TableColumn<>("Message");
        messageCol.setCellValueFactory(new PropertyValueFactory<>("message"));
        messageCol.setPrefWidth(350);

        // Type column
        TableColumn<Notification, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(100);

        // Date column
        TableColumn<Notification, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getCreatedAt().toString()
            )
        );
        dateCol.setPrefWidth(150);

        // Actions column
        TableColumn<Notification, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(param -> new TableCell<Notification, Void>() {
            private final Button markAsReadBtn = new Button("Mark as Read");

            {
                markAsReadBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 12px;");
                markAsReadBtn.setOnAction(e -> {
                    Notification notification = getTableView().getItems().get(getIndex());
                    if (TesfaRMIClient.isConnected()) {
                        boolean success = TesfaRMIClient.markNotificationRead(notification.getId());
                        if (success) {
                            notification.setRead(true);
                            Platform.runLater(() -> getTableView().refresh());
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
                    Notification notification = getTableView().getItems().get(getIndex());
                    if (!notification.isRead()) {
                        setGraphic(markAsReadBtn);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        notificationTable.getColumns().addAll(messageCol, typeCol, dateCol, actionCol);
        notificationTable.setPrefWidth(580);
        notificationTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadNotifications() {
        if (TesfaRMIClient.isConnected()) {
            List<Notification> notifications = TesfaRMIClient.getNotifications(userId);
            Platform.runLater(() -> {
                notificationTable.setItems(javafx.collections.FXCollections.observableArrayList(notifications));
            });
        } else {
            Platform.runLater(() -> {
                showAlert("Not connected to RMI server. Please try again.");
            });
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