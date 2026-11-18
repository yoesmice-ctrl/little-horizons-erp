package com.littlehorizons.erp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton class responsible for managing the MySQL database connection.
 */
public class DatabaseConnection {

    private static DatabaseConnection instance;

    private static final String URL = "jdbc:mysql://localhost:3306/erp_little_horizons?useUnicode=true&characterEncoding=UTF-8&serverTimezone=America/Bogota";
    private static final String USER = "campus2023";     // Change to "campus 2023" if it has a space
    private static final String PASSWORD = "campus2023"; // Change to "campus 2023" if it has a space

    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            ErrorLogger.logError("DatabaseConnection", "MySQL JDBC Driver not found", e);
            throw new RuntimeException("Failed to load MySQL JDBC Driver", e);
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Opens and returns a new JDBC connection to be used inside try-with-resources blocks.
     *
     * @return an active Connection to erp_little_horizons
     * @throws SQLException if a database access error occurs
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
