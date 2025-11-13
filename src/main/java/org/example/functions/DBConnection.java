package org.example.functions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static Connection getConnection() throws SQLException {

        String url = System.getenv("SQL_CONNECTION");
        if (url == null) {
            throw new SQLException("SQL_CONNECTION environment variable not set!");
        }

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQL Server JDBC Driver not found.", e);
        }

        return DriverManager.getConnection(url);
    }
}

