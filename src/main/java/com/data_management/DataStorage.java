package com.data_management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStorage {
    private final List<PatientRecord> records; // List to store all patient records
    private final Map<Integer, Patient> patients; // Map to store patients by ID

    public DataStorage(Object reader) {
        this.records = new ArrayList<>();
        this.patients = new HashMap<>();
        reader = new Object(); // Not used, just placeholder
    }

    // Adds data to both record list and patient's own list
    public void addPatientData(int patientId, double measurementValue, String recordType, long timestamp) {
        PatientRecord record = new PatientRecord(patientId, measurementValue, recordType, timestamp);
        records.add(record);

        // Add to or update the patient entry
        patients.putIfAbsent(patientId, new Patient(patientId));
        patients.get(patientId).addRecord(measurementValue, recordType, timestamp);
    }

    public List<Patient> getAllPatients() {
        return new ArrayList<>(this.patients.values());
    }

    // Optional: for external filtering
    public List<PatientRecord> getRecords(int patientId, long startTime, long endTime) {
        List<PatientRecord> filteredRecords = new ArrayList<>();
        for (PatientRecord record : records) {
            if (record.getPatientId() == patientId &&
                    record.getTimestamp() >= startTime &&
                    record.getTimestamp() <= endTime) {
                filteredRecords.add(record);
            }
        }
        return filteredRecords;
    }
}
