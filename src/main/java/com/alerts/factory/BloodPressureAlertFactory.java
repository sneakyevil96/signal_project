package com.alerts.factory;
import com.alerts.Alert;

//Produces alerts for blood pressure anomalies.
public class BloodPressureAlertFactory extends AlertFactory {
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        // You can also subclass Alert to BPAlert if you like
        return new Alert(
                patientId,
                "[BP] " + condition,
                timestamp
        );
    }
}