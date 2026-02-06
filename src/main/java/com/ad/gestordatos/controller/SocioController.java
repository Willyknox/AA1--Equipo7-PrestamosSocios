package com.ad.gestordatos.controller;

import com.ad.gestordatos.model.Socio;
import com.ad.gestordatos.util.GestorDatos;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;

public class SocioController {

    @FXML
    private TextField txtDni;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtEmail;
    @FXML
    private DatePicker dpNacimiento;
    @FXML
    private CheckBox chkMasPrestamos;
    @FXML
    private Label lblError;
    @FXML
    private Label lblInfo;

    private GestorDatos gestorDatos;
    private List<Socio> sociosList;
    private int currentIndex = -1;

    public SocioController() {
        gestorDatos = new GestorDatos();
    }

    @FXML
    public void initialize() {
        refreshContext();
        if (sociosList != null && !sociosList.isEmpty()) {
            currentIndex = 0;
            showSocio(getSocioAt(currentIndex));
        } else {
            clearFields();
        }

        // Add listeners for basic validation feedback (optional per requirement
        // "Validation en linea")
        txtDni.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!Socio.isValidDni(newVal)) {
                lblError.setText("Formato DNI inválido (8 num + letra)");
            } else {
                lblError.setText("");
            }
        });
    }

    private void refreshContext() {
        try {
            sociosList = gestorDatos.getAllSocios();
        } catch (Exception e) {
            showError("Error al cargar socios: " + e.getMessage());
        }
    }

    private Socio getSocioAt(int index) {
        if (index >= 0 && index < sociosList.size()) {
            return sociosList.get(index);
        }
        return null;
    }

    private void showSocio(Socio socio) {
        if (socio != null) {
            txtDni.setText(socio.getDni());
            txtNombre.setText(socio.getNombre());
            txtEmail.setText(socio.getEmail());
            dpNacimiento.setValue(socio.getNacimiento());
            chkMasPrestamos.setSelected(socio.isMasPrestamos());
            lblInfo.setText("Socio ID: " + socio.getId());
        } else {
            clearFields();
            lblInfo.setText("No hay registros");
        }
    }

    @FXML
    private void onFirst() {
        if (!sociosList.isEmpty()) {
            currentIndex = 0;
            showSocio(getSocioAt(currentIndex));
        }
    }

    @FXML
    private void onPrev() {
        if (currentIndex > 0) {
            currentIndex--;
            showSocio(getSocioAt(currentIndex));
        }
    }

    @FXML
    private void onNext() {
        if (currentIndex < sociosList.size() - 1) {
            currentIndex++;
            showSocio(getSocioAt(currentIndex));
        }
    }

    @FXML
    private void onLast() {
        if (!sociosList.isEmpty()) {
            currentIndex = sociosList.size() - 1;
            showSocio(getSocioAt(currentIndex));
        }
    }

    @FXML
    private void onNew() {
        clearFields();
        currentIndex = -1; // Indicator for new record
        lblInfo.setText("Nuevo Socio");
        txtDni.requestFocus();
    }

    @FXML
    private void onSave() {
        try {
            // Validate
            String dni = txtDni.getText();
            String nombre = txtNombre.getText();
            String email = txtEmail.getText();
            java.time.LocalDate nacimiento = dpNacimiento.getValue();
            boolean masPrestamos = chkMasPrestamos.isSelected();

            Socio socio = new Socio(dni, nombre, email, nacimiento, masPrestamos);

            // Check validations implicitly by setters in model or explicitly here
            if (currentIndex == -1) {
                // Create
                gestorDatos.createSocio(socio);
                showInfo("Socio creado correctamente.");
            } else {
                // Update
                Socio existing = getSocioAt(currentIndex);
                if (existing != null) {
                    socio.setId(existing.getId());
                    gestorDatos.updateSocio(socio);
                    showInfo("Socio actualziado correctamente.");
                }
            }
            refreshContext();
            if (currentIndex == -1) {
                // if new, go to last
                onLast();
            } else {
                // stay? or reload
                showSocio(getSocioAt(currentIndex));
            }

        } catch (IllegalArgumentException ex) {
            showError("Validación: " + ex.getMessage());
        } catch (Exception ex) {
            showError("Error al guardar: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    private void onDelete() {
        if (currentIndex >= 0 && currentIndex < sociosList.size()) {
            Socio current = getSocioAt(currentIndex);
            try {
                gestorDatos.deleteSocio(current.getId());
                showInfo("Socio eliminado.");
                refreshContext();
                if (currentIndex >= sociosList.size()) {
                    currentIndex = sociosList.size() - 1;
                }
                showSocio(getSocioAt(currentIndex));
            } catch (Exception e) {
                showError("Error al eliminar: " + e.getMessage());
            }
        }
    }

    private void clearFields() {
        txtDni.clear();
        txtNombre.clear();
        txtEmail.clear();
        dpNacimiento.setValue(null);
        chkMasPrestamos.setSelected(false);
        lblError.setText("");
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.show();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }
}
