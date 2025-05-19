package com.alerts.rules;
import com.alerts.Alert;
import com.alerts.AlertDispatcher;
import com.alerts.AlertRule;
import com.alerts.factory.AlertFactory;
import com.alerts.factory.BloodPressureAlertFactory;
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

        for (PatientRecord r : records) {
            String type = r.getRecordType();
            if ("Systolic".equalsIgnoreCase(type)) {
                systolic.add(r);
            } else if ("Diastolic".equalsIgnoreCase(type)) {
                diastolic.add(r);
            }
        }
        AlertFactory bpFactory = new BloodPressureAlertFactory();
        for (PatientRecord r : systolic) {
            double value = r.getMeasurementValue();
            long ts = r.getTimestamp();
            if (value > 180 || value < 90) {
                dispatcher.dispatch(
                        bpFactory.createAlert(
                                String.valueOf(patient.getId()),
                                "Critical systolic BP: " + value, ts));
            }
        }
        for (PatientRecord r : diastolic) {
            double value = r.getMeasurementValue();
            long ts = r.getTimestamp();
            if (value > 120 || value < 60) {
                dispatcher.dispatch(
                        bpFactory.createAlert(
                                String.valueOf(patient.getId()),
                                "Critical diastolic BP: " + value, ts));
            }
        }
    }
}
