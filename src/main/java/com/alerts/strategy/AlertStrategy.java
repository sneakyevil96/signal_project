package com.alerts.strategy;
import com.alerts.AlertDispatcher;
import com.data_management.Patient;
/**
 * Interface for alert condition strategies applied to patient data.
 */
public interface AlertStrategy {
    //Inspect the patient’s data and use the dispatcher to send any alerts
    void checkAlert(Patient patient, AlertDispatcher dispatcher);
}