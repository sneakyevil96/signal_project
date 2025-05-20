package com.alerts.rules;

import com.alerts.Alert;
import com.alerts.AlertDispatcher;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class LowOxygenRuleTest {

    private AlertDispatcher dispatcher;
    private LowOxygenRule rule;
    private Patient patient;
    private static final long TS = Instant.parse("2025-05-01T08:30:00Z").toEpochMilli();

    @BeforeEach
    void setUp() {
        dispatcher = mock(AlertDispatcher.class);
        rule       = new LowOxygenRule();
        patient    = new Patient(42);
    }

    @Test
    void triggersWhenBelowThreshold() {
        // Add one OxygenSaturation reading below 92%
        patient.addRecord(91.0, "OxygenSaturation", TS);

        rule.evaluate(patient, dispatcher);

        verify(dispatcher, times(1)).dispatch(argThat((Alert a) ->
                a.getPatientId().equals("42") &&
                        a.getCondition().contains("Low oxygen saturation detected: 91.0%") &&
                        a.getTimestamp() == TS
        ));
    }

    @Test
    void doesNotTriggerWhenAtOrAboveThreshold() {
        // Add readings at and above threshold
        patient.addRecord(92.0, "OxygenSaturation", TS);
        patient.addRecord(95.0, "OxygenSaturation", TS + 1000);

        rule.evaluate(patient, dispatcher);

        verify(dispatcher, never()).dispatch(any());
    }

    @Test
    void handlesNegativeOxygenLevel() {
        Patient patient = new Patient(1);
        patient.addRecord(-10.0, "OxygenSaturation", TS);

        rule.evaluate(patient, dispatcher);

        verify(dispatcher, times(1)).dispatch(argThat((Alert a) ->
                a.getPatientId().equals("1") &&
                        a.getCondition().contains("Low oxygen saturation detected: -10.0%") &&
                        a.getTimestamp() == TS
        ));
    }

}
