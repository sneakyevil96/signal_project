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
     * Executes the checkAlert operation.
     * @param patient Patient.
     * @param dispatcher Dispatcher.
     */
    @Override
    public void checkAlert(Patient patient, AlertDispatcher dispatcher) {
        wrapped.checkAlert(patient, dispatcher);
    }
}