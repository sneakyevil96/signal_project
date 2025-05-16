// com/alerts/rules/RapidOxygenDropRule.java

package com.alerts.rules;

import com.alerts.Alert;
import com.alerts.AlertDispatcher;
import com.alerts.AlertRule;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

public class RapidOxygenDropRule implements AlertRule {
    private static final double DROP_THRESHOLD = 5.0;
    private static final long TIME_WINDOW_MS = 10 * 60 * 1000; // 10 minutes

    @Override
    public void evaluate(Patient patient, AlertDispatcher dispatcher) {
        List<PatientRecord> allRecords = patient.getRecords(0, Long.MAX_VALUE);

        List<PatientRecord> oxygenRecords = new ArrayList<>();
        for (int i = 0; i < allRecords.size(); i++) {
            if ("OxygenSaturation".equalsIgnoreCase(allRecords.get(i).getRecordType())) {
                oxygenRecords.add(allRecords.get(i));
            }
        }

        for (int i = 0; i < oxygenRecords.size(); i++) {
            PatientRecord baseRecord = oxygenRecords.get(i);
            double baseValue = baseRecord.getMeasurementValue();
            long baseTime = baseRecord.getTimestamp();

            for (int j = i + 1; j < oxygenRecords.size(); j++) {
                PatientRecord compareRecord = oxygenRecords.get(j);
                long timeDiff = compareRecord.getTimestamp() - baseTime;

                if (timeDiff > TIME_WINDOW_MS) {
                    break;
                }

                double drop = baseValue - compareRecord.getMeasurementValue();
                if (drop >= DROP_THRESHOLD) {
                    dispatcher.dispatch(new Alert(
                            String.valueOf(patient.getId()),
                            "(!) Rapid oxygen drop detected: " + drop + "% in under 10 minutes",
                            compareRecord.getTimestamp()
                    ));
                    break;
                }
            }
        }
    }
}
