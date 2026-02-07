package com.ad.gestordatos.dao;

import com.ad.gestordatos.model.Socio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the SocioDAO interface.
 * Handles database operations for Partner (Socio) entities.
 */
public class SocioDAOImpl implements SocioDAO {

    @Override
    public void create(Socio socio) throws Exception {
        String sql = "INSERT INTO socio (dni, nombre, email, nacimiento, mas_prestamos) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, socio.getDni());
            stmt.setString(2, socio.getNombre());
            stmt.setString(3, socio.getEmail());
            stmt.setDate(4, Date.valueOf(socio.getNacimiento()));
            stmt.setBoolean(5, socio.isMasPrestamos());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating socio failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    socio.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating socio failed, no ID obtained.");
                }
            }
        }
    }

    @Override
    public Socio read(int id) throws Exception {
        String sql = "SELECT * FROM socio WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSocio(rs);
                }
            }
        }
        return null;
    }

    @Override
    public void update(Socio socio) throws Exception {
        String sql = "UPDATE socio SET dni=?, nombre=?, email=?, nacimiento=?, mas_prestamos=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, socio.getDni());
            stmt.setString(2, socio.getNombre());
            stmt.setString(3, socio.getEmail());
            stmt.setDate(4, Date.valueOf(socio.getNacimiento()));
            stmt.setBoolean(5, socio.isMasPrestamos());
            stmt.setInt(6, socio.getId());

            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM socio WHERE id=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Socio> findAll() throws Exception {
        List<Socio> socios = new ArrayList<>();
        String sql = "SELECT * FROM socio";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                socios.add(mapResultSetToSocio(rs));
            }
        }
        return socios;
    }

    private Socio mapResultSetToSocio(ResultSet rs) throws SQLException {
        Socio socio = new Socio();
        socio.setId(rs.getInt("id"));
        socio.setDni(rs.getString("dni"));
        socio.setNombre(rs.getString("nombre"));
        socio.setEmail(rs.getString("email"));
        socio.setNacimiento(rs.getDate("nacimiento").toLocalDate());
        socio.setMasPrestamos(rs.getBoolean("mas_prestamos"));
        return socio;
    }
}
