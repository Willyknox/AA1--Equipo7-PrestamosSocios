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

    private Parent welcomeContent;

    @FXML
    public void initialize() {
        // Guardar el contenido de bienvenida original para poder restaurarlo
        welcomeContent = (Parent) mainContainer.getCenter();
    }

    @FXML
    private void showSocios() {
        loadView("vista-socio");
    }

    @FXML
    private void showSociosTable() {
        loadView("SociosTableView");
    }

    public void loadSocioFormWithData(com.ad.gestordatos.model.Socio socio) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("view/vista-socio.fxml"));
            Parent view = loader.load();
            SocioController controller = loader.getController();
            controller.setMainController(this);
            controller.loadSocioForEdit(socio);
            mainContainer.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showPrestamos() {
        loadView("PrestamoForm");
    }

    @FXML
    private void showTabla() {
        loadView("TablaView");
    }

    public void showWelcome() {
        mainContainer.setCenter(welcomeContent);
    }

    public void loadPrestamoFormWithData(int prestamoId, java.time.LocalDate diaPrestamo,
            java.time.LocalDate diaVencimiento, float importe, boolean pagado, int idSocio) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("view/PrestamoForm.fxml"));
            Parent view = loader.load();
            PrestamoController controller = loader.getController();
            controller.setMainController(this);
            controller.loadPrestamoForEdit(prestamoId, diaPrestamo, diaVencimiento, importe, pagado, idSocio);
            mainContainer.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("view/" + fxml + ".fxml"));
            Parent view = loader.load();
            // Pasar referencia del MainController a los controllers hijos
            Object controller = loader.getController();
            if (controller instanceof SocioController) {
                ((SocioController) controller).setMainController(this);
            } else if (controller instanceof PrestamoController) {
                ((PrestamoController) controller).setMainController(this);
            } else if (controller instanceof TablaController) {
                ((TablaController) controller).setMainController(this);
            } else if (controller instanceof SociosTableController) {
                ((SociosTableController) controller).setMainController(this);
            }
            mainContainer.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
