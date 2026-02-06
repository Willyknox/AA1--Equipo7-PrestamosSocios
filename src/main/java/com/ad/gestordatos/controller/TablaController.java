package com.ad.gestordatos.controller;

import com.ad.gestordatos.model.Socio;
import com.ad.gestordatos.util.GestorDatos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;

public class TablaController {

    @FXML
    private TableView<Socio> tablaSocios;
    @FXML
    private TableColumn<Socio, Integer> colId;
    @FXML
    private TableColumn<Socio, String> colDni;
    @FXML
    private TableColumn<Socio, String> colNombre;
    @FXML
    private TableColumn<Socio, String> colEmail;
    @FXML
    private TableColumn<Socio, LocalDate> colNacimiento;
    @FXML
    private TableColumn<Socio, Boolean> colMasPrestamos;

    private GestorDatos gestorDatos;

    public void initialize() {
        gestorDatos = new GestorDatos();
        
        // Configurar columnas para que mapeen propiedades del modelo Socio
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colNacimiento.setCellValueFactory(new PropertyValueFactory<>("nacimiento"));
        colMasPrestamos.setCellValueFactory(new PropertyValueFactory<>("masPrestamos"));

        cargarDatos();
    }

    private void cargarDatos() {
        try {
            // Nota: En una app real, getAllSocios devuelve probablemente una lista normal java.util.List
            // JavaFX necesita ObservableList para la tabla.
            ObservableList<Socio> listaSocios = FXCollections.observableArrayList(gestorDatos.getAllSocios());
            tablaSocios.setItems(listaSocios);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error al cargar datos");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }
}
