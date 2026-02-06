package com.ad.gestordatos.model;

import java.time.LocalDate;

/**
 * Entity representing a Prestamo.
 */
public class Prestamo {
    private int id;
    private LocalDate diaPrestamo;
    private LocalDate diaVencimiento;
    private float importe;
    private boolean estaPagado;
    private int idSocio;

    public Prestamo() {
    }

    public Prestamo(int id, LocalDate diaPrestamo, LocalDate diaVencimiento, float importe, boolean estaPagado, int idSocio) {
        this.id = id;
        this.diaPrestamo = diaPrestamo;
        this.diaVencimiento = diaVencimiento;
        this.importe = importe;
        this.estaPagado = estaPagado;
        this.idSocio = idSocio;
    }

    public Prestamo(LocalDate diaPrestamo, LocalDate diaVencimiento, float importe, boolean estaPagado, int idSocio) {
        this.diaPrestamo = diaPrestamo;
        this.diaVencimiento = diaVencimiento;
        this.importe = importe;
        this.estaPagado = estaPagado;
        this.idSocio = idSocio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDiaPrestamo() {
        return diaPrestamo;
    }

    public void setDiaPrestamo(LocalDate diaPrestamo) {
        if (diaPrestamo == null) {
            throw new IllegalArgumentException("Dia prestamo cannot be null");
        }
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
         if (importe < 0) {
            throw new IllegalArgumentException("Importe cannot be negative");
        }
        this.importe = importe;
    }

    public boolean isEstaPagado() {
        return estaPagado;
    }

    public void setEstaPagado(boolean estaPagado) {
        this.estaPagado = estaPagado;
    }

    public int getIdSocio() {
        return idSocio;
    }

    public void setIdSocio(int idSocio) {
        this.idSocio = idSocio;
    }

    @Override
    public String toString() {
        return "Prestamo{" +
                "id=" + id +
                ", diaPrestamo=" + diaPrestamo +
                ", diaVencimiento=" + diaVencimiento +
                ", importe=" + importe +
                ", estaPagado=" + estaPagado +
                ", idSocio=" + idSocio +
                '}';
    }
}
