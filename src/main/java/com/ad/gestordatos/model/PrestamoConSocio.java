package com.ad.gestordatos.model;

import java.time.LocalDate;

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
