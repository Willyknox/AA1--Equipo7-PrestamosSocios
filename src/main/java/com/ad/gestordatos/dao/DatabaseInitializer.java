package com.ad.gestordatos.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseInitializer {

    public static void initialize() throws SQLException, IOException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            if (!tablesExist(conn)) {
                System.out.println("Database tables not found. Initializing schema...");
                createSchema(conn);
                System.out.println("Schema initialized successfully.");
            } else {
                System.out.println("Database tables already exist.");
            }
        }
    }

    private static boolean tablesExist(Connection conn) throws SQLException {
        // Check for a known table, e.g., "socio"
        try (ResultSet rs = conn.getMetaData().getTables(null, null, "socio", null)) {
            return rs.next();
        }
    }

    private static void createSchema(Connection conn) throws SQLException, IOException {
        String schemaPath = "db_schema.sql";
        try (InputStream input = DatabaseInitializer.class.getClassLoader().getResourceAsStream(schemaPath)) {
            if (input == null) {
                throw new IOException("Schema file not found: " + schemaPath);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                String sql = reader.lines().collect(Collectors.joining("\n"));

                // Split by semicolon to execute multiple statements if needed,
                // but usually simple schemas can be run in blocks if supported or split
                // manually.
                // For simplicity assuming the file contains valid SQL statements separated by ;
                String[] statements = sql.split(";");

                try (Statement stmt = conn.createStatement()) {
                    for (String statement : statements) {
                        if (!statement.trim().isEmpty()) {
                            stmt.execute(statement);
                        }
                    }
                }
            }
        }
    }
}
