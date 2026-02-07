package com.ad.gestordatos.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility class for initializing the database schema.
 * Checks if tables exist and runs the schema SQL script if necessary.
 */
public class DatabaseInitializer {

    /**
     * Initializes the database by creating schema if it doesn't exist.
     * 
     * @throws SQLException If a database access error occurs.
     * @throws IOException  If the schema file cannot be read.
     */
    public static void initialize() throws SQLException, IOException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            if (!tablesExist(conn)) {
                System.out.println("Initializing database schema...");
                createSchema(conn);
                System.out.println("Database schema initialized successfully.");
            } else {
                System.out.println("Database tables already exist. Skipping initialization.");
            }
        }
    }

    private static boolean tablesExist(Connection conn) throws SQLException {
        // Check for a core table, e.g., "socio"
        try (ResultSet rs = conn.getMetaData().getTables(null, null, "socio", null)) {
            return rs.next();
        }
    }

    private static void createSchema(Connection conn) throws SQLException, IOException {
        String schemaPath = "db_schema.sql";
        try (InputStream input = DatabaseInitializer.class.getClassLoader().getResourceAsStream(schemaPath)) {
            if (input == null) {
                throw new IOException("Schema file not found in classpath: " + schemaPath);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    Statement stmt = conn.createStatement()) {

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
                        // Remove the delimiter from the end
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
