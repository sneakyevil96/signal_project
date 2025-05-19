package com.data_management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton class for storing patient records in memory.
 */
public class DataStorage {
    private final List<PatientRecord> records;
    private final Map<Integer, Patient> patients;

    // Singleton instance
    private static DataStorage instance;

    /**
     * Private constructor to prevent direct instantiation.
     */
    private DataStorage() {
        this.records = new ArrayList<>();
        this.patients = new HashMap<>();
    }

    /**
     * Get the single instance of DataStorage.
     *
     * @return the shared DataStorage instance
     */
    public static synchronized DataStorage getInstance() {
        if (instance == null) {
            instance = new DataStorage();
        }
        return instance;
    }

    /**
     * Adds a new data point for a patient.
     *
     * @param patientId        unique identifier of the patient
     * @param measurementValue the value of the measurement
     * @param recordType       the type of measurement (e.g., "Systolic", "OxygenSaturation")
     * @param timestamp        the timestamp of the measurement in ms since epoch
     */
    public void addPatientData(int patientId, double measurementValue, String recordType, long timestamp) {
        PatientRecord record = new PatientRecord(patientId, measurementValue, recordType, timestamp);
        records.add(record);

        patients.putIfAbsent(patientId, new Patient(patientId));
        patients.get(patientId).addRecord(measurementValue, recordType, timestamp);
    }

    /**
     * Retrieve all patients currently stored.
     *
     * @return a list of Patient objects
     */
    public List<Patient> getAllPatients() {
        return new ArrayList<>(this.patients.values());
    }

    /**
     * Retrieve all records for a patient within a time window.
     *
     * @param patientId the patient ID
     * @param startTime inclusive start of time window (ms since epoch)
     * @param endTime   inclusive end of time window (ms since epoch)
     * @return list of PatientRecord in the specified window
     */
    public List<PatientRecord> getRecords(int patientId, long startTime, long endTime) {
        List<PatientRecord> filtered = new ArrayList<>();
        for (PatientRecord record : records) {
            if (record.getPatientId() == patientId
                    && record.getTimestamp() >= startTime
                    && record.getTimestamp() <= endTime) {
                filtered.add(record);
            }
        }
        return filtered;
    }
}
