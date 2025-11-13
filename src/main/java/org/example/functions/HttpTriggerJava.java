package org.example.functions;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import java.sql.*;

public class HttpTriggerJava {

    @FunctionName("GetSensorCount")
    public HttpResponseMessage getSensorCount(
            @HttpTrigger(name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = AuthorizationLevel.FUNCTION)
            HttpRequestMessage<String> request,
            final ExecutionContext context) {

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM sensordata")) {

            rs.next();
            int count = rs.getInt("total");

            return request.createResponseBuilder(HttpStatus.OK)
                    .body("Total sensor records: " + count)
                    .build();

        } catch (Exception e) {
            context.getLogger().severe("Error in GetSensorCount: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage())
                    .build();
        }
    }

    @FunctionName("GetSensorExtremes")
    public HttpResponseMessage getSensorExtremes(
            @HttpTrigger(name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = AuthorizationLevel.FUNCTION)
            HttpRequestMessage<String> request,
            final ExecutionContext context) {

        String sql =
            "SELECT sensorID, " +
            "       MIN(temp)     AS minTemp,  MAX(temp)     AS maxTemp, " +
            "       MIN(wind)     AS minWind,  MAX(wind)     AS maxWind, " +
            "       MIN(humidity) AS minHum,   MAX(humidity) AS maxHum, " +
            "       MIN(co2)      AS minCo2,   MAX(co2)      AS maxCo2 " +
            "FROM sensordata " +
            "GROUP BY sensorID " +
            "ORDER BY sensorID";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            StringBuilder sb = new StringBuilder();
            sb.append("Min/Max values per sensorID:\n");

            while (rs.next()) {
                int sensorID = rs.getInt("sensorID");
                int minTemp  = rs.getInt("minTemp");
                int maxTemp  = rs.getInt("maxTemp");
                int minWind  = rs.getInt("minWind");
                int maxWind  = rs.getInt("maxWind");
                int minHum   = rs.getInt("minHum");
                int maxHum   = rs.getInt("maxHum");
                int minCo2   = rs.getInt("minCo2");
                int maxCo2   = rs.getInt("maxCo2");

                sb.append(String.format(
                    "Sensor %d -> temp[min: %d, max: %d], wind[min: %d, max: %d], humidity[min: %d, max: %d], co2[min: %d, max: %d]%n",
                    sensorID, minTemp, maxTemp, minWind, maxWind, minHum, maxHum, minCo2, maxCo2
                ));
            }

            return request.createResponseBuilder(HttpStatus.OK)
                    .body(sb.toString())
                    .build();

        } catch (Exception e) {
            context.getLogger().severe("Error in GetSensorExtremes: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage())
                    .build();
        }
    }
}
