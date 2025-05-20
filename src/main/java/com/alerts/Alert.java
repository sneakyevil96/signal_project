package com.alerts;
/**
 * Represents an alert triggered by specific patient conditions.
 */
public class Alert {
    private String patientId;
    private String condition;
    private long timestamp;

    public Alert(String patientId, String condition, long timestamp) {
        this.patientId = patientId;
        this.condition = condition;
        this.timestamp = timestamp;
    }

    /**
     * Executes the getPatientId operation
     */
    public String getPatientId() {
        return patientId;
    }

    /**
     * Executes the getCondition operation.
     */
    public String getCondition() {
        return condition;
    }

    /**
     * Executes the getTimestamp operation.
     * @return a long timestamp.
     */
    public long getTimestamp() {
        return timestamp;
    }
}