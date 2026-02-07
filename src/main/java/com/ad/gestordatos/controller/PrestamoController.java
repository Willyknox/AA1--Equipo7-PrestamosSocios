package com.ad.gestordatos.controller;

import com.ad.gestordatos.model.Prestamo;
import com.ad.gestordatos.model.Socio;
import com.ad.gestordatos.util.GestorDatos;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the Loan (Prestamo) Management Form.
 * Handles creation, modification, deletion, and navigation of loans.
 */
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

    private final GestorDatos gestorDatos;
    private List<Prestamo> prestamosList = new java.util.ArrayList<>();
    private int currentIndex = -1;
    private MainController mainController;

    /**
     * Constructor. Initializes the Data Manager.
     */
    public PrestamoController() {
        gestorDatos = new GestorDatos();
    }

    /**
     * Sets the main controller reference for navigation.
     * 
     * @param mainController The main application controller.
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Initializes the controller logic.
     * Loads the list of partners (Socios) and the list of loans.
     */
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

    /**
     * Populates the ComboBox with Socio objects.
     * Configures a StringConverter to display "Name (DNI)".
     */
    private void loadSociosInComboBox() {
        try {
            List<Socio> socios = gestorDatos.getAllSocios();
            cmbSocio.getItems().addAll(socios);
            cmbSocio.setConverter(new StringConverter<Socio>() {
                @Override
                public String toString(Socio socio) {
                    return (socio == null) ? "" : socio.getNombre() + " (" + socio.getDni() + ")";
                }

                @Override
                public Socio fromString(String string) {
                    // One-way conversion for display is sufficient here
                    return null;
                }
            });
        } catch (Exception e) {
            showError("Error loading partners (socios): " + e.getMessage());
        }
    }

    /**
     * Reloads the list of loans from the data source.
     */
    private void refreshContext() {
        try {
            prestamosList = gestorDatos.getAllPrestamos();
        } catch (Exception e) {
            showError("Error loading loans from database: " + e.getMessage());
        }
    }

    /**
     * Safely retrieves a Prestamo at the specified index.
     * 
     * @param index The index in the list.
     * @return The Prestamo object, or null if index is invalid.
     */
    private Prestamo getPrestamoAt(int index) {
        if (index >= 0 && index < prestamosList.size()) {
            return prestamosList.get(index);
        }
        return null;
    }

    /**
     * Updates the UI fields to display the details of the provided Prestamo.
     * 
     * @param prestamo The loan to display.
     */
    private void showPrestamo(Prestamo prestamo) {
        if (prestamo != null) {
            dpPrestamo.setValue(prestamo.getDiaPrestamo());
            dpVencimiento.setValue(prestamo.getDiaVencimiento());
            txtImporte.setText(String.valueOf(prestamo.getImporte()));
            chkPagado.setSelected(prestamo.isEstaPagado());
            selectSocioById(prestamo.getIdSocio());

            lblInfo.setText("Loan ID: " + prestamo.getId());
            lblError.setText(""); // Clear error messages
        } else {
            clearFields();
            lblInfo.setText("No records found");
        }
    }

    /**
     * Selects the Socio in the ComboBox that matches the given ID.
     * 
     * @param idSocio The ID of the socio to select.
     */
    private void selectSocioById(int idSocio) {
        for (Socio socio : cmbSocio.getItems()) {
            if (socio.getId() == idSocio) {
                cmbSocio.setValue(socio);
                return;
            }
        }
        cmbSocio.setValue(null);
    }

    /**
     * Prepares the form for editing an existing loan.
     * Used when navigating from the TableView.
     */
    public void loadPrestamoForEdit(int prestamoId, java.time.LocalDate diaPrestamo,
            java.time.LocalDate diaVencimiento, float importe, boolean pagado, int idSocio) {
        dpPrestamo.setValue(diaPrestamo);
        dpVencimiento.setValue(diaVencimiento);
        txtImporte.setText(String.valueOf(importe));
        chkPagado.setSelected(pagado);
        selectSocioById(idSocio);

        lblInfo.setText("Editing Loan ID: " + prestamoId);

        // Find and set the current index for consistency
        for (int i = 0; i < prestamosList.size(); i++) {
            if (prestamosList.get(i).getId() == prestamoId) {
                currentIndex = i;
                return;
            }
        }
    }

    // --- Navigation Actions ---

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
        lblInfo.setText("New Loan (Unsaved)");
        dpPrestamo.requestFocus();
    }

    /**
     * Saves the current loan (Create or Update).
     */
    @FXML
    private void onSave() {
        try {
            // Validation
            if (dpPrestamo.getValue() == null || dpVencimiento.getValue() == null) {
                lblError.setText("Dates are required.");
                return;
            }

            java.time.LocalDate diaPrestamo = dpPrestamo.getValue();
            java.time.LocalDate diaVencimiento = dpVencimiento.getValue();

            // Parse amount, handling comma as decimal separator
            String importeStr = txtImporte.getText().replace(",", ".");
            float importe = Float.parseFloat(importeStr);

            boolean pagado = chkPagado.isSelected();
            Socio socioSeleccionado = cmbSocio.getValue();

            if (socioSeleccionado == null) {
                lblError.setText("A partner (Socio) must be selected.");
                return;
            }
            int idSocio = socioSeleccionado.getId();

            Prestamo prestamo = new Prestamo(diaPrestamo, diaVencimiento, importe, pagado, idSocio);

            if (currentIndex == -1) {
                // Create new
                gestorDatos.createPrestamo(prestamo);
                showInfo("Loan created successfully.");
            } else {
                // Update existing
                Prestamo existing = getPrestamoAt(currentIndex);
                if (existing != null) {
                    prestamo.setId(existing.getId());
                    gestorDatos.updatePrestamo(prestamo);
                    showInfo("Loan updated successfully.");
                }
            }

            // Refresh list and maintain position
            refreshContext();
            if (currentIndex == -1) {
                onLast();
            } else {
                showPrestamo(getPrestamoAt(currentIndex));
            }

        } catch (NumberFormatException e) {
            lblError.setText("Invalid amount format.");
        } catch (Exception ex) {
            showError("Error saving loan: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    private void onDelete() {
        if (currentIndex >= 0 && currentIndex < prestamosList.size()) {
            Prestamo current = getPrestamoAt(currentIndex);

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Deletion");
            confirm.setHeaderText(null);
            confirm.setContentText("Are you sure you want to delete this loan?");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    gestorDatos.deletePrestamo(current.getId());
                    showInfo("Loan deleted.");

                    refreshContext();
                    // Adjust index if we deleted the last item
                    if (currentIndex >= prestamosList.size()) {
                        currentIndex = prestamosList.size() - 1;
                    }
                    showPrestamo(getPrestamoAt(currentIndex));
                } catch (Exception e) {
                    showError("Error deleting loan: " + e.getMessage());
                }
            }
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
        alert.setTitle("Error");
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
