package com.alerts.factory;
import com.alerts.Alert;
//Produces Alerts for blood-oxygen saturation issues.
public class BloodOxygenAlertFactory extends AlertFactory {
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new Alert(
                patientId,
                "[O2] " + condition,
                timestamp
        );
    }
}