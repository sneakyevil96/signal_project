package com.alerts.rules;

import com.alerts.Alert;
import com.alerts.AlertDispatcher;
import com.alerts.rules.CriticalBloodPressureRule;
import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

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
        // High systolic > 180
        patient.addRecord(200.0, "Systolic", TS);

        rule.evaluate(patient, dispatcher);

        verify(dispatcher, times(1)).dispatch(argThat((Alert a) ->
                a.getPatientId().equals("1") &&
                        a.getCondition().contains("systolic BP: 200.0") &&
                        a.getTimestamp() == TS
        ));
    }

    @Test
    void triggersOnLowDiastolic() {
        // Low diastolic < 60
        patient.addRecord(50.0, "Diastolic", TS);

        rule.evaluate(patient, dispatcher);

        verify(dispatcher, times(1)).dispatch(argThat((Alert a) ->
                a.getPatientId().equals("1") &&
                        a.getCondition().contains("diastolic BP: 50.0") &&
                        a.getTimestamp() == TS
        ));
    }

    @Test
    void doesNotTriggerForNormalReadings() {
        // Normal blood pressure values
        patient.addRecord(120.0, "Systolic", TS);
        patient.addRecord(80.0,  "Diastolic", TS);

        rule.evaluate(patient, dispatcher);

        verify(dispatcher, never()).dispatch(any());
    }
}
