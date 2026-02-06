package com.ad.gestordatos.dao;

import com.ad.gestordatos.model.Prestamo;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAOImpl implements PrestamoDAO {

    @Override
    public void create(Prestamo prestamo) throws Exception {
        String sql = "INSERT INTO prestamo (dia_prestamo, dia_vencimiento, importe, esta_pagado, id_socio) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDate(1, Date.valueOf(prestamo.getDiaPrestamo()));
            if (prestamo.getDiaVencimiento() != null) {
                stmt.setDate(2, Date.valueOf(prestamo.getDiaVencimiento()));
            } else {
                stmt.setNull(2, Types.DATE);
            }
            stmt.setFloat(3, prestamo.getImporte());
            stmt.setBoolean(4, prestamo.isEstaPagado());
            stmt.setInt(5, prestamo.getIdSocio());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating prestamo failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    prestamo.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating prestamo failed, no ID obtained.");
                }
            }
        }
    }

    @Override
    public Prestamo read(int id) throws Exception {
        String sql = "SELECT * FROM prestamo WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPrestamo(rs);
                }
            }
        }
        return null;
    }

    @Override
    public void update(Prestamo prestamo) throws Exception {
        String sql = "UPDATE prestamo SET dia_prestamo=?, dia_vencimiento=?, importe=?, esta_pagado=?, id_socio=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(prestamo.getDiaPrestamo()));
            if (prestamo.getDiaVencimiento() != null) {
                stmt.setDate(2, Date.valueOf(prestamo.getDiaVencimiento()));
            } else {
                stmt.setNull(2, Types.DATE);
            }
            stmt.setFloat(3, prestamo.getImporte());
            stmt.setBoolean(4, prestamo.isEstaPagado());
            stmt.setInt(5, prestamo.getIdSocio());
            stmt.setInt(6, prestamo.getId());

            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM prestamo WHERE id=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Prestamo> findAllBySocio(int idSocio) throws Exception {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT * FROM prestamo WHERE id_socio = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idSocio);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    prestamos.add(mapResultSetToPrestamo(rs));
                }
            }
        }
        return prestamos;
    }

    @Override
    public List<Prestamo> findAll() throws Exception {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT * FROM prestamo";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                prestamos.add(mapResultSetToPrestamo(rs));
            }
        }
        return prestamos;
    }

    private Prestamo mapResultSetToPrestamo(ResultSet rs) throws SQLException {
        Prestamo prestamo = new Prestamo();
        prestamo.setId(rs.getInt("id"));
        prestamo.setDiaPrestamo(rs.getDate("dia_prestamo").toLocalDate());
        Date diaVencimiento = rs.getDate("dia_vencimiento");
        if (diaVencimiento != null) {
            prestamo.setDiaVencimiento(diaVencimiento.toLocalDate());
        }
        prestamo.setImporte(rs.getFloat("importe"));
        prestamo.setEstaPagado(rs.getBoolean("esta_pagado"));
        prestamo.setIdSocio(rs.getInt("id_socio"));
        return prestamo;
    }
}
