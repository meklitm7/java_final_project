package gui.screens;

import db.DBConnection;
import db.UserDAO;
import gui.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.sql.Connection;

public class RegisterPage extends BorderPane {
    public RegisterPage(Stage stage) {
        setPadding(new Insets(24));

        VBox container = new VBox(14);
        container.setAlignment(Pos.CENTER);

        Label title = new Label("Create Account - Tesfa Donation System");
        title.setFont(Font.font("Arial", 26));
        title.setTextFill(Color.web("#2c3e50"));

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        nameField.setStyle("-fx-pref-width: 250px;");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setStyle("-fx-pref-width: 250px;");

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Donor", "Volunteer");
        roleBox.setPromptText("Select Role");
        roleBox.setStyle("-fx-pref-width: 250px;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-pref-width: 250px;");

        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Confirm Password");
        confirmField.setStyle("-fx-pref-width: 250px;");

        Label msg = new Label();
        msg.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");

        Button registerBtn = new Button("Create Account");
        registerBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");

        Hyperlink backLink = new Hyperlink("Back to Login");
        backLink.setStyle("-fx-text-fill: #16a085;");

        GridPane form = new GridPane();
        form.setVgap(10);
        form.setHgap(10);
        form.setAlignment(Pos.CENTER);

        int r = 0;
        form.add(new Label("Name:"), 0, r);
        form.add(nameField, 1, r++);
        form.add(new Label("Email:"), 0, r);
        form.add(emailField, 1, r++);
        form.add(new Label("Role:"), 0, r);
        form.add(roleBox, 1, r++);
        form.add(new Label("Password:"), 0, r);
        form.add(passwordField, 1, r++);
        form.add(new Label("Confirm:"), 0, r);
        form.add(confirmField, 1, r++);

        container.getChildren().addAll(title, form, msg, registerBtn, backLink);
        setCenter(container);

        backLink.setOnAction(e -> SceneManager.showLogin(stage));

        registerBtn.setOnAction(e -> {
            String name = nameField.getText() == null ? "" : nameField.getText().trim();
            String email = emailField.getText() == null ? "" : emailField.getText().trim();
            String role = roleBox.getValue();
            String password = passwordField.getText() == null ? "" : passwordField.getText();
            String confirm = confirmField.getText() == null ? "" : confirmField.getText();

            if (name.isEmpty() || email.isEmpty() || role == null || password.isEmpty() || confirm.isEmpty()) {
                msg.setText("All fields are required.");
                return;
            }
            if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                msg.setText("Invalid email format.");
                return;
            }
            if (!password.equals(confirm)) {
                msg.setText("Passwords do not match.");
                return;
            }
            if (role.equals("Admin")) {
                try (Connection conn = DBConnection.connect()) {
                    UserDAO userDAO = new UserDAO(conn);
                    if (userDAO.countAdmins() >= 3) {
                        msg.setText("Admin limit reached (max 3).");
                        return;
                    }
                } catch (Exception ex) {
                    msg.setText("Error checking admins: " + ex.getMessage());
                    return;
                }
            }

            try (Connection conn = DBConnection.connect()) {
                if (conn == null) {
                    msg.setText("Database connection failed.");
                    return;
                }

                UserDAO userDAO = new UserDAO(conn);
                boolean ok = userDAO.addUser(name, role, email, password, "");
                if (!ok) {
                    msg.setText("Registration failed (email may exist).");
                    return;
                }

                msg.setText("Success! Please login.");
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                javafx.application.Platform.runLater(() -> SceneManager.showLogin(stage));
                            }
                        },
                        1500);
            } catch (Exception ex) {
                msg.setText("Registration error: " + ex.getMessage());
            }
        });
    }
}