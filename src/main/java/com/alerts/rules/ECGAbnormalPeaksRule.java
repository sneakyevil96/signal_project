package com.alerts.rules;
import com.alerts.Alert;
import com.alerts.AlertDispatcher;
import com.alerts.AlertRule;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
/**
 * Strategy for detecting abnormal ECG peaks.
 */
public class ECGAbnormalPeaksRule implements AlertRule {
    private static final int WINDOW_SIZE = 5;
    private static final double THRESHOLD = 20.0; // units above moving average

    @Override
/**
 * Executes the evaluate operation.
 * @param patient Patient.
 * @param dispatcher Dispatcher.
 */
    public void evaluate(Patient patient, AlertDispatcher dispatcher) {
        List<PatientRecord> records = patient.getRecords(0, Long.MAX_VALUE);
        Deque<Double> window = new ArrayDeque<>();

        for (PatientRecord record : records) {
            if ("ECG".equalsIgnoreCase(record.getRecordType())) {
                double value = record.getMeasurementValue();

                if (window.size() == WINDOW_SIZE) {
                    double sum = 0;
                    for (double v : window) {
                        sum += v;
                    }
                    double avg = sum / WINDOW_SIZE;

                    if (value > avg + THRESHOLD) {
                        dispatcher.dispatch(new Alert(
                                String.valueOf(patient.getId()),
                                "ECG abnormal peak detected: " + value + " (avg=" + avg + ")",
                                record.getTimestamp()));
                    }

                    window.pollFirst();
                }

                window.addLast(value);
            }
        }
    }
}
