package gui;

import java.io.File;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Tesfa - JavaFX frontend entrypoint.
 */
public class TesfaApp extends Application {

  @Override
  public void start(Stage primaryStage) {

    // Window title
    primaryStage.setTitle("Tesfa Donation System");

    // App icon
    try {
      Image appIcon = new Image(
          new File("C:\\Users\\Hp-NoteBook\\java_final_project\\resource\\tesfa.png")
              .toURI()
              .toString());

      primaryStage.getIcons().add(appIcon);

    } catch (Exception e) {
      System.err.println("Error loading app icon: " + e.getMessage());
    }

    // Show first scene
    SceneManager.showLogin(primaryStage);

    // Display window
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}