package com.alerts.factory;
import com.alerts.Alert;
//Produces Alerts for blood-oxygen saturation issues.
/**
 * Factory for creating blood oxygen-related alerts.
 */
public class BloodOxygenAlertFactory extends AlertFactory {
    @Override
/**
 * Executes the createAlert operation.
 * @param Alert Alert.
 * @param patientId Patient.
 * @param condition Condition.
 * @param timestamp Timestamp.
 */
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new Alert(patientId,"[O2] " + condition, timestamp);
    }
}
