package org.example.functions;

import java.io.*;
import java.sql.*;
import java.util.*;

public class CreateDB {

  public static Connection getConnection() throws IOException, SQLException {
    try {
      String url = "jdbc:sqlserver://leeds-data-server.database.windows.net:1433;database=leeds-data;user=TheGrid@leeds-data-server;password=IlanAlp123;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
      Connection connection = DriverManager.getConnection(url);
      System.out.println("Success - connected to the DB.");
      return connection;
    } catch (SQLException e) {
      throw new SQLException(e.getMessage());
    }
  }
  public static void createTable(Connection database) throws SQLException {
    Statement statement = database.createStatement();

    try {
      statement.executeUpdate("DROP TABLE sensordata");
    } catch (SQLException error)
    {
      // error
    }

    // Creates a new table per exec.
    statement.executeUpdate(
        "CREATE TABLE sensordata (" +
        "sensorID INT NOT NULL," +
        "temp INT NOT NULL," +
        "wind INT NOT NULL," +
        "humidity INT NOT NULL," +
        "co2 INT NOT NULL)"
    );

    statement.close();
    System.out.println("Success - created table sensordata.");
  }

  public static void addData(Connection database) throws SQLException {
    List<List<Map<String, Integer>>> batches = generateBatches(100);

    String sql = "INSERT INTO sensordata (sensorID, temp, wind, humidity, co2) " +
                 "VALUES (?, ?, ?, ?, ?)";
    PreparedStatement statement = database.prepareStatement(sql);

    long startTime = System.currentTimeMillis();

    int rowsInserted = 0;

    for (List<Map<String, Integer>> batch : batches) {
      for (Map<String, Integer> sensor : batch) {
        statement.setInt(1, sensor.get("sensorID"));
        statement.setInt(2, sensor.get("temp"));
        statement.setInt(3, sensor.get("wind"));
        statement.setInt(4, sensor.get("humidity"));
        statement.setInt(5, sensor.get("co2"));
        statement.executeUpdate();
        rowsInserted++;
      }
    }

    long endTime = System.currentTimeMillis();
    long totalTime = endTime - startTime;

    statement.close();

    System.out.println("Inserted " + rowsInserted + " rows into sensordata.");
    System.out.println("Execution time: " + totalTime + "ms");
  }

  public static List<List<Map<String, Integer>>> generateBatches(int numberOfBatches) {
    List<List<Map<String, Integer>>> allBatches = new ArrayList<>();
    Random random = new Random();

    for (int batchNumber = 1; batchNumber <= numberOfBatches; batchNumber++) {

      List<Map<String, Integer>> batch = new ArrayList<>();


      for (int sensorID = 1; sensorID <= 20; sensorID++) {

        Map<String, Integer> sensor = new HashMap<>();
        sensor.put("sensorID", sensorID);
        sensor.put("temp", 10 + random.nextInt(15));
        sensor.put("wind", random.nextInt(30));
        sensor.put("humidity", 30 + random.nextInt(50));
        sensor.put("co2", 400 + random.nextInt(800));

        batch.add(sensor);
      }

      allBatches.add(batch);
    }

    return allBatches;
  }

  /**
   * Main program.
   */
  public static void main(String[] argv) {
    Connection database = null;

    try {
      database = getConnection();
      createTable(database);
      addData(database);
      System.out.println("Done.");
    } catch (Exception error) {
      error.printStackTrace();
    } finally {
      if (database != null) {
        try {
          database.close();
        } catch (Exception error) {
          // ignore
        }
      }
    }
  }
}
