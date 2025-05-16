// com/alerts/rules/HypotensiveHypoxemiaRule.java

package com.alerts.rules;

import com.alerts.Alert;
import com.alerts.AlertDispatcher;
import com.alerts.AlertRule;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

public class HypotensiveHypoxemiaRule implements AlertRule {
    private static final double SYSTOLIC_THRESHOLD = 90.0;
    private static final double OXYGEN_THRESHOLD = 92.0;
    private static final long TIME_TOLERANCE_MS = 60 * 1000; // allow up to 1 minute timestamp difference

    @Override
    public void evaluate(Patient patient, AlertDispatcher dispatcher) {
        List<PatientRecord> records = patient.getRecords(0, Long.MAX_VALUE);

        List<PatientRecord> systolicRecords = new ArrayList<>();
        List<PatientRecord> oxygenRecords = new ArrayList<>();

        for (int i = 0; i < records.size(); i++) {
            PatientRecord record = records.get(i);
            if ("Systolic".equalsIgnoreCase(record.getRecordType())) {
                systolicRecords.add(record);
            } else if ("OxygenSaturation".equalsIgnoreCase(record.getRecordType())) {
                oxygenRecords.add(record);
            }
        }

        for (int i = 0; i < systolicRecords.size(); i++) {
            PatientRecord bp = systolicRecords.get(i);
            if (bp.getMeasurementValue() < SYSTOLIC_THRESHOLD) {
                long bpTime = bp.getTimestamp();
                for (int j = 0; j < oxygenRecords.size(); j++) {
                    PatientRecord ox = oxygenRecords.get(j);
                    if (ox.getMeasurementValue() < OXYGEN_THRESHOLD &&
                            Math.abs(ox.getTimestamp() - bpTime) <= TIME_TOLERANCE_MS) {

                        dispatcher.dispatch(new Alert(
                                String.valueOf(patient.getId()),
                                "Hypotensive Hypoxemia Alert: SBP=" + bp.getMeasurementValue() +
                                        ", SpO2=" + ox.getMeasurementValue(),
                                Math.max(bpTime, ox.getTimestamp())
                        ));
                        break; // avoid duplicate alerts for the same BP reading
                    }
                }
            }
        }
    }
}
