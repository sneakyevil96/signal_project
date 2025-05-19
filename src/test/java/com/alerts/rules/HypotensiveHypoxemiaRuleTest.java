package com.alerts.rules;

import com.alerts.Alert;
import com.alerts.AlertDispatcher;
import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class HypotensiveHypoxemiaRuleTest {

    private AlertDispatcher dispatcher;
    private HypotensiveHypoxemiaRule rule;
    private static final long BASE_TS = Instant.parse("2025-05-01T09:00:00Z").toEpochMilli();

    @BeforeEach
    void setUp() {
        dispatcher = mock(AlertDispatcher.class);
        rule       = new HypotensiveHypoxemiaRule();
    }

    @Test
    void triggersWhenBothConditionsMetWithinTolerance() {
        Patient patient = new Patient(5);
        // Systolic below 90 at BASE_TS
        patient.addRecord(85.0, "Systolic", BASE_TS);
        // Oxygen below 92 within 60s of BASE_TS
        patient.addRecord(90.0, "OxygenSaturation", BASE_TS + 30 * 1000);

        rule.evaluate(patient, dispatcher);

        verify(dispatcher, times(1)).dispatch(argThat((Alert a) ->
                a.getPatientId().equals("5") &&
                        a.getCondition().contains("SBP=85.0") &&
                        a.getCondition().contains("SpO2=90.0") &&
                        a.getTimestamp() == BASE_TS + 30 * 1000
        ));
    }

    @Test
    void doesNotTriggerIfOnlySystolicLow() {
        Patient patient = new Patient(6);
        patient.addRecord(85.0, "Systolic", BASE_TS);
        // No oxygen record
        rule.evaluate(patient, dispatcher);
        verify(dispatcher, never()).dispatch(any());
    }

    @Test
    void doesNotTriggerIfOnlyOxygenLow() {
        Patient patient = new Patient(7);
        // Only oxygen low
        patient.addRecord(90.0, "OxygenSaturation", BASE_TS);
        rule.evaluate(patient, dispatcher);
        verify(dispatcher, never()).dispatch(any());
    }

    @Test
    void doesNotTriggerIfOutsideTimeTolerance() {
        Patient patient = new Patient(8);
        // Systolic low at BASE_TS
        patient.addRecord(85.0, "Systolic", BASE_TS);
        // Oxygen low but 2 minutes later (>60s tolerance)
        patient.addRecord(90.0, "OxygenSaturation", BASE_TS + 120 * 1000);

        rule.evaluate(patient, dispatcher);
        verify(dispatcher, never()).dispatch(any());
    }
}
