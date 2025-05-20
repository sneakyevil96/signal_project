package com.alerts.rules;
import com.alerts.Alert;
import com.alerts.AlertDispatcher;
import com.alerts.AlertRule;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.List;
/**
 * Base class for alert strategies triggered by specific events.
 */
public class TriggeredAlertRule implements AlertRule {
    private static final String TRIGGER_TYPE = "TriggeredAlert";

    @Override
/**
 * Executes the evaluate operation.
 * @param patient Patient.
 * @param dispatcher Dispatcher.
 */
    public void evaluate(Patient patient, AlertDispatcher dispatcher) {
        List<PatientRecord> records = patient.getRecords(0, Long.MAX_VALUE);

        for (int i = 0; i < records.size(); i++) {
            PatientRecord record = records.get(i);
            if (TRIGGER_TYPE.equalsIgnoreCase(record.getRecordType())) {
                dispatcher.dispatch(new Alert(
                        String.valueOf(patient.getId()),
                        "Manual alert triggered by nurse/patient.",
                        record.getTimestamp()
                ));
            }
        }
    }
}
