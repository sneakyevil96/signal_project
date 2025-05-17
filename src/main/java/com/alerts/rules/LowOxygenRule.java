// com/alerts/rules/LowOxygenRule.java

package com.alerts.rules;

import com.alerts.Alert;
import com.alerts.AlertDispatcher;
import com.alerts.AlertRule;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;

public class LowOxygenRule implements AlertRule {
    private static final double OXYGEN_THRESHOLD = 92.0;

    @Override
    public void evaluate(Patient patient, AlertDispatcher dispatcher) {
        List<PatientRecord> records = patient.getRecords(0, Long.MAX_VALUE);

        for (int i = 0; i < records.size(); i++) {
            PatientRecord record = records.get(i);
            if ("OxygenSaturation".equalsIgnoreCase(record.getRecordType())) {
                double value = record.getMeasurementValue();
                if (value < OXYGEN_THRESHOLD) {
                    dispatcher.dispatch(new Alert(
                            String.valueOf(patient.getId()),
                            "Low oxygen saturation detected: " + value + "%",
                            record.getTimestamp()
                    ));
                }
            }
        }
    }
}
