package com.alerts.strategy;
import com.alerts.AlertDispatcher;
import com.alerts.factory.AlertFactory;
import com.alerts.factory.BloodOxygenAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.List;
/**
 * Strategy to detect abnormal oxygen saturation levels.
 */
public class OxygenSaturationStrategy implements AlertStrategy {
    private static final double THRESHOLD = 92.0;
    private final AlertFactory factory = new BloodOxygenAlertFactory();

    /**
     * Executes the checkAlert operation.
     * @param patient Patient.
     * @param dispatcher Dispatcher.
     */
    @Override
    public void checkAlert(Patient patient, AlertDispatcher dispatcher) {
        List<PatientRecord> recs = patient.getRecords(0, Long.MAX_VALUE);
        for (PatientRecord r : recs) {
            if ("OxygenSaturation".equalsIgnoreCase(r.getRecordType())) {
                double v = r.getMeasurementValue();
                long ts = r.getTimestamp();
                if (v < THRESHOLD) {
                    dispatcher.dispatch(factory.createAlert(String.valueOf(patient.getId()),"Low oxygen saturation: " + v + "%", ts));
                }
            }
        }
    }
}