package org.example.functions;

import java.io.*;
import java.sql.*;
import java.util.*;




public class CreateDB {


  /**
   * Establishes a connection to the database.
   *
   * The details of which driver to use, which database to
   * access and the username and password to use are being
   * hard-coded. 
   * Refer to the connection string, JDBC SQL authentication
   * on Azure 
   *
   * @return Connection object representing the connection
   * @throws IOException if properties file cannot be accessed
   * @throws SQLException if connection fails
   */

  public static Connection getConnection() throws IOException, SQLException
  {
    try {
      String url = "jdbc:sqlserver://leeds-data-server.database.windows.net:1433;database=leeds-data;user=TheGrid@leeds-data-server;password=IlanAlp123;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
      Connection connection = DriverManager.getConnection(url);
      System.out.println();
      return connection;
    }
    catch (SQLException e) {
      throw new SQLException(e.getMessage());
    }
  }

  /**
   * Creates a table to hold the data.
   *
   * @param database connection to database
   * @throws SQLException if table creation fails
   */

  public static void createTable(Connection database) throws SQLException
  {
    // Create a Statement object with which we can execute SQL commands

    Statement statement = database.createStatement();

    // Drop existing table, if present

    try {
      statement.executeUpdate("DROP TABLE sensordata");
    }
    catch (SQLException error) {
      // Catch and ignore SQLException, as this merely indicates
      // that the table didn't exist in the first place!
    }

    // Create a fresh table

    statement.executeUpdate("CREATE TABLE sensordata ("
                          + "sensorID INT NOT NULL PRIMARY KEY,"
                          + "temp INT NOT NULL,"
                          + "wind INT NOT NULL,"
                          + "humidity INT NOT NULL,"
                          + "co2 INT NOT NULL)"
                          );

    statement.close();
  }


  /**
   * Adds data to the table.
   *
   * @param in source of data
   * @param database connection to database
   * @throws IOException if there is a problem reading from the file
   * @throws SQLException if insertion fails for any reason
   */

  public static void addData(BufferedReader in, Connection database)
   throws IOException, SQLException
  {
    // Prepare statement used to insert data

    PreparedStatement statement =
     database.prepareStatement("INSERT INTO sensordata VALUES(?,?,?,?,?)");

    // Loop over input data, inserting it into table...
 
    while (true) {

      // Obtain sensor ID, Temperature, Wind R.Humidity and CO2 from input file

      String line = in.readLine();
      if (line == null)
        break;

      String[] parser = line.split(",");

      int sensorID = Integer.parseInt(parser[0]);
      int temp = Integer.parseInt(parser[1]);
      int wind = Integer.parseInt(parser[2]);
      int humidity = Integer.parseInt(parser[3]);
      int co2 = Integer.parseInt(parser[4]);

      // Insert data into table

      statement.setInt(1, sensorID);
      statement.setInt(2, temp);
      statement.setInt(3, wind);
      statement.setInt(4, humidity);
      statement.setInt(5, co2);

      statement.executeUpdate();


    }

    statement.close();
    in.close();
  }


  /**
   * Main program.
   */

  public static void main(String[] argv)
  {
    if (argv.length == 0) {
      System.err.println("usage: java CreateDB <inputFile>");
      System.exit(1);
    }

    Connection database = null;
 
    try {
      BufferedReader input = new BufferedReader(new FileReader(argv[0]));
      database = getConnection();
      System.out.println("Success - connected to the DB.");
      createTable(database);
      long startTime = System.currentTimeMillis();
      addData(input, database);
      long endTime = System.currentTimeMillis();
      long totalTime = endTime - startTime;
      System.out.println(totalTime + "ms");

      System.out.println("Success - created table.");
    }
    catch (Exception error) {
      error.printStackTrace();
    }
    finally {

      // This will always execute, even if an exception has
      // been thrown elsewhere in the code - so this is
      // the ideal place to close the connection to the DB...

      if (database != null) {
        try {
          database.close();
        }
        catch (Exception error) {}
      }
    }
  }


}
