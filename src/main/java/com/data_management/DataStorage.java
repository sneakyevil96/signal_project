package com.data_management;
import java.util.*;
/**
 * Singleton class that stores patient data and records.
 */
public class DataStorage {
    private static DataStorage instance;

    private final List<PatientRecord> records;
    private final Map<Integer, Patient> patients;

    private DataStorage() {
        this.records = new ArrayList<>();
        this.patients = new HashMap<>();
    }
    /**
     * Returns the singleton instance.
     */
    public static synchronized DataStorage getInstance() {
        if (instance == null) {
            instance = new DataStorage();
        }
        return instance;
    }

    /**
     * Clears all stored data. Useful for resetting state in tests.
     * Executes the clear operation.
     */
    public void clear() {
        records.clear();
        patients.clear();
    }

    public void addPatientData(int patientId,
                               double measurementValue,
                               String recordType,
                               long timestamp) {
        PatientRecord record = new PatientRecord(patientId, measurementValue, recordType, timestamp);
        records.add(record);
        patients
                .computeIfAbsent(patientId, Patient::new)
                .addRecord(measurementValue, recordType, timestamp);
    }
    /**
     * Executes the getAllPatients operation.
     * @return a List of patients.
     */
    public List<Patient> getAllPatients() {
        return new ArrayList<>(patients.values());
    }

    /**
     * Executes the getRecords operation.
     * @return List of patient records.
     * @param patientId PatientId.
     * @param startTime StartTime.
     * @param endTime Endtime.
     */
    public List<PatientRecord> getRecords(int patientId, long startTime, long endTime) {
        List<PatientRecord> out = new ArrayList<>();
        for (PatientRecord r : records) {
            if (r.getPatientId() == patientId
                    && r.getTimestamp() >= startTime
                    && r.getTimestamp() <= endTime) {
                out.add(r);
            }
        }
        return out;
    }
}
