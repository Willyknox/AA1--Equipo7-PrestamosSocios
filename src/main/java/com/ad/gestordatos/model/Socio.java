package com.ad.gestordatos.model;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Entity representing a Socio.
 */
public class Socio {
    private int id;
    private String dni;
    private String nombre;
    private String email;
    private LocalDate nacimiento;
    private boolean masPrestamos;

    // Regex for DNI validation (8 digits followed by a letter)
    private static final Pattern DNI_PATTERN = Pattern.compile("^[0-9]{8}[A-Za-z]$");

    public Socio() {
    }

    public Socio(int id, String dni, String nombre, String email, LocalDate nacimiento, boolean masPrestamos) {
        this.id = id;
        this.dni = dni;
        this.nombre = nombre;
        this.email = email;
        this.nacimiento = nacimiento;
        this.masPrestamos = masPrestamos;
    }

    public Socio(String dni, String nombre, String email, LocalDate nacimiento, boolean masPrestamos) {
        this.dni = dni;
        this.nombre = nombre;
        this.email = email;
        this.nacimiento = nacimiento;
        this.masPrestamos = masPrestamos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        if (!isValidDni(dni)) {
            throw new IllegalArgumentException("Invalid DNI format");
        }
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre cannot be empty");
        }
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        this.email = email;
    }

    public LocalDate getNacimiento() {
        return nacimiento;
    }

    public void setNacimiento(LocalDate nacimiento) {
        if (nacimiento == null) {
            throw new IllegalArgumentException("Fecha de nacimiento cannot be null");
        }
        this.nacimiento = nacimiento;
    }

    public boolean isMasPrestamos() {
        return masPrestamos;
    }

    public void setMasPrestamos(boolean masPrestamos) {
        this.masPrestamos = masPrestamos;
    }

    public static boolean isValidDni(String dni) {
        return dni != null && DNI_PATTERN.matcher(dni).matches();
    }
    
    @Override
    public String toString() {
        return "Socio{" +
                "id=" + id +
                ", dni='" + dni + '\'' +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", nacimiento=" + nacimiento +
                ", masPrestamos=" + masPrestamos +
                '}';
    }
}
