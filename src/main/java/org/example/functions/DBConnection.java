package org.example.functions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;

public class DBConnection
{

    public static Connection getConnection() throws SQLException
    {

        String url = System.getenv("SQL_CONNECTION");
        System.out.println("DEBUG SQL_CONNECTION = " + url);

        if (url == null)
        {
            throw new SQLException("SQL_CONNECTION environment variable not set!");
        }

        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e)
        {
            throw new SQLException("SQL Server JDBC Driver not found.", e);
        }
        return DriverManager.getConnection(url);
    }

    public static void createTable(Connection database) throws SQLException
    {
        // Create a Statement object with which we can execute SQL commands

        Statement statement = database.createStatement();

        // Drop existing table, if present

        try
        {
            statement.executeUpdate("DROP TABLE sensordata");
            System.out.println("table dropped");
        } catch (SQLException error)
        {
            // Catch and ignore SQLException, as this merely indicates
            // that the table didn't exist in the first place!
        }


        statement.executeUpdate("CREATE TABLE sensordata ("
                + "sensorID INT NOT NULL,"
                + "temp INT NOT NULL,"
                + "wind INT NOT NULL,"
                + "humidity INT NOT NULL,"
                + "co2 INT NOT NULL)"
        );

        statement.close();
    }
}

