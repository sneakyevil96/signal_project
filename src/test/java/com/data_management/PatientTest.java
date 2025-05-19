package com.data_management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PatientTest {

    private Patient patient;

    @BeforeEach
    void setUp() {
        // patient ID must match DataStorage usage
        patient = new Patient(1);

        // Add three records on consecutive days
        patient.addRecord(
                120.0,
                "systolic",
                Instant.parse("2025-05-01T10:00:00Z").toEpochMilli()
        );
        patient.addRecord(
                118.0,
                "systolic",
                Instant.parse("2025-05-02T12:00:00Z").toEpochMilli()
        );
        patient.addRecord(
                122.0,
                "systolic",
                Instant.parse("2025-05-03T14:00:00Z").toEpochMilli()
        );
    }

    @Test
    void testGetRecordsWithinInterval() {
        long start = Instant.parse("2025-05-02T00:00:00Z").toEpochMilli();
        long end   = Instant.parse("2025-05-03T00:00:00Z").toEpochMilli();

        List<PatientRecord> recs = patient.getRecords(start, end);

        // Should return exactly one record (May 2, 2025)
        assertEquals(1, recs.size(), "Should return only the record on May 2, 2025");

        long timestamp = recs.get(0).getTimestamp();
        assertEquals(
                Instant.parse("2025-05-02T12:00:00Z").toEpochMilli(),
                timestamp,
                "Returned record should match the requested timestamp"
        );
    }
}
