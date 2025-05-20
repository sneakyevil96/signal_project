package com.cardio_generator.generators;
import com.alerts.AlertDispatcher;
import com.alerts.strategy.AlertStrategy;
import com.data_management.Patient;
import com.data_management.DataStorage;
import java.util.List;
/**
 * Evaluates patient data using alert strategies and dispatches alerts when needed.
 */
public class AlertGenerator {
    private final DataStorage dataStorage;
    private final List<AlertStrategy> strategies;
    private final AlertDispatcher dispatcher;

    // Constructor.
    public AlertGenerator(DataStorage storage, List<AlertStrategy> strategies, AlertDispatcher dispatcher) {
        this.dataStorage = storage;
        this.strategies = strategies;
        this.dispatcher = dispatcher;
    }

    /**
     * Executes the evaluateAllPatients operation.
     */
    public void evaluateAllPatients() {
        for (Patient p : dataStorage.getAllPatients()) {
            for (AlertStrategy strat : strategies) {
                strat.checkAlert(p, dispatcher);
            }
        }
    }
}