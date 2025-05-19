package com.alerts.rules;

import com.alerts.Alert;
import com.alerts.AlertDispatcher;
import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class RapidOxygenDropRuleTest {

    private AlertDispatcher dispatcher;
    private RapidOxygenDropRule rule;
    private static final long BASE_TS = Instant.parse("2025-05-01T10:00:00Z").toEpochMilli();

    @BeforeEach
    void setUp() {
        dispatcher = mock(AlertDispatcher.class);
        rule       = new RapidOxygenDropRule();
    }

    @Test
    void triggersWhenDropOverThresholdWithinWindow() {
        Patient patient = new Patient(7);
        // Base reading at 97%, then 91% five minutes later (drop = 6.0%)
        patient.addRecord(97.0, "OxygenSaturation", BASE_TS);
        patient.addRecord(91.0, "OxygenSaturation", BASE_TS + 5 * 60 * 1000);

        rule.evaluate(patient, dispatcher);

        verify(dispatcher, times(1)).dispatch(argThat((Alert a) ->
                a.getPatientId().equals("7") &&
                        a.getCondition().contains("Rapid oxygen drop detected: 6.0% in under 10 minutes") &&
                        a.getTimestamp() == BASE_TS + 5 * 60 * 1000
        ));
    }

    @Test
    void doesNotTriggerWhenDropUnderThreshold() {
        Patient patient = new Patient(8);
        // Drop of only 4.0% within window
        patient.addRecord(100.0, "OxygenSaturation", BASE_TS);
        patient.addRecord(96.0,  "OxygenSaturation", BASE_TS + 5 * 60 * 1000);

        rule.evaluate(patient, dispatcher);

        verify(dispatcher, never()).dispatch(any());
    }

    @Test
    void doesNotTriggerWhenDropOverThresholdButOutsideWindow() {
        Patient patient = new Patient(9);
        // Drop of 6.0% but after 10 minutes + 1 ms
        patient.addRecord(98.0, "OxygenSaturation", BASE_TS);
        patient.addRecord(92.0, "OxygenSaturation", BASE_TS + (10 * 60 * 1000) + 1);

        rule.evaluate(patient, dispatcher);

        verify(dispatcher, never()).dispatch(any());
    }
}
