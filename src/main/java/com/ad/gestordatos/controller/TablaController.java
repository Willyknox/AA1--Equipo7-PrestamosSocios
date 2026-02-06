package com.ad.gestordatos.controller;

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
    private TableColumn<PrestamoConSocio, String> colDni;
    @FXML
    private TableColumn<PrestamoConSocio, LocalDate> colDiaPrestamo;
    @FXML
    private TableColumn<PrestamoConSocio, LocalDate> colDiaVencimiento;
    @FXML
    private TableColumn<PrestamoConSocio, Float> colImporte;
    @FXML
    private TableColumn<PrestamoConSocio, Boolean> colPagado;

    private GestorDatos gestorDatos;

    public TablaController() {
        gestorDatos = new GestorDatos();
    }

    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreSocio"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dniSocio"));
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
}
