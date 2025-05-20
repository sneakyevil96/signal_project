package com.alerts.decorator;
import com.alerts.AlertDispatcher;
import com.alerts.strategy.AlertStrategy;
import com.data_management.Patient;

public abstract class AlertStrategyDecorator implements AlertStrategy {
    protected final AlertStrategy wrapped;

    // Constructor.
    protected AlertStrategyDecorator(AlertStrategy wrapped) {
        this.wrapped = wrapped;
    }

    /**
     * executes checkAlert operation.
     * @param patient patient.
     * @param dispatcher dispatcher.
     */
    @Override
    public void checkAlert(Patient patient, AlertDispatcher dispatcher) {
        wrapped.checkAlert(patient, dispatcher);
    }
}