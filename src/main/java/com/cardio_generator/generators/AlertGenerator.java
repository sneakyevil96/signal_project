package com.cardio_generator.generators;

import java.util.Random;
import com.cardio_generator.outputs.OutputStrategy;

public class AlertGenerator implements PatientDataGenerator {

    public static final Random randomGenerator = new Random();
    private boolean[] AlertStates; // false = resolved, true = active

    public AlertGenerator(int patientCount) {
        // patientCount+1 so index matches patientId
        AlertStates = new boolean[patientCount + 1];
    }

    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            long timestamp = System.currentTimeMillis();

            if (AlertStates[patientId]) {
                // 90% chance to resolve an active alert
                if (randomGenerator.nextDouble() < 0.9) {
                    AlertStates[patientId] = false;
                    // 0.0 for resolved
                    outputStrategy.output(patientId, timestamp, "Alert", 0.0);
                }
            } else {
                double lambda = 0.1; // average rate per period
                double p = -Math.expm1(-lambda);
                boolean alertTriggered = randomGenerator.nextDouble() < p;

                if (alertTriggered) {
                    AlertStates[patientId] = true;
                    // 1.0 for triggered
                    outputStrategy.output(patientId, timestamp, "Alert", 1.0);
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}
