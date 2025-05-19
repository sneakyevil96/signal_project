package com.cardio_generator.outputs;

public interface OutputStrategy {
    /**
     * @param patientId   the patient’s ID
     * @param timestamp   when the sample was generated
     * @param recordType  e.g. "ECG", "SystolicPressure", "TriggeredAlert"
     * @param measurement the numeric measurement value (or special codes for alerts)
     */
    default void output(int patientId, long timestamp, String recordType, double measurement) {
    }
}

