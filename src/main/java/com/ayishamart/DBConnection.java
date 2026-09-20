package com.ayishamart;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

public class DBConnection {

    private static final HikariDataSource dataSource;

    static {
        try {
            Properties properties = new Properties();

            InputStream input =
                    DBConnection.class
                            .getClassLoader()
                            .getResourceAsStream("application.properties");

            if (input == null) {
                throw new RuntimeException(
                        "application.properties not found"
                );
            }

            properties.load(input);
            input.close();

            HikariConfig config =
                    new HikariConfig();

            config.setJdbcUrl(
                    properties.getProperty("db.url")
            );

            // MySQL JDBC Driver
            config.setDriverClassName(
                    "com.mysql.cj.jdbc.Driver"
            );

            config.setUsername(
                    properties.getProperty("db.username")
            );

            config.setPassword(
                    System.getenv("DB_PASSWORD")
            );

            config.setMaximumPoolSize(10);

            config.setMinimumIdle(2);

            config.setConnectionTimeout(10000);

            config.setIdleTimeout(60000);

            config.setMaxLifetime(1800000);

            dataSource =
                    new HikariDataSource(config);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Database connection failed",
                    e
            );
        }
    }

    public static Connection getConnection()
            throws java.sql.SQLException {

        return dataSource.getConnection();
    }
}