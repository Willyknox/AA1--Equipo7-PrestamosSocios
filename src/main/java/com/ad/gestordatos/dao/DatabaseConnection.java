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
            }
        } catch (IOException ex) {
            System.out.println("Error cargando db.properties, usando valores por defecto.");
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
                Class.forName("org.mariadb.jdbc.Driver");
                connection = DriverManager.getConnection(url, user, password);
            } catch (ClassNotFoundException e) {
                System.err.println("Error: Driver MariaDB no encontrado.");
                e.printStackTrace();
                throw new SQLException("Driver MariaDB no encontrado.", e);
            } catch (SQLException e) {
                System.err.println("Error: No se pudo conectar a la base de datos.");
                e.printStackTrace();
                throw e;
            }
        }
        return connection;
    }

    // --- Initialization Logic ---

    public static void initializeDatabase() throws SQLException, java.io.IOException {
        try (Connection conn = getInstance().getConnection()) {
            if (!tablesExist(conn)) {
                System.out.println("Tablas no encontradas. Inicializando esquema...");
                createSchema(conn);
                System.out.println("Esquema inicializado correctamente.");
            } else {
                System.out.println("Las tablas de la base de datos ya existen.");
            }
        }
    }

    private static boolean tablesExist(Connection conn) throws SQLException {
        try (java.sql.ResultSet rs = conn.getMetaData().getTables(null, null, "socio", null)) {
            return rs.next();
        }
    }

    private static void createSchema(Connection conn) throws SQLException, java.io.IOException {
        String schemaPath = "db_schema.sql";
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream(schemaPath)) {
            if (input == null) {
                throw new java.io.IOException("Archivo de esquema no encontrado: " + schemaPath);
            }

            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(input));
                 java.sql.Statement stmt = conn.createStatement()) {

                String delimiter = ";";
                StringBuilder currentStatement = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    String trimmedLine = line.trim();

                    if (trimmedLine.isEmpty() || trimmedLine.startsWith("--")) {
                        continue;
                    }

                    if (trimmedLine.toUpperCase().startsWith("DELIMITER")) {
                        delimiter = trimmedLine.substring("DELIMITER".length()).trim();
                        continue;
                    }

                    currentStatement.append(line).append("\n");

                    if (trimmedLine.endsWith(delimiter)) {
                        String sql = currentStatement.toString().trim();
                        sql = sql.substring(0, sql.lastIndexOf(delimiter));

                        if (!sql.trim().isEmpty()) {
                            stmt.execute(sql);
                        }
                        currentStatement.setLength(0);
                    }
                }
            }
        }
    }
}
