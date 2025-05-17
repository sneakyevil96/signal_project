// com/alerts/AlertRule.java
package com.alerts;

import com.data_management.Patient;

/**
 * Represents a reusable and modular rule that can evaluate a patient's data
 * and trigger alerts through the given dispatcher. Implementations should
 * contain only one responsibility each and adhere to SOLID principles.
 */
public interface AlertRule {
    /**
     * Evaluate the patient’s data and dispatch alerts if conditions are met.
     *
     * @param patient the patient whose data should be evaluated
     * @param dispatcher the dispatcher responsible for sending out alerts
     */
    void evaluate(Patient patient, AlertDispatcher dispatcher);
}