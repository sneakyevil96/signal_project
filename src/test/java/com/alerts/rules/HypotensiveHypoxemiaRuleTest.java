package com.alerts.rules;

import com.alerts.Alert;
import com.alerts.AlertDispatcher;
import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HypotensiveHypoxemiaRuleTest {

    private AlertDispatcher dispatcher;
    private HypotensiveHypoxemiaRule rule;
    private static final long BASE_TS = Instant.parse("2025-05-01T09:00:00Z").toEpochMilli();

    @BeforeEach
    void setUp() {
        dispatcher = mock(AlertDispatcher.class);
        rule = new HypotensiveHypoxemiaRule();
    }

    @Test
    void triggersWhenBothConditionsMetWithinTolerance() {
        Patient patient = new Patient(5);
        patient.addRecord(85.0, "Systolic", BASE_TS);
        patient.addRecord(90.0, "OxygenSaturation", BASE_TS + 30 * 1000);

        rule.evaluate(patient, dispatcher);

        ArgumentCaptor<Alert> captor = ArgumentCaptor.forClass(Alert.class);
        verify(dispatcher, times(1)).dispatch(captor.capture());

        Alert alert = captor.getValue();
        System.out.println("DEBUG - ALERT CONDITION: " + alert.getCondition());

        assertEquals("5", alert.getPatientId());
        assertTrue(alert.getCondition().contains("systolic BP=85.0"), "Condition should mention systolic BP=85.0");
        assertTrue(alert.getCondition().contains("SpO2=90.0"), "Condition should mention SpO2=90.0");
        assertEquals(BASE_TS + 30 * 1000, alert.getTimestamp());
    }

    @Test
    void doesNotTriggerIfOnlySystolicLow() {
        Patient patient = new Patient(6);
        patient.addRecord(85.0, "Systolic", BASE_TS);
        rule.evaluate(patient, dispatcher);
        verify(dispatcher, never()).dispatch(any());
    }

    @Test
    void doesNotTriggerIfOnlyOxygenLow() {
        Patient patient = new Patient(7);
        patient.addRecord(90.0, "OxygenSaturation", BASE_TS);
        rule.evaluate(patient, dispatcher);
        verify(dispatcher, never()).dispatch(any());
    }

    @Test
    void doesNotTriggerIfOutsideTimeTolerance() {
        Patient patient = new Patient(8);
        patient.addRecord(85.0, "Systolic", BASE_TS);
        patient.addRecord(90.0, "OxygenSaturation", BASE_TS + 120 * 1000);
        rule.evaluate(patient, dispatcher);
        verify(dispatcher, never()).dispatch(any());
    }

    @Test
    void testThresholdBoundaryValues() {
        Patient patient = new Patient(1);
        patient.addRecord(90.0, "Systolic", BASE_TS);
        patient.addRecord(92.0, "OxygenSaturation", BASE_TS);
        rule.evaluate(patient, dispatcher);
        verify(dispatcher, never()).dispatch(any());
    }
}