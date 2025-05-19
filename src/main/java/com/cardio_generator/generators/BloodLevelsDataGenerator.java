package com.cardio_generator.generators;

import java.util.Random;
import com.cardio_generator.outputs.OutputStrategy;

public class BloodLevelsDataGenerator implements PatientDataGenerator {
    private static final Random random = new Random();
    private final double[] baselineCholesterol;
    private final double[] baselineWhiteCells;
    private final double[] baselineRedCells;

    public BloodLevelsDataGenerator(int patientCount) {
        baselineCholesterol = new double[patientCount + 1];
        baselineWhiteCells   = new double[patientCount + 1];
        baselineRedCells     = new double[patientCount + 1];

        for (int i = 1; i <= patientCount; i++) {
            baselineCholesterol[i] = 150 + random.nextDouble() * 50;
            baselineWhiteCells[i]  = 4 + random.nextDouble() * 6;
            baselineRedCells[i]    = 4.5 + random.nextDouble() * 1.5;
        }
    }

    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            double cholesterol = baselineCholesterol[patientId] + (random.nextDouble() - 0.5) * 10;
            double whiteCells  = baselineWhiteCells[patientId]   + (random.nextDouble() - 0.5) * 1;
            double redCells    = baselineRedCells[patientId]     + (random.nextDouble() - 0.5) * 0.2;

            long timestamp = System.currentTimeMillis();

            outputStrategy.output(patientId, timestamp, "Cholesterol", cholesterol);
            outputStrategy.output(patientId, timestamp, "WhiteBloodCells", whiteCells);
            outputStrategy.output(patientId, timestamp, "RedBloodCells", redCells);
        } catch (Exception e) {
            System.err.println("An error occurred while generating blood levels data for patient " + patientId);
            e.printStackTrace();
        }
    }
}
