package com.ad.gestordatos.util;

import com.ad.gestordatos.dao.*;
import com.ad.gestordatos.model.Prestamo;
import com.ad.gestordatos.model.Socio;
import java.util.List;

public class GestorDatos {
    private final SocioDAO socioDAO;
    private final PrestamoDAO prestamoDAO;

    public GestorDatos() {
        this.socioDAO = new SocioDAOImpl();
        this.prestamoDAO = new PrestamoDAOImpl();
    }

    // Socio operations
    public void createSocio(Socio socio) throws Exception {
        socioDAO.create(socio);
    }

    public Socio readSocio(int id) throws Exception {
        return socioDAO.read(id);
    }

    public void updateSocio(Socio socio) throws Exception {
        socioDAO.update(socio);
    }

    public void deleteSocio(int id) throws Exception {
        // Optional: Check/Delete related prestamos first?
        // For now, assume CASCADE or manual deletion handling in UI logic
        socioDAO.delete(id);
    }

    public List<Socio> getAllSocios() throws Exception {
        return socioDAO.findAll();
    }

    // Prestamo operations
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
}
