package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import java.util.List;

public class AlertGenerator {
    private final DataStorage dataStorage;
    private final List<AlertRule> alertRules;
    private final AlertDispatcher alertDispatcher;

    public AlertGenerator(DataStorage dataStorage, List<AlertRule> alertRules, AlertDispatcher dispatcher) {
        this.dataStorage = dataStorage;
        this.alertRules = alertRules;
        this.alertDispatcher = dispatcher;
    }

    public void evaluateAllPatients() {
        for (Patient patient : dataStorage.getAllPatients()) {
            for (AlertRule rule : alertRules) {
                rule.evaluate(patient, alertDispatcher);
            }
        }
    }
}
