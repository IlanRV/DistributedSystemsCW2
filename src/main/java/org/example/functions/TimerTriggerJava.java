package org.example.functions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.*;
import java.util.Random;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with Timer trigger.
 */
public class TimerTriggerJava {
    /**
     * This function will be invoked periodically according to the specified schedule.
     */
    @FunctionName("GenerateSensorData")
    public void run(
        @TimerTrigger(name = "timerInfo", schedule = "*/10 * * * * *") String timerInfo,
        final ExecutionContext context
    ) {
        context.getLogger().info("Sensor Simulation function executed at: " + LocalDateTime.now());

        Random random = new Random();

        int sensorID = random.nextInt(1000);
        int temp = 10 + random.nextInt(15);
        int wind = random.nextInt(30);
        int humidity = 30 + random.nextInt(50);
        int co2 = 400 + random.nextInt(800);


        try (Connection connection = DBConnection.getConnection() ){

            String sql = "INSERT INTO sensordata (sensorID, temp, wind, humidity, co2) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql);


            ps.setInt(1, sensorID);
            ps.setInt(2, temp);
            ps.setInt(3, wind);
            ps.setInt(4, humidity);
            ps.setInt(5, co2);

            ps.executeUpdate();
            context.getLogger().info("Inserted sensor record: " + sensorID);
        } catch (Exception e) {
            context.getLogger().severe("DB error: " + e.getMessage());
        }

    }
}
