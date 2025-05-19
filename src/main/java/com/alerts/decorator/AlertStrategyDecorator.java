package com.alerts.decorator;
import com.alerts.AlertDispatcher;
import com.alerts.strategy.AlertStrategy;
import com.data_management.Patient;

public abstract class AlertStrategyDecorator implements AlertStrategy {
    protected final AlertStrategy wrapped;

    protected AlertStrategyDecorator(AlertStrategy wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void checkAlert(Patient patient, AlertDispatcher dispatcher) {
        wrapped.checkAlert(patient, dispatcher);
    }
}
