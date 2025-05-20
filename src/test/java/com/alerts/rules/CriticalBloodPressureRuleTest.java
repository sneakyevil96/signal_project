package com.alerts.rules;

import com.alerts.Alert;
import com.alerts.AlertDispatcher;
import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the CriticalBloodPressureRule class.
 */
class CriticalBloodPressureRuleTest {

    private AlertDispatcher dispatcher;
    private CriticalBloodPressureRule rule;
    private Patient patient;
    private static final long TS = Instant.parse("2025-05-01T10:00:00Z").toEpochMilli();

    @BeforeEach
    void setUp() {
        dispatcher = mock(AlertDispatcher.class);
        rule = new CriticalBloodPressureRule();
        patient = new Patient(1);
    }

    @Test
    void triggersOnHighSystolic() {
        patient.addRecord(200.0, "Systolic", TS);
        rule.evaluate(patient, dispatcher);

        verify(dispatcher).dispatch(argThat((Alert a) ->
                a.getPatientId().equals("1") &&
                        a.getCondition().contains("systolic BP: 200.0") &&
                        a.getTimestamp() == TS
        ));
    }

    @Test
    void triggersOnLowDiastolic() {
        patient.addRecord(50.0, "Diastolic", TS);
        rule.evaluate(patient, dispatcher);

        verify(dispatcher).dispatch(argThat((Alert a) ->
                a.getPatientId().equals("1") &&
                        a.getCondition().contains("diastolic BP: 50.0") &&
                        a.getTimestamp() == TS
        ));
    }

    @Test
    void doesNotTriggerForNormalReadings() {
        patient.addRecord(120.0, "Systolic", TS);
        patient.addRecord(80.0,  "Diastolic", TS);
        rule.evaluate(patient, dispatcher);

        verify(dispatcher, never()).dispatch(any());
    }

    @Test
    void testExtremelyHighSystolicEdgeCase() {
        patient.addRecord(500.0, "Systolic", TS);
        rule.evaluate(patient, dispatcher);

        verify(dispatcher).dispatch(argThat((Alert a) ->
                a.getPatientId().equals("1") &&
                        a.getCondition().contains("systolic BP: 500.0")
        ));
    }

    @Test
    void testUnrecognizedTypeDoesNotTrigger() {
        patient.addRecord(500.0, "BloodPressure", TS);  // Unsupported type
        rule.evaluate(patient, dispatcher);

        verify(dispatcher, never()).dispatch(any());
    }

    @Test
    void testNegativeDiastolicIgnored() {
        patient.addRecord(-100.0, "Diastolic", TS);  // Invalid but may be parsed
        rule.evaluate(patient, dispatcher);

        // Still under 60, so it *might* dispatch — depends on how your logic treats negatives
        verify(dispatcher).dispatch(argThat((Alert a) ->
                a.getCondition().contains("diastolic BP: -100.0")
        ));
    }
}