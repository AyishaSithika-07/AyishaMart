package com.ayishamart;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/ayisha_mart";

    private static final String USER = "root";

    private static final String PASSWORD = "Ayisha@2007";

    public static Connection getConnection() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(
                    URL,
                    USER,
                    PASSWORD
            );

        } catch (Exception e) {
            throw new RuntimeException("Database connection failed:"+e.getMessage(),e);
        }
    }
}