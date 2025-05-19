package com.alerts.factory;
import com.alerts.Alert;
//produces Alerts for ECG (heart rhythm) abnormalities
public class ECGAlertFactory extends AlertFactory {
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new Alert(
                patientId,
                "[ECG] " + condition,
                timestamp
        );
    }
}