package org.example.functions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.*;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

public class TimerTriggerJava {

    @FunctionName("GenerateSensorData")
    public void run(
        @TimerTrigger(name = "timerInfo", schedule = "*/10 * * * * *") String timerInfo,
        final ExecutionContext context
    ) {
        context.getLogger().info("GenerateSensorData triggered at: " + LocalDateTime.now());

        try (Connection connection = DBConnection.getConnection()) {

            DBConnection.createTable(connection);
            context.getLogger().info("sensordata table recreated.");
            List<List<Map<String, Integer>>> batches = generateBatches(10);

            String sql = "INSERT INTO sensordata (sensorID, temp, wind, humidity, co2) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql);

            for (List<Map<String, Integer>> batch : batches) {
                for (Map<String, Integer> sensor : batch) {
                    ps.setInt(1, sensor.get("sensorID"));
                    ps.setInt(2, sensor.get("temp"));
                    ps.setInt(3, sensor.get("wind"));
                    ps.setInt(4, sensor.get("humidity"));
                    ps.setInt(5, sensor.get("co2"));
                    ps.executeUpdate();
                }
            }

        } catch (Exception e) {
            context.getLogger().severe("DB error: " + e.getMessage());
        }
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
}
