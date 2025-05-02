package com.data_management;

import java.util.ArrayList;
import java.util.List;

public class DataStorage {
    private final List<PatientRecord> records; // List to store patient records

    public DataStorage(Object reader) {
        this.records = new ArrayList<>(); // Initialize the records list
        reader = new Object();
        // Optionally, initialize the reader if needed (done it!)
    }

    // Method to add patient data
    public void addPatientData(int patientId, double measurementValue, String recordType, long timestamp) {
        PatientRecord record = new PatientRecord(patientId, measurementValue, recordType, timestamp);
        records.add(record); // Add the new record to the list
    }

    // Method to get records based on patient ID and timestamp range
    public List<PatientRecord> getRecords(int patientId, long startTime, long endTime) {
        List<PatientRecord> filteredRecords = new ArrayList<>();

        for (PatientRecord record : records) {
            if (record.getPatientId() == patientId && record.getTimestamp() >= startTime && record.getTimestamp() <= endTime) {
                filteredRecords.add(record); // Add matching records to the filtered list
            }
        }

        return filteredRecords; // Return the list of filtered records
    }
}
