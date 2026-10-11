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

            try (InputStream input =
                    DBConnection.class
                            .getClassLoader()
                            .getResourceAsStream("application.properties")) {

                if (input == null) {
                    throw new RuntimeException(
                            "application.properties not found"
                    );
                }

                properties.load(input);
            }

            HikariConfig config = new HikariConfig();

            // Railway environment variables
            String railwayHost = System.getenv("MYSQLHOST");
            String railwayPort = System.getenv("MYSQLPORT");
            String railwayDatabase = System.getenv("MYSQL_DATABASE");
            String railwayUser = System.getenv("MYSQLUSER");
            String railwayPassword = System.getenv("MYSQLPASSWORD");

            if (railwayHost != null
                    && !railwayHost.isBlank()
                    && railwayPort != null
                    && railwayDatabase != null
                    && railwayUser != null
                    && railwayPassword != null) {

                // Railway MySQL connection
                String jdbcUrl =
                        "jdbc:mysql://" + railwayHost + ":"
                        + railwayPort + "/" + railwayDatabase
                        + "?useSSL=true&requireSSL=true";

                config.setJdbcUrl(jdbcUrl);
                config.setUsername(railwayUser);
                config.setPassword(railwayPassword);

            } else {

                // Local MySQL connection
                config.setJdbcUrl(
                        properties.getProperty("db.url")
                );

                config.setUsername(
                        properties.getProperty("db.username")
                );

                String localPassword =
                        System.getenv("DB_PASSWORD");

                if (localPassword == null
                        || localPassword.isBlank()) {
                    throw new RuntimeException(
                            "DB_PASSWORD environment variable is missing"
                    );
                }

                config.setPassword(localPassword);
            }

            config.setDriverClassName(
                    "com.mysql.cj.jdbc.Driver"
            );

            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(10000);
            config.setIdleTimeout(60000);
            config.setMaxLifetime(1800000);

            dataSource = new HikariDataSource(config);

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