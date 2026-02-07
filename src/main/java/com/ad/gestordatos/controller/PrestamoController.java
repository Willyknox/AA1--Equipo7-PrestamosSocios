package com.ad.gestordatos.controller;

import com.ad.gestordatos.model.Prestamo;
import com.ad.gestordatos.model.Socio;
import com.ad.gestordatos.util.GestorDatos;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import java.util.List;

public class PrestamoController {

    @FXML
    private DatePicker dpPrestamo;
    @FXML
    private DatePicker dpVencimiento;
    @FXML
    private TextField txtImporte;
    @FXML
    private CheckBox chkPagado;
    @FXML
    private ComboBox<Socio> cmbSocio;
    @FXML
    private Label lblError;
    @FXML
    private Label lblInfo;

    private GestorDatos gestorDatos;
    private List<Prestamo> prestamosList = new java.util.ArrayList<>();
    private int currentIndex = -1;
    private MainController mainController;

    public PrestamoController() {
        gestorDatos = new GestorDatos();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        loadSociosInComboBox();
        refreshContext();
        if (!prestamosList.isEmpty()) {
            currentIndex = 0;
            showPrestamo(getPrestamoAt(currentIndex));
        } else {
            clearFields();
        }
    }

    private void loadSociosInComboBox() {
        try {
            List<Socio> socios = gestorDatos.getAllSocios();
            cmbSocio.getItems().addAll(socios);
            cmbSocio.setConverter(new StringConverter<Socio>() {
                @Override
                public String toString(Socio socio) {
                    if (socio == null) return "";
                    return socio.getNombre() + " (" + socio.getDni() + ")";
                }

                @Override
                public Socio fromString(String string) {
                    return null;
                }
            });
        } catch (Exception e) {
            showError("Error al cargar socios: " + e.getMessage());
        }
    }

    private void refreshContext() {
        try {
            prestamosList = gestorDatos.getAllPrestamos();
        } catch (Exception e) {
            showError("Error al cargar prestamos: " + e.getMessage());
        }
    }

    private Prestamo getPrestamoAt(int index) {
        if (index >= 0 && index < prestamosList.size()) {
            return prestamosList.get(index);
        }
        return null;
    }

    private void showPrestamo(Prestamo prestamo) {
        if (prestamo != null) {
            dpPrestamo.setValue(prestamo.getDiaPrestamo());
            dpVencimiento.setValue(prestamo.getDiaVencimiento());
            txtImporte.setText(String.valueOf(prestamo.getImporte()));
            chkPagado.setSelected(prestamo.isEstaPagado());
            // Seleccionar el socio en el ComboBox
            selectSocioById(prestamo.getIdSocio());
            lblInfo.setText("Prestamo ID: " + prestamo.getId());
        } else {
            clearFields();
            lblInfo.setText("No hay registros");
        }
    }

    private void selectSocioById(int idSocio) {
        for (Socio socio : cmbSocio.getItems()) {
            if (socio.getId() == idSocio) {
                cmbSocio.setValue(socio);
                return;
            }
        }
        cmbSocio.setValue(null);
    }

    public void loadPrestamoForEdit(int prestamoId, java.time.LocalDate diaPrestamo,
            java.time.LocalDate diaVencimiento, float importe, boolean pagado, int idSocio) {
        dpPrestamo.setValue(diaPrestamo);
        dpVencimiento.setValue(diaVencimiento);
        txtImporte.setText(String.valueOf(importe));
        chkPagado.setSelected(pagado);
        selectSocioById(idSocio);
        lblInfo.setText("Editando Prestamo ID: " + prestamoId);
        // Buscar el indice del prestamo para actualizar en vez de crear
        for (int i = 0; i < prestamosList.size(); i++) {
            if (prestamosList.get(i).getId() == prestamoId) {
                currentIndex = i;
                return;
            }
        }
    }

    @FXML
    private void onFirst() {
        if (!prestamosList.isEmpty()) {
            currentIndex = 0;
            showPrestamo(getPrestamoAt(currentIndex));
        }
    }

    @FXML
    private void onPrev() {
        if (currentIndex > 0) {
            currentIndex--;
            showPrestamo(getPrestamoAt(currentIndex));
        }
    }

    @FXML
    private void onNext() {
        if (currentIndex < prestamosList.size() - 1) {
            currentIndex++;
            showPrestamo(getPrestamoAt(currentIndex));
        }
    }

    @FXML
    private void onLast() {
        if (!prestamosList.isEmpty()) {
            currentIndex = prestamosList.size() - 1;
            showPrestamo(getPrestamoAt(currentIndex));
        }
    }

    @FXML
    private void onNew() {
        clearFields();
        currentIndex = -1;
        lblInfo.setText("Nuevo Prestamo");
        dpPrestamo.requestFocus();
    }

    @FXML
    private void onSave() {
        try {
            java.time.LocalDate diaPrestamo = dpPrestamo.getValue();
            java.time.LocalDate diaVencimiento = dpVencimiento.getValue();
            float importe = Float.parseFloat(txtImporte.getText());
            boolean pagado = chkPagado.isSelected();

            Socio socioSeleccionado = cmbSocio.getValue();
            if (socioSeleccionado == null) {
                lblError.setText("Debe seleccionar un socio");
                return;
            }
            int idSocio = socioSeleccionado.getId();

            Prestamo prestamo = new Prestamo(diaPrestamo, diaVencimiento, importe, pagado, idSocio);

            if (currentIndex == -1) {
                gestorDatos.createPrestamo(prestamo);
                showInfo("Prestamo creado correctamente.");
            } else {
                Prestamo existing = getPrestamoAt(currentIndex);
                if (existing != null) {
                    prestamo.setId(existing.getId());
                    gestorDatos.updatePrestamo(prestamo);
                    showInfo("Prestamo actualizado correctamente.");
                }
            }
            refreshContext();
            if (currentIndex == -1) {
                onLast();
            } else {
                showPrestamo(getPrestamoAt(currentIndex));
            }

        } catch (NumberFormatException e) {
            lblError.setText("Importe debe ser un número válido");
        } catch (Exception ex) {
            showError("Error al guardar: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    private void onDelete() {
        if (currentIndex >= 0 && currentIndex < prestamosList.size()) {
            Prestamo current = getPrestamoAt(currentIndex);
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar eliminación");
            confirm.setHeaderText(null);
            confirm.setContentText("¿Está seguro de que desea eliminar este préstamo?");
            confirm.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    try {
                        gestorDatos.deletePrestamo(current.getId());
                        showInfo("Prestamo eliminado.");
                        refreshContext();
                        if (currentIndex >= prestamosList.size()) {
                            currentIndex = prestamosList.size() - 1;
                        }
                        showPrestamo(getPrestamoAt(currentIndex));
                    } catch (Exception e) {
                        showError("Error al eliminar: " + e.getMessage());
                    }
                }
            });
        }
    }

    @FXML
    private void onVolver() {
        if (mainController != null) {
            mainController.showWelcome();
        }
    }

    private void clearFields() {
        dpPrestamo.setValue(null);
        dpVencimiento.setValue(null);
        txtImporte.clear();
        chkPagado.setSelected(false);
        cmbSocio.setValue(null);
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
