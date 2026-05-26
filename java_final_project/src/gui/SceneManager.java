package gui;

import gui.screens.*;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
    public static void showLogin(Stage stage) {
        Scene scene = new Scene(new LoginPage(stage), 980, 620);
        stage.setScene(scene);
    }

    public static void showRegister(Stage stage) {
        Scene scene = new Scene(new RegisterPage(stage), 980, 680);
        stage.setScene(scene);
    }

    public static void showDonor(Stage stage) {
        stage.setScene(new Scene(new DonorDashboard(stage), 1200, 720));
    }

    public static void showVolunteer(Stage stage) {
        stage.setScene(new Scene(new VolunteerDashboard(stage), 1200, 720));
    }

    public static void showAdmin(Stage stage) {
        stage.setScene(new Scene(new AdminDashboard(stage), 1200, 720));
    }
}