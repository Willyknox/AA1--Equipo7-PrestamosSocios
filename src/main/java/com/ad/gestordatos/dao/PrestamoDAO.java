package com.ad.gestordatos.dao;

import com.ad.gestordatos.model.Prestamo;
import com.ad.gestordatos.model.PrestamoConSocio;
import java.util.List;

public interface PrestamoDAO {
    void create(Prestamo prestamo) throws Exception;

    Prestamo read(int id) throws Exception;

    void update(Prestamo prestamo) throws Exception;

    void delete(int id) throws Exception;

    List<Prestamo> findAllBySocio(int idSocio) throws Exception;

    List<Prestamo> findAll() throws Exception;

    List<PrestamoConSocio> findAllWithSocio() throws Exception;
}
