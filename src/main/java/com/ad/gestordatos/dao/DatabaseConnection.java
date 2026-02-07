package com.ad.gestordatos.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Singleton class for managing the Database Connection.
 * Reads configuration from db.properties or uses default values.
 * Automatically initializes the database if it does not exist.
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    private String url = "jdbc:mariadb://localhost:3306/gestordatos";
    private String user = "root";
    private String password = ""; // Default empty, usually correct for local dev if no password set

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
            boolean connected = false;
            String[] commonPasswords = { password, "root", "1234", "", "admin" };

            try {
                // Ensure Mariadb driver is loaded
                Class.forName("org.mariadb.jdbc.Driver");
                DriverManager.setLoginTimeout(5);

                for (String pwd : commonPasswords) {
                    try {
                        System.out.println("Attempting connection to " + url + " with user: " + user + " and password: "
                                + (pwd.isEmpty() ? "<empty>" : pwd));
                        connection = DriverManager.getConnection(url, user, pwd);
                        System.out.println("Connection successful!");
                        this.password = pwd; // Store working password
                        connected = true;
                        break;
                    } catch (SQLException e) {
                        // Check if error is "Unknown database" (ErrorCode 1049)
                        if (e.getErrorCode() == 1049) {
                            System.out.println("Database does not exist. Attempting to create it with password: "
                                    + (pwd.isEmpty() ? "<empty>" : pwd));
                            this.password = pwd; // Temporarily assume this password works for server connection
                            initializeDatabase();
                            // Retry connection to the now-created DB
                            connection = DriverManager.getConnection(url, user, pwd);
                            connected = true;
                            break;
                        } else if (e.getErrorCode() == 1045) {
                            System.out.println("Access denied for password: " + (pwd.isEmpty() ? "<empty>" : pwd));
                            continue; // Try next password
                        } else {
                            throw e; // Other error, rethrow
                        }
                    }
                }

                if (!connected) {
                    throw new SQLException("Failed to connect to database using any of the attempted passwords.");
                }

            } catch (ClassNotFoundException e) {
                throw new SQLException("MariaDB JDBC Driver not found", e);
            }
        }
        return connection;
    }

    private void initializeDatabase() throws SQLException {
        // Connect to the server without a specific database
        // Need to handle potential URL formats carefully. Assuming
        // jdbc:mariadb://host:port/db
        String serverUrl = url;
        int lastSlash = url.lastIndexOf("/");
        if (lastSlash > "jdbc:mariadb://".length()) {
            serverUrl = url.substring(0, lastSlash);
        }

        System.out.println("Connecting to server URL: " + serverUrl + " to create database...");

        try (Connection rootConnection = DriverManager.getConnection(serverUrl, user, password);
                Statement stmt = rootConnection.createStatement()) {

            // Create Database first (idempotent)
            stmt.execute("CREATE DATABASE IF NOT EXISTS gestordatos");
            System.out.println("Database 'gestordatos' created or already exists.");
        } catch (SQLException e) {
            System.err.println("Error creating database: " + e.getMessage());
            throw e;
        }

        // Connect to the new DB to create tables and triggers
        try (Connection dbConnection = DriverManager.getConnection(url, user, password);
                Statement stmt = dbConnection.createStatement()) {

            String schemaScript = loadSchemaScript();
            if (schemaScript == null) {
                System.err.println("Schema script not found/empty. Skipping table creation.");
                return;
            }

            // Split by our custom marker
            String[] statements = schemaScript.split("-- STATEMENT_END");

            for (String sql : statements) {
                String command = sql.trim();
                if (command.isEmpty())
                    continue;

                // Remove "USE gestordatos" if present, as we are already connected to it
                if (command.toUpperCase().startsWith("USE "))
                    continue;

                try {
                    stmt.execute(command);
                } catch (SQLException ex) {
                    // Check if it's "Table already exists" (error 1050), though DROP IF EXISTS
                    // should handle it.
                    System.err.println("Error executing init SQL: "
                            + command.substring(0, Math.min(command.length(), 50)) + "...");
                    System.err.println("Error message: " + ex.getMessage());
                    // Propagate the error? Or continue?
                    // Failing fast is safer for "portable" reliability.
                    throw new SQLException("Failed to execute SQL statement: " + command, ex);
                }
            }
            System.out.println("Database initialized successfully with schema.");
        } catch (Exception e) {
            throw new SQLException("Failed to initialize database: " + e.getMessage(), e);
        }
    }

    private String loadSchemaScript() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db_schema.sql")) {
            if (input == null)
                return null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException e) {
            System.err.println("Error reading db_schema.sql: " + e.getMessage());
            return null;
        }
    }
}
