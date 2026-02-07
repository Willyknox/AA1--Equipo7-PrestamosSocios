package com.ad.gestordatos.util;

import com.ad.gestordatos.dao.*;
import com.ad.gestordatos.model.Prestamo;
import com.ad.gestordatos.model.PrestamoConSocio;
import com.ad.gestordatos.model.Socio;
import java.util.List;

/**
 * Service layer class to manage data operations.
 * Acts as an intermediary between Controllers and DAOs.
 */
public class GestorDatos {
    private final SocioDAO socioDAO;
    private final PrestamoDAO prestamoDAO;

    public GestorDatos() {
        this.socioDAO = new SocioDAOImpl();
        this.prestamoDAO = new PrestamoDAOImpl();
    }

    // ============================
    // Socio Operations
    // ============================

    public void createSocio(Socio socio) throws Exception {
        socioDAO.create(socio);
    }

    public Socio readSocio(int id) throws Exception {
        return socioDAO.read(id);
    }

    public void updateSocio(Socio socio) throws Exception {
        socioDAO.update(socio);
    }

    /**
     * Deletes a Socio by ID.
     * Note: This might fail if the Socio has associated Prestamos and the database
     * enforces foreign key constraints without CASCADE DELETE.
     * 
     * @param id The ID of the Socio to delete.
     * @throws Exception If delete operation fails.
     */
    public void deleteSocio(int id) throws Exception {
        socioDAO.delete(id);
    }

    public List<Socio> getAllSocios() throws Exception {
        return socioDAO.findAll();
    }

    // ============================
    // Prestamo Operations
    // ============================

    public void createPrestamo(Prestamo prestamo) throws Exception {
        prestamoDAO.create(prestamo);
    }

    public Prestamo readPrestamo(int id) throws Exception {
        return prestamoDAO.read(id);
    }

    public void updatePrestamo(Prestamo prestamo) throws Exception {
        prestamoDAO.update(prestamo);
    }

    public void deletePrestamo(int id) throws Exception {
        prestamoDAO.delete(id);
    }

    public List<Prestamo> getAllPrestamos() throws Exception {
        return prestamoDAO.findAll();
    }

    public List<Prestamo> getPrestamosBySocio(int idSocio) throws Exception {
        return prestamoDAO.findAllBySocio(idSocio);
    }

    /**
     * Retrieves all Prestamos joined with their Socio information.
     * Useful for displaying in tables where Socio name is needed.
     * 
     * @return List of PrestamoConSocio objects.
     * @throws Exception If database error occurs.
     */
    public List<PrestamoConSocio> getAllPrestamosConSocio() throws Exception {
        return prestamoDAO.findAllWithSocio();
    }
}
