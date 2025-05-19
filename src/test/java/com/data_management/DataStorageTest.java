package com.data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.cardio_generator.*;
import java.util.*;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;

import java.util.List;

class DataStorageTest {
    private HealthDataSimulator one;
    Object reader = null;

//    @Test
    void testAddAndGetRecords() {
//        List<Integer> aList;
//        aList = new ArrayList<Integer>();
//        aList.add(1);
//        aList.add(2);
//        aList.add(3);
//        aList.add(4);
//        assertEquals(aList, one.initializePatientIds(4));

//        // Create a new DataStorage instance
//        DataStorage storage = new DataStorage(reader);
//
//        // Add two records for patient 1 with different timestamps
//        storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L);
//        storage.addPatientData(1, 200.0, "WhiteBloodCells", 1714376789051L);
//
//        // Retrieve the records within the timestamp range
//        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);
//
//        // Validate that two records were retrieved
//        assertEquals(2, records.size(), "There should be exactly two records.");
//
//        // Validate the first record's measurement value
//        assertEquals(100.0, records.get(0).getMeasurementValue(), "The first record's measurement value should be 100.0");
    }
}