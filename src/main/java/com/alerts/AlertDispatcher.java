package com.alerts;

/**
 * Interface for alert delivery systems. Concrete implementations may
 * log alerts to console, send notifications, or store them in a file.
 */
public interface AlertDispatcher {
    /**
     * Dispatches the alert via the appropriate channel.
     *
     * @param alert the alert object containing details to be dispatched
     */
    void dispatch(Alert alert);
}