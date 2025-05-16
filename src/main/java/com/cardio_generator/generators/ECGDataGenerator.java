package com.cardio_generator.generators;

import java.util.Random;
import com.cardio_generator.outputs.OutputStrategy;

public class ECGDataGenerator implements PatientDataGenerator {
    private static final Random random = new Random();
    private double[] lastEcgValues;
    private static final double PI = Math.PI;

    public ECGDataGenerator(int patientCount) {
        lastEcgValues = new double[patientCount + 1];
        for (int i = 1; i <= patientCount; i++) {
            lastEcgValues[i] = 0;
        }
    }

    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            double ecgValue = simulateEcgWaveform(patientId, lastEcgValues[patientId]);
            outputStrategy.output(patientId, System.currentTimeMillis(), "ECG", Double.toString(ecgValue));
            lastEcgValues[patientId] = ecgValue;
        } catch (Exception e) {
            System.err.println("An error occurred while generating ECG data for patient " + patientId);
            e.printStackTrace();
        }
    }

    private double simulateEcgWaveform(int patientId, double lastEcgValue) {
        double hr = 60.0 + random.nextDouble() * 20.0;
        double t = System.currentTimeMillis() / 1000.0;
        double ecgFrequency = hr / 60.0;

        double pWave = 0.1 * Math.sin(2 * PI * ecgFrequency * t);
        double qrsComplex = 0.5 * Math.sin(2 * PI * 3 * ecgFrequency * t);
        double tWave = 0.2 * Math.sin(2 * PI * 2 * ecgFrequency * t + PI / 4);

        return pWave + qrsComplex + tWave + random.nextDouble() * 0.05;
    }
}
