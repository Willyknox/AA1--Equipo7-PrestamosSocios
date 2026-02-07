package com.ad.gestordatos.controller;

import com.ad.gestordatos.model.Socio;
import com.ad.gestordatos.util.GestorDatos;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Controller for the Partner (Socio) Management Form.
 * Handles creation, modification, deletion, and validation of partners.
 */
public class SocioController {

    @FXML
    private TextField txtDni;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido1;
    @FXML
    private TextField txtApellido2;
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

    private final GestorDatos gestorDatos;
    private List<Socio> sociosList = new java.util.ArrayList<>();
    private int currentIndex = -1;
    private MainController mainController;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");

    public SocioController() {
        gestorDatos = new GestorDatos();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
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

        setupValidationListeners();
    }

    /**
     * Sets up real-time validation listeners for input fields.
     */
    private void setupValidationListeners() {
        // Real-time DNI validation
        txtDni.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!Socio.isValidDni(newVal)) {
                lblError.setText("Invalid DNI format (8 digits + letter)");
            } else if (lblError.getText().contains("DNI")) {
                lblError.setText("");
            }
        });

        // Name and Surname validation: only letters and spaces
        String soloLetrasRegex = "[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ ]+";
        ValidationListener nameListener = (obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty() && !newVal.matches(soloLetrasRegex)) {
                ((TextField) ((javafx.beans.property.StringProperty) obs).getBean()).setText(oldVal);
            }
        };

        txtNombre.textProperty().addListener(nameListener);
        txtApellido1.textProperty().addListener(nameListener);
        txtApellido2.textProperty().addListener(nameListener);

        // Email validation on focus lost
        txtEmail.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                String email = txtEmail.getText();
                if (email != null && !email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
                    lblError.setText("Invalid email format (example@domain.com)");
                } else if (lblError.getText().contains("email")) {
                    lblError.setText("");
                }
            }
        });
    }

    // interface for simple casting in listener above
    private interface ValidationListener extends javafx.beans.value.ChangeListener<String> {
    }

    private void refreshContext() {
        try {
            sociosList = gestorDatos.getAllSocios();
        } catch (Exception e) {
            showError("Error loading partners: " + e.getMessage());
        }
    }

    private Socio getSocioAt(int index) {
        if (sociosList != null && index >= 0 && index < sociosList.size()) {
            return sociosList.get(index);
        }
        return null;
    }

    private void showSocio(Socio socio) {
        if (socio != null) {
            txtDni.setText(socio.getDni());

            // Split full name into parts
            String[] partes = socio.getNombre() != null ? socio.getNombre().split(" ", 3) : new String[] { "" };
            txtNombre.setText(partes.length > 0 ? partes[0] : "");
            txtApellido1.setText(partes.length > 1 ? partes[1] : "");
            txtApellido2.setText(partes.length > 2 ? partes[2] : "");

            txtEmail.setText(socio.getEmail());
            dpNacimiento.setValue(socio.getNacimiento());
            chkMasPrestamos.setSelected(socio.isMasPrestamos());

            lblInfo.setText("Partner ID: " + socio.getId());
            lblError.setText("");
        } else {
            clearFields();
            lblInfo.setText("No records");
        }
    }

    // --- Navigation ---

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
        currentIndex = -1;
        lblInfo.setText("New Partner (Unsaved)");
        txtDni.requestFocus();
    }

    @FXML
    private void onSave() {
        try {
            String dni = txtDni.getText();
            String nombreTxt = txtNombre.getText();
            String apellido1 = txtApellido1.getText();
            String apellido2 = txtApellido2.getText();
            String email = txtEmail.getText();
            java.time.LocalDate nacimiento = dpNacimiento.getValue();
            boolean masPrestamos = chkMasPrestamos.isSelected();

            // Validation
            if (!Socio.isValidDni(dni)) {
                lblError.setText("Invalid DNI (8 digits + letter)");
                return;
            }
            if (isEmpty(nombreTxt) || isEmpty(apellido1) || isEmpty(apellido2)) {
                lblError.setText("Name and both surnames are required");
                return;
            }
            // Construct full name
            String nombre = (nombreTxt.trim() + " " + apellido1.trim() + " " + apellido2.trim()).trim();

            if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
                lblError.setText("Invalid Email");
                return;
            }
            if (nacimiento == null) {
                lblError.setText("Birth date is required");
                return;
            }

            Socio socio = new Socio(dni, nombre, email, nacimiento, masPrestamos);

            if (currentIndex == -1) {
                gestorDatos.createSocio(socio);
                showInfo("Partner created successfully.");
            } else {
                Socio existing = getSocioAt(currentIndex);
                if (existing != null) {
                    socio.setId(existing.getId());
                    gestorDatos.updateSocio(socio);
                    showInfo("Partner updated successfully.");
                }
            }
            refreshContext();
            if (currentIndex == -1) {
                onLast();
            } else {
                showSocio(getSocioAt(currentIndex));
            }

        } catch (IllegalArgumentException ex) {
            lblError.setText("Validation: " + ex.getMessage());
        } catch (Exception ex) {
            showError("Error saving: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    @FXML
    private void onDelete() {
        if (currentIndex >= 0 && currentIndex < sociosList.size()) {
            Socio current = getSocioAt(currentIndex);

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Deletion");
            confirm.setHeaderText(null);
            confirm.setContentText("Are you sure you want to delete partner " + current.getNombre() + "?");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    gestorDatos.deleteSocio(current.getId());
                    showInfo("Partner deleted.");

                    refreshContext();
                    if (currentIndex >= sociosList.size()) {
                        currentIndex = sociosList.size() - 1;
                    }
                    showSocio(getSocioAt(currentIndex));
                } catch (Exception e) {
                    showError("Error deleting: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Loads a socio for editing. Called from the TableView.
     * 
     * @param socio The socio to edit.
     */
    public void loadSocioForEdit(Socio socio) {
        refreshContext();
        // Find existing index
        for (int i = 0; i < sociosList.size(); i++) {
            if (sociosList.get(i).getId() == socio.getId()) {
                currentIndex = i;
                break;
            }
        }
        showSocio(socio);
    }

    @FXML
    private void onVolver() {
        if (mainController != null) {
            mainController.showWelcome();
        }
    }

    private void clearFields() {
        txtDni.clear();
        txtNombre.clear();
        txtApellido1.clear();
        txtApellido2.clear();
        txtEmail.clear();
        dpNacimiento.setValue(null);
        chkMasPrestamos.setSelected(false);
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
