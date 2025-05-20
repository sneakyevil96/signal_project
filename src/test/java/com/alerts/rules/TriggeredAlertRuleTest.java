package com.alerts.rules;

import com.alerts.Alert;
import com.alerts.AlertDispatcher;
import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class TriggeredAlertRuleTest {

    private AlertDispatcher dispatcher;
    private TriggeredAlertRule rule;
    private static final long TS = Instant.parse("2025-05-01T11:00:00Z").toEpochMilli();

    @BeforeEach
    void setUp() {
        dispatcher = mock(AlertDispatcher.class);
        rule       = new TriggeredAlertRule();
    }

    @Test
    void triggersOnTriggeredAlertRecord() {
        Patient patient = new Patient(99);
        // Add a manual trigger record
        patient.addRecord(0.0, "TriggeredAlert", TS);

        rule.evaluate(patient, dispatcher);

        verify(dispatcher, times(1)).dispatch(argThat((Alert a) ->
                a.getPatientId().equals("99") &&
                        a.getCondition().equals("Manual alert triggered by nurse/patient.") &&
                        a.getTimestamp() == TS
        ));
    }

    @Test
    void doesNotTriggerForOtherRecordTypes() {
        Patient patient = new Patient(100);
        // Add some non-trigger records
        patient.addRecord(1.0, "HeartRate", TS);
        patient.addRecord(2.0, "Systolic", TS + 1000);

        rule.evaluate(patient, dispatcher);

        verify(dispatcher, never()).dispatch(any());
    }
    @Test
    void testEvaluateWithNullDispatcher() {
        Patient patient = new Patient(1);
        assertThrows(NullPointerException.class, () ->
                rule.evaluate(patient, null)
        );
    }

    @Test
    void testEvaluateWithNullPatient() {
        AlertDispatcher dispatcher = mock(AlertDispatcher.class);
        assertThrows(NullPointerException.class, () ->
                rule.evaluate(null, dispatcher)
        );
    }


}
