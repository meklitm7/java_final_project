package util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.util.function.Consumer;

public class BackgroundTask {

    // Run a task in the background and update the UI on completion
    public static void runInBackground(Runnable backgroundTask, Consumer<Boolean> uiUpdateTask) {
        ThreadPoolManager.getInstance().execute(() -> {
            try {
                boolean success = false;
                // Run the background task
                backgroundTask.run();
                success = true;

                // Update the UI on the JavaFX Application Thread
                Platform.runLater(() -> {
                    uiUpdateTask.accept(success);
                });
            } catch (Exception e) {
                e.printStackTrace();
                // Show error in UI
                Platform.runLater(() -> {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Background Task Failed");
                    alert.setContentText("An error occurred: " + e.getMessage());
                    alert.showAndWait();
                });
            }
        });
    }

    // Overloaded method to run a task with a result
    public static <T> void runInBackground(java.util.concurrent.Callable<T> backgroundTask, Consumer<T> uiUpdateTask) {
        ThreadPoolManager.getInstance().execute(() -> {
            try {
                T result = backgroundTask.call();
                // Update the UI on the JavaFX Application Thread
                Platform.runLater(() -> {
                    uiUpdateTask.accept(result);
                });
            } catch (Exception e) {
                e.printStackTrace();
                // Show error in UI
                Platform.runLater(() -> {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Background Task Failed");
                    alert.setContentText("An error occurred: " + e.getMessage());
                    alert.showAndWait();
                });
            }
        });
    }
}
