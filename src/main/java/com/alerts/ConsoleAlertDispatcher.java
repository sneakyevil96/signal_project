package com.alerts;

/**
 * A simple implementation of AlertDispatcher that logs alerts to the console.
 */
public class ConsoleAlertDispatcher implements AlertDispatcher {
    @Override
    public void dispatch(Alert alert) {
        System.out.println("(!) ALERT: Patient ID " + alert.getPatientId()
                + " | Condition: " + alert.getCondition()
                + " | Timestamp: " + alert.getTimestamp());
    }
}
