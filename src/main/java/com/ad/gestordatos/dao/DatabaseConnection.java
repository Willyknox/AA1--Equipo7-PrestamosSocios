package com.ad.gestordatos.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/**
 * Singleton class for managing the Database Connection.
 * Reads configuration from db.properties or uses default values.
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    private String url = "jdbc:mariadb://localhost:3306/gestordatos";
    private String user = "root";
    private String password = "";

    private DatabaseConnection() {
        loadProperties();
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                Properties prop = new Properties();
                prop.load(input);
                this.url = prop.getProperty("db.url", this.url);
                this.user = prop.getProperty("db.user", this.user);
                this.password = prop.getProperty("db.password", this.password);
            } else {
                System.out.println("Warning: db.properties not found, using default credentials.");
            }
        } catch (IOException ex) {
            System.err.println("Error loading db.properties: " + ex.getMessage());
        }
    }

    /**
     * Returns the singleton instance of DatabaseConnection.
     * 
     * @return The instance.
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Establishes or retrieves the active database connection.
     * 
     * @return The Connection object.
     * @throws SQLException If connection fails.
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Ensure Mariadb driver is loaded
                Class.forName("org.mariadb.jdbc.Driver");
                connection = DriverManager.getConnection(url, user, password);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MariaDB JDBC Driver not found", e);
            }
        }
        return connection;
    }
}
