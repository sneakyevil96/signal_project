package com.alerts.factory;

import com.alerts.Alert;

/**
 * Abstract factory for creating Alert instances.
 */
public abstract class AlertFactory {
    /**
     * Create an Alert of the appropriate subtype.
     *
     * @param patientId the patient’s ID
     * @param condition a short description of the condition
     * @param timestamp when it occurred
     * @return a new Alert
     */
    public abstract Alert createAlert(String patientId, String condition, long timestamp);
}