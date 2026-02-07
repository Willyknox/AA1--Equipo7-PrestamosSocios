package com.ad.gestordatos.controller;

import com.ad.gestordatos.model.Socio;
import com.ad.gestordatos.util.GestorDatos;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the Socios Table View.
 * Displays a list of partners and allows selection for editing or deletion.
 */
public class SociosTableController {

    @FXML
    private TableView<Socio> tableSocios;
    @FXML
    private TableColumn<Socio, String> colDni;
    @FXML
    private TableColumn<Socio, String> colNombre;
    @FXML
    private TableColumn<Socio, String> colApellido1;
    @FXML
    private TableColumn<Socio, String> colApellido2;
    @FXML
    private TableColumn<Socio, String> colEmail;
    @FXML
    private TableColumn<Socio, String> colNacimiento;

    private final GestorDatos gestorDatos;
    private MainController mainController;

    public SociosTableController() {
        gestorDatos = new GestorDatos();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        // Use standard resize policy
        tableSocios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        colDni.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDni()));
        colNombre.setCellValueFactory(cellData -> {
            String[] partes = splitNombre(cellData.getValue().getNombre());
            return new SimpleStringProperty(partes[0]);
        });
        colApellido1.setCellValueFactory(cellData -> {
            String[] partes = splitNombre(cellData.getValue().getNombre());
            return new SimpleStringProperty(partes[1]);
        });
        colApellido2.setCellValueFactory(cellData -> {
            String[] partes = splitNombre(cellData.getValue().getNombre());
            return new SimpleStringProperty(partes[2]);
        });
        colEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        colNacimiento.setCellValueFactory(cellData -> {
            LocalDate fecha = cellData.getValue().getNacimiento();
            return new SimpleStringProperty(fecha != null ? fecha.toString() : "");
        });

        loadData();
    }

    /**
     * Splits a full name string into parts (Name, First Surname, Second Surname).
     * 
     * @param nombreCompleto The full name string.
     * @return Array of strings of length 3.
     */
    private String[] splitNombre(String nombreCompleto) {
        String[] partes = nombreCompleto != null ? nombreCompleto.split(" ", 3) : new String[] { "" };
        String nombre = partes.length > 0 ? partes[0] : "";
        String apellido1 = partes.length > 1 ? partes[1] : "";
        String apellido2 = partes.length > 2 ? partes[2] : "";
        return new String[] { nombre, apellido1, apellido2 };
    }

    private void loadData() {
        try {
            List<Socio> lista = gestorDatos.getAllSocios();
            tableSocios.setItems(FXCollections.observableArrayList(lista));
        } catch (Exception e) {
            showError("Error al cargar socios: " + e.getMessage());
        }
    }

    @FXML
    private void onModificar() {
        Socio selected = tableSocios.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Select a partner to modify.");
            return;
        }
        if (mainController != null) {
            mainController.loadSocioFormWithData(selected);
        }
    }

    @FXML
    private void onBorrar() {
        Socio selected = tableSocios.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Select a partner to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete partner " + selected.getNombre() + "?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                gestorDatos.deleteSocio(selected.getId());
                showInfo("Partner deleted.");
                loadData();
            } catch (Exception e) {
                showError("Error deleting: " + e.getMessage());
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
        alert.setTitle("Error");
        alert.setContentText(msg);
        alert.show();
    }

    private void showWarning(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText(msg);
        alert.show();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setContentText(msg);
        alert.show();
    }
}
