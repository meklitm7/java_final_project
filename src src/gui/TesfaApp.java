package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import network.TesfaClient;
import rmi.TesfaRMIClient;
import service.BranchSyncService;
import util.ThreadPoolManager;

import java.io.File;

public class TesfaApp extends Application {
    private static BranchSyncService syncService;
    private static final int BRANCH_ID = 1; // Unique ID for this branch

    @Override
    public void start(Stage primaryStage) {
        // Window title
        primaryStage.setTitle("Tesfa Donation System - Branch " + BRANCH_ID);

        // App icon
        try {
            Image appIcon = new Image(
                new File("resource/tesfa.png").toURI().toString());
            primaryStage.getIcons().add(appIcon);
        } catch (Exception e) {
            System.err.println("Error loading app icon: " + e.getMessage());
        }

        // Connect to RMI server
        try {
            TesfaRMIClient.connect("localhost", 1099);
            System.out.println("Connected to Tesfa RMI Server");

            // Start branch synchronization service
            syncService = new BranchSyncService(BRANCH_ID);
            syncService.startSync();
        } catch (Exception e) {
            System.err.println("Failed to connect to RMI server: " + e.getMessage());
            // You might want to show an alert here or continue in offline mode
        }

        // Load CSS
        Scene scene = new Scene(new gui.screens.LoginPage(primaryStage), 980, 620);
        scene.getStylesheets().add(getClass().getResource("/gui/styles.css").toExternalForm());
        primaryStage.setScene(scene);

        // Show first scene
        SceneManager.showLogin(primaryStage);

        // Display window
        primaryStage.show();

        // Shutdown thread pool and sync service when the application closes
        primaryStage.setOnCloseRequest(event -> {
            ThreadPoolManager.getInstance().shutdown();
            if (syncService != null) {
                syncService.stopSync();
            }
            TesfaRMIClient.disconnect();
        });
    }

    public static void main(String[] args) {
         System.setProperty("prism.order", "sw");
    System.setProperty("prism.verbose", "true");
    System.setProperty("javafx.graphics.disableNative", "false");
        launch(args);
    }
}