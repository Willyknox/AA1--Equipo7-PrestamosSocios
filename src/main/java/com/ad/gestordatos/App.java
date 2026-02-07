package com.ad.gestordatos;

import com.ad.gestordatos.dao.DatabaseInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main Entry Point for the JavaFX Application.
 * Initializes the database and loads the main view.
 */
public class App extends Application {

    private static Scene scene;
    private static final String MAIN_VIEW = "view/MainView";

    @Override
    public void start(Stage stage) throws IOException {
        initializeDatabase(stage);

        try {
            // Load the main scene
            scene = new Scene(loadFXML(MAIN_VIEW), 800, 600);
            stage.setScene(scene);
            stage.setTitle("Data Manager");

            // Ensure window closes existing threads
            stage.setOnCloseRequest(event -> {
                System.out.println("Application closing...");
                System.exit(0);
            });

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Application Start Error", "Could not load main view", e.getMessage());
        }
    }

    private void initializeDatabase(Stage stage) {
        try {
            DatabaseInitializer.initialize();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Database Initialization Error",
                    "Could not connect to or initialize the database.",
                    "Ensure MariaDB is running.\nDetails: " + e.getMessage());
        }
    }

    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait(); // Use showAndWait to block until user acknowledges
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}
