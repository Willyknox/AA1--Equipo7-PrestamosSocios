package com.ad.gestordatos.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    private String url = "jdbc:mariadb://localhost:3306/gestordatos";
    private String user = "root";
    private String password = "Sandro.89";

    private DatabaseConnection() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                Properties prop = new Properties();
                prop.load(input);
                this.url = prop.getProperty("db.url", this.url);
                this.user = prop.getProperty("db.user", this.user);
                this.password = prop.getProperty("db.password", this.password);
                System.out.println("DEBUG: Loaded properties. URL: " + this.url + ", User: " + this.user);
            } else {
                System.err.println("DEBUG: db.properties not found in classpath!");
            }
        } catch (IOException ex) {
            System.out.println("Could not load db.properties, using defaults.");
            ex.printStackTrace();
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Ensure driver is loaded
                Class.forName("org.mariadb.jdbc.Driver");
                connection = DriverManager.getConnection(url, user, password);
            } catch (ClassNotFoundException e) {
                System.err.println("CRITICAL ERROR: MariaDB JDBC Driver not found!");
                e.printStackTrace();
                throw new SQLException("MariaDB JDBC Driver not found. Ensure the dependency is in pom.xml", e);
            } catch (SQLException e) {
                System.err.println("CRITICAL ERROR: Could not connect to database!");
                System.err.println("URL: " + url);
                System.err.println("User: " + user);
                e.printStackTrace();
                throw e;
            }
        }
        return connection;
    }
}
