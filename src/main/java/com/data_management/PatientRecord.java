package com.data_management;

/**
 * Represents a single record of patient data at a specific point in time.
 * This class stores all necessary details for a single observation or
 * measurement taken from a patient, including the type of record (such as ECG, blood
 * pressure), the measurement value, and the exact timestamp when the measurement was
 * taken.
 */
public class PatientRecord {
    private int patientId;
    private String recordType; // Example: ECG, blood pressure, etc.
    private double measurementValue; // Example: heart rate
    private long timestamp;

    /**
     * Constructs a new patient record with specified details.
     *
     * @param patientId        the unique identifier for the patient
     * @param measurementValue the numerical value of the recorded measurement
     * @param recordType       the type of measurement (e.g., "ECG", "Blood
     *                         Pressure")
     * @param timestamp        the time at which the measurement was recorded, in
     *                         milliseconds since epoch
     */

    public PatientRecord(int patientId, double measurementValue, String recordType, long timestamp) {
        this.patientId = patientId;
        this.measurementValue = measurementValue;
        this.recordType = recordType;
        this.timestamp = timestamp;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public double getMeasurementValue() {
        return measurementValue;
    }

    public void setMeasurementValue(double measurementValue) {
        this.measurementValue = measurementValue;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }
}
