package com.ad.gestordatos.model;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) representing a Loan with its associated Partner's
 * details.
 * Used primarily for display in the TableView.
 */
public class PrestamoConSocio {
    private int prestamoId;
    private String nombreSocio;
    private String dniSocio;
    private LocalDate diaPrestamo;
    private LocalDate diaVencimiento;
    private float importe;
    private boolean estaPagado;

    public PrestamoConSocio() {
    }

    public PrestamoConSocio(int prestamoId, String nombreSocio, String dniSocio,
            LocalDate diaPrestamo, LocalDate diaVencimiento,
            float importe, boolean estaPagado) {
        this.prestamoId = prestamoId;
        this.nombreSocio = nombreSocio;
        this.dniSocio = dniSocio;
        this.diaPrestamo = diaPrestamo;
        this.diaVencimiento = diaVencimiento;
        this.importe = importe;
        this.estaPagado = estaPagado;
    }

    public int getPrestamoId() {
        return prestamoId;
    }

    public void setPrestamoId(int prestamoId) {
        this.prestamoId = prestamoId;
    }

    public String getNombreSocio() {
        return nombreSocio;
    }

    /**
     * Extracts only the first name from the full name string.
     * 
     * @return The first name.
     */
    public String getNombreSolo() {
        return splitName(0);
    }

    /**
     * Extracts the first surname from the full name string.
     * 
     * @return The first surname.
     */
    public String getApellido1Socio() {
        return splitName(1);
    }

    /**
     * Extracts the second surname from the full name string.
     * 
     * @return The second surname.
     */
    public String getApellido2Socio() {
        return splitName(2);
    }

    private String splitName(int index) {
        if (nombreSocio == null)
            return "";
        String[] partes = nombreSocio.split(" ", 3);
        return partes.length > index ? partes[index] : "";
    }

    public void setNombreSocio(String nombreSocio) {
        this.nombreSocio = nombreSocio;
    }

    public String getDniSocio() {
        return dniSocio;
    }

    public void setDniSocio(String dniSocio) {
        this.dniSocio = dniSocio;
    }

    public LocalDate getDiaPrestamo() {
        return diaPrestamo;
    }

    public void setDiaPrestamo(LocalDate diaPrestamo) {
        this.diaPrestamo = diaPrestamo;
    }

    public LocalDate getDiaVencimiento() {
        return diaVencimiento;
    }

    public void setDiaVencimiento(LocalDate diaVencimiento) {
        this.diaVencimiento = diaVencimiento;
    }

    public float getImporte() {
        return importe;
    }

    public void setImporte(float importe) {
        this.importe = importe;
    }

    public boolean isEstaPagado() {
        return estaPagado;
    }

    public void setEstaPagado(boolean estaPagado) {
        this.estaPagado = estaPagado;
    }
}
