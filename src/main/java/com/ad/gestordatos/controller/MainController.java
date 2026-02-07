package com.ad.gestordatos.controller;

import com.ad.gestordatos.App;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

/**
 * MainController manages the main application window and navigation.
 * It acts as the central coordinator for loading and switching between
 * different views
 * (Socios, Prestamos, Tables) within the main BorderPane container.
 */
public class MainController {

    @FXML
    private BorderPane mainContainer;

    private Parent welcomeContent;

    /**
     * Initializes the controller.
     * Captures the initial "Welcome" content to allow returning to it later.
     */
    @FXML
    public void initialize() {
        // Preserve the original welcome content to restore it when "Volver" is clicked
        welcomeContent = (Parent) mainContainer.getCenter();
    }

    /**
     * Navigates to the Socio Form for creating a new Socio.
     */
    @FXML
    private void showSocios() {
        loadView("SocioForm");
    }

    /**
     * Navigates to the Socios Table to view the list of Socios.
     */
    @FXML
    private void showSociosTable() {
        loadView("SociosTableView");
    }

    /**
     * Loads the Socio Form and populates it with data for editing.
     *
     * @param socio The Socio object containing the data to edit.
     */
    public void loadSocioFormWithData(com.ad.gestordatos.model.Socio socio) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("view/SocioForm.fxml"));
            Parent view = loader.load();

            // Get the controller and pass the MainController reference for navigation
            SocioController controller = loader.getController();
            controller.setMainController(this);

            // Populate the form with the provided Socio data
            controller.loadSocioForEdit(socio);

            mainContainer.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading Socio form for edit", e.getMessage());
        }
    }

    /**
     * Navigates to the Prestamo Form for creating a new Prestamo.
     */
    @FXML
    private void showPrestamos() {
        loadView("PrestamoForm");
    }

    /**
     * Navigates to the Prestamos Table to view the list of Loans.
     */
    @FXML
    private void showTabla() {
        loadView("PrestamosTableView");
    }

    /**
     * Restores the initial welcome view in the center of the BorderPane.
     */
    public void showWelcome() {
        if (welcomeContent != null) {
            mainContainer.setCenter(welcomeContent);
        }
    }

    /**
     * Loads the Prestamo Form and populates it with data for editing.
     *
     * @param prestamoId     The ID of the loan to edit.
     * @param diaPrestamo    Date of the loan.
     * @param diaVencimiento Due date of the loan.
     * @param importe        Amount of the loan.
     * @param pagado         Status of payment.
     * @param idSocio        ID of the associated member.
     */
    public void loadPrestamoFormWithData(int prestamoId, java.time.LocalDate diaPrestamo,
            java.time.LocalDate diaVencimiento, float importe, boolean pagado, int idSocio) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("view/PrestamoForm.fxml"));
            Parent view = loader.load();

            PrestamoController controller = loader.getController();
            controller.setMainController(this);
            // Populate form
            controller.loadPrestamoForEdit(prestamoId, diaPrestamo, diaVencimiento, importe, pagado, idSocio);

            mainContainer.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading Prestamo form for edit", e.getMessage());
        }
    }

    /**
     * Generic helper method to load an FXML view into the center of the main
     * container.
     * handles setting up the controller connection (child -> main).
     *
     * @param fxml The base name of the FXML file (without .fxml extension).
     */
    private void loadView(String fxml) {
        System.out.println("DEBUG: Attempting to load view: " + fxml);
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("view/" + fxml + ".fxml"));
            Parent view = loader.load();
            System.out.println("DEBUG: View loaded successfully: " + fxml);

            // Inject MainController reference into the child controller
            // This allows child controllers to trigger navigation (e.g., "Volver" buttons)
            Object controller = loader.getController();
            System.out.println(
                    "DEBUG: Controller obtained: " + (controller != null ? controller.getClass().getName() : "null"));

            if (controller instanceof SocioController) {
                ((SocioController) controller).setMainController(this);
            } else if (controller instanceof PrestamoController) {
                ((PrestamoController) controller).setMainController(this);
            } else if (controller instanceof PrestamosTableController) {
                ((PrestamosTableController) controller).setMainController(this);
            } else if (controller instanceof SociosTableController) {
                ((SociosTableController) controller).setMainController(this);
            }
            mainContainer.setCenter(view);
        } catch (IOException e) {
            System.err.println("ERROR: Failed to load view " + fxml);
            e.printStackTrace();
            showError("Navigation Error", "Could not load view: " + fxml + "\n" + e.getMessage());
        }
    }

    /**
     * Helper to show error alerts.
     */
    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
