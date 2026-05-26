package gui.screens;

import db.DBConnection;
import db.UserDAO;
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
import java.sql.Connection;

public class LoginPage extends BorderPane {
    public LoginPage(Stage stage) {
        setPadding(new Insets(24));

        VBox container = new VBox(14);
        container.setAlignment(Pos.CENTER);

        Label title = new Label("Tesfa Donation System - Login");
        title.setFont(Font.font("Arial", 26));
        title.setTextFill(Color.web("#2c3e50"));

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

        container.getChildren().addAll(title, form, msg, actions, registerLink);
        setCenter(container);

        registerLink.setOnAction(e -> SceneManager.showRegister(stage));

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

            try (Connection conn = DBConnection.connect()) {
                if (conn == null) {
                    msg.setText("Database connection failed.");
                    return;
                }

                UserDAO userDAO = new UserDAO(conn);
                User user = userDAO.getUserByEmail(email);
                if (user == null || !userDAO.login(email, password)) {
                    msg.setText("Invalid credentials.");
                    return;
                }

                // Store current user with all 5 parameters
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
        });
    }
}