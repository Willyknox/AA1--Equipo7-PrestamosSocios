package com.ad.gestordatos.controller;

import com.ad.gestordatos.model.Prestamo;
import com.ad.gestordatos.model.PrestamoConSocio;
import com.ad.gestordatos.util.GestorDatos;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller for displaying the list of loans (Prestamos) associated with
 * Socios.
 * Managing the TableView logic for displaying options like Edit and Delete.
 */
public class PrestamosTableController {

    @FXML
    private TableView<PrestamoConSocio> tablePrestamos;
    @FXML
    private TableColumn<PrestamoConSocio, String> colNombre;
    @FXML
    private TableColumn<PrestamoConSocio, String> colApellido1;
    @FXML
    private TableColumn<PrestamoConSocio, String> colApellido2;
    @FXML
    private TableColumn<PrestamoConSocio, LocalDate> colDiaPrestamo;
    @FXML
    private TableColumn<PrestamoConSocio, LocalDate> colDiaVencimiento;
    @FXML
    private TableColumn<PrestamoConSocio, Float> colImporte;
    @FXML
    private TableColumn<PrestamoConSocio, Boolean> colPagado;

    private final GestorDatos gestorDatos;
    private MainController mainController;

    public PrestamosTableController() {
        gestorDatos = new GestorDatos();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        System.out.println("DEBUG: PrestamosTableController initialized");
        // Using CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN as replacement for
        // deprecated CONSTRAINED_RESIZE_POLICY
        tablePrestamos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreSolo"));
        colApellido1.setCellValueFactory(new PropertyValueFactory<>("apellido1Socio"));
        colApellido2.setCellValueFactory(new PropertyValueFactory<>("apellido2Socio"));
        colDiaPrestamo.setCellValueFactory(new PropertyValueFactory<>("diaPrestamo"));
        colDiaVencimiento.setCellValueFactory(new PropertyValueFactory<>("diaVencimiento"));
        colImporte.setCellValueFactory(new PropertyValueFactory<>("importe"));
        colPagado.setCellValueFactory(new PropertyValueFactory<>("estaPagado"));

        loadData();
    }

    private void loadData() {
        System.out.println("DEBUG: PrestamosTableController.loadData called");
        try {
            List<PrestamoConSocio> lista = gestorDatos.getAllPrestamosConSocio();
            tablePrestamos.setItems(FXCollections.observableArrayList(lista));
        } catch (Exception e) {
            System.err.println("ERROR: Failed to load data in PrestamosTableController");
            e.printStackTrace();
            showError("Error loading data: " + e.getMessage());
        }
    }

    @FXML
    private void onEliminar() {
        PrestamoConSocio selected = tablePrestamos.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Select a loan from the table to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this loan?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                gestorDatos.deletePrestamo(selected.getPrestamoId());
                showInfo("Loan deleted successfully.");
                loadData();
            } catch (Exception e) {
                showError("Error deleting: " + e.getMessage());
            }
        }
    }

    @FXML
    private void onEditar() {
        PrestamoConSocio selected = tablePrestamos.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Select a loan from the table to edit.");
            return;
        }
        if (mainController != null) {
            try {
                // Fetch full object to get ID and other details unavailable in the light
                // wrapper
                Prestamo prestamo = gestorDatos.readPrestamo(selected.getPrestamoId());
                mainController.loadPrestamoFormWithData(
                        prestamo.getId(),
                        prestamo.getDiaPrestamo(),
                        prestamo.getDiaVencimiento(),
                        prestamo.getImporte(),
                        prestamo.isEstaPagado(),
                        prestamo.getIdSocio());
            } catch (Exception e) {
                showError("Error loading loan: " + e.getMessage());
            }
        }
    }

    @FXML
    private void onVolver() {
        if (mainController != null) {
            mainController.showWelcome();
        }
    }

    // Helper methods

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.show();
    }

    private void showWarning(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(msg);
        alert.show();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }
}
