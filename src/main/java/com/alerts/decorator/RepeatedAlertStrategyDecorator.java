package com.alerts.decorator;
import com.alerts.AlertDispatcher;
import com.alerts.strategy.AlertStrategy;
import com.data_management.Patient;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
//Runs the wrapped strategy once immediately, then once more after the configured delay.
/**
 * Prevents duplicate alerts within a time window.
 */
public class RepeatedAlertStrategyDecorator extends AlertStrategyDecorator {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final long delay;
    private final TimeUnit unit;

    public RepeatedAlertStrategyDecorator(AlertStrategy wrapped, long delay, TimeUnit unit) {
        super(wrapped);
        this.delay = delay;
        this.unit = unit;
    }

    /**
     * Executes the checkAlert operation.
     * @param patient Patient.
     * @param dispatcher Dispatcher.
     */
    @Override
    public void checkAlert(Patient patient, AlertDispatcher dispatcher) {
        // First run immediately
        super.checkAlert(patient, dispatcher);
        // Schedule a second check after delay
        scheduler.schedule(() -> wrapped.checkAlert(patient, dispatcher), delay, unit);
    }
}