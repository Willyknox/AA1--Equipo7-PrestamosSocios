package com.ad.gestordatos.dao;

import com.ad.gestordatos.model.Socio;
import java.util.List;

public interface SocioDAO {
    void create(Socio socio) throws Exception;

    Socio read(int id) throws Exception;

    void update(Socio socio) throws Exception;

    void delete(int id) throws Exception;

    List<Socio> findAll() throws Exception;
    // Additional methods for navigation if needed, or just use findAll and
    // navigation in memory for simple requirements
}
