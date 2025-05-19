package com.alerts.strategy;
import com.alerts.AlertDispatcher;
import com.alerts.factory.AlertFactory;
import com.alerts.factory.ECGAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.List;

public class HeartRateStrategy implements AlertStrategy {
    private final AlertFactory factory = new ECGAlertFactory();

    @Override
    public void checkAlert(Patient patient, AlertDispatcher dispatcher) {
        List<PatientRecord> recs = patient.getRecords(0, Long.MAX_VALUE);
        for (PatientRecord r : recs) {
            if ("ECG".equalsIgnoreCase(r.getRecordType())) {
                double v = r.getMeasurementValue();
                long ts = r.getTimestamp();
                // e.g. abnormal if instantaneous ECG voltage spikes beyond ±0.8
                if (Math.abs(v) > 0.8) {
                    dispatcher.dispatch(
                            factory.createAlert(String.valueOf(patient.getId()),
                                    "Abnormal ECG peak: " + v,
                                    ts));
                }
            }
        }
    }
}
