package com.ad.gestordatos.controller;

import com.ad.gestordatos.model.Prestamo;
import com.ad.gestordatos.model.PrestamoConSocio;
import com.ad.gestordatos.util.GestorDatos;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;

public class TablaController {

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

    private GestorDatos gestorDatos;
    private MainController mainController;

    public TablaController() {
        gestorDatos = new GestorDatos();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        tablePrestamos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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
        try {
            List<PrestamoConSocio> lista = gestorDatos.getAllPrestamosConSocio();
            tablePrestamos.setItems(FXCollections.observableArrayList(lista));
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Error al cargar datos: " + e.getMessage());
            alert.show();
        }
    }

    @FXML
    private void onEliminar() {
        PrestamoConSocio selected = tablePrestamos.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Seleccione un préstamo de la tabla para eliminar.");
            alert.show();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Está seguro de que desea eliminar este préstamo?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try {
                    gestorDatos.deletePrestamo(selected.getPrestamoId());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Préstamo eliminado correctamente.");
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
    private void onEditar() {
        PrestamoConSocio selected = tablePrestamos.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Seleccione un préstamo de la tabla para editar.");
            alert.show();
            return;
        }
        if (mainController != null) {
            // Buscar el prestamo completo para obtener el idSocio
            try {
                Prestamo prestamo = gestorDatos.readPrestamo(selected.getPrestamoId());
                mainController.loadPrestamoFormWithData(
                    prestamo.getId(),
                    prestamo.getDiaPrestamo(),
                    prestamo.getDiaVencimiento(),
                    prestamo.getImporte(),
                    prestamo.isEstaPagado(),
                    prestamo.getIdSocio()
                );
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Error al cargar préstamo: " + e.getMessage());
                alert.show();
            }
        }
    }

    @FXML
    private void onVolver() {
        if (mainController != null) {
            mainController.showWelcome();
        }
    }
}
