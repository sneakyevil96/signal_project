package com.alerts.factory;
import com.alerts.Alert;
//Produces alerts for blood pressure anomalies.
/**
 * Factory for creating blood pressure-related alerts.
 */
public class BloodPressureAlertFactory extends AlertFactory {
    @Override
/**
 * Executes the createAlert operation.
 * @param Alert Alert.
 * @param patientId Patientid.
 * @param condition Condition.
 * @param timestamp Timestamp.
 */
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new Alert(
                patientId,
                "[BP] " + condition,
                timestamp
        );
    }
}