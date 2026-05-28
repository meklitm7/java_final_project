package gui.screens;

import gui.SceneManager;
import model.CurrentUser;
import model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import rmi.TesfaRMIClient;

public class LoginPage extends BorderPane {
    private boolean connectedToServer;

    public LoginPage(Stage stage) {
        this(stage, true); // Default to connected
    }

    public LoginPage(Stage stage, boolean connectedToServer) {
        this.connectedToServer = connectedToServer;
        setPadding(new Insets(24));

        VBox container = new VBox(14);
        container.setAlignment(Pos.CENTER);

        Label title = new Label("Tesfa Donation System - Login");
        title.setFont(Font.font("Arial", 26));
        title.setTextFill(Color.web("#2c3e50"));

        // Add connection status
        Label connectionStatus = new Label();
        if (connectedToServer) {
            connectionStatus.setText("Connected to RMI Server");
            connectionStatus.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 12px;");
        } else {
            connectionStatus.setText("Offline mode - Limited functionality");
            connectionStatus.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px;");
        }

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setStyle("-fx-pref-width: 250px;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-pref-width: 250px;");

        Label msg = new Label();
        msg.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");

        Button loginBtn = new Button("Login");
        loginBtn.setDefaultButton(true);
        loginBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 120px;");

        Hyperlink registerLink = new Hyperlink("Create New Account");
        registerLink.setStyle("-fx-text-fill: #16a085;");

        GridPane form = new GridPane();
        form.setVgap(10);
        form.setHgap(10);
        form.setAlignment(Pos.CENTER);

        form.add(new Label("Email:"), 0, 0);
        form.add(emailField, 1, 0);
        form.add(new Label("Password:"), 0, 1);
        form.add(passwordField, 1, 1);

        HBox actions = new HBox(10, loginBtn);
        actions.setAlignment(Pos.CENTER);

        container.getChildren().addAll(title, connectionStatus, form, msg, actions, registerLink);
        setCenter(container);

        registerLink.setOnAction(e -> {
            if (connectedToServer) {
                SceneManager.showRegister(stage);
            } else {
                msg.setText("Registration is unavailable in offline mode.");
            }
        });

        loginBtn.setOnAction(e -> {
            String email = emailField.getText() == null ? "" : emailField.getText().trim();
            String password = passwordField.getText() == null ? "" : passwordField.getText();

            if (email.isEmpty() || password.isEmpty()) {
                msg.setText("Email and password are required.");
                return;
            }
            if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                msg.setText("Invalid email format.");
                return;
            }

            if (connectedToServer) {
                try {
                    User user = TesfaRMIClient.login(email, password);
                    if (user == null) {
                        msg.setText("Invalid credentials.");
                        return;
                    }

                    // Store current user
                    CurrentUser.setUser(
                        user.getId(),
                        user.getName(),
                        user.getRole(),
                        user.getEmail(),
                        user.getTelegramUsername()
                    );

                    // Route by role
                    switch (user.getRole()) {
                        case "Donor":
                            SceneManager.showDonor(stage);
                            break;
                        case "Volunteer":
                            SceneManager.showVolunteer(stage);
                            break;
                        case "Admin":
                            SceneManager.showAdmin(stage);
                            break;
                        default:
                            msg.setText("Unknown role: " + user.getRole());
                    }
                } catch (Exception ex) {
                    msg.setText("Login error: " + ex.getMessage());
                }
            } else {
                msg.setText("Login is unavailable in offline mode.");
            }
        });
    }
}