package com.ad.gestordatos.controller;

import com.ad.gestordatos.model.Socio;
import com.ad.gestordatos.util.GestorDatos;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;
import java.util.regex.Pattern;

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

    private GestorDatos gestorDatos;
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
        if (!sociosList.isEmpty()) {
            currentIndex = 0;
            showSocio(getSocioAt(currentIndex));
        } else {
            clearFields();
        }

        // Validacion DNI en tiempo real
        txtDni.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!Socio.isValidDni(newVal)) {
                lblError.setText("Formato DNI inválido (8 num + letra)");
            } else if (lblError.getText().contains("DNI")) {
                lblError.setText("");
            }
        });

        // Validacion Nombre, Apellido1, Apellido2: solo letras y espacios
        String soloLetrasRegex = "[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ ]+";
        txtNombre.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty() && !newVal.matches(soloLetrasRegex)) {
                txtNombre.setText(oldVal);
            }
        });
        txtApellido1.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty() && !newVal.matches(soloLetrasRegex)) {
                txtApellido1.setText(oldVal);
            }
        });
        txtApellido2.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty() && !newVal.matches(soloLetrasRegex)) {
                txtApellido2.setText(oldVal);
            }
        });

        // Validacion Email al perder el foco
        txtEmail.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                String email = txtEmail.getText();
                if (email != null && !email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
                    lblError.setText("Formato email inválido (ejemplo@dominio.com)");
                } else if (lblError.getText().contains("email")) {
                    lblError.setText("");
                }
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
            // Separar nombre completo en nombre, apellido1, apellido2
            String[] partes = socio.getNombre() != null ? socio.getNombre().split(" ", 3) : new String[]{""};
            txtNombre.setText(partes.length > 0 ? partes[0] : "");
            txtApellido1.setText(partes.length > 1 ? partes[1] : "");
            txtApellido2.setText(partes.length > 2 ? partes[2] : "");
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
        currentIndex = -1;
        lblInfo.setText("Nuevo Socio");
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

            // Validaciones antes de guardar
            if (!Socio.isValidDni(dni)) {
                lblError.setText("DNI inválido (8 dígitos + letra)");
                return;
            }
            if (nombreTxt == null || nombreTxt.trim().isEmpty()) {
                lblError.setText("El nombre no puede estar vacío");
                return;
            }
            if (apellido1 == null || apellido1.trim().isEmpty()) {
                lblError.setText("El primer apellido no puede estar vacío");
                return;
            }
            if (apellido2 == null || apellido2.trim().isEmpty()) {
                lblError.setText("El segundo apellido no puede estar vacío");
                return;
            }
            // Concatenar nombre completo
            String nombre = (nombreTxt.trim() + " " + apellido1.trim() + " " + apellido2.trim()).trim();
            if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
                lblError.setText("Email inválido (ejemplo@dominio.com)");
                return;
            }
            if (nacimiento == null) {
                lblError.setText("La fecha de nacimiento es obligatoria");
                return;
            }

            Socio socio = new Socio(dni, nombre, email, nacimiento, masPrestamos);

            if (currentIndex == -1) {
                gestorDatos.createSocio(socio);
                showInfo("Socio creado correctamente.");
            } else {
                Socio existing = getSocioAt(currentIndex);
                if (existing != null) {
                    socio.setId(existing.getId());
                    gestorDatos.updateSocio(socio);
                    showInfo("Socio actualizado correctamente.");
                }
            }
            refreshContext();
            if (currentIndex == -1) {
                onLast();
            } else {
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
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar eliminación");
            confirm.setHeaderText(null);
            confirm.setContentText("¿Está seguro de que desea eliminar el socio " + current.getNombre() + "?");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
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
            });
        }
    }

    public void loadSocioForEdit(Socio socio) {
        refreshContext();
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
        alert.setContentText(msg);
        alert.show();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }
}
