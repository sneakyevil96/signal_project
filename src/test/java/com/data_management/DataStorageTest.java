package com.data_management;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DataStorageTest {
    private DataStorage storage;

    @BeforeEach
    void setUp() {
        storage = DataStorage.getInstance();
        storage.clear();
    }

    @Test
    void testSingletonInstance() {
        DataStorage another = DataStorage.getInstance();
        assertSame(storage, another, "getInstance() should always return the same object");
    }

    @Test
    void testAddPatientDataCreatesPatientAndRecord() {
        // Add one record
        storage.addPatientData(5, 123.4, "ECG", 1_000L);

        // After adding, we should have exactly one patient
        List<Patient> patients = storage.getAllPatients();
        assertEquals(1, patients.size(), "Should have 1 patient in storage");

        Patient p = patients.get(0);
        assertEquals(5, p.getId(), "Patient ID should match");
        // And that patient should have exactly one record
        List<PatientRecord> recs = p.getRecords(0, Long.MAX_VALUE);
        assertEquals(1, recs.size(), "Patient should have exactly one record");
        PatientRecord r = recs.get(0);
        assertEquals(123.4, r.getMeasurementValue(), 1e-6);
        assertEquals("ECG", r.getRecordType());
        assertEquals(1_000L, r.getTimestamp());
    }

    @Test
    void testGetAllPatientsWithMultipleAdds() {
        // Add data for two different patient IDs
        storage.addPatientData(1, 10.0, "A", 100L);
        storage.addPatientData(2, 20.0, "B", 200L);
        storage.addPatientData(1, 11.0, "A", 110L);

        List<Patient> patients = storage.getAllPatients();
        assertEquals(2, patients.size(), "Should have two distinct patients");

        // Check that patient 1 has two records, patient 2 has one
        Patient p1 = patients.stream().filter(p -> p.getId() == 1).findFirst().orElseThrow();
        Patient p2 = patients.stream().filter(p -> p.getId() == 2).findFirst().orElseThrow();
        assertEquals(2, p1.getRecords(0, Long.MAX_VALUE).size());
        assertEquals(1, p2.getRecords(0, Long.MAX_VALUE).size());
    }

    @Test
    void testGetRecordsFiltersByTimeWindow() {
        storage.addPatientData(3, 5.0, "X", 50L);
        storage.addPatientData(3, 6.0, "X", 150L);
        storage.addPatientData(3, 7.0, "X", 250L);

        // Only those between 100 and 200 inclusive
        List<PatientRecord> recs = storage.getRecords(3, 100L, 200L);
        assertEquals(1, recs.size(), "Only one record falls in [100,200]");
        assertEquals(6.0, recs.get(0).getMeasurementValue(), 1e-6);
    }

    @Test
    void testAddRecordWithNegativeMeasurement() {
        DataStorage storage = DataStorage.getInstance();
        storage.clear();
        storage.addPatientData(1, -50.0, "HeartRate", System.currentTimeMillis());
        assertFalse(storage.getAllPatients().isEmpty());
    }
}
