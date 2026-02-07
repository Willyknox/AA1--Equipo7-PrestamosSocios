package com.ad.gestordatos.controller;

import com.ad.gestordatos.model.Socio;
import com.ad.gestordatos.util.GestorDatos;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;

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

    private GestorDatos gestorDatos;
    private MainController mainController;

    public SociosTableController() {
        gestorDatos = new GestorDatos();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        tableSocios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

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

    private String[] splitNombre(String nombreCompleto) {
        String[] partes = nombreCompleto != null ? nombreCompleto.split(" ", 3) : new String[]{""};
        String nombre = partes.length > 0 ? partes[0] : "";
        String apellido1 = partes.length > 1 ? partes[1] : "";
        String apellido2 = partes.length > 2 ? partes[2] : "";
        return new String[]{nombre, apellido1, apellido2};
    }

    private void loadData() {
        try {
            List<Socio> lista = gestorDatos.getAllSocios();
            tableSocios.setItems(FXCollections.observableArrayList(lista));
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Error al cargar socios: " + e.getMessage());
            alert.show();
        }
    }

    @FXML
    private void onModificar() {
        Socio selected = tableSocios.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Seleccione un socio de la tabla para modificar.");
            alert.show();
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
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Seleccione un socio de la tabla para borrar.");
            alert.show();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminacion");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Esta seguro de que desea eliminar el socio " + selected.getNombre() + "?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    gestorDatos.deleteSocio(selected.getId());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Socio eliminado correctamente.");
                    alert.show();
                    loadData();
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Error al eliminar: " + e.getMessage());
                    alert.show();
                }
            }
        });
    }

    @FXML
    private void onVolver() {
        if (mainController != null) {
            mainController.showWelcome();
        }
    }
}
