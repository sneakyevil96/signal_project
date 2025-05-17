// com/alerts/rules/CriticalBloodPressureRule.java

package com.alerts.rules;

import com.alerts.Alert;
import com.alerts.AlertDispatcher;
import com.alerts.AlertRule;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

public class CriticalBloodPressureRule implements AlertRule {

    @Override
    public void evaluate(Patient patient, AlertDispatcher dispatcher) {
        List<PatientRecord> records = patient.getRecords(0, Long.MAX_VALUE);

        List<PatientRecord> systolic = new ArrayList<>();
        List<PatientRecord> diastolic = new ArrayList<>();

        for (int i = 0; i < records.size(); i++) {
            PatientRecord r = records.get(i);
            String type = r.getRecordType();
            if ("Systolic".equalsIgnoreCase(type)) {
                systolic.add(r);
            } else if ("Diastolic".equalsIgnoreCase(type)) {
                diastolic.add(r);
            }
        }

        for (int i = 0; i < systolic.size(); i++) {
            double value = systolic.get(i).getMeasurementValue();
            if (value > 180 || value < 90) {
                dispatcher.dispatch(new Alert(
                        String.valueOf(patient.getId()),
                        "(!) Critical systolic BP: " + value,
                        systolic.get(i).getTimestamp()
                ));
            }
        }

        for (int i = 0; i < diastolic.size(); i++) {
            double value = diastolic.get(i).getMeasurementValue();
            if (value > 120 || value < 60) {
                dispatcher.dispatch(new Alert(
                        String.valueOf(patient.getId()),
                        "(!) Critical diastolic BP: " + value,
                        diastolic.get(i).getTimestamp()
                ));
            }
        }
    }
}
