package com.alerts.rules;

import com.alerts.Alert;
import com.alerts.AlertDispatcher;
import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class ECGAbnormalPeaksRuleTest {
    private AlertDispatcher dispatcher;
    private ECGAbnormalPeaksRule rule;
    private static final long BASE_TS = Instant.parse("2025-05-01T00:00:00Z").toEpochMilli();

    @BeforeEach
    void setUp() {
        dispatcher = mock(AlertDispatcher.class);
        rule = new ECGAbnormalPeaksRule();
    }

    @Test
    void doesNotTriggerForValuesBelowThreshold() {
        Patient patient = new Patient(1);
        // Add initial WINDOW_SIZE + 1 values that are below avg + THRESHOLD
        double[] values = {100, 102, 98, 101, 99, 110}; // last value < 120 avg + 20
        long ts = BASE_TS;
        for (double v : values) {
            patient.addRecord(v, "ECG", ts);
            ts += 1000; // 1sec apart
        }

        rule.evaluate(patient, dispatcher);

        verify(dispatcher, never()).dispatch(any());
    }

    @Test
    void triggersOnAbnormalPeak() {
        Patient patient = new Patient(2);
        // Build up a stable window of 100s
        for (int i = 0; i < 5; i++) {
            patient.addRecord(100.0, "ECG", BASE_TS + i * 1000);
        }
        // Now add a spike of 130 (> avg 100 + THRESHOLD 20)
        long spikeTs = BASE_TS + 5 * 1000;
        patient.addRecord(130.0, "ECG", spikeTs);

        rule.evaluate(patient, dispatcher);

        verify(dispatcher, times(1)).dispatch(argThat((Alert a) ->
                a.getPatientId().equals("2") &&
                        a.getCondition().contains("ECG abnormal peak detected: 130.0") &&
                        a.getTimestamp() == spikeTs
        ));
    }
}
