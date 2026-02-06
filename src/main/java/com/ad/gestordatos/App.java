package com.ad.gestordatos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        try {
            // Initialize database
            com.ad.gestordatos.dao.DatabaseInitializer.initialize();
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Could not connect to database");
            alert.setContentText("Please ensure MariaDB is running.\nError: " + e.getMessage());
            alert.showAndWait();
            // Optional: exit or allow simpler mode? For now, we continue but app might fail
            // later if DB needed.
        }

        scene = new Scene(loadFXML("view/MainView"), 800, 600);
        stage.setScene(scene);
        stage.setTitle("Gestor de Datos");
        stage.show();
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
