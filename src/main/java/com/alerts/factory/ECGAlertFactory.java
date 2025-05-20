package com.alerts.factory;
import com.alerts.Alert;
/**
 * Factory for creating ECG-related alerts.
 */
public class ECGAlertFactory extends AlertFactory {
    @Override
/**
 * Executes the createAlert operation.
 * @param Alert Alert.
 * @param patientId PatientId.
 * @param condition Condition.
 * @param timestamp Timestamp.
 */
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new Alert(
                patientId,
                "[ECG] " + condition,
                timestamp
        );
    }
}