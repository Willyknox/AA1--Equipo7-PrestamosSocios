package com.ad.gestordatos.controller;

import com.ad.gestordatos.App;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

public class MainController {

    @FXML
    private BorderPane mainContainer;

    @FXML
    public void initialize() {
        // Load default view
        loadView("SocioForm");
    }

    @FXML
    private void showSocios() {
        loadView("SocioForm");
    }

    @FXML
    private void showPrestamos() {
        loadView("PrestamoForm");
    }

    // Future requirement: TableView
    @FXML
    private void showTabla() {
        loadView("TablaView");
    }

    private void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("view/" + fxml + ".fxml"));
            Parent view = loader.load();
            mainContainer.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle error (maybe show alert)
        }
    }
}
