package com.alerts.strategy;
import com.alerts.AlertDispatcher;
import com.alerts.factory.AlertFactory;
import com.alerts.factory.BloodPressureAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.List;
/**
 * Strategy to detect abnormal blood pressure levels.
 */
public class BloodPressureStrategy implements AlertStrategy {
    private final AlertFactory factory = new BloodPressureAlertFactory();

    /**
     * Executes the checkAlert operation.
     * @param patient Patient.
     * @param dispatcher Dispatcher.
     */
    @Override
    public void checkAlert(Patient patient, AlertDispatcher dispatcher) {
        List<PatientRecord> recs = patient.getRecords(0, Long.MAX_VALUE);
        for (PatientRecord r : recs) {
            String type = r.getRecordType();
            double v = r.getMeasurementValue();
            long ts = r.getTimestamp();
            if ( "Systolic".equalsIgnoreCase(type) && (v > 180 || v < 90) ) {
                dispatcher.dispatch(
                        factory.createAlert(String.valueOf(patient.getId()),
                                "Critical systolic BP: " + v, ts));
            }
            if ( "Diastolic".equalsIgnoreCase(type) && (v > 120 || v < 60) ) {
                dispatcher.dispatch(
                        factory.createAlert(String.valueOf(patient.getId()),
                                "Critical diastolic BP: " + v, ts));
            }
        }
    }
}