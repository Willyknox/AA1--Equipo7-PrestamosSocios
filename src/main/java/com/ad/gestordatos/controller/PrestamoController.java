package com.ad.gestordatos.controller;

import com.ad.gestordatos.model.Prestamo;
import com.ad.gestordatos.util.GestorDatos;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
    private TextField txtIdSocio;
    @FXML
    private Label lblError;
    @FXML
    private Label lblInfo;

    private GestorDatos gestorDatos;
    private List<Prestamo> prestamosList;
    private int currentIndex = -1;

    public PrestamoController() {
        gestorDatos = new GestorDatos();
    }

    @FXML
    public void initialize() {
        refreshContext();
        if (!prestamosList.isEmpty()) {
            currentIndex = 0;
            showPrestamo(getPrestamoAt(currentIndex));
        } else {
            clearFields();
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
            txtIdSocio.setText(String.valueOf(prestamo.getIdSocio()));
            lblInfo.setText("Prestamo ID: " + prestamo.getId());
        } else {
            clearFields();
            lblInfo.setText("No hay registros");
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
            int idSocio = Integer.parseInt(txtIdSocio.getText());

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
            showError("Error de formato numérico: " + e.getMessage());
        } catch (Exception ex) {
            showError("Error al guardar: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    private void onDelete() {
        if (currentIndex >= 0 && currentIndex < prestamosList.size()) {
            Prestamo current = getPrestamoAt(currentIndex);
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
    }

    private void clearFields() {
        dpPrestamo.setValue(null);
        dpVencimiento.setValue(null);
        txtImporte.clear();
        chkPagado.setSelected(false);
        txtIdSocio.clear();
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
